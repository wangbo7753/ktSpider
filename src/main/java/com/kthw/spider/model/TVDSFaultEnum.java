package com.kthw.spider.model;

/**
 * Created by YFZX-WB on 2016/10/31.
 */
public enum TVDSFaultEnum {

    STOP("拦停故障", 1),
    CONFIRM("确认故障", 2),
    FORECAST("预报故障", 3),
    OTHER("其他", 4);

    private String faultName;

    private int faultLevel;

    TVDSFaultEnum(String faultName, int faultLevel) {
        this.faultName = faultName;
        this.faultLevel = faultLevel;
    }

    public static int getFaultLevel(String faultName) {
        for (TVDSFaultEnum fault: TVDSFaultEnum.values()) {
            if (fault.getFaultName().equals(faultName)) {
                return fault.getFaultLevel();
            }
        }
        return OTHER.getFaultLevel();
    }

    public String getFaultName() {
        return faultName;
    }

    public int getFaultLevel() {
        return faultLevel;
    }

}
