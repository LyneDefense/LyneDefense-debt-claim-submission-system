package com.backend.debt.model.page;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class PageParam implements Serializable {

  private static final Integer PAGE_NO = 1;
  private static final Integer PAGE_SIZE = 10;

  @NotNull private Integer pageNo = PAGE_NO;

  @NotNull private Integer pageSize = PAGE_SIZE;

  /** 默认构造函数 */
  public PageParam() {}

  /**
   * 带参数的构造函数
   *
   * @param pageNo 页码，从1开始
   * @param pageSize 每页记录数
   */
  public PageParam(Integer pageNo, Integer pageSize) {
    this.pageNo = pageNo != null ? pageNo : PAGE_NO;
    this.pageSize = pageSize != null ? pageSize : PAGE_SIZE;
  }
}
