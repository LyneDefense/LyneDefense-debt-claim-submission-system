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

  /** 担保物明细 */
  private String collateralDetails;

  /** 确认性质 */
  private String confirmNature;
}
