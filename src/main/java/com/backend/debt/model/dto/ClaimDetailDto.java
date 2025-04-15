package com.backend.debt.model.dto;

import com.backend.debt.enums.ReviewStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClaimDetailDto {

  /** 申报债权性质 */
  private String claimNature;

  /** 担保物明细 */
  private String collateralDetails;

  /** 申报本金金额 */
  private Double declaredPrincipal;

  /** 申报利息金额 */
  private Double declaredInterest;

  /** 申报其他项目金额 */
  private Double declaredOther;

  /** 审查状态（部分确认/暂缓确认/不予确认） */
  private ReviewStatus reviewStatus;

  /** 确认部分详情 */
  private DeclaredConfirmInfoDto confirmedDetail;


  /** 申报金额合计（自动计算字段） */
  public Double getDeclaredTotal() {
    return this.declaredPrincipal + this.declaredInterest + this.declaredOther;
  }
}
