package com.backend.debt.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/** 审查状态枚举 */
@Getter
public enum ReviewStatus {
  CONFIRM_ALL("CONFIRM_ALL", "全部确认"),
  CONFIRM_PART("CONFIRM_PART", "部分确认"),
  CONFIRM_SUSPEND("CONFIRM_SUSPEND", "暂缓确认"),
  CONFIRM_REJECT("CONFIRM_REJECT", "不予确认"),
  NOT_CONFIRMED("NOT_CONFIRMED", "未审核确认"),
  ;

  @EnumValue private final String code;
  private final String displayName;

  ReviewStatus(String code, String displayName) {
    this.code = code;
    this.displayName = displayName;
  }
}
