package com.backend.debt.model.dto.confirm.statistic;

import com.backend.debt.enums.ReviewStatus;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmedStatisticDto extends BaseConfirmStatisticDto {

  /** 担保物明细 */
  private String collateralDetails;

  /** 确认性质 */
  private String confirmNature;

  /** 削减金额 */
  private Double deductionAmount;

  public ConfirmedStatisticDto(
      List<ReviewStatus> reviewStatus,
      Double principal,
      Double interest,
      Double other,
      Integer count,
      String collateralDetails,
      String confirmNature) {
    super(reviewStatus, principal, interest, other, count);
    this.collateralDetails = collateralDetails;
    this.confirmNature = confirmNature;
  }
}
