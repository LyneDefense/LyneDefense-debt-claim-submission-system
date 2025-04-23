package com.backend.debt.model.dto.confirm.statistic;

import com.backend.debt.enums.ReviewStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SuspendConfirmStatisticDto {

  /** 确认类型 */
  private List<ReviewStatus> reviewStatus;

  /** 本金 */
  private Double principal;

  /** 利息 */
  private Double interest;

  /** 其他 */
  private Double other;

  /** 笔数 */
  private Integer count;

  /** 暂缓确认性质 */
  private String suspendNature;

  @JsonProperty("total")
  public Double getTotal() {
    return addNullSafe(this.principal, addNullSafe(this.interest, this.other));
  }

  /** 空值安全的加法运算，如果任一参数为null，视为0 */
  private Double addNullSafe(Double a, Double b) {
    return (a == null ? 0.0 : a) + (b == null ? 0.0 : b);
  }

  public static SuspendConfirmStatisticDto defaultEmpty() {
    return new SuspendConfirmStatisticDto(List.of(), 0.0, 0.0, 0.0, 0, "");
  }
}
