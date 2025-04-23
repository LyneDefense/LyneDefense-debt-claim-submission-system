package com.backend.debt.model.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "债权详情请求参数")
public class ClaimFillingQuery {

  @ApiModelProperty(example = "普通债权")
  private String claimNature;

  @ApiModelProperty(example = "房屋、土地等资产")
  private String collateralDetails;

  @ApiModelProperty(example = "10000.00")
  @DecimalMin(value = "0", message = "申报本金不能小于0")
  private Double claimPrincipal;

  @ApiModelProperty(example = "500.00")
  @DecimalMin(value = "0", message = "申报利息不能小于0")
  private Double claimInterest;

  @ApiModelProperty(example = "200.00")
  @DecimalMin(value = "0", message = "申报其他金额不能小于0")
  private Double claimOther;
}
