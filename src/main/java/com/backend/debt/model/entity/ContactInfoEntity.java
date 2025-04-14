package com.backend.debt.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/** 收件信息表实体类 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("contact_info")
public class ContactInfoEntity extends BaseEntity {

  /** 主键ID */
  @TableId(value = "id", type = IdType.ASSIGN_UUID)
  private String id;

  /** 关联的declaration_registration表ID */
  private String declarationId;

  /** 收件人姓名 */
  private String recipientName;

  /** 收件人电话 */
  private String recipientPhone;

  /** 收件地址 */
  private String recipientAddress;

  /** 电子邮件 */
  private String email;
}
