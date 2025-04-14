package com.backend.debt.controller;

import com.backend.debt.model.Resp;
import com.backend.debt.model.dto.DeclarationRegistrationDto;
import com.backend.debt.model.dto.DeclarationRegistrationSimpleDto;
import com.backend.debt.model.page.PageResult;
import com.backend.debt.model.query.DeclarationPageQuery;
import com.backend.debt.service.DeclarationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/** 债权申报控制器 */
@Tag(name = "债权申报管理", description = "债权申报相关接口")
@RestController
@RequestMapping("/api/declarations")
@RequiredArgsConstructor
public class DeclarationController {

  private final DeclarationService declarationService;

  /**
   * 分页查询债权申报和债权人信息（POST请求，支持复杂查询条件）
   *
   * @param query 查询参数，包含分页信息和过滤条件
   * @return 包含债权申报和债权人信息的分页结果
   */
  @Operation(summary = "分页查询债权申报和债权人信息（完整版）")
  @PostMapping("/page-with-creditors")
  public Resp<PageResult<DeclarationRegistrationSimpleDto>> pageDeclarationWithCreditors(
      @RequestBody DeclarationPageQuery query) {

    // 调用服务获取分页结果
    PageResult<DeclarationRegistrationSimpleDto> pageResult =
        declarationService.pageDeclarationWithCreditors(query);

    // 返回成功响应
    return Resp.data(pageResult);
  }

  /**
   * 根据ID查询债权申报详情
   *
   * @param id 债权申报ID
   * @return 债权申报详情，包含关联的债权人、代理人、联系方式和申报详情等信息
   */
  @Operation(summary = "根据ID查询债权申报详情")
  @GetMapping("/detail/{id}")
  public Resp<DeclarationRegistrationDto> getDeclarationById(
      @Parameter(description = "债权申报ID", required = true) @PathVariable String id) {

    // 调用服务获取债权申报详情
    DeclarationRegistrationDto dto = declarationService.getDeclarationById(id);

    // 返回成功响应
    return Resp.data(dto);
  }
}
