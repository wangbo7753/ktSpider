package com.kthw.spider.model;

/**
 * Created by YFZX-WB on 2016/9/9.
 */
public class TADSBean {

    private String bureau;

    private String line;

    private String site;

    private String train_id;

    private String train_type;
    
    private String vehicle_id;

    private String vehicle_order;

    private String axle_number;

    private String pass_time;

    private String fault_type;

    private String fault_level;

    private String alarm_number;
    
    private String check;
    
    private String transact;

	public String getBureau() {
		return bureau;
	}

	public void setBureau(String bureau) {
		this.bureau = bureau;
	}

	public String getLine() {
		return line;
	}

	public void setLine(String line) {
		this.line = line;
	}

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public String getTrain_id() {
		return train_id;
	}

	public void setTrain_id(String train_id) {
		this.train_id = train_id;
	}

	public String getTrain_type() {
		return train_type;
	}

	public void setTrain_type(String train_type) {
		this.train_type = train_type;
	}

	public String getVehicle_id() {
		return vehicle_id;
	}

	public void setVehicle_id(String vehicle_id) {
		this.vehicle_id = vehicle_id;
	}

	public String getVehicle_order() {
		return vehicle_order;
	}

	public void setVehicle_order(String vehicle_order) {
		this.vehicle_order = vehicle_order;
	}

	public String getAxle_number() {
		return axle_number;
	}

	public void setAxle_number(String axle_number) {
		this.axle_number = axle_number;
	}

	public String getPass_time() {
		return pass_time;
	}

	public void setPass_time(String pass_time) {
		this.pass_time = pass_time;
	}

	public String getFault_type() {
		return fault_type;
	}

	public void setFault_type(String fault_type) {
		this.fault_type = fault_type;
	}

	public String getFault_level() {
		return fault_level;
	}

	public void setFault_level(String fault_level) {
		this.fault_level = fault_level;
	}

	public String getAlarm_number() {
		return alarm_number;
	}

	public void setAlarm_number(String alarm_number) {
		this.alarm_number = alarm_number;
	}

	public String getCheck() {
		return check;
	}

	public void setCheck(String check) {
		this.check = check;
	}

	public String getTransact() {
		return transact;
	}

	public void setTransact(String transact) {
		this.transact = transact;
	}

	@Override
	public String toString() {
		return "TADSBean [bureau=" + bureau + ", line=" + line + ", site=" + site + ", train_id=" + train_id + ", train_type=" + train_type + ", vehicle_id=" + vehicle_id + ", vehicle_order=" + vehicle_order + ", axle_number=" + axle_number + ", pass_time=" + pass_time + ", fault_type=" + fault_type + ", fault_level=" + fault_level + ", alarm_number=" + alarm_number + ", check=" + check + ", transact=" + transact + "]";
	}

}
