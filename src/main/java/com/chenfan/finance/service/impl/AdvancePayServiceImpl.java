package com.chenfan.finance.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.chenfan.common.config.TaskTypeEnum;
import com.chenfan.common.utils.CopyUtil;
import com.chenfan.common.vo.Response;
import com.chenfan.common.vo.ResponseCode;
import com.chenfan.common.vo.TaskFlowVo;
import com.chenfan.common.vo.UserVO;
import com.chenfan.finance.commons.purchaseexceptions.PurchaseException;
import com.chenfan.finance.commons.utils.BeanMapper;
import com.chenfan.finance.commons.utils.NumberToStringForChineseMoney;
import com.chenfan.finance.dao.AdvancepayApplyMapper;
import com.chenfan.finance.dao.CfPoHeaderMapper;
import com.chenfan.finance.dao.DownpaymentConfigMapper;
import com.chenfan.finance.enums.ErrorMessageEnum;
import com.chenfan.finance.enums.PayApplyOperationEnum;
import com.chenfan.finance.enums.PayTypeEnum;
import com.chenfan.finance.enums.RoleEnum;
import com.chenfan.finance.model.Advancepay;
import com.chenfan.finance.model.CfBankAndCash;
import com.chenfan.finance.model.CfPoHeader;
import com.chenfan.finance.model.bo.AdvancePayBo;
import com.chenfan.finance.model.dto.*;
import com.chenfan.finance.model.vo.*;
import com.chenfan.finance.server.*;
import com.chenfan.finance.server.remote.vo.BrandFeignVO;
import com.chenfan.finance.service.AdvancePayService;
import com.chenfan.finance.service.CfBankAndCashService;
import com.chenfan.finance.service.helper.AdvancePayHelper;
import com.chenfan.privilege.request.SRoleVOReq;
import com.chenfan.privilege.request.SUserVOReq;
import com.chenfan.privilege.response.SRoleVORes;
import com.chenfan.privilege.response.SUserVORes;
import com.chenfan.vendor.response.VendorResModel;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.util.StringUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang.StringUtils;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author mbji
 */
@Service
@Slf4j
public class AdvancePayServiceImpl implements AdvancePayService {

    @Autowired
    private AdvancepayApplyMapper advancepayApplyMapper;

    @Autowired
    private AdvancePayHelper advancePayHelper;

    @Autowired
    private CfPoHeaderMapper cfPoHeaderMapper;

    @Autowired
    private DownpaymentConfigMapper downpaymentConfigMapper;

    @Autowired
    private TaskRemoteServer taskRemoteServer;

    @Autowired
    private VendorCenterServer vendorCenterServer;

    @Autowired
    private PrivilegeUserServer privilegeUserServer;

    @Autowired
    private BaseRemoteServer baseRemoteServer;
    @Autowired
    private CfBankAndCashService cfBankAndCashService;

    @Autowired
    private PurchaseRemoteServer purchaseRemoteServer;
    @Autowired
    private BaseInfoRemoteServer baseInfoRemoteServer;
    /**
     * 采购单号
     */
    @Override
    public List<CfPoHeader> selectAllCode() {
        return cfPoHeaderMapper.selectAllCode();
    }

    /**
     * 预付款申请
     *
     * @param bo
     */
    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.REPEATABLE_READ)
    public void advancePayApply(AdvancePayBo bo, UserVO user)
            throws PurchaseException, InstantiationException, IllegalAccessException {
        // 先判断有没有申请过预付款申请/尾款申请, 采购单定金比为100%，不能再进行预付款申请,采购单核算状态为已核算，不能再进行预付款申请
        CfPoHeader poMain = cfPoHeaderMapper.selectByPrimaryKey(bo.getPoId());
        // 判断是不是业务一组组长在操作，不是业务一组组长不能操作
        Response<List<SRoleVORes>> listResponse = privilegeUserServer.listRoles(SRoleVOReq.builder().userId(user.getUserId()).build());
        List<GRole> collect = Lists.newArrayList();
        if (Objects.nonNull(listResponse) && listResponse.getCode() == ResponseCode.SUCCESS.getCode()) {
            List<SRoleVORes> roleVoResList = listResponse.getObj();
            if (!CollectionUtils.isEmpty(roleVoResList)) {
                collect = CopyUtil.copyListProperties(roleVoResList, GRole::new).stream().filter(x -> x.getRoleCode().contains("224")).collect(Collectors.toList());
            }
        }
        if (collect.size() == 0) {
            throw new PurchaseException(ErrorMessageEnum.ONLY_ORDER_MANAGEER.getMsg());
        }

        if (null == bo.getPaymentConfId() && null == bo.getFirstPayment()) {
            throw new PurchaseException(ErrorMessageEnum.PAYMENT_ERROR.getMsg());
        }
        // 打包组装付款申请单dto数据
        AdvancepayApply advancepayApply = advancePayHelper.packAdvancepayApply(bo, collect, user);

        //判断是比例付款还是周期付款
        String paymentType;
        if (null != bo.getFirstOrLastPay()) {
            if (bo.getFirstOrLastPay() == 0) {
                paymentType = "0";
            } else {
                paymentType = "1";
            }
        } else {
            paymentType = bo.getPaymentType();
        }
        //查询advancePayId是否存在
        Integer advancePayId = advancepayApplyMapper.findAdvancePayId(paymentType, bo.getPoCode(), bo.getMaterialType());
        //如果不为空，添加一条新的数据
        if (null == advancePayId) {
            // 插入数据库
            int insert = advancepayApplyMapper.insertSelective(advancepayApply);

            if (insert == 0) {
                throw new PurchaseException(ErrorMessageEnum.APPLY_ERROR.getMsg());
            }
        } else {
            Advancepay advancepay = BeanMapper.map(advancepayApply, Advancepay.class);
            advancepay.setAdvancePayId(advancePayId);
            advancepay.setUpdateBy(user.getUserId());
            advancepay.setUpdateName(user.getRealName());
            advancepay.setUpdateDate(new Date());
            int updateSelective = advancepayApplyMapper.updateAdvancepayDetails(advancepay);

            if (updateSelective == 0) {
                throw new PurchaseException(ErrorMessageEnum.APPLY_ERROR.getMsg());
            }
        }

        if (bo.getFirstOrLastPay() != null) {
            //如果是定金，取传过来的定金金额；尾款取尾款金额
            BigDecimal money = new BigDecimal(0);
            if (bo.getFirstOrLastPay() == 0) {
                money = bo.getBargain();
            } else if (bo.getFirstOrLastPay() == 1) {
                money = bo.getRetainage();
            }
            // 更新采购单订金或者尾款
            int update =
                    cfPoHeaderMapper.updateByPrimaryKeySelective(
                            new CfPoHeader(
                                    bo.getPoId(),
                                    //advancepayApply.getMoney(),
                                    money,
                                    bo.getBargainRatio(),
                                    bo.getRetainageRatio(),
                                    PayTypeEnum.getEnumByValue(bo.getFirstOrLastPay())));
            if (update == 0) {
                throw new PurchaseException(ErrorMessageEnum.APPLY_ERROR.getMsg());
            }
        }
        if (bo.getPayType() != null && bo.getPayType() > 1) {
            // 更新采购单分期付款
            int update =
                    cfPoHeaderMapper.updateHirePurchase(bo.getPoId(), bo.getPayValue(), 1);
            if (update == 0) {
                throw new PurchaseException(ErrorMessageEnum.APPLY_ERROR.getMsg());
            }
        }
       this.updatePurchasePoHeader(bo.getPoId());
    }

    public void updatePurchasePoHeader(Long poId){

            CfPoHeader cfPoHeader = cfPoHeaderMapper.selectByPrimaryKey(poId);
            PoHeader poHeader = new PoHeader();
            poHeader.setPoId(poId);
            poHeader.setBargain(Objects.isNull(cfPoHeader.getBargain())?new BigDecimal(-1):cfPoHeader.getBargain());
            poHeader.setBargainRatio(Objects.isNull(cfPoHeader.getBargainRatio())?new BigDecimal(-1):cfPoHeader.getBargainRatio());
            poHeader.setHirePurchase(Objects.isNull(cfPoHeader.getHirePurchase())?BigDecimal.ZERO:cfPoHeader.getHirePurchase());
            poHeader.setRetainage(Objects.isNull(cfPoHeader.getRetainage())?new BigDecimal(-1):cfPoHeader.getRetainage());
            poHeader.setRetainageRatio(Objects.isNull(cfPoHeader.getRetainageRatio())?new BigDecimal(-1):cfPoHeader.getRetainageRatio());
            purchaseRemoteServer.updatePoHeaderFromFinance(poHeader);


    }
    /**
     * 修改付款申请
     *
     * @param bo
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAdvancePay(AdvancePayBo bo, UserVO user)
            throws InstantiationException, IllegalAccessException, PurchaseException {
        Response<List<SRoleVORes>> listResponse = privilegeUserServer.listRoles(SRoleVOReq.builder().userId(user.getUserId()).build());
        List<String> roleCodes = Lists.newArrayList();
        if (Objects.nonNull(listResponse) && listResponse.getCode() == ResponseCode.SUCCESS.getCode()) {
            List<SRoleVORes> sRoleVoResList = listResponse.getObj();
            if (!CollectionUtils.isEmpty(sRoleVoResList)) {
                roleCodes = CopyUtil.copyListProperties(sRoleVoResList, GRole::new).stream()
                        .map(GRole::getRoleCode).collect(Collectors.toList());
            }
        }
        if (CollectionUtils.isEmpty(roleCodes)) {
            throw new PurchaseException(ErrorMessageEnum.NO_USER.getMsg());
        }
        if (!roleCodes.contains(RoleEnum.FIRST_OM.getCode())) {
            throw new PurchaseException(ErrorMessageEnum.ONLY_OM.getMsg());
        }
        AdvancepayApply apply = BeanMapper.map(bo, AdvancepayApply.class);
        apply.setUpdateBy(user.getUserId());
        apply.setUpdateName(user.getRealName());
        apply.setUpdateDate(new Date());
        // 设置付款金额大写
        apply.setMoneyCapital(
                NumberToStringForChineseMoney.getChineseMoneyStringForBigDecimal(apply.getMoney()));
        AdvancePayVo selectByPayId = advancepayApplyMapper.selectByPayId(apply.getAdvancePayId());

        Response<SUserVORes> serverUserInfo = privilegeUserServer.getUserInfo(SUserVOReq.builder().userId(selectByPayId.getCreateBy()).build());
        if (Objects.nonNull(serverUserInfo) && serverUserInfo.getCode() == ResponseCode.SUCCESS.getCode()) {
            SUserVORes userVoRes = serverUserInfo.getObj();
            if (Objects.nonNull(userVoRes)) {
                selectByPayId.setDepartName(userVoRes.getDepartmentName());
            }
        }

        // 如果修改了金额，则改价原因不能为空
        if (0 != selectByPayId.getMoney().compareTo(apply.getMoney())) {
            if (StringUtils.isBlank(apply.getUpdatePriceReason())) {
                throw new PurchaseException(ErrorMessageEnum.REASON_NULL.getMsg());
            }
        }
        // 金额只能编辑比原先小的值
        if (0 > selectByPayId.getMoney().compareTo(apply.getMoney())) {
            throw new PurchaseException(ErrorMessageEnum.MONEY_BIGGER.getMsg());
        }
        if (Objects.nonNull(apply.getMoney())) {
            CfPoHeader pomainByPoId = cfPoHeaderMapper.selectByPrimaryKey(selectByPayId.getPoId());
            if (1 == pomainByPoId.getHsStatus()) {
                throw new PurchaseException(ErrorMessageEnum.STATEMENTS_ERROR.getMsg());
            }
            CfPoHeader poMain = new CfPoHeader();
            poMain.setPoId(selectByPayId.getPoId());
            if (PayTypeEnum.FIRST_PAYMENT.getValue().toString().equals(selectByPayId.getPaymentType())) {
                poMain.setBargain(apply.getMoney());
            }
            if (PayTypeEnum.FINAL_PAYMENT.getValue().toString().equals(selectByPayId.getPaymentType())) {
                poMain.setRetainage(apply.getMoney());
            }
            if (poMain.getBargain() != null && poMain.getRetainage() != null) {
                cfPoHeaderMapper.updateByPrimaryKeySelective(poMain);
            }
        }
        if (advancepayApplyMapper.updateByPrimaryKeySelectiveWithenclosure(apply) == 0) {
            throw new PurchaseException(ErrorMessageEnum.UPDATE_ERROR.getMsg());
        }
        this.updatePurchasePoHeader(selectByPayId.getPoId());
    }

    /**
     * 付款申请单列表
     *
     * @param bo
     * @return
     */
    @Override
    public AdvancePayListVo advancePayList(AdvancePayBo bo) {
        PageHelper.startPage(bo.getPageNum(), bo.getPageSize());
        PageInfo<AdvancePayVo> pageInfo = new PageInfo<>(advancepayApplyMapper.selectAll(bo));
        List<AdvancePayVo> advancepayApplies = pageInfo.getList();
        //这里变更调用getAllVendorList接口
        Response<List<VendorResModel>> allVendorListRes = vendorCenterServer.getAllVendorList(new HashMap(){{put("vendorIds",advancepayApplies.stream().map(v -> v.getVendorId()).filter(v -> v != null).distinct().collect(Collectors.toList()));}});
        Map<Integer, VendorResModel> vendorResModelMap = allVendorListRes.getObj().stream().collect(Collectors.toMap(VendorResModel::getVendorId, Function.identity()));
        advancepayApplies.forEach(x -> {
            if (vendorResModelMap.containsKey(x.getVendorId())) {
                x.setVenAbbName(vendorResModelMap.get(x.getVendorId()).getVenAbbName());
                x.setVendorName(vendorResModelMap.get(x.getVendorId()).getVendorName());
            }
        });
        BigDecimal count = advancepayApplies.stream().map(AdvancepayApply::getMoney).reduce(BigDecimal.ZERO, BigDecimal::add);
        return new AdvancePayListVo(pageInfo, null, count);
    }

    /**
     * 付款申请单详情
     *
     * @param payId
     * @return
     */
    @Override
    public AdvancePayVo advancePayInfo(Integer payId, UserVO user) {
        AdvancePayVo advancePayVo = advancepayApplyMapper.selectByPayIdAndUser(payId, user);
        Response<SUserVORes> serverUserInfo = privilegeUserServer.getUserInfo(SUserVOReq.builder().userId(advancePayVo.getCreateBy()).build());
        if (Objects.nonNull(serverUserInfo) && serverUserInfo.getCode() == ResponseCode.SUCCESS.getCode()) {
            SUserVORes sUserVoRes = serverUserInfo.getObj();
            if (Objects.nonNull(sUserVoRes)) {
                advancePayVo.setDepartName(sUserVoRes.getDepartmentName());
            }
        }
        Response<BrandFeignVO> brandResponse = baseInfoRemoteServer.selectByBrandId(advancePayVo.getBrandId());
        BrandFeignVO brand = brandResponse.getObj();
        String type = "1";
        if (type.equals(brand.getBrandType())) {
            log.info("直营数据：{}",brand);
            if (0 == advancePayVo.getApaMaterialType()) {
                advancePayVo.setFinancialBody(brand.getFinancialBody());
            } else {
                advancePayVo.setFinancialBody(brand.getCustomerName());
            }
        }
        if (null != advancePayVo) {
            String financialBody = advancePayVo.getFinancialBody();
            //拼接财务主体，如果不为空，并且不为空字符串，则拼接"付款申请单"
            if (StringUtils.isNotEmpty(financialBody)) {
                advancePayVo.setFinancialBody(financialBody + "付款申请单");
            } else {
                //如果为空或为空串则就为"付款申请单"
                advancePayVo.setFinancialBody("付款申请单");
            }
        }

        Response<VendorResModel> info = vendorCenterServer.getInfo(advancePayVo.getVendorId());
        advancePayVo.setVenAbbName(info.getObj().getVenAbbName());
        advancePayVo.setVendorName(info.getObj().getVendorName());
        advancePayVo.setVendorCode(info.getObj().getVendorCode());
        //物料类型赋值String
        if (advancePayVo.getApaMaterialType().equals(0)) {
            advancePayVo.setApaMaterialTypeString("成衣");
        } else if (advancePayVo.getApaMaterialType().equals(1)) {
            advancePayVo.setApaMaterialTypeString("辅料");
        }
        //采购类型赋值String
        if (advancePayVo.getPoType().equals(0)) {
            advancePayVo.setPoTypeString("成衣");
        } else if (advancePayVo.getPoType().equals(1)) {
            advancePayVo.setPoTypeString("辅料");
        }
        String imgUrls = advancePayVo.getImgUrls();
        if (StringUtils.isNotBlank(imgUrls)) {
            advancePayVo.setImgList(JSONArray.parseArray(imgUrls));
        }
        return advancePayVo;
    }

    /**
     * 确认、关闭、审核、提交、付款、复核、驳回操作
     *
     * @param bo
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void auditOrClose(AdvancePayBo bo, UserVO user) throws PurchaseException {
        Response<List<SRoleVORes>> listResponse = privilegeUserServer.listRoles(SRoleVOReq.builder().userId(user.getUserId()).build());
        List<String> roleCodes = Lists.newArrayList();
        if (Objects.nonNull(listResponse) && listResponse.getCode() == ResponseCode.SUCCESS.getCode()) {
            List<SRoleVORes> sRoleVoRes = listResponse.getObj();
            if (!CollectionUtils.isEmpty(sRoleVoRes)) {
                roleCodes = CopyUtil.copyListProperties(sRoleVoRes, GRole::new).stream()
                        .map(GRole::getRoleCode).collect(Collectors.toList());
            }
        }
        if (CollectionUtils.isEmpty(roleCodes)) {
            throw new PurchaseException(ErrorMessageEnum.NO_USER.getMsg());
        }
        synchronized (AdvancePayServiceImpl.class) {
            updateAudit(bo, user, roleCodes);
        }
        this.updatePurchasePoHeader(bo.getPoId());

    }

    @Transactional(rollbackFor = Exception.class)
    public void updateAudit(AdvancePayBo bo, UserVO userVo, List<String> roleCodes) throws PurchaseException {
        AdvancePayVo advancePayVo = advancepayApplyMapper.selectByPayId(bo.getAdvancePayId());
        bo.setPoId(advancePayVo.getPoId());
        Response<SUserVORes> serverUserInfo = privilegeUserServer.getUserInfo(SUserVOReq.builder().userId(advancePayVo.getCreateBy()).build());
        if (Objects.nonNull(serverUserInfo) && serverUserInfo.getCode() == ResponseCode.SUCCESS.getCode()) {
            SUserVORes sUserVoRes = serverUserInfo.getObj();
            if (Objects.nonNull(sUserVoRes)) {
                advancePayVo.setDepartName(sUserVoRes.getDepartmentName());
            }
        }
        createObject(bo, roleCodes, advancePayVo, userVo);
    }

    private void createObject(AdvancePayBo bo, List<String> roleCodes, AdvancePayVo advancePayVo, UserVO userVo) throws PurchaseException {
        AdvancepayApply advancepayApply = null;
        // 分发对应的操作到对应的处理方法
        switch (Objects.requireNonNull(PayApplyOperationEnum.getEnumByOperation(bo.getOperation()))) {
            // 确认
            case CONFIRM:
                advancepayApply = advancePayHelper.confirm(roleCodes, bo, advancePayVo, userVo);
                setQuestPerson(null == advancepayApply, advancepayApplyMapper.updateByPrimaryKeySelective(advancepayApply));
                break;
            // 关闭
            case CLOSE:
                advancepayApply = advancePayHelper.close(roleCodes, bo, advancePayVo);
                List<String> payTypeString = advancepayApplyMapper.selectPayType(advancePayVo.getPoId());
                List<Integer> payTypes = new ArrayList<>();
                if (payTypeString != null && payTypeString.size() > 0) {
                    for (String s : payTypeString) {
                        if (!"0".equals(s) && !"1".equals(s)) {
                            payTypes.add(Integer.valueOf(s));
                        }
                    }
                }
                String zero = "0";
                String one = "1";
                if (!zero.equals(advancePayVo.getPaymentType()) && !one.equals(advancePayVo.getPaymentType())) {
                    for (Integer payType : payTypes) {
                        if (payType > Integer.parseInt(advancePayVo.getPaymentType())) {
                            throw new RuntimeException("请从最后一期开始关闭");
                        }
                    }
                }
                setQuestPerson(null == advancepayApply, advancepayApplyMapper.updateByPrimaryKeySelective(advancepayApply));
                break;
            // 提交
            case SUBMIT:
                advancepayApply = advancePayHelper.submit(roleCodes, bo, advancePayVo, userVo);
                createTask(advancePayVo, userVo, TaskTypeEnum.PAYMENTREVIEWRASK.getCode());
                setQuestPerson(null == advancepayApply, advancepayApplyMapper.updateByPrimaryKeySelective(advancepayApply));
                break;
            // 付款
            case FINANCE_AUDIT:
                advancepayApply = advancePayHelper.financeAudit(roleCodes, bo, advancePayVo, userVo);
                //财务已审核
                uodateTask(advancePayVo.getAdvancePayCode(), TaskTypeEnum.PAYMENTREVIEWRASK.getCode());
                createTask(advancePayVo, userVo, TaskTypeEnum.TWOPAYMENTREVIEWRASK.getCode());
                setQuestPerson(null == advancepayApply, advancepayApplyMapper.updateByPrimaryKeySelective(advancepayApply));
                break;
            // 复核
            case RECHECK:
                advancepayApply = advancePayHelper.reckeck(roleCodes, bo, advancePayVo, userVo);
                //待打款
                uodateTask(advancePayVo.getAdvancePayCode(), TaskTypeEnum.TWOPAYMENTREVIEWRASK.getCode());
                setQuestPerson(null == advancepayApply, advancepayApplyMapper.updateByPrimaryKeySelective(advancepayApply));
                break;
            // 已打款
            case PAID:
                advancepayApply = advancePayHelper.paid(roleCodes, bo, advancePayVo, userVo);
                //已打款
                uodateTask(advancePayVo.getAdvancePayCode(), TaskTypeEnum.TWOPAYMENTREVIEWRASK.getCode());
                setQuestPerson(null == advancepayApply, advancepayApplyMapper.updateByPrimaryKeySelective(advancepayApply));
                createBankAndCash(advancePayVo, userVo);
                break;
            // 审核
            case AUDIT:
                advancepayApply = advancePayHelper.audit(roleCodes, bo, advancePayVo, userVo);
                setQuestPerson(null == advancepayApply, advancepayApplyMapper.updateByPrimaryKeySelective(advancepayApply));
                break;
            // 驳回
            case REJECT:
                advancepayApply = advancePayHelper.reject(roleCodes, advancePayVo);
                setQuestPerson(null == advancepayApply, advancepayApplyMapper.reject(advancepayApply));
                uodateTask(advancePayVo.getAdvancePayCode(), TaskTypeEnum.PAYMENTREVIEWRASK.getCode());
                uodateTask(advancePayVo.getAdvancePayCode(), TaskTypeEnum.TWOPAYMENTREVIEWRASK.getCode());
                break;
            default:
                break;
        }
    }

    /**
     * 任务中心创建
     */
    private void createTask(AdvancePayVo advancePayVo, UserVO user, Integer taskType) throws PurchaseException {
        TaskFlowVo taskFlowVo = new TaskFlowVo();
        taskFlowVo.setPreTaskId(advancePayVo.getAdvancePayId());
        taskFlowVo.setPreTaskCode(advancePayVo.getAdvancePayCode());
        List<PaymentVo> paymentVo = advancepayApplyMapper.getProductCode(advancePayVo.getAdvancePayId());
        StringBuffer buf = new StringBuffer();
        if (paymentVo.size() > 1) {
            paymentVo.forEach(paymentVo1 -> {
                String productCode = paymentVo1.getProductCode();
                taskFlowVo.setTaskMessage("付款类型:" + paymentVo1.getPayment() + paymentVo1.getProportion() + "%, " + "付款金额" + advancePayVo.getMoney());
                buf.append(productCode + ",");
            });
            taskFlowVo.setProductCode(buf.substring(0, buf.length() - 1));
        } else {
            paymentVo.forEach(paymentVo1 -> {
                String productCode = paymentVo1.getProductCode();
                taskFlowVo.setTaskMessage("付款类型:" + paymentVo1.getPayment() + paymentVo1.getProportion() + "%, " + "付款金额" + advancePayVo.getMoney());
                taskFlowVo.setProductCode(productCode);
            });
        }
        taskFlowVo.setBrandId(advancePayVo.getBrandId());
        taskFlowVo.setVendorId(advancePayVo.getVendorId());
        taskFlowVo.setCreateBy(user.getUserId());
        taskFlowVo.setCreateDate(new Date());
        taskFlowVo.setTaskType(taskType);
        taskFlowVo.setMsg("你有一笔新的预付款【" + advancePayVo.getAdvancePayCode() + "】需要审核");
        taskFlowVo.setTaskState(TaskTypeEnum.UNFINISHED.getCode());
        taskFlowVo.setTaskOperation(TaskTypeEnum.GOTODETAILS.getCode());
        com.chenfan.common.vo.Response taskFlow = taskRemoteServer.createTaskFlow(taskFlowVo);
        if (taskFlow.getCode() != HttpStatus.OK.value()) {
            throw new PurchaseException("创建任务失败");
        }
    }

    /**
     * 修改任务中心状态为已完成
     */
    private void uodateTask(String advancePayCode, Integer taskType) throws PurchaseException {
        TaskFlowVo taskFlowVo = new TaskFlowVo();
        taskFlowVo.setPreTaskCode(advancePayCode);
        taskFlowVo.setTaskType(taskType);
        com.chenfan.common.vo.Response response =
                taskRemoteServer.updateTaskFlow(taskFlowVo);
        if (response.getCode() != HttpStatus.OK.value()) {
            throw new PurchaseException("关闭任务失败");
        }

    }

    private void setQuestPerson(boolean b, int i) throws PurchaseException {
        // 改状态、下个任务人
        if (b || i == 0) {
            throw new PurchaseException(ErrorMessageEnum.FAIL.getMsg());
        }
    }

    /**
     * 查询订金配置表
     *
     * @return
     */
    @Override
    public List<DownpaymentConfig> paymentConfigList() {
        return downpaymentConfigMapper.selectAll();
    }

    @Override
    public List<AdvancepayApplyVo> exportExcel(AdvancePayBo bo) {
        List<AdvancepayApplyVo> advancepayApplyVos = advancepayApplyMapper.exportExcel(bo);
        Response<List<VendorResModel>> allVendorListRes = vendorCenterServer.getAllVendorList(new HashMap(){{put("vendorIds",advancepayApplyVos.stream().map(v -> v.getVendorId()).filter(v -> v != null).distinct().collect(Collectors.toList()));}});
        Map<Integer, VendorResModel> vendorResModelMap = allVendorListRes.getObj().stream().collect(Collectors.toMap(VendorResModel::getVendorId, Function.identity()));
        advancepayApplyVos.forEach(x -> {
            if (vendorResModelMap.containsKey(x.getVendorId())) {
                x.setVenAbbName(vendorResModelMap.get(x.getVendorId()).getVenAbbName());
                x.setVendorName(vendorResModelMap.get(x.getVendorId()).getVendorName());
            }
        });

        return advancepayApplyVos;
    }

    @Override
    public AdvanceVo createList(String poCode, UserVO user) {
        AdvanceVo advanceVo = advancepayApplyMapper.selectByPoId(poCode);
        //根据poCode查询是否有一期付款
        BigDecimal money = advancepayApplyMapper.findMoneyByPoCode(poCode);
        if (null != money) {
            advanceVo.setFirstPayment(money);
        } else {
            advanceVo.setFirstPayment(null);
        }
        //调用getUserInfo获取角色信息
        Response<SUserVORes> serverUserInfo = privilegeUserServer.getUserInfo(SUserVOReq.builder().userId(user.getUserId()).build());
        if (Objects.nonNull(serverUserInfo) && serverUserInfo.getCode() == ResponseCode.SUCCESS.getCode()) {
            SUserVORes userVoRes = serverUserInfo.getObj();
            if (Objects.nonNull(userVoRes)) {
                advanceVo.setDepartmentName(userVoRes.getDepartmentName());
                advanceVo.setAccount(userVoRes.getAccount());
                advanceVo.setRoleName(userVoRes.getRoleName());
            }
        }
        return advanceVo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.REPEATABLE_READ)
    public void createFromPurchase(AdvancePayToNewFinanceVO vo)  throws PurchaseException{
        if(null != vo && !CollectionUtils.isEmpty(vo.getAdvancepayApplies()) && null != vo.getBo()){
            List<AdvancepayApply> advancepayApplies = vo.getAdvancepayApplies();
            AdvancePayBo bo = vo.getBo();

            for(AdvancepayApply ad : advancepayApplies){
                CfPoHeader cfPoHeader = cfPoHeaderMapper.selectByPrimaryKey(ad.getPoId());
                // 生成到新预付款的数据，只能是分期或者定金尾款方式，所以需要先检测一下
                // 插入数据库
                int insert = advancepayApplyMapper.insertSelective(ad);
                if (bo.getFirstOrLastPay() != null) {
                    //生成定金方式时，判断是不是已经生成了分期付款的方式
                    if(cfPoHeader.getHirePurchase() != null&&BigDecimal.ZERO.compareTo(cfPoHeader.getHirePurchase())!=0){
                        throw new PurchaseException(ErrorMessageEnum.ALREADY_EXIST_HIRE_PURCHASE.getMsg());
                    }
                    // 更新财务系统的采购单订金或者尾款
                    int update =
                            cfPoHeaderMapper.updateByPrimaryKeySelective(
                                    new CfPoHeader(
                                            ad.getPoId().longValue(),
                                            ad.getMoney(),
                                            bo.getPercent(),
                                            bo.getPercent(),
                                            PayTypeEnum.getEnumByValue(bo.getFirstOrLastPay())));
                    if (update == 0) {
                        throw new PurchaseException(ErrorMessageEnum.APPLY_ERROR.getMsg());
                    }

                    // 更新老系统的采购单订金或者尾款(生成预付款会第一次填充定金尾款，所以不需要查采购单里面的做累计)
                    PoHeader po = new PoHeader(ad.getPoId(),ad.getMoney(),bo.getPercent(),PayTypeEnum.getEnumByValue(bo.getFirstOrLastPay()));
                    purchaseRemoteServer.updatePoHeaderFromFinance(po);
                }
                if (bo.getPayType() != null && bo.getPayType() > 1) {
                    // 更新财务系统的采购单分期付款
                    //生成分期付款方式时，判断是不是已经生成了定金的方式
                    if(cfPoHeader.getBargain() != null || cfPoHeader.getBargainRatio() != null){
                        throw new PurchaseException(ErrorMessageEnum.ALREADY_EXIST_BARGAIN.getMsg());
                    }
                    BigDecimal hirePurchaseForNew = cfPoHeader.getHirePurchase() == null ? BigDecimal.ZERO : cfPoHeader.getHirePurchase();
                    BigDecimal payValueInBo = ad.getMoney() == null ? BigDecimal.ZERO : ad.getMoney();
                    int update =
                            cfPoHeaderMapper.updateHirePurchase(ad.getPoId(), hirePurchaseForNew.add(payValueInBo), 1);
                    if (update == 0) {
                        throw new PurchaseException(ErrorMessageEnum.APPLY_ERROR.getMsg());
                    }

                    // 更新老系统的采购单分期付款,因为分期付款需要加上之前的数量，所以需要先查一下采购单,做累加
                    PoHeader po = new PoHeader();
                    po.setPoId(ad.getPoId());
                    // 根据预付款信息中的poId去老系统查对应的采购单
                    Response<PoHeader> poHeaderInfoByPoId = purchaseRemoteServer.getPoHeaderInfoByPoId(ad.getPoId().intValue());
                    if(null != poHeaderInfoByPoId && null != poHeaderInfoByPoId.getObj()){
                        //能够获取到数据，再更新分期付款
                        BigDecimal hirePurchase = poHeaderInfoByPoId.getObj().getHirePurchase() == null ? BigDecimal.ZERO : poHeaderInfoByPoId.getObj().getHirePurchase();
                        BigDecimal payValue = ad.getMoney() == null ? BigDecimal.ZERO : ad.getMoney();
                        po.setHirePurchase(payValue.add(hirePurchase));
                        purchaseRemoteServer.updatePoHeaderFromFinance(po);
                    }
                }

                if (insert == 0) {
                    throw new PurchaseException(ErrorMessageEnum.APPLY_ERROR.getMsg());
                }
            }
        }
    }

    @Override
    public Integer getAdvanceByPoCode(String poCode) {
        AdvanceVo advanceVo = advancepayApplyMapper.selectByPoId(poCode);
        Integer paymentConfigId = 0;
        if(null != advanceVo){
            paymentConfigId = advanceVo.getPaymentConfigId();
            return paymentConfigId;
        }
        return null;
    }

    @Override
    public Integer checkIfExistAdvance(AdvancePayBo bo) {
        Integer result = 0;
        if(null != bo){
            List<Integer> poIds = bo.getPoIds();
            result = advancepayApplyMapper.checkIfExists(poIds);
        }
        return result;
    }

    @Override
    public Integer checkIfExistAdvanceByPoId(Integer poId, Integer firstOrLastPay) {
        return advancepayApplyMapper.selectCountByPoIdAndState(poId.longValue(),firstOrLastPay);
    }

    @Override
    public void createBankAndCash(AdvancePayVo advancePayVo, UserVO userVo) {
        CfBankAndCash cash = new CfBankAndCash();
        cash.setRecordType(getType(advancePayVo.getPayment()));
        cash.setArapType("4");
        cash.setArapDate(new Date());
        cash.setBrandId(advancePayVo.getBrandId());
        cash.setBankAndCashStatus(2);
        cash.setBalance(advancePayVo.getVendorCode());
        Response<VendorResModel> info = vendorCenterServer.getInfo(advancePayVo.getVendorId());
        cash.setBalanceName(info.getObj().getVenAbbName());
        cash.setRecordUser(advancePayVo.getCreateName());
        cash.setAmount(advancePayVo.getMoney());
        cash.setBank(advancePayVo.getBank());
        cash.setBankNo(advancePayVo.getBankAccount());
        cash.setCollectionUnit(advancePayVo.getReceiptDepartment());
        cash.setSourceCode(advancePayVo.getAdvancePayCode());
        cfBankAndCashService.create(cash, userVo);
    }

    public static String getType(String payment) {
        //  汇票 汇款 现金 银行转账 支付宝转账
        // -》 记录类型 1支付宝，2微信支付，3现金，4支票，5汇款
        if(Objects.isNull(payment)){
            return "5";
        }
        String alipay = "支付宝";
        String cash = "现金";
        String invoice = "发票";
        if (payment.contains(alipay)) {
            return "1";
        }
        if (payment.contains(cash)) {
            return "3";
        }
        if (payment.contains(invoice)) {
            return "4";
        }
        return "5";
    }

}
