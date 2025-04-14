package com.backend.debt.model.dto.confirm.statistic;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RejectConfirmStatisticDto extends BaseConfirmStatisticDto {

  /** 不予确认原因 */
  private String rejectReason;
}
