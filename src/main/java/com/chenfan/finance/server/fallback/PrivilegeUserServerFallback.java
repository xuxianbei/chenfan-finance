package com.chenfan.finance.server.fallback;

import com.chenfan.common.vo.Response;
import com.chenfan.common.vo.ResponseCode;
import com.chenfan.finance.server.PrivilegeUserServer;
import com.chenfan.finance.server.dto.CompanyNameReq;
import com.chenfan.finance.server.dto.SCompanyRes;
import com.chenfan.finance.server.remote.model.SCompanyBank;
import com.chenfan.privilege.request.SRoleVOReq;
import com.chenfan.privilege.request.SUserVOReq;
import com.chenfan.privilege.response.SRoleVORes;
import com.chenfan.privilege.response.SUserVORes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author: xuxianbei
 * Date: 2020/9/11
 * Time: 16:41
 * Version:V1.0
 */
@Slf4j
@Component
public class PrivilegeUserServerFallback implements PrivilegeUserServer {
    @Override
    public Response<List<SUserVORes>> listUsers(SUserVOReq userVoReq) {
        log.info("BaseRemoteServer 调用失败");
        return Response.error(ResponseCode.FAIL);
    }

    @Override
    public Response<SUserVORes> getUserInfo(SUserVOReq userVoReq) {
        log.info("BaseRemoteServer 调用失败");
        return Response.error(ResponseCode.FAIL);
    }

    @Override
    public Response<List<SRoleVORes>> listRoles(SRoleVOReq roleReq) {
        log.info("BaseRemoteServer 调用失败");
        return Response.error(ResponseCode.FAIL);
    }

    @Override
    public Response<List<SRoleVORes>> getUsersByRoleCode(SRoleVOReq roleReq) {
        log.info("BaseRemoteServer 调用失败");
        return Response.error(ResponseCode.FAIL);
    }

    @Override
    public Response<List<SCompanyRes>> getList(String companyCode) {
        return Response.error(ResponseCode.FAIL);
    }

    @Override
    public Response<Map<String, SCompanyBank>> selectBankByCompanyName(CompanyNameReq companyNameReq) {
        log.info("BaseRemoteServer 调用失败");
        return Response.error(ResponseCode.FAIL);
    }

    @Override
    public Response<List<SCompanyBank>> getCompanyBank() {
        log.info("BaseRemoteServer 调用失败");
        return Response.error(ResponseCode.FAIL);
    }

    @Override
    public Response<List<SCompanyBank>> getByCompanyName(CompanyNameReq companyNameReq) {
        log.info("BaseRemoteServer 调用失败");
        return Response.error(ResponseCode.FAIL);
    }
}
