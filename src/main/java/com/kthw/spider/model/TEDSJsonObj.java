package com.kthw.spider.model;

import java.util.List;

/**
 * Created by YFZX-WB on 2016/10/9.
 */
public class TEDSJsonObj {

    private String total;

    private List<TEDSResponseBean> rows;

    private String msg;

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public List<TEDSResponseBean> getRows() {
        return rows;
    }

    public void setRows(List<TEDSResponseBean> rows) {
        this.rows = rows;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
