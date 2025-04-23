package com.backend.debt.model.query;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "债权详情请求参数")
public class ClaimFillingQuery {

  @Schema(description = "申报债权性质", example = "普通债权")
  private String claimNature;

  @Schema(description = "担保物明细", example = "房屋、土地等资产")
  private String collateralDetails;

  @Schema(description = "申报本金", example = "10000.00")
  @DecimalMin(value = "0", message = "申报本金不能小于0")
  private Double claimPrincipal;

  @Schema(description = "申报利息", example = "500.00")
  @DecimalMin(value = "0", message = "申报利息不能小于0")
  private Double claimInterest;

  @Schema(description = "申报其他金额", example = "200.00")
  @DecimalMin(value = "0", message = "申报其他金额不能小于0")
  private Double claimOther;
}
