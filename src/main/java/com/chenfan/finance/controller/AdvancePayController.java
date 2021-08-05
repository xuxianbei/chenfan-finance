package com.chenfan.finance.controller;

import com.alibaba.fastjson.JSONObject;
import com.baidu.unbiz.fluentvalidator.ComplexResult;
import com.chenfan.common.config.Constant;
import com.chenfan.common.vo.Response;
import com.chenfan.common.vo.ResponseCode;
import com.chenfan.common.vo.UserVO;
import com.chenfan.finance.commons.annotation.MultiLockInt;
import com.chenfan.finance.enums.OperateLockEnum;
import com.chenfan.finance.model.bo.CfPoHeaderBo;
import com.chenfan.finance.model.vo.AdvancePayToNewFinanceVO;
import com.chenfan.finance.model.vo.CfPoHeaderVo;
import com.chenfan.finance.service.CfPoHeaderService;
import com.chenfan.finance.utils.AuthorizationUtil;
import com.chenfan.privilege.common.config.SearchAuthority;
import com.chenfan.finance.commons.purchaseexceptions.PurchaseException;
import com.chenfan.finance.commons.utils.HibernateValidator;
import com.chenfan.finance.commons.utils.poiutil.ExportExcelUtils;
import com.chenfan.finance.commons.utils.validategroup.Audit;
import com.chenfan.finance.commons.utils.validategroup.Create;
import com.chenfan.finance.commons.utils.validategroup.Update;
import com.chenfan.finance.enums.ErrorMessageEnum;
import com.chenfan.finance.model.bo.AdvancePayBo;
import com.chenfan.finance.model.vo.AdvancepayApplyVo;
import com.chenfan.finance.service.AdvancePayService;
import feign.Param;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 预付款订单管理
 * @author mbji
 */
@RestController
@RequestMapping("advancePay/")
@Api(tags ="预付款订单管理" )
public class AdvancePayController {

    private static Logger logger = LoggerFactory.getLogger(AdvancePayController.class);
    @Autowired
    private AdvancePayService advancePayService;

    @Autowired
    private CfPoHeaderService cfPoHeaderService;

    @Resource
    private AuthorizationUtil authorizationUtil;

    /**
     * 选择采购单
     * @param bo
     * @return
     */
    @ApiOperation(value = "选择采购单", notes = "选择采购单", produces = "application/json")
    @GetMapping(value = "purchaseOrders")
    public Response<List<CfPoHeaderVo>> purchaseOrders( @SearchAuthority CfPoHeaderBo bo){
        logger.info("选择采购单>>");
        UserVO user = authorizationUtil.getUser();
        try {
            return new Response(ResponseCode.SUCCESS, cfPoHeaderService.selectPurchaseOrder(bo,user));
        } catch (Exception e) {
            return new Response<>(ResponseCode.NOT_FIND_DATABASE_CONN);
        }
    }

    /**
     * 预付款申请
     * @param bo
     * @return
     */
    @MultiLockInt(paramNames={"poId"},unlockEnum = OperateLockEnum.CHARGE_INVOICE_CREATE,isCheck = true,isCollections = false)
    @ApiOperation(value = "预付款申请", notes = "预付款申请", produces = "application/json")
    @PostMapping(value = "apply", produces = {"application/json;charset=UTF-8"})
    public Response advancePayApply( @RequestBody AdvancePayBo bo) {
        UserVO user = authorizationUtil.getUser();
        ComplexResult complexResult = HibernateValidator.checkParamters(bo, Create.class);
        if (!complexResult.isSuccess()) {
            return new Response(Constant.ERROR_PARAM_CODE, HibernateValidator.errorMsg(complexResult));
        }
        try {
                if(null != user.getCompanyId()){
                    bo.setCompanyId(user.getCompanyId());
                }
                advancePayService.advancePayApply(bo,user);
        } catch (Exception pe) {
            logger.error(ErrorMessageEnum.APPLY_ERROR.getMsg(), pe);
            return new Response(pe);
        }
        return new Response(ResponseCode.SUCCESS);
    }

    @MultiLockInt(paramNames={"advancePayId"},unlockEnum = OperateLockEnum.CHARGE_INVOICE_CREATE,isCheck = true,isCollections = false)
    @ApiOperation(value = "预付款修改", notes = "预付款修改", produces = "application/json")
    @PostMapping(value = "update", produces = {"application/json;charset=UTF-8"})
    public Response updateAdvancePay( @RequestBody AdvancePayBo bo) {
        UserVO user = authorizationUtil.getUser();
        ComplexResult complexResult = HibernateValidator.checkParamters(bo, Update.class);
        if (!complexResult.isSuccess()) {
            return new Response(Constant.ERROR_PARAM_CODE, HibernateValidator.errorMsg(complexResult));
        }
        try {
            advancePayService.updateAdvancePay(bo, user);
            return new Response(ResponseCode.SUCCESS);
        } catch (Exception e) {
            logger.error(ErrorMessageEnum.UPDATE_ERROR.getMsg(), e);
            return new Response(e);
        }
    }

    @ApiOperation(value = "预付款列表", notes = "预付款列表", produces = "application/json")
    @GetMapping(value = "list")
    public Response advancePayList(@SearchAuthority AdvancePayBo bo) {
        setPaymentType(bo);
        return new Response(ResponseCode.SUCCESS, advancePayService.advancePayList(bo));
    }

    @ApiOperation(value = "预付款详情", notes = "预付款详情", produces = "application/json")
    @GetMapping(value = "info/{payId}", produces = {"application/json;charset=UTF-8"})
    public Response advancePayInfo(@PathVariable("payId") Integer payId) {
        UserVO user = authorizationUtil.getUser();
        return new Response(ResponseCode.SUCCESS, advancePayService.advancePayInfo(payId,user));
    }

    @MultiLockInt(paramNames={"advancePayId"},unlockEnum = OperateLockEnum.CHARGE_INVOICE_CREATE,isCheck = true,isCollections = false)
    @ApiOperation(value = "预付款确认、关闭、审核、提交、付款、复核、驳回操作",
            notes = "预付款确认、关闭、审核、提交、付款、复核、驳回操作", produces = "application/json")
    @PostMapping(value = "auditOrClose", produces = {"application/json;charset=UTF-8"})
    public Response auditOrClose(@RequestBody AdvancePayBo bo) {
        UserVO user = authorizationUtil.getUser();
        ComplexResult complexResult = HibernateValidator.checkParamters(bo, Audit.class);
        if (!complexResult.isSuccess()) {
            return new Response(Constant.ERROR_PARAM_CODE, HibernateValidator.errorMsg(complexResult));
        }
        try {
            advancePayService.auditOrClose(bo,user);
        } catch (PurchaseException e) {
            logger.error(ErrorMessageEnum.FAIL.getMsg(), e);
            return new Response(e);
        }catch (Exception e) {
            logger.error(ErrorMessageEnum.FAIL.getMsg(), e);
            return new Response(e);
        }
        return new Response(ResponseCode.SUCCESS);
    }

    /**
     * 查询订金配置表
     *
     * @return
     */
    @ApiOperation(value = "查询订金配置表", notes = "查询订金配置表", produces = "application/json")
    @GetMapping(value = "paymentConfigList", produces = {"application/json;charset=UTF-8"})
    public Response paymentConfigList() {
        return new Response(ResponseCode.SUCCESS, advancePayService.paymentConfigList());
    }

    @ApiOperation(value = "导出excel", notes = "导出excel")
    @GetMapping(value = "exportExcel")
    public void exportExcel(@RequestHeader(Constant.AUTHORIZATION_TOKEN) String token,@SearchAuthority AdvancePayBo bo, HttpServletResponse response) {
        setPaymentType(bo);
        List<AdvancepayApplyVo> advancePayVos = advancePayService.exportExcel(bo);
        String title = "预付款申请表";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String fileName = title + sdf.format(new Date()) + ".xlsx";
        ExportExcelUtils.exportExcel(advancePayVos, title, title, AdvancepayApplyVo.class, fileName, response);
    }

    public void setPaymentType(AdvancePayBo bo) {
        if (!StringUtils.isEmpty(bo.getPaymentTypeOption())) {
            String[] option = bo.getPaymentTypeOption().split("-");
           if(option.length>1){
               bo.setProportionName(option[0]);
               bo.setProportion(new BigDecimal(option[1]));
               bo.setPaymentType(option[2]);
           }else {
               bo.setPayType(Integer.valueOf(bo.getPaymentTypeOption()));
           }
        }
    }


    /**
     * 根据定金比例获取定金金额，尾款金额
     * @param poId
     * @param paymentConfId
     * @return
     */
    @ApiOperation(value = "获取金额", notes = "获取金额", produces = "application/json")
    @GetMapping(value = "getAmountOfMoney", produces = {"application/json;charset=UTF-8"})
    public Response getAmountOfMoney(@Param("poId") Long poId, @Param("paymentConfId") Integer paymentConfId,
                                     @Param("payValue")BigDecimal payValue, @Param("payType")String payType,
                                     @Param("firstOrLastPay") Integer firstOrLastPay){
        return new Response(ResponseCode.SUCCESS,cfPoHeaderService.getAmountOfMoney(poId,paymentConfId,payValue,payType,firstOrLastPay));
    }

    /**
     * 优化采购单预付款申请
     * @param poCode
     * @return
     */
    @ApiOperation(value = "优化采购单预付款申请", notes = "优化采购单预付款申请", produces = "application/json")
    @GetMapping(value = "advance/{poCode}", produces = {"application/json;charset=UTF-8"})
    public Response<BigDecimal> advance(@PathVariable("poCode") String poCode) {
        return new Response<>(ResponseCode.SUCCESS, cfPoHeaderService.queryAdvance(poCode));
    }

    /**
     * 根据poCode判断是否有预付款订单
     * @param poCode
     * @return
     */
    @ApiOperation(value = "预付款申请新建列表", notes = "预付款申请新建列表", produces = "application/json")
    @GetMapping(value = "createList", produces = {"application/json;charset=UTF-8"})
    public Response createList(@RequestHeader(Constant.AUTHORIZATION_TOKEN) String token, @Param("poCode") String poCode){
        UserVO user = authorizationUtil.getUser();
        return new Response(ResponseCode.SUCCESS,advancePayService.createList(poCode,user));
    }

    /**
     * 老采购单推送过来的数据生成预付款申请单
     * @param bo
     * @return
     */
    @ApiOperation(value = "老采购生成新预付款", notes = "老采购生成新预付款", produces = "application/json")
    @PostMapping(value = "advancePayToFinance", produces = {"application/json;charset=UTF-8"})
    public Response advancePayToFinance(@RequestBody AdvancePayToNewFinanceVO bo) {
        try {
            synchronized (this){
                advancePayService.createFromPurchase(bo);
            }
        }  catch (PurchaseException pe) {
            logger.error(ErrorMessageEnum.APPLY_ERROR.getMsg(), pe);
            return new Response(pe);
        } catch (Exception e) {
            logger.error(ErrorMessageEnum.APPLY_ERROR.getMsg(), e);
            return new Response(e);
        }
        return new Response(ResponseCode.SUCCESS);
    }

    @ApiOperation(value = "根据采购单code获取生成到财务的预付款", notes = "根据采购单code获取生成到财务的预付款", produces = "application/json")
    @GetMapping(value = "getAdvanceByPoCode", produces = {"application/json;charset=UTF-8"})
    public Response<Integer> getAdvanceByPoCode(@Param("poCode") String poCode){
        return new Response<Integer>(ResponseCode.SUCCESS,advancePayService.getAdvanceByPoCode(poCode));
    }

    @ApiOperation(value = "根据采购单ids查看是否已经生成了预付款单", notes = "根据采购单code获取生成到财务的预付款", produces = "application/json")
    @PostMapping(value = "checkIfExistAdvance", produces = {"application/json;charset=UTF-8"})
    public Response<Integer> checkIfExistAdvance(@RequestBody AdvancePayBo bo){
        return new Response<Integer>(ResponseCode.SUCCESS,advancePayService.checkIfExistAdvance(bo));
    }

    @ApiOperation(value = "查看是否已经存在定金比为百分之百的采购单", notes = "查看是否已经存在定金比为百分之百的采购单", produces = "application/json")
    @GetMapping(value = "checkIfExistAdvanceByPoId", produces = {"application/json;charset=UTF-8"})
    public Response<Integer> checkIfExistAdvanceByPoId(@Param("poId")Integer poId,@Param("firstOrLastPay") Integer firstOrLastPay){
        return new Response<Integer>(ResponseCode.SUCCESS,advancePayService.checkIfExistAdvanceByPoId(poId,firstOrLastPay));
    }
}
