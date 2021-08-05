package com.chenfan.finance.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.ThreadFactoryImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池配置
 * @author: xuxianbei
 * Date: 2021/3/19
 * Time: 9:47
 * Version:V1.0
 */
@Component
@Slf4j
public class ThreadPoolConfig {

    /**
     * 定时清理内存线程池
     *
     * @return
     */
    @Bean("sheduleClearMemory")
    public ThreadPoolTaskScheduler sheduleClearMemoryThreadPool() {
        ThreadPoolTaskScheduler executor = new ThreadPoolTaskScheduler();
        // 设置拒绝策略
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 处理异常
        executor.setThreadFactory(new ThreadFactoryImpl("异步任务处理线程"));

        return executor;
    }
}
