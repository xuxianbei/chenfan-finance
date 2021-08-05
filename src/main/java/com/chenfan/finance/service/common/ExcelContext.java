package com.chenfan.finance.service.common;

import com.chenfan.finance.server.dto.SCompanyRes;
import com.chenfan.finance.server.remote.vo.InvoiceTitleVO;
import lombok.Data;

import java.util.List;

/**
 * excel 上下文
 *
 * @author: xuxianbei
 * Date: 2021/5/7
 * Time: 15:26
 * Version:V1.0
 */
@Data
public class ExcelContext {

    public static ExcelContext getInstance() {
        return new ExcelContext();
    }

    /**
     * 公司信息
     */
    private List<SCompanyRes> sCompanyRes;

    /**
     * 账单抬头信息
     */
    private List<InvoiceTitleVO> invoiceTitleVos;

}
