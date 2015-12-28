package com.fortunes.faas.vo;

import java.util.Date;
import net.fortunes.core.Model;
/**
 * 
 * @author Leo
 * @version 2011-4-22
 */
public class EmployeeVO extends Model {
	
	private String code;
	
	private String name;
	
	private String pinYinName;
	
	private String nickName;
	
	private String sex;
	
	private String post;
	
	private String position;//岗位
	
	private String education;
	
	private String peopleType;//员工类别（合同）
	
	private String postType;//岗位类型：员工、管理岗位
	
	private String email;
	
	private String photoId;
	
	private String phone;
	
	private String mobile;
	
	private Date birthday;
	
	private Date joinWorkDate;
	
	private Date hireDate;
	
	private String status;
	
	private long organizationId;
	
	private String card;
	
	private String password;
	
	private boolean turnAway = false;
	
	private Date turnAwayDate;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPinYinName() {
		return pinYinName;
	}

	public void setPinYinName(String pinYinName) {
		this.pinYinName = pinYinName;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getPost() {
		return post;
	}

	public void setPost(String post) {
		this.post = post;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getEducation() {
		return education;
	}

	public void setEducation(String education) {
		this.education = education;
	}

	public String getPeopleType() {
		return peopleType;
	}

	public void setPeopleType(String peopleType) {
		this.peopleType = peopleType;
	}

	public String getPostType() {
		return postType;
	}

	public void setPostType(String postType) {
		this.postType = postType;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhotoId() {
		return photoId;
	}

	public void setPhotoId(String photoId) {
		this.photoId = photoId;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public Date getJoinWorkDate() {
		return joinWorkDate;
	}

	public void setJoinWorkDate(Date joinWorkDate) {
		this.joinWorkDate = joinWorkDate;
	}

	public Date getHireDate() {
		return hireDate;
	}

	public void setHireDate(Date hireDate) {
		this.hireDate = hireDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public long getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(long organizationId) {
		this.organizationId = organizationId;
	}

	public String getCard() {
		return card;
	}

	public void setCard(String card) {
		this.card = card;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean getTurnAway() {
		return turnAway;
	}

	public void setTurnAway(boolean turnAway) {
		this.turnAway = turnAway;
	}

	public Date getTurnAwayDate() {
		return turnAwayDate;
	}

	public void setTurnAwayDate(Date turnAwayDate) {
		this.turnAwayDate = turnAwayDate;
	}
	
}
