package com.chenfan.finance.controller;

import com.alibaba.fastjson.JSON;
import com.chenfan.common.config.Constant;
import com.chenfan.finance.FinanceApplication;
import com.chenfan.finance.model.CfBankAndCash;
import com.chenfan.finance.model.CfBankAndCashInvoiceExtend;
import com.chenfan.finance.model.vo.CfBankAndCashShowRequestVo;
import com.chenfan.finance.service.CfBankAndCashService;
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
 * @date 2020/12/9  11:24
 */
@SpringBootTest(classes = FinanceApplication.class)
class CfBankAndCashControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;

    @MockBean
    private CfBankAndCashService cfBankAndCashService;
    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }
    @Test
    void create() throws Exception {
        this.mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/bank_and_cash/create")//TODO
                        .headers(BaseTestConfig.getHttpHeaders())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .content(JSON.toJSONString(new CfBankAndCash()))
        ).andExpect(MockMvcResultMatchers.status().isOk()).andDo(print()).andReturn();
    }

    @Test
    void delete()throws Exception  {
        this.mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/bank_and_cash/delete_batch")//TODO
                        .headers(BaseTestConfig.getHttpHeaders())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .content(JSON.toJSONString(new CfBankAndCashShowRequestVo()))
        ).andExpect(MockMvcResultMatchers.status().isOk()).andDo(print()).andReturn();
    }

    @Test
    void getList() throws Exception {
        when(cfBankAndCashService.getList(Mockito.any(CfBankAndCashShowRequestVo.class))).thenReturn(null);
        this.mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/bank_and_cash/getList")//TODO
                        .headers(BaseTestConfig.getHttpHeaders())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .content(JSON.toJSONString(new CfBankAndCashShowRequestVo()))
        ).andExpect(MockMvcResultMatchers.status().isOk()).andDo(print()).andReturn();
    }

    @Test
    void getInfo() throws Exception {
        this.mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/bank_and_cash/info")//TODO
                        .headers(BaseTestConfig.getHttpHeaders())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        ).andExpect(MockMvcResultMatchers.status().isOk()).andDo(print()).andReturn();
    }

    @Test
    void update() throws Exception {
        this.mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/bank_and_cash/update")//TODO
                        .headers(BaseTestConfig.getHttpHeaders())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .content(JSON.toJSONString(new CfBankAndCash()))
        ).andExpect(MockMvcResultMatchers.status().isOk()).andDo(print()).andReturn();
    }

    @Test
    void review() throws Exception {
        this.mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/bank_and_cash/review_flow")//TODO
                        .headers(BaseTestConfig.getHttpHeaders())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .content(JSON.toJSONString(new CfBankAndCash()))
        ).andExpect(MockMvcResultMatchers.status().isOk()).andDo(print()).andReturn();
    }

    @Test
    void export() throws Exception {
        this.mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/bank_and_cash/export")//TODO
                        .headers(BaseTestConfig.getHttpHeaders())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .content(JSON.toJSONString(new CfBankAndCashShowRequestVo()))
        ).andExpect(MockMvcResultMatchers.status().isOk()).andDo(print()).andReturn();
    }

    @Test
    void createBankCashAndClear() throws Exception {
        this.mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/bank_and_cash/createBankCashAndClear")//TODO
                        .headers(BaseTestConfig.getHttpHeaders())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .content(JSON.toJSONString(new CfBankAndCashInvoiceExtend()))
        ).andExpect(MockMvcResultMatchers.status().isOk()).andDo(print()).andReturn();
    }
}