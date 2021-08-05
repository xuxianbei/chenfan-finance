package com.chenfan.finance.service.impl;

import com.chenfan.common.vo.Response;
import com.chenfan.common.vo.ResponseCode;
import com.chenfan.common.vo.UserVO;
import com.chenfan.finance.dao.AdvancepayApplyMapper;
import com.chenfan.finance.dao.CfPoHeaderMapper;
import com.chenfan.finance.dao.DownpaymentConfigMapper;
import com.chenfan.finance.enums.ChildOrderTypeEnum;
import com.chenfan.finance.enums.OrderTypeEnum;
import com.chenfan.finance.model.Brand;
import com.chenfan.finance.model.bo.CfPoHeaderBo;
import com.chenfan.finance.model.dto.BaseGetBrandInfoList;
import com.chenfan.finance.model.dto.DownpaymentConfig;
import com.chenfan.finance.model.vo.CfPoHeaderListVo;
import com.chenfan.finance.model.vo.CfPoHeaderVo;
import com.chenfan.finance.model.vo.PayApplyInfo;
import com.chenfan.finance.server.BaseInfoRemoteServer;
import com.chenfan.finance.server.BaseRemoteServer;
import com.chenfan.finance.server.PrivilegeUserServer;
import com.chenfan.finance.server.VendorCenterServer;
import com.chenfan.finance.server.remote.request.BrandFeignRequest;
import com.chenfan.finance.server.remote.vo.BrandFeignVO;
import com.chenfan.finance.service.CfPoHeaderService;
import com.chenfan.privilege.request.SUserVOReq;
import com.chenfan.privilege.response.SUserVORes;
import com.chenfan.vendor.response.VendorResModel;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author 2062
 */
@Service
public class CfPoHeaderServiceImpl implements CfPoHeaderService {

    @Autowired
    private CfPoHeaderMapper cfPoHeaderMapper;

    @Autowired
    private VendorCenterServer vendorCenterServer;

    @Autowired
    private PrivilegeUserServer privilegeUserServer;

    @Autowired
    private BaseRemoteServer baseRemoteServer;

    @Autowired
    private DownpaymentConfigMapper downpaymentConfigMapper;

    @Autowired
    private AdvancepayApplyMapper advancepayApplyMapper;

    @Autowired
    private BaseInfoRemoteServer baseInfoRemoteServer;

    @Override
    public CfPoHeaderListVo selectPurchaseOrder(CfPoHeaderBo bo, UserVO user) {
        PageHelper.startPage(bo.getPageNum(),bo.getPageSize());
        PageInfo<CfPoHeaderVo> page = new PageInfo<>(cfPoHeaderMapper.selectPurchaseOrder(bo));
        List<CfPoHeaderVo> list = page.getList();
        Response<List<VendorResModel>> allVendorListRes = vendorCenterServer.getAllVendorList(new HashMap(){{put("vendorIds",list.stream().map(v -> v.getVendorId()).filter(v -> v != null).distinct().collect(Collectors.toList()));}});
        Map<Integer, VendorResModel> vendorResModelMap = allVendorListRes.getObj().stream().collect(Collectors.toMap(VendorResModel::getVendorId, Function.identity()));
        BrandFeignRequest brandFeignRequest = new BrandFeignRequest();
        brandFeignRequest.setBrandIdList(list.stream().filter(x->Objects.nonNull(x.getBrandId())).map(x->x.getBrandId().intValue()).collect(Collectors.toList()));
        Response<List<BrandFeignVO>> brandByBrandIdList = baseInfoRemoteServer.getBrandByBrandIdList(brandFeignRequest);
        Map<Integer, BrandFeignVO> brandFeignVOMap = brandByBrandIdList.getObj().stream().collect(Collectors.toMap(BrandFeignVO::getBrandId, Function.identity()));
        list.forEach(x -> {
            if (vendorResModelMap.containsKey(x.getVendorId().intValue())) {
                x.setVendorName(vendorResModelMap.get(x.getVendorId().intValue()).getVendorName());
                x.setVenAbbName(vendorResModelMap.get(x.getVendorId().intValue()).getVenAbbName());
            }
            if (brandFeignVOMap.containsKey(x.getBrandId().intValue())) {
                x.setCustomerName(brandFeignVOMap.get(x.getBrandId().intValue()).getCustomerName());
                x.setFinancialBody(brandFeignVOMap.get(x.getBrandId().intValue()).getFinancialBody());
            }
            String orderTypeString = OrderTypeEnum.getMsgByCode(x.getOrderType());
            if (null != x.getChildOrderType()) {
                orderTypeString += "-" + ChildOrderTypeEnum.getMsgByCode(x.getChildOrderType());
            }
            x.setOrderTypeString(orderTypeString);
        });
        CfPoHeaderListVo cfPoHeaderListVo = new CfPoHeaderListVo();
        //调用getUserInfo获取角色信息
        Response<SUserVORes> serverUserInfo = privilegeUserServer.getUserInfo(SUserVOReq.builder().userId(user.getUserId()).build());
        if (Objects.nonNull(serverUserInfo) && serverUserInfo.getCode() == ResponseCode.SUCCESS.getCode()) {
            SUserVORes userVoRes = serverUserInfo.getObj();
            if (Objects.nonNull(userVoRes)) {
                cfPoHeaderListVo.setDepartmentName(userVoRes.getDepartmentName());
                cfPoHeaderListVo.setAccount(userVoRes.getAccount());
                cfPoHeaderListVo.setRoleName(userVoRes.getRoleName());
            }
        }
        Calendar now = Calendar.getInstance();
        String setCurrentTime = now.get(Calendar.YEAR) + "年" + (now.get(Calendar.MONTH) + 1) + "月" + now.get(Calendar.DAY_OF_MONTH) + "日";
        cfPoHeaderListVo.setCurrentTime(setCurrentTime);
        cfPoHeaderListVo.setPage(page);
        return cfPoHeaderListVo;
    }




    /**
     * 根据定金比 获取定金金额，尾款金额
     *
     * @param poId
     * @param paymentConfId
     * @return
     */
    @Override
    public PayApplyInfo getAmountOfMoney(Long poId, Integer paymentConfId, BigDecimal payValue, String payType, Integer firstOrLastPay) {
        //根据采购单号查询含税金额总计、供应商、存货编码、采购类型
        PayApplyInfo payApplyInfo = cfPoHeaderMapper.selectPayInfo(poId);
        //获取定金比例
        if (null != paymentConfId) {
            DownpaymentConfig downpaymentConfig = downpaymentConfigMapper.selectByPrimaryKey(paymentConfId);
            if (Objects.nonNull(downpaymentConfig) && payApplyInfo.getIncludedTaxMoneyCount() != null) {
                BigDecimal proportion = new BigDecimal(100);
                //定金金额
                if (null != firstOrLastPay && firstOrLastPay == 0) {
                    //定金比例
                    BigDecimal bargainRatio = downpaymentConfig.getProportion();
                    BigDecimal bargain = payApplyInfo.getIncludedTaxMoneyCount().multiply(bargainRatio.divide(proportion)).setScale(2, BigDecimal.ROUND_HALF_UP);
                    payApplyInfo.setBargain(bargain);
                    payApplyInfo.setProportion(bargainRatio);
                }
                //尾款金额
                if (null != firstOrLastPay && firstOrLastPay == 1) {
                    BigDecimal retainageRatio = downpaymentConfig.getTailProportion();
                    BigDecimal retainage = payApplyInfo.getIncludedTaxMoneyCount().multiply(retainageRatio.divide(proportion)).setScale(2, BigDecimal.ROUND_HALF_UP);
                    BigDecimal bargain = payApplyInfo.getIncludedTaxMoneyCount().subtract(retainage).setScale(2, BigDecimal.ROUND_HALF_UP);
                    payApplyInfo.setBargain(bargain);
                    payApplyInfo.setProportion(downpaymentConfig.getProportion());
                    payApplyInfo.setRetainage(retainage);
                    payApplyInfo.setTailProportion(retainageRatio);
                }
                payApplyInfo.setPaymentConfId(downpaymentConfig.getPaymentConfId());
            }
        }
        if (StringUtils.isNotBlank(payType)) {
            payApplyInfo.setPayType(payType);
        } else {
            payApplyInfo.setPayType(null);
        }
        if (null != payValue) {
            payApplyInfo.setPayValue(payValue);
        } else {
            payApplyInfo.setPayValue(null);
        }
        if (null != firstOrLastPay) {
            payApplyInfo.setFirstOrLastPay(firstOrLastPay);
        } else {
            payApplyInfo.setFirstOrLastPay(null);
        }
        //根据poid查询是否有一期付款
        BigDecimal money = advancepayApplyMapper.findMoneyByPoId(poId);
        String type = "2";
        if (null != money) {
            payApplyInfo.setFirstPayment(money);
        } else if (type.equals(payType)) {
            payApplyInfo.setFirstPayment(payValue);
        } else {
            payApplyInfo.setFirstPayment(null);
        }

        return payApplyInfo;
    }


    @Override
    public BigDecimal queryAdvance(String poCode) {
        BigDecimal initPay = BigDecimal.ZERO;
        List<CfPoHeaderVo> list = cfPoHeaderMapper.selectListToInitPay(poCode);
        if (list != null && list.size() > 0) {
            for (CfPoHeaderVo mainVo : list) {
                BigDecimal bigDecimal = mainVo.getQualifiedQuantityCount().multiply(mainVo.getTaxUnitPrice());
                initPay = initPay.add(bigDecimal);
            }
        }
        BigDecimal bigDecimal = initPay.setScale(2, BigDecimal.ROUND_HALF_UP);
        return bigDecimal;
    }

}
