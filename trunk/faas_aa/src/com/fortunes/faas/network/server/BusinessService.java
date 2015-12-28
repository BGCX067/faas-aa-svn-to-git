package com.fortunes.faas.network.server;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import net.fortunes.core.Helper;
import net.fortunes.util.Tools;

import org.apache.mina.core.session.IoSession;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.fortunes.faas.model.AttShift;
import com.fortunes.faas.model.MeetOrTrain;
import com.fortunes.faas.model.MoveAttInfo;
import com.fortunes.faas.network.model.MessageObject;
import com.fortunes.faas.service.MeetOrTrainService;
import com.fortunes.faas.service.MoveAttInfoService;
import com.fortunes.faas.util.VoUtil;
import com.fortunes.faas.vo.EmployeeVO;
import com.fortunes.faas.vo.MeetOrTrainVO;
import com.fortunes.faas.vo.MoveAttInfoVO;
import com.fortunes.faas.vo.OrganizationVO;
import com.fortunes.fjdp.admin.AdminHelper;
import com.fortunes.fjdp.admin.model.Employee;
import com.fortunes.fjdp.admin.model.Organization;
import com.fortunes.fjdp.admin.service.EmployeeService;
import com.fortunes.fjdp.admin.service.OrganizationService;

@Component
public class BusinessService {
	
	@Resource EmployeeService employeeService;
	@Resource OrganizationService organizationService;
	@Resource private JdbcTemplate jdbcTemplate;
	@Resource private MeetOrTrainService meetOrTrainService;
	@Resource private MoveAttInfoService moveAttInfoService;
	
	public MessageObject<EmployeeVO> syncEmp(IoSession session,Object msg){
		List<Employee> emps = employeeService.getAll();
		MessageObject<EmployeeVO> list = new MessageObject<EmployeeVO>("1000",toVOList(emps));
		return list;
	}
	
	public MessageObject<OrganizationVO> syncOrganization(IoSession session,Object msg){
		List<Organization> orgs = organizationService.getAll();
		MessageObject<OrganizationVO> list = new MessageObject<OrganizationVO>("1000",toOrgVO(orgs));
		return list;
	}
	
	public MessageObject<AttShift> syncAttShift(IoSession session,Object msg) throws Exception {
		Object as=msg;
		return null;
	}
	
	public MessageObject<MeetOrTrainVO> syncMeet(IoSession session,Object msg) throws Exception{
		MessageObject<MeetOrTrainVO> me=(MessageObject<MeetOrTrainVO>)msg;
		long meId=0;
		int status = 1;
		List<MeetOrTrainVO> list = me.getList();
		for(MeetOrTrainVO meVo : list){
			MeetOrTrain meet=new MeetOrTrain();
			meet.setName(meVo.getName());
			meet.setAddress(meVo.getAddress());
			meet.setStartDate(meVo.getStartDate());
			meet.setOrgName(meVo.getOrgName());
			meet.setEndDate(meVo.getEndDate());
			meet.setOrganizationCode(meVo.getOrganizationCode());
			if(meVo.getOrganizationCode()!="HR"){
				Organization organization = null;
				if(meVo.getOrganizationCode().equals("null") || meVo.getOrganizationCode().equals("")){
					organization = organizationService.getOrganizationByName(meVo.getOrgName());
				}else{
					organization = organizationService.getOrganizationByCode(meVo.getOrganizationCode());
				}
				Employee depEmp = organization.getBranchLeader();
				Employee perEmp = organizationService.getOrganizationByCode("HR").getBranchLeader();
				if(depEmp==null&&perEmp==null){
					status = 0;//如果是0则是没有任何一个部门负责人
					break;
				}
				if(depEmp==perEmp||depEmp==null){
					meet.setPersonnelVerify(perEmp);
					meet.setCurrentVerify(perEmp);
				}else{
					meet.setDepartmentVerify(depEmp);
					meet.setPersonnelVerify(perEmp);
					meet.setCurrentVerify(depEmp);
				}
				
			}else{
				Organization organization = organizationService.getOrganizationByCode(meVo.getOrganizationCode());
				meet.setPersonnelVerify(organizationService.getOrganizationByCode("HR").getBranchLeader());
				meet.setCurrentVerify(organization.getBranchLeader());
			}
			meet.setTheme(meVo.getTheme());
			meId=meetOrTrainService.AddMeetOrTrain(meet);
		}
		List<MeetOrTrainVO> as=new ArrayList<MeetOrTrainVO>();
		MeetOrTrainVO meOV=new MeetOrTrainVO();
		meOV.setId(meId);
		meOV.setTheme(status+"");
		as.add(meOV);
		System.out.println("返回的Id是："+meId);
		MessageObject<MeetOrTrainVO> list1 = new MessageObject<MeetOrTrainVO>("1000",as);
		return list1;
	}
	
	public MessageObject<MoveAttInfoVO> syncAtt(IoSession session,Object msg) throws Exception{
		MessageObject<MoveAttInfoVO> att = (MessageObject<MoveAttInfoVO>)msg;
		List<MoveAttInfo> attList=new ArrayList<MoveAttInfo>();
		List<MoveAttInfoVO> list=att.getList();
		for(MoveAttInfoVO e : list){
			MoveAttInfo mo=new MoveAttInfo();
			mo.setName(e.getName());mo.setCode(e.getCode());mo.setOrganization(e.getOrganization());
			mo.setAttDate(e.getAttDate());mo.setAttPlus(e.getAttPlus());
			mo.setMeetOrTrain(meetOrTrainService.get(e.getMeId()+""));
			mo.setStatus(e.getStatus());mo.setPlusInfo(e.getPlusInfo());
			attList.add(mo);
		}
		moveAttInfoService.bathMoveAttInfo(attList);
		MessageObject<MoveAttInfoVO> atts = new MessageObject<MoveAttInfoVO>("1000",null);
		return atts;
	}
	
	private List<EmployeeVO> toVOList(List<Employee> list){
		List<EmployeeVO> voList = new ArrayList<EmployeeVO>();
		for(Employee emp : list){
			voList.add(VoUtil.toVO(emp));
		}
		return voList;
	}

	private List<OrganizationVO> toOrgVO(List<Organization> list){
		List<OrganizationVO> voList = new ArrayList<OrganizationVO>();
		for(Organization org : list){
			voList.add(VoUtil.toVO(org));
		}
		return voList;
	}
}
