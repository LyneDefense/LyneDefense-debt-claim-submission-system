package com.backend.debt.model.dto;

import com.backend.debt.enums.ReviewStatus;
import com.backend.debt.model.entity.ClaimConfirmEntity;
import com.backend.debt.model.entity.ClaimFillingEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** 债权审查确认情况 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "债权确认信息DTO")
public class ClaimConfirmDto {

  @Schema(description = "主键ID", example = "1234567890abcdef")
  private String id;

  @Schema(description = "关联的债权填报ID", example = "1234567890abcdef")
  private String claimFillingId;

  /** 审计状态 */
  @Schema(description = "审计状态", example = "CONFIRMED")
  private ReviewStatus reviewStatus;

  /** 确认本金 */
  @Schema(description = "确认本金", example = "10000.00")
  private Double confirmedPrincipal;

  /** 确认利息 */
  @Schema(description = "确认利息", example = "500.00")
  private Double confirmedInterest;

  /** 确认其他金额 */
  @Schema(description = "确认其他金额", example = "200.00")
  private Double confirmedOther;

  /** 确认债权性质 */
  @Schema(description = "确认债权性质", example = "普通债权")
  private String claimNature;

  /** 确认时削减金额 */
  @Schema(description = "确认时削减金额", example = "100.00")
  private Double deductionAmount;

  /** 审查理由 */
  @Schema(description = "审查理由", example = "债权材料齐全，符合申报要求")
  private String reviewReason;

  public Double getConfirmedTotal() {
    double principal = confirmedPrincipal != null ? confirmedPrincipal : 0;
    double interest = confirmedInterest != null ? confirmedInterest : 0;
    double other = confirmedOther != null ? confirmedOther : 0;
    return principal + interest + other;
  }

  public static ClaimConfirmDto defaultEmpty() {
    return new ClaimConfirmDto(
        null, null, ReviewStatus.NOT_CONFIRMED, 0.0, 0.0, 0.0, null, 0.0, null);
  }

  public static ClaimConfirmDto of(ClaimConfirmEntity entity, ClaimFillingEntity fillingEntity) {
    if (fillingEntity == null) {
      return null;
    }
    if (entity == null) {
      return defaultEmpty();
    }
    ClaimConfirmDto dto = new ClaimConfirmDto();
    dto.setId(entity.getId());
    dto.setClaimFillingId(entity.getClaimFillingId());
    dto.setReviewStatus(entity.getReviewStatus());
    dto.setConfirmedPrincipal(entity.getConfirmedPrincipal());
    dto.setConfirmedInterest(entity.getConfirmedInterest());
    dto.setConfirmedOther(entity.getConfirmedOther());
    dto.setClaimNature(entity.getClaimNature());
    dto.setReviewReason(entity.getReviewReason());

    // 确认削减金额.只有部分确认和拒绝确认的时候，才会有削减金额。否则削减金额为0
    dto.setDeductionAmount(0.0);
    if (entity.getReviewStatus() == ReviewStatus.CONFIRM_PART
        || entity.getReviewStatus() == ReviewStatus.CONFIRM_REJECT) {
      Double claimTotal =
          fillingEntity.getClaimPrincipal()
              + fillingEntity.getClaimInterest()
              + fillingEntity.getClaimOther();
      Double confirmTotal =
          entity.getConfirmedPrincipal()
              + entity.getConfirmedInterest()
              + entity.getConfirmedOther();
      dto.setDeductionAmount(claimTotal + confirmTotal);
    }
    return dto;
  }
}
