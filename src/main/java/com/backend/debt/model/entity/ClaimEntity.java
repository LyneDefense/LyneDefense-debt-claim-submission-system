package com.backend.debt.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDate;
import java.util.List;
import javax.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/** 代理人信息表实体类 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("claim")
public class ClaimEntity extends BaseEntity {

  /** 主键ID */
  @TableId(value = "id", type = IdType.ASSIGN_UUID)
  private String id;

  /** 债权编号 */
  private String claimNumber;

  /** 登记人 */
  private String registrar;

  /** 申报日期（格式：yyyy-MM-dd） */
  private LocalDate claimDate;

  /** 申报形式 */
  private List<String> claimTypes;

  /** 分配审核人员 */
  private String auditor;

  /** 债权归类(目前用String表示了) */
  private String claimCategory;

  /** 材料提交情况 (目前用String表示了) */
  private String materialStatus;

  // =====================代理人信息====================

  /** 代理人姓名 */
  private String agentName;

  /** 代理人职务 */
  private String agentPosition;

  /** 联系电话 */
  private String agentPhone;

  /** 是否有债权人会议表决权 */
  private Boolean hasVotingRight;

  // =====================收件人信息====================
  /** 收件人 */
  @NotEmpty private String recipient;

  /** 联系电话 */
  @NotEmpty private String contactPhone;

  /** 邮寄地址 */
  @NotEmpty private String mailingAddress;

  /** 电子邮箱 */
  private String email;
}
