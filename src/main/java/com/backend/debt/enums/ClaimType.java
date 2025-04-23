package com.backend.debt.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum ClaimType {
  MAIL("MAIL", "邮寄"),
  EMAIL("EMAIL", "电子邮件"),
  ONSITE("ONSITE", "现场");

  @EnumValue private final String code;
  private final String displayName;

  ClaimType(String code, String displayName) {
    this.code = code;
    this.displayName = displayName;
  }
}
