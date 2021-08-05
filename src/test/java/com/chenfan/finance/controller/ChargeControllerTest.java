package com.chenfan.finance.controller;

import com.alibaba.fastjson.JSON;
import com.chenfan.common.config.Constant;
import com.chenfan.common.vo.UserVO;
import com.chenfan.finance.FinanceApplication;
import com.chenfan.finance.model.dto.CfChargeListQuery;
import com.chenfan.finance.model.dto.CfChargeSaveQuery;
import com.chenfan.finance.model.dto.UpdateChargeStatusDTO;
import com.chenfan.finance.model.vo.CfChargeRelevanceVO;
import com.chenfan.finance.service.CfChargeService;
import lombok.val;
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

import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

/**
 * @author Eleven.Xiao
 * @description //junit 单元测试
 * @date 2020/12/9  11:25
 */
@SpringBootTest(classes = FinanceApplication.class)
class ChargeControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;
    @MockBean
    CfChargeService cfChargeService;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }
    @Test
    void queryChargeList() throws Exception  {
        when(cfChargeService.queryChargeList(Mockito.any())).thenReturn(null);
        this.mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/charge/queryChargeList")//TODO
                        .headers(BaseTestConfig.getHttpHeaders())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        ).andExpect(MockMvcResultMatchers.status().isOk()).andDo(print()).andReturn();
    }

    @Test
    void chargeListGroupByChargeSource() throws Exception {
        when(cfChargeService.chargeListGroupByChargeSource(Mockito.any())).thenReturn(null);
        this.mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/charge/chargeListGroupByChargeSource")//TODO
                        .headers(BaseTestConfig.getHttpHeaders())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        ).andExpect(MockMvcResultMatchers.status().isOk()).andDo(print()).andReturn();
    }

    @Test
    void chargeList() throws Exception {
        //when(cfChargeService.chargeList(Mockito.any())).thenReturn(null);
        this.mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/charge/chargeList")//TODO
                        .headers(BaseTestConfig.getHttpHeaders())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .content(JSON.toJSONString(new CfChargeListQuery()))
        ).andExpect(MockMvcResultMatchers.status().isOk()).andDo(print()).andReturn();
    }

    @Test
    void chargeListPost() throws Exception {
       // when(cfChargeService.chargeList(Mockito.any())).thenReturn(null);
        this.mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/charge/chargeList")//TODO
                        .headers(BaseTestConfig.getHttpHeaders())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        ).andExpect(MockMvcResultMatchers.status().isOk()).andDo(print()).andReturn();
    }

    @Test
    void saveCharge() throws Exception {
        when(cfChargeService.saveCharge(Mockito.any(CfChargeSaveQuery.class),Mockito.any(UserVO.class))).thenReturn(1L);
        this.mockMvc.perform(
                MockMvcRequestBuilders
                        .put("/charge/saveCharge")//TODO
                        .headers(BaseTestConfig.getHttpHeaders())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .content(JSON.toJSONString(new CfChargeSaveQuery()))
        ).andExpect(MockMvcResultMatchers.status().isOk()).andDo(print()).andReturn();
    }

    @Test
    void updateCharge() throws Exception {
        Mockito.doNothing().when(cfChargeService).updateCharge(Mockito.any(CfChargeSaveQuery.class),Mockito.any(UserVO.class));
        this.mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/charge/updateCharge")//TODO
                        .headers(BaseTestConfig.getHttpHeaders())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .content(JSON.toJSONString(new CfChargeSaveQuery()))
        ).andExpect(MockMvcResultMatchers.status().isOk()).andDo(print()).andReturn();
    }

    @Test
    void detailCharge() throws Exception {
        when(cfChargeService.detailCharge(Mockito.any())).thenReturn(null);
        this.mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/charge/detailCharge/1")//TODO
                        .headers(BaseTestConfig.getHttpHeaders())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        ).andExpect(MockMvcResultMatchers.status().isOk()).andDo(print()).andReturn();
    }

    @Test
    void updateChargeCheckStatus() throws Exception {
        Mockito.doNothing().when(cfChargeService).updateChargeCheckStatus(Mockito.anyLong(),Mockito.anyInt(),Mockito.any(UserVO.class));
        this.mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/charge/updateChargeCheckStatus/1?status=0")//TODO
                        .headers(BaseTestConfig.getHttpHeaders())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        ).andExpect(MockMvcResultMatchers.status().isOk()).andDo(print()).andReturn();
    }

    @Test
    void updateChargeListCheckStatus() throws Exception {
        UpdateChargeStatusDTO updateChargeStatusDTO = new UpdateChargeStatusDTO();
        updateChargeStatusDTO.setChargeIds(new TreeSet<>());
        updateChargeStatusDTO.setStatus(0);
        Mockito.doNothing().when(cfChargeService).updateChargeListCheckStatus(Mockito.anySet(),Mockito.anyInt(),Mockito.any(UserVO.class));
        this.mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/charge/updateChargeListCheckStatus")//TODO
                        .headers(BaseTestConfig.getHttpHeaders())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .content(JSON.toJSONString(updateChargeStatusDTO))
        ).andExpect(MockMvcResultMatchers.status().isOk()).andDo(print()).andReturn();
    }

    @Test
    void delRelevance() throws Exception {
        when(cfChargeService.delRelevance(Mockito.any(), Mockito.any())).thenReturn(0);
        this.mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/charge/delRelevance")//TODO
                        .headers(BaseTestConfig.getHttpHeaders())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .content(JSON.toJSONString(new CfChargeRelevanceVO()))
        ).andExpect(MockMvcResultMatchers.status().isOk()).andDo(print()).andReturn();
    }
}