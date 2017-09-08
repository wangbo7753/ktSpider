package com.kthw.spider.model;

/**
 * Created by YFZX-WB on 2016/9/8.
 */
public class TFDSBean {

    private String crossDate;

    private String crossAddr;

    private String groupDigit;

    private String trainNum;

    private String carNum;

    private String trainModel;

    private String faultName;

    private String railGuard;

    private String faultConfirmor;

    private String detailUrl;

    public String getCrossDate() {
        return crossDate;
    }

    public void setCrossDate(String crossDate) {
        this.crossDate = crossDate;
    }

    public String getCrossAddr() {
        return crossAddr;
    }

    public void setCrossAddr(String crossAddr) {
        this.crossAddr = crossAddr;
    }

    public String getGroupDigit() {
        return groupDigit;
    }

    public void setGroupDigit(String groupDigit) {
        this.groupDigit = groupDigit;
    }

    public String getTrainNum() {
        return trainNum;
    }

    public void setTrainNum(String trainNum) {
        this.trainNum = trainNum;
    }

    public String getCarNum() {
        return carNum;
    }

    public void setCarNum(String carNum) {
        this.carNum = carNum;
    }

    public String getTrainModel() {
        return trainModel;
    }

    public void setTrainModel(String trainModel) {
        this.trainModel = trainModel;
    }

    public String getFaultName() {
        return faultName;
    }

    public void setFaultName(String faultName) {
        this.faultName = faultName;
    }

    public String getRailGuard() {
        return railGuard;
    }

    public void setRailGuard(String railGuard) {
        this.railGuard = railGuard;
    }

    public String getFaultConfirmor() {
        return faultConfirmor;
    }

    public void setFaultConfirmor(String faultConfirmor) {
        this.faultConfirmor = faultConfirmor;
    }

    public String getDetailUrl() {
        return detailUrl;
    }

    public void setDetailUrl(String detailUrl) {
        this.detailUrl = detailUrl;
    }

    public String toString() {
        return "TFDSBean[crossDate=" + this.crossDate + ", crossAddr=" + this.crossAddr + ", groupDigit=" + this.groupDigit
            + ", trainNum=" + this.trainNum + ", carNum=" + this.carNum + ", trainModel=" + this.trainModel
            + ", faultName=" + this.faultName + ", railGuard=" + this.railGuard
            + ", faultConfirmor=" + this.faultConfirmor + ", detailUrl=" + this.detailUrl + "]";
    }
}
