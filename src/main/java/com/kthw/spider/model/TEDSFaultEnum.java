package com.kthw.spider.model;

/**
 * Created by YFZX-WB on 2016/10/31.
 */
public enum TEDSFaultEnum {

    SPEED_LIMIT("限速运行", 1),
    STOP("立即停车检查", 1),
    FRONT_STATION("前方站停车检查", 2),
    FRONT_RAIL_STATION("前方办客站检查", 3),
    CANCEL_SPEED_LIMIT("取消限速", 4),
    OTHER("其他", 5);

    private String faultName;

    private int faultLevel;

    TEDSFaultEnum(String faultName, int faultLevel) {
        this.faultName = faultName;
        this.faultLevel = faultLevel;
    }

    public static int getFaultLevel(String faultName) {
        for (TEDSFaultEnum fault: TEDSFaultEnum.values()) {
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
