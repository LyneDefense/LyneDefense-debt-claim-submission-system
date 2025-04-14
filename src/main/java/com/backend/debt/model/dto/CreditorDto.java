package com.backend.debt.model.dto;

import com.backend.debt.enums.IdType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreditorDto {
  /** 债权人名称 */
  @NotEmpty private String name;

  /** 证件类型（如：统一社会信用代码/身份证） */
  @NotNull private IdType idType;

  /** 有效身份证件号码 */
  @NotEmpty private String idNumber;
}
