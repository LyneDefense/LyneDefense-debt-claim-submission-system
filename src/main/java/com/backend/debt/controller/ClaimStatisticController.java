package com.backend.debt.controller;

import com.backend.debt.model.Resp;
import com.backend.debt.model.dto.confirm.statistic.ClaimConfirmStatisticDto;
import com.backend.debt.service.IClaimStatisticService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javax.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@Api(value = "债权统计管理", tags = "债权统计管理相关接口")
@RestController
@RequestMapping("/statistic")
public class ClaimStatisticController {

  @Resource private IClaimStatisticService claimStatisticService;

  @ApiOperation(value = "获取当前债权的统计信息", notes = "获取当前债权的统计信息,包括确认统计，暂缓统计，不予确认统计和统计状态等")
  @GetMapping("/{claim_id}")
  public Resp<ClaimConfirmStatisticDto> getClaimStatistic(
      @PathVariable(value = "claim_id") String claimId) {
    return Resp.data(claimStatisticService.calculateConfirmedStatistic(claimId));
  }
}
