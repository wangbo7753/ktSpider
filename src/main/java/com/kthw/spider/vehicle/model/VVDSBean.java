package com.kthw.spider.vehicle.model;

/**
 * Created by YFZX-WB on 2016/12/17.
 */
public class VVDSBean {

    private String STrainNo;  // 车次

    private int NMarshallingCount;  // 总辆数

    private String SPassTime;  // 过车时间

    private String SProbeStationName;  // 探测站

    private String SWorkTypeName;  // 作业类别

    private String SRunningDirection;  // 运行方向

    private int NAxleCount;  // 总轴数

    private int SAverageSpeed;  // 平均车速

    private String SBureaName;  // 担当局

    public String getSTrainNo() {
        return STrainNo;
    }

    public void setSTrainNo(String STrainNo) {
        this.STrainNo = STrainNo;
    }

    public int getNMarshallingCount() {
        return NMarshallingCount;
    }

    public void setNMarshallingCount(int NMarshallingCount) {
        this.NMarshallingCount = NMarshallingCount;
    }

    public String getSPassTime() {
        return SPassTime;
    }

    public void setSPassTime(String SPassTime) {
        this.SPassTime = SPassTime;
    }

    public String getSProbeStationName() {
        return SProbeStationName;
    }

    public void setSProbeStationName(String SProbeStationName) {
        this.SProbeStationName = SProbeStationName;
    }

    public String getSWorkTypeName() {
        return SWorkTypeName;
    }

    public void setSWorkTypeName(String SWorkTypeName) {
        this.SWorkTypeName = SWorkTypeName;
    }

    public String getSRunningDirection() {
        return SRunningDirection;
    }

    public void setSRunningDirection(String SRunningDirection) {
        this.SRunningDirection = SRunningDirection;
    }

    public int getNAxleCount() {
        return NAxleCount;
    }

    public void setNAxleCount(int NAxleCount) {
        this.NAxleCount = NAxleCount;
    }

    public int getSAverageSpeed() {
        return SAverageSpeed;
    }

    public void setSAverageSpeed(int SAverageSpeed) {
        this.SAverageSpeed = SAverageSpeed;
    }

    public String getSBureaName() {
        return SBureaName;
    }

    public void setSBureaName(String SBureaName) {
        this.SBureaName = SBureaName;
    }
}
