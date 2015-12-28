package com.fortunes.faas.model;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import net.fortunes.core.Model;

@Entity
public class Device extends Model{
	
	@Id @GeneratedValue
	private long id;
	
	private String ip;//ip地址
	
	private String port;//端口
	
	private String model;//型号
	
	private String code;//设备编号
	
	private String location;//安装位置
	
	private int employeeCount;//人员记录数
	
	private int checkCount;//考勤记录数
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastActiveTime;//最后活动时间
	
	private int lastStamp;//最后一次发送data Stamp
	
	private int lastOpStamp;//最后上传人员数据的最新时间戳标记
	
	private int status;//状态
	
//	@ManyToOne(fetch = FetchType.LAZY)
//	private Area area;
	
    public Device() {
    }
    
    public Device(long id) {
    	this.id = id;
    }
    
    @Override
	public String toString() {
		return "";
	}
    
    /*=============== setter and getter =================*/
    
    public void setId(long id) {
		this.id = id;
	}

	public long getId() {
		return id;
	}
	
	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getIp() {
		return ip;
	}
	public void setPort(String port) {
		this.port = port;
	}

	public String getPort() {
		return port;
	}
	public void setModel(String model) {
		this.model = model;
	}

	public String getModel() {
		return model;
	}
	public void setEmployeeCount(int employeeCount) {
		this.employeeCount = employeeCount;
	}

	public int getEmployeeCount() {
		return employeeCount;
	}
	public void setCheckCount(int checkCount) {
		this.checkCount = checkCount;
	}

	public int getCheckCount() {
		return checkCount;
	}
	public void setLastActiveTime(Date lastActiveTime) {
		this.lastActiveTime = lastActiveTime;
	}

	public Date getLastActiveTime() {
		return lastActiveTime;
	}
	public void setStatus(int status) {
		this.status = status;
	}

	public int getStatus() {
		return status;
	}

	public int getLastStamp() {
		return lastStamp;
	}

	public void setLastStamp(int lastStamp) {
		this.lastStamp = lastStamp;
	}

	public int getLastOpStamp() {
		return lastOpStamp;
	}

	public void setLastOpStamp(int lastOpStamp) {
		this.lastOpStamp = lastOpStamp;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getLocation() {
		return location;
	}
//
//	public Area getArea() {
//		return area;
//	}
//
//	public void setArea(Area area) {
//		this.area = area;
//	}

	@Override
	public boolean equals(Object obj) {
		Device device = (Device)obj;
		if(this.getCode().equals(device.getCode())){
			return true;
		}
		return false;
	}

}
