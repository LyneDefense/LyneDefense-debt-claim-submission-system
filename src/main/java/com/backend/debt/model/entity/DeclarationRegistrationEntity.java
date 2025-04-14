package com.backend.debt.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDate;
import javax.lang.model.type.DeclaredType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("declaration_registration")
public class DeclarationRegistrationEntity extends BaseEntity {

  /** 主键ID */
  @TableId(value = "id", type = IdType.ASSIGN_UUID)
  private String id;

  /** 债权编号 */
  private String claimNumber;

  /** 登记人 */
  private String registrar;

  /** 申报日期 */
  private LocalDate declarationDate;

  /** 申报形式 */
  private DeclaredType declaredType;

  /** 分配的审核人员 */
  private String auditor;

  /** 债权归类 */
  private String claimCategory;

  /** 材料提交情况 */
  private String materialStatus;
}
