package com.chenfan.finance.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * @author lizhejin
 */
@Configuration
@EnableOpenApi
@RefreshScope
public class SwaggerConfig {

    @Value("${swagger.onoff}")
    private String onoff;

    private ApiInfo initApiInfo() {

        return new ApiInfoBuilder()
                .title("SCM基本数据")
                .description(initContextInfo())
                .contact(new Contact("lizhejin", "http://10.228.81.197:10080/chenfan-cloud/chenfan-base/blob/master/README.md", "lizhejin@thechenfan.com"))
                .version("1.0")
                .build();
    }

    private String initContextInfo() {
        StringBuffer sb = new StringBuffer();
        sb.append("## SCM采购服务，主要业务有 \n\n")
                .append("### 提供的信息 \n\n");
        return sb.toString();
    }

    @Bean
    public Docket restfulApi() {
        Docket docket = new Docket(DocumentationType.SWAGGER_2)
                .groupName("chenfan-finance")
                .genericModelSubstitutes(ResponseEntity.class)
                .useDefaultResponseMessages(true)
                .forCodeGeneration(false)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.chenfan.finance.controller"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(initApiInfo());
        String isTrue = "true";
        if (!isTrue.equals(onoff)) {
            docket = new Docket(DocumentationType.SWAGGER_2)
                    .groupName("")
                    .genericModelSubstitutes(ResponseEntity.class)
                    .useDefaultResponseMessages(true)
                    .forCodeGeneration(false)
                    .select()
                    .build()
                    .apiInfo(new ApiInfoBuilder().build());
        }

        return docket;
    }
}