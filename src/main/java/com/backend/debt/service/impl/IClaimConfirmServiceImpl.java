package com.backend.debt.service.impl;

import com.backend.debt.enums.ReviewStatus;
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
import java.util.Objects;
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
      this.validate(fillingEntity, query);
      ClaimConfirmEntity confirmEntity = query.to(claimFillingId);
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
    this.validate(fillingEntity, query);
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

  private void validate(ClaimFillingEntity fillingEntity, ClaimConfirmQuery query) {
    // 如果确认状态是全部确认
    if (query.getReviewStatus() == ReviewStatus.CONFIRM_ALL) {
      // 确认的本金、利息和其他金额必须等于申报的金额
      if (!Objects.equals(fillingEntity.getClaimPrincipal(), query.getConfirmedPrincipal())) {
        throw new CustomException(
            HttpResponseStatus.BAD_REQUEST.code(),
            "确认状态为全部确认，债权确认本金金额不匹配",
            "确认状态为全部确认，债权确认本金金额不匹配");
      }
      if (!Objects.equals(fillingEntity.getClaimInterest(), query.getConfirmedInterest())) {
        throw new CustomException(
            HttpResponseStatus.BAD_REQUEST.code(),
            "确认状态为全部确认，债权确认利息金额不匹配",
            "确认状态为全部确认，债权确认本金金额不匹配");
      }
      if (!Objects.equals(fillingEntity.getClaimOther(), query.getConfirmedOther())) {
        throw new CustomException(
            HttpResponseStatus.BAD_REQUEST.code(),
            "确认状态为全部确认，债权确认其他金额不匹配",
            "确认状态为全部确认，债权确认本金金额不匹配");
      }
    }
    if (query.getReviewStatus() == ReviewStatus.CONFIRM_PART) {
      // 如果是部分确认，确认的金额必须小于等于申报的金额
      if (query.getConfirmedPrincipal() > fillingEntity.getClaimPrincipal()
          || query.getConfirmedInterest() > fillingEntity.getClaimInterest()
          || query.getConfirmedOther() > fillingEntity.getClaimOther()) {
        throw new CustomException(
            HttpResponseStatus.BAD_REQUEST.code(), "债权确认金额超过申报金额", "债权确认金额超过申报金额");
      }
    }
    if (query.getReviewStatus() == ReviewStatus.CONFIRM_REJECT) {
      // 如果是拒绝确认，确认的金额必须为0或者Null
      if ((query.getConfirmedPrincipal() != null && query.getConfirmedPrincipal() != 0)
          || (query.getConfirmedInterest() != null && query.getConfirmedInterest() != 0)
          || (query.getConfirmedOther() != null && query.getConfirmedOther() != 0)) {
        throw new CustomException(
            HttpResponseStatus.BAD_REQUEST.code(), "拒绝确认时，债权确认金额必须为0或为NULL", "拒绝确认时，债权确认金额必须为0");
      }
    }
    if (query.getReviewStatus() == ReviewStatus.CONFIRM_SUSPEND) {
      // 如果是暂缓确认，确认的金额必须为0或者Null
      if ((query.getConfirmedPrincipal() != null && query.getConfirmedPrincipal() != 0)
          || (query.getConfirmedInterest() != null && query.getConfirmedInterest() != 0)
          || (query.getConfirmedOther() != null && query.getConfirmedOther() != 0)) {
        throw new CustomException(
            HttpResponseStatus.BAD_REQUEST.code(), "暂缓确认时，债权确认金额必须为0或为NULL", "拒绝确认时，债权确认金额必须为0");
      }
    }
  }
}
