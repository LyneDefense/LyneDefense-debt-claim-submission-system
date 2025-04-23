package com.backend.debt.model.dto.confirm.statistic;

import com.backend.debt.enums.ReviewStatus;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
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

  @JsonProperty("total")
  public Double getTotal() {
    return addNullSafe(this.principal, addNullSafe(this.interest, this.other));
  }

  /**
   * 空值安全的加法运算，如果任一参数为null，视为0
   */
  private Double addNullSafe(Double a, Double b) {
    return (a == null ? 0.0 : a) + (b == null ? 0.0 : b);
  }

  public static RejectConfirmStatisticDto defaultEmpty() {
    return new RejectConfirmStatisticDto(0.0, 0.0, 0.0, 0, "");
  }
}
