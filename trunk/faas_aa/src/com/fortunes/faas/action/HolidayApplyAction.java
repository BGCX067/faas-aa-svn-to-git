package com.fortunes.faas.action;
 
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
 
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import com.fortunes.faas.model.ApproveProcess;
import com.fortunes.faas.model.HolidayApply;
import com.fortunes.faas.model.ApproveProcess.ApproveStatus;
import com.fortunes.faas.model.HolidayApply.ApplyStatus;
import com.fortunes.faas.service.ApproveProcessService;
import com.fortunes.faas.service.AttendancePlusService;
import com.fortunes.faas.service.HolidayApplyService;
import com.fortunes.fjdp.Constants;
import com.fortunes.fjdp.admin.AdminHelper;
import com.fortunes.fjdp.admin.model.Dict;
import com.fortunes.fjdp.admin.model.Employee;
import com.fortunes.fjdp.admin.model.Role;
import com.fortunes.fjdp.admin.model.User;
import com.fortunes.fjdp.admin.service.DictService;
import com.fortunes.fjdp.admin.service.EmployeeService;
import com.fortunes.fjdp.admin.service.UserService;

import net.fortunes.core.ListData;
import net.fortunes.core.action.GenericAction;
import net.fortunes.core.service.GenericService;
import net.fortunes.util.Tools;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@SuppressWarnings("serial")
@Component @Scope("prototype")
public class HolidayApplyAction extends GenericAction<HolidayApply>{
	
	@Resource HolidayApplyService holidayApplyService;
	@Resource ApproveProcessService approveProcessService;
	@Resource EmployeeService employeeService;
	@Resource private AttendancePlusService attendancePlusService;
	@Resource private DictService dictService;
	
	@Override
	public String create() throws Exception {
		String leaveApplyStr = p("leaveApply");
		Employee leaveApply = employeeService.get(leaveApplyStr);
		String leaveTypeStr = p("leaveType");
		double leavesDay = Double.parseDouble(p("leaveDays"));
		if("leaveType_03_1".equals(leaveTypeStr)&&(leaveApply.getAnnualLeave()<leavesDay)){
			setJsonMessage(true, "无法请到"+leavesDay+"天的年假，您的年假天数是"+leaveApply.getAnnualLeave()+"天");
		}else{
			HolidayApply holidayApply = getEntityClass().newInstance();
			setEntity(holidayApply);
			holidayApply.setCurrentApprove(holidayApply.getLeaveApprove1());
			HolidayApply ha = getDefService().add(holidayApply);
//			holidayApplyService.createFlow(ha);
			setJsonMessage(true, holidayApply.toString().equals("")?
					"新增了一条记录!" : "新增了("+holidayApply+")的记录");
		}
		return render(jo);
	}
	
	
	public String update() throws Exception {

		String leaveApplyStr = p("leaveApply");
		Employee leaveApply = employeeService.get(leaveApplyStr);
		String leaveTypeStr = p("leaveType");
		double leavesDay = Double.parseDouble(p("leaveDays"));
		if ("leaveType_03_1".equals(leaveTypeStr)
				&& (leaveApply.getAnnualLeave() < leavesDay)) {
			setJsonMessage(true, "无法请到" + leavesDay + "天的年假，您的年假天数是"
					+ leaveApply.getAnnualLeave() + "天");
		} else {
			HolidayApply entity = getDefService().get(id);
			String applyType = p("operateApplyType");// 判断是否是请假或者是加班
			if (!applyType.equals("overTime")) {
				setEntityForEdit(entity);
				String leaveApprove1 = p("leaveApprove1");
				String leaveApprove2 = p("leaveApprove2");
				String leaveApprove3 = p("leaveApprove3");
				String leaveApprove4 = p("leaveApprove4");
				String certigier = p("certigier");
				String bossAffirm = p("bossAffirm");

				if (entity.getApplyStatus().name().equals("PROGRESS")) {
					if (!AdminHelper.longToString(
							entity.getLeaveApprove1().getId()).equals(
							leaveApprove1)) {
						if (entity.getNode() == 1) {
							// 如果还没有开始审批或已经审批，那么只需要改变第一审批人，其他审批人直接改变,
							entity.setCurrentApprove(AdminHelper
									.toEmployee(leaveApprove1));
						} else {
							// 如果已经审批了，那么改变第一审批人，那么删除所有审批人的审批信息，从第一审批人开始
							approveProcessService.delWrongProdess(id, entity
									.getLeaveApprove1());
							approveProcessService.delWrongProdess(id, entity
									.getLeaveApprove2());
							approveProcessService.delWrongProdess(id, entity
									.getLeaveApprove3());
							approveProcessService.delWrongProdess(id, entity
									.getLeaveApprove4());
							approveProcessService.delWrongProdess(id, entity
									.getCertigier());
							approveProcessService.delWrongProdess(id, entity
									.getBossAffirm());
							entity.setCurrentApprove(AdminHelper
									.toEmployee(leaveApprove1));
						}
					} else if ((entity.getLeaveApprove2() != null && !AdminHelper
							.longToString(entity.getLeaveApprove2().getId())
							.equals(leaveApprove2))
							|| (entity.getLeaveApprove2() == null && StringUtils
									.isNotEmpty(leaveApprove2))) {
						if (StringUtils.isNotEmpty(leaveApprove2)) {
							if (entity.getNode() >= 2) {
								// 如果node>=2则表示第一审批人已经审批，第二审批人已审批或未审批，则改变第二审批人，和当前审批人，删除除之后所有审批人的审批信息。
								entity.setCurrentApprove(AdminHelper
										.toEmployee(leaveApprove2));
								entity.setNode(2);
								approveProcessService.delWrongProdess(id,
										entity.getLeaveApprove2());
								approveProcessService.delWrongProdess(id,
										entity.getLeaveApprove3());
								approveProcessService.delWrongProdess(id,
										entity.getLeaveApprove4());
								approveProcessService.delWrongProdess(id,
										entity.getCertigier());
								approveProcessService.delWrongProdess(id,
										entity.getBossAffirm());
							} else {
								// 如果是这样则表示第一审批人还未审批，则直接改变第二审批人，和之后的审批人。
							}
						} else {
							// 如果是这样则，表示取消了当前的审批人，那么流程结束,删除第二审批人和之后审批人的审批信息，同时要判断确认人和行长室是否为空，如果不为空则继续流程
							if (entity.getCurrentApprove().getId() == entity
									.getLeaveApprove2().getId()) {
								if (StringUtils.isNotEmpty(certigier)) {
									entity.setCurrentApprove(AdminHelper
											.toEmployee(certigier));
									entity.setNode(5);
								} else if (StringUtils.isNotEmpty(bossAffirm)) {
									entity.setCurrentApprove(AdminHelper
											.toEmployee(bossAffirm));
									entity.setNode(6);
								} else {
									entity.setCurrentApprove(null);
									entity.setApplyStatus(ApplyStatus.FINISHED);
									entity.setNode(2);
								}
							} else {
								if (entity.getNode() >= 2) {
									approveProcessService.delWrongProdess(id,
											entity.getLeaveApprove2());
									approveProcessService.delWrongProdess(id,
											entity.getLeaveApprove3());
									approveProcessService.delWrongProdess(id,
											entity.getLeaveApprove4());
									approveProcessService.delWrongProdess(id,
											entity.getCertigier());
									approveProcessService.delWrongProdess(id,
											entity.getBossAffirm());
									if (StringUtils.isNotEmpty(certigier)) {
										entity.setCurrentApprove(AdminHelper
												.toEmployee(certigier));
										entity.setNode(5);
									} else if (StringUtils
											.isNotEmpty(bossAffirm)) {
										entity.setCurrentApprove(AdminHelper
												.toEmployee(bossAffirm));
										entity.setNode(6);
									} else {
										entity.setCurrentApprove(null);
										entity
												.setApplyStatus(ApplyStatus.FINISHED);
										entity.setNode(2);
									}
								}
							}
						}

					} else if ((entity.getLeaveApprove3() != null && !AdminHelper
							.longToString(entity.getLeaveApprove3().getId())
							.equals(leaveApprove3))
							|| (entity.getLeaveApprove3() == null && StringUtils
									.isNotEmpty(leaveApprove3))) {
						if (StringUtils.isNotEmpty(leaveApprove3)) {
							if (entity.getNode() >= 3) {
								// 如果node>=3则表示，第二审批人已经审批，第三审批人审批或者未审批，则改变第三审批人，和当前审批人，删除之后所有审批人的审批信息。
								entity.setCurrentApprove(AdminHelper
										.toEmployee(leaveApprove3));
								entity.setNode(3);
								approveProcessService.delWrongProdess(id,
										entity.getLeaveApprove3());
								approveProcessService.delWrongProdess(id,
										entity.getLeaveApprove4());
								approveProcessService.delWrongProdess(id,
										entity.getCertigier());
								approveProcessService.delWrongProdess(id,
										entity.getBossAffirm());
							} else {
								// 如果是这样表示流程还没有走到这里，直接改变第三审批人和之后审批人。
							}
						} else {
							// 如果是这样则表示取消了当前的审批人，那么流程结束，删除第三审批人，和之后审批人的审批信息，同时判断确认人和行长室审核是否为空，如果不为空则继续流程
							if (entity.getCurrentApprove().getId() == entity
									.getLeaveApprove3().getId()) {
								if (StringUtils.isNotEmpty(certigier)) {
									entity.setCurrentApprove(AdminHelper
											.toEmployee(certigier));
									entity.setNode(5);
								} else if (StringUtils.isNotEmpty(bossAffirm)) {
									entity.setCurrentApprove(AdminHelper
											.toEmployee(bossAffirm));
									entity.setNode(6);
								} else {
									entity.setCurrentApprove(null);
									entity.setApplyStatus(ApplyStatus.FINISHED);
									entity.setNode(3);
								}
							} else {
								if (entity.getNode() >= 3) {
									approveProcessService.delWrongProdess(id,
											entity.getLeaveApprove3());
									approveProcessService.delWrongProdess(id,
											entity.getLeaveApprove4());
									approveProcessService.delWrongProdess(id,
											entity.getCertigier());
									approveProcessService.delWrongProdess(id,
											entity.getBossAffirm());
									if (StringUtils.isNotEmpty(certigier)) {
										entity.setCurrentApprove(AdminHelper
												.toEmployee(certigier));
										entity.setNode(5);
									} else if (StringUtils
											.isNotEmpty(bossAffirm)) {
										entity.setCurrentApprove(AdminHelper
												.toEmployee(bossAffirm));
										entity.setNode(6);
									} else {
										entity.setCurrentApprove(null);
										entity
												.setApplyStatus(ApplyStatus.FINISHED);
										entity.setNode(3);
									}
								}
							}
						}

					} else if ((entity.getLeaveApprove4() != null && !AdminHelper
							.longToString(entity.getLeaveApprove4().getId())
							.equals(leaveApprove4))
							|| (entity.getLeaveApprove4() == null && StringUtils
									.isNotEmpty(leaveApprove4))) {
						if (StringUtils.isNotEmpty(leaveApprove4)) {
							if (entity.getNode() >= 4) {
								// 如果node>=4则表示，第三审批人已经审批，第四审批人审批或者未审批，则改变第四审批人，和当前审批人，删除之后所有审批人的审批信息
								entity.setCurrentApprove(AdminHelper
										.toEmployee(leaveApprove4));
								entity.setNode(4);
								approveProcessService.delWrongProdess(id,
										entity.getLeaveApprove4());
								approveProcessService.delWrongProdess(id,
										entity.getCertigier());
								approveProcessService.delWrongProdess(id,
										entity.getBossAffirm());
							} else {
								// 如果是这样则表示流程还没有走到这里，直接改变第四审批人和之后审批人。
							}
						} else {
							// 如果是这样这表示取消了当前审批人，那么流程结束，删除第四审批人的审批信息，同时判断确认人和行长室是否为空，如果不为空则继续流程
							if (entity.getCurrentApprove().getId() == entity
									.getLeaveApprove4().getId()) {
								if (StringUtils.isNotEmpty(certigier)) {
									entity.setCurrentApprove(AdminHelper
											.toEmployee(certigier));
									entity.setNode(5);
								} else if (StringUtils.isNotEmpty(bossAffirm)) {
									entity.setCurrentApprove(AdminHelper
											.toEmployee(bossAffirm));
									entity.setNode(6);
								} else {
									entity.setCurrentApprove(null);
									entity.setApplyStatus(ApplyStatus.FINISHED);
									entity.setNode(4);
								}
							} else {
								if (entity.getNode() >= 4) {
									approveProcessService.delWrongProdess(id,
											entity.getLeaveApprove4());
									approveProcessService.delWrongProdess(id,
											entity.getCertigier());
									approveProcessService.delWrongProdess(id,
											entity.getBossAffirm());
									if (StringUtils.isNotEmpty(certigier)) {
										entity.setCurrentApprove(AdminHelper
												.toEmployee(certigier));
										entity.setNode(5);
									} else if (StringUtils
											.isNotEmpty(bossAffirm)) {
										entity.setCurrentApprove(AdminHelper
												.toEmployee(bossAffirm));
										entity.setNode(6);
									} else {
										entity.setCurrentApprove(null);
										entity
												.setApplyStatus(ApplyStatus.FINISHED);
										entity.setNode(4);
									}
								}
							}
						}

					} else if ((entity.getCertigier() != null && !AdminHelper
							.longToString(entity.getCertigier().getId())
							.equals(certigier))
							|| (entity.getCertigier() == null && StringUtils
									.isNotEmpty(certigier))) {
						if (StringUtils.isNotEmpty(certigier)) {
							if (entity.getNode() >= 5) {
								// 如果node>=5则表示，之前审批人已经审批，确认人审批或者没有审批，直接改变确认人，和当前审批人，删除之后所有审批人的审批信息。
								entity.setCurrentApprove(AdminHelper
										.toEmployee(certigier));
								entity.setNode(5);
								approveProcessService.delWrongProdess(id,
										entity.getCertigier());
								approveProcessService.delWrongProdess(id,
										entity.getBossAffirm());
							} else {
								// 如果是这样则表示流程还没有走到这里，直接改变确认人和之后审批人。
							}
						} else {
							// 如果是这样则百事取消了当前确认人，那么流程结束，同时判断行长室是否为空，如果不为看则继续流程
							if (entity.getCurrentApprove().getId() == entity
									.getCertigier().getId()) {
								if (StringUtils.isNotEmpty(bossAffirm)) {
									entity.setCurrentApprove(AdminHelper
											.toEmployee(bossAffirm));
									entity.setNode(6);
								} else {
									entity.setCurrentApprove(null);
									entity.setApplyStatus(ApplyStatus.FINISHED);
									entity.setNode(5);
								}
							} else {
								if (entity.getNode() >= 5) {
									approveProcessService.delWrongProdess(id,
											entity.getCertigier());
									approveProcessService.delWrongProdess(id,
											entity.getBossAffirm());
									if (StringUtils.isNotEmpty(bossAffirm)) {
										entity.setCurrentApprove(AdminHelper
												.toEmployee(bossAffirm));
										entity.setNode(6);
									} else {
										entity.setCurrentApprove(null);
										entity
												.setApplyStatus(ApplyStatus.FINISHED);
										entity.setNode(4);
									}
								}
							}
						}

					} else if ((entity.getBossAffirm() != null && !AdminHelper
							.longToString(entity.getBossAffirm().getId())
							.equals(bossAffirm))
							|| (entity.getBossAffirm() == null && StringUtils
									.isNotEmpty(bossAffirm))) {
						if (entity.getNode() >= 6) {
							// 如果是这样则表示，之前审批人已经审批，行长办公室审核审批或者没有审批，直接改变审核人，和当前审批人。
							entity.setCurrentApprove(AdminHelper
									.toEmployee(bossAffirm));
							entity.setNode(6);
							approveProcessService.delWrongProdess(id, entity
									.getBossAffirm());
						} else {
							// 如果是这样则表示流程还没有走到这里，直接改变审核人。
						}
					} else {

					}
					entity.setLeaveApprove1(AdminHelper
							.toEmployee(leaveApprove1));
					entity.setLeaveApprove2(AdminHelper
							.toEmployee(leaveApprove2));
					entity.setLeaveApprove3(AdminHelper
							.toEmployee(leaveApprove3));
					entity.setLeaveApprove4(AdminHelper
							.toEmployee(leaveApprove4));
					entity.setCertigier(AdminHelper.toEmployee(certigier));
					entity.setBossAffirm(AdminHelper.toEmployee(bossAffirm));
				} else if (entity.getApplyStatus().name().equals("FINISHED")) {
					if (!AdminHelper.longToString(
							entity.getLeaveApprove1().getId()).equals(
							leaveApprove1)) {
						approveProcessService.delWrongProdess(id, entity
								.getLeaveApprove1());
						approveProcessService.delWrongProdess(id, entity
								.getLeaveApprove2());
						approveProcessService.delWrongProdess(id, entity
								.getLeaveApprove3());
						approveProcessService.delWrongProdess(id, entity
								.getLeaveApprove4());
						approveProcessService.delWrongProdess(id, entity
								.getCertigier());
						approveProcessService.delWrongProdess(id, entity
								.getBossAffirm());
						entity.setCurrentApprove(AdminHelper
								.toEmployee(leaveApprove1));
						entity.setApplyStatus(ApplyStatus.PROGRESS);
						entity.setNode(1);
					} else if ((entity.getLeaveApprove2() != null && !AdminHelper
							.longToString(entity.getLeaveApprove2().getId())
							.equals(leaveApprove2))
							|| (entity.getLeaveApprove2() == null && StringUtils
									.isNotEmpty(leaveApprove2))) {
						if (entity.getNode() > 2) {
							// 这种情况为第一审批人已经审批，第二审批人没有，或者也已经审批过。
							// 如果第二审批人没有，则取消第二审批人，删除之后审批人的审批记录。
							// 如果第二审批人有，则修改审批人，流程从这里开始,删除审批人的审批记录
							if (StringUtils.isNotEmpty(leaveApprove2)) {
								approveProcessService.delWrongProdess(id,
										entity.getLeaveApprove2());
								// entity.setLeaveApprove2(AdminHelper.toEmployee(leaveApprove2));
								entity.setNode(2);
								entity.setApplyStatus(ApplyStatus.PROGRESS);
								entity.setCurrentApprove(AdminHelper
										.toEmployee(leaveApprove2));
								approveProcessService.delWrongProdess(id,
										entity.getCertigier());
								approveProcessService.delWrongProdess(id,
										entity.getBossAffirm());
							} else {
								approveProcessService.delWrongProdess(id,
										entity.getLeaveApprove2());
								entity.setCurrentApprove(null);
								entity.setApplyStatus(ApplyStatus.FINISHED);
								if (StringUtils.isNotEmpty(certigier)) {
									entity.setNode(5);
								} else if (StringUtils.isNotEmpty(bossAffirm)) {
									entity.setNode(6);
								}
							}
							approveProcessService.delWrongProdess(id, entity
									.getLeaveApprove3());
							approveProcessService.delWrongProdess(id, entity
									.getLeaveApprove4());

						} else {
							entity.setCurrentApprove(AdminHelper
									.toEmployee(leaveApprove2));
							entity.setApplyStatus(ApplyStatus.PROGRESS);
							entity.setNode(2);
						}
					} else if ((entity.getLeaveApprove3() != null && !AdminHelper
							.longToString(entity.getLeaveApprove3().getId())
							.equals(leaveApprove3))
							|| (entity.getLeaveApprove3() == null && StringUtils
									.isNotEmpty(leaveApprove3))) {
						if (entity.getNode() > 3) {
							if (StringUtils.isNotEmpty(leaveApprove3)) {
								approveProcessService.delWrongProdess(id,
										entity.getLeaveApprove3());
								// entity.setLeaveApprove3(AdminHelper.toEmployee(leaveApprove3));
								entity.setNode(3);
								entity.setApplyStatus(ApplyStatus.PROGRESS);
								entity.setCurrentApprove(AdminHelper
										.toEmployee(leaveApprove3));
								approveProcessService.delWrongProdess(id,
										entity.getCertigier());
								approveProcessService.delWrongProdess(id,
										entity.getBossAffirm());
							} else {
								approveProcessService.delWrongProdess(id,
										entity.getLeaveApprove3());
								entity.setCurrentApprove(null);
								entity.setApplyStatus(ApplyStatus.FINISHED);
								if (StringUtils.isNotEmpty(certigier)) {
									entity.setNode(5);
								} else if (StringUtils.isNotEmpty(bossAffirm)) {
									entity.setNode(6);
								}
							}
							approveProcessService.delWrongProdess(id, entity
									.getLeaveApprove4());
						} else {
							entity.setCurrentApprove(AdminHelper
									.toEmployee(leaveApprove3));
							entity.setApplyStatus(ApplyStatus.PROGRESS);
							entity.setNode(3);
						}
					} else if ((entity.getLeaveApprove4() != null && !AdminHelper
							.longToString(entity.getLeaveApprove4().getId())
							.equals(leaveApprove4))
							|| (entity.getLeaveApprove4() == null && StringUtils
									.isNotEmpty(leaveApprove4))) {
						if (entity.getNode() > 4) {
							if (StringUtils.isNotEmpty(leaveApprove4)) {
								approveProcessService.delWrongProdess(id,
										entity.getLeaveApprove4());
								// entity.setLeaveApprove4(AdminHelper.toEmployee(leaveApprove4));
								entity.setNode(4);
								entity.setApplyStatus(ApplyStatus.PROGRESS);
								entity.setCurrentApprove(AdminHelper
										.toEmployee(leaveApprove4));
								approveProcessService.delWrongProdess(id,
										entity.getCertigier());
								approveProcessService.delWrongProdess(id,
										entity.getBossAffirm());
							} else {
								approveProcessService.delWrongProdess(id,
										entity.getLeaveApprove4());
								entity.setCurrentApprove(null);
								entity.setApplyStatus(ApplyStatus.FINISHED);
								if (StringUtils.isNotEmpty(certigier)) {
									entity.setNode(5);
								} else if (StringUtils.isNotEmpty(bossAffirm)) {
									entity.setNode(6);
								}
							}

						} else {
							entity.setCurrentApprove(AdminHelper
									.toEmployee(leaveApprove4));
							entity.setApplyStatus(ApplyStatus.PROGRESS);
							entity.setNode(4);
						}

					} else if ((entity.getCertigier() != null && !AdminHelper
							.longToString(entity.getCertigier().getId())
							.equals(leaveApprove3))
							|| (entity.getCertigier() == null && StringUtils
									.isNotEmpty(certigier))) {
						if (entity.getNode() > 5) {
							if (StringUtils.isNotEmpty(certigier)) {
								approveProcessService.delWrongProdess(id,
										entity.getCertigier());
								// entity.setCertigier(AdminHelper.toEmployee(certigier));
								entity.setNode(5);
								entity.setApplyStatus(ApplyStatus.PROGRESS);
								entity.setCurrentApprove(AdminHelper
										.toEmployee(certigier));
								approveProcessService.delWrongProdess(id,
										entity.getBossAffirm());
							} else {
								approveProcessService.delWrongProdess(id,
										entity.getCertigier());
								entity.setCurrentApprove(null);
								entity.setApplyStatus(ApplyStatus.FINISHED);
								if (StringUtils.isNotEmpty(bossAffirm)) {
									entity.setNode(6);
								}
							}

						} else {
							entity.setCurrentApprove(AdminHelper
									.toEmployee(certigier));
							entity.setApplyStatus(ApplyStatus.PROGRESS);
							entity.setNode(5);
						}
					} else if ((entity.getBossAffirm() != null && !AdminHelper
							.longToString(entity.getBossAffirm().getId())
							.equals(bossAffirm))
							|| (entity.getBossAffirm() == null && StringUtils
									.isNotEmpty(bossAffirm))) {
						if (entity.getNode() > 6) {
							if (StringUtils.isNotEmpty(bossAffirm)) {
								approveProcessService.delWrongProdess(id,
										entity.getBossAffirm());
								// entity.setBossAffirm(AdminHelper.toEmployee(bossAffirm));
								entity.setNode(6);
								entity.setApplyStatus(ApplyStatus.PROGRESS);
								entity.setCurrentApprove(AdminHelper
										.toEmployee(bossAffirm));
							} else {
								approveProcessService.delWrongProdess(id,
										entity.getBossAffirm());
								entity.setCurrentApprove(null);
								entity.setApplyStatus(ApplyStatus.FINISHED);
								if (StringUtils.isNotEmpty(bossAffirm)) {
									entity.setNode(6);
								}
							}
						} else {
							entity.setCurrentApprove(AdminHelper
									.toEmployee(bossAffirm));
							entity.setApplyStatus(ApplyStatus.PROGRESS);
							entity.setNode(6);
						}
					}
					entity.setLeaveApprove1(AdminHelper
							.toEmployee(leaveApprove1));
					entity.setLeaveApprove2(AdminHelper
							.toEmployee(leaveApprove2));
					entity.setLeaveApprove3(AdminHelper
							.toEmployee(leaveApprove3));
					entity.setLeaveApprove4(AdminHelper
							.toEmployee(leaveApprove4));
					entity.setCertigier(AdminHelper.toEmployee(certigier));
					entity.setBossAffirm(AdminHelper.toEmployee(bossAffirm));
				} else if (entity.getApplyStatus().name().equals("FAILED")) {
					approveProcessService.delWrongProdess(id, entity
							.getLeaveApprove1());
					approveProcessService.delWrongProdess(id, entity
							.getLeaveApprove2());
					approveProcessService.delWrongProdess(id, entity
							.getLeaveApprove3());
					approveProcessService.delWrongProdess(id, entity
							.getLeaveApprove4());
					approveProcessService.delWrongProdess(id, entity
							.getCertigier());
					approveProcessService.delWrongProdess(id, entity
							.getBossAffirm());
					entity.setCurrentApprove(AdminHelper
							.toEmployee(leaveApprove1));
					entity.setApplyStatus(ApplyStatus.PROGRESS);
					entity.setNode(1);
					entity.setLeaveApprove1(AdminHelper
							.toEmployee(leaveApprove1));
					entity.setLeaveApprove2(AdminHelper
							.toEmployee(leaveApprove2));
					entity.setLeaveApprove3(AdminHelper
							.toEmployee(leaveApprove3));
					entity.setLeaveApprove4(AdminHelper
							.toEmployee(leaveApprove4));
					entity.setCertigier(AdminHelper.toEmployee(certigier));
					entity.setBossAffirm(AdminHelper.toEmployee(bossAffirm));
				}

			} else {
				HolidayApply workOverTime = this.getDefService().get(
						p("overTime_id"));
				if (p("applyStatus").equals("FINISHED")) {
					approveProcessService.delWrongProdess(id, workOverTime
							.getLeaveApprove1());
				}
				setEntity(workOverTime);
				entity = workOverTime;
			}
			getDefService().update(entity);
			setJsonMessage(true, entity.toString().equals("") ? "更新了一条记录!"
					: "更新了(" + entity + ")的记录");
		}
		return render(jo);
	}
	
	@Override
	public String list() throws Exception {
		int nodeId = Integer.parseInt(p("nodeId"));
		String employeeId = p("employeeId");
		if(employeeId.equals("")||employeeId==null){
			employeeId=null;
		}
		String Sdate = Tools.date2String(Tools.string2Date(p("sdate")));
		String Edate = Tools.date2String(Tools.string2Date(p("edate")));
		String applyType = p("applyType");
		String leaveType = p("leaveType");
		ListData<HolidayApply> listData = null;
		//我的任务列表
		if(Constants.MY_TASK_HOLIDAYAPPLY==nodeId){
			listData = holidayApplyService.myTaskList(authedUser,ApplyStatus.PROGRESS,employeeId,Sdate,Edate,start, limit,applyType,leaveType);
		}
		//待审批请假单
		else if(Constants.MY_WAIT_APPROVE_HOLIDAYAPPLY==nodeId){
			listData = holidayApplyService.approveList(authedUser, ApplyStatus.PROGRESS,employeeId,Sdate,Edate, start, limit,applyType,leaveType);
		}
		//我的已审批的假单
		else if(Constants.MY_APPROVED_HOLIDAYAPPLY==nodeId){
			listData = holidayApplyService.approveList(authedUser, ApplyStatus.FINISHED,employeeId,Sdate,Edate, start, limit,applyType,leaveType);
		}
		//审批未通过的假单
		else if(Constants.MY_UNAPPROVED_HOLIDAYAPPLY==nodeId){
			listData = holidayApplyService.approveList(authedUser, ApplyStatus.FAILED,employeeId,Sdate,Edate, start, limit,applyType,leaveType);
		}
//		User user = userService.getUserByAuthedUserId(authedUser.getId());
//		Employee employee = user.getEmployee();
//		Role role = roleService.getRoleByCode(Role.SYSTEM_MANAGER);
//		List<Role> roles = userService.getRolesByAuthedUserId(user.getId());
		JSONArray ja = new JSONArray();
		for(HolidayApply ha:listData.getList()){
			ja.add(toJsonObject(ha));
		}
		jo.put(DATA_KEY, ja);
		jo.put(TOTAL_COUNT_KEY, listData.getTotal());
		return render(jo);
	}
	
	@Override
	public String del() throws Exception {
		// TODO Auto-generated method stub
		HolidayApply holidayApply = this.holidayApplyService.get(id);
		List<ApproveProcess> app = holidayApply.getApproveProcesses();
		String applyStatus = holidayApply.getApplyStatus().name();
		//删除审批通过的假条时一起删除原来的数据
		if(applyStatus.equals("FINISHED")){
			Date startDate = holidayApply.getStartDate();
			Date endDate = holidayApply.getEndDate();
			String employeeId = holidayApply.getLeaveApply().getId()+"";
			attendancePlusService.clearPlusInfos(startDate, endDate, employeeId);
		}
		try {
			for (ApproveProcess pro : app) {
				approveProcessService.del(pro);
			}
			
			this.holidayApplyService.del(holidayApply);
			setJsonMessage(true,"记录成功删除!");
		} catch (Exception e) {
			setJsonMessage(true,"记录删除失败");
		}
		return render(jo);
	}
	
	public String holidayApprove() throws Exception {
		ApproveStatus approveStatus = null;
		if(p("approveStatus").equals("")){
			if(p("suggestion").equals("")){
				setJsonMessage(true, "请选择同意或不同意，或填入审批意见");
				return render(jo);
			}
			approveStatus = ApproveStatus.valueOf("REBUT");
		}else{
			approveStatus = ApproveStatus.valueOf(p("approveStatus"));
		}
		String id = p("id");
		HolidayApply ha = holidayApplyService.get(id);
		if(ha.getCancelDate()!=null&&(ApproveStatus.AGREE.equals(approveStatus)||ApproveStatus.INTENDAGREE.equals(approveStatus))){
			Calendar cancel = Calendar.getInstance();
			cancel.setTime(ha.getCancelDate());
			cancel.add(Calendar.DATE, 1);
			attendancePlusService.clearPlusInfos(cancel.getTime(),ha.getEndDate(),Long.toString(ha.getLeaveApply().getId()));
			ha.setEndDate(ha.getCancelDate());
			ha.setLeaveDays(ha.getCancelDays());
			ha.setApplyStatus(ApplyStatus.FINISHED);
			ha.setCurrentApprove(null);
			holidayApplyService.update(ha);
		}else if(ApproveStatus.AGREE.equals(approveStatus)||ApproveStatus.INTENDAGREE.equals(approveStatus)){
			holidayApplyService.holidayApprove(ha);
		}else{
			holidayApplyService.unPassApprove(ha);
		}
		ApproveProcess ap = new ApproveProcess();
		ap.setHolidayApply(ha);
		if(ha.getCertigier()!=null&&authedUser.getEmployee().equals(ha.getCertigier())){
			ap.setCertigier(Constants.IS_CERTIGIER);
		}
		setEntity(ap);
		approveProcessService.add(ap);
		setJsonMessage(true, "审批成功");
		return render(jo);
	}
	
	public String holidayApproveEntering()throws Exception{
		String id = p("id");
		HolidayApply ha = holidayApplyService.get(id);
		String assistant = p("assistant");//分管行领导
		String leader = p("leader");//分行行长
		if(StringUtils.isNotEmpty(assistant)){
			ha.setLeaveApprove2(AdminHelper.toEmployee(assistant));
			holidayApplyService.holidayApprove(ha);
			ApproveProcess ap = new ApproveProcess();
			ap.setApproveDate(new Date());
			ap.setApproveStatus(ApproveStatus.AGREE);
			ap.setHolidayApply(ha);
			ap.setLeaveApprove(AdminHelper.toEmployee(assistant));
			ap.setSuggestion(p("deputyGovernor"));
			approveProcessService.add(ap);
		}
		if(StringUtils.isNotEmpty(leader)){
			ha.setLeaveApprove2(AdminHelper.toEmployee(leader));
			holidayApplyService.holidayApprove(ha);
			ApproveProcess ap = new ApproveProcess();
			ap.setApproveDate(new Date());
			ap.setApproveStatus(ApproveStatus.AGREE);
			ap.setHolidayApply(ha);
			ap.setLeaveApprove(AdminHelper.toEmployee(leader));
			ap.setSuggestion(p("president"));
			approveProcessService.add(ap);
		}
		return render(jo);
	}
	
	public String holidayCancel()throws Exception{
		HolidayApply holiday = holidayApplyService.get(id);
		Date cancelDate = AdminHelper.toDate(p("cancelEndDate"));
		holiday.setCancelDate(cancelDate);
		holiday.setCancelDays(Double.parseDouble(p("cancelDays")));
		holiday.setApplyStatus(ApplyStatus.PROGRESS);
		holiday.setCancelApprove(AdminHelper.toEmployee(p("cancelApprove")));
		holiday.setCurrentApprove(AdminHelper.toEmployee(p("cancelApprove")));
		holidayApplyService.update(holiday);
		setJsonMessage(true, "销假申请已经完成，等待审批");
		return render(jo);
	}
	
	public String placeOnFile()throws Exception{
		String id = p("id");
		HolidayApply ho = holidayApplyService.get(id);
		ho.setApplyStatus(ApplyStatus.FINISHED);
		holidayApplyService.update(ho);
		Calendar start = Calendar.getInstance();
		start.setTime(ho.getStartDate());
		
		Calendar end = Calendar.getInstance();
		end.setTime(ho.getEndDate());
		attendancePlusService.createPlusInfo(start, end,Long.toString(ho.getLeaveApply().getId()), "ALL_DAY", "LEAVE",ho.getApplyReason(), null, null);
		if(ho.getApplyStatus().name().equals("FINISHED")&&ho.getLeaveType().getId().equals("ANNUAL_LEAVE")){
			Employee e = employeeService.get(Long.toString(ho.getLeaveApply().getId()));
			e.setAnnualLeave(e.getAnnualLeave()-ho.getLeaveDays());
			employeeService.update(e);
		}
		setJsonMessage(true,"请假单以归档");
		return render(jo);
	}
	/**
	 * 加班审批流程
	 * @return
	 * @throws Exception
	 */
	public String workOverTime()throws Exception{
		HolidayApply holidayApply = new HolidayApply();
		setEntity(holidayApply);
		holidayApplyService.add(holidayApply);
		setJsonMessage(true, "申请以成功提交");
		return render(jo);
	}
	
	public String getProcessById()throws Exception{
		String holidayApplyId = p("holidayApplyId");
		List<ApproveProcess> list = approveProcessService.getProcessesByHolidayId(holidayApplyId);
		JSONArray jsonArray = new JSONArray();
		for(ApproveProcess a : list){
			jsonArray.add(toJsonObject(a));
		}
		jo.put("approveProcess", jsonArray);
		return render(jo);
	}
	
	protected void setEntity(ApproveProcess a) throws Exception {
		a.setApproveDate(new Date());
		if(p("approveStatus")!=null&&!p("approveStatus").equals("")){
			a.setApproveStatus(ApproveStatus.valueOf(p("approveStatus")));
		}else{
			a.setApproveStatus(ApproveStatus.valueOf("REBUT"));
		}
		
		a.setLeaveApprove(authedUser.getEmployee());
		a.setSuggestion(p("suggestion"));
	}
	
	@Override
	protected void setEntity(HolidayApply entity) throws Exception {
		String applyType = p("operateApplyType");
		entity.setApplyType(applyType);
		entity.setOperater(authedUser.getEmployee());
		entity.setLeaveApplyType(p("leaveApplyType"));
		if(applyType.equals("overTime")){
			entity.setLeaveApply(AdminHelper.toEmployee(p("employeeId")));
			entity.setWorkOverTime(Tools.stringToDate(p("workOverTimeDate")));
			entity.setEWorkOverTimeDate(Tools.stringToDate(p("eWorkOverTimeDate")));
			entity.setOverTimeStart(p("workOverTimeStart"));
			entity.setOverTimeEnd(p("workOverTimeEnd"));
			entity.setApplyReason(p("workOverTimeReason"));
			entity.setLeaveApprove1(AdminHelper.toEmployee(p("leaveApprove1")));
			entity.setCurrentApprove(AdminHelper.toEmployee(p("leaveApprove1")));
		}else{
			Employee leaveApply = AdminHelper.toEmployee(p("leaveApply"));
			Dict leaveType = AdminHelper.toDict(p("leaveType"));
			
			String tempLeaveType = p("leaveType");
			if(StringUtils.isNotBlank(tempLeaveType)) {
				leaveType = dictService.get(tempLeaveType);
			}
			
			double leavesDay = Double.parseDouble(p("leaveDays"));
			entity.setLeaveApply(leaveApply);
			entity.setLeaveType(leaveType);
			entity.setStartDate(AdminHelper.toDate(p("startDate")));
			entity.setEndDate(AdminHelper.toDate(p("endDate")));
			entity.setIsMarry(p("isMarry").equals("YES")?"已婚":"未婚");
			entity.setLeaveDays(leavesDay);
			entity.setCancelDays(Double.parseDouble(p("leaveDays")));
			entity.setBackDate(AdminHelper.toDate(p("backDate")));
			entity.setSite(p("site"));
			entity.setGoOutPhone(p("goOutPhone"));
			entity.setApplyReason(p("applyReason"));
			entity.setCurrentApprove(AdminHelper.toEmployee(p("leaveApprove1")));
		}
		
		entity.setLeaveApprove1(AdminHelper.toEmployee(p("leaveApprove1")));
		entity.setLeaveApprove2(AdminHelper.toEmployee(p("leaveApprove2")));
		entity.setLeaveApprove3(AdminHelper.toEmployee(p("leaveApprove3")));
		entity.setLeaveApprove4(AdminHelper.toEmployee(p("leaveApprove4")));
		entity.setCertigier(AdminHelper.toEmployee(p("certigier")));
		entity.setBossAffirm(AdminHelper.toEmployee(p("bossAffirm")));
	}
	
	protected void setEntityForEdit(HolidayApply entity) throws Exception {
		String applyType = p("operateApplyType");
		entity.setApplyType(applyType);
		entity.setOperater(authedUser.getEmployee());
		entity.setLeaveApply(AdminHelper.toEmployee(p("leaveApply")));
		entity.setLeaveType(AdminHelper.toDict(p("leaveType")));
		entity.setStartDate(AdminHelper.toDate(p("startDate")));
		entity.setEndDate(AdminHelper.toDate(p("endDate")));
		entity.setIsMarry(p("isMarry").equals("YES")?"已婚":"未婚");
		entity.setLeaveDays(Double.parseDouble(p("leaveDays")));
		entity.setCancelDays(Double.parseDouble(p("leaveDays")));
		entity.setBackDate(AdminHelper.toDate(p("backDate")));
		entity.setSite(p("site"));
		entity.setGoOutPhone(p("goOutPhone"));
		entity.setApplyReason(p("applyReason"));
	}

	@Override
	protected JSONObject toJsonObject(HolidayApply entity) throws Exception {
		AdminHelper record = new AdminHelper();
		record.put("id", entity.getId());
		record.put("name", entity.getLeaveApply().getName());
		record.put("code", entity.getLeaveApply().getCode());
		record.put("organization", entity.getLeaveApply().getOrganization());
		record.put("applyReason", entity.getApplyReason());
		record.put("applyType", entity.getApplyType().equals("holiday")?"请假":"加班");
		record.put("applyDate", entity.getApplyDate());
		Dict dict = entity.getLeaveApply().getPosition();
		record.put("position", dict==null?"":dict.getText());
		JSONObject applyStatus = new JSONObject();
		applyStatus.put("id", entity.getApplyStatus());
		applyStatus.put("text", entity.getApplyStatus().getLabel());
		record.put("applyStatus", applyStatus);
		record.put("currentApprove", entity.getCurrentApprove());
		record.put("oldOrNew",entity.getOldOrNew());
		record.put("leaveApplyType", entity.getLeaveApplyType());
		if(entity.getApplyType().equals("overTime")){
			record.put("leaveApply", entity.getLeaveApply());
			record.put("workOverTime", entity.getWorkOverTime());
			record.put("eWorkOverTimeDate", entity.getEWorkOverTimeDate());
			record.put("workOverTimeStart", entity.getOverTimeStart());
			record.put("workOverTimeEnd", entity.getOverTimeEnd());
			record.put("leaveApprove1", entity.getLeaveApprove1());
		}else{
			record.put("leaveApply", entity.getLeaveApply());
			record.put("joinWorkDate", entity.getLeaveApply().getJoinWorkDate());
			record.put("hireDate", entity.getLeaveApply().getHireDate());
			record.put("goOutPhone", entity.getGoOutPhone());
			record.put("backDate", entity.getBackDate());
			record.put("site", entity.getSite());
			record.put("node", entity.getNode());
			JSONObject leaveType = new JSONObject();
			leaveType.put("id", entity.getLeaveType().getId());
			leaveType.put("text", entity.getLeaveType().getText());
			record.put("leaveType",leaveType);
			record.put("leaveDays",entity.getLeaveDays());
			record.put("startDate", entity.getStartDate());
			record.put("endDate", entity.getEndDate());
			record.put("isMarry", entity.getIsMarry());
			record.put("cancelDate", entity.getCancelDate());
			record.put("leaveApprove1", entity.getLeaveApprove1());
			record.put("leaveApprove2", entity.getLeaveApprove2());
			record.put("leaveApprove3", entity.getLeaveApprove3());
			record.put("leaveApprove4", entity.getLeaveApprove4());
			record.put("certigier", entity.getCertigier());
			record.put("bossAffirm", entity.getBossAffirm());
		}
		return record.getJsonObject();
	}
	protected JSONObject toJsonObject(ApproveProcess ap) throws Exception {
		AdminHelper record = new AdminHelper();
		record.put("id", ap.getId());
		record.put("leaveApprove", ap.getLeaveApprove().getName());
		record.put("approveStatus", ap.getApproveStatus().getLabel());
		record.put("suggestion", ap.getSuggestion());
		record.put("approveDate", ap.getApproveDate());
		record.put("postType", ap.getLeaveApprove().getPostType().getText());
		record.put("approveOrganization", ap.getLeaveApprove().getOrganization().getName());
		if(ap.getCertigier()!=null&&ap.getCertigier().equals(Constants.IS_CERTIGIER)){
			record.put("isCertigier", Constants.IS_CERTIGIER);//如果是假期负责人则为yes
		}else{
			record.put("isCertigier", Constants.NOT_CERTIGIER);
		}
		return record.getJsonObject();
	}
	@Override
	public GenericService<HolidayApply> getDefService() {
		return holidayApplyService;
	}
}
