package com.backend.debt.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;
import lombok.Data;

/**
 * 债权申报与债权人信息DTO
 *
 * <p>包含债权申报基本信息和关联的债权人列表
 */
@Data
@Schema(description = "债权申报与债权人信息")
public class DeclarationRegistrationSimpleDto {

  /** 主键ID */
  @Schema(description = "主键ID")
  private String id;

  /** 债权编号 */
  @Schema(description = "债权编号")
  private String claimNumber;

  /** 登记人 */
  @Schema(description = "登记人")
  private String registrar;

  /** 申报日期 */
  @Schema(description = "申报日期", type = "string", example = "2023-01-01")
  @JsonDeserialize(using = LocalDateDeserializer.class)
  @JsonSerialize(using = LocalDateSerializer.class)
  private LocalDate declarationDate;

  /** 分配的审核人员 */
  @Schema(description = "分配的审核人员")
  private String auditor;

  /** 债权归类 */
  @Schema(description = "债权归类")
  private String claimCategory;

  /** 材料提交情况 */
  @Schema(description = "材料提交情况")
  private String materialStatus;

  /** 创建时间 */
  @Schema(description = "创建时间")
  private String createTime;

  /** 债权人列表 */
  @Schema(description = "债权人列表")
  private List<CreditorDto> creditors;
}
