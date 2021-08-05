package com.chenfan.finance.commons.base;

import com.chenfan.finance.constant.FinanceProjectConstant;
import com.chenfan.finance.utils.RedisServiceUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;


/**
 * @author zhaoganlin
 * @Package com.chenfan.finance.commons.base
 * @ClassName FinanceProjectInitRunner
 * @description
 * @date created in 2020-09-04 11:33
 * @modified by
 */

@Component
@Slf4j
public class FinanceProjectInitRunner implements ApplicationRunner {

    @Autowired
    private RedisServiceUtil redisServiceUtil;

    @Override
    public void run(ApplicationArguments args){
        log.info("====================== init start =======================");
        redisServiceUtil.storageAppKey(FinanceProjectConstant.FINANCE_PROJECT_KEY);
        log.info(" finance_project_key : {} ", FinanceProjectConstant.FINANCE_PROJECT_KEY);
        log.info("====================== init end =======================");
    }

}
