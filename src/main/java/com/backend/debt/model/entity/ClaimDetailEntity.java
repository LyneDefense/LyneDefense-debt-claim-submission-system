package com.backend.debt.model.entity;

import com.backend.debt.enums.ReviewStatus;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/** 申报详情表实体类 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("claim_detail")
public class ClaimDetailEntity extends BaseEntity {


  /** 主键ID */
  @TableId(value = "id", type = IdType.ASSIGN_UUID)
  private String id;

  /** 申报债权性质 */
  private String claimNature;

  /** 担保物明细 */
  private String collateralDetails;

  /** 申报本金金额 */
  private Double declaredPrincipal;

  /** 申报利息金额 */
  private Double declaredInterest;

  /** 申报其他项目金额 */
  private Double declaredOther;

  /** 审查状态（部分确认/暂缓确认/不予确认） */
  private ReviewStatus reviewStatus;
}
