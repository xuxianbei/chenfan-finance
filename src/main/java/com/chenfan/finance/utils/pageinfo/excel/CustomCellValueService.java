package com.chenfan.finance.utils.pageinfo.excel;

import cn.afterturn.easypoi.excel.entity.params.ExcelImportEntity;
import cn.afterturn.easypoi.excel.imports.CellValueService;
import cn.afterturn.easypoi.handler.inter.IExcelDataHandler;
import cn.afterturn.easypoi.handler.inter.IExcelDictHandler;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;

import java.util.Date;
import java.util.Map;

/**
 * @author: xuxianbei
 * Date: 2021/3/24
 * Time: 11:50
 * Version:V1.0
 */
public class CustomCellValueService extends CellValueService {



    @Override
    public Object getValue(IExcelDataHandler<?> dataHandler, Object object, Object cell,
                           Map<String, ExcelImportEntity> excelParams,
                           String titleString, IExcelDictHandler dictHandler) throws Exception {
        try {
            //解决replace/format 字符串前后空格
            if (cell != null && cell instanceof Cell) {
                Cell c = (Cell)cell;
                switch(c.getCellType()) {
                    case STRING:
                        c.setCellValue(StringUtils.trim(c.getRichStringCellValue() == null ? "" : c.getRichStringCellValue().getString()));
                        break;
                    default:;
                }

            }
            Object result = super.getValue(dataHandler, object, cell, excelParams, titleString, dictHandler);
            if (result instanceof String) {
                return ((String) result).trim();
            }
            return result;
        } catch (Exception e) {
            if (excelParams.get(titleString).getMethod().getParameterTypes()[0].equals(Date.class)) {
                return null;
            }
            return -1;
        }
    }
}
