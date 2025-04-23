package com.backend.debt.model.query;

import com.backend.debt.model.page.PageParam;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDate;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** 债权申报分页查询参数 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "债权申报分页查询参数")
public class ClaimSimplePageQuery extends PageParam {

  @ApiModelProperty(value = "债权编号")
  private String claimNumber;

  @ApiModelProperty(value = "登记人")
  private String registrar;

  @ApiModelProperty(value = "申报日期开始", example = "2023-01-01")
  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate startClaimDate;

  @ApiModelProperty(value = "申报日期结束", example = "2023-12-31")
  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate endClaimDate;
}
