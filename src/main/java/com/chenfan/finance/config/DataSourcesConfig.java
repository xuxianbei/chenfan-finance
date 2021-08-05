/*
package com.chenfan.finance.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

*/
/**
 * @author lizhejin
 *//*

public class DataSourcesConfig {


    @Bean(name = "finance")
    @ConfigurationProperties(prefix = "spring.datasource.druid.finance")
    public DataSource finance() {
        return new DruidDataSource();
    }

    @Bean
    @Primary
    public DynamicDataSource dataSource(@Qualifier("finance") DataSource finance) {
        Map<Object, Object> targetDataSources = new HashMap<>(16);
        targetDataSources.put(DatabaseType.finance, finance);
        DynamicDataSource dataSource = new DynamicDataSource();
        dataSource.setTargetDataSources(targetDataSources);
        dataSource.setDefaultTargetDataSource(finance);
        return dataSource;
    }
}


*/
