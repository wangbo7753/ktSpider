package com.kthw.spider.param;

import java.util.Map;

/**
 * Created by YFZX-WB on 2016/9/7.
 */
public class TFDSRequestParam {

    private String b_date;

    private String b_h;

    private String b_m;

    private String e_date;

    private String e_h;

    private String e_m;

    private String station_j;

    private String station_d;

    private String station_c;

    private String faultTypeSql;

    private String reportParamsId;

    private String report1CurrPage;

    private String report1CachedId;

    public TFDSRequestParam(Map<String, String> params) {
        if (params != null) {
            this.b_date = params.get("B_DATE");
            this.b_h = params.get("B_H");
            this.b_m = params.get("B_M");
            this.e_date = params.get("E_DATE");
            this.e_h = params.get("E_H");
            this.e_m = params.get("E_M");
            this.station_j = params.get("station_j");
            this.station_d = params.get("station_d");
            this.station_c = params.get("station_c") == null ? "" : params.get("station_c");
            this.faultTypeSql = params.get("FAULT_TYPE_SQL");
            this.reportParamsId = params.get("reportParamsId");
            this.report1CurrPage = params.get("report1_currPage");
            this.report1CachedId = params.get("report1_cachedId");
        }
    }

    public String toString() {
        return "?station_d=" + this.station_d
            + "&SORT_SQL=+ORDER+BY+PASS_TIME+DESC"
            + "&B_H=" + this.b_h
            + "&E_H=" + this.e_h
            + "&station_j=" + this.station_j
            + "&B_DATE=" + this.b_date
            + "&B_M=" + this.b_m
            + "&E_DATE=" + this.e_date
            + "&E_M=" + this.e_m
            + "&station_c=" + this.station_c
            + "&FAULT_TYPE_SQL=" + this.faultTypeSql
            + "&reportParamsId=" + this.reportParamsId
            + "&report1_currPage=" + this.report1CurrPage
            + "&report1_cachedId=" + this.report1CachedId;
    }

    public String initRequestCode() {
        return "?SORT_SQL=+ORDER+BY+PASS_TIME+DESC"
            + "&B_DATE=" + this.b_date
            + "&B_H=" + this.b_h
            + "&B_M=" + this.b_m
            + "&E_DATE=" + this.e_date
            + "&E_H=" + this.e_h
            + "&E_M=" + this.e_m
            + "&station_j=" + this.station_j
            + "&station_d=" + this.station_d
            + "&station_c=" + this.station_c
            + "&FAULT_TYPE_SQL=" + this.faultTypeSql;
    }
}
