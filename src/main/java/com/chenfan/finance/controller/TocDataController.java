package com.chenfan.finance.controller;

import com.chenfan.common.vo.Response;

import com.chenfan.finance.model.TocReportRp;
import com.chenfan.finance.model.bo.TocAlipayOrigin;
import com.chenfan.finance.model.dto.TocReportDto;
import com.chenfan.finance.service.TocDataService;
import com.github.pagehelper.PageInfo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import java.util.Date;
import java.util.List;


/**
 * @Author Wen.Xiao
 * @Description // toc 流水导入
 * @Date 2021/3/2  15:57
 * @Version 1.0
 */
@Api(tags = "流水导入")
@Slf4j
@RestController
@RequestMapping("toc")
public class TocDataController {

    @Resource
    TocDataService tocImportDataService;

    /**
     * 导入原始单据
     */
    @ApiOperation(value = "导入原始单据", notes = "导入原始单据", produces = "application/json")
    @PostMapping(value = "importData")
    public Response<Object> importData(@RequestParam(value = "files",required = false) MultipartFile files, @RequestParam(value = "lasts",required = false) MultipartFile[] lasts) throws Exception {
        return tocImportDataService.importData(files,lasts);
    }

    /**
     * 匹配成功后按周推送u8的数据列表
     * @param tocReportByWeek
     * @return
     */
    @ApiOperation(value = "by周匹配成功报表--按周汇总数据", notes = "by周匹配成功报表--按周汇总数据", produces = "application/json")
    @PostMapping(value = "getDataOfWeek" )
    public Response<PageInfo<TocReportRp.TocReportRpByWeek>>  getDataOfWeek(@RequestBody TocReportDto.TocReportByWeek tocReportByWeek){
        return tocImportDataService.getDataOfWeek(tocReportByWeek);
    }

    /**
     * 匹配成功后按周推送u8的数据列表
     * @param tocReportByWeek
     * @param response
     */
    @ApiOperation(value = "数据导出-by周匹配成功报表--按周汇总数据", notes = "数据导出-by周匹配成功报表--按周汇总数据", produces = "application/json")
    @PostMapping(value = "getDataOfWeek/export" )
    public void getDataOfWeekOfExport(@RequestBody TocReportDto.TocReportByWeek tocReportByWeek, HttpServletResponse response){
         tocImportDataService.getDataOfWeekOfExport(tocReportByWeek,response);
    }

    /**
     * 支付宝流水mapping的结果 (收入和支出都存在)
     * @param tocReportBySuccess
     * @return
     */
    @ApiOperation(value = "by周支付宝流水----mapping的结果", notes = "by周支付宝流水----mapping的结果", produces = "application/json")
    @PostMapping(value = "getDataOfSuccess" )
    public Response<PageInfo<TocReportRp.TocReportRpBySuccess>> getDataOfSuccess(@RequestBody TocReportDto.TocReportBySuccess tocReportBySuccess){
        return tocImportDataService.getDataOfSuccess(tocReportBySuccess);
    }

    /**
     * 数据导出-by周支付宝流水----mapping的结果
     * @param tocReportBySuccess
     * @param response
     */
    @ApiOperation(value = "数据导出-by周支付宝流水----mapping的结果", notes = "数据导出-by周支付宝流水----mapping的结果", produces = "application/json")
    @PostMapping(value = "getDataOfSuccess/export" )
    public void getDataOfSuccessOfExport(@RequestBody TocReportDto.TocReportBySuccess tocReportBySuccess, HttpServletResponse response){
         tocImportDataService.getDataOfSuccessOfExport(tocReportBySuccess,response);
    }

    /**
     * 原始支付宝流水(失败或者未匹配,成功的流水数据将不再这个地方显示)
     * @param tocReportByFailure
     * @return
     */
    @ApiOperation(value = "by周匹配失败报表-- 匹配异常流水报表", notes = "by周匹配失败报表-- 匹配异常流水报表", produces = "application/json")
    @PostMapping(value = "getDataOfFailure" )
    public Response<PageInfo<TocAlipayOrigin>> getDataOfFailure(@RequestBody TocReportDto.TocReportByFailure tocReportByFailure){
        return tocImportDataService.getDataOfFailure(tocReportByFailure);
    }

    /**
     * 数据导出-by周匹配失败报表-- 匹配异常流水报表
     * @param tocReportByFailure
     * @param response
     */
    @ApiOperation(value = "数据导出-by周匹配失败报表-- 匹配异常流水报表", notes = "数据导出-by周匹配失败报表-- 匹配异常流水报表", produces = "application/json")
    @PostMapping(value = "getDataOfFailure/export" )
    public void getDataOfFailureOfExport(@RequestBody TocReportDto.TocReportByFailure tocReportByFailure, HttpServletResponse response){
         tocImportDataService.getDataOfFailureOfExport(tocReportByFailure,response);
    }
    /**
     * 根据按周汇总的id 查询当前关联的销售单或者退次单
     * @param mappingDetailId
     * @return
     */
    @ApiOperation(value = "by周详情（SKU对应的详情）--按周汇总单条关联的销售单详情", notes = "by周详情（SKU对应的详情）--按周汇总单条关联的销售单详情", produces = "application/json")
    @GetMapping(value = "getDataListOfId/{mappingDetailId}")
    public Response<List<TocReportRp.TocReportRpByDetail>> getDataListOfId(@PathVariable("mappingDetailId") Integer  mappingDetailId){
        return tocImportDataService.getDataListOfId(mappingDetailId);
    }

    @ApiOperation(value = "推送上个月品牌数据到u8", notes = "推送上个月品牌数据到u8", produces = "application/json")
    @GetMapping("/pushData")
    public  Response pushDataToU8ByBrandId(@ApiParam("品牌编码,不传的时候默认推送所有的品牌") @RequestParam(value = "brandIds",required = false) List<Integer> brandIds, @ApiParam("推送月份,不填默认上月 yyyy-MM-dd") @RequestParam(value = "pushDate",required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date pushDate){
        return tocImportDataService.pushDataToU8ByBrandId(brandIds,pushDate);
    }



}
