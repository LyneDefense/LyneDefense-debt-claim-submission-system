package com.backend.debt.model.dto.confirm.statistic;

import com.backend.debt.enums.ReviewStatus;
import com.backend.debt.enums.StatisticStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClaimConfirmStatisticDto {

  private List<StatisticStatus> statisticStatus;

  private ConfirmedStatisticDto confirmedStatistic;

  private RejectConfirmStatisticDto rejectConfirmStatistic;

  private SuspendConfirmStatisticDto suspendConfirmStatistic;
}
