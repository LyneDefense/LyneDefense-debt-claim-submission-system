package com.backend.debt.model.dto;

import com.backend.debt.model.entity.ClaimConfirmEntity;
import com.backend.debt.model.entity.ClaimFillingEntity;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "债权填报详情DTO")
public class ClaimFillingDto {

  @Schema(description = "主键ID", example = "1234567890abcdef")
  private String id;

  @Schema(description = "关联的债权ID", example = "1234567890abcdef")
  private String claimId;

  /** 申报债权性质 */
  @Schema(description = "申报债权性质", example = "普通债权")
  private String claimNature;

  /** 担保物明细 */
  @Schema(description = "担保物明细", example = "房屋、土地等资产")
  private String collateralDetails;

  /** 申报本金金额 */
  @Schema(description = "申报本金", example = "10000.00")
  private Double claimPrincipal;

  /** 申报利息金额 */
  @Schema(description = "申报利息", example = "500.00")
  private Double claimInterest;

  /** 申报其他项目金额 */
  @Schema(description = "申报其他金额", example = "200.00")
  private Double claimOther;

  /** 确认部分详情 */
  private ClaimConfirmDto confirmedDetail;

  /** 申报金额合计（自动计算字段） */
  @JsonValue
  public Double getDeclaredTotal() {
    return this.claimPrincipal + this.claimInterest + this.claimOther;
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
