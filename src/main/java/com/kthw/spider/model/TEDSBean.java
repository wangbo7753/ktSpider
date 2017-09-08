package com.kthw.spider.model;

/**
 * Created by YFZX-WB on 2016/9/9.
 */
public class TEDSBean {

    private String trainModel;

    private String trainUnitNum;

    private String trainIdentityNum;

    private String crossTime;

    private String checkStation;

    private String alarmType;

    private String alarmParts;

    private String manuAlertId;

    private String alarmName;

    private String confirmor;

    private String reviewStatus;

    private String repairStatus;

    public String getTrainModel() {
        return trainModel;
    }

    public void setTrainModel(String trainModel) {
        this.trainModel = trainModel;
    }

    public String getTrainUnitNum() {
        return trainUnitNum;
    }

    public void setTrainUnitNum(String trainUnitNum) {
        this.trainUnitNum = trainUnitNum;
    }

    public String getTrainIdentityNum() {
        return trainIdentityNum;
    }

    public void setTrainIdentityNum(String trainIdentityNum) {
        this.trainIdentityNum = trainIdentityNum;
    }

    public String getCrossTime() {
        return crossTime;
    }

    public void setCrossTime(String crossTime) {
        this.crossTime = crossTime;
    }

    public String getCheckStation() {
        return checkStation;
    }

    public void setCheckStation(String checkStation) {
        this.checkStation = checkStation;
    }

    public String getAlarmType() {
        return alarmType;
    }

    public void setAlarmType(String alarmType) {
        this.alarmType = alarmType;
    }

    public String getManuAlertId() {
        return manuAlertId;
    }

    public void setManuAlertId(String manuAlertId) {
        this.manuAlertId = manuAlertId;
    }

    public String getAlarmParts() {
        return alarmParts;
    }

    public void setAlarmParts(String alarmParts) {
        this.alarmParts = alarmParts;
    }

    public String getAlarmName() {
        return alarmName;
    }

    public void setAlarmName(String alarmName) {
        this.alarmName = alarmName;
    }

    public String getConfirmor() {
        return confirmor;
    }

    public void setConfirmor(String confirmor) {
        this.confirmor = confirmor;
    }

    public String getReviewStatus() {
        return reviewStatus;
    }

    public void setReviewStatus(String reviewStatus) {
        this.reviewStatus = reviewStatus;
    }

    public String getRepairStatus() {
        return repairStatus;
    }

    public void setRepairStatus(String repairStatus) {
        this.repairStatus = repairStatus;
    }

    @Override
    public String toString() {
        return "TEDSBean{" +
                "trainModel='" + trainModel + '\'' +
                ", trainUnitNum='" + trainUnitNum + '\'' +
                ", trainIdentityNum='" + trainIdentityNum + '\'' +
                ", crossTime='" + crossTime + '\'' +
                ", checkStation='" + checkStation + '\'' +
                ", alarmType='" + alarmType + '\'' +
                ", manuAlertId='" + manuAlertId + '\'' +
                ", alarmParts='" + alarmParts + '\'' +
                ", alarmName='" + alarmName + '\'' +
                ", confirmor='" + confirmor + '\'' +
                ", reviewStatus='" + reviewStatus + '\'' +
                ", repairStatus='" + repairStatus + '\'' +
                '}';
    }
}
