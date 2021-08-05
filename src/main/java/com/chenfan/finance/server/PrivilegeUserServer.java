package com.chenfan.finance.server;

import com.chenfan.common.vo.Response;
import com.chenfan.finance.server.dto.CompanyNameReq;
import com.chenfan.finance.server.dto.SCompanyRes;
import com.chenfan.finance.server.fallback.PrivilegeUserServerFallback;
import com.chenfan.finance.server.remote.model.SCompanyBank;
import com.chenfan.privilege.api.PrivilegeUserRemoteServer;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

/**
 * @author weishili
 */
@Component
@FeignClient(name = "chenfan-cloud-privilege", fallback = PrivilegeUserServerFallback.class)
public interface PrivilegeUserServer extends PrivilegeUserRemoteServer {

    /**
     * 公司列表
     *
     * @param companyCode
     * @return
     */
    @GetMapping(value = "/company/getList")
    Response<List<SCompanyRes>> getList(String companyCode);

    /**
     * 根据公司名称获取银行账户   根据公司名称筛选第一个创建的银行
     *
     * @param companyNameReq
     * @return
     */
    @PostMapping(value = "/company/selectBankByCompanyName")
    Response<Map<String, SCompanyBank>> selectBankByCompanyName(@RequestBody CompanyNameReq companyNameReq);

    /**
     * 获取非禁用的公司列表
     *
     * @return
     */
    @GetMapping(value = "company/getCompanyBank")
    Response<List<SCompanyBank>> getCompanyBank();

    /**
     * 根据公司名称筛选银行
     *
     * @return
     */
    @PostMapping(value = "company/getByCompanyName")
    Response<List<SCompanyBank>> getByCompanyName(@RequestBody CompanyNameReq companyNameReq);
}