package com.backend.debt.algo;

import com.backend.debt.enums.ReviewStatus;
import com.backend.debt.model.dto.ClaimDetailDto;
import com.backend.debt.model.dto.DeclaredSummaryDto;
import com.backend.debt.model.dto.confirm.statistic.BaseConfirmStatisticDto;
import com.backend.debt.model.dto.confirm.statistic.ConfirmedStatisticDto;
import com.backend.debt.model.dto.confirm.statistic.RejectConfirmStatisticDto;
import com.backend.debt.model.dto.confirm.statistic.SuspendConfirmStatisticDto;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class StatisticCalculator {

  /**
   * 计算申报债权金额总计
   *
   * @return 申报金额总计
   */
  public DeclaredSummaryDto calculateDeclaredSummary(List<ClaimDetailDto> claimDetails) {

    if (claimDetails == null || claimDetails.isEmpty()) {
      return null;
    }
    Double declaredPrincipal =
        claimDetails.stream().mapToDouble(ClaimDetailDto::getDeclaredPrincipal).sum();
    Double declaredInterest =
        claimDetails.stream().mapToDouble(ClaimDetailDto::getDeclaredInterest).sum();
    Double declaredOther =
        claimDetails.stream().mapToDouble(ClaimDetailDto::getDeclaredOther).sum();
    Integer count = claimDetails.size();
    String collateralDetails =
        claimDetails.stream()
            .map(ClaimDetailDto::getCollateralDetails)
            .reduce((a, b) -> a + ", " + b)
            .orElse("");
    String claimNature =
        claimDetails.stream()
            .map(ClaimDetailDto::getClaimNature)
            .reduce((a, b) -> a + ", " + b)
            .orElse("");
    return new DeclaredSummaryDto(
        declaredPrincipal, declaredInterest, declaredOther, count, collateralDetails, claimNature);
  }

  /**
   * 计算不予确认债权统计信息。
   *
   * @param claimDetails 申报详情列表
   * @return 不予确认债权统计信息
   */
  public BaseConfirmStatisticDto calculateRejectConfirmStatistic(
      List<ClaimDetailDto> claimDetails) {
    boolean isReject =
        claimDetails.stream()
            .allMatch(
                claimDetailDto -> claimDetailDto.getReviewStatus() == ReviewStatus.CONFIRM_REJECT);
    if (claimDetails.isEmpty() || !isReject) {
      return new RejectConfirmStatisticDto(
          List.of(ReviewStatus.CONFIRM_REJECT), 0.0, 0.0, 0.0, 0, "");
    }
    List<ClaimDetailDto> rejectClaimDetails =
        claimDetails.stream()
            .filter(
                claimDetailDto -> claimDetailDto.getReviewStatus() == ReviewStatus.CONFIRM_REJECT)
            .toList();
    Double principal =
        rejectClaimDetails.stream().mapToDouble(ClaimDetailDto::getDeclaredPrincipal).sum();
    Double interest =
        rejectClaimDetails.stream().mapToDouble(ClaimDetailDto::getDeclaredInterest).sum();
    Double other = rejectClaimDetails.stream().mapToDouble(ClaimDetailDto::getDeclaredOther).sum();
    Integer count = rejectClaimDetails.size();
    String rejectReason =
        rejectClaimDetails.stream()
            .map(claim -> claim.getConfirmedDetail().getReviewReason())
            .reduce((a, b) -> a + ", " + b)
            .orElse("");
    return new RejectConfirmStatisticDto(
        List.of(ReviewStatus.CONFIRM_REJECT), principal, interest, other, count, rejectReason);
  }

  /**
   * 计算暂缓确认债权统计信息。
   *
   * @param claimDetails 申报详情列表
   * @return 暂缓确认认债权统计信息
   */
  public BaseConfirmStatisticDto calculateSuspendConfirmStatistic(
      List<ClaimDetailDto> claimDetails) {
    if (claimDetails == null || claimDetails.isEmpty()) {
      return null;
    }
    List<ClaimDetailDto> suspendClaimDetails =
        claimDetails.stream()
            .filter(
                claimDetailDto -> claimDetailDto.getReviewStatus() == ReviewStatus.CONFIRM_SUSPEND)
            .toList();
    Double principal =
        suspendClaimDetails.stream().mapToDouble(ClaimDetailDto::getDeclaredPrincipal).sum();
    Double interest =
        suspendClaimDetails.stream().mapToDouble(ClaimDetailDto::getDeclaredInterest).sum();
    Double other = suspendClaimDetails.stream().mapToDouble(ClaimDetailDto::getDeclaredOther).sum();
    Integer count = suspendClaimDetails.size();
    String nature =
        suspendClaimDetails.stream()
            .map(claim -> claim.getConfirmedDetail().getClaimNature())
            .reduce((a, b) -> a + ", " + b)
            .orElse("");
    return new SuspendConfirmStatisticDto(
        List.of(ReviewStatus.CONFIRM_SUSPEND), principal, interest, other, count, nature);
  }

  /**
   * 计算确认债权统计信息。
   *
   * @param claimDetails 申报详情列表
   * @return 确认债权统计信息汇总
   */
  public List<BaseConfirmStatisticDto> calculateConfirmedStatistic(
      List<ClaimDetailDto> claimDetails) {
    ConfirmedStatisticDto confirmedStatistic = new ConfirmedStatisticDto();
    RejectConfirmStatisticDto rejectConfirmStatistic = new RejectConfirmStatisticDto();
    SuspendConfirmStatisticDto suspendConfirmStatistic = new SuspendConfirmStatisticDto();
    for (ClaimDetailDto claimDetail : claimDetails) {
      if (claimDetail.getReviewStatus() == ReviewStatus.CONFIRMED_ALL) {
        // 如果这一笔是全部确认，直接加到债权确认汇总里
        confirmedStatistic.setPrincipal(
            confirmedStatistic.getPrincipal() + claimDetail.getDeclaredPrincipal());
        confirmedStatistic.setInterest(
            confirmedStatistic.getInterest() + claimDetail.getDeclaredInterest());
        confirmedStatistic.setOther(confirmedStatistic.getOther() + claimDetail.getDeclaredOther());
        confirmedStatistic.setCount(confirmedStatistic.getCount() + 1);
        confirmedStatistic.setCollateralDetails(
            this.stringAdd(
                confirmedStatistic.getCollateralDetails(), claimDetail.getCollateralDetails()));
        confirmedStatistic.setConfirmNature(
            this.stringAdd(
                confirmedStatistic.getConfirmNature(),
                claimDetail.getConfirmedDetail().getClaimNature()));
      }
      if (claimDetail.getReviewStatus() == ReviewStatus.CONFIRM_PART) {
        // 如果这一笔是部分确认，那需要加到债权确认汇总和削减里
        confirmedStatistic.setPrincipal(
            confirmedStatistic.getPrincipal() + claimDetail.getDeclaredPrincipal());
        confirmedStatistic.setInterest(
            confirmedStatistic.getInterest() + claimDetail.getDeclaredInterest());
        confirmedStatistic.setOther(confirmedStatistic.getOther() + claimDetail.getDeclaredOther());
        confirmedStatistic.setCount(confirmedStatistic.getCount() + 1);
        Double deduce =
            claimDetail.getDeclaredTotal() - claimDetail.getConfirmedDetail().getConfirmedTotal();
        confirmedStatistic.setDeductionAmount(confirmedStatistic.getDeductionAmount() + deduce);
        confirmedStatistic.setConfirmNature(
            this.stringAdd(
                confirmedStatistic.getConfirmNature(),
                claimDetail.getConfirmedDetail().getClaimNature()));
        confirmedStatistic.setCollateralDetails(
            this.stringAdd(
                confirmedStatistic.getCollateralDetails(), claimDetail.getCollateralDetails()));
      }

      if (claimDetail.getReviewStatus() == ReviewStatus.CONFIRM_SUSPEND) {
        suspendConfirmStatistic.setPrincipal(
            suspendConfirmStatistic.getPrincipal() + claimDetail.getDeclaredPrincipal());
        suspendConfirmStatistic.setInterest(
            suspendConfirmStatistic.getInterest() + claimDetail.getDeclaredInterest());
        suspendConfirmStatistic.setOther(
            suspendConfirmStatistic.getOther() + claimDetail.getDeclaredOther());
        suspendConfirmStatistic.setCount(suspendConfirmStatistic.getCount() + 1);
        suspendConfirmStatistic.setSuspendNature(ReviewStatus.CONFIRM_SUSPEND.getDisplayName());
        // 暂缓确认的需要加到债权确认汇总的削减金额里
        Double deduce = claimDetail.getDeclaredTotal();
        confirmedStatistic.setDeductionAmount(confirmedStatistic.getDeductionAmount() + deduce);
      }

      if (claimDetail.getReviewStatus() == ReviewStatus.CONFIRM_REJECT) {
        // 不予确认的 需要加到确认 汇总的削减金额里
        Double deduce = claimDetail.getDeclaredTotal();
        confirmedStatistic.setDeductionAmount(confirmedStatistic.getDeductionAmount() + deduce);
      }
    }
    return new ArrayList<>();

    // 不予确认
    // 部分确认、暂缓确认
    // 部分确认、不予确认
  }

  public ReviewStatus getReviewStatus(List<ClaimDetailDto> claimDetails) {
    // 全部不予确认，则汇总状态为不予确认
    if (claimDetails.stream()
        .allMatch(
            claimDetailDto -> claimDetailDto.getReviewStatus() == ReviewStatus.CONFIRM_REJECT)) {
      return ReviewStatus.CONFIRM_REJECT;
    }
    // 全部确认，则汇总状态为全部确认
    if (claimDetails.stream()
        .allMatch(
            claimDetailDto -> claimDetailDto.getReviewStatus() == ReviewStatus.CONFIRMED_ALL)) {
      return ReviewStatus.CONFIRMED_ALL;
    }
    // 暂缓确认。只存在不予确认和暂缓确认。
    boolean hasSuspend =
        claimDetails.stream()
            .anyMatch(
                claimDetailDto -> claimDetailDto.getReviewStatus() == ReviewStatus.CONFIRM_SUSPEND);
    boolean hasReject =
        claimDetails.stream()
            .anyMatch(
                claimDetailDto -> claimDetailDto.getReviewStatus() == ReviewStatus.CONFIRM_REJECT);
    boolean hasPartConfirm =
        claimDetails.stream()
            .anyMatch(
                claimDetailDto -> claimDetailDto.getReviewStatus() == ReviewStatus.CONFIRM_PART);
    // 只要存在部分确认
    if (hasPartConfirm) {
      return ReviewStatus.CONFIRM_PART;
    }
    if (claimDetails.stream()
        .anyMatch(
            claimDetailDto -> claimDetailDto.getReviewStatus() == ReviewStatus.CONFIRM_SUSPEND)) {
      return ReviewStatus.CONFIRM_SUSPEND;
    }

    return ReviewStatus.CONFIRM_PART;
  }

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
    return list.isEmpty() ? toAdd : String.join("、", list);
  }
}
