package com.backend.debt.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "债权申报金额汇总DTO")
public class DeclaredSummaryDto {

  /** 申报本金合计 */
  @Schema(description = "本金总额", example = "50000.00")
  private Double totalPrincipal;

  /** 申报利息合计 */
  @Schema(description = "利息总额", example = "2500.00")
  private Double totalInterest;

  /** 其他项目金额合计 */
  @Schema(description = "其他金额总额", example = "1000.00")
  private Double totalOther;

  /** 笔数 */
  private Integer count;

  /** 担保物明细 */
  private String collateralDetails;

  /** 债权性质 */
  private String claimNature;

  /** 申报总金额（自动计算字段） */
  @Schema(description = "总计金额", example = "53500.00")
  private Double total;
}
