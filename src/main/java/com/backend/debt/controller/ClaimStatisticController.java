package com.backend.debt.controller;

import com.backend.debt.model.Resp;
import com.backend.debt.model.dto.confirm.statistic.ClaimConfirmStatisticDto;
import com.backend.debt.service.IClaimStatisticService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@Tag(name = "债权统计管理", description = "债权统计管理相关接口")
@RestController
@RequestMapping("/statistic")
public class ClaimStatisticController {

  @Resource private IClaimStatisticService claimStatisticService;

  @Operation(summary = "获取当前债权的统计信息", description = "获取当前债权的统计信息,包括确认统计，暂缓统计，不予确认统计和统计状态等")
  @GetMapping("/{claim_id}")
  public Resp<ClaimConfirmStatisticDto> getClaimStatistic(
      @PathVariable(value = "claim_id") String claimId) {
    return Resp.data(claimStatisticService.calculateConfirmedStatistic(claimId));
  }
}
