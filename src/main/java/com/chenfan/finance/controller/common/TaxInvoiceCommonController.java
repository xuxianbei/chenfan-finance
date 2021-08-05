package com.chenfan.finance.controller.common;

import com.chenfan.common.config.Constant;
import com.chenfan.common.vo.Response;
import com.chenfan.finance.commons.aspect.CFRequestHolder;
import com.chenfan.finance.exception.ModuleBizState;
import com.chenfan.finance.model.dto.*;
import com.chenfan.finance.model.vo.*;
import com.chenfan.finance.service.common.TaxInvoiceCommonService;
import com.chenfan.finance.utils.pageinfo.PageInfoUtil;
import com.chenfan.privilege.common.config.SearchAuthority;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 开票管理
 *
 * @author: xuxianbei
 * Date: 2021/3/5
 * Time: 14:12
 * Version:V1.0
 */
@Slf4j
@RestController
@RequestMapping("/tax/invoice")
public class TaxInvoiceCommonController {

    @Autowired
    private TaxInvoiceCommonService taxInvoiceCommonService;


    /**
     * 预新增视图
     *
     * @param taxInvoiceCommonPreAddViewDto
     * @return
     */
    @PostMapping("/preAddView")
    public Response<CfTaxInvoiceCommonPreVo> preAddView(@RequestBody @Validated TaxInvoiceCommonPreAddViewDto taxInvoiceCommonPreAddViewDto) {
        return PageInfoUtil.smartSuccess(taxInvoiceCommonService.preAddView(taxInvoiceCommonPreAddViewDto));
    }


    /**
     * 新增
     *
     * @param taxInvoiceAddDto
     * @return
     */
    @PostMapping("add")
    public Response<Long> add(@Validated @RequestBody TaxInvoiceAddDto taxInvoiceAddDto, @RequestHeader(Constant.AUTHORIZATION_TOKEN) String token) {
        return PageInfoUtil.smartSuccess(taxInvoiceCommonService.add(taxInvoiceAddDto));
    }

    /**
     * 新增并提交
     *
     * @param taxInvoiceAddDto
     * @return
     */
    @ApiOperation("新增并提交")
    @PostMapping("/addAndSubmit")
    public Response addAndSubmit(@Validated @RequestBody TaxInvoiceAddDto taxInvoiceAddDto) {
        return PageInfoUtil.smartSuccess(taxInvoiceCommonService.addAndSubmit(taxInvoiceAddDto));
    }


    /**
     * 列表
     *
     * @param taxInvoiceCommonListDto
     * @return
     */
    @PostMapping("list")
    public Response<PageInfo<TaxInvoiceVo>> list(@SearchAuthority TaxInvoiceCommonListDto taxInvoiceCommonListDto) {
        log.info("获取的关联的brandIds:{},companyIds:{},userIds:{}", taxInvoiceCommonListDto.getBrandIds(),
                 taxInvoiceCommonListDto.getCompanyIds(), taxInvoiceCommonListDto.getUserIds());
        return PageInfoUtil.smartSuccess(taxInvoiceCommonService.list(taxInvoiceCommonListDto));
    }

    /**
     * 导出excel
     */
    @ApiOperation("导出")
    @PostMapping("/export")
    public void export(@RequestBody TaxInvoiceCommonListDto taxInvoiceCommonListDto, HttpServletResponse response) {
        taxInvoiceCommonService.export(taxInvoiceCommonListDto, response);
    }

    /**
     * 详情
     *
     * @param taxInvoiceId
     * @return
     */
    @GetMapping("detail")
    public Response<TaxInvoiceDetailVo> detail(@RequestParam Long taxInvoiceId) {
        return PageInfoUtil.smartSuccess(taxInvoiceCommonService.detail(taxInvoiceId));
    }

    /**
     * 更新
     *
     * @param taxInvoiceCommonUpdateDto
     * @return
     */
    @PostMapping("update")
    public Response<Integer> update(@RequestBody @Validated TaxInvoiceCommonUpdateDto taxInvoiceCommonUpdateDto) {
        return PageInfoUtil.smartSuccess(taxInvoiceCommonService.update(taxInvoiceCommonUpdateDto));
    }

    /**
     * 更新并提交
     *
     * @param taxInvoiceCommonUpdateDto
     * @return
     */
    @ApiOperation("更新并提交")
    @PostMapping("updateAndSubmit")
    public Response<Integer> updateAndSubmit(@RequestBody @Validated TaxInvoiceCommonUpdateDto taxInvoiceCommonUpdateDto) {
        return PageInfoUtil.smartSuccess(taxInvoiceCommonService.updateAndSubmit(taxInvoiceCommonUpdateDto));
    }

    /**
     * 更新状态
     *
     * @param taxInvoiceCommonUpdateStatusDto
     * @return
     */
    @PostMapping("update/status")
    public Response<Integer> updateStatus(@RequestBody @Validated TaxInvoiceCommonUpdateStatusDto taxInvoiceCommonUpdateStatusDto) {
        CFRequestHolder.setInvalidThreadLocal(taxInvoiceCommonUpdateStatusDto.getOperationContent());
        return PageInfoUtil.smartSuccess(taxInvoiceCommonService.updateStatus(taxInvoiceCommonUpdateStatusDto));
    }


    /**
     * 更新-填写发票
     *
     * @param taxInvoiceCommonUpdateInvoiceDto
     * @return
     */
    @PostMapping("update/invoice")
    public Response<Integer> updateInvoice(@RequestBody @Validated TaxInvoiceCommonUpdateInvoiceDto taxInvoiceCommonUpdateInvoiceDto) {
        return PageInfoUtil.smartSuccess(taxInvoiceCommonService.updateInvoice(taxInvoiceCommonUpdateInvoiceDto));
    }

    /**
     * 开票结算主体
     *
     * @param value
     * @return
     */
    @GetMapping("/fields/balance")
    public Response<List<String>> fieldsBalances(String value) {
        return PageInfoUtil.smartSuccess(taxInvoiceCommonService.fieldsBalances(value));
    }

    /**
     * 批量开票导入
     *
     * @param
     */
    @ApiOperation("批量开票导入")
    @PostMapping("/import")
    public Response<String> importInvoice(MultipartFile multipartFile) {
        String fileId = taxInvoiceCommonService.importInvoice(multipartFile);
        if (!StringUtils.isBlank(fileId)) {
            return new Response<>(ModuleBizState.IMPORT_FAILED_WITH_REASON_DOWNLOAD, fileId);
        }
        return Response.success(fileId);
    }

    /**
     * 下载批量开票失败原因
     */
    @ApiOperation("下载批量开票失败原因")
    @GetMapping("/download")
    public void download(HttpServletResponse response, String fileId) {
        taxInvoiceCommonService.download(response, fileId);
    }

    @ApiOperation("新增并设置已开票")
    @PostMapping("/addAndComplete")
    public Response<Integer> addAndComplete(@Validated @RequestBody TaxInvoiceAddDto taxInvoiceAddDto, @RequestHeader(Constant.AUTHORIZATION_TOKEN) String token) {
        return Response.error(taxInvoiceCommonService.addAndComplete(taxInvoiceAddDto));
    }

    /**
     * 查询mcn收入合同关联发票
     *
     * @param contractCode
     * @return Response<List<McnTaxInvoiceVo>>
     */
    @GetMapping("/getMcnTaxInvoice")
    public Response<List<McnTaxInvoiceVo>> getMcnTaxInvoice(@RequestParam String contractCode) {
        return Response.success(taxInvoiceCommonService.getMcnTaxInvoice(contractCode));
    }
}
