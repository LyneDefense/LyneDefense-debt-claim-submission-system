package com.backend.debt.model.dto;

import com.backend.debt.model.entity.ClaimConfirmEntity;
import com.backend.debt.model.entity.ClaimFillingEntity;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "债权填报详情DTO")
public class ClaimFillingDto {

  @ApiModelProperty(value = "主键ID", example = "1234567890abcdef")
  private String id;

  @ApiModelProperty(value = "关联的债权ID", example = "1234567890abcdef")
  private String claimId;

  /** 申报债权性质 */
  @ApiModelProperty(value = "申报债权性质", example = "普通债权")
  private String claimNature;

  /** 担保物明细 */
  @ApiModelProperty(value = "担保物明细", example = "房屋、土地等资产")
  private String collateralDetails;

  /** 申报本金金额 */
  @ApiModelProperty(value = "申报本金", example = "10000.00")
  private Double claimPrincipal;

  /** 申报利息金额 */
  @ApiModelProperty(value = "申报利息", example = "500.00")
  private Double claimInterest;

  /** 申报其他项目金额 */
  @ApiModelProperty(value = "申报其他金额", example = "200.00")
  private Double claimOther;

  /** 确认部分详情 */
  private ClaimConfirmDto confirmedDetail;

  /** 申报金额合计（自动计算字段） */
  @JsonProperty("total")
  public Double getTotal() {
    return addNullSafe(this.claimPrincipal, addNullSafe(this.claimInterest, this.claimOther));
  }

  /** 空值安全的加法运算，如果任一参数为null，视为0 */
  private Double addNullSafe(Double a, Double b) {
    return (a == null ? 0.0 : a) + (b == null ? 0.0 : b);
  }

  public static ClaimFillingDto of(ClaimFillingEntity entity, ClaimConfirmEntity confirmEntity) {
    ClaimFillingDto dto = new ClaimFillingDto();
    ClaimConfirmDto claimConfirmDto = ClaimConfirmDto.of(confirmEntity, entity);
    dto.setId(entity.getId());
    dto.setClaimId(entity.getClaimId());
    dto.setClaimNature(entity.getClaimNature());
    dto.setCollateralDetails(entity.getCollateralDetails());
    dto.setClaimPrincipal(entity.getClaimPrincipal());
    dto.setClaimInterest(entity.getClaimInterest());
    dto.setClaimOther(entity.getClaimOther());
    dto.setConfirmedDetail(claimConfirmDto);
    return dto;
  }
}
