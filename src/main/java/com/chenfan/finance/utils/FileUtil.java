package com.chenfan.finance.utils;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import com.chenfan.finance.utils.pageinfo.excel.ExcelExportStatisticStyler;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * @author SXR
 * @date 2018/7/13
 */
public class FileUtil {

    public static void exportExcel(
            List<?> list,
            String title,
            String sheetName,
            Class<?> pojoClass,
            String fileName,
            boolean isCreateHeader,
            HttpServletResponse response)
            throws Exception {
        ExportParams exportParams = new ExportParams(title, sheetName);
        exportParams.setCreateHeadRows(isCreateHeader);
        defaultExport(list, pojoClass, fileName, response, exportParams);
    }

    public static void exportExcel(
            List<?> list,
            String title,
            String sheetName,
            Class<?> pojoClass,
            String fileName,
            HttpServletResponse response)
            throws Exception {
        defaultExport(list, pojoClass, fileName, response, new ExportParams(title, sheetName));
    }

    public static void exportExcel(
            List<?> list,
            String title,
            String secondTitle,
            String sheetName,
            Class<?> pojoClass,
            String fileName,
            HttpServletResponse response)
            throws Exception {
        defaultExport(list, pojoClass, fileName, response, new ExportParams(title, secondTitle, sheetName));
    }

    public static void exportExcelV2(
            List<?> list,
            String title,
            String secondTitle,
            String sheetName,
            Class<?> pojoClass,
            String fileName,
            HttpServletResponse response)
            throws Exception {
        ExportParams exportParams = new ExportParams(title, sheetName,ExcelType.XSSF);
        exportParams.setSecondTitle(secondTitle);
        defaultExport(list, pojoClass, fileName, response,exportParams );
    }

    public static void exportExcel(
            List<Map<String, Object>> list, String fileName, HttpServletResponse response)
            throws Exception {
        defaultExport(list, fileName, response);
    }

    private static void defaultExport(
            List<?> list,
            Class<?> pojoClass,
            String fileName,
            HttpServletResponse response,
            ExportParams exportParams)
            throws Exception {
        exportParams.setStyle(ExcelExportStatisticStyler.class);
        Workbook workbook = ExcelExportUtil.exportExcel(exportParams, pojoClass, list);
        if (workbook != null) {
            downLoadExcel(fileName, response, workbook);
        }
    }

    private static void downLoadExcel(
            String fileName, HttpServletResponse response, Workbook workbook) throws Exception {

        try (OutputStream out = response.getOutputStream())
        {
            response.setCharacterEncoding("UTF-8");
            response.setHeader("content-Type", "application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            workbook.write(baos);
            response.setHeader("Content-Length", String.valueOf(baos.size()));
            out.write( baos.toByteArray() );
        } catch (IOException e) {
            try {
                throw new Exception(e.getMessage());
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
    }

    private static void defaultExport(
            List<Map<String, Object>> list, String fileName, HttpServletResponse response)
            throws Exception {
        Workbook workbook = ExcelExportUtil.exportExcel(list, ExcelType.HSSF);
        if (workbook != null) {
            downLoadExcel(fileName, response, workbook);
        }
    }

    public static <T> List<T> importExcel(
            String filePath, Integer titleRows, Integer headerRows, Class<T> pojoClass)
            throws Exception {
        if (StringUtils.isBlank(filePath)) {
            return null;
        }
        ImportParams params = new ImportParams();
        params.setTitleRows(titleRows);
        params.setHeadRows(headerRows);
        List<T> list;
        try {
            list = ExcelImportUtil.importExcel(new File(filePath), pojoClass, params);
        } catch (NoSuchElementException e) {
            throw new Exception("模板不能为空");
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
        return list;
    }

    public static <T> List<T> importExcel(
            MultipartFile file, Integer titleRows, Integer headerRows, Class<T> pojoClass)
            throws Exception {
        if (file == null) {
            return null;
        }
        ImportParams params = new ImportParams();
        params.setTitleRows(titleRows);
        params.setHeadRows(headerRows);
        List<T> list;
        try {
            list = ExcelImportUtil.importExcel(file.getInputStream(), pojoClass, params);
        } catch (NoSuchElementException e) {
            throw new Exception("excel文件不能为空");
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
        return list;
    }
}
