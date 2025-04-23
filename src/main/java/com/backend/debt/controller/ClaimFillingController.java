package com.backend.debt.controller;

import com.backend.debt.model.Resp;
import com.backend.debt.model.dto.ClaimDetailDto;
import com.backend.debt.model.query.ClaimFillingQuery;
import com.backend.debt.service.IClaimFillingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javax.annotation.Resource;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Api(value = "债权申报金额管理", tags = "债权申报金额相关接口")
@RestController
@RequestMapping("/filling")
@Slf4j
public class ClaimFillingController {

  @Resource private IClaimFillingService claimFillingService;

  @ApiOperation(value = "更新债权申报金额信息", notes = "更新特定债权申报的详情信息，包括债权性质、担保物明细和申报金额等")
  @PutMapping("/update/{claim_filling_id}")
  public Resp<ClaimDetailDto> updateClaimFilling(
      @PathVariable(value = "claim_filling_id") String claimFillingId,
      @Valid @RequestBody ClaimFillingQuery query) {
    ClaimDetailDto detailDto = claimFillingService.updateClaimFilling(claimFillingId, query);
    return Resp.data(detailDto);
  }

  @ApiOperation(value = "添加债权申报金额信息", notes = "为特定债权申报添加详情信息，包括债权性质、担保物明细和申报金额等")
  @PostMapping("/add/{claim_id}")
  public Resp<Void> addClaimFilling(
      @PathVariable(value = "claim_id") String claimId,
      @Valid @RequestBody ClaimFillingQuery query) {
    boolean success = claimFillingService.addClaimFilling(claimId, query);
    return success ? Resp.ok() : Resp.error(500, "添加债权申报金额失败");
  }

  @ApiOperation(value = "删除债权申报金额信息", notes = "删除特定债权申报金额信息")
  @DeleteMapping("/delete/{claim_filling_id}")
  public Resp<Void> deleteClaimFilling(
      @PathVariable(value = "claim_filling_id") String claimFillingId) {
    log.info("删除债权申报金额信息，ID：{}", claimFillingId);
    boolean success = claimFillingService.deleteClaimFilling(claimFillingId);
    return success ? Resp.ok() : Resp.error(500, "删除债权申报金额失败");
  }
}
