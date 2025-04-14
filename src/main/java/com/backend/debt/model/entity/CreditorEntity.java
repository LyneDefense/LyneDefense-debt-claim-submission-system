package com.backend.debt.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/** 债权人信息表实体类 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("creditor")
public class CreditorEntity extends BaseEntity {

  /** 主键ID */
  @TableId(value = "id", type = IdType.ASSIGN_UUID)
  private String id;

  /** 关联的declaration_registration表ID */
  private String declarationId;

  /** 债权人姓名 */
  @TableField("name")
  private String name;

  /** 证件号码 */
  private String identificationNumber;

  /** 联系电话 */
  private String phone;

  /** 联系地址 */
  private String address;
}
