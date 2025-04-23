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

  private Double getTotal() {
    return this.principal + this.interest + this.other;
  }

  public static SuspendConfirmStatisticDto defaultEmpty() {
    return new SuspendConfirmStatisticDto(List.of(), 0.0, 0.0, 0.0, 0, "");
  }
}
