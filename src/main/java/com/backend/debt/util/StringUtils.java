package com.backend.debt.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StringUtils {

  /**
   * 将字符串添加到源字符串中，处理空值和重复值。 字符串使用"、"作为分隔符连接。
   *
   * @param source 要添加到的源字符串
   * @param toAdd 要添加的字符串
   * @return 去重后的组合字符串
   */
  public static String stringAdd(String source, String toAdd) {
    if (source == null) {
      source = "";
    }

    if (toAdd == null || toAdd.isEmpty()) {
      return source;
    }

    // 分割字符串并过滤空值
    List<String> list = new ArrayList<>(Arrays.asList(source.split("、")));
    list.removeIf(String::isEmpty);

    // 判断并去重添加
    if (!list.contains(toAdd)) {
      list.add(toAdd);
    }
    if (list.size() == 1) {
      return list.get(0);
    }
    // 空列表直接返回 toAdd（兼容原逻辑）
    return String.join("、", list);
  }
}
