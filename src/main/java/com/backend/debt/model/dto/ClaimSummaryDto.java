package com.backend.debt.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "债权申报金额汇总DTO")
public class ClaimSummaryDto {

  /** 申报本金合计 */
  @ApiModelProperty(value = "本金总额", example = "50000.00")
  private Double totalPrincipal;

  /** 申报利息合计 */
  @ApiModelProperty(value = "利息总额", example = "2500.00")
  private Double totalInterest;

  /** 其他项目金额合计 */
  @ApiModelProperty(value = "其他金额总额", example = "1000.00")
  private Double totalOther;

  /** 笔数 */
  private Integer count;

  /** 债权性质 */
  private String claimNature;

  /** 申报总金额（自动计算字段） */
  @ApiModelProperty(value = "总计金额", example = "53500.00")
  public Double getTotal() {
    return this.totalPrincipal + this.totalInterest + this.totalOther;
  }
}
