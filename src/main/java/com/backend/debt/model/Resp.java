package com.backend.debt.model;

import lombok.Getter;
import lombok.Setter;
import com.backend.debt.exceptions.CustomException;
import com.backend.debt.exceptions.HttpResponseStatus;

/**
 * 通用响应对象
 * <p>
 * 该类定义了API的标准响应格式，包括状态码、错误信息、业务数据和成功标志。
 * 所有的API响应都应该使用此类进行包装，以保持一致的返回格式。
 * </p>
 *
 * @param <T> 响应数据的类型
 */
@Getter
@Setter
public class Resp<T> {
  /**
   * 错误码，成功时为200
   */
  protected int errorCode;
  
  /**
   * 错误信息，成功时为"OK"
   */
  protected String errorMessage;
  
  /**
   * 业务数据
   */
  private T data;
  
  /**
   * 请求是否成功的标志
   */
  private boolean success = false;

  private Resp() {}

  public static Resp<Void> ok() {
    return ok(HttpResponseStatus.OK.code(), true, HttpResponseStatus.OK.reasonPhrase());
  }

  /**
   * 创建一个自定义状态的成功响应
   * 
   * @param errorCode 状态码
   * @param success 是否成功
   * @param message 响应消息
   * @return 自定义成功响应
   */
  public static Resp<Void> ok(int errorCode, boolean success, String message) {
    Resp<Void> resp = error(errorCode, message);
    resp.setSuccess(success);
    return resp;
  }

  /**
   * 创建一个包含数据的成功响应
   * 
   * @param data 响应数据
   * @param <T> 数据类型
   * @return 包含数据的成功响应
   */
  public static <T> Resp<T> data(T data) {
    Resp<T> resp = new Resp<>();
    resp.setErrorCode(HttpResponseStatus.OK.code());
    resp.setErrorMessage(HttpResponseStatus.OK.reasonPhrase());
    resp.setData(data);
    resp.setSuccess(true);
    return resp;
  }

  /**
   * 创建一个自定义异常的错误响应
   * 
   * @param ex 自定义异常
   * @return 错误响应
   */
  public static Resp<Void> error(CustomException ex) {
    Resp<Void> resp = new Resp<>();
    resp.setErrorCode(ex.getCode());
    resp.setErrorMessage(ex.getMessage());
    return resp;
  }

  /**
   * 创建一个自定义错误码和错误信息的错误响应
   * 
   * @param errorCode 错误码
   * @param errorMessage 错误信息
   * @return 错误响应
   */
  public static Resp<Void> error(int errorCode, String errorMessage) {
    Resp<Void> resp = new Resp<>();
    resp.setErrorCode(errorCode);
    resp.setErrorMessage(errorMessage);
    return resp;
  }

  /**
   * 创建一个内部服务器错误响应
   * 
   * @param message 错误信息
   * @return 内部服务器错误响应
   */
  public static Resp<Void> internalServerError(String message) {
    Resp<Void> resp = new Resp<>();
    resp.setErrorCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
    resp.setErrorMessage(message);
    return resp;
  }
}
