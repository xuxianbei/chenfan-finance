package com.chenfan.finance.service.common;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.chenfan.finance.dao.CfClearDetailMapper;
import com.chenfan.finance.enums.ChargeCommonEnum;
import com.chenfan.finance.exception.FinanceBizState;
import com.chenfan.finance.model.CfClearDetail;
import com.chenfan.finance.model.CfClearHeader;
import com.chenfan.finance.model.vo.ClearHeaderDetailInvoiceDetailVo;
import com.chenfan.finance.model.vo.ClearHeaderDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 代理核销
 *
 * @author: xuxianbei
 * Date: 2021/4/20
 * Time: 19:52
 * Version:V1.0
 */
@Service
public class DelegateClearService {

    @Resource
    private CfClearDetailMapper clearDetailMapper;

    @Autowired
    private McnClearService mcnClearService;

    @Autowired
    private HugeGoodsClearService hugeGoodsClearService;


    public void clearHeaderDetailBase(ClearHeaderDetailVo clearHeaderDetailVo, CfClearHeader cfClearHeader) {
        getClearDetail(cfClearHeader).clearHeaderDetailBase(clearHeaderDetailVo, cfClearHeader);
    }

    private ClearDetail getClearDetail(CfClearHeader cfClearHeader) {
        switch (ChargeCommonEnum.valueOfChargeSourceType(cfClearHeader.getJobType())) {
            case CHARGE_SOURCE_TYPE_HUGE_GOODS:
                return hugeGoodsClearService;
            case CHARGE_SOURCE_TYPE_MCN:
                return mcnClearService;
            default:
                throw FinanceBizState.DATE_ERROR;
        }
    }

    public List<ClearHeaderDetailInvoiceDetailVo> detailInvoiceDetailVos(ClearHeaderDetailVo clearHeaderDetailVo, CfClearHeader cfClearHeader) {
        List<CfClearDetail> cfClearDetails = clearDetailMapper.selectList(
                Wrappers.lambdaQuery(CfClearDetail.class).eq(CfClearDetail::getClearId, cfClearHeader.getClearId()));
        return getClearDetail(cfClearHeader).detailInvoiceDetailVos(clearHeaderDetailVo, cfClearHeader, cfClearDetails);
    }

    public void clearHeaderDetailTotalVo(ClearHeaderDetailVo clearHeaderDetailVo, CfClearHeader cfClearHeader) {
        getClearDetail(cfClearHeader).clearHeaderDetailTotalVo(clearHeaderDetailVo, cfClearHeader);
    }
}
