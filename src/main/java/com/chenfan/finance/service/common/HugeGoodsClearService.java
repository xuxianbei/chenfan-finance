package com.chenfan.finance.service.common;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.chenfan.common.vo.Response;
import com.chenfan.finance.commons.exception.FinanceTipException;
import com.chenfan.finance.dao.CfChargeMapper;
import com.chenfan.finance.dao.CfClearDetailMapper;
import com.chenfan.finance.enums.ChargeCommonEnum;
import com.chenfan.finance.model.CfCharge;
import com.chenfan.finance.model.CfClearDetail;
import com.chenfan.finance.model.CfClearHeader;
import com.chenfan.finance.model.vo.ClearHeaderDetailInvoiceDetailVo;
import com.chenfan.finance.model.vo.ClearHeaderDetailVo;
import com.chenfan.finance.server.VendorCenterServer;
import com.chenfan.finance.utils.RpcUtil;
import com.chenfan.vendor.response.VendorResModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 大货采购
 *
 * @author: xuxianbei
 * Date: 2021/4/20
 * Time: 19:44
 * Version:V1.0
 */
@Service
public class HugeGoodsClearService implements ClearDetail {

    @Autowired
    private VendorCenterServer vendorCenterServer;

    @Resource
    private CfChargeMapper cfChargeMapper;


    @Override
    public void clearHeaderDetailBase(ClearHeaderDetailVo clearHeaderDetailVo, CfClearHeader cfClearHeader) {
        if (notSupport(cfClearHeader)) {
            return;
        }
        Response<VendorResModel> resModelResponse = vendorCenterServer.getVendorByCode(null, cfClearHeader.getBalance());

        VendorResModel vendorResModel = RpcUtil.getObjNoException(resModelResponse);
        if (Objects.nonNull(vendorResModel)) {
            clearHeaderDetailVo.getClearHeaderDetailBase().setBalance(vendorResModel.getVenAbbName());
        }

    }

    private boolean notSupport(CfClearHeader cfClearHeader) {
        if (!cfClearHeader.getJobType().equals(ChargeCommonEnum.CHARGE_SOURCE_TYPE_HUGE_GOODS.getCode())) {
            return true;
        }
        return false;
    }

    private List<VendorResModel> getBalances() {
        Response<List<VendorResModel>> vendorByCodeRes = vendorCenterServer.getAllVendorList(new HashMap<>(16));
        List<VendorResModel> vendorResModels = vendorByCodeRes.getObj();
        if (CollectionUtils.isEmpty(vendorResModels)) {
            throw new FinanceTipException("vendorCenterServer服务繁忙，稍后重试");
        }
        return vendorResModels;
    }

    private List<ClearHeaderDetailInvoiceDetailVo> getClearHeaderDetailInvoiceDetailVos(ClearHeaderDetailVo clearHeaderDetailVo, List<CfClearDetail> cfClearDetails) {
        List<ClearHeaderDetailInvoiceDetailVo> clearHeaderDetailInvoiceDetailVos;
        List<CfCharge> cfCharges = cfChargeMapper.selectList(Wrappers.lambdaQuery(CfCharge.class).in(CfCharge::getChargeId,
                cfClearDetails.stream().map(CfClearDetail::getChargeId).collect(Collectors.toList())));

        List<VendorResModel> vendorResModels = getBalances();

        //前面已经校验了，这里直接取
        clearHeaderDetailVo.getClearHeaderDetailBase().setInvoiceNo(cfCharges.get(0).getInvoiceNo());

        clearHeaderDetailInvoiceDetailVos = cfClearDetails.stream().map(cfClearDetail -> {
            ClearHeaderDetailInvoiceDetailVo clearHeaderDetailInvoiceDetailVo = new ClearHeaderDetailInvoiceDetailVo();
            BeanUtils.copyProperties(cfClearDetail, clearHeaderDetailInvoiceDetailVo);

            CfCharge cfCharge =
                    cfCharges.stream().filter(t ->
                            t.getChargeId().equals(cfClearDetail.getChargeId())).findFirst().get();

            //这行报错，肯定数据异常
            String chargeCode = cfCharge.getChargeCode();
            clearHeaderDetailInvoiceDetailVo.setChargeCode(chargeCode);
            clearHeaderDetailInvoiceDetailVo.setBrandId(cfCharge.getBrandId());
            clearHeaderDetailInvoiceDetailVo.setCreateDate(cfCharge.getCreateDate());


            Optional<VendorResModel> optionalVendorResModel =
                    vendorResModels.stream().filter(t -> t.getVendorCode().equals(clearHeaderDetailInvoiceDetailVo.getBalance())).findFirst();
            if (optionalVendorResModel.isPresent()) {
                clearHeaderDetailInvoiceDetailVo.setBalanceName(optionalVendorResModel.get().getVenAbbName());
            }
            return clearHeaderDetailInvoiceDetailVo;
        }).collect(Collectors.toList());
        return clearHeaderDetailInvoiceDetailVos;
    }

    @Override
    public List<ClearHeaderDetailInvoiceDetailVo> detailInvoiceDetailVos(ClearHeaderDetailVo clearHeaderDetailVo, CfClearHeader cfClearHeader, List<CfClearDetail> cfClearDetails) {
        List<ClearHeaderDetailInvoiceDetailVo> clearHeaderDetailInvoiceDetailVos = new ArrayList<>();
        if (notSupport(cfClearHeader)) {
            return clearHeaderDetailInvoiceDetailVos;
        }

        return getClearHeaderDetailInvoiceDetailVos(clearHeaderDetailVo, cfClearDetails);
    }

    @Override
    public void clearHeaderDetailTotalVo(ClearHeaderDetailVo clearHeaderDetailVo, CfClearHeader cfClearHeader) {

    }
}
