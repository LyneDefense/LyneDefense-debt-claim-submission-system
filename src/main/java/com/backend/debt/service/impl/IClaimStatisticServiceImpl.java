package com.backend.debt.service.impl;

import com.backend.debt.enums.ReviewStatus;
import com.backend.debt.enums.StatisticStatus;
import com.backend.debt.model.dto.ClaimConfirmDto;
import com.backend.debt.model.dto.ClaimFillingDto;
import com.backend.debt.model.dto.confirm.statistic.*;
import com.backend.debt.service.IClaimFillingService;
import com.backend.debt.service.IClaimStatisticService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jakarta.annotation.Resource;
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
    return claimFillings.stream()
        .allMatch(claimFilling -> claimFilling.getConfirmedDetail() != null);
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
        case CONFIRMED_ALL:
          processAllConfirmed(claimFilling, claimConfirm, confirmedStatistic);
          allConfirmCount++;
          break;
        case CONFIRM_PART:
          processPartConfirmed(claimFilling, claimConfirm, confirmedStatistic);
          partConfirmCount++;
          break;
        case CONFIRM_SUSPEND:
          processSuspendConfirmed(claimConfirm, suspendConfirmStatistic);
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
        confirmedStatistic.getPrincipal() + claimConfirm.getConfirmedPrincipal());
    confirmedStatistic.setInterest(
        confirmedStatistic.getInterest() + claimConfirm.getConfirmedInterest());
    confirmedStatistic.setOther(confirmedStatistic.getOther() + claimConfirm.getConfirmedOther());
    confirmedStatistic.setCount(confirmedStatistic.getCount() + 1);
    confirmedStatistic.setCollateralDetails(
        this.stringAdd(
            confirmedStatistic.getCollateralDetails(), claimFilling.getCollateralDetails()));
    confirmedStatistic.setConfirmNature(
        this.stringAdd(
            confirmedStatistic.getConfirmNature(),
            claimFilling.getConfirmedDetail().getClaimNature()));
  }

  /** 处理部分确认的债权。 */
  private void processPartConfirmed(
      ClaimFillingDto claimFilling,
      ClaimConfirmDto claimConfirm,
      ConfirmedStatisticDto confirmedStatistic) {
    confirmedStatistic.setPrincipal(
        confirmedStatistic.getPrincipal() + claimConfirm.getConfirmedPrincipal());
    confirmedStatistic.setInterest(
        confirmedStatistic.getInterest() + claimConfirm.getConfirmedInterest());
    confirmedStatistic.setOther(confirmedStatistic.getOther() + claimConfirm.getConfirmedOther());
    confirmedStatistic.setCount(confirmedStatistic.getCount() + 1);
    Double deduce =
        claimFilling.getDeclaredTotal() - claimFilling.getConfirmedDetail().getConfirmedTotal();
    confirmedStatistic.setConfirmNature(
        this.stringAdd(
            confirmedStatistic.getConfirmNature(),
            claimFilling.getConfirmedDetail().getClaimNature()));
    confirmedStatistic.setCollateralDetails(
        this.stringAdd(
            confirmedStatistic.getCollateralDetails(), claimFilling.getCollateralDetails()));
    confirmedStatistic.setDeductionAmount(confirmedStatistic.getDeductionAmount() + deduce);
  }

  /** 处理暂缓确认的债权。 */
  private void processSuspendConfirmed(
      ClaimConfirmDto claimConfirm, SuspendConfirmStatisticDto suspendConfirmStatistic) {
    suspendConfirmStatistic.setPrincipal(
        suspendConfirmStatistic.getPrincipal() + claimConfirm.getConfirmedPrincipal());
    suspendConfirmStatistic.setInterest(
        suspendConfirmStatistic.getInterest() + claimConfirm.getConfirmedInterest());
    suspendConfirmStatistic.setOther(
        suspendConfirmStatistic.getOther() + claimConfirm.getConfirmedOther());
    suspendConfirmStatistic.setCount(suspendConfirmStatistic.getCount() + 1);
    suspendConfirmStatistic.setSuspendNature(ReviewStatus.CONFIRM_SUSPEND.getDisplayName());
  }

  /** 处理拒绝确认的债权。 */
  private void processRejectConfirmed(
      ClaimFillingDto claimFilling,
      ClaimConfirmDto claimConfirm,
      ConfirmedStatisticDto confirmedStatistic,
      RejectConfirmStatisticDto rejectConfirmStatistic) {
    Double deduce = claimFilling.getDeclaredTotal();
    confirmedStatistic.setDeductionAmount(confirmedStatistic.getDeductionAmount() + deduce);
    rejectConfirmStatistic.setInterest(
        rejectConfirmStatistic.getInterest() + claimFilling.getClaimInterest());
    rejectConfirmStatistic.setPrincipal(
        rejectConfirmStatistic.getPrincipal() + claimFilling.getClaimPrincipal());
    rejectConfirmStatistic.setOther(
        rejectConfirmStatistic.getOther() + claimFilling.getClaimOther());
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
          List.of(StatisticStatus.CONFIRMED_ALL), confirmedStatistic, null, null);
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
            List.of(StatisticStatus.CONFIRM_PART, StatisticStatus.CONFIRM_SUSPEND),
            confirmedStatistic,
            null,
            null);

    if (partConfirmCount > 0 && suspendCount > 0) {
      dto.setStatisticStatus(
          List.of(StatisticStatus.CONFIRM_PART, StatisticStatus.CONFIRM_SUSPEND));
      dto.setSuspendConfirmStatistic(suspendConfirmStatistic);
    }

    return dto;
  }

  /**
   * 将字符串添加到源字符串中，处理空值和重复值。 字符串使用"、"作为分隔符连接。
   *
   * @param source 要添加到的源字符串
   * @param toAdd 要添加的字符串
   * @return 去重后的组合字符串
   */
  public String stringAdd(String source, String toAdd) {
    if (source == null) {
      source = "";
    }

    if (toAdd == null || toAdd.isEmpty()) {
      return source;
    }

    // 分割字符串并过滤空值
    List<String> list = new ArrayList<>(Arrays.asList(source.split("、")));
    list.removeIf(String::isEmpty);

    // 判断并去重添加
    if (!list.contains(toAdd)) {
      list.add(toAdd);
    }

    // 空列表直接返回 toAdd（兼容原逻辑）
    return String.join("、", list);
  }
}
