package com.backend.debt.model.query;

import com.backend.debt.enums.ClaimType;
import com.backend.debt.model.dto.AgentDto;
import com.backend.debt.model.dto.ContactInfoDto;
import com.backend.debt.model.entity.ClaimEntity;
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
import lombok.Data;

@Data
@ApiModel(value = "债权请求参数")
public class ClaimQuery {

  @ApiModelProperty(value = "债权编号", example = "CLAIM-2023-001")
  private String claimNumber;

  @ApiModelProperty(value = "登记人", example = "张三")
  private String registrar;

  @ApiModelProperty(value = "申报日期", example = "2023-01-01")
  @JsonDeserialize(using = LocalDateDeserializer.class)
  @JsonSerialize(using = LocalDateSerializer.class)
  private LocalDate claimDate;

  @ApiModelProperty(value = "申报形式", required = true)
  private List<ClaimType> claimTypes;

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

  public ClaimEntity toClaimEntity() {
    ClaimEntity entity =
        ClaimEntity.builder()
            .claimNumber(this.claimNumber)
            .registrar(this.registrar)
            .claimDate(this.claimDate)
            .claimTypes(ClaimType.toClaimTypeCodes(this.claimTypes))
            .auditor(this.auditor)
            .claimCategory(this.claimCategory)
            .materialStatus(this.materialStatus)
            .build();
    if (this.agent != null) {
      entity.setAgentName(this.agent.getName());
      entity.setAgentPosition(this.agent.getPosition());
      entity.setAgentPhone(this.agent.getPhone());
      entity.setHasVotingRight(this.agent.getHasVotingRight());
    }
    if (this.creditorContactInfo != null) {
      entity.setRecipient(this.creditorContactInfo.getRecipient());
      entity.setContactPhone(this.creditorContactInfo.getContactPhone());
      entity.setMailingAddress(this.creditorContactInfo.getMailingAddress());
      entity.setEmail(this.creditorContactInfo.getEmail());
    }
    return entity;
  }
}
