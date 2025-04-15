package com.backend.debt.model.dto;

import com.backend.debt.enums.DeclarationType;
import com.backend.debt.model.dto.confirm.statistic.BaseConfirmStatisticDto;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeclarationRegistrationDto {

  private String id;

  /** 债权编号 */
  @NotNull @NotEmpty private String claimNumber;

  /** 登记人 */
  @NotNull @NotEmpty private String registrar;

  /** 申报日期（格式：yyyy-MM-dd） */
  @NotNull
  @JsonDeserialize(using = LocalDateDeserializer.class)
  @JsonSerialize(using = LocalDateSerializer.class)
  private LocalDate declarationDate;

  /** 申报形式 */
  @NotEmpty private List<DeclarationType> declarationTypes;

  /** 债权人信息 */
  private List<CreditorDto> creditors;

  /** 分配审核人员 */
  private String auditor;

  /** 债权归类(目前用String表示了) */
  private String claimCategory;

  /** 材料提交情况 (目前用String表示了) */
  private String materialStatus;

  /** 代理人信息 */
  private AgentDto agent;

  /** 收件信息 */
  @Valid private ContactInfoDto creditorContactInfo;

  /** 申报详情 */
  private List<ClaimDetailDto> claimDetails;

  /** ----------------------以下为统计信息--------------------- */
  private List<BaseConfirmStatisticDto> confirmStatistics;

  private DeclaredSummaryDto declaredSummary;
}
