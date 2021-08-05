package com.chenfan.finance.service.helper;

import com.alibaba.fastjson.JSONObject;
import com.chenfan.common.config.BillNoConstantClassField;
import com.chenfan.common.exception.BusinessException;
import com.chenfan.common.exception.SystemState;
import com.chenfan.common.vo.Response;
import com.chenfan.common.vo.ResponseCode;
import com.chenfan.common.vo.UserVO;
import com.chenfan.finance.commons.purchaseexceptions.PurchaseException;
import com.chenfan.finance.commons.utils.BeanMapper;
import com.chenfan.finance.commons.utils.NumberToStringForChineseMoney;
import com.chenfan.finance.dao.AdvancepayApplyMapper;
import com.chenfan.finance.dao.CfPoHeaderMapper;
import com.chenfan.finance.dao.DownpaymentConfigMapper;
import com.chenfan.finance.enums.*;
import com.chenfan.finance.model.CfPoHeader;
import com.chenfan.finance.model.bo.AdvancePayBo;
import com.chenfan.finance.model.dto.AdvancepayApply;
import com.chenfan.finance.model.dto.GRole;
import com.chenfan.finance.model.vo.AdvancePayVo;
import com.chenfan.finance.model.vo.AdvanceVo;
import com.chenfan.finance.model.vo.PayApplyInfo;
import com.chenfan.finance.server.BaseInfoRemoteServer;
import com.chenfan.finance.server.BaseRemoteServer;
import com.chenfan.finance.server.PrivilegeUserServer;
import com.chenfan.finance.server.VendorCenterServer;
import com.chenfan.finance.server.remote.request.BrandFeignRequest;
import com.chenfan.finance.server.remote.vo.BrandFeignVO;
import com.chenfan.privilege.request.SUserVOReq;
import com.chenfan.privilege.response.SUserVORes;
import com.chenfan.vendor.response.VendorResModel;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author mbji
 */
@Service
public class AdvancePayHelper {


    @Autowired
    private CfPoHeaderMapper cfPoHeaderMapper;

    @Autowired
    private BaseRemoteServer baseRemoteServer;

    @Autowired
    private DownpaymentConfigMapper downpaymentConfigMapper;

    @Autowired
    private VendorCenterServer vendorCenterServer;

    @Autowired
    private PrivilegeUserServer privilegeUserServer;

    @Autowired
    private AdvancepayApplyMapper advancepayApplyMapper;

    @Autowired
    private BaseInfoRemoteServer baseInfoRemoteServer;

    private static final Logger LOGGER = LoggerFactory.getLogger(AdvancePayHelper.class);

    /**
     * 组装do
     *
     * @param bo
     * @param collect
     * @return
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws InstantiationException
     */
    public AdvancepayApply packAdvancepayApply(AdvancePayBo bo, List<GRole> collect, UserVO userVO) throws IllegalAccessException, InstantiationException {
        //根据采购单号查询含税金额总计、供应商、存货编码、采购类型
        PayApplyInfo payApplyInfo = cfPoHeaderMapper.selectPayInfo(bo.getPoId());
        if (payApplyInfo.getVendorId() != null) {
            Response<VendorResModel> info = vendorCenterServer.getInfo(payApplyInfo.getVendorId());
            if (Objects.nonNull(info) && info.getCode() == ResponseCode.SUCCESS.getCode() && Objects.nonNull(info.getObj())) {
                if (null != bo.getBank()) {
                    payApplyInfo.setBank(bo.getBank());
                } else {
                    payApplyInfo.setBank(info.getObj().getCvenBank());
                    LOGGER.info("bank-", payApplyInfo.getBank());
                }
                if (null != bo.getBankAccount()) {
                    payApplyInfo.setBankAccount(bo.getBankAccount());
                } else {
                    payApplyInfo.setBankAccount(info.getObj().getCvenAccount());
                    LOGGER.info("bankAccount-", payApplyInfo.getBankAccount());
                }
                if (null != bo.getAccname()) {
                    payApplyInfo.setAccName(bo.getAccname());
                } else {
                    payApplyInfo.setAccName(info.getObj().getAccname());
                    LOGGER.info("accName-", payApplyInfo.getAccName());
                }
                payApplyInfo.setVendorCode(info.getObj().getVendorCode());
                payApplyInfo.setVendorName(info.getObj().getVendorName());
            }
        }
        //设置采购单号，采购单id，申请部门，用户id，任务人
        AdvancepayApply advancepayApply = BeanMapper.map(bo, AdvancepayApply.class);
        //设置品牌id，品牌名称，供应商名称，供应商id，开户银行，银行账号
        BeanMapper.map(payApplyInfo, advancepayApply);
        //这里手动将采购单的采购类型赋值给预付款申请单的物料类型
        advancepayApply.setMaterialType(payApplyInfo.getPoType());
        if (bo.getPaymentConfId() != null) {
            advancepayApply.setMoney(bo.getMoney());
        } else {
            advancepayApply.setMoney(bo.getMoney());
        }
        if (bo.getFirstOrLastPay() == null) {
            if (null == bo.getPayType()) {
                AdvanceVo advanceVo = advancepayApplyMapper.selectByPoId(bo.getPoCode());
                if (Objects.nonNull(advanceVo)) {
                    advancepayApply.setPaymentType(advanceVo.getPaymentType());
                }
            } else {
                advancepayApply.setPaymentType(bo.getPayType().toString());
            }
        } else {
            advancepayApply.setPaymentType(bo.getFirstOrLastPay().toString());
        }
        //手动设置PaymentConfId
        if (null != bo.getPaymentConfId()) {
            advancepayApply.setPaymentConfId(bo.getPaymentConfId());
        }

        //设置付款金额大写
        advancepayApply.setMoneyCapital(NumberToStringForChineseMoney.getChineseMoneyStringForBigDecimal(advancepayApply.getMoney()));
        //设置角色编码
        //如果这个用户有多个理单员的角色默认取第一个理单员角色
        GRole tsRole = collect.get(0);
        advancepayApply.setRoleId(tsRole.getRoleCode());
        //设置角色编码
        advancepayApply.setFirstRoleId(tsRole.getRoleCode());
        //设置职务
        advancepayApply.setDuties(tsRole.getPositionName());
        //设置收款单位 shopType 1为直营 其他为代运营 如果是直营为：供应商名称   代运营为：供应商名称+AccName
        if (payApplyInfo.getAccName() == null) {
            payApplyInfo.setAccName("");
        }

        //查询品牌信息
        BrandFeignVO brandFeignVO = getBrandInfo(payApplyInfo.getBrandId());


        String brandType = "1";
        if (null != bo.getReceiptDepartment()) {
            advancepayApply.setReceiptDepartment(bo.getReceiptDepartment());
        } else {
            if (brandType.equals(brandFeignVO.getBrandType())) {
                advancepayApply.setReceiptDepartment(payApplyInfo.getVendorName());
            } else {
                advancepayApply.setReceiptDepartment(payApplyInfo.getVendorName() + payApplyInfo.getAccName());
            }
        }
        //}
        advancepayApply.setAdvancePayCode(baseRemoteServer.getPayNum(BillNoConstantClassField.ADVANCEPAY_APPLY_BUSINESS).getObj().toString());
        advancepayApply.setCreateBy(userVO.getUserId());
        advancepayApply.setCreateName(userVO.getRealName());
        advancepayApply.setTaskPerson(userVO.getRealName());
        advancepayApply.setAccname(payApplyInfo.getAccName());
        return advancepayApply;
    }

    private BrandFeignVO getBrandInfo(Integer brandId) {
        BrandFeignRequest brandFeignRequest = new BrandFeignRequest();
        List<Integer> brandIdList = new ArrayList<>();
        brandIdList.add(brandId);
        brandFeignRequest.setBrandIdList(brandIdList);
        Response<List<BrandFeignVO>> response = baseInfoRemoteServer.getBrandByBrandIdList(brandFeignRequest);
        if (HttpStateEnum.OK.getCode() != response.getCode()) {
            LOGGER.error("调用baseinfo服务查询品牌信息报错,参数{}", JSONObject.toJSONString(brandFeignRequest));
            throw new BusinessException(response.getCode(), response.getMessage());
        }

        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(response.getObj())) {
            return response.getObj().get(0);
        }
        throw new BusinessException(SystemState.BUSINESS_ERROR.code(), "品牌不存在,品牌ID" + brandId);
    }

    /**
     * 提交
     *
     * @param roleCodes
     * @param bo
     * @param advancePayVo
     */
    public AdvancepayApply submit(List<String> roleCodes, AdvancePayBo bo, AdvancePayVo advancePayVo, UserVO userVO) throws PurchaseException {
        //只能操作已审核的单子
        if (!AdvencePayEnum.AUDIT.getCode().equals(advancePayVo.getState())) {
            throw new PurchaseException(ErrorMessageEnum.NO_POWER.getMsg());
        }
        List<RoleEnum> roleEnums = getRoleEnum(roleCodes, advancePayVo);
        if (CollectionUtils.isEmpty(roleEnums)) {
            throw new PurchaseException(ErrorMessageEnum.ONLY_TASKMAN.getMsg());
        }
        //返回打包后的dto --设置状态为已提交，角色id为当前登录人rolecode， 付款单号 ，设置任务人
        AdvancepayApply advancepayApply = getAdvancepayApply(bo.getAdvancePayId(), RoleEnum.SUPPLYCHAIN_INTERN);
        advancepayApply.setSupplychainInternName(userVO.getRealName());
        advancepayApply.setSupplychainInternDate(new Date());
        return advancepayApply;
    }

    /**
     * 已打款
     *
     * @param roleCodes
     * @param bo
     * @param advancePayVo
     */
    public AdvancepayApply paid(List<String> roleCodes, AdvancePayBo bo, AdvancePayVo advancePayVo, UserVO userVO) throws PurchaseException {
        //只能操作待打款的单子
        if (!AdvencePayEnum.COMPLETE.getCode().equals(advancePayVo.getState())) {
            throw new PurchaseException(ErrorMessageEnum.NO_POWER.getMsg());
        }
        List<RoleEnum> roleEnums = getRoleEnum(roleCodes, advancePayVo);
        if (CollectionUtils.isEmpty(roleEnums)) {
            throw new PurchaseException(ErrorMessageEnum.ONLY_TASKMAN.getMsg());
        }
        //返回打包后的dto --设置状态为已提交，角色id为当前登录人rolecode， 付款单号 ，设置任务人
        AdvancepayApply advancepayApply = getAdvancepayApply(bo.getAdvancePayId(), RoleEnum.CASHIER);
        advancepayApply.setCashier(userVO.getRealName());
        advancepayApply.setCashierDate(new Date());
        advancepayApply.setImgUrls(bo.getImgUrls());
        //最后一步清空任务人
        advancepayApply.setTaskPerson("");
        return advancepayApply;
    }

    /**
     * 打包修改的dto
     *
     * @param payId    预付款单id
     * @param roleEnum 当前登录人角色code
     * @return
     */
    public AdvancepayApply getAdvancepayApply(Integer payId, RoleEnum roleEnum) {
        if (roleEnum.getNextTaskPerson().length != 0) {
            Response<List<SUserVORes>> listResponse = privilegeUserServer.listUsers(SUserVOReq.builder().roleCode(String.valueOf(roleEnum.getNextTaskPerson()[0])).build());
            List<String> taskman = Lists.newArrayList();
            if (Objects.nonNull(listResponse) && listResponse.getCode() == ResponseCode.SUCCESS.getCode()) {
                List<SUserVORes> suserVoResList = listResponse.getObj();
                if (!CollectionUtils.isEmpty(suserVoResList)) {
                    taskman = suserVoResList.stream().map(SUserVORes::getUsername).collect(Collectors.toList());
                }
            }
            if (!CollectionUtils.isEmpty(taskman)) {
                return new AdvancepayApply(payId, roleEnum.getCode(), roleEnum.getAgreeState().getCode(), StringUtils.join(taskman, ","));
            }
        }
        return new AdvancepayApply(payId, roleEnum.getCode(), roleEnum.getAgreeState().getCode(), null);
    }

    /**
     * 打包修改的dto
     *
     * @param payId    预付款单id
     * @param roleEnum 当前登录人角色code
     * @return
     */
    public AdvancepayApply getAdvancepayApply(Integer payId, RoleEnum roleEnum, Integer taskPerson) {
        if (roleEnum.getNextTaskPerson().length != 0) {
            Response<List<SUserVORes>> listResponse = privilegeUserServer.listUsers(SUserVOReq.builder().roleCode(String.valueOf(taskPerson)).build());
            List<String> taskman = Lists.newArrayList();
            if (Objects.nonNull(listResponse) && listResponse.getCode() == ResponseCode.SUCCESS.getCode()) {
                List<SUserVORes> sUserVoResList = listResponse.getObj();
                if (!CollectionUtils.isEmpty(sUserVoResList)) {
                    taskman = sUserVoResList.stream().map(SUserVORes::getUsername).collect(Collectors.toList());
                }
            }
            if (!CollectionUtils.isEmpty(taskman)) {
                return new AdvancepayApply(payId, roleEnum.getCode(), roleEnum.getAgreeState().getCode(), StringUtils.join(taskman, ","));
            }
        }
        return new AdvancepayApply(payId, roleEnum.getCode(), roleEnum.getAgreeState().getCode(), null);
    }

    /**
     * 付款
     *
     * @param roleCodes
     * @param bo
     * @param advancePayVo
     */
    public AdvancepayApply financeAudit(List<String> roleCodes, AdvancePayBo bo, AdvancePayVo advancePayVo, UserVO userVO) throws PurchaseException {
        //只能操作已提交的单子
        if (!AdvencePayEnum.SUBMIT.getCode().equals(advancePayVo.getState())) {
            throw new PurchaseException(ErrorMessageEnum.NO_POWER.getMsg());
        }
        List<RoleEnum> roleEnums = getRoleEnum(roleCodes, advancePayVo);
        if (CollectionUtils.isEmpty(roleEnums)) {
            throw new PurchaseException(ErrorMessageEnum.ONLY_TASKMAN.getMsg());
        }
        //返回打包后的dto --设置状态为已付款，角色id为当前登录人rolecode， 付款单号 ，设置任务人
        AdvancepayApply advancepayApply = getAdvancepayApply(bo.getAdvancePayId(), RoleEnum.FINANCE);
        advancepayApply.setFinaceName(userVO.getRealName());
        advancepayApply.setFinaceDate(new Date());
        return advancepayApply;
    }

    /**
     * 复核
     *
     * @param roleCodes
     * @param bo
     * @param advancePayVo
     */
    public AdvancepayApply reckeck(List<String> roleCodes, AdvancePayBo bo, AdvancePayVo advancePayVo, UserVO userVO) throws PurchaseException {
        //只能操作已付款的单子
        if (!AdvencePayEnum.FINANCE_AUDIT.getCode().equals(advancePayVo.getState())) {
            throw new PurchaseException(ErrorMessageEnum.NO_POWER.getMsg());
        }
        List<RoleEnum> roleEnums = getRoleEnum(roleCodes, advancePayVo);
        if (CollectionUtils.isEmpty(roleEnums)) {
            throw new PurchaseException(ErrorMessageEnum.ONLY_TASKMAN.getMsg());
        }
        //返回打包后的dto --设置状态为已付款，角色id为当前登录人rolecode， 付款单号 ，设置任务人
        AdvancepayApply advancepayApply = getAdvancepayApply(bo.getAdvancePayId(), RoleEnum.FINANCE_GM);
        advancepayApply.setFinaceGMName(userVO.getRealName());
        advancepayApply.setFinaceGMDate(new Date());
        return advancepayApply;
    }

    /**
     * 审核
     *
     * @param roleCodes
     * @param bo
     * @param userVO
     */
    public AdvancepayApply audit(List<String> roleCodes, AdvancePayBo bo, AdvancePayVo advancePayVo, UserVO userVO) throws PurchaseException {
        if (!AdvencePayEnum.CONFIRM.getCode().equals(advancePayVo.getState())) {
            throw new PurchaseException(ErrorMessageEnum.NO_POWER.getMsg());
        }
        //对应的状态只能由对应的任务人来操作，否则就提示失败
        List<RoleEnum> roleEnums = getRoleEnum(roleCodes, advancePayVo);
        if (CollectionUtils.isEmpty(roleEnums)) {
            throw new PurchaseException(ErrorMessageEnum.ONLY_TASKMAN.getMsg());
        }
        //默认取第一个角色
        RoleEnum roleEnum = roleEnums.get(0);
        //其他的角色都是只有一个或者没有任务人
        AdvancepayApply advancepayApply = getAdvancepayApply(bo.getAdvancePayId(), roleEnum);
        advancepayApply.setVerifyDate(new Date());
        advancepayApply.setVerifyName(userVO.getRealName());
        setAuditPerson(userVO, roleEnum, advancepayApply);
        return advancepayApply;
    }

    public void setAuditPerson(UserVO userVO, RoleEnum roleEnum, AdvancepayApply advancepayApply) {
        switch (roleEnum) {
            case FIRST_GM:
                advancepayApply.setConfirmName(userVO.getRealName());
                advancepayApply.setConfirmDate(new Date());
                break;
            case FINANCE:
                advancepayApply.setFinaceName(userVO.getRealName());
                advancepayApply.setFinaceDate(new Date());
                break;
            default:
                break;
        }
    }

    /**
     * 根据当前单据上个任务人角色code和当前单据状态
     * 拿到当前登录用户适应的角色
     *
     * @param roleCodes
     * @param advancePayVo
     * @return
     */
    public List<RoleEnum> getRoleEnum(List<String> roleCodes, AdvancePayVo advancePayVo) {
        //根据单据状态和上个操作人拿到可操作此条单据的角色集合
        List<RoleEnum> enumList = RoleEnum.getEnumsByState(AdvencePayEnum.getEnumByCodeAndState(advancePayVo.getState()), advancePayVo.getRoleId());
        RoleEnum roleEnum = null;
        List<RoleEnum> roleEnums = new ArrayList<>();
        //用可操作此条单据的角色的集合跟当前用户拥有角色的集合取交集
        for (RoleEnum anEnum : enumList) {
            for (String roleCode : roleCodes) {
                if (anEnum.equals(RoleEnum.getEnumByCode(roleCode))) {
                    roleEnums.add(anEnum);
                }
            }
        }
        return roleEnums;
    }

    /**
     * 驳回
     *
     * @param advancePayVo
     * @return
     */
    public AdvancepayApply reject(List<String> roleCodes, AdvancePayVo advancePayVo) throws PurchaseException {
        //对应的状态只能由对应的任务人来操作，否则就提示失败
        List<RoleEnum> roleEnums = getRoleEnum(roleCodes, advancePayVo);
        if (CollectionUtils.isEmpty(roleEnums)) {
            throw new PurchaseException(ErrorMessageEnum.ONLY_TASKMAN.getMsg());
        }
        AdvancepayApply advancepayApply = new AdvancepayApply(advancePayVo.getAdvancePayId(),
                advancePayVo.getFirstRoleId(), AdvencePayEnum.NOT_CONFIRM.getCode(),
                advancePayVo.getCreateName());
        advancepayApply.setApplyConfirmName(null);
        advancepayApply.setApplyConfirmDate(null);
        advancepayApply.setConfirmName(null);
        advancepayApply.setConfirmDate(null);
        advancepayApply.setSupplychainInternDate(null);
        advancepayApply.setSupplychainInternName(null);
        advancepayApply.setFinaceName(null);
        advancepayApply.setFinaceDate(null);
        advancepayApply.setFinaceGMName(null);
        advancepayApply.setFinaceGMDate(null);
        advancepayApply.setCooOrHelperName(null);
        advancepayApply.setCooOrHelperDate(null);
        advancepayApply.setImgUrls(null);
        return advancepayApply;
    }

    /**
     * 确认操作
     *
     * @param roleCodes
     * @param bo
     * @param advancePayVo
     */
    public AdvancepayApply confirm(List<String> roleCodes, AdvancePayBo bo, AdvancePayVo advancePayVo, UserVO userVO) throws PurchaseException {
        if (!AdvencePayEnum.NOT_CONFIRM.getCode().equals(advancePayVo.getState())) {
            throw new PurchaseException(ErrorMessageEnum.NO_POWER.getMsg());
        }
        //对应的状态只能由对应的任务人来操作，否则就提示失败
        List<RoleEnum> roleEnums = getRoleEnum(roleCodes, advancePayVo);
        if (CollectionUtils.isEmpty(roleEnums)) {
            throw new PurchaseException(ErrorMessageEnum.ONLY_TASKMAN.getMsg());
        }
        //默认取第一个角色
        RoleEnum roleEnum = roleEnums.get(0);
        AdvancepayApply advancepayApply = getAdvancepayApply(bo.getAdvancePayId(), roleEnum);
        advancepayApply.setApplyConfirmDate(new Date());
        advancepayApply.setApplyConfirmName(userVO.getRealName());
        return advancepayApply;
    }

    /**
     * 关闭操作
     *
     * @param roleCodes
     * @param bo
     * @param advancePayVo
     */
    public AdvancepayApply close(List<String> roleCodes, AdvancePayBo bo, AdvancePayVo advancePayVo) throws PurchaseException {
        CfPoHeader poMain = cfPoHeaderMapper.selectByPrimaryKey(advancePayVo.getPoId());
        String zero = "0";
        String one = "1";
        if (poMain.getRetainage() != null && zero.equals(advancePayVo.getPaymentType())) {
            throw new PurchaseException("该预付款已经申请了尾款，如需关闭请先关闭对应的尾款单据");
        }
        //对应的状态只能由对应的任务人来操作，否则就提示失败
        List<RoleEnum> roleEnums = getRoleEnum(roleCodes, advancePayVo);
        if (CollectionUtils.isEmpty(roleEnums)) {
            throw new PurchaseException(ErrorMessageEnum.ONLY_TASKMAN.getMsg());
        }
        //清除采购单里面的订金，或尾款
        boolean checkType = zero.equals(advancePayVo.getPaymentType()) || one.equals(advancePayVo.getPaymentType());
        if (advancePayVo.getMoney() != null && checkType) {

            int update = cfPoHeaderMapper.updateByPrimaryKeySelective(new CfPoHeader(advancePayVo.getPoId(), new BigDecimal(-1), new BigDecimal(-1), new BigDecimal(-1), PayTypeEnum.getEnumByValue(Integer.parseInt(advancePayVo.getPaymentType()))));
            if (update == 0) {
                throw new PurchaseException(ErrorMessageEnum.ID_NOT_FOUND.getMsg());
            }
        }
        if (Integer.valueOf(advancePayVo.getPaymentType()) > 1) {
            int update = cfPoHeaderMapper.updateHirePurchase(advancePayVo.getPoId(), advancePayVo.getMoney(), 0);
            if (update == 0) {
                throw new PurchaseException(ErrorMessageEnum.ID_NOT_FOUND.getMsg());
            }
        }
        //清空任务人
        return new AdvancepayApply(bo.getAdvancePayId(), roleCodes.get(0), AdvencePayEnum.CLOSE.getCode(), "");
    }
}
