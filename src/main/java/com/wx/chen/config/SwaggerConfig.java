package com.wx.chen.config;


import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
* 
* @author Guanghui Chen  E-mail:chenguanghui@zhuoqin.cn
* @version 创建时间：2019年7月11日 下午4:03:20
* 类说明 : 设置swagger便于调试
*/
@Configuration
@EnableSwagger2
public class SwaggerConfig {

	@Bean
    public Docket createRestApi() {
               return new Docket(DocumentationType.SWAGGER_2)
		            .enable(true)
		            .apiInfo(apiInfo())
		            .select()
		            .apis(RequestHandlerSelectors.basePackage("com.wx.chen.controller"))
		            .apis(RequestHandlerSelectors.any())
		            .paths(PathSelectors.any())
					.build()
					.securitySchemes(security())
					.securityContexts(securityContexts());         //主要关注点--统一填写一次token
    }
    private ApiInfo apiInfo(){
        return new ApiInfoBuilder()
                .title("wx.chen.-Api")
                .description("wx.chen. Api")
                .version("0.0.1")
                .build();
	}
	private List<ApiKey> security() {
		List<ApiKey> list = new ArrayList<>();
		ApiKey key = new ApiKey("Authorization", "Authorization", "header");
		list.add(key);
        return list;
	}
	private List<SecurityContext> securityContexts() {
		List<SecurityContext> list = new ArrayList<>();
		list.add(SecurityContext.builder()
		.securityReferences(defaultAuth())
		.forPaths(PathSelectors.regex("^(?!auth).*$"))
		.build());
        return list;
    }

	private List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
		AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
		authorizationScopes[0] = authorizationScope;
		List<SecurityReference> list = new ArrayList<>();
		list.add(new SecurityReference("Authorization", authorizationScopes));
        return list;
    }
 
	
}