package com.chenfan.finance.controller;

import com.alibaba.fastjson.JSONObject;
import com.chenfan.common.config.Constant;
import com.chenfan.common.exception.SystemState;
import com.chenfan.common.vo.Response;
import com.chenfan.common.vo.ResponseCode;
import com.chenfan.common.vo.UserVO;
import com.chenfan.finance.commons.annotation.Login;
import com.chenfan.finance.commons.annotation.MultiLockInt;
import com.chenfan.finance.commons.exception.FinanceTipException;
import com.chenfan.finance.enums.OperateLockEnum;
import com.chenfan.finance.model.CfInvoiceHeader;
import com.chenfan.finance.model.CfInvoiceSettlement;
import com.chenfan.finance.model.bo.CfInvoiceHeaderAddBO;
import com.chenfan.finance.model.bo.CfInvoiceHeaderBO;
import com.chenfan.finance.model.bo.InvoiceSwitchBanBO;
import com.chenfan.finance.model.dto.*;
import com.chenfan.finance.model.vo.CfInvoiceHeaderDetailVO;
import com.chenfan.finance.model.vo.CfInvoiceSettlementVo;
import com.chenfan.finance.model.vo.ChargeInVO;
import com.chenfan.finance.service.CfInvoiceHeaderService;
import com.chenfan.finance.service.CfInvoiceSettlementService;
import com.chenfan.finance.service.common.state.TaxInvoiceStateService;
import com.chenfan.finance.utils.AuthorizationUtil;
import com.chenfan.finance.utils.OperateUtil;
import com.chenfan.privilege.common.config.SearchAuthority;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.ElementType;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author xiongbin
 * @Description: 账单管理接口
 * @date 2020-08-25
 */
@Slf4j
@RestController
@RequestMapping("invoice")
@Api(tags = "账单管理接口")
public class CfInvoiceHeaderController {

    @Autowired
    private CfInvoiceHeaderService invoiceHeaderService;
    @Resource
    private TaxInvoiceStateService taxInvoiceStateService;
    @Resource
    private AuthorizationUtil authorizationUtil;
    @ApiOperation(value = "红字抵充待选列表", notes = "红字抵充待选列表", produces = "application/json")
    @PostMapping(value = "list/associated", produces = {"application/json;charset=UTF-8"})
    public Response<Object> invoiceHeaderListOfAssociated(@Validated @RequestBody @SearchAuthority CfInvoiceHeaderListOfAssociatedDTO dto) {
        dto.setJobTypes(Arrays.asList("1","2"));
        return Response.success(invoiceHeaderService.invoiceHeaderListOfAssociated(dto));
    }

    @ApiOperation(value = "根据所选的红字抵充的账单编号计算当前蓝字账单的红字调整后金额", notes = "根据所选的红字抵充的账单编号计算当前蓝字账单的红字调整后金额", produces = "application/json")
    @GetMapping(value = "/sum/realRedMoney", produces = {"application/json;charset=UTF-8"})
    public Response<BigDecimal> getRedRealMoney(CalculateRedRealMoneyDto calculateRedRealMoneyDto){
          return invoiceHeaderService.getRedRealMoney(calculateRedRealMoneyDto);
    }
    /**
     * 账单列表
     */
    @ApiOperation(value = "账单列表", notes = "账单列表", produces = "application/json")
    @GetMapping(value = "list", produces = {"application/json;charset=UTF-8"})
    public Response<Object> invoiceHeaderList(@SearchAuthority CfInvoiceHeaderListDTO dto) {
        log.info("获取的关联的brandIds:{}", dto.getBrandIds());
        if (StringUtils.isBlank(dto.getJobType())) {
            dto.setJobTypes(Arrays.asList("1", "2"));
        } else {
            dto.setJobTypes(Arrays.asList(dto.getJobType()));
        }
        return new Response<>(ResponseCode.SUCCESS, invoiceHeaderService.invoiceHeaderList(dto));
    }
    @Deprecated
    @ApiOperation(value = "新增账单预览", notes = "新增账单预览", produces = "application/json")
    @PostMapping(value = "goAdd", produces = {"application/json;charset=UTF-8"})
    public Response<CfInvoiceHeaderBO> goAdd(@RequestBody InvoiceHeaderGoAddDTO invoiceHeaderGoAddDTO) {
        return invoiceHeaderService.goAdd(invoiceHeaderGoAddDTO.getChargeIds(), invoiceHeaderGoAddDTO.getInvoiceNo());
    }
    @Deprecated
    @ApiOperation(value = "账单保存", notes = "账单保存", produces = "application/json")
    @PostMapping(value = "save", produces = {"application/json;charset=UTF-8"})
    public Response<Object> save(@RequestBody CfInvoiceHeaderAddBO invoiceHeaderAddBO ) {
        UserVO user = authorizationUtil.getUserOfRequired();
        try {
            return invoiceHeaderService.save(invoiceHeaderAddBO, user);
        } catch (FinanceTipException tip) {
            return new Response<>(HttpStatus.PRECONDITION_FAILED.value(), tip.getMessage());
        }
    }

    /**
     * 需要限制，避免同一个费用被多次生成账单
     * @param invoiceHeaderAddDTO

     * @return
     */
    @MultiLockInt(paramNames={"chargeIds"},unlockEnum = OperateLockEnum.CHARGE_INVOICE_CREATE,isCheck = true)
    @ApiOperation(value = "账单直接新增", notes = "账单直接新增", produces = "application/json")
    @PostMapping(value = "add", produces = {"application/json;charset=UTF-8"})
    public Response<Object> add(@RequestBody InvoiceHeaderAddDTO invoiceHeaderAddDTO){
        UserVO user = authorizationUtil.getUserOfRequired();
        return invoiceHeaderService.addInvoices(invoiceHeaderAddDTO,user);
    }

    @ApiOperation(value = "账单编辑", notes = "账单编辑", produces = "application/json")
    @PutMapping(value = "update", produces = {"application/json;charset=UTF-8"})
    public Response<Object> update(@RequestBody CfInvoiceHeaderAddBO invoiceHeaderAddBO) {
        UserVO user = authorizationUtil.getUserOfRequired();
        return invoiceHeaderService.update(invoiceHeaderAddBO, user);
    }

    @ApiOperation(value = "填写发票信息", notes = "填写发票信息", produces = "application/json")
    @PutMapping(value = "updateInvoiceInfo", produces = {"application/json;charset=UTF-8"})
    @MultiLockInt(paramNames={"invoiceSettlementId"},paramClass =String.class,isCheck = true,isCollections = false)
    public Response<Object> updateInvoiceInfo(@RequestBody CfInvoiceSettlement settlement) {
        UserVO user = authorizationUtil.getUserOfRequired();
        return invoiceHeaderService.updateInvoiceInfo(settlement, user);

    }

    @ApiOperation(value = "修改发票信息", notes = "修改发票信息", produces = "application/json")
    @PutMapping(value = "editInvoiceInfo", produces = {"application/json;charset=UTF-8"})
    @MultiLockInt(paramNames={"invoiceSettlementId"},paramClass =String.class,isCheck = true,isCollections = false)
    public Response<Object> editInvoiceInfo(@RequestBody CfInvoiceSettlement settlement) {
        UserVO user = authorizationUtil.getUserOfRequired();
        return invoiceHeaderService.editInvoiceInfo(settlement, user);

    }

    /**
     * 手动调整 延期扣款、质检扣款、红字扣减、税差、其他费用功能
     * @param cfInvoiceUpdateMoneyDto
     * @return
     */
    @MultiLockInt(paramNames={"invoiceIdsOfAssociated"},unlockEnum = OperateLockEnum.UPDATE_ADJUST_MONEY)
    @ApiOperation("手动调整 延期扣款、质检扣款、红字扣减、税差、其他费用功能")
    @PostMapping("updateInvoiceDelayMoney")
    public Response<Object> updateInvoiceDelayMoney(@RequestBody CfInvoiceUpdateMoneyDto cfInvoiceUpdateMoneyDto) {
        UserVO user = authorizationUtil.getUserOfRequired();
        invoiceHeaderService.updateInvoiceDelayMoney(cfInvoiceUpdateMoneyDto, user);
        return new Response<>(ResponseCode.SUCCESS);
    }

    /**
     * 批量账单审核/驳回
     *
     * @param modes 供应商id
     * @return 成功/失败状态码
     */
    @Login
    @ApiOperation(value = "批量账单审核/驳回", notes = "批量账单审核/驳回", produces = "application/json")
    @PutMapping(value = "auditInvoices")
    public Response auditInvoices(@RequestBody List<InvoiceSwitchBanBO> modes) {
        UserVO user = authorizationUtil.getUserOfRequired();
        invoiceHeaderService.auditInvoices(modes, user);
        return new Response<>(ResponseCode.SUCCESS);
    }

    /**
     * 账单强制驳回（账单管理增加强制驳回按钮
     *
     * 点强制驳回时，账单回滚至草稿状态
     *
     * 账单下的结算单需作废）
     *
     * @param invoiceIds 账单id
     * @return 成功/失败状态码
     */
    @Login
    @ApiOperation(value = "批量账单强制驳回", notes = "批量账单强制驳回", produces = "application/json")
    @PutMapping(value = "forcedToDismissInvoices")
    @MultiLockInt(unlockEnum = OperateLockEnum.UPDATE_ADJUST_MONEY)
    public Response forcedToDismissInvoices(@RequestBody List<Long> invoiceIds, @ApiIgnore UserVO userVO, @RequestHeader(value = Constant.AUTHORIZATION_TOKEN) String token) {
        invoiceHeaderService.forcedToDismissInvoices(invoiceIds, userVO);
        return new Response<>(ResponseCode.SUCCESS);
    }

    /**
     * 由于查询详情被其他地方应用，所有显示两位只能在这里控制两位数够死了
     * @param invoiceId
     * @return
     */
    @ApiOperation("账单详情")
    @GetMapping("invoiceDetail/{invoiceId}")
    public Response<CfInvoiceHeaderDetailVO> invoiceDetail(@PathVariable Long invoiceId) {
        CfInvoiceHeaderDetailVO detail = invoiceHeaderService.detail(invoiceId,true);
        OperateUtil.onConvertedDecimal(detail,"settlementAmount","balanceOfStatement");
        List<CfInvoiceSettlementVo> settlementList = detail.getSettlementList();
        for (CfInvoiceSettlementVo c:settlementList) {
            OperateUtil.onConvertedDecimal(c,"ClearMoney");
            OperateUtil.onConvertedDecimalBySuper(c,"invoiceSettlementMoney");
        }
        List<ChargeInVO> chargeInList = detail.getChargeInList();
        for (ChargeInVO cf:chargeInList) {
            OperateUtil.onConvertedDecimal(cf,"actualAmount","settlementAmount","postponeDeductionsTotal","taxMoney","unitAmount");
        }
        return new Response<>(ResponseCode.SUCCESS, detail);
    }

    @ApiOperation(value = "登记人下拉", notes = "登记人下拉", produces = "application/json")
    @GetMapping(value = "createNameList", produces = {"application/json;charset=UTF-8"})
    public Response<Object> createNameList() {
        return invoiceHeaderService.createNameList();
    }


    @ApiOperation("账单详情-导出")
    @GetMapping("invoiceExport")
    public Response<Object> invoiceExport(CfInvoiceHeaderListDTO dto, HttpServletResponse response) {
        UserVO user = authorizationUtil.getUserOfRequired();
        return new Response<>(ResponseCode.SUCCESS, invoiceHeaderService.invoiceExport(dto, user, response));
    }


    @ApiOperation(value = "拆分账单")
    @GetMapping(value = "splitInvoice")
    @MultiLockInt(paramClass =String.class,isCheck = true,isCollections = false)
    public synchronized Response<Object> splitInvoice(String invoiceNo) {
        return invoiceHeaderService.splitInvoice(invoiceNo);
    }

    @ApiOperation(value = "拆分账单")
    @GetMapping(value = "splitInvoice/{nos}")
    @MultiLockInt(paramClass =String.class,isCheck = true)
    public synchronized List<Response<Object>> splitInvoice(@PathVariable String[] nos) {
        List<Response<Object>> list = new ArrayList<>();
        for (String no : nos) {
           try {
               Response<Object> objectResponse = invoiceHeaderService.splitInvoice(no);
               objectResponse.setMessage( no+"  : "+objectResponse.getMessage());
               list.add(objectResponse);
           } catch (Exception e) {
               log.error("拆分账单异常", e);
               Response<Object> stringResponse = new Response<>(ResponseCode.SAVE_ERROR, e.getMessage());
               stringResponse.setMessage( no+"  : "+stringResponse.getMessage());
               list.add(stringResponse);
           }

        }
        return list;
    }

    @ApiOperation(value = "修正账单")
    @GetMapping(value = "fixAdjustInvoice")
    @MultiLockInt(paramClass =String.class,isCheck = true,isCollections = false)
    public synchronized Response<Object> fixAdjustInvoice(String invoiceNo) {
        return invoiceHeaderService.fixAdjustInvoice(invoiceNo);
    }

    @ApiOperation(value = "u8fix")
    @GetMapping(value = "u8fix")
    @MultiLockInt(paramClass =String.class,isCheck = true,isCollections = false)
    public synchronized Response<Object> mq(String no) {
        invoiceHeaderService.u8fix(no);
        return new Response<>(ResponseCode.SUCCESS);
    }

    /**
     * 更新状态
     *
     * @param taxInvoiceId
     * @param invoiceStatus
     * @return
     */
    @ApiOperation("更新状态")
    @GetMapping("/updateStates")
    public Response<Integer> updateState(@RequestParam Long taxInvoiceId, @RequestParam Integer invoiceStatus, @RequestHeader(Constant.AUTHORIZATION_TOKEN) String token) {
        return Response.success(taxInvoiceStateService.updateState(taxInvoiceId, invoiceStatus));
    }

}
