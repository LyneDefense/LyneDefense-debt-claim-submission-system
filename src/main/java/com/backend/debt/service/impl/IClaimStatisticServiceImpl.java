package com.backend.debt.service.impl;

import com.backend.debt.enums.ReviewStatus;
import com.backend.debt.enums.StatisticStatus;
import com.backend.debt.model.dto.ClaimConfirmDto;
import com.backend.debt.model.dto.ClaimFillingDto;
import com.backend.debt.model.dto.confirm.statistic.*;
import com.backend.debt.service.IClaimFillingService;
import com.backend.debt.service.IClaimStatisticService;
import com.backend.debt.util.StringUtils;
import java.util.List;
import javax.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class IClaimStatisticServiceImpl implements IClaimStatisticService {

  @Resource private IClaimFillingService claimFillingService;

  /**
   * 计算指定债权ID的确认统计信息。 该方法处理所有债权申报并基于其确认状态生成统计信息。
   *
   * @param claimId 要计算统计信息的债权ID
   * @return 包含计算统计信息的ClaimConfirmStatisticDto对象
   */
  @Override
  public ClaimConfirmStatisticDto calculateConfirmedStatistic(String claimId) {
    List<ClaimFillingDto> claimFillings = claimFillingService.getClaimFillingByClaimId(claimId);

    if (!isAllClaimsConfirmed(claimFillings)) {
      log.info("债权没有全部审核确认，不进行统计，ID:{}", claimId);
      return new ClaimConfirmStatisticDto(
          List.of(StatisticStatus.CONFIRM_NOT_COMPLETE), null, null, null);
    }

    ConfirmedStatisticDto confirmedStatistic = new ConfirmedStatisticDto();
    RejectConfirmStatisticDto rejectConfirmStatistic = new RejectConfirmStatisticDto();
    SuspendConfirmStatisticDto suspendConfirmStatistic = new SuspendConfirmStatisticDto();

    int[] counts =
        processClaimFillings(
            claimFillings, confirmedStatistic, rejectConfirmStatistic, suspendConfirmStatistic);

    return determineStatisticResult(
        claimFillings.size(),
        counts,
        confirmedStatistic,
        rejectConfirmStatistic,
        suspendConfirmStatistic);
  }

  /**
   * 检查所有债权是否都已确认。
   *
   * @param claimFillings 要检查的债权申报列表
   * @return 如果所有债权都已确认返回true，否则返回false
   */
  private boolean isAllClaimsConfirmed(List<ClaimFillingDto> claimFillings) {
    if (claimFillings == null || claimFillings.isEmpty()) {
      return false;
    }
    boolean allClaimsConfirmed = true;
    for (ClaimFillingDto claimFilling : claimFillings) {
      if (claimFilling.getConfirmedDetail() == null) {
        allClaimsConfirmed = false;
        break;
      }
      if (claimFilling.getConfirmedDetail().getReviewStatus() != null
          && claimFilling.getConfirmedDetail().getReviewStatus() == ReviewStatus.NOT_CONFIRMED) {
        allClaimsConfirmed = false;
        break;
      }
    }
    return allClaimsConfirmed;
  }

  /**
   * 处理所有债权申报并更新相应的统计DTO。 返回包含不同确认类型计数的数组： [拒绝计数, 暂缓计数, 全部确认计数, 部分确认计数]
   *
   * @param claimFillings 要处理的债权申报列表
   * @param confirmedStatistic 确认统计的DTO
   * @param rejectConfirmStatistic 拒绝确认统计的DTO
   * @param suspendConfirmStatistic 暂缓确认统计的DTO
   * @return 不同确认类型的计数数组
   */
  private int[] processClaimFillings(
      List<ClaimFillingDto> claimFillings,
      ConfirmedStatisticDto confirmedStatistic,
      RejectConfirmStatisticDto rejectConfirmStatistic,
      SuspendConfirmStatisticDto suspendConfirmStatistic) {

    int rejectCount = 0;
    int suspendCount = 0;
    int allConfirmCount = 0;
    int partConfirmCount = 0;

    for (ClaimFillingDto claimFilling : claimFillings) {
      ClaimConfirmDto claimConfirm = claimFilling.getConfirmedDetail();

      switch (claimConfirm.getReviewStatus()) {
        case CONFIRM_ALL:
          processAllConfirmed(claimFilling, claimConfirm, confirmedStatistic);
          allConfirmCount++;
          break;
        case CONFIRM_PART:
          processPartConfirmed(claimFilling, claimConfirm, confirmedStatistic);
          partConfirmCount++;
          break;
        case CONFIRM_SUSPEND:
          processSuspendConfirmed(claimFilling, claimConfirm, suspendConfirmStatistic);
          suspendCount++;
          break;
        case CONFIRM_REJECT:
          processRejectConfirmed(
              claimFilling, claimConfirm, confirmedStatistic, rejectConfirmStatistic);
          rejectCount++;
          break;
      }
    }

    return new int[] {rejectCount, suspendCount, allConfirmCount, partConfirmCount};
  }

  /** 处理已完全确认的债权。 */
  private void processAllConfirmed(
      ClaimFillingDto claimFilling,
      ClaimConfirmDto claimConfirm,
      ConfirmedStatisticDto confirmedStatistic) {
    confirmedStatistic.setPrincipal(
        addNullSafe(confirmedStatistic.getPrincipal(), claimConfirm.getConfirmedPrincipal()));
    confirmedStatistic.setInterest(
        addNullSafe(confirmedStatistic.getInterest(), claimConfirm.getConfirmedInterest()));
    confirmedStatistic.setOther(
        addNullSafe(confirmedStatistic.getOther(), claimConfirm.getConfirmedOther()));
    confirmedStatistic.setCount(incrementNullSafe(confirmedStatistic.getCount()));
    confirmedStatistic.setCollateralDetails(
        StringUtils.stringAdd(
            confirmedStatistic.getCollateralDetails(), claimFilling.getCollateralDetails()));
    confirmedStatistic.setConfirmNature(
        StringUtils.stringAdd(
            confirmedStatistic.getConfirmNature(),
            claimFilling.getConfirmedDetail().getClaimNature()));
  }

  /** 处理部分确认的债权。 */
  private void processPartConfirmed(
      ClaimFillingDto claimFilling,
      ClaimConfirmDto claimConfirm,
      ConfirmedStatisticDto confirmedStatistic) {
    confirmedStatistic.setPrincipal(
        addNullSafe(confirmedStatistic.getPrincipal(), claimConfirm.getConfirmedPrincipal()));
    confirmedStatistic.setInterest(
        addNullSafe(confirmedStatistic.getInterest(), claimConfirm.getConfirmedInterest()));
    confirmedStatistic.setOther(
        addNullSafe(confirmedStatistic.getOther(), claimConfirm.getConfirmedOther()));
    confirmedStatistic.setCount(incrementNullSafe(confirmedStatistic.getCount()));
    Double deduce =
        subtractNullSafe(
            claimFilling.getTotal(), claimFilling.getConfirmedDetail().getConfirmedTotal());
    confirmedStatistic.setConfirmNature(
        StringUtils.stringAdd(
            confirmedStatistic.getConfirmNature(),
            claimFilling.getConfirmedDetail().getClaimNature()));
    confirmedStatistic.setCollateralDetails(
        StringUtils.stringAdd(
            confirmedStatistic.getCollateralDetails(), claimFilling.getCollateralDetails()));
    confirmedStatistic.setDeductionAmount(
        addNullSafe(confirmedStatistic.getDeductionAmount(), deduce));
    confirmedStatistic.setConfirmNature(
        StringUtils.stringAdd(
            confirmedStatistic.getConfirmNature(), claimConfirm.getClaimNature()));
  }

  /** 处理暂缓确认的债权。 */
  private void processSuspendConfirmed(
      ClaimFillingDto claimFilling,
      ClaimConfirmDto claimConfirm,
      SuspendConfirmStatisticDto suspendConfirmStatistic) {
    suspendConfirmStatistic.setPrincipal(
        addNullSafe(suspendConfirmStatistic.getPrincipal(), claimFilling.getClaimPrincipal()));
    suspendConfirmStatistic.setInterest(
        addNullSafe(suspendConfirmStatistic.getInterest(), claimFilling.getClaimInterest()));
    suspendConfirmStatistic.setOther(
        addNullSafe(suspendConfirmStatistic.getOther(), claimFilling.getClaimOther()));
    suspendConfirmStatistic.setCount(incrementNullSafe(suspendConfirmStatistic.getCount()));
    suspendConfirmStatistic.setSuspendNature(
        StringUtils.stringAdd(
            suspendConfirmStatistic.getSuspendNature(), claimConfirm.getClaimNature()));
  }

  /** 处理拒绝确认的债权。 */
  private void processRejectConfirmed(
      ClaimFillingDto claimFilling,
      ClaimConfirmDto claimConfirm,
      ConfirmedStatisticDto confirmedStatistic,
      RejectConfirmStatisticDto rejectConfirmStatistic) {
    Double deduce = claimFilling.getTotal();
    confirmedStatistic.setDeductionAmount(
        addNullSafe(confirmedStatistic.getDeductionAmount(), deduce));
    rejectConfirmStatistic.setInterest(
        addNullSafe(rejectConfirmStatistic.getInterest(), claimFilling.getClaimInterest()));
    rejectConfirmStatistic.setPrincipal(
        addNullSafe(rejectConfirmStatistic.getPrincipal(), claimFilling.getClaimPrincipal()));
    rejectConfirmStatistic.setOther(
        addNullSafe(rejectConfirmStatistic.getOther(), claimFilling.getClaimOther()));
    rejectConfirmStatistic.setCount(incrementNullSafe(rejectConfirmStatistic.getCount()));
    rejectConfirmStatistic.setRejectReason(
        StringUtils.stringAdd(
            rejectConfirmStatistic.getRejectReason(), claimConfirm.getReviewReason()));
  }

  /**
   * 根据计数和处理后的统计信息确定最终的统计结果。
   *
   * @param totalClaims 债权总数
   * @param counts 包含不同确认类型计数的数组
   * @param confirmedStatistic 确认统计的DTO
   * @param rejectConfirmStatistic 拒绝确认统计的DTO
   * @param suspendConfirmStatistic 暂缓确认统计的DTO
   * @return 包含适当状态和统计信息的最终ClaimConfirmStatisticDto
   */
  private ClaimConfirmStatisticDto determineStatisticResult(
      int totalClaims,
      int[] counts,
      ConfirmedStatisticDto confirmedStatistic,
      RejectConfirmStatisticDto rejectConfirmStatistic,
      SuspendConfirmStatisticDto suspendConfirmStatistic) {

    int rejectCount = counts[0];
    int suspendCount = counts[1];
    int allConfirmCount = counts[2];
    int partConfirmCount = counts[3];

    if (allConfirmCount == totalClaims) {
      return new ClaimConfirmStatisticDto(
          List.of(StatisticStatus.CONFIRM_ALL), confirmedStatistic, null, null);
    }
    if (rejectCount == totalClaims) {
      return new ClaimConfirmStatisticDto(
          List.of(StatisticStatus.CONFIRM_REJECT), null, rejectConfirmStatistic, null);
    }
    if (suspendCount == totalClaims || suspendCount + rejectCount == totalClaims) {
      return new ClaimConfirmStatisticDto(
          List.of(StatisticStatus.CONFIRM_SUSPEND), null, null, suspendConfirmStatistic);
    }
    if (partConfirmCount == totalClaims || partConfirmCount + rejectCount == totalClaims) {
      return new ClaimConfirmStatisticDto(List.of(StatisticStatus.CONFIRM_PART), null, null, null);
    }

    ClaimConfirmStatisticDto dto =
        new ClaimConfirmStatisticDto(
            List.of(StatisticStatus.CONFIRM_PART), confirmedStatistic, null, null);

    if (partConfirmCount > 0 && suspendCount > 0) {
      dto.setStatisticStatus(
          List.of(StatisticStatus.CONFIRM_PART, StatisticStatus.CONFIRM_SUSPEND));
      dto.setSuspendConfirmStatistic(suspendConfirmStatistic);
    }

    return dto;
  }

  /**
   * 空值安全的加法运算，如果任一参数为null，视为0
   *
   * @param a 第一个操作数
   * @param b 第二个操作数
   * @return 两数之和，任一为null则视为0
   */
  private Double addNullSafe(Double a, Double b) {
    return (a == null ? 0.0 : a) + (b == null ? 0.0 : b);
  }

  /**
   * 空值安全的减法运算，如果任一参数为null，视为0
   *
   * @param a 被减数
   * @param b 减数
   * @return 减法结果，任一为null则视为0
   */
  private Double subtractNullSafe(Double a, Double b) {
    return (a == null ? 0.0 : a) - (b == null ? 0.0 : b);
  }

  /**
   * 空值安全的Integer递增运算，如果值为null，视为0再递增
   *
   * @param value 要递增的值
   * @return 递增后的结果，原值为null则返回1
   */
  private Integer incrementNullSafe(Integer value) {
    return (value == null ? 0 : value) + 1;
  }
}
