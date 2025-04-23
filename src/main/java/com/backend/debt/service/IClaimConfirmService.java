package com.backend.debt.service;

import com.backend.debt.model.dto.ClaimConfirmDto;
import com.backend.debt.model.query.ClaimConfirmQuery;

/** 债权确认服务接口 */
public interface IClaimConfirmService {

  /**
   * 添加债权确认信息
   *
   * @param claimFillingId 债权详情ID
   * @param query 债权确认信息
   * @return 添加的债权确认信息
   */
  ClaimConfirmDto addClaimConfirm(String claimFillingId, ClaimConfirmQuery query);

  /**
   * 更新债权确认信息
   *
   * @param claimConfirmId 债权确认ID
   * @param query 债权确认更新信息
   * @return ClaimConfirmDto
   */
  ClaimConfirmDto updateClaimConfirm(String claimConfirmId, ClaimConfirmQuery query);

  /**
   * 删除债权确认信息
   *
   * @param claimConfirmId 债权确认ID
   * @return 是否删除成功
   */
  boolean deleteClaimConfirm(String claimConfirmId);
}
