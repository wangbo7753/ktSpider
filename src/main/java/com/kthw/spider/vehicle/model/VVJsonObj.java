package com.kthw.spider.vehicle.model;

import java.util.List;

/**
 * Created by YFZX-WB on 2016/12/19.
 */
public class VVJsonObj {

    private String total;

    private List<VVDSBean> rows;

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public List<VVDSBean> getRows() {
        return rows;
    }

    public void setRows(List<VVDSBean> rows) {
        this.rows = rows;
    }

}
