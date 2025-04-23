package com.backend.debt.model.dto;

import com.backend.debt.enums.IdTypeEnum;
import com.backend.debt.model.entity.CreditorEntity;
import java.util.List;
import java.util.stream.Collectors;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreditorDto {

  private String id;

  /** 债权人名称 */
  private String name;

  /** 证件类型（如：统一社会信用代码/身份证） */
  private IdTypeEnum idType;

  /** 有效身份证件号码 */
  private String idNumber;

  public static List<CreditorDto> ofList(List<CreditorEntity> creditorEntities) {
    return creditorEntities.stream()
        .map(
            entity -> {
              return CreditorDto.builder()
                  .id(entity.getId())
                  .name(entity.getName())
                  .idType(entity.getIdType())
                  .idNumber(entity.getIdentificationNumber())
                  .build();
            })
        .collect(Collectors.toList());
  }
}
