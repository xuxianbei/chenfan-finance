package com.chenfan.finance.commons.utils.poiutil;

import cn.afterturn.easypoi.entity.vo.NormalExcelConstants;
import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import com.chenfan.finance.config.UserVoConstextHolder;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.ui.ModelMap;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.util.List;

/**
 * @Author: guangyu
 * @Date: 2018/8/11 15:16
 * @Description:
 */
public class ExportExcelUtils {

    /**
     * @see: 导出excel(xlsx)
     * @param: [list, title, sheetName, pojoClass, fileName, response]
     * @author: xuguangyu@thechenfan.com
     * @date: 2018/8/11 15:33
     */
    public static void exportExcel(List<?> list, String title, String sheetName, Class<?> pojoClass,String fileName, HttpServletResponse response){
        ExportParams exportParams = new ExportParams(title, sheetName);
        exportParams.setType(ExcelType.HSSF);
        defaultExport(list, pojoClass, fileName, response, exportParams);
    }

    private static void defaultExport(List<?> list, Class<?> pojoClass, String fileName, HttpServletResponse response, ExportParams exportParams) {
        Workbook workbook = ExcelExportUtil.exportExcel(exportParams,pojoClass,list);
        if (workbook != null) {
            downLoadExcel(fileName, response, workbook);
        }
    }

    private static void downLoadExcel(String fileName, HttpServletResponse response, Workbook workbook) {
        try {
            response.setCharacterEncoding("UTF-8");
            response.setHeader("content-Type", "application/vnd.ms-excel");
            response.setHeader("Content-Disposition",
                    "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static <T> void exportXlsAll(ModelMap map, List<T> list, Class<? extends T> clazz, String title) {
        map.put(NormalExcelConstants.FILE_NAME, LocalDate.now() + title);
        map.put(NormalExcelConstants.CLASS, clazz);
        map.put(NormalExcelConstants.PARAMS, new ExportParams(title, "导出人:" + UserVoConstextHolder.getUserVo().getRealName(),
                "导出信息"));
        map.put(NormalExcelConstants.DATA_LIST, list);
    }


}
