package com.chenfan.finance.service;

import com.chenfan.common.vo.Response;
import com.chenfan.finance.model.TocReportRp;
import com.chenfan.finance.model.bo.TocAlipayOrigin;
import com.chenfan.finance.model.dto.TocReportDto;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Author Wen.Xiao
 * @Description // 处理toc 数据业务
 * @Date 2021/3/2  15:58
 * @Version 1.0
 */
public interface TocDataService {
    /**
     * 导入支付宝原始流水表
     * @param files
     * @param lasts
     * @return
     * @throws Exception
     */
    Response importData(MultipartFile files, MultipartFile[] lasts) throws Exception;

    /**
     * 按周报表
     * @param tocReportByWeek
     * @return
     */
    Response<PageInfo<TocReportRp.TocReportRpByWeek>> getDataOfWeek(@RequestBody TocReportDto.TocReportByWeek tocReportByWeek);
    /**
     * 匹配成功的支付宝流水列表
     * @param tocReportBySuccess
     * @return
     */
    Response<PageInfo<TocReportRp.TocReportRpBySuccess>> getDataOfSuccess(@RequestBody TocReportDto.TocReportBySuccess tocReportBySuccess);
    /**
     * 匹配失败的支付宝流水列表
     * @param tocReportByFailure
     * @return
     */
    Response<PageInfo<TocAlipayOrigin>> getDataOfFailure(@RequestBody TocReportDto.TocReportByFailure tocReportByFailure);
    /**
     * 关联的支付宝流水等详情
     * @param mappingDetailId
     * @return
     */
    Response<List<TocReportRp.TocReportRpByDetail>> getDataListOfId(Integer  mappingDetailId);


    /**
     * 按照导出
     * @param tocReportByWeek
     * @param response
     */
    void getDataOfWeekOfExport(TocReportDto.TocReportByWeek tocReportByWeek, HttpServletResponse response);

    /**
     * 成功按周导出
     * @param tocReportBySuccess
     * @param response
     */
   void getDataOfSuccessOfExport(TocReportDto.TocReportBySuccess tocReportBySuccess, HttpServletResponse response);

    /**
     * 匹配失败得数据
     * @param tocReportByFailure
     * @param response
     */
    void getDataOfFailureOfExport(TocReportDto.TocReportByFailure tocReportByFailure, HttpServletResponse response);

    /**
     * 推送特定品牌数据到u8
     * @param brandIds
     * @return
     */
    Response pushDataToU8ByBrandId(List<Integer> brandIds, Date pushDate);

    /**
     * 异步调用
     * @param files
     */
    void  insertDataForFile(List<File> files);
}
