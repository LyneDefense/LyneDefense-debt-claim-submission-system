package com.backend.debt.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * SpringDoc配置类
 *
 * <p>该类负责配置SpringDoc OpenAPI文档生成器，用于自动生成API文档。 SpringDoc是基于OpenAPI 3规范的API文档生成工具，它可以自动扫描应用中的控制器，
 * 生成RESTful API的文档，并提供Swagger UI界面用于浏览和测试API。
 */
@Configuration
@SecurityScheme(
    name = "BearerAuth", // 若需JWT认证可保留此配置[6](@ref)
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    scheme = "bearer")
public class SpringDocConfig {
  /** API文档是否启用的配置项 */
  @Value("${springdoc.api-docs.enabled:false}")
  private Boolean enable;

  /**
   * 自定义OpenAPI配置
   *
   * <p>此配置定义了API文档的基本信息，如标题、描述、版本等， 以及服务器信息和安全配置。
   *
   * @return OpenAPI配置对象
   */
  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .info(apiInfo())
        // 动态控制文档生成（也可通过springdoc.api-docs.enabled配置实现）
        .addServersItem(new Server().url(enable ? "/" : ""));
  }

  /**
   * 定义API信息
   *
   * <p>包括API的标题、描述、版本、联系人信息和许可证信息等
   *
   * @return API信息对象
   */
  private Info apiInfo() {
    return new Info().title("债权申报系统接口规范").description("债权申报系统API文档，包含了所有可用的API接口说明和参数定义");
  }
}
