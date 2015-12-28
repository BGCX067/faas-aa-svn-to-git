package com.fortunes.faas.vo;

import net.fortunes.core.Model;

@SuppressWarnings("serial")
public class AttshiftMoreThanMidole extends Model {
	private long employeeId;
	private String empName;
	private double holidays;
	
	public AttshiftMoreThanMidole() {
	}
	public AttshiftMoreThanMidole(long employeeId, String empName,
			double holidays) {
		this.employeeId = employeeId;
		this.empName = empName;
		this.holidays = holidays;
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
	public double getHolidays() {
		return holidays;
	}
	public void setHolidays(double holidays) {
		this.holidays = holidays;
	}
	
}
