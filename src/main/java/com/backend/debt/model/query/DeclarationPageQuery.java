package com.backend.debt.model.query;

import com.backend.debt.model.page.PageParam;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** 债权申报分页查询参数 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "债权申报分页查询参数")
public class DeclarationPageQuery extends PageParam {

  @Schema(description = "债权编号")
  private String claimNumber;

  @Schema(description = "登记人")
  private String registrar;

  @Schema(description = "申报日期开始", type = "string", example = "2023-01-01")
  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate declarationDateStart;

  @Schema(description = "申报日期结束", type = "string", example = "2023-12-31")
  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate declarationDateEnd;

  @Schema(description = "债权人名称")
  private String creditorName;
}
