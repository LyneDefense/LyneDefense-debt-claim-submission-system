package com.backend.debt.model.query;

import com.backend.debt.enums.ClaimType;
import com.backend.debt.model.dto.AgentDto;
import com.backend.debt.model.dto.ContactInfoDto;
import com.backend.debt.model.dto.CreditorDto;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.time.LocalDate;
import java.util.List;
import lombok.Data;

@Data
@Schema(description = "债权请求参数")
public class ClaimQuery {

  @Schema(description = "债权编号", example = "CLAIM-2023-001")
  private String claimNumber;

  @Schema(description = "登记人", example = "张三")
  private String registrar;

  @Schema(description = "申报日期", type = "string", example = "2023-01-01", format = "date")
  @JsonDeserialize(using = LocalDateDeserializer.class)
  @JsonSerialize(using = LocalDateSerializer.class)
  private LocalDate claimDate;

  @Schema(description = "申报形式", required = true)
  @NotEmpty
  private List<ClaimType> claimTypes;

  @Schema(description = "债权人信息列表")
  private List<CreditorDto> creditors;

  @Schema(description = "分配审核人员", example = "李四")
  private String auditor;

  @Schema(description = "债权归类", example = "普通债权")
  private String claimCategory;

  @Schema(description = "材料提交情况", example = "已提交全部材料")
  private String materialStatus;

  @Schema(description = "代理人信息")
  private AgentDto agent;

  @Schema(description = "收件信息")
  @Valid
  private ContactInfoDto creditorContactInfo;
}
