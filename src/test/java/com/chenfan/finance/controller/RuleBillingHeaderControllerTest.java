package com.chenfan.finance.controller;

import com.alibaba.fastjson.JSON;
import com.chenfan.common.config.Constant;
import com.chenfan.finance.FinanceApplication;
import com.chenfan.finance.model.bo.RuleBillingHeaderBo;
import com.chenfan.finance.service.RuleBillingHeaderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

/**
 * @author Eleven.Xiao
 * @description //junit 单元测试
 * @date 2020/12/9  11:26
 */
@SpringBootTest(classes = FinanceApplication.class)
class RuleBillingHeaderControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;
    @MockBean
    RuleBillingHeaderService ruleBillingHerderService;


    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }
    @Test
    void ruleBillList() throws Exception {
        this.mockMvc.perform(
            MockMvcRequestBuilders
                    .get("/ruleBill/list")//TODO
                    .headers(BaseTestConfig.getHttpHeaders())
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .accept(MediaType.APPLICATION_JSON_VALUE)
    ).andExpect(MockMvcResultMatchers.status().isOk()).andDo(print()).andReturn();
    }

    @Test
    void updateState() throws Exception {
        when( ruleBillingHerderService.updateState(Mockito.anyInt(), Mockito.anyLong())).thenReturn(0);
        this.mockMvc.perform(
            MockMvcRequestBuilders
                    .get("/ruleBill/updateState")//TODO
                    .headers(BaseTestConfig.getHttpHeaders())
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .accept(MediaType.APPLICATION_JSON_VALUE)
    ).andExpect(MockMvcResultMatchers.status().isOk()).andDo(print()).andReturn();
    }

    @Test
    void detail() throws Exception {
        this.mockMvc.perform(
            MockMvcRequestBuilders
                    .get("/ruleBill/detail")//TODO
                    .headers(BaseTestConfig.getHttpHeaders())
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .accept(MediaType.APPLICATION_JSON_VALUE)
                    //.content(JSON.toJSONString(new RuleBillingHeaderBo()))
    ).andExpect(MockMvcResultMatchers.status().isOk()).andDo(print()).andReturn();
    }

    @Test
    void addRule() throws Exception {
        this.mockMvc.perform(
            MockMvcRequestBuilders
                    .post("/ruleBill/addRule")//TODO
                    .headers(BaseTestConfig.getHttpHeaders())
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .accept(MediaType.APPLICATION_JSON_VALUE)
                    .content(JSON.toJSONString(new RuleBillingHeaderBo()))
        ).andExpect(MockMvcResultMatchers.status().isOk()).andDo(print()).andReturn();
    }

    @Test
    void updateRule() throws Exception {
        this.mockMvc.perform(
            MockMvcRequestBuilders
                    .post("/ruleBill/updateRule")//TODO
                    .headers(BaseTestConfig.getHttpHeaders())
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .accept(MediaType.APPLICATION_JSON_VALUE)
                    .content(JSON.toJSONString(new RuleBillingHeaderBo()))
    ).andExpect(MockMvcResultMatchers.status().isOk()).andDo(print()).andReturn();
    }
}