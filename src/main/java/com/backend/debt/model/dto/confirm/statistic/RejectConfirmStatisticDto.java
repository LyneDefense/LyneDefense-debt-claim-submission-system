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
public class RejectConfirmStatisticDto extends BaseConfirmStatisticDto {

  /** 不予确认原因 */
  private String rejectReason;

  public RejectConfirmStatisticDto(
      List<ReviewStatus> reviewStatus,
      Double principal,
      Double interest,
      Double other,
      Integer count,
      String rejectReason) {
    super(reviewStatus, principal, interest, other, count);
    this.rejectReason = rejectReason;
  }
}
