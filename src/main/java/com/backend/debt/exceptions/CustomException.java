package com.backend.debt.exceptions;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

/**
 * 自定义异常类
 * <p>
 * 该异常类用于业务逻辑异常处理，可自定义错误码和错误信息。
 * 所有业务相关的异常应该使用此类或其子类，以便统一异常处理。
 * </p>
 */
@Slf4j
@EqualsAndHashCode(callSuper = true)
@Data
public class CustomException extends RuntimeException {

  /**
   * 错误码，默认为500（服务器内部错误）
   */
  private int code = 500;
  
  /**
   * 向客户端展示的错误信息
   */
  private String message;
  
  /**
   * 内部错误信息，用于日志记录和调试
   */
  private String internalMessage;

  /**
   * 创建一个完整的自定义异常
   *
   * @param code 错误码
   * @param message 错误信息
   * @param internalMessage 内部错误信息
   */
  public CustomException(int code, String message, String internalMessage) {
    super();
    this.code = code;
    this.message = message;
    this.internalMessage = internalMessage;
    log.error("Exception Code: {}, Message: {}", this.code, this.getMessage(), this);
  }

  /**
   * 创建一个简单的自定义异常，使用内部错误信息作为错误信息
   *
   * @param internalMessage 内部错误信息
   */
  public CustomException(String internalMessage) {
    super();
    this.message = internalMessage;
    this.internalMessage = internalMessage;
    log.error("Exception Code: {}, Message: {}", this.code, this.getMessage(), this);
  }

  /**
   * 创建一个包含原始异常的自定义异常
   *
   * @param internalMessage 内部错误信息
   * @param e 原始异常
   */
  public CustomException(String internalMessage, Throwable e) {
    super(internalMessage, e);
    this.internalMessage = internalMessage;
    log.error("Exception Code: {}, Message: {}", this.code, this.getMessage(), this);
  }

  /**
   * 复制另一个自定义异常的信息创建新的异常
   *
   * @param e 源自定义异常
   */
  public CustomException(CustomException e) {
    super();
    this.code = e.getCode();
    this.message = e.getMessage();
    this.internalMessage = e.getInternalMessage();
    log.error("Exception Code: {}, Message: {}", this.code, this.getMessage(), this);
  }

  /**
   * 使用格式化参数创建自定义异常
   *
   * @param commonException 基础异常模板
   * @param args 格式化参数
   */
  public CustomException(CustomException commonException, Object... args) {
    super();
    this.code = commonException.getCode();
    this.message = commonException.getMessage();
    this.internalMessage = String.format(commonException.getInternalMessage(), args);
    log.error("Exception Code: {}, Message: {}", this.code, this.getMessage(), this);
  }
}
