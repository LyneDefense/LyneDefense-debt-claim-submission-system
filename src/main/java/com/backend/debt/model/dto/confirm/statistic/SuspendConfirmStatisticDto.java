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
public class SuspendConfirmStatisticDto extends BaseConfirmStatisticDto {

  /** 暂缓确认性质 */
  private String suspendNature;

  public SuspendConfirmStatisticDto(
      List<ReviewStatus> reviewStatus,
      Double principal,
      Double interest,
      Double other,
      Integer count,
      String suspendNature) {
    super(reviewStatus, principal, interest, other, count);
    this.suspendNature = suspendNature;
  }
}
