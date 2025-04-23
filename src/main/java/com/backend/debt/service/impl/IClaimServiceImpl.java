package com.backend.debt.service.impl;

import com.backend.debt.exceptions.CustomException;
import com.backend.debt.exceptions.HttpResponseStatus;
import com.backend.debt.mapper.ClaimConfirmMapper;
import com.backend.debt.mapper.ClaimFillingMapper;
import com.backend.debt.mapper.ClaimMapper;
import com.backend.debt.mapper.CreditorMapper;
import com.backend.debt.mapper.query.LambdaQueryWrapperX;
import com.backend.debt.model.dto.AgentDto;
import com.backend.debt.model.dto.ClaimConfirmDto;
import com.backend.debt.model.dto.ClaimDetailDto;
import com.backend.debt.model.dto.ClaimFillingDto;
import com.backend.debt.model.dto.ClaimSimpleDto;
import com.backend.debt.model.dto.ContactInfoDto;
import com.backend.debt.model.dto.CreditorDto;
import com.backend.debt.model.dto.DeclaredSummaryDto;
import com.backend.debt.model.entity.ClaimConfirmEntity;
import com.backend.debt.model.entity.ClaimEntity;
import com.backend.debt.model.entity.ClaimFillingEntity;
import com.backend.debt.model.entity.CreditorEntity;
import com.backend.debt.model.page.PageResult;
import com.backend.debt.model.query.ClaimQuery;
import com.backend.debt.model.query.ClaimSimplePageQuery;
import com.backend.debt.service.IClaimService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/** 债权申报服务实现类 */
@Slf4j
@Service
public class IClaimServiceImpl implements IClaimService {

  @Resource
  private ClaimMapper claimMapper;
  @Resource private CreditorMapper creditorMapper;
  @Resource private ClaimFillingMapper claimFillingMapper;
  @Resource private ClaimConfirmMapper claimConfirmMapper;

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
                .eqIfPresent(ClaimEntity::getClaimNumber, query.getClaimNumber())
                .likeIfPresent(ClaimEntity::getRegistrar, query.getRegistrar())
                .geIfPresent(ClaimEntity::getClaimDate, query.getStartClaimDate())
                .leIfPresent(ClaimEntity::getClaimDate, query.getEndClaimDate()));

    // 如果没有查询到数据，直接返回空结果
    if (claimEntityPage.getList().isEmpty()) {
      return PageResult.empty();
    }

    // 2. 获取所有债权ID
    List<String> claimIds = claimEntityPage.getList().stream().map(ClaimEntity::getId).toList();

    // 3. 查询这些债权ID关联的所有债权人
    List<CreditorEntity> creditorEntities =
        creditorMapper.selectList(
            new LambdaQueryWrapperX<CreditorEntity>().in(CreditorEntity::getClaimId, claimIds));

    // 4. 将债权人按债权ID分组，形成Map<债权ID, List<债权人>>
    Map<String, List<CreditorEntity>> creditorClaimIdMap =
        creditorEntities.stream().collect(Collectors.groupingBy(CreditorEntity::getClaimId));

    // 5. 将ClaimEntity转换为ClaimSimpleDto，并关联对应的债权人
    List<ClaimSimpleDto> resultList =
        claimEntityPage.getList().stream()
            .map(
                claimEntity -> {
                  ClaimSimpleDto dto = new ClaimSimpleDto();
                  // 复制基本属性
                  BeanUtils.copyProperties(claimEntity, dto);

                  // 设置日期属性，ClaimSimpleDto使用claimDate字段
                  dto.setClaimDate(claimEntity.getClaimDate());

                  // 设置债权人列表
                  List<CreditorEntity> creditors =
                      creditorClaimIdMap.getOrDefault(claimEntity.getId(), Collections.emptyList());
                  dto.setCreditors(CreditorDto.ofList(creditors));

                  return dto;
                })
            .collect(Collectors.toList());

    // 6. 返回最终分页结果
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

    // 1. 构建并保存债权申报实体
    ClaimEntity claimEntity = new ClaimEntity();
    BeanUtils.copyProperties(addDto, claimEntity);

    // 处理枚举类型数据转换
    if (addDto.getClaimTypes() != null && !addDto.getClaimTypes().isEmpty()) {
      claimEntity.setClaimTypes(
          addDto.getClaimTypes().stream().map(Enum::name).collect(Collectors.toList()));
    }

    // 保存代理人信息
    if (addDto.getAgent() != null) {
      claimEntity.setAgentName(addDto.getAgent().getName());
      claimEntity.setAgentPosition(addDto.getAgent().getPosition());
      claimEntity.setAgentPhone(addDto.getAgent().getPhone());
      claimEntity.setHasVotingRight(addDto.getAgent().getHasVotingRight());
    }

    // 保存收件人信息
    if (addDto.getCreditorContactInfo() != null) {
      claimEntity.setRecipient(addDto.getCreditorContactInfo().getRecipient());
      claimEntity.setContactPhone(addDto.getCreditorContactInfo().getContactPhone());
      claimEntity.setMailingAddress(addDto.getCreditorContactInfo().getMailingAddress());
      claimEntity.setEmail(addDto.getCreditorContactInfo().getEmail());
    }

    // 保存债权实体
    claimMapper.insert(claimEntity);

    // 2. 保存债权人信息
    if (addDto.getCreditors() != null && !addDto.getCreditors().isEmpty()) {
      for (CreditorDto creditorDto : addDto.getCreditors()) {
        CreditorEntity creditorEntity = new CreditorEntity();
        BeanUtils.copyProperties(creditorDto, creditorEntity);
        creditorEntity.setClaimId(claimEntity.getId());
        creditorMapper.insert(creditorEntity);
      }
    }

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

      // 5. 查询并批量删除债权人记录
      List<CreditorEntity> creditorEntities =
          creditorMapper.selectList(
              new LambdaQueryWrapperX<CreditorEntity>().eq(CreditorEntity::getClaimId, claimId));

      if (!CollectionUtils.isEmpty(creditorEntities)) {
        List<String> creditorIds =
            creditorEntities.stream().map(CreditorEntity::getId).collect(Collectors.toList());

        creditorMapper.deleteBatchIds(creditorIds);
        log.info("批量删除债权人记录，数量：{}，关联债权ID：{}", creditorIds.size(), claimId);
      }

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

      // 3. 如果提供了债权人信息，则先删除旧的债权人记录，再添加新的
      if (updateDto.getCreditors() != null) {
        // 查询并批量删除债权人记录
        List<CreditorEntity> oldCreditors =
            creditorMapper.selectList(
                new LambdaQueryWrapperX<CreditorEntity>().eq(CreditorEntity::getClaimId, claimId));

        if (!CollectionUtils.isEmpty(oldCreditors)) {
          List<String> creditorIds =
              oldCreditors.stream().map(CreditorEntity::getId).collect(Collectors.toList());

          creditorMapper.deleteBatchIds(creditorIds);
          log.info("更新时批量删除旧的债权人记录，数量：{}，关联债权ID：{}", creditorIds.size(), claimId);
        }

        // 添加新的债权人记录
        if (!updateDto.getCreditors().isEmpty()) {
          for (CreditorDto creditorDto : updateDto.getCreditors()) {
            CreditorEntity creditorEntity = new CreditorEntity();
            BeanUtils.copyProperties(creditorDto, creditorEntity);
            creditorEntity.setClaimId(claimId);
            creditorMapper.insert(creditorEntity);
          }
          log.info("更新时添加新的债权人记录，数量：{}，关联债权ID：{}", updateDto.getCreditors().size(), claimId);
        }
      }

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
      // 2. 创建返回DTO
      ClaimDetailDto detailDto = new ClaimDetailDto();

      // 3. 复制基本属性
      BeanUtils.copyProperties(claimEntity, detailDto);

      // 5. 设置代理人信息
      if (StringUtils.hasText(claimEntity.getAgentName())) {
        AgentDto agentDto = new AgentDto();
        agentDto.setName(claimEntity.getAgentName());
        agentDto.setPosition(claimEntity.getAgentPosition());
        agentDto.setPhone(claimEntity.getAgentPhone());
        agentDto.setHasVotingRight(claimEntity.getHasVotingRight());
        detailDto.setAgent(agentDto);
      }

      // 6. 设置收件人信息
      if (StringUtils.hasText(claimEntity.getRecipient())) {
        ContactInfoDto contactInfoDto = new ContactInfoDto();
        contactInfoDto.setRecipient(claimEntity.getRecipient());
        contactInfoDto.setContactPhone(claimEntity.getContactPhone());
        contactInfoDto.setMailingAddress(claimEntity.getMailingAddress());
        contactInfoDto.setEmail(claimEntity.getEmail());
        detailDto.setCreditorContactInfo(contactInfoDto);
      }

      // 7. 查询并设置债权人信息
      List<CreditorEntity> creditorEntities =
          creditorMapper.selectList(
              new LambdaQueryWrapperX<CreditorEntity>().eq(CreditorEntity::getClaimId, claimId));

      if (!CollectionUtils.isEmpty(creditorEntities)) {
        detailDto.setCreditors(CreditorDto.ofList(creditorEntities));
      }

      // 8. A. 查询债权详情记录
      List<ClaimFillingEntity> fillingEntities =
          claimFillingMapper.selectList(
              new LambdaQueryWrapperX<ClaimFillingEntity>()
                  .eq(ClaimFillingEntity::getClaimId, claimId));

      // 8. B. 转换为DTO并设置到返回对象
      if (!CollectionUtils.isEmpty(fillingEntities)) {
        List<ClaimFillingDto> fillingDtos = new ArrayList<>();
        List<String> fillingIds = new ArrayList<>();

        // 收集填报IDs，用于后续查询确认信息
        for (ClaimFillingEntity entity : fillingEntities) {
          ClaimFillingDto dto = new ClaimFillingDto();
          BeanUtils.copyProperties(entity, dto);
          fillingDtos.add(dto);
          fillingIds.add(entity.getId());
        }

        detailDto.setClaimFillings(fillingDtos);

        // 9. 查询债权确认信息
        if (!CollectionUtils.isEmpty(fillingIds)) {
          List<ClaimConfirmEntity> confirmEntities =
              claimConfirmMapper.selectList(
                  new LambdaQueryWrapperX<ClaimConfirmEntity>()
                      .in(ClaimConfirmEntity::getClaimFillingId, fillingIds));

          if (!CollectionUtils.isEmpty(confirmEntities)) {
            List<ClaimConfirmDto> confirmDtos = new ArrayList<>();

            for (ClaimConfirmEntity entity : confirmEntities) {
              ClaimConfirmDto dto = new ClaimConfirmDto();
              BeanUtils.copyProperties(entity, dto);
              confirmDtos.add(dto);
            }

            detailDto.setClaimConfirms(confirmDtos);
          }
        }

        // 10. 计算申报金额汇总
        calculateDeclaredSummary(detailDto, fillingEntities);
      }

      log.info("获取债权申报详情成功，ID：{}", claimId);
      return detailDto;
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
    if (!StringUtils.hasText(claimId)) {
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
   * @param detailDto 债权申报详情DTO
   * @param fillingEntities 债权填报实体列表
   */
  private void calculateDeclaredSummary(
      ClaimDetailDto detailDto, List<ClaimFillingEntity> fillingEntities) {
    if (CollectionUtils.isEmpty(fillingEntities)) {
      return;
    }

    double totalPrincipal = 0;
    double totalInterest = 0;
    double totalOther = 0;

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
    }

    DeclaredSummaryDto summaryDto = new DeclaredSummaryDto();
    summaryDto.setTotalPrincipal(totalPrincipal);
    summaryDto.setTotalInterest(totalInterest);
    summaryDto.setTotalOther(totalOther);
    summaryDto.setTotal(totalPrincipal + totalInterest + totalOther);

    detailDto.setDeclaredSummary(summaryDto);
  }
}
