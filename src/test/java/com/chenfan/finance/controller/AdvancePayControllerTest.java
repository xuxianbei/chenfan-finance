package com.chenfan.finance.controller;

import com.alibaba.fastjson.JSON;
import com.chenfan.common.config.Constant;
import com.chenfan.common.vo.UserVO;
import com.chenfan.finance.FinanceApplication;
import com.chenfan.finance.model.bo.AdvancePayBo;
import com.chenfan.finance.model.bo.CfPoHeaderBo;
import com.chenfan.finance.model.vo.AdvancePayToNewFinanceVO;
import com.chenfan.finance.service.AdvancePayService;
import com.chenfan.finance.service.CfPoHeaderService;
import org.junit.jupiter.api.AfterEach;
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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

/**
 * @author Eleven.Xiao
 * @description // junit 单元测试
 * @date 2020/12/9  11:24
 */
@SpringBootTest(classes = FinanceApplication.class)
class AdvancePayControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;

    @MockBean
    private AdvancePayService advancePayService;
    @MockBean
    private CfPoHeaderService cfPoHeaderService;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }




    @Test
    void purchaseOrders() throws Exception {
        //数据打桩，目的是使当前待测试的controller 接口调用的业务服务接口返回目标值
        when(cfPoHeaderService.selectPurchaseOrder(Mockito.any(CfPoHeaderBo.class),Mockito.any(UserVO.class))).thenReturn(null);
        this.mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/advancePay/purchaseOrders")//TODO
                        .headers(BaseTestConfig.getHttpHeaders())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        ).andExpect(MockMvcResultMatchers.status().isOk()).andDo(print()).andReturn();
    }

    @Test
    void advancePayApply() throws Exception  {
        //数据打桩，目的是使当前待测试的controller 接口调用的业务服务接口返回目标值
        doNothing().when(advancePayService).advancePayApply(Mockito.any(AdvancePayBo.class),Mockito.any(UserVO.class));
        this.mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/advancePay/apply")//TODO
                        .headers(BaseTestConfig.getHttpHeaders())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .content(JSON.toJSONString(new AdvancePayBo()))
        ).andExpect(MockMvcResultMatchers.status().isOk()).andDo(print()).andReturn();
    }

    @Test
    void updateAdvancePay() throws Exception {
        //数据打桩，目的是使当前待测试的controller 接口调用的业务服务接口返回目标值
        doNothing().when(advancePayService).updateAdvancePay(Mockito.any(AdvancePayBo.class),Mockito.any(UserVO.class));
        this.mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/advancePay/update")//TODO
                        .headers(BaseTestConfig.getHttpHeaders())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                         .content(JSON.toJSONString(new AdvancePayBo()))
        ).andExpect(MockMvcResultMatchers.status().isOk()).andDo(print()).andReturn();
    }

    @Test
    void advancePayList() throws Exception {
        //数据打桩，目的是使当前待测试的controller 接口调用的业务服务接口返回目标值
        when(advancePayService.advancePayList(Mockito.any(AdvancePayBo.class))).thenReturn(null);
        this.mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/advancePay/list")//TODO
                        .headers(BaseTestConfig.getHttpHeaders())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        ).andExpect(MockMvcResultMatchers.status().isOk()).andDo(print()).andReturn();
    }

    @Test
    void advancePayInfo() throws Exception {
        //数据打桩，目的是使当前待测试的controller 接口调用的业务服务接口返回目标值
        this.mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/advancePay/info/1")//TODO
                        .headers(BaseTestConfig.getHttpHeaders())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        ).andExpect(MockMvcResultMatchers.status().isOk()).andDo(print()).andReturn();
    }

    @Test
    void auditOrClose() throws Exception {
        //数据打桩，目的是使当前待测试的controller 接口调用的业务服务接口返回目标值
        this.mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/advancePay/auditOrClose")//TODO
                        .headers(BaseTestConfig.getHttpHeaders())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .content(JSON.toJSONString(new AdvancePayBo()))
        ).andExpect(MockMvcResultMatchers.status().isOk()).andDo(print()).andReturn();
    }

    @Test
    void paymentConfigList() throws Exception {
        //数据打桩，目的是使当前待测试的controller 接口调用的业务服务接口返回目标值
        this.mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/advancePay/paymentConfigList")//TODO
                        .headers(BaseTestConfig.getHttpHeaders())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        ).andExpect(MockMvcResultMatchers.status().isOk()).andDo(print()).andReturn();
    }

    @Test
    void exportExcel() throws Exception {
        //数据打桩，目的是使当前待测试的controller 接口调用的业务服务接口返回目标值
        this.mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/advancePay/exportExcel")//TODO
                        .headers(BaseTestConfig.getHttpHeaders())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        ).andExpect(MockMvcResultMatchers.status().isOk()).andDo(print()).andReturn();
    }


    @Test
    void getAmountOfMoney() throws Exception {
        //数据打桩，目的是使当前待测试的controller 接口调用的业务服务接口返回目标值
        this.mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/advancePay/getAmountOfMoney")//TODO
                        .headers(BaseTestConfig.getHttpHeaders())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        ).andExpect(MockMvcResultMatchers.status().isOk()).andDo(print()).andReturn();
    }

    @Test
    void advance() throws Exception  {
        //数据打桩，目的是使当前待测试的controller 接口调用的业务服务接口返回目标值
        this.mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/advancePay/advance/1")//TODO
                        .headers(BaseTestConfig.getHttpHeaders())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        ).andExpect(MockMvcResultMatchers.status().isOk()).andDo(print()).andReturn();
    }

    @Test
    void createList() throws Exception  {
        //数据打桩，目的是使当前待测试的controller 接口调用的业务服务接口返回目标值
        this.mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/advancePay/createList")//TODO
                        .headers(BaseTestConfig.getHttpHeaders())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        ).andExpect(MockMvcResultMatchers.status().isOk()).andDo(print()).andReturn();
    }

    @Test
    void advancePayToFinance() throws Exception {
        //数据打桩，目的是使当前待测试的controller 接口调用的业务服务接口返回目标值
        this.mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/advancePay/advancePayToFinance")//TODO
                        .headers(BaseTestConfig.getHttpHeaders())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .content(JSON.toJSONString(new AdvancePayToNewFinanceVO()))
        ).andExpect(MockMvcResultMatchers.status().isOk()).andDo(print()).andReturn();
    }
}