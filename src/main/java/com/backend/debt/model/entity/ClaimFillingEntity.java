package com.backend.debt.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("claim_filling")
public class ClaimFillingEntity extends BaseEntity {

  /** 主键ID */
  @TableId(value = "id", type = IdType.ASSIGN_UUID)
  private String id;

  private String claimId;

  /** 申报债权性质 */
  private String claimNature;

  /** 申报本金 */
  private Double claimPrincipal;

  /** 申报利息 */
  private Double claimInterest;

  /** 申报其他 */
  private Double claimOther;

  /** 担保物明细 */
  private String collateralDetails;
}
