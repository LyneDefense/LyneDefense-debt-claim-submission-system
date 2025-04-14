package com.backend.debt.model.dto;

import com.backend.debt.enums.ReviewStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** 债权审查确认情况 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeclaredConfirmInfoDto {

  /** 审计状态 */
  private ReviewStatus reviewStatus;

  /** 确认本金 */
  private Double confirmedPrincipal;

  /** 确认利息 */
  private Double confirmedInterest;

  /** 确认其他金额 */
  private Double confirmedOther;

  /** 确认债权性质 */
  private String claimNature;

  /** 确认时削减金额 */
  private Double deductionAmount;

  /** 审查理由 */
  private String reviewReason;

  private Double getConfirmedTotal() {
    return this.confirmedPrincipal
        + this.confirmedInterest
        + this.confirmedOther
        - this.deductionAmount;
  }
}
