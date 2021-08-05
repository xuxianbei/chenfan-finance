package com.chenfan.finance.model.vo;

import com.github.pagehelper.PageInfo;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author mbji
 */
@Data
public class AdvancePayListVo {
    private PageInfo<AdvancePayVo> page;
    private BigDecimal totalCount;

    private BigDecimal count;


    public AdvancePayListVo(PageInfo<AdvancePayVo> page, BigDecimal totalCount, BigDecimal count) {
        this.page = page;
        this.totalCount = totalCount;
        this.count = count;
    }

    public BigDecimal getCount() {
        return count;
    }

    public void setCount(BigDecimal count) {
        this.count = count;
    }


    public AdvancePayListVo() {
    }

    public PageInfo<AdvancePayVo> getPage() {
        return page;
    }

    public void setPage(PageInfo<AdvancePayVo> page) {
        this.page = page;
    }

    public BigDecimal getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(BigDecimal totalCount) {
        this.totalCount = totalCount;
    }
}
