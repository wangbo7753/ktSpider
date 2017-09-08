package com.kthw.spider.model;

import java.util.List;

/**
 * Created by YFZX-WB on 2016/10/9.
 */
public class TVDSJsonObj {

    private String total;

    private List<TVDSBean> rows;

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public List<TVDSBean> getRows() {
        return rows;
    }

    public void setRows(List<TVDSBean> rows) {
        this.rows = rows;
    }
}
