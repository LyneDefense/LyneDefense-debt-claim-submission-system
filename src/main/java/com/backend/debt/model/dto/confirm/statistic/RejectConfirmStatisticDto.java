package com.backend.debt.model.dto.confirm.statistic;

import com.backend.debt.enums.ReviewStatus;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RejectConfirmStatisticDto {

  /** 本金 */
  private Double principal;

  /** 利息 */
  private Double interest;

  /** 其他 */
  private Double other;

  /** 笔数 */
  private Integer count;

  /** 不予确认原因 */
  private String rejectReason;

  private Double getTotal() {
    return this.principal + this.interest + this.other;
  }

  public static RejectConfirmStatisticDto defaultEmpty() {
    return new RejectConfirmStatisticDto(0.0, 0.0, 0.0, 0, "");
  }
}
