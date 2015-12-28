package com.fortunes.faas.vo;

import net.fortunes.core.Model;

public class HolidayCountVO extends Model{
	private Long employeeId;
	
	private String code;
	
	private double count;
	
	public HolidayCountVO(){}
	
	public HolidayCountVO(Long employeeId,String code,double count){
		this.employeeId = employeeId;
		this.code = code;
		this.count = count;
	}
	
	public Long getEmployeeId() {
		return employeeId;
	}
	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public double getCount() {
		return count;
	}
	public void setCount(double count) {
		this.count = count;
	}
	
}
