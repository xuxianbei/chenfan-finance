package com.chenfan.finance.controller;

import com.alibaba.fastjson.JSON;
import com.chenfan.common.config.Constant;
import com.chenfan.finance.FinanceApplication;
import com.chenfan.finance.model.CfInvoiceSettlement;
import com.chenfan.finance.model.bo.CfInvoiceSettlementBO;
import com.chenfan.finance.model.dto.InvoiceSettlementPercentDTO;
import com.chenfan.finance.service.CfInvoiceSettlementService;
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

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

/**
 * @author Eleven.Xiao
 * @description //junit 单元测试
 * @date 2020/12/9  11:25
 */
@SpringBootTest(classes = FinanceApplication.class)
class CfSettlementControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;

    @MockBean
    CfInvoiceSettlementService cfInvoiceSettlementService;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }
    @Test
    void createSettlement() throws Exception {
        when(cfInvoiceSettlementService.addSettlement(Mockito.any(), Mockito.any())).thenReturn(0);
        this.mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/settlement/createSettlement")//TODO
                        .headers(BaseTestConfig.getHttpHeaders())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .content(JSON.toJSONString(new CfInvoiceSettlement()))
        ).andExpect(MockMvcResultMatchers.status().isOk()).andDo(print()).andReturn();
    }

    @Test
    void updateSettlement() throws Exception {
        this.mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/settlement/updateSettlement")//TODO
                        .headers(BaseTestConfig.getHttpHeaders())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .content(JSON.toJSONString(new ArrayList<CfInvoiceSettlementBO>()))
        ).andExpect(MockMvcResultMatchers.status().isOk()).andDo(print()).andReturn();
    }

    @Test
    void getPaymentReqPrint() throws Exception {
        when(cfInvoiceSettlementService.paymentReqPrint(Mockito.anyLong(), Mockito.any())).thenReturn(null);
        this.mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/settlement/paymentReqPrint/1")//TODO
                        .headers(BaseTestConfig.getHttpHeaders())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        ).andExpect(MockMvcResultMatchers.status().isOk()).andDo(print()).andReturn();
    }

    @Test
    void getInvoicePrint() throws Exception {
        when(cfInvoiceSettlementService.print(Mockito.anyLong())).thenReturn(null);
        this.mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/settlement/invoicePrint/1")//TODO
                        .headers(BaseTestConfig.getHttpHeaders())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        ).andExpect(MockMvcResultMatchers.status().isOk()).andDo(print()).andReturn();
    }

    @Test
    void updateSettle() throws Exception {
        this.mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/settlement/updateSettle")//TODO
                        .headers(BaseTestConfig.getHttpHeaders())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .content(JSON.toJSONString(new CfInvoiceSettlement()))
        ).andExpect(MockMvcResultMatchers.status().isOk()).andDo(print()).andReturn();
    }

    @Test
    void getSettlementAmount() throws Exception {
        when(cfInvoiceSettlementService.getSettlementAmount(Mockito.any())).thenReturn(null);
        this.mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/settlement/getSettlementAmount")//TODO
                        .headers(BaseTestConfig.getHttpHeaders())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .content(JSON.toJSONString(new CfInvoiceSettlement()))
        ).andExpect(MockMvcResultMatchers.status().isOk()).andDo(print()).andReturn();
    }

    @Test
    void invoiceDetailSettlementExport() throws Exception {
        when(cfInvoiceSettlementService.invoiceDetailSettlementExport(Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(null);
        this.mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/settlement/invoiceDetailSettlementExport")//TODO
                        .headers(BaseTestConfig.getHttpHeaders())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .content(JSON.toJSONString(new InvoiceSettlementPercentDTO()))
        ).andExpect(MockMvcResultMatchers.status().isOk()).andDo(print()).andReturn();
    }
}