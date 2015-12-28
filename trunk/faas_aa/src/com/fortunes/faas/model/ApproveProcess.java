package com.fortunes.faas.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
 
import com.fortunes.fjdp.admin.AdminHelper;
import com.fortunes.fjdp.admin.model.Employee;

import net.fortunes.core.Model;

/**
 * 请假审批过程实体对象
 * @author Leo
 * @version 2011-3-28
 */
@Entity
public class ApproveProcess extends Model{
	
	public enum ApproveStatus{
		AGREE("同意"),
		INTENDAGREE("拟同意"),
		DISAGREE("不同意"),
		REBUT("驳回");
		private String label;
		private ApproveStatus(String label){
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
	
	@ManyToOne
	private HolidayApply holidayApply;//请假单
	
	@ManyToOne
	private Employee leaveApprove;//审批人
	
	private String certigier;
	
	@Temporal(TemporalType.DATE)
	private Date approveDate;
	
	@Enumerated(EnumType.STRING)
	private ApproveStatus approveStatus;//审批状态
	
	private String suggestion;//审批意见
	public ApproveProcess(){}
	public ApproveProcess(long id,long holidayApplyId,long leaveApplyId,String LeaveApplyName,long organizationId,String organizationName,String certigier,Date approveDate,ApproveStatus approveStatus,String suggestion){
		this.id = id;
		this.holidayApply = AdminHelper.toHolidayApply(Long.toString(holidayApplyId));
		this.leaveApprove = new Employee(leaveApplyId,LeaveApplyName,organizationId,organizationName);
		this.certigier = certigier;
		this.approveDate = approveDate;
		this.approveStatus = approveStatus;
		this.suggestion = suggestion;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public HolidayApply getHolidayApply() {
		return holidayApply;
	}

	public void setHolidayApply(HolidayApply holidayApply) {
		this.holidayApply = holidayApply;
	}

	public Employee getLeaveApprove() {
		return leaveApprove;
	}

	public void setLeaveApprove(Employee leaveApprove) {
		this.leaveApprove = leaveApprove;
	}

	public ApproveStatus getApproveStatus() {
		return approveStatus;
	}

	public void setApproveStatus(ApproveStatus approveStatus) {
		this.approveStatus = approveStatus;
	}

	public String getSuggestion() {
		return suggestion;
	}

	public void setSuggestion(String suggestion) {
		this.suggestion = suggestion;
	}

	public Date getApproveDate() {
		return approveDate;
	}

	public void setApproveDate(Date approveDate) {
		this.approveDate = approveDate;
	}

	public String getCertigier() {
		return certigier;
	}

	public void setCertigier(String certigier) {
		this.certigier = certigier;
	}
	
}
