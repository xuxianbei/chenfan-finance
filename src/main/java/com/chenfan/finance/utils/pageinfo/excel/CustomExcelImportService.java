package com.chenfan.finance.utils.pageinfo.excel;

import cn.afterturn.easypoi.excel.imports.ExcelImportService;

import java.lang.reflect.Field;

/**
 * @author: xuxianbei
 * Date: 2021/3/24
 * Time: 11:48
 * Version:V1.0
 */
public class CustomExcelImportService extends ExcelImportService {

    public CustomExcelImportService() {
        super();
        try {
            Field field = ExcelImportService.class.getDeclaredField("cellValueServer");
            field.setAccessible(true);
            field.set(this, new CustomCellValueService());
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
