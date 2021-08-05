package com.chenfan.finance.controller;

import com.alibaba.fastjson.JSON;
import com.chenfan.common.config.Constant;
import com.chenfan.common.vo.UserVO;
import com.chenfan.finance.FinanceApplication;
import com.chenfan.finance.model.dto.CfClearAddAndUpdateDto;
import com.chenfan.finance.model.dto.CfClearListDto;
import com.chenfan.finance.model.vo.CfBankAndCashShowRequestVo;
import com.chenfan.finance.service.CfClearHeaderService;
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
 * @date 2020/12/9  11:25
 */
@SpringBootTest(classes = FinanceApplication.class)
class CfClearControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;
    @MockBean
    CfClearHeaderService cfClearHeaderService;
    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }
    @Test
    void saveAfterCharge() throws Exception {
        this.mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/cfClear/saveAfterCharge")//TODO
                        .headers(BaseTestConfig.getHttpHeaders())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .content(JSON.toJSONString(new CfClearAddAndUpdateDto()))
        ).andExpect(MockMvcResultMatchers.status().isOk()).andDo(print()).andReturn();
    }
    //TODO 400 异常，待调试
    // 调试结果：服务端接受参数验证未通过，需填写正确的请求参数
    @Test
    void update() throws Exception {
        CfClearAddAndUpdateDto cfClearAddAndUpdateDto = new CfClearAddAndUpdateDto();
        cfClearAddAndUpdateDto.setBrandId(1L);
        //when(cfClearHeaderService.update(Mockito.any(CfClearAddAndUpdateDto.class),Mockito.any(UserVO.class))).thenReturn(null);
        this.mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/cfClear/update")//TODO
                        .headers(BaseTestConfig.getHttpHeaders())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .content(JSON.toJSONString(cfClearAddAndUpdateDto))
        ).andExpect(MockMvcResultMatchers.status().isOk()).andDo(print()).andReturn();
    }

    @Test
    void list() throws Exception {
        this.mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/cfClear/list")//TODO
                        .headers(BaseTestConfig.getHttpHeaders())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .content(JSON.toJSONString(new CfClearListDto()))
        ).andExpect(MockMvcResultMatchers.status().isOk()).andDo(print()).andReturn();
    }

    @Test
    void detail() throws Exception {
        when(cfClearHeaderService.detail(Mockito.anyString())).thenReturn(null);
        this.mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/cfClear/detail")//TODO
                        .headers(BaseTestConfig.getHttpHeaders())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        ).andExpect(MockMvcResultMatchers.status().isOk()).andDo(print()).andReturn();
    }

    @Test
    void delete() throws Exception {
        when(cfClearHeaderService.delete(Mockito.anyListOf(String.class))).thenReturn(null);
        this.mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/cfClear/delete?clearNos=1")//TODO
                        .headers(BaseTestConfig.getHttpHeaders())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                       // .param("1","1")
        ).andExpect(MockMvcResultMatchers.status().isOk()).andDo(print()).andReturn();
    }

    @Test
    void clearUsers() throws Exception {
        when(cfClearHeaderService.clearUsers()).thenReturn(null);
        this.mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/cfClear/clearUsers")//TODO
                        .headers(BaseTestConfig.getHttpHeaders())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        ).andExpect(MockMvcResultMatchers.status().isOk()).andDo(print()).andReturn();
    }

    @Test
    void export() throws Exception {
        this.mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/cfClear/export")//TODO
                        .headers(BaseTestConfig.getHttpHeaders())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .content(JSON.toJSONString(new CfClearListDto()))
        ).andExpect(MockMvcResultMatchers.status().isOk()).andDo(print()).andReturn();
    }
}