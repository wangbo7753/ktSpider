package com.kthw.spider.vehicle.model;

/**
 * Created by YFZX-WB on 2016/12/16.
 */
public class VEDSBean {

    private String SPasstime;  // 过车时间

    private String SStationname;  // 探测站

    private String IStationkm;  // 公里标

    private String STrainsetwayseq;  // 过站顺序

    private String STrainsetno;  // 车组号

    private String STrainno;  // 车次

    private String STrainsettype; // 车型

    private String SBureaname;  // 配属局

    private String SDepotname;  // 配属所

    private String CDirectionflag;  // 运行方向：D-下行;U-上行

    private int SMarshltype;  // 总辆数

    private int STagspeed;  // 平均车速

    private int SMaxspeed;  // 最高车速

    private int SMinspeed;  // 最低车速

    public String getSPasstime() {
        return SPasstime;
    }

    public void setSPasstime(String SPasstime) {
        this.SPasstime = SPasstime;
    }

    public String getSStationname() {
        return SStationname;
    }

    public void setSStationname(String SStationname) {
        this.SStationname = SStationname;
    }

    public String getIStationkm() {
        return IStationkm;
    }

    public void setIStationkm(String IStationkm) {
        this.IStationkm = IStationkm;
    }

    public String getSTrainsetwayseq() {
        return STrainsetwayseq;
    }

    public void setSTrainsetwayseq(String STrainsetwayseq) {
        this.STrainsetwayseq = STrainsetwayseq;
    }

    public String getSTrainsetno() {
        return STrainsetno;
    }

    public void setSTrainsetno(String STrainsetno) {
        this.STrainsetno = STrainsetno;
    }

    public String getSTrainno() {
        return STrainno;
    }

    public void setSTrainno(String STrainno) {
        this.STrainno = STrainno;
    }

    public String getSTrainsettype() {
        return STrainsettype;
    }

    public void setSTrainsettype(String STrainsettype) {
        this.STrainsettype = STrainsettype;
    }

    public String getSBureaname() {
        return SBureaname;
    }

    public void setSBureaname(String SBureaname) {
        this.SBureaname = SBureaname;
    }

    public String getSDepotname() {
        return SDepotname;
    }

    public void setSDepotname(String SDepotname) {
        this.SDepotname = SDepotname;
    }

    public String getCDirectionflag() {
        return CDirectionflag;
    }

    public void setCDirectionflag(String CDirectionflag) {
        this.CDirectionflag = CDirectionflag;
    }

    public int getSMarshltype() {
        return SMarshltype;
    }

    public void setSMarshltype(int SMarshltype) {
        this.SMarshltype = SMarshltype;
    }

    public int getSTagspeed() {
        return STagspeed;
    }

    public void setSTagspeed(int STagspeed) {
        this.STagspeed = STagspeed;
    }

    public int getSMaxspeed() {
        return SMaxspeed;
    }

    public void setSMaxspeed(int SMaxspeed) {
        this.SMaxspeed = SMaxspeed;
    }

    public int getSMinspeed() {
        return SMinspeed;
    }

    public void setSMinspeed(int SMinspeed) {
        this.SMinspeed = SMinspeed;
    }
}
