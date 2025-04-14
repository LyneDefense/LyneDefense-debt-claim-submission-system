package com.backend.debt.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置类
 * <p>
 * 该类用于配置Web相关功能，如跨域资源共享(CORS)、拦截器、静态资源访问等。
 * 通过实现WebMvcConfigurer接口，可以自定义Spring MVC的各种配置。
 * </p>
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

  /**
   * 配置跨域资源共享(CORS)
   * <p>
   * 允许前端应用从不同源访问API，解决浏览器的同源策略限制。
   * 当前配置允许所有来源、所有方法和所有头信息的跨域请求。
   * 在生产环境中，应该根据实际需求限制来源和方法，提高安全性。
   * </p>
   *
   * @param registry CORS注册表
   */
  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry
        .addMapping("/**")                   // 应用于所有路径
        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")  // 允许的HTTP方法
        .allowedOriginPatterns("*")          // 允许的来源，生产环境应限制为特定域名
        .allowedHeaders("*")                 // 允许的请求头
        .exposedHeaders("Authorization")     // 暴露的响应头，如果需要前端访问
        .allowCredentials(true)              // 允许发送身份凭证（如cookies）
        .maxAge(3600);                       // 预检请求的缓存时间（秒）
  }
}
