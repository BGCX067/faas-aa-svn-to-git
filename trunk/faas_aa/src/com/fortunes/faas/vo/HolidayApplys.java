package com.fortunes.faas.vo;

import java.util.Date;

import javax.persistence.ManyToOne;

import com.fortunes.faas.model.HolidayApply.ApplyStatus;
import com.fortunes.fjdp.admin.model.Dict;
import com.fortunes.fjdp.admin.model.Employee;

import net.fortunes.core.Model;

	public class HolidayApplys extends Model{
		private long id;
		private Date applyDate;
		private Date startDate;
		private Date endDate;
		private Date cancelDate;
		private Date workOverTime;
		private String overTimeStart;
		private String overTimeEnd;
		private String applyType;
		private double cancelDays;
		private double leaveDays;
		private Dict leaveType;
		private String leaveTypeText;
		private ApplyStatus applyStatus;
		private Date backDate;
		private String site;
		private String goOutPhone;
		private String applyReason;
		private Employee leaveApply;//请假申请人
		private Employee operater;//请假操作员
		private Employee currentApprove;//当前审批人
		private Employee LeaveApprove1;
		private Employee LeaveApprove2;
		private Employee LeaveApprove3;
		private Employee LeaveApprove4;
		private Employee certigier;	
		private Employee bossAffirm;
		private Employee cancelApprove;
		private int node;
		public HolidayApplys(){}
		public HolidayApplys(long id,Date applyDate,Date startDate,Date endDate,Date cancelDate,Date workOverTime,String overTimeStart
				,String overTimeEnd,String applyType,double cancelDays,double leaveDays,String leaveType,String leaveTypeText,ApplyStatus applyStatus,Date backDate,String site,
				String goOutPhone,String applyReason,long leaveApply,String leaveApplyName,long operater,String operateName,
				long currentApprove,String currentApproveName,long LeaveApprove1,String leaveApprove1Name,long LeaveApprove2
				,String LeaveApprove2Name,long LeaveApprove3,String LeaveApprove3Name,long LeaveApprove4,String LeaveApprove4Name,
				long certigier,String certigierName,long bossAffirm,String bossAffirmName,long cancelApprove,String cancelApproveName,int node){
			this.id = id;
			this.applyDate = applyDate;
			this.startDate = startDate;
			this.endDate = endDate;
			this.cancelDate = endDate;
			this.workOverTime = workOverTime;
			this.overTimeStart = overTimeStart;
			this.overTimeEnd = overTimeEnd;
			this.applyType = applyType;
			this.cancelDays = cancelDays;
			this.leaveDays = leaveDays;
			this.leaveType = new Dict(leaveType,leaveTypeText);
			this.applyStatus = applyStatus;
			this.backDate = backDate;
			this.site = site;
			this.goOutPhone = goOutPhone;
			this.applyReason = applyReason;
			this.leaveApply = new Employee(leaveApply,leaveApplyName);
			this.operater = new Employee(operater,operateName);
			this.cancelApprove = new Employee(currentApprove,currentApproveName);
			this.LeaveApprove1 = new Employee(LeaveApprove1,leaveApprove1Name);
			this.LeaveApprove2 = new Employee(LeaveApprove2,LeaveApprove2Name);
			this.LeaveApprove3 = new Employee(LeaveApprove3,LeaveApprove3Name);
			this.LeaveApprove4 = new Employee(LeaveApprove4,LeaveApprove4Name);
			this.certigier = new Employee(certigier,certigierName);
			this.bossAffirm = new Employee(bossAffirm,bossAffirmName);
			this.cancelApprove = new Employee(cancelApprove,cancelApproveName);
			this.node = node;
		}
		public long getId() {
			return id;
		}
		public void setId(long id) {
			this.id = id;
		}
		public Date getApplyDate() {
			return applyDate;
		}
		public void setApplyDate(Date applyDate) {
			this.applyDate = applyDate;
		}
		public Date getStartDate() {
			return startDate;
		}
		public void setStartDate(Date startDate) {
			this.startDate = startDate;
		}
		public Date getEndDate() {
			return endDate;
		}
		public void setEndDate(Date endDate) {
			this.endDate = endDate;
		}
		public Date getCancelDate() {
			return cancelDate;
		}
		public void setCancelDate(Date cancelDate) {
			this.cancelDate = cancelDate;
		}
		public Date getWorkOverTime() {
			return workOverTime;
		}
		public void setWorkOverTime(Date workOverTime) {
			this.workOverTime = workOverTime;
		}
		public String getOverTimeStart() {
			return overTimeStart;
		}
		public void setOverTimeStart(String overTimeStart) {
			this.overTimeStart = overTimeStart;
		}
		public String getOverTimeEnd() {
			return overTimeEnd;
		}
		public void setOverTimeEnd(String overTimeEnd) {
			this.overTimeEnd = overTimeEnd;
		}
		public String getApplyType() {
			return applyType;
		}
		public void setApplyType(String applyType) {
			this.applyType = applyType;
		}
		public double getCancelDays() {
			return cancelDays;
		}
		public void setCancelDays(double cancelDays) {
			this.cancelDays = cancelDays;
		}
		public double getLeaveDays() {
			return leaveDays;
		}
		public void setLeaveDays(double leaveDays) {
			this.leaveDays = leaveDays;
		}
		public String getLeaveTypeText() {
			return leaveTypeText;
		}
		public void setLeaveTypeText(String leaveTypeText) {
			this.leaveTypeText = leaveTypeText;
		}
		public ApplyStatus getApplyStatus() {
			return applyStatus;
		}
		public void setApplyStatus(ApplyStatus applyStatus) {
			this.applyStatus = applyStatus;
		}
		public Date getBackDate() {
			return backDate;
		}
		public void setBackDate(Date backDate) {
			this.backDate = backDate;
		}
		public String getSite() {
			return site;
		}
		public void setSite(String site) {
			this.site = site;
		}
		public String getGoOutPhone() {
			return goOutPhone;
		}
		public void setGoOutPhone(String goOutPhone) {
			this.goOutPhone = goOutPhone;
		}
		public String getApplyReason() {
			return applyReason;
		}
		public void setApplyReason(String applyReason) {
			this.applyReason = applyReason;
		}
		public Employee getLeaveApply() {
			return leaveApply;
		}
		public void setLeaveApply(Employee leaveApply) {
			this.leaveApply = leaveApply;
		}
		public Employee getOperater() {
			return operater;
		}
		public void setOperater(Employee operater) {
			this.operater = operater;
		}
		public Employee getCurrentApprove() {
			return currentApprove;
		}
		public void setCurrentApprove(Employee currentApprove) {
			this.currentApprove = currentApprove;
		}
		public Employee getLeaveApprove1() {
			return LeaveApprove1;
		}
		public void setLeaveApprove1(Employee leaveApprove1) {
			LeaveApprove1 = leaveApprove1;
		}
		public Employee getLeaveApprove2() {
			return LeaveApprove2;
		}
		public void setLeaveApprove2(Employee leaveApprove2) {
			LeaveApprove2 = leaveApprove2;
		}
		public Employee getLeaveApprove3() {
			return LeaveApprove3;
		}
		public void setLeaveApprove3(Employee leaveApprove3) {
			LeaveApprove3 = leaveApprove3;
		}
		public Employee getCertigier() {
			return certigier;
		}
		public void setCertigier(Employee certigier) {
			this.certigier = certigier;
		}
		public Employee getBossAffirm() {
			return bossAffirm;
		}
		public void setBossAffirm(Employee bossAffirm) {
			this.bossAffirm = bossAffirm;
		}
		public Employee getCancelApprove() {
			return cancelApprove;
		}
		public void setCancelApprove(Employee cancelApprove) {
			this.cancelApprove = cancelApprove;
		}
		public int getNode() {
			return node;
		}
		public void setNode(int node) {
			this.node = node;
		}
		public void setLeaveType(Dict leaveType) {
			this.leaveType = leaveType;
		}
		public Employee getLeaveApprove4() {
			return LeaveApprove4;
		}
		public void setLeaveApprove4(Employee leaveApprove4) {
			LeaveApprove4 = leaveApprove4;
		}
		public Dict getLeaveType() {
			return leaveType;
		}
		
}
