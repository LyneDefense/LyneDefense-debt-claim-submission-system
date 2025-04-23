package com.backend.debt.service;

import com.backend.debt.model.dto.ClaimDetailDto;
import com.backend.debt.model.dto.ClaimFillingDto;
import com.backend.debt.model.entity.ClaimFillingEntity;
import com.backend.debt.model.query.ClaimFillingQuery;
import java.util.List;

/** 债权申报金额服务接口 */
public interface IClaimFillingService {

  /**
   * 更新债权申报金额信息
   *
   * @param claimFillingId 债权申报金额ID
   * @param query 债权申报金额更新信息
   * @return 更新后的债权申报金额详情
   */
  ClaimDetailDto updateClaimFilling(String claimFillingId, ClaimFillingQuery query);

  /**
   * 添加债权申报金额信息
   *
   * @param claimId 债权申报金额ID
   * @param query 债权申报金额信息
   * @return 是否添加成功
   */
  boolean addClaimFilling(String claimId, ClaimFillingQuery query);

  /**
   * 删除债权申报金额信息
   *
   * @param claimFillingId 债权申报金额ID
   * @return 是否删除成功
   */
  boolean deleteClaimFilling(String claimFillingId);

  /**
   * 校验并获取债权申报金额信息
   *
   * @param claimFillingId 债权详情ID
   * @return ClaimFillingEntity
   */
  ClaimFillingEntity validateAndGet(String claimFillingId);

  /**
   * 根据债权ID获取债权申报金额信息以及确认情况
   * @param claimId claimId
   * @return List<ClaimFillingDto>
   */
  List<ClaimFillingDto> getClaimFillingByClaimId(String claimId);
}
