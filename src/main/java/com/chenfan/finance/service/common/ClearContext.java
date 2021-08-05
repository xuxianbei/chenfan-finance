package com.chenfan.finance.service.common;

import com.chenfan.finance.model.CfBankAndCash;
import com.chenfan.finance.model.CfChargeCommon;
import lombok.Data;

import java.util.List;

/**
 * 核销上下文
 *
 * @author: xuxianbei
 * Date: 2021/5/6
 * Time: 10:50
 * Version:V1.0
 */
@Data
public class ClearContext {

    public static ClearContext getInstance() {
        return new ClearContext();
    }


    /**
     * 等待核销的费用
     */
    private List<CfChargeCommon> waitCfChargeCommons;

    /**
     * 等待核销的实收付
     */
    private List<CfBankAndCash> waitCfBankAndCashs;
}
