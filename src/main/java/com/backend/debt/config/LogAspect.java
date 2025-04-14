package com.backend.debt.config;

import cn.hutool.core.date.StopWatch;
import cn.hutool.json.JSONUtil;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * 日志切面配置
 * <p>
 * 该类使用AOP技术，用于记录API接口的调用日志，包括接口名称、请求参数、返回结果和执行时间。
 * 通过该切面可以集中处理日志记录逻辑，而不需要在每个控制器方法中添加日志代码。
 * </p>
 */
@Aspect
@Component
@Slf4j
public class LogAspect {

  /**
   * 环绕通知，记录控制器方法的请求和响应日志
   *
   * @param joinPoint 切点
   * @return 原方法的返回值
   * @throws Throwable 如果原方法抛出异常
   */
  @Around("execution(public * com.backend.debt.controller..*(..))")
  public Object logApiAccess(ProceedingJoinPoint joinPoint) throws Throwable {
    // 获取方法签名和参数
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    Method method = signature.getMethod();
    String methodName = method.getName();
    String className = joinPoint.getTarget().getClass().getSimpleName();
    Object[] args = joinPoint.getArgs();
    
    // 获取请求信息
    HttpServletRequest request = null;
    ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    if (attributes != null) {
      request = attributes.getRequest();
    }
    
    // 记录请求日志
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    
    StringBuilder logMessage = new StringBuilder();
    logMessage.append("\n========== 请求开始 ==========\n");
    logMessage.append("API: ").append(className).append(".").append(methodName).append("\n");
    
    // 记录请求路径和方法
    if (request != null) {
      logMessage.append("请求路径: ").append(request.getRequestURI()).append("\n");
      logMessage.append("请求方法: ").append(request.getMethod()).append("\n");
      logMessage.append("来源IP: ").append(getClientIp(request)).append("\n");
    }
    
    // 格式化请求参数
    logMessage.append("请求参数: ");
    if (args != null && args.length > 0) {
      // 检查参数中是否有@RequestBody注解，打印完整JSON
      Parameter[] parameters = method.getParameters();
      for (int i = 0; i < parameters.length; i++) {
        if (i < args.length && hasAnnotation(parameters[i], RequestBody.class)) {
          try {
            logMessage.append("\n").append(JSONUtil.toJsonPrettyStr(args[i]));
          } catch (Exception e) {
            logMessage.append(args[i]);
          }
        } else if (i < args.length) {
          logMessage.append(args[i]).append(", ");
        }
      }
    } else {
      logMessage.append("无");
    }
    
    log.info(logMessage.toString());
    
    // 执行原方法
    Object result = null;
    try {
      result = joinPoint.proceed();
      
      // 记录响应日志
      stopWatch.stop();
      StringBuilder responseLog = new StringBuilder();
      responseLog.append("\n========== 请求结束 ==========\n");
      responseLog.append("API: ").append(className).append(".").append(methodName).append("\n");
      responseLog.append("执行时间: ").append(stopWatch.getTotalTimeMillis()).append(" ms\n");
      responseLog.append("响应结果: ");
      
      if (result != null) {
        try {
          // 简化输出，避免过长日志
          String resultStr = JSONUtil.toJsonStr(result);
          if (resultStr.length() > 1000) {
            responseLog.append(resultStr.substring(0, 1000)).append("... [内容过长已截断]");
          } else {
            responseLog.append(resultStr);
          }
        } catch (Exception e) {
          responseLog.append(result);
        }
      } else {
        responseLog.append("无");
      }
      
      log.info(responseLog.toString());
      return result;
    } catch (Throwable e) {
      stopWatch.stop();
      log.error("\n========== 请求异常 ==========\n" +
                "API: " + className + "." + methodName + "\n" +
                "执行时间: " + stopWatch.getTotalTimeMillis() + " ms\n" +
                "异常信息: " + e.getMessage(), e);
      throw e;
    }
  }
  
  /**
   * 检查参数是否有指定注解
   *
   * @param parameter 参数
   * @param annotationClass 注解类
   * @return 是否有该注解
   */
  private boolean hasAnnotation(Parameter parameter, Class<? extends Annotation> annotationClass) {
    return parameter.getAnnotation(annotationClass) != null;
  }
  
  /**
   * 获取客户端真实IP地址
   *
   * @param request HTTP请求
   * @return 客户端IP地址
   */
  private String getClientIp(HttpServletRequest request) {
    String ip = request.getHeader("X-Forwarded-For");
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("Proxy-Client-IP");
    }
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("WL-Proxy-Client-IP");
    }
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("HTTP_CLIENT_IP");
    }
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("HTTP_X_FORWARDED_FOR");
    }
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getRemoteAddr();
    }
    return ip;
  }
}
