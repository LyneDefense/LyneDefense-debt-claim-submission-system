package com.backend.debt.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum DeclarationType {
  MAIL("MAIL", "邮寄"),
  EMAIL("EMAIL", "电子邮件"),
  ONSITE("ONSITE", "现场");

  @EnumValue private final String code;
  private final String displayName;

  DeclarationType(String code, String displayName) {
    this.code = code;
    this.displayName = displayName;
  }
}
