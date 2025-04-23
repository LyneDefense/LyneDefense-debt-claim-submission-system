package com.backend.debt.service;

import com.backend.debt.model.dto.confirm.statistic.ClaimConfirmStatisticDto;

public interface IClaimStatisticService {

  /**
   * 计算指定债权ID的确认统计信息。
   *
   * @param claimId claimId
   * @return ClaimConfirmStatisticDto
   */
  ClaimConfirmStatisticDto calculateConfirmedStatistic(String claimId);
}
