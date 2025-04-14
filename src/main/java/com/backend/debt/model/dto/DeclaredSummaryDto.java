package com.backend.debt.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeclaredSummaryDto {

  /** 申报本金合计 */
  private Double declaredPrincipal;

  /** 申报利息合计 */
  private Double declaredInterest;

  /** 其他项目金额合计 */
  private Double declaredOther;

  /** 笔数 */
  private Integer count;

  /** 担保物明细 */
  private String collateralDetails;

  /** 债权性质 */
  private String claimNature;

  /** 申报总金额（自动计算字段） */
  private Double getTotal() {
    return this.declaredPrincipal + this.declaredInterest + this.declaredOther;
  }
}
