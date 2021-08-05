package com.chenfan.finance.service.common.state;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.chenfan.finance.dao.ApprovalFlowMapper;
import com.chenfan.finance.enums.GetCode;
import com.chenfan.finance.exception.FinanceBizState;
import com.chenfan.finance.exception.ModuleBizState;
import com.chenfan.finance.model.ApprovalFlow;
import com.chenfan.finance.utils.pageinfo.PageInfoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 简单提炼了公共方法 没啥用处
 *
 * @author: xuxianbei
 * Date: 2021/3/11
 * Time: 10:55
 * Version:V1.0
 */
public abstract class AbstractStateService {

    protected static final String FA_PIAO = "WP";

    @Autowired
    protected PageInfoUtil pageInfoUtil;

    @Resource
    protected ApprovalFlowMapper approvalFlowMapper;

    protected boolean judgeStatus(Integer oldInvoiceStatus, Integer invoiceStatus, Integer customerInvoiceWay) {
        Assert.isTrue(!oldInvoiceStatus.equals(invoiceStatus), ModuleBizState.TARGET_DATE_ERROR.message());
        return true;
    }

    protected boolean judgeStatus(Integer oldInvoiceStatus, Integer invoiceStatus, Integer customerInvoiceWay, JudgeStateExt judgeStateExt) {
        Assert.isTrue(!oldInvoiceStatus.equals(invoiceStatus), ModuleBizState.DATE_ERROR.message());
        return true;
    }

    protected boolean judgeStatus(Integer oldInvoiceStatus, Integer invoiceStatus, Integer customerInvoiceWay, List<List<GetCode>> lists) {
        for (List<GetCode> getCodes : lists) {
            if (getCodes.size() > 1) {
                return invoiceStatus.equals(oldInvoiceStatus) && getCodes.stream().filter(t -> t.getCode().equals(oldInvoiceStatus)).findFirst().isPresent()
                        && getCodes.stream().filter(t -> t.getCode().equals(invoiceStatus)).findFirst().isPresent();
            }
        }
        Assert.isTrue(!oldInvoiceStatus.equals(invoiceStatus), ModuleBizState.DATE_ERROR.message());
        return true;
    }


    protected void judgeInList(List<GetCode> list, Integer invoiceStatus) {
        Assert.isTrue(list.stream().filter(t -> t.getCode().equals(invoiceStatus)).findFirst().isPresent(), ModuleBizState.DATE_ERROR.message());
    }

    protected Integer getInvoiceStatusEnumFromList(Integer invoiceStatus, List<List<GetCode>> invoiceStatussTaxInvoice, Integer newInvoiceStatus) {
        return getInvoiceStatusEnumFromList(invoiceStatus, invoiceStatussTaxInvoice, newInvoiceStatus, false);
    }


    /**
     * 没办法写崩了。
     *
     * @param invoiceStatus
     * @param invoiceStatussTaxInvoice
     * @param newInvoiceStatus
     * @param checkState               是否只校验状态
     * @return
     */
    protected Integer getInvoiceStatusEnumFromList(Integer invoiceStatus, List<List<GetCode>> invoiceStatussTaxInvoice, Integer newInvoiceStatus, boolean checkState) {
        int i;
        for (i = 0; i < invoiceStatussTaxInvoice.size(); i++) {
            List<GetCode> enums = invoiceStatussTaxInvoice.get(i);
            if (enums.size() == 1) {
                if (enums.get(0).getCode().equals(invoiceStatus)) {
                    i++;
                    break;
                }
            } else {
                //新老状态都在一个集合里，说明是可以互相切换的
                List<GetCode> news = enums.stream().filter(t -> t.getCode().equals(newInvoiceStatus)).collect(Collectors.toList());
                List<GetCode> olds = enums.stream().filter(t -> t.getCode().equals(invoiceStatus)).collect(Collectors.toList());
                if (olds.size() > 1 && news.size() > 1) {
                    return newInvoiceStatus;
                }
                if ((olds.size() == 1 && news.size() == 1) && (!olds.get(0).equals(news.get(0)))) {
                    return newInvoiceStatus;
                }
                if (olds.size() > 1 && news.size() == 1) {
                    return newInvoiceStatus;
                }
                //老状态在集合里，新的状态在下一个集合中的第一个
                if (enums.stream().filter(t -> t.getCode().equals(invoiceStatus)).findFirst().isPresent() &&
                        invoiceStatussTaxInvoice.get(i + 1).get(0).getCode().equals(newInvoiceStatus)) {
                    i++;
                    break;
                }

            }
        }
        if (i >= invoiceStatussTaxInvoice.size()) {
            throw FinanceBizState.STATE_ERROR;
        }

        if (checkState) {
            Optional<GetCode> option = invoiceStatussTaxInvoice.get(i).stream().filter(t -> t.getCode().equals(newInvoiceStatus)).findFirst();
            if (option.isPresent()) {
                return option.get().getCode();
            }
        }

        return invoiceStatussTaxInvoice.get(i).get(0).getCode();
    }

}
