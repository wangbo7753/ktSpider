package com.kthw.spider.model;

/**
 * Created by YFZX-WB on 2016/10/8.
 */
public class TVDSBean {

    private String SFailureWarnId; // IMG-UUID

    private String SPassTime; // 过车时间

    private String STrainNo; // 车次

    private String SCarNo; // 车号

    private String NCarSeq; // 辆序

    private String SProbeStationName; // 探测站

    private String SRcdsysTypeName; // 部件部位

    private String SFaultCategoryName; // 故障

    private String SDisposalName; // 故障类别

    private String SAnalystRemark; // 故障详情

    private String SAnalystName; // 填报人

    private String NMarshallingCount;

    public String getSFailureWarnId() {
        return SFailureWarnId;
    }

    public void setSFailureWarnId(String SFailureWarnId) {
        this.SFailureWarnId = SFailureWarnId;
    }

    public String getSPassTime() {
        return SPassTime;
    }

    public void setSPassTime(String SPassTime) {
        this.SPassTime = SPassTime;
    }

    public String getSTrainNo() {
        return STrainNo;
    }

    public void setSTrainNo(String STrainNo) {
        this.STrainNo = STrainNo;
    }

    public String getSCarNo() {
        return SCarNo;
    }

    public void setSCarNo(String SCarNo) {
        this.SCarNo = SCarNo;
    }

    public String getNCarSeq() {
        return NCarSeq;
    }

    public void setNCarSeq(String NCarSeq) {
        this.NCarSeq = NCarSeq;
    }

    public String getSProbeStationName() {
        return SProbeStationName;
    }

    public void setSProbeStationName(String SProbeStationName) {
        this.SProbeStationName = SProbeStationName;
    }

    public String getSRcdsysTypeName() {
        return SRcdsysTypeName;
    }

    public void setSRcdsysTypeName(String SRcdsysTypeName) {
        this.SRcdsysTypeName = SRcdsysTypeName;
    }

    public String getSFaultCategoryName() {
        return SFaultCategoryName;
    }

    public void setSFaultCategoryName(String SFaultCategoryName) {
        this.SFaultCategoryName = SFaultCategoryName;
    }

    public String getSDisposalName() {
        return SDisposalName;
    }

    public void setSDisposalName(String SDisposalName) {
        this.SDisposalName = SDisposalName;
    }

    public String getSAnalystRemark() {
        return SAnalystRemark;
    }

    public void setSAnalystRemark(String SAnalystRemark) {
        this.SAnalystRemark = SAnalystRemark;
    }

    public String getSAnalystName() {
        return SAnalystName;
    }

    public void setSAnalystName(String SAnalystName) {
        this.SAnalystName = SAnalystName;
    }

    public String getNMarshallingCount() {
        return NMarshallingCount;
    }

    public void setNMarshallingCount(String NMarshallingCount) {
        this.NMarshallingCount = NMarshallingCount;
    }

    @Override
    public String toString() {
        return "TVDSBean{" +
                "SFailureWarnId='" + SFailureWarnId + '\'' +
                ", SPassTime='" + SPassTime + '\'' +
                ", STrainNo='" + STrainNo + '\'' +
                ", SCarNo='" + SCarNo + '\'' +
                ", NCarSeq='" + NCarSeq + '\'' +
                ", SProbeStationName='" + SProbeStationName + '\'' +
                ", SRcdsysTypeName='" + SRcdsysTypeName + '\'' +
                ", SFaultCategoryName='" + SFaultCategoryName + '\'' +
                ", SDisposalName='" + SDisposalName + '\'' +
                ", SAnalystRemark='" + SAnalystRemark + '\'' +
                ", SAnalystName='" + SAnalystName + '\'' +
                ", NMarshallingCount='" + NMarshallingCount + '\'' +
                '}';
    }

}
