package com.backend.debt.model.entity;

import com.backend.debt.enums.IdTypeEnum;
import com.baomidou.mybatisplus.annotation.*;
import lombok.*;
import lombok.experimental.Accessors;

/** 债权人信息表实体类 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("creditor")
public class CreditorEntity extends BaseEntity {

  /** 主键ID */
  @TableId(value = "id", type = IdType.ASSIGN_UUID)
  private String id;

  /** 关联的claim_detail表ID */
  private String claimId;

  /** 债权人姓名 */
  private String name;

  /** 证件号码 */
  private String identificationNumber;

  /** 联系电话 */
  private String phone;

  /** 联系地址 */
  private String address;

  /** 证件类型 */
  private IdTypeEnum idType;
}
