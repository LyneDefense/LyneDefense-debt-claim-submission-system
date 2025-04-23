package com.backend.debt.service.impl;

import com.backend.debt.exceptions.CustomException;
import com.backend.debt.exceptions.HttpResponseStatus;
import com.backend.debt.mapper.ClaimConfirmMapper;
import com.backend.debt.model.dto.ClaimConfirmDto;
import com.backend.debt.model.entity.ClaimConfirmEntity;
import com.backend.debt.model.entity.ClaimFillingEntity;
import com.backend.debt.model.query.ClaimConfirmQuery;
import com.backend.debt.service.IClaimConfirmService;
import com.backend.debt.service.IClaimFillingService;
import com.backend.debt.service.IClaimService;
import java.time.LocalDateTime;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@Service
public class IClaimConfirmServiceImpl implements IClaimConfirmService {

  @Resource private ClaimConfirmMapper claimConfirmMapper;
  @Resource private IClaimService claimService;
  @Resource private IClaimFillingService claimFillingService;

  /**
   * 添加债权确认信息
   *
   * @param claimFillingId 债权详情ID
   * @param query 债权确认信息
   * @return 添加的债权确认信息
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public ClaimConfirmDto addClaimConfirm(String claimFillingId, ClaimConfirmQuery query) {
    log.info("添加债权确认信息，债权详情ID：{}，数据：{}", claimFillingId, query);

    try {
      ClaimFillingEntity fillingEntity = claimFillingService.validateAndGet(claimFillingId);
      String claimId = fillingEntity.getClaimId();
      claimService.validateAndGet(claimId);

      ClaimConfirmEntity confirmEntity = new ClaimConfirmEntity();
      confirmEntity.setClaimFillingId(claimFillingId);
      // 5. 设置债权确认信息
      confirmEntity.setReviewStatus(query.getReviewStatus());
      confirmEntity.setConfirmedPrincipal(query.getConfirmedPrincipal());
      confirmEntity.setConfirmedInterest(query.getConfirmedInterest());
      confirmEntity.setConfirmedOther(query.getConfirmedOther());
      confirmEntity.setClaimNature(query.getClaimNature());
      confirmEntity.setReviewReason(query.getReviewReason());

      claimConfirmMapper.insert(confirmEntity);
      log.info("添加债权确认信息成功，ID：{}", confirmEntity.getId());

      return ClaimConfirmDto.of(confirmEntity, fillingEntity);
    } catch (CustomException e) {
      throw e;
    } catch (Exception e) {
      String errorMsg = "添加债权确认信息异常，债权详情ID：" + claimFillingId;
      log.error(errorMsg + "，异常信息：{}", e.getMessage(), e);
      throw new CustomException(
          HttpResponseStatus.INTERNAL_SERVER_ERROR.code(), "添加债权确认记录失败", errorMsg);
    }
  }

  /**
   * 更新债权确认信息
   *
   * @param claimConfirmId 债权确认ID
   * @param query 债权确认更新信息
   * @return 是否更新成功
   */
  @Override
  public ClaimConfirmDto updateClaimConfirm(String claimConfirmId, ClaimConfirmQuery query) {
    ClaimConfirmEntity updateEntity = this.validateAndGet(claimConfirmId);
    ClaimFillingEntity fillingEntity =
        claimFillingService.validateAndGet(updateEntity.getClaimFillingId());
    // 更新确认实体
    updateEntity.setId(claimConfirmId);
    // 只更新非空字段，避免覆盖现有数据
    if (query.getReviewStatus() != null) {
      updateEntity.setReviewStatus(query.getReviewStatus());
    }
    if (query.getConfirmedPrincipal() != null) {
      updateEntity.setConfirmedPrincipal(query.getConfirmedPrincipal());
    }
    if (query.getConfirmedInterest() != null) {
      updateEntity.setConfirmedInterest(query.getConfirmedInterest());
    }
    if (query.getConfirmedOther() != null) {
      updateEntity.setConfirmedOther(query.getConfirmedOther());
    }
    if (query.getClaimNature() != null) {
      updateEntity.setClaimNature(query.getClaimNature());
    }
    if (query.getReviewReason() != null) {
      updateEntity.setReviewReason(query.getReviewReason());
    }
    updateEntity.setUpdateTime(LocalDateTime.now());

    // 更新数据库
    claimConfirmMapper.updateById(updateEntity);
    return ClaimConfirmDto.of(updateEntity, fillingEntity);
  }

  /**
   * 删除债权确认信息
   *
   * @param claimConfirmId 债权确认ID
   * @return 是否删除成功
   */
  @Override
  public boolean deleteClaimConfirm(String claimConfirmId) {
    this.validateAndGet(claimConfirmId);
    // 执行删除操作
    int rows = claimConfirmMapper.deleteById(claimConfirmId);
    return rows > 0;
  }

  public ClaimConfirmEntity validateAndGet(String claimConfirmId) {
    if (!StringUtils.hasText(claimConfirmId)) {
      throw new CustomException(
          HttpResponseStatus.BAD_REQUEST.code(), "债权确认ID不能为空", "删除债权确认信息失败，ID为空");
    }
    ClaimConfirmEntity confirmEntity = claimConfirmMapper.selectById(claimConfirmId);
    if (confirmEntity == null) {
      throw new CustomException(
          HttpResponseStatus.NOT_FOUND.code(), "债权确认记录不存在", "删除债权确认信息失败，ID不存在：" + claimConfirmId);
    }
    return confirmEntity;
  }
}
