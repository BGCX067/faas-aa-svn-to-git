package com.fortunes.faas.util;

import java.util.List;

import com.fortunes.faas.vo.EmployeeVO;
import com.fortunes.faas.vo.OrganizationVO;
import com.fortunes.fjdp.admin.model.Employee;
import com.fortunes.fjdp.admin.model.Organization;

/**
 * 
 * @author Leo
 * @version 2011-4-22
 */
public class VoUtil {

	public static EmployeeVO toVO(Employee emp){//23
		EmployeeVO vo = new EmployeeVO();
		vo.setBirthday(emp.getBirthday());
		vo.setCard(emp.getCard());
		vo.setCode(emp.getCode());
		vo.setEducation(emp.getEducation()!=null?emp.getEducation().getText():null);
		vo.setEmail(emp.getEmail());
		vo.setHireDate(emp.getHireDate());
		vo.setJoinWorkDate(emp.getJoinWorkDate());
		vo.setMobile(emp.getMobile());
		vo.setName(emp.getName());
		vo.setNickName(emp.getNickName());
		if(emp.getOrganization()!=null){
			vo.setOrganizationId(emp.getOrganization().getId());
		}
		vo.setPassword(emp.getPassword());
		vo.setPeopleType(emp.getPeopleType()!=null?emp.getPeopleType().getText():null);
		vo.setPhone(emp.getPhone());
		vo.setPhotoId(emp.getPhotoId());
		vo.setPinYinName(emp.getPinYinName());
		vo.setPosition(emp.getPosition()!=null?emp.getPosition().getText():null);
		vo.setPostType(emp.getPostType()!=null?emp.getPostType().getText():null);
		vo.setPost(emp.getPost()!=null?emp.getPost().getText():null);
		vo.setStatus(emp.getStatus()!=null?emp.getStatus().getText():null);
		vo.setSex(emp.getSex()!=null?emp.getSex().getText():null);
		vo.setTurnAway(emp.getTurnAway());
		vo.setTurnAwayDate(emp.getTurnAwayDate());
		
		return vo;
	}
	
	public static OrganizationVO toVO(Organization org){
		OrganizationVO vo = new OrganizationVO();
		vo.setId(org.getId());
		vo.setName(org.getName());
		vo.setFullName(org.getFullName());
		vo.setAddress(org.getAddress());
		vo.setLeaf(org.getLeaf());
		if(org.getParent()!=null){
			vo.setParentId(org.getParent().getId());
		}
		vo.setShortName(org.getShortName());
		vo.setTel(org.getTel());
		vo.setType(org.getType().getText());
		vo.setCode(org.getCode());
		return vo;
	}
}
