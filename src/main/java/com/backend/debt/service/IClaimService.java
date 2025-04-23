package com.backend.debt.service;

import com.backend.debt.model.dto.ClaimDetailDto;
import com.backend.debt.model.dto.ClaimSimpleDto;
import com.backend.debt.model.entity.ClaimEntity;
import com.backend.debt.model.page.PageResult;
import com.backend.debt.model.query.ClaimQuery;
import com.backend.debt.model.query.ClaimSimplePageQuery;

/** 债权申报服务接口 */
public interface IClaimService {

  /**
   * 分页查询债权申报简要信息
   *
   * @param query 分页查询参数
   * @return 分页结果
   */
  PageResult<ClaimSimpleDto> getSimplePage(ClaimSimplePageQuery query);

  /**
   * 添加债权申报信息
   *
   * @param addDto 债权申报信息
   * @return 新增债权申报的ID
   */
  String addClaimItem(ClaimQuery addDto);

  /**
   * 删除债权申报信息 同时删除相关联的记录（债权人信息等）
   *
   * @param claimId 债权申报ID
   * @return 是否删除成功
   */
  boolean deleteClaimItem(String claimId);

  /**
   * 更新债权申报信息
   *
   * @param claimId 债权申报ID
   * @param updateDto 债权申报更新信息
   * @return 是否更新成功
   */
  boolean updateClaimItem(String claimId, ClaimQuery updateDto);

  /**
   * 获取债权申报详情
   *
   * @param claimId 债权申报ID
   * @return 债权申报详情
   */
  ClaimDetailDto getClaimDetail(String claimId);

  /**
   * 检验和获取债权申报Item
   * @param claimId claimId
   * @return ClaimEntity
   */
  ClaimEntity validateAndGet(String claimId);
}
