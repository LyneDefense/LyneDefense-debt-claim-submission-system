package com.backend.debt.config;

import com.backend.debt.exceptions.CustomException;
import com.backend.debt.model.Resp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * 全局异常处理器
 *
 * <p>该类负责捕获并处理应用程序中抛出的各种异常，将其转换为统一的响应格式返回给客户端。 通过集中处理异常，可以提供一致的错误响应，减少重复代码，并进行统一的日志记录。
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  /**
   * 处理自定义业务异常
   *
   * @param ex 自定义异常实例
   * @return 统一格式的错误响应
   */
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  @ExceptionHandler(CustomException.class)
  public Resp<Void> handleCustomException(CustomException ex) {
    log.error("业务异常: {}", ex.getInternalMessage(), ex);
    return Resp.error(ex.getCode(), ex.getMessage());
  }

  /**
   * 处理参数验证异常
   *
   * @param ex 参数验证异常
   * @return 统一格式的错误响应
   */
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
  public Resp<Void> handleValidationException(Exception ex) {
    String errorMessage = "参数验证失败";

    if (ex instanceof MethodArgumentNotValidException) {
      MethodArgumentNotValidException validException = (MethodArgumentNotValidException) ex;
      if (validException.getBindingResult().getFieldError() != null) {
        errorMessage = validException.getBindingResult().getFieldError().getDefaultMessage();
      }
    } else if (ex instanceof BindException) {
      BindException bindException = (BindException) ex;
      if (bindException.getBindingResult().getFieldError() != null) {
        errorMessage = bindException.getBindingResult().getFieldError().getDefaultMessage();
      }
    }

    log.error("参数验证异常: {}", errorMessage, ex);
    return Resp.error(HttpStatus.BAD_REQUEST.value(), errorMessage);
  }

  /**
   * 处理参数类型不匹配异常
   *
   * @param ex 参数类型不匹配异常
   * @return 统一格式的错误响应
   */
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public Resp<Void> handleTypeMismatchException(MethodArgumentTypeMismatchException ex) {
    log.error("参数类型不匹配: {}", ex.getMessage(), ex);
    return Resp.error(HttpStatus.BAD_REQUEST.value(), "参数类型不正确: " + ex.getName());
  }

  /**
   * 处理HTTP消息转换异常
   *
   * @param ex HTTP消息转换异常
   * @return 统一格式的错误响应
   */
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public Resp<Void> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
    log.error("请求数据格式不正确: {}", ex.getMessage(), ex);
    return Resp.error(HttpStatus.BAD_REQUEST.value(), "请求数据格式不正确");
  }

  /**
   * 处理所有未捕获的异常
   *
   * @param ex 未捕获的异常
   * @return 统一格式的错误响应
   */
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(Exception.class)
  @ResponseBody
  public Resp<Void> handleException(Exception ex) {
    log.error("系统内部异常: ", ex);
    return Resp.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "系统内部异常");
  }
}
