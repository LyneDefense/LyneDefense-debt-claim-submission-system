package com.backend.debt.model.dto;

import com.backend.debt.enums.ClaimType;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDate;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "债权详情DTO，包含债权申报的完整信息")
public class ClaimDetailDto {

  @ApiModelProperty(value = "债权ID", example = "1234567890abcdef")
  private String id;

  @ApiModelProperty(value = "债权编号", required = true, example = "CLAIM-2023-001")
  @NotNull
  @NotEmpty
  private String claimNumber;

  @ApiModelProperty(value = "登记人", required = true, example = "张三")
  @NotNull
  @NotEmpty
  private String registrar;

  @ApiModelProperty(value = "申报日期", required = true, example = "2023-01-01")
  @NotNull
  @JsonDeserialize(using = LocalDateDeserializer.class)
  @JsonSerialize(using = LocalDateSerializer.class)
  private LocalDate claimDate;

  @ApiModelProperty(value = "申报形式", required = true)
  @NotEmpty
  private List<ClaimType> claimTypes;

  @ApiModelProperty(value = "债权人信息列表")
  private List<CreditorDto> creditors;

  @ApiModelProperty(value = "分配审核人员", example = "李四")
  private String auditor;

  @ApiModelProperty(value = "债权归类", example = "普通债权")
  private String claimCategory;

  @ApiModelProperty(value = "材料提交情况", example = "已提交全部材料")
  private String materialStatus;

  @ApiModelProperty(value = "代理人信息")
  private AgentDto agent;

  @ApiModelProperty(value = "收件信息")
  @Valid
  private ContactInfoDto creditorContactInfo;

  @ApiModelProperty(value = "申报详情列表")
  private List<ClaimFillingDto> claimFillings;

  @ApiModelProperty(value = "债权确认信息列表")
  private List<ClaimConfirmDto> claimConfirms;

  @ApiModelProperty(value = "申报金额汇总")
  private DeclaredSummaryDto declaredSummary;
}
