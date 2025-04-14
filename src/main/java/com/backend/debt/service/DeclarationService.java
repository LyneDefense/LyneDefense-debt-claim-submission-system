package com.backend.debt.service;

import com.backend.debt.model.dto.DeclarationRegistrationDto;
import com.backend.debt.model.dto.DeclarationRegistrationSimpleDto;
import com.backend.debt.model.page.PageResult;
import com.backend.debt.model.query.DeclarationPageQuery;

/** 债权申报服务接口 */
public interface DeclarationService {

  /**
   * 分页查询债权申报和债权人信息
   *
   * @param query 查询参数，包含分页信息和过滤条件
   * @return 包含债权申报和债权人信息的分页结果
   */
  PageResult<DeclarationRegistrationSimpleDto> pageDeclarationWithCreditors(DeclarationPageQuery query);

  /**
   * 根据ID查询债权申报详情
   *
   * @param id 债权申报ID
   * @return 债权申报详情，包含关联的债权人、代理人、联系方式和申报详情等信息
   */
  DeclarationRegistrationDto getDeclarationById(String id);
}
