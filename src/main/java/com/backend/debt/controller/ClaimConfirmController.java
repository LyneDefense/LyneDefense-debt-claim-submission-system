package com.backend.debt.controller;

import com.backend.debt.model.Resp;
import com.backend.debt.model.dto.ClaimConfirmDto;
import com.backend.debt.model.query.ClaimConfirmQuery;
import com.backend.debt.service.IClaimConfirmService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "债权确认管理", description = "债权确认相关接口")
@RestController
@RequestMapping("/confirm")
public class ClaimConfirmController {

  @Resource private IClaimConfirmService claimConfirmService;

  @Operation(summary = "添加债权确认信息", description = "为特定债权申报金额添加确认信息，包括确认金额、债权性质和审查意见等")
  @PostMapping("/add/{claim_filling_id}")
  public Resp<ClaimConfirmDto> addClaimConfirm(
      @PathVariable(value = "claim_filling_id") String claimFillingId,
      @Valid @RequestBody ClaimConfirmQuery query) {
    ClaimConfirmDto confirmDto = claimConfirmService.addClaimConfirm(claimFillingId, query);
    return Resp.data(confirmDto);
  }

  @Operation(summary = "更新债权确认信息", description = "更新特定债权确认信息，包括确认金额、债权性质和审查意见等")
  @PutMapping("/update/{claim_confirm_id}")
  public Resp<ClaimConfirmDto> updateClaimConfirm(
      @PathVariable(value = "claim_confirm_id") String claimConfirmId,
      @Valid @RequestBody ClaimConfirmQuery query) {
    return Resp.data(claimConfirmService.updateClaimConfirm(claimConfirmId, query));
  }

  @Operation(summary = "删除债权确认信息", description = "删除特定债权确认信息")
  @DeleteMapping("/delete/{claim_confirm_id}")
  public Resp<Void> deleteClaimConfirm(
      @PathVariable(value = "claim_confirm_id") String claimConfirmId) {
    boolean success = claimConfirmService.deleteClaimConfirm(claimConfirmId);
    return success ? Resp.ok() : Resp.error(500, "删除债权确认信息失败");
  }
}
