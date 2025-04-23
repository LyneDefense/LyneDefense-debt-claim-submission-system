package com.backend.debt.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/** 证件类型枚举（支持个人/机构类型智能判断） */
@Getter
public enum IdTypeEnum {
  // 个人类型
  ID_CARD("ID_CARD", "居民身份证", false),
  PASSPORT("PASSPORT", "护照", false),
  HKMACAU_PASS("HKMACAU", "港澳居民来往内地通行证", false),

  // 机构类型
  USCC("USCC", "统一社会信用代码", true),
  BUSINESS_LICENSE("BL", "营业执照注册号", true),
  ORG_CODE("OC", "组织机构代码证", true);

  @EnumValue private final String code;
  private final String displayName;
  private final boolean isOrganization;

  IdTypeEnum(String code, String displayName, boolean isOrganization) {
    this.code = code;
    this.displayName = displayName;
    this.isOrganization = isOrganization;
  }

  // 智能解析方法（带容错处理）
  public static IdTypeEnum fromInput(String input) {
    String cleanValue = input.replaceAll("[*（）]", "").trim();

    return switch (cleanValue) {
      case "身份证", "个人身份证", "身份证号" -> ID_CARD;
      case "统一信用代码", "信用代码", "社会信用代码" -> USCC;
      case "营业执照", "执照号" -> BUSINESS_LICENSE;
      case "组织机构代码", "机构代码" -> ORG_CODE;
      default -> parseByDisplayName(cleanValue);
    };
  }

  private static IdTypeEnum parseByDisplayName(String cleanValue) {
    for (IdTypeEnum type : values()) {
      if (type.displayName.equalsIgnoreCase(cleanValue)) {
        return type;
      }
    }
    throw new IllegalArgumentException("无效的证件类型: " + cleanValue);
  }

  // 判断是否需要组织机构信息
  public boolean requireOrganizationInfo() {
    return isOrganization;
  }
}
