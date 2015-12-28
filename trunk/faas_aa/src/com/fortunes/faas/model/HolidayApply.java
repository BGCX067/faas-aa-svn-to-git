package com.fortunes.faas.model;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fortunes.fjdp.AppHelper;
import com.fortunes.fjdp.admin.model.Dict;
import com.fortunes.fjdp.admin.model.Employee;
import com.fortunes.fjdp.admin.model.Role;
import net.fortunes.core.Model;

@Entity
public class HolidayApply extends Model {	
	
	public enum ApplyStatus {
		PROGRESS("审批中"),//进行中
		FAILED("审批失败"),//失败
		FINISHED("已审批完成");//已完成
		private String label;
		private ApplyStatus(String label){
			this.label = label;
		}
		public String getLabel() {
			return label;
		}
		public void setLabel(String label) {
			this.label = label;
		}
		
	}
	@Id @GeneratedValue
	private long id;
	
	@Temporal(TemporalType.DATE)
	private Date applyDate = new Date();//申请日期
	
	@Temporal(TemporalType.DATE)
	private Date startDate;//开始时间
	
	@Temporal(TemporalType.DATE)
	private Date endDate;//结束时间
	
	private Date cancelDate;//销假结束日期
	@Column @Temporal(TemporalType.DATE)
	private Date workOverTime;//加班开始日期
	@Column @Temporal(TemporalType.DATE)
	private Date eWorkOverTimeDate;
	
	private String overTimeStart;//加班开始时间
	
	private String overTimeEnd;//加班结束时间
	
	private String applyType;//加班或者请假
	
	private String oldOrNew = "NEW";//假单是新版本，还是旧版本
	
	private String leaveApplyType; //请假人类型
	
	private double leaveDays;
	
	private String isMarry;//婚姻状况
	
	private double cancelDays;
	
	@ManyToOne
	private Dict leaveType;//请假类型
	
	@Enumerated(EnumType.STRING)
	private ApplyStatus applyStatus = ApplyStatus.PROGRESS;//审批状态
	private Date backDate;//回行上班时间
	private String site;//拟往地点
	private String goOutPhone;//外出联系电话
	private String applyReason;//申请原因
	
	@ManyToOne
	private Employee leaveApply;//请假申请人
	
	@ManyToOne
	private Employee operater;//请假操作员
	
	@ManyToOne
	private Employee currentApprove;//当前审批人
	
	@ManyToOne
	private Employee LeaveApprove1;

	@ManyToOne
	private Employee LeaveApprove2;
	
	@ManyToOne
	private Employee LeaveApprove3;
	
	@ManyToOne
	private Employee LeaveApprove4;
	
	@ManyToOne
	private Employee certigier;	
	
	@ManyToOne
	private Employee bossAffirm;
	
	@ManyToOne
	private Employee cancelApprove;

	private int node = 1;
	
	@OneToMany(mappedBy = "holidayApply")
	private List<ApproveProcess> approveProcesses;
	
	public HolidayApply(){
	}
	public HolidayApply(long id){
		this.id = id;
	}
	public HolidayApply(long id,String site){
		this.id = id;
		this.site = site;
	}
	
	public HolidayApply(long id,Date applyDate,Date startDate,Date endDate,Date cancelDate,Date workOverTime,String overTimeStart
			,String overTimeEnd,String applyType,double cancelDays,double leaveDays,String leaveType,String leaveTypeText,ApplyStatus applyStatus,Date backDate,String site,
			String goOutPhone,String applyReason,long leaveApply,String leaveApplyName,long operater,String operateName,
			long currentApprove,String currentApproveName){
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
		if(currentApprove!=0){
			this.cancelApprove = new Employee(currentApprove,currentApproveName);
		}else{this.cancelApprove=null;}
	}
	
	public HolidayApply(long id,String dictId,String text,double leaveDays,Date startDate,Date endDate,Date cancelDate,long employeeId,String applyType){
		this.id = id;
		this.leaveType = new Dict(dictId,text);
		this.leaveDays = leaveDays;
		this.startDate = startDate;
		this.endDate = endDate;
		this.cancelDate = cancelDate;
		this.leaveApply = AppHelper.toEmployee(Long.toString(employeeId));
		this.applyType = applyType;
	}
	public HolidayApply(long id,String dictId,String text,Date startDate,Date endDate,String applyReason){
		this.id = id;
		this.leaveType = new Dict(dictId,text);
		this.startDate = startDate;
		this.endDate = endDate;
		this.applyReason = applyReason;
	}
	public HolidayApply(long id,Date workOverTime,String overTimeStart,String overTimeEnd,String applyType){
		this.id = id;
		this.workOverTime = workOverTime;
		this.overTimeStart = overTimeStart;
		this.overTimeEnd = overTimeEnd;
		this.applyType = applyType;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getApplyType() {
		return applyType;
	}

	public void setApplyType(String applyType) {
		this.applyType = applyType;
	}

	public String getOverTimeStart() {
		return overTimeStart;
	}

	public void setOverTimeStart(String overTimeStart) {
		this.overTimeStart = overTimeStart;
	}

	public Date getCancelDate() {
		return cancelDate;
	}

	public void setCancelDate(Date cancelDate) {
		this.cancelDate = cancelDate;
	}

	public String getOverTimeEnd() {
		return overTimeEnd;
	}

	public void setOverTimeEnd(String overTimeEnd) {
		this.overTimeEnd = overTimeEnd;
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

	public double getLeaveDays() {
		return leaveDays;
	}

	public void setLeaveDays(double leaveDays) {
		this.leaveDays = leaveDays;
	}

	public ApplyStatus getApplyStatus() {
		return applyStatus;
	}

	public void setApplyStatus(ApplyStatus applyStatus) {
		this.applyStatus = applyStatus;
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
	public int getNode() {
		return node;
	}

	public void setNode(int node) {
		this.node = node;
	}

	public Employee getOperater() {
		return operater;
	}

	public void setOperater(Employee operater) {
		this.operater = operater;
	}

	public List<ApproveProcess> getApproveProcesses() {
		return approveProcesses;
	}

	public void setApproveProcesses(List<ApproveProcess> approveProcesses) {
		this.approveProcesses = approveProcesses;
	}

	public Dict getLeaveType() {
		return leaveType;
	}

	public void setLeaveType(Dict leaveType) {
		this.leaveType = leaveType;
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

	public Employee getLeaveApprove4() {
		return LeaveApprove4;
	}

	public void setLeaveApprove4(Employee leaveApprove4) {
		LeaveApprove4 = leaveApprove4;
	}

	public Employee getCertigier() {
		return certigier;
	}

	public void setCertigier(Employee certigier) {
		this.certigier = certigier;
	}

	public Employee getCancelApprove() {
		return cancelApprove;
	}

	public void setCancelApprove(Employee cancelApprove) {
		this.cancelApprove = cancelApprove;
	}

	public Employee getBossAffirm() {
		return bossAffirm;
	}

	public void setBossAffirm(Employee bossAffirm) {
		this.bossAffirm = bossAffirm;
	}

	public double getCancelDays() {
		return cancelDays;
	}

	public void setCancelDays(double cancelDays) {
		this.cancelDays = cancelDays;
	}

	public Date getWorkOverTime() {
		return workOverTime;
	}

	public void setWorkOverTime(Date workOverTime) {
		this.workOverTime = workOverTime;
	}
	public Date getEWorkOverTimeDate() {
		return eWorkOverTimeDate;
	}
	public void setEWorkOverTimeDate(Date workOverTimeDate) {
		eWorkOverTimeDate = workOverTimeDate;
	}
	public String getIsMarry() {
		return isMarry;
	}
	public void setIsMarry(String isMarry) {
		this.isMarry = isMarry;
	}
	public String getOldOrNew() {
		return oldOrNew;
	}
	public void setOldOrNew(String oldOrNew) {
		this.oldOrNew = oldOrNew;
	}
	public String getLeaveApplyType() {
		return leaveApplyType;
	}
	public void setLeaveApplyType(String leaveApplyType) {
		this.leaveApplyType = leaveApplyType;
	}
	
}
