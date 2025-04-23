package com.backend.debt.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
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

  public static List<String> toClaimTypeCodes(List<ClaimType> claimTypes) {
    return claimTypes.stream().map(ClaimType::getCode).collect(Collectors.toList());
  }

  public static List<ClaimType> ofClaimTypeCodes(List<String> claimTypeCodes) {
    if (claimTypeCodes == null) {
      return new ArrayList<>();
    }
    return claimTypeCodes.stream()
        .map(
            code -> {
              for (ClaimType type : ClaimType.values()) {
                if (type.getCode().equals(code)) {
                  return type;
                }
              }
              throw new IllegalArgumentException("无效的申报形式: " + code);
            })
        .collect(Collectors.toList());
  }
}
