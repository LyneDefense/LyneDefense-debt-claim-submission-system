package com.backend.debt.controller;

import com.backend.debt.model.Resp;
import com.backend.debt.model.dto.ClaimDetailDto;
import com.backend.debt.model.dto.ClaimSimpleDto;
import com.backend.debt.model.page.PageResult;
import com.backend.debt.model.query.ClaimQuery;
import com.backend.debt.model.query.ClaimSimplePageQuery;
import com.backend.debt.service.IClaimService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/** 债权申报控制器 */
@Tag(name = "债权申报管理", description = "债权申报基本信息相关接口")
@RestController
@RequestMapping("")
@Slf4j
public class ClaimController {

  @Resource private IClaimService claimService;

  @Operation(summary = "获取债权信息分页列表", description = "债权信息的简单字段展示集合")
  @PostMapping("/simple/page")
  public Resp<PageResult<ClaimSimpleDto>> getSimplePage(@RequestBody ClaimSimplePageQuery query) {
    PageResult<ClaimSimpleDto> pageResult = claimService.getSimplePage(query);
    return Resp.data(pageResult);
  }

  @Operation(summary = "添加债权申报", description = "提交债权申报信息，包括债权人、代理人和联系信息等")
  @PostMapping("/add")
  public Resp<String> addClaimItem(@Valid @RequestBody ClaimQuery query) {
    String claimId = claimService.addClaimItem(query);
    return Resp.data(claimId);
  }

  @Operation(summary = "删除债权申报", description = "删除债权申报信息及其关联的所有记录，包括债权人、债权申报详情、债权确认等")
  @DeleteMapping("/delete/{claim_id}")
  public Resp<Void> deleteClaimItem(@PathVariable(value = "claim_id") String claimId) {
    boolean success = claimService.deleteClaimItem(claimId);
    return success ? Resp.ok() : Resp.error(500, "删除债权申报失败");
  }

  @Operation(summary = "更新债权申报", description = "更新债权申报信息，包括债权基本信息、债权人信息、代理人和联系信息等")
  @PutMapping("/update/{claim_id}")
  public Resp<Void> updateClaimItem(
      @PathVariable(value = "claim_id") String claimId, @Valid @RequestBody ClaimQuery updateDto) {
    boolean success = claimService.updateClaimItem(claimId, updateDto);
    return success ? Resp.ok() : Resp.error(500, "更新债权申报失败");
  }

  @Operation(summary = "获取债权申报详情", description = "查询债权申报的完整信息，包括债权基本信息、债权人、申报详情和确认信息等")
  @GetMapping("/detail/{claim_id}")
  public Resp<ClaimDetailDto> getClaimDetail(@PathVariable(value = "claim_id") String claimId) {
    ClaimDetailDto detailDto = claimService.getClaimDetail(claimId);
    return Resp.data(detailDto);
  }
}
