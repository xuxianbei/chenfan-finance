package com.chenfan.finance.model.vo;

import com.github.pagehelper.PageInfo;
import lombok.Data;

/**
 * @author 2062
 */
@Data
public class CfPoHeaderListVo {
    private PageInfo<CfPoHeaderVo> page;

    private String account;

    private String departmentName;

    private String roleName;

    private String currentTime;

    /*public CfPoHeaderListVo(PageInfo<CfPoHeaderVo> page, String account, String departmentName, String roleName, Date currentTime) {
        this.page = page;
        this.account = account;
        this.departmentName = departmentName;
        this.roleName = roleName;
        this.currentTime = currentTime;
    }*/

    public PageInfo<CfPoHeaderVo> getPage() {
        return page;
    }

    public void setPage(PageInfo<CfPoHeaderVo> page) {
        this.page = page;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }

}
