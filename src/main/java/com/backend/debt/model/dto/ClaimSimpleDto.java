package com.backend.debt.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDate;
import java.util.List;
import lombok.Data;

/**
 * 债权申报与债权人信息DTO
 *
 * <p>包含债权申报基本信息和关联的债权人列表
 */
@Data
@ApiModel(value = "债权申报与债权人信息")
public class ClaimSimpleDto {

  /** 主键ID */
  @ApiModelProperty(value = "主键ID")
  private String id;

  /** 债权编号 */
  @ApiModelProperty(value = "债权编号")
  private String claimNumber;

  /** 登记人 */
  @ApiModelProperty(value = "登记人")
  private String registrar;

  /** 申报日期 */
  @ApiModelProperty(value = "申报日期", example = "2023-01-01")
  @JsonDeserialize(using = LocalDateDeserializer.class)
  @JsonSerialize(using = LocalDateSerializer.class)
  private LocalDate claimDate;

  /** 分配的审核人员 */
  @ApiModelProperty(value = "分配的审核人员")
  private String auditor;

  /** 债权归类 */
  @ApiModelProperty(value = "债权归类")
  private String claimCategory;

  /** 材料提交情况 */
  @ApiModelProperty(value = "材料提交情况")
  private String materialStatus;

  /** 债权人列表 */
  @ApiModelProperty(value = "债权人列表")
  private List<CreditorDto> creditors;
}
