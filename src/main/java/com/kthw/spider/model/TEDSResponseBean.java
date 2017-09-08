package com.kthw.spider.model;

/**
 * Created by YFZX-WB on 2016/10/8.
 */
public class TEDSResponseBean {

    private String STrainsettype2; // 车型

    private String SAlertNum; // 报警件数

    private String SRcdsystypename; // 报警部位

    private String SManualertid; // 图片标识

    private String SAlarmmodename; // 报警名称

    private String CAffirmflag; // 报警类型

    private String SStationname; // 探测站

    private String SCarcode; // 车辆号

    private String STrainsetno; // 车组号

    private String SPasstime; // 过车时间

    private String SChief; // 确认人

    public String getSTrainsettype2() {
        return STrainsettype2;
    }

    public void setSTrainsettype2(String STrainsettype2) {
        this.STrainsettype2 = STrainsettype2;
    }

    public String getSAlertNum() {
        return SAlertNum;
    }

    public void setSAlertNum(String SAlertNum) {
        this.SAlertNum = SAlertNum;
    }

    public String getSRcdsystypename() {
        return SRcdsystypename;
    }

    public void setSRcdsystypename(String SRcdsystypename) {
        this.SRcdsystypename = SRcdsystypename;
    }

    public String getSManualertid() {
        return SManualertid;
    }

    public void setSManualertid(String SManualertid) {
        this.SManualertid = SManualertid;
    }

    public String getSAlarmmodename() {
        return SAlarmmodename;
    }

    public void setSAlarmmodename(String SAlarmmodename) {
        this.SAlarmmodename = SAlarmmodename;
    }

    public String getCAffirmflag() {
        return CAffirmflag;
    }

    public void setCAffirmflag(String CAffirmflag) {
        this.CAffirmflag = CAffirmflag;
    }

    public String getSStationname() {
        return SStationname;
    }

    public void setSStationname(String SStationname) {
        this.SStationname = SStationname;
    }

    public String getSCarcode() {
        return SCarcode;
    }

    public void setSCarcode(String SCarcode) {
        this.SCarcode = SCarcode;
    }

    public String getSTrainsetno() {
        return STrainsetno;
    }

    public void setSTrainsetno(String STrainsetno) {
        this.STrainsetno = STrainsetno;
    }

    public String getSPasstime() {
        return SPasstime;
    }

    public void setSPasstime(String SPasstime) {
        this.SPasstime = SPasstime;
    }

    public String getSChief() {
        return SChief;
    }

    public void setSChief(String SChief) {
        this.SChief = SChief;
    }

    @Override
    public String toString() {
        return "TEDSResponseBean{" +
                "STrainsettype2='" + STrainsettype2 + '\'' +
                ", SAlertNum='" + SAlertNum + '\'' +
                ", SRcdsystypename='" + SRcdsystypename + '\'' +
                ", SManualertid='" + SManualertid + '\'' +
                ", SAlarmmodename='" + SAlarmmodename + '\'' +
                ", CAffirmflag='" + CAffirmflag + '\'' +
                ", SStationname='" + SStationname + '\'' +
                ", SCarcode='" + SCarcode + '\'' +
                ", STrainsetno='" + STrainsetno + '\'' +
                ", SPasstime='" + SPasstime + '\'' +
                ", SChief='" + SChief + '\'' +
                '}';
    }
}
