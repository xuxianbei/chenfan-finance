package com.chenfan.finance;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import com.chenfan.ccp.plug.rpc.service.ApprovalRemoteService;
import com.chenfan.ccp.plug.rpc.service.CodeGenerateService;
import com.chenfan.ccp.plug.rpc.service.impl.DefaultApprovalRemoteServiceImpl;
import com.chenfan.ccp.plug.rpc.service.impl.DefaultCodeGenerateServiceImpl;
import com.chenfan.ccp.util.start.ApplicationContextUtil;
import com.chenfan.code.generate.CodeGenerateClient;
import com.chenfan.common.exception.GlobalDefaultExceptionHandler;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author xuxb
 */
@SpringBootApplication(exclude = DruidDataSourceAutoConfigure.class)
@EnableFeignClients(basePackages={"com.chenfan.*"})
@EnableDiscoveryClient
@EnableTransactionManagement
@MapperScan("com.chenfan.**.dao.**")
@EnableScheduling
@EnableAsync
@EnableRedisHttpSession
@Import(GlobalDefaultExceptionHandler.class)
public class FinanceApplication {

    @Bean
    public ApprovalRemoteService approvalRemoteService() {
        return new DefaultApprovalRemoteServiceImpl();
    }

    @Bean
    public CodeGenerateService codeGenerateService() {
        return new DefaultCodeGenerateServiceImpl();
    }

    public static void main(String[] args) {
        ApplicationContextUtil.run(args);
    }

}
