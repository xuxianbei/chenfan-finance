package com.chenfan.finance.utils;

import com.chenfan.finance.constant.FinanceProjectConstant;
import com.chenfan.finance.constant.RedisKeyConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * @Description:
 * @author: zhaoganlin
 * @date: 20/09/04
 * @version: V1.0
 */

@Slf4j
@Component
public class RedisServiceUtil {

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Value("${spring.profiles.active}")
    private String env;

    /**
     * redis分布式锁
     * 保证同一秒内集群环境下只有一个任务执行
     * 需要传入任务对应的KEY
     *
     * @param function 目标函数
     */
    public void setRedisLock(Consumer<String> function) {
        String appKey = redisTemplate.opsForValue().get(RedisKeyConstant.FINANCE_INIT_KEY);
        String dev = "dev";
        if (!dev.equalsIgnoreCase(env)) {
            log.info("================定时任务start===================");
            function.accept(appKey);
            log.info("================定时任务end===================");
            return;
        }
        if (FinanceProjectConstant.FINANCE_PROJECT_KEY.equals(appKey)) {
            log.info("================定时任务start===================");
            function.accept(appKey);
            log.info("================定时任务end===================");
        } else if (appKey == null) {
            storageAppKey(FinanceProjectConstant.FINANCE_PROJECT_KEY);
        }
    }

    /**
     * 存储项目唯一标识
     *
     * @param appKey
     */
    public void storageAppKey(String appKey) {
        try {
            String appKeyExist = redisTemplate.opsForValue().get(RedisKeyConstant.FINANCE_INIT_KEY);
            if (appKeyExist == null) {
                redisTemplate.opsForValue().set(RedisKeyConstant.FINANCE_INIT_KEY, appKey, 60 * 10, TimeUnit.MINUTES);
            }
        } catch (Exception e) {
            log.error("初始化项目关键信息异常：{}", e.getMessage());
        }
    }


}
