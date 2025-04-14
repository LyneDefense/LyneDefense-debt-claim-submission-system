package com.backend.debt.model.dto.confirm.statistic;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SuspendConfirmStatisticDto {

  /** 暂缓确认性质 */
  private String suspendNature;
}
