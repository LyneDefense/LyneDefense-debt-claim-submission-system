package com.backend.debt.model.dto;

import com.backend.debt.enums.IdTypeEnum;
import com.backend.debt.model.entity.CreditorEntity;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreditorDto {
  /** 债权人名称 */
  @NotEmpty private String name;

  /** 证件类型（如：统一社会信用代码/身份证） */
  @NotNull private IdTypeEnum idType;

  /** 有效身份证件号码 */
  @NotEmpty private String idNumber;

  public static List<CreditorDto> ofList(List<CreditorEntity> creditorEntities) {
    return creditorEntities.stream()
        .map(
            entity -> {
              CreditorDto dto = new CreditorDto();
              BeanUtils.copyProperties(entity, dto);
              return dto;
            })
        .collect(Collectors.toList());
  }
}
