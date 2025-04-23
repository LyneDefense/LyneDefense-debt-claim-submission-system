package com.backend.debt.controller;

import com.backend.debt.model.Resp;
import com.backend.debt.model.dto.CreditorDto;
import com.backend.debt.model.query.CreditorAddQuery;
import com.backend.debt.service.IClaimCreditorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import javax.annotation.Resource;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/** 债权人信息控制器 */
@Api(value = "债权人管理", tags = "债权人信息相关接口")
@RestController
@RequestMapping("/creditor")
@Slf4j
public class CreditorController {

  @Resource private IClaimCreditorService claimCreditorService;

  @ApiOperation(value = "获取债权关联的债权人列表", notes = "根据债权ID获取关联的所有债权人信息")
  @GetMapping("/list/{claim_id}")
  public Resp<List<CreditorDto>> getCreditorsByClaimId(@PathVariable("claim_id") String claimId) {
    List<CreditorDto> creditors = claimCreditorService.getCreditorsByClaimId(claimId);
    return Resp.data(creditors);
  }

  @ApiOperation(value = "获取债权人详情", notes = "根据债权人ID获取债权人详细信息")
  @GetMapping("/get/{creditor_id}")
  public Resp<CreditorDto> getCreditorDetail(@PathVariable("creditor_id") String creditorId) {
    CreditorDto creditor = claimCreditorService.getCreditorById(creditorId);
    return Resp.data(creditor);
  }

  @ApiOperation(value = "添加债权人", notes = "为指定债权添加一个或多个债权人")
  @PostMapping("/add/{claim_id}")
  public Resp<Integer> addCreditors(
      @PathVariable("claim_id") String claimId, @Valid @RequestBody CreditorAddQuery query) {
    int count = claimCreditorService.addCreditors(claimId, query.getCreditors());
    return Resp.data(count);
  }

  @ApiOperation(value = "更新债权人", notes = "更新指定债权人的信息")
  @PutMapping("/update/{creditor_id}")
  public Resp<Void> updateCreditor(
      @PathVariable("creditor_id") String creditorId, @Valid @RequestBody CreditorDto creditorDto) {
    boolean success = claimCreditorService.updateCreditor(creditorId, creditorDto);
    return success ? Resp.ok() : Resp.error(500, "更新债权人失败");
  }

  @ApiOperation(value = "删除债权人", notes = "删除指定的债权人信息")
  @DeleteMapping("/delete/{creditor_id}")
  public Resp<Void> deleteCreditor(@PathVariable("creditor_id") String creditorId) {
    boolean success = claimCreditorService.deleteCreditor(creditorId);
    return success ? Resp.ok() : Resp.error(500, "删除债权人失败");
  }
}
