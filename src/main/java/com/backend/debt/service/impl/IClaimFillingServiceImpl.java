package com.backend.debt.service.impl;

import com.backend.debt.exceptions.CustomException;
import com.backend.debt.exceptions.HttpResponseStatus;
import com.backend.debt.mapper.ClaimConfirmMapper;
import com.backend.debt.mapper.ClaimFillingMapper;
import com.backend.debt.mapper.query.LambdaQueryWrapperX;
import com.backend.debt.model.dto.ClaimConfirmDto;
import com.backend.debt.model.dto.ClaimDetailDto;
import com.backend.debt.model.dto.ClaimFillingDto;
import com.backend.debt.model.entity.ClaimConfirmEntity;
import com.backend.debt.model.entity.ClaimEntity;
import com.backend.debt.model.entity.ClaimFillingEntity;
import com.backend.debt.model.query.ClaimFillingQuery;
import com.backend.debt.service.IClaimFillingService;
import com.backend.debt.service.IClaimService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Slf4j
@Service
public class IClaimFillingServiceImpl implements IClaimFillingService {

  @Resource private ClaimConfirmMapper claimConfirmMapper;
  @Resource private ClaimFillingMapper claimFillingMapper;
  @Resource private IClaimService claimService;

  /**
   * 更新债权详情信息
   *
   * @param claimFillingId 债权详情ID
   * @param query 债权详情更新信息
   * @return 更新后的债权申报详情
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public ClaimDetailDto updateClaimFilling(String claimFillingId, ClaimFillingQuery query) {
    log.info("更新债权详情信息，ID：{}，数据：{}", claimFillingId, query);
    ClaimFillingEntity fillingEntity = this.validateAndGet(claimFillingId);

    try {
      // 2. 获取关联的债权ID
      String claimId = fillingEntity.getClaimId();
      // 2.1 校验关联的债权申报记录是否存在
      ClaimEntity claimEntity = claimService.validateAndGet(claimId);

      // 3. 更新债权详情记录
      if (query.getClaimNature() != null) {
        fillingEntity.setClaimNature(query.getClaimNature());
      }
      if (query.getCollateralDetails() != null) {
        fillingEntity.setCollateralDetails(query.getCollateralDetails());
      }
      if (query.getClaimPrincipal() != null) {
        fillingEntity.setClaimPrincipal(query.getClaimPrincipal());
      }
      if (query.getClaimInterest() != null) {
        fillingEntity.setClaimInterest(query.getClaimInterest());
      }
      if (query.getClaimOther() != null) {
        fillingEntity.setClaimOther(query.getClaimOther());
      }

      // 4. 更新数据库记录
      claimFillingMapper.updateById(fillingEntity);
      log.info("更新债权详情信息成功，ID：{}", claimFillingId);

      // 5. 返回更新后的完整债权详情
      return claimService.getClaimDetail(claimId);
    } catch (CustomException e) {
      throw e; // 自定义异常直接抛出
    } catch (Exception e) {
      String errorMsg = "更新债权详情信息异常，ID：" + claimFillingId;
      log.error(errorMsg + "，异常信息：{}", e.getMessage(), e);
      throw new CustomException(
          HttpResponseStatus.INTERNAL_SERVER_ERROR.code(), "更新债权详情记录失败", errorMsg);
    }
  }

  /**
   * 添加债权详情信息
   *
   * @param claimId 债权申报ID
   * @param query 债权详情信息
   * @return 是否添加成功
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public boolean addClaimFilling(String claimId, ClaimFillingQuery query) {
    log.info("添加债权详情信息，债权ID：{}，数据：{}", claimId, query);

    // 1. 检查债权申报是否存在
    claimService.validateAndGet(claimId);

    try {
      ClaimFillingEntity fillingEntity = new ClaimFillingEntity();
      fillingEntity.setClaimNature(query.getClaimNature());
      fillingEntity.setCollateralDetails(query.getCollateralDetails());
      fillingEntity.setClaimPrincipal(query.getClaimPrincipal());
      fillingEntity.setClaimInterest(query.getClaimInterest());
      fillingEntity.setClaimOther(query.getClaimOther());

      claimFillingMapper.insert(fillingEntity);
      log.info("添加债权详情信息成功，ID：{}", fillingEntity.getId());

      return true;
    } catch (Exception e) {
      String errorMsg = "添加债权详情信息异常，债权ID：" + claimId;
      log.error(errorMsg + "，异常信息：{}", e.getMessage(), e);
      throw new CustomException(
          HttpResponseStatus.INTERNAL_SERVER_ERROR.code(), "添加债权详情记录失败", errorMsg);
    }
  }

  /**
   * 删除债权详情信息
   *
   * @param claimFillingId 债权详情ID
   * @return 是否删除成功
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public boolean deleteClaimFilling(String claimFillingId) {
    log.info("删除债权详情信息，ID：{}", claimFillingId);
    this.validateAndGet(claimFillingId);
    try {
      // 2. 查询关联的债权确认记录
      List<ClaimConfirmEntity> confirmEntities =
          claimConfirmMapper.selectList(
              new LambdaQueryWrapperX<ClaimConfirmEntity>()
                  .eq(ClaimConfirmEntity::getClaimFillingId, claimFillingId));

      // 3. 删除关联的债权确认记录
      if (!CollectionUtils.isEmpty(confirmEntities)) {
        List<String> confirmIds =
            confirmEntities.stream().map(ClaimConfirmEntity::getId).collect(Collectors.toList());
        claimConfirmMapper.deleteBatchIds(confirmIds);
        log.info("删除关联的债权确认记录，数量：{}，关联债权详情ID：{}", confirmIds.size(), claimFillingId);
      }

      // 4. 删除债权详情记录
      claimFillingMapper.deleteById(claimFillingId);
      log.info("删除债权详情信息成功，ID：{}", claimFillingId);

      return true;
    } catch (CustomException e) {
      throw e; // 自定义异常直接抛出
    } catch (Exception e) {
      String errorMsg = "删除债权详情信息异常，ID：" + claimFillingId;
      log.error(errorMsg + "，异常信息：{}", e.getMessage(), e);
      throw new CustomException(
          HttpResponseStatus.INTERNAL_SERVER_ERROR.code(), "删除债权详情记录失败", errorMsg);
    }
  }

  @Override
  public ClaimFillingEntity validateAndGet(String claimFillingId) {
    if (!StringUtils.hasText(claimFillingId)) {
      throw new CustomException(
          HttpResponseStatus.BAD_REQUEST.code(), "债权详情ID不能为空", "删除债权详情信息失败，ID为空");
    }
    ClaimFillingEntity fillingEntity = claimFillingMapper.selectById(claimFillingId);
    if (fillingEntity == null) {
      throw new CustomException(
          HttpResponseStatus.NOT_FOUND.code(), "债权详情记录不存在", "债权详情ID不存在：" + claimFillingId);
    }
    return fillingEntity;
  }

  @Override
  public List<ClaimFillingDto> getClaimFillingByClaimId(String claimId) {
    if (claimId == null || claimId.isEmpty()) {
      return new ArrayList<>();
    }

    // 查询该债权申报下的所有债权申报金额记录
    List<ClaimFillingEntity> fillingEntities =
        claimFillingMapper.selectList(
            new LambdaQueryWrapperX<ClaimFillingEntity>()
                .eq(ClaimFillingEntity::getClaimId, claimId));

    if (fillingEntities.isEmpty()) {
      return new ArrayList<>();
    }

    // 收集所有填报ID，用于批量查询确认信息
    List<String> fillingIds = fillingEntities.stream().map(ClaimFillingEntity::getId).toList();

    // 转换为DTO并关联确认信息
    List<ClaimFillingDto> resultList = new ArrayList<>();

    // 填充基本信息
    for (ClaimFillingEntity entity : fillingEntities) {
      ClaimFillingDto fillingDto = new ClaimFillingDto();
      BeanUtils.copyProperties(entity, fillingDto);
      resultList.add(fillingDto);
    }

    // 如果没有填报ID，直接返回填报DTO列表（确认信息为null）
    if (fillingIds.isEmpty()) {
      return resultList;
    }

    // 批量查询所有关联的确认信息
    List<ClaimConfirmEntity> confirmEntities =
        claimConfirmMapper.selectList(
            new LambdaQueryWrapperX<ClaimConfirmEntity>()
                .in(ClaimConfirmEntity::getClaimFillingId, fillingIds));

    // 如果没有确认信息，直接返回填报DTO列表（确认信息为null）
    if (confirmEntities.isEmpty()) {
      return resultList;
    }

    // 将确认信息按填报ID进行分组，便于后续快速查找
    Map<String, ClaimConfirmEntity> confirmMap =
        confirmEntities.stream()
            .collect(
                Collectors.toMap(
                    ClaimConfirmEntity::getClaimFillingId,
                    confirmEntity -> confirmEntity,
                    // 如果有多个确认信息（理论上不应该），取最后一个
                    (existing, replacement) -> replacement));

    // 设置关联的确认信息
    for (ClaimFillingDto fillingDto : resultList) {
      ClaimConfirmEntity confirmEntity = confirmMap.get(fillingDto.getId());
      if (confirmEntity != null) {
        ClaimConfirmDto confirmDto = new ClaimConfirmDto();
        BeanUtils.copyProperties(confirmEntity, confirmDto);
        fillingDto.setConfirmedDetail(confirmDto);
      }
    }

    return resultList;
  }
}
