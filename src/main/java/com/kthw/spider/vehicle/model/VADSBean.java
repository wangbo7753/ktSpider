package com.kthw.spider.vehicle.model;

import org.apache.commons.lang.StringUtils;

/**
 * Created by YFZX-WB on 2016/12/21.
 */
public class VADSBean extends VFDSBean {

    private String lineType;

    private String axleCount;

    public String getLineType() {
        return lineType;
    }

    public void setLineType(String lineType) {
        this.lineType = lineType;
    }

    public String getAxleCount() {
        return axleCount;
    }

    public void setAxleCount(String axleCount) {
        this.axleCount = axleCount;
    }

    public String getSpeed() {
        String speed = getAvgSpeed().trim();
        if (StringUtils.isBlank(speed)) {
            speed = "0";
        }
        if (speed.startsWith("L")) {
            speed = speed.substring(2);
        }
        if (speed.endsWith("km/h")) {
            speed = speed.replace("km/h", "");
        }
        return speed;
    }
}
