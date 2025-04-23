package com.backend.debt.service;

import com.backend.debt.model.dto.CreditorDto;
import java.util.List;
import java.util.Map;

/** 债权人信息服务接口 */
public interface IClaimCreditorService {

  /**
   * 根据债权ID获取债权人列表
   *
   * @param claimId 债权ID
   * @return 债权人列表
   */
  List<CreditorDto> getCreditorsByClaimId(String claimId);
  
  /**
   * 根据多个债权ID批量获取债权人列表
   *
   * @param claimIds 债权ID列表
   * @return Map<债权ID, 债权人列表>
   */
  Map<String, List<CreditorDto>> getCreditorsByClaimIds(List<String> claimIds);

  /**
   * 添加债权人信息
   *
   * @param claimId 债权ID
   * @param creditorDtos 债权人信息列表
   * @return 添加的数量
   */
  int addCreditors(String claimId, List<CreditorDto> creditorDtos);

  /**
   * 更新债权人信息
   *
   * @param creditorId 债权人ID
   * @param creditorDto 债权人更新信息
   * @return 是否更新成功
   */
  boolean updateCreditor(String creditorId, CreditorDto creditorDto);

  /**
   * 删除债权人信息
   *
   * @param creditorId 债权人ID
   * @return 是否删除成功
   */
  boolean deleteCreditor(String creditorId);

  /**
   * 批量删除债权人信息
   *
   * @param creditorIds 债权人ID列表
   * @return 删除的数量
   */
  int batchDeleteCreditors(List<String> creditorIds);

  /**
   * 根据债权ID删除所有关联的债权人信息
   *
   * @param claimId 债权ID
   * @return 删除的数量
   */
  int deleteCreditorsByClaimId(String claimId);

  /**
   * 获取债权人详情
   *
   * @param creditorId 债权人ID
   * @return 债权人详情
   */
  CreditorDto getCreditorById(String creditorId);
}
