package com.chenfan.finance.controller.common;

import com.chenfan.common.vo.Response;
import com.chenfan.finance.commons.aspect.CFRequestHolder;
import com.chenfan.finance.model.TaxInvoiceFillDtoList;
import com.chenfan.finance.model.dto.*;
import com.chenfan.finance.model.vo.CfInvoiceCommonDetailSettleVo;
import com.chenfan.finance.model.vo.CfInvoiceCommonDetailVo;
import com.chenfan.finance.model.vo.CfInvoiceCommonPreVo;
import com.chenfan.finance.model.vo.CfInvoiceCommonVo;
import com.chenfan.finance.service.common.InvoiceCommonService;
import com.chenfan.finance.utils.pageinfo.PageInfoUtil;
import com.chenfan.finance.utils.pageinfo.model.CreateVo;
import com.chenfan.privilege.common.config.SearchAuthority;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 账单
 *
 * @author: xuxianbei
 * Date: 2021/3/1
 * Time: 15:16
 * Version:V1.0
 */
@RestController
@RequestMapping("invoice/common")
public class InvoiceCommonController {

    @Autowired
    private InvoiceCommonService invoiceCommonService;

    /**
     * 创建人
     *
     * @return
     */
    @GetMapping("/createNameList")
    public Response<List<CreateVo>> createNameList() {
        return PageInfoUtil.smartSuccess(invoiceCommonService.createNameList());
    }


    /**
     * 预新增视图
     *
     * @param chargeId
     * @return
     */
    @GetMapping("/preAddView")
    public Response<CfInvoiceCommonPreVo> preAddView(@RequestParam Long chargeId) {
        return PageInfoUtil.smartSuccess(invoiceCommonService.preAddView(chargeId));
    }

    /**
     * 新增
     *
     * @return
     */
    @PostMapping("/add")
    public Response<Integer> add(@Validated @RequestBody InvoiceCommonAddDto invoiceCommonAddDto) {
        return PageInfoUtil.smartSuccess(invoiceCommonService.add(invoiceCommonAddDto));
    }

    /**
     * 列表
     *
     * @return
     */
    @PostMapping("/list")
    public Response<PageInfo<CfInvoiceCommonVo>> list(@SearchAuthority @RequestBody InvoiceCommonDto invoiceCommonDto) {
        return PageInfoUtil.smartSuccess(invoiceCommonService.list(invoiceCommonDto));
    }


    /**
     * 详情
     *
     * @param invoiceId
     * @return
     */
    @GetMapping("/detail")
    public Response<CfInvoiceCommonDetailVo> detail(@RequestParam Long invoiceId) {
        return PageInfoUtil.smartSuccess(invoiceCommonService.detail(invoiceId));
    }

    /**
     * 详情-结算
     *
     * @param invoiceId
     * @return
     */
    @GetMapping("/detail/settle")
    public Response<CfInvoiceCommonDetailSettleVo> detailSettle(@RequestParam Long invoiceId) {
        return PageInfoUtil.smartSuccess(invoiceCommonService.detailSettle(invoiceId));
    }


    /**
     * 更新
     *
     * @param invoiceCommonUpdateDto
     * @return
     */
    @PostMapping("/update")
    public Response<Integer> update(@Validated @RequestBody InvoiceCommonUpdateDto invoiceCommonUpdateDto) {
        return PageInfoUtil.smartSuccess(invoiceCommonService.update(invoiceCommonUpdateDto));
    }

    /**
     * 更新 状态
     *
     * @param invoiceUpdateStateDto
     * @return
     */
    @PostMapping("/approval/state")
    public Response<Integer> updateState(@Validated @RequestBody InvoiceUpdateStateDto invoiceUpdateStateDto) {
        CFRequestHolder.setInvalidThreadLocal(invoiceUpdateStateDto.getOperationContent());
        return PageInfoUtil.smartSuccess(invoiceCommonService.updateState(invoiceUpdateStateDto));
    }

    /**
     * 批量填写发票
     *
     * @param taxInvoiceFillDtos
     * @return
     */
    @PostMapping("/tax/invoice/fill/batch")
    public Response<Integer> taxInvoiceFillBatch(@Validated @RequestBody TaxInvoiceFillDtoList taxInvoiceFillDtos) {
        return PageInfoUtil.smartSuccess(invoiceCommonService.taxInvoiceFillBatch(taxInvoiceFillDtos));
    }


    /**
     * 填写发票
     *
     * @param taxInvoiceFillDto
     * @return
     */
    @PostMapping("/tax/invoice/fill")
    public Response<Integer> taxInvoiceFill(@Validated @RequestBody TaxInvoiceFillDto taxInvoiceFillDto) {
        return PageInfoUtil.smartSuccess(invoiceCommonService.taxInvoiceFill(taxInvoiceFillDto));
    }


    /**
     * 账单-结算-更新
     *
     * @param updateSettlementDto
     * @return
     */
    @PostMapping("invoice/settle/update")
    public Response<Integer> updateSettlementUpdate(@RequestBody @Validated UpdateSettlementDto updateSettlementDto) {
        return PageInfoUtil.smartSuccess(invoiceCommonService.updateSettlementUpdate(updateSettlementDto));
    }


    /**
     * 批量更新状态
     *
     * @param invoiceCommonUpdateBatchDto
     * @return
     */
    @PostMapping("update/batch/state")
    public Response<Integer> updateBatchState(@RequestBody @Validated InvoiceCommonUpdateBatchDto invoiceCommonUpdateBatchDto) {
        return PageInfoUtil.smartSuccess(invoiceCommonService.updateBatchState(invoiceCommonUpdateBatchDto));
    }


    /**
     * 导出
     * @param invoiceCommonDto
     */
    @PostMapping("list/export")
    public void export(@SearchAuthority @RequestBody InvoiceCommonDto invoiceCommonDto, HttpServletResponse response) {
        invoiceCommonService.listExport(invoiceCommonDto, response);
    }

    /**
     * 通过财务账单号获取对应ID
     * @param invoiceNo
     * @return
     */
    @GetMapping("getId")
    public Response<Long> getInvoiceIdByNo(@RequestParam("invoiceNo") String invoiceNo) {
        return PageInfoUtil.smartSuccess(invoiceCommonService.getInvoiceIdByNo(invoiceNo));
    }

}
