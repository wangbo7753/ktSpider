package com.kthw.spider.vehicle.model;

import java.util.List;

/**
 * Created by YFZX-WB on 2016/12/17.
 */
public class VEJsonObj {

    private String total;

    private List<VEDSBean> rows;

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public List<VEDSBean> getRows() {
        return rows;
    }

    public void setRows(List<VEDSBean> rows) {
        this.rows = rows;
    }

}
