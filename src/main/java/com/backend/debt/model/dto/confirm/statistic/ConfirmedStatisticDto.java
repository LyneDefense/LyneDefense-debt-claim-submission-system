package com.backend.debt.model.dto.confirm.statistic;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmedStatisticDto {

  /** 本金 */
  private Double principal;

  /** 利息 */
  private Double interest;

  /** 其他 */
  private Double other;

  /** 笔数 */
  private Integer count;

  /** 担保物明细 */
  private String collateralDetails;

  /** 确认性质 */
  private String confirmNature;

  /** 削减金额 */
  private Double deductionAmount;

  private Double getTotal() {
    return this.principal + this.interest + this.other;
  }
}
