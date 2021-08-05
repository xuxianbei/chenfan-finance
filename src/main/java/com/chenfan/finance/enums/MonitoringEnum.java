package com.chenfan.finance.enums;

import java.util.Objects;

/**
 * @Author Wen.Xiao
 * @Description // 告警枚举
 * @Date 2021/6/24  10:43
 * @Version 1.0
 */
public enum MonitoringEnum {
    po_quantity("cfquantity","采购下单数量"),
    po_taxUnitPrice("cftaxUnitPrice","采购下单的含税单价"),
    po_unitPrice("cfunitPrice","采购下单的无税单价"),
    po_taxRate("cftaxRate","税率"),
    po_markupUnitPrice("cfmarkupUnitPrice","加价不含税单价"),
    po_markupRate("cfmarkupRate","加价倍率"),
    po_conEndDate("cfconEndDate","采购合同截至交期"),
    cftax_unit_price("cftax_unit_price","含税单价"),
    cfunit_price("cfunit_price","不含税单价"),
    cftax_rate("cftax_rate","税率"),
    cfinventory_id("cfinventory_id","商品档案编号"),
    cfvendor_id("cfvendor_id","供应商ID"),
    cfbrand_id("cfbrand_id","品牌ID")
    ;
    private final String fileName;
    private final String notifyName;

    public String getFileName() {
        return fileName;
    }

    public String getNotifyName() {
        return notifyName;
    }

    MonitoringEnum(String fileName, String notifyName) {
        this.fileName = fileName;
        this.notifyName = notifyName;
    }

    public static String getEnumNotifyNameByFile(String fileName) {
        for (MonitoringEnum monitoringEnum : MonitoringEnum.values()) {
            if(Objects.nonNull(fileName)&&Objects.equals(monitoringEnum.getFileName().toUpperCase(),fileName.toUpperCase())){
                return monitoringEnum.getNotifyName();
            }

        }
        return null;
    }
}
