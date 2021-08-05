package com.chenfan.finance.controller.common;

import com.chenfan.common.vo.Response;
import com.chenfan.finance.model.dto.BankAndCashBatchDeleteDto;
import com.chenfan.finance.service.common.BankAndCashCommonService;
import com.chenfan.finance.utils.pageinfo.PageInfoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

/**
 * 实收付
 * @author: xuxianbei
 * Date: 2021/5/12
 * Time: 19:41
 * Version:V1.0
 */
@RestController
@RequestMapping(value = "bank_and_cash/common")
public class CfBankAndCashCommonController {

    @Autowired
    private BankAndCashCommonService bankAndCashCommonService;

    /**
     * 批量打款
     *
     * @param multipartFile
     * @return
     */
    @PostMapping("batch/create/and/clear")
    public Response<Integer> batchCreateAndClear(@RequestParam MultipartFile multipartFile) {
        return PageInfoUtil.smartSuccess(bankAndCashCommonService.batchCreateAndClear(multipartFile));
    }


    /**
     * 下载失败原因
     *
     * @param response
     * @throws Exception
     */
    @GetMapping("batch/excel/output")
    public void batchExcelOutput(HttpServletResponse response) throws Exception {
        bankAndCashCommonService.batchExcelOutput(response);
    }



    /**
     * 批量作废
     * @param bankAndCashBatchDeleteDto
     * @return
     */
    @PostMapping("batch/invalid")
    public Response<Integer> batchInvalid(@Validated @RequestBody BankAndCashBatchDeleteDto bankAndCashBatchDeleteDto) {
        return PageInfoUtil.smartSuccess(bankAndCashCommonService.batchInvalid(bankAndCashBatchDeleteDto));
    }
}
