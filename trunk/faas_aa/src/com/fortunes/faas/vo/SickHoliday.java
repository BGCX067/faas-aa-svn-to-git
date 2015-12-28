package com.fortunes.faas.vo;

import net.fortunes.core.Model;

public class SickHoliday extends Model {
	
	private long employeeId;
	private String empName;
	private double sickFeiBaoHolidays;// 病假（非胞胎） 'b45ed16b-f9ec-4e87-8a87-1cb4fb2d859e',病假（胞胎）'bce240b5-1294-4e8f-a1bb-74152f0e0ff4';
	public SickHoliday() {
		
	}
	public SickHoliday(long employeeId, String empName,
			double sickFeiBaoHolidays) {
		this.employeeId = employeeId;
		this.empName = empName;
		this.sickFeiBaoHolidays = sickFeiBaoHolidays;
	}
	public long getEmployeeId() {
		return employeeId;
	}
	public void setEmployeeId(long employeeId) {
		this.employeeId = employeeId;
	}
	public String getEmpName() {
		return empName;
	}
	public void setEmpName(String empName) {
		this.empName = empName;
	}
	public double getSickFeiBaoHolidays() {
		return sickFeiBaoHolidays;
	}
	public void setSickFeiBaoHolidays(double sickFeiBaoHolidays) {
		this.sickFeiBaoHolidays = sickFeiBaoHolidays;
	}
	
}
