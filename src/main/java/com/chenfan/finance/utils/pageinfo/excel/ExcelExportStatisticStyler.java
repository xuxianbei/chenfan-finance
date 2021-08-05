package com.chenfan.finance.utils.pageinfo.excel;

import cn.afterturn.easypoi.excel.entity.params.ExcelExportEntity;
import cn.afterturn.easypoi.excel.entity.vo.BaseEntityTypeConstants;
import cn.afterturn.easypoi.excel.export.styler.ExcelExportStylerDefaultImpl;
import org.apache.poi.ss.usermodel.*;

/**
 * author:   tangwei
 * Date:     2021/4/23 16:51
 * Description: 自定义导出单元格样式
 */
public class ExcelExportStatisticStyler extends ExcelExportStylerDefaultImpl {
    private CellStyle numberCellStyle;

    public ExcelExportStatisticStyler(Workbook workbook) {
        super(workbook);
        createNumberCellStyler();
    }

    /**
     * 数字类型样式
     */
    private void createNumberCellStyler() {
        numberCellStyle = workbook.createCellStyle();
        numberCellStyle.setAlignment(HorizontalAlignment.CENTER);
        numberCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        numberCellStyle.setDataFormat((short) BuiltinFormats.getBuiltinFormat("0.00"));
        numberCellStyle.setWrapText(true);
    }

    @Override
    public CellStyle getStyles(boolean noneStyler, ExcelExportEntity entity) {
        if (entity != null && BaseEntityTypeConstants.DOUBLE_TYPE.equals(entity.getType())) {
            return numberCellStyle;
        }
        return super.getStyles(noneStyler, entity);
    }
}