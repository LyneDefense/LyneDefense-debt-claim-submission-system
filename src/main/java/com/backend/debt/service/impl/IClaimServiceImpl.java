package com.backend.debt.service.impl;

import cn.hutool.core.util.StrUtil;
import com.backend.debt.exceptions.CustomException;
import com.backend.debt.exceptions.HttpResponseStatus;
import com.backend.debt.mapper.ClaimConfirmMapper;
import com.backend.debt.mapper.ClaimFillingMapper;
import com.backend.debt.mapper.ClaimMapper;
import com.backend.debt.mapper.query.LambdaQueryWrapperX;
import com.backend.debt.model.dto.ClaimDetailDto;
import com.backend.debt.model.dto.ClaimFillingDto;
import com.backend.debt.model.dto.ClaimSimpleDto;
import com.backend.debt.model.dto.ClaimSummaryDto;
import com.backend.debt.model.dto.CreditorDto;
import com.backend.debt.model.entity.ClaimConfirmEntity;
import com.backend.debt.model.entity.ClaimEntity;
import com.backend.debt.model.entity.ClaimFillingEntity;
import com.backend.debt.model.page.PageResult;
import com.backend.debt.model.query.ClaimQuery;
import com.backend.debt.model.query.ClaimSimplePageQuery;
import com.backend.debt.service.IClaimCreditorService;
import com.backend.debt.service.IClaimService;
import com.backend.debt.util.StringUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

/** 债权申报服务实现类 */
@Slf4j
@Service
public class IClaimServiceImpl implements IClaimService {

  @Resource private ClaimMapper claimMapper;
  @Resource private ClaimFillingMapper claimFillingMapper;
  @Resource private ClaimConfirmMapper claimConfirmMapper;
  @Resource private IClaimCreditorService claimCreditorService;

  /**
   * 分页查询债权申报简要信息
   *
   * @param query 分页查询参数
   * @return 分页结果
   */
  @Override
  public PageResult<ClaimSimpleDto> getSimplePage(ClaimSimplePageQuery query) {
    // 1. 查询符合条件的债权申报数据
    PageResult<ClaimEntity> claimEntityPage =
        claimMapper.selectPage(
            query,
            new LambdaQueryWrapperX<ClaimEntity>()
                .likeIfPresent(ClaimEntity::getClaimNumber, query.getClaimNumber())
                .likeIfPresent(ClaimEntity::getRegistrar, query.getRegistrar())
                .geIfPresent(ClaimEntity::getClaimDate, query.getStartClaimDate())
                .leIfPresent(ClaimEntity::getClaimDate, query.getEndClaimDate()));

    // 如果没有查询到数据，直接返回空结果
    if (claimEntityPage.getList().isEmpty()) {
      return PageResult.empty();
    }

    // 2. 获取所有债权ID
    List<String> claimIds = claimEntityPage.getList().stream().map(ClaimEntity::getId).toList();

    // 3. 批量查询所有债权ID对应的债权人列表
    Map<String, List<CreditorDto>> creditorMap =
        claimCreditorService.getCreditorsByClaimIds(claimIds);

    // 4. 将ClaimEntity转换为ClaimSimpleDto，并关联对应的债权人
    List<ClaimSimpleDto> resultList =
        claimEntityPage.getList().stream()
            .map(
                claimEntity -> {
                  ClaimSimpleDto dto = ClaimSimpleDto.of(claimEntity);
                  // 设置债权人列表 - 从map中获取
                  dto.setCreditors(
                      creditorMap.getOrDefault(claimEntity.getId(), new ArrayList<>()));
                  return dto;
                })
            .collect(Collectors.toList());

    // 5. 返回最终分页结果
    return new PageResult<>(resultList, claimEntityPage.getTotal());
  }

  /**
   * 添加债权申报信息
   *
   * @param addDto 债权申报信息
   * @return 新增债权申报的ID
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public String addClaimItem(ClaimQuery addDto) {
    log.info("添加债权申报信息：{}", addDto);
    // 先检查是否存在相同的债权申报记录,claimNumber相同为相同的申报记录
    ClaimEntity existingClaim =
        claimMapper.selectOne(
            new LambdaQueryWrapperX<ClaimEntity>()
                .eq(ClaimEntity::getClaimNumber, addDto.getClaimNumber()));
    if (existingClaim != null) {
      throw new CustomException(
          HttpResponseStatus.BAD_REQUEST.code(),
          "债权申报记录已存在",
          "添加债权申报信息失败，claimNumber已存在：" + addDto.getClaimNumber());
    }
    // 1. 构建并保存债权申报实体
    ClaimEntity claimEntity = addDto.toClaimEntity();
    // 保存债权实体
    claimMapper.insert(claimEntity);

    return claimEntity.getId();
  }

  /**
   * 删除债权申报信息 同时删除相关联的记录（债权人信息、债权申报详情、债权确认等）
   *
   * @param claimId 债权申报ID
   * @return 是否删除成功
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public boolean deleteClaimItem(String claimId) {
    log.info("删除债权申报信息，ID：{}", claimId);
    this.validateAndGet(claimId);

    try {
      // 2. 查询关联的债权详情记录
      List<ClaimFillingEntity> fillingEntities =
          claimFillingMapper.selectList(
              new LambdaQueryWrapperX<ClaimFillingEntity>()
                  .eq(ClaimFillingEntity::getClaimId, claimId));

      // 收集债权详情ID列表
      List<String> fillingIds = new ArrayList<>();
      if (!CollectionUtils.isEmpty(fillingEntities)) {
        fillingIds =
            fillingEntities.stream().map(ClaimFillingEntity::getId).collect(Collectors.toList());
      }

      // 3. 批量删除关联的债权确认记录
      if (!CollectionUtils.isEmpty(fillingIds)) {
        // 查询所有关联的确认记录ID
        List<ClaimConfirmEntity> confirmEntities =
            claimConfirmMapper.selectList(
                new LambdaQueryWrapperX<ClaimConfirmEntity>()
                    .in(ClaimConfirmEntity::getClaimFillingId, fillingIds));

        if (!CollectionUtils.isEmpty(confirmEntities)) {
          List<String> confirmIds =
              confirmEntities.stream().map(ClaimConfirmEntity::getId).collect(Collectors.toList());

          // 批量删除确认记录
          claimConfirmMapper.deleteBatchIds(confirmIds);
          log.info("批量删除债权确认记录，数量：{}，关联详情IDs：{}", confirmIds.size(), fillingIds);
        }

        // 4. 批量删除债权详情记录
        claimFillingMapper.deleteBatchIds(fillingIds);
        log.info("批量删除债权详情记录，数量：{}，关联债权ID：{}", fillingIds.size(), claimId);
      }

      // 5. 删除债权关联的所有债权人记录
      claimCreditorService.deleteCreditorsByClaimId(claimId);

      // 6. 删除债权主记录
      claimMapper.deleteById(claimId);
      log.info("删除债权申报信息成功，ID：{}", claimId);

      return true;
    } catch (Exception e) {
      String errorMsg = "删除债权申报信息异常，ID：" + claimId;
      log.error(errorMsg + "，异常信息：{}", e.getMessage(), e);
      throw new CustomException(
          HttpResponseStatus.INTERNAL_SERVER_ERROR.code(), "删除债权申报记录失败", errorMsg);
    }
  }

  /**
   * 更新债权申报信息
   *
   * @param claimId 债权申报ID
   * @param updateDto 债权申报更新信息
   * @return 是否更新成功
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public boolean updateClaimItem(String claimId, ClaimQuery updateDto) {
    log.info("更新债权申报信息，DTO：{}", updateDto);
    ClaimEntity claimEntity = this.validateAndGet(claimId);
    try {
      // 2. 更新债权主记录
      BeanUtils.copyProperties(updateDto, claimEntity, "id", "createTime");

      // 处理枚举类型数据转换
      if (updateDto.getClaimTypes() != null && !updateDto.getClaimTypes().isEmpty()) {
        claimEntity.setClaimTypes(
            updateDto.getClaimTypes().stream().map(Enum::name).collect(Collectors.toList()));
      }

      // 更新代理人信息
      if (updateDto.getAgent() != null) {
        claimEntity.setAgentName(updateDto.getAgent().getName());
        claimEntity.setAgentPosition(updateDto.getAgent().getPosition());
        claimEntity.setAgentPhone(updateDto.getAgent().getPhone());
        claimEntity.setHasVotingRight(updateDto.getAgent().getHasVotingRight());
      }

      // 更新收件人信息
      if (updateDto.getCreditorContactInfo() != null) {
        claimEntity.setRecipient(updateDto.getCreditorContactInfo().getRecipient());
        claimEntity.setContactPhone(updateDto.getCreditorContactInfo().getContactPhone());
        claimEntity.setMailingAddress(updateDto.getCreditorContactInfo().getMailingAddress());
        claimEntity.setEmail(updateDto.getCreditorContactInfo().getEmail());
      }

      // 更新债权实体
      claimMapper.updateById(claimEntity);

      log.info("更新债权申报信息成功，ID：{}", claimId);
      return true;
    } catch (Exception e) {
      String errorMsg = "更新债权申报信息异常，ID：" + claimId;
      log.error(errorMsg + "，异常信息：{}", e.getMessage(), e);
      throw new CustomException(
          HttpResponseStatus.INTERNAL_SERVER_ERROR.code(), "更新债权申报记录失败", errorMsg);
    }
  }

  /**
   * 获取债权申报详情
   *
   * @param claimId 债权申报ID
   * @return 债权申报详情DTO
   */
  @Override
  public ClaimDetailDto getClaimDetail(String claimId) {
    log.info("获取债权申报详情，ID：{}", claimId);
    ClaimEntity claimEntity = this.validateAndGet(claimId);
    try {
      List<CreditorDto> creditors = claimCreditorService.getCreditorsByClaimId(claimId);

      // 查询债权详情记录
      List<ClaimFillingEntity> fillingEntities =
          claimFillingMapper.selectList(
              new LambdaQueryWrapperX<ClaimFillingEntity>()
                  .eq(ClaimFillingEntity::getClaimId, claimId));
      // 如果债权申报记录为空
      if (fillingEntities.isEmpty()) {
        return ClaimDetailDto.of(claimEntity, creditors, new ArrayList<>(), null);
      }

      // 收集所有 filling ID
      List<String> fillingIds = fillingEntities.stream().map(ClaimFillingEntity::getId).toList();

      // 批量查询所有 claim confirmation 实体
      List<ClaimConfirmEntity> confirmEntities =
          claimConfirmMapper.selectList(
              new LambdaQueryWrapperX<ClaimConfirmEntity>()
                  .in(ClaimConfirmEntity::getClaimFillingId, fillingIds));

      // 创建 fillingId 到 confirmEntity 的映射
      Map<String, ClaimConfirmEntity> confirmMap =
          confirmEntities.stream()
              .collect(
                  Collectors.toMap(
                      ClaimConfirmEntity::getClaimFillingId, confirmEntity -> confirmEntity));

      // 构建 DTOs
      List<ClaimFillingDto> fillingDtos =
          fillingEntities.stream()
              .map(
                  entity -> {
                    ClaimConfirmEntity confirmEntity = confirmMap.get(entity.getId());
                    return ClaimFillingDto.of(entity, confirmEntity);
                  })
              .collect(Collectors.toList());

      // 计算申报金额汇总
      ClaimSummaryDto claimSummary = calculateDeclaredSummary(fillingEntities);
      log.info("获取债权申报详情成功，ID：{}", claimId);
      return ClaimDetailDto.of(claimEntity, creditors, fillingDtos, claimSummary);
    } catch (CustomException e) {
      throw e; // 自定义异常直接抛出
    } catch (Exception e) {
      String errorMsg = "获取债权申报详情异常，ID：" + claimId;
      log.error(errorMsg + "，异常信息：{}", e.getMessage(), e);
      throw new CustomException(
          HttpResponseStatus.INTERNAL_SERVER_ERROR.code(), "获取债权申报详情失败", errorMsg);
    }
  }

  @Override
  public ClaimEntity validateAndGet(String claimId) {
    if (StrUtil.isBlank(claimId)) {
      throw new CustomException(
          HttpResponseStatus.BAD_REQUEST.code(), "债权ID不能为空", "获取债权申报详情失败，ID为空");
    }

    ClaimEntity claimEntity = claimMapper.selectById(claimId);
    if (claimEntity == null) {
      throw new CustomException(
          HttpResponseStatus.NOT_FOUND.code(), "债权申报记录不存在", "获取债权申报详情失败，ID不存在：" + claimId);
    }
    return claimEntity;
  }

  /**
   * 计算申报金额汇总
   *
   * @param fillingEntities 债权填报实体列表
   */
  private ClaimSummaryDto calculateDeclaredSummary(List<ClaimFillingEntity> fillingEntities) {
    if (CollectionUtils.isEmpty(fillingEntities)) {
      return null;
    }

    double totalPrincipal = 0;
    double totalInterest = 0;
    double totalOther = 0;
    String claimNature = "";

    for (ClaimFillingEntity entity : fillingEntities) {
      if (entity.getClaimPrincipal() != null) {
        totalPrincipal += entity.getClaimPrincipal();
      }
      if (entity.getClaimInterest() != null) {
        totalInterest += entity.getClaimInterest();
      }
      if (entity.getClaimOther() != null) {
        totalOther += entity.getClaimOther();
      }
      if (!StrUtil.isBlank(entity.getClaimNature())) {
        claimNature = StringUtils.stringAdd(claimNature, entity.getClaimNature());
      }
    }

    return ClaimSummaryDto.builder()
        .totalPrincipal(totalPrincipal)
        .totalInterest(totalInterest)
        .totalOther(totalOther)
        .claimNature(claimNature)
        .count(fillingEntities.size())
        .build();
  }
}
