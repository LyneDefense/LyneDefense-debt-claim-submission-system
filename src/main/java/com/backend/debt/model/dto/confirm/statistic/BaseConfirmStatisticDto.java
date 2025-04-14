package com.backend.debt.model.dto.confirm.statistic;

import com.backend.debt.enums.ReviewStatus;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "review_status",
    visible = true)
@JsonSubTypes(
    value = {
      @JsonSubTypes.Type(value = RejectConfirmStatisticDto.class, name = "CONFIRM_REJECT"),
      @JsonSubTypes.Type(value = SuspendConfirmStatisticDto.class, name = "CONFIRM_SUSPEND"),
      @JsonSubTypes.Type(value = ConfirmedStatisticDto.class, name = "CONFIRMED_ALL"),
    })
public class BaseConfirmStatisticDto {

  /** 确认类型 */
  private ReviewStatus reviewStatus;

  /** 本金 */
  private Double principal;

  /** 利息 */
  private Double interest;

  /** 其他 */
  private Double other;

  /** 笔数 */
  private Double count;

  private Double getTotal() {
    return this.principal + this.interest + this.other;
  }
}
