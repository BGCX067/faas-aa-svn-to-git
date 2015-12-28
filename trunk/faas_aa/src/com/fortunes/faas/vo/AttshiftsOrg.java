package com.fortunes.faas.vo;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import net.fortunes.core.Model;

@SuppressWarnings("serial")
public class AttshiftsOrg extends Model{

	private long orgId;//部门id
	private String orgName; //机构名称
	
	// 员工类别
	private int holidayPersonTotal;  //休假人员总数
	private double longOrMiddleTermTotal; //中长期工；
	private double shortTermTotal;  //短期工
	private double laoWuTotal; //劳务工
	private double dxTotal;//定向劳务
	private double outOfSystemTotal;//系统外员工
	
	//主要业务岗位
	private double managerPostLeaves;//管理岗位休假(行长、副行长、营业主管、行长助理、风险主管、总经理、副总经理、总经理助理、主任、副主任、主任助理、纪检监察特派员、总工程师)
	//('16758e01-aaaa-4125-b8f6-ae0e44953e60','2b92d83d-e001-465e-bc86-e78b727762fe','2d4de503-1026-443e-8307-6a3e0d615b22','61df9e5b-2aca-4ef9-a06a-af576c03546b','667b7990-676d-4e05-8172-209721c059f4','8a5b1366-23dc-4e04-aa86-39009a536364','b7acd412-cca5-4d6e-abc5-3d027915feab','c6a6006a-8719-4718-bb83-c4e6ad2a9c45','d6839df1-20da-484a-8b1a-9f979826df58','d730ddd3-0b64-4fa3-a132-dc95b8b58ba9','f3d99a96-2c9a-41c0-9db0-e0bf57b484f3','position_03','position_07')
	private double cabinetLeaves;//柜员休假
	private double customerManageLeaves;//客户经理休假(客户经理岗（消费信贷）、客户经理岗（对公）、对公客户经理助理、客户经理岗)
	//('ce0b3234-dc2d-42bb-8721-28f6a5830016','efda9b52-78b2-42dd-b86e-d117b1c5aad2','f5272ee5-d85b-4dbb-ba02-34f69e149b8d','position_04')
	
	private double oherLeaves; //其他岗位
	
	//休假类别
	private double annualLeave; //年休假
	private double familyPlanningLeave;//探亲假
	private double maternityLeave;//产假
	private double sickBaoLeave;//病假(保胎)
	private double sickFeiBaoLeave;  //非保胎 
	private double sickFeiBaoOutOf6Leave;//非保胎（超出6个月）
	private double thingsLeave;//事假
	private double otherHoldiayLeave; //其他假期
	
	private double absents;//旷工
	private double lates;//迟到
	private double earlys;//早退
	
	private String note;
	
	public AttshiftsOrg(){
		
	}
	
	public AttshiftsOrg(long orgId, String orgName, int longOrMiddleTermTotal,
			int shortTermTotal, int laoWuTotal, int dxTotal,
			int outOfSystemTotal, double annualLeave,
			double familyPlanningLeave, double maternityLeave,
			double sickBaoLeave, double thingsLeave, double absents,
			double lates, double earlys) {
		this.orgId = orgId;
		this.orgName = orgName;
		this.longOrMiddleTermTotal = longOrMiddleTermTotal;
		this.shortTermTotal = shortTermTotal;
		this.laoWuTotal = laoWuTotal;
		this.dxTotal = dxTotal;
		this.outOfSystemTotal = outOfSystemTotal;
		this.annualLeave = annualLeave;
		this.familyPlanningLeave = familyPlanningLeave;
		this.maternityLeave = maternityLeave;
		this.sickBaoLeave = sickBaoLeave;
		this.thingsLeave = thingsLeave;
		this.absents = absents;
		this.lates = lates;
		this.earlys = earlys;
	}

//	public AttshiftsOrg(long orgId, String orgName, int longOrMiddleTermTotal,
//			int shortTermTotal, int laoWuTotal, int dxTotal,
//			int outOfSystemTotal, double managerPostLeaves,
//			double cabinetLeaves, double customerManageLeaves,
//			double oherLeaves, double annualLeave, double familyPlanningLeave,
//			double maternityLeave, double sickBaoLeave, double thingsLeave,
//			double absents, double lates, double earlys) {
//		super();
//		this.orgId = orgId;
//		this.orgName = orgName;
//		this.longOrMiddleTermTotal = longOrMiddleTermTotal;
//		this.shortTermTotal = shortTermTotal;
//		this.laoWuTotal = laoWuTotal;
//		this.dxTotal = dxTotal;
//		this.outOfSystemTotal = outOfSystemTotal;
////		this.managerPostLeaves = managerPostLeaves;
////		this.cabinetLeaves = cabinetLeaves;
////		this.customerManageLeaves = customerManageLeaves;
////		this.oherLeaves = oherLeaves;
//		this.annualLeave = annualLeave;
//		this.familyPlanningLeave = familyPlanningLeave;
//		this.maternityLeave = maternityLeave;
//		this.sickBaoLeave = sickBaoLeave;
//		this.thingsLeave = thingsLeave;
//		this.absents = absents;
//		this.lates = lates;
//		this.earlys = earlys;
//	}
//
//	public AttshiftsOrg(long orgId, String orgName, int holidayPersonTotal,
//			int longOrMiddleTermTotal, int shortTermTotal, int laoWuTotal,
//			int outOfSystemTotal, double managerPostLeaves,
//			double cabinetLeaves, double customerManageLeaves,
//			double oherLeaves, double annualLeave, double familyPlanningLeave,
//			double maternityLeave, double sickBaoLeave, double sickFeiBaoLeave,
//			double sickFeiBaoOutOf6Leave, double thingsLeave, double absents,
//			double lates, double earlys, String note) {
//		this.orgId = orgId;
//		this.orgName = orgName;
////		this.holidayPersonTotal = holidayPersonTotal;
//		this.longOrMiddleTermTotal = longOrMiddleTermTotal;
//		this.shortTermTotal = shortTermTotal;
//		this.laoWuTotal = laoWuTotal;
//		this.outOfSystemTotal = outOfSystemTotal;
////		this.managerPostLeaves = managerPostLeaves;
////		this.cabinetLeaves = cabinetLeaves;
////		this.customerManageLeaves = customerManageLeaves;
////		this.oherLeaves = oherLeaves;
//		this.annualLeave = annualLeave;
//		this.familyPlanningLeave = familyPlanningLeave;
//		this.maternityLeave = maternityLeave;
//		this.sickBaoLeave = sickBaoLeave;
////		this.sickFeiBaoLeave = sickFeiBaoLeave;
////		this.sickFeiBaoOutOf6Leave = sickFeiBaoOutOf6Leave;
//		this.thingsLeave = thingsLeave;
//		this.absents = absents;
//		this.lates = lates;
//		this.earlys = earlys;
//		this.note = note;
//	}

	//===================================getter and setter===============================================
	public long getOrgId() {
		return orgId;
	}

	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public int getHolidayPersonTotal() {
		return holidayPersonTotal;
	}

	public void setHolidayPersonTotal(int holidayPersonTotal) {
		this.holidayPersonTotal = holidayPersonTotal;
	}

	

	public double getManagerPostLeaves() {
		return managerPostLeaves;
	}

	public void setManagerPostLeaves(double managerPostLeaves) {
		this.managerPostLeaves = managerPostLeaves;
	}

	public double getCabinetLeaves() {
		return cabinetLeaves;
	}

	public void setCabinetLeaves(double cabinetLeaves) {
		this.cabinetLeaves = cabinetLeaves;
	}

	public double getCustomerManageLeaves() {
		return customerManageLeaves;
	}

	public void setCustomerManageLeaves(double customerManageLeaves) {
		this.customerManageLeaves = customerManageLeaves;
	}

	public double getOherLeaves() {
		return oherLeaves;
	}

	public void setOherLeaves(double oherLeaves) {
		this.oherLeaves = oherLeaves;
	}

	public double getAnnualLeave() {
		return annualLeave;
	}

	public void setAnnualLeave(double annualLeave) {
		this.annualLeave = annualLeave;
	}

	public double getFamilyPlanningLeave() {
		return familyPlanningLeave;
	}

	public void setFamilyPlanningLeave(double familyPlanningLeave) {
		this.familyPlanningLeave = familyPlanningLeave;
	}

	public double getMaternityLeave() {
		return maternityLeave;
	}

	public void setMaternityLeave(double maternityLeave) {
		this.maternityLeave = maternityLeave;
	}

	public double getSickBaoLeave() {
		return sickBaoLeave;
	}

	public void setSickBaoLeave(double sickBaoLeave) {
		this.sickBaoLeave = sickBaoLeave;
	}

	public double getLongOrMiddleTermTotal() {
		return longOrMiddleTermTotal;
	}

	public void setLongOrMiddleTermTotal(double longOrMiddleTermTotal) {
		this.longOrMiddleTermTotal = longOrMiddleTermTotal;
	}

	public double getShortTermTotal() {
		return shortTermTotal;
	}

	public void setShortTermTotal(double shortTermTotal) {
		this.shortTermTotal = shortTermTotal;
	}

	public double getLaoWuTotal() {
		return laoWuTotal;
	}

	public void setLaoWuTotal(double laoWuTotal) {
		this.laoWuTotal = laoWuTotal;
	}

	public double getDxTotal() {
		return dxTotal;
	}

	public void setDxTotal(double dxTotal) {
		this.dxTotal = dxTotal;
	}

	public double getOutOfSystemTotal() {
		return outOfSystemTotal;
	}

	public void setOutOfSystemTotal(double outOfSystemTotal) {
		this.outOfSystemTotal = outOfSystemTotal;
	}

	public double getThingsLeave() {
		return thingsLeave;
	}

	public void setThingsLeave(double thingsLeave) {
		this.thingsLeave = thingsLeave;
	}

	public double getAbsents() {
		return absents;
	}

	public void setAbsents(double absents) {
		this.absents = absents;
	}

	public double getLates() {
		return lates;
	}

	public void setLates(double lates) {
		this.lates = lates;
	}

	public double getEarlys() {
		return earlys;
	}

	public void setEarlys(double earlys) {
		this.earlys = earlys;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public double getSickFeiBaoLeave() {
		return sickFeiBaoLeave;
	}

	public void setSickFeiBaoLeave(double sickFeiBaoLeave) {
		this.sickFeiBaoLeave = sickFeiBaoLeave;
	}

	public double getSickFeiBaoOutOf6Leave() {
		return sickFeiBaoOutOf6Leave;
	}

	public void setSickFeiBaoOutOf6Leave(double sickFeiBaoOutOf6Leave) {
		this.sickFeiBaoOutOf6Leave = sickFeiBaoOutOf6Leave;
	}

	public double getOtherHoldiayLeave() {
		return otherHoldiayLeave;
	}

	public void setOtherHoldiayLeave(double otherHoldiayLeave) {
		this.otherHoldiayLeave = otherHoldiayLeave;
	}
}
