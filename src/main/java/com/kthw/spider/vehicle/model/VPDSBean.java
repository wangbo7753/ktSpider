package com.kthw.spider.vehicle.model;

/**
 * Created by YFZX-WB on 2016/12/22.
 */
public class VPDSBean extends VFDSBean {

    private String lineType;

    private String reportId;

    public String getLineType() {
        return lineType;
    }

    public void setLineType(String lineType) {
        this.lineType = lineType;
    }

    public String getReportId() {
        if (reportId != null && reportId.indexOf("?") != -1) {
            reportId = reportId.substring(reportId.indexOf("?") + 1);
            for (String param : reportId.split("&")) {
                if (param.startsWith("IReportId=")) {
                    return param.split("=")[1];
                }
            }
        }
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }
}
