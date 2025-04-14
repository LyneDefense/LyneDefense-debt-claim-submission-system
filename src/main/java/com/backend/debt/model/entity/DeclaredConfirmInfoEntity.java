package com.backend.debt.model.entity;

import com.backend.debt.enums.ReviewStatus;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/** 审查确认情况实体类 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("declared_confirm_info")
public class DeclaredConfirmInfoEntity extends BaseEntity {

  /** 主键ID */
  @TableId(value = "id", type = IdType.ASSIGN_UUID)
  private String id;

  /** 关联的申报详情表ID */
  private String claimDetailId;

  /** 审计状态 */
  private ReviewStatus reviewStatus;

  /** 确认本金 */
  private Double confirmedPrincipal;

  /** 确认利息 */
  private Double confirmedInterest;

  /** 确认其他金额 */
  private Double confirmedOther;

  /** 确认债权性质 */
  private String claimNature;

  /** 确认时削减金额 */
  private Double deductionAmount;

  /** 审查理由 */
  private String reviewReason;
}
