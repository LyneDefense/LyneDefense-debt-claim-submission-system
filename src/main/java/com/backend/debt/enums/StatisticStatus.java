package com.backend.debt.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum StatisticStatus {
  CONFIRMED_ALL("CONFIRMED_ALL", "全部确认"),
  CONFIRM_PART("CONFIRM_PART", "部分确认"),
  CONFIRM_SUSPEND("CONFIRM_SUSPEND", "暂缓确认"),
  CONFIRM_REJECT("CONFIRM_REJECT", "不予确认"),
  CONFIRM_NOT_COMPLETE("CONFIRM_NOT_COMPLETE", "未全部完成审核确认"),
  ;

  @EnumValue private final String code;
  private final String displayName;

  StatisticStatus(String code, String displayName) {
    this.code = code;
    this.displayName = displayName;
  }
}
