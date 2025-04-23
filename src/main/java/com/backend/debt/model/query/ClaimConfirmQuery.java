package com.backend.debt.model.query;

import com.backend.debt.enums.ReviewStatus;
import com.backend.debt.model.entity.ClaimConfirmEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "债权确认信息请求参数")
public class ClaimConfirmQuery {

  /** 审计状态 */
  @NotNull(message = "审计状态不能为空")
  @ApiModelProperty(value = "审计状态", example = "CONFIRMED", required = true)
  private ReviewStatus reviewStatus;

  /** 确认本金 */
  @DecimalMin(value = "0", message = "确认本金不能小于0")
  @ApiModelProperty(value = "确认本金", example = "10000.00")
  private Double confirmedPrincipal;

  /** 确认利息 */
  @DecimalMin(value = "0", message = "确认利息不能小于0")
  @ApiModelProperty(value = "确认利息", example = "500.00")
  private Double confirmedInterest;

  /** 确认其他金额 */
  @DecimalMin(value = "0", message = "确认其他金额不能小于0")
  @ApiModelProperty(value = "确认其他金额", example = "200.00")
  private Double confirmedOther;

  /** 确认债权性质 */
  @ApiModelProperty(value = "确认债权性质", example = "普通债权")
  private String claimNature;

  /** 审查理由 */
  @ApiModelProperty(value = "审查理由", example = "债权材料齐全，符合申报要求")
  private String reviewReason;

  public ClaimConfirmEntity to(String claimFillingId) {
    return ClaimConfirmEntity.builder()
        .claimFillingId(claimFillingId)
        .reviewStatus(reviewStatus)
        .confirmedPrincipal(confirmedPrincipal)
        .confirmedInterest(confirmedInterest)
        .confirmedOther(confirmedOther)
        .claimNature(claimNature)
        .reviewReason(reviewReason)
        .build();
  }
}
