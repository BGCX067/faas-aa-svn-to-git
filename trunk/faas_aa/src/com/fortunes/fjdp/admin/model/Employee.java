package com.fortunes.fjdp.admin.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.AccessType;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.fortunes.faas.model.Area;
import com.fortunes.faas.model.MeetOrTrain;
import com.fortunes.fjdp.admin.AdminHelper;
import com.sun.image.codec.jpeg.TruncatedFileException;

import net.fortunes.core.Helper;
import net.fortunes.core.Model;

@Entity
public class Employee extends Model{
	
	@Id 
	@GeneratedValue
	private long id;
	
	private String code;
	
	private String name;
	
	private String pinYinName;
	
	private String nickName;
	
	@ManyToOne
	private Dict sex;
	
	@ManyToOne
	private Dict post;
	
	@ManyToOne
	private Dict position;//岗位
	
	@ManyToOne
	private Dict education;
	
	@ManyToOne
	private Dict peopleType;//员工类别（合同）
	
	@ManyToOne
	private Dict postType = new Dict("postType_01","员工");//岗位类型：员工、管理岗位
	
	private String email;
	
	private String photoId;
	
	private String phone;
	
	private String mobile;
	
	@Column @Temporal(TemporalType.DATE)
	private Date birthday;
	
	@Column @Temporal(TemporalType.DATE)
	private Date joinWorkDate;
	
	@Column @Temporal(TemporalType.DATE)
	private Date hireDate;
	
	@ManyToOne
	private Dict status;
	
	@OneToOne(mappedBy = "employee" )
//	@Fetch(FetchMode.JOIN)
	private User user;
		
	@ManyToOne
	private Organization organization;
	
	private String card;
	
	private String password;
	
	private boolean turnAway = false;
	
	@Column @Temporal(TemporalType.DATE)
	private Date turnAwayDate;
	
	private double salary = 0.0;


	private double annualLeave = 0.0;
	

	//区域津贴
	private double regionalAllowance = 0.0;
	
//	@ManyToMany
//	private List<Area> areas = new ArrayList<Area>();
	
	@ManyToOne
	private Organization branchLeader;
	
	@OneToMany(mappedBy="currentVerify")
	private List<MeetOrTrain> meetOrTrain = new ArrayList<MeetOrTrain>();
	
	
	
	public Employee() {
    }
    
    public Employee(long id) {
    	this.id = id;
    }
    
    public Employee(long id,String name){
    	this.id = id;
    	this.name = name;
    }
    
    public Employee(long id,String name,String code,String card){
    	this.id = id;
    	this.name = name;
    	this.code = code;
    	this.card =card;
    }
    
    public Employee(String code,String name) {
        this.code = code;
        this.name = name;
    }
    
    public Employee(long id,String code,String name){
    	this.id = id;
    	this.code = code;
    	this.name = name;
    }
    public Employee(long id,String name,long orgId,String orgName){
    	this.id = id;
    	this.name = name;
    	if(orgId!=0){
    		Organization org = new Organization(orgId,"",orgName);
    		this.organization = org;
    	}else{
    		this.organization = null;
    	}
    }
    
    public Employee(long id , String code,String name,Dict sex , Dict position,Date birthday){
    	this.id = id;
    	this.code = code;
    	this.name = name;
    	this.sex = sex;
    	this.position = position;
    	this.birthday = birthday;
    }
    public Employee(long id , String code,String name,String sexId,String sexText , String positionId,String positionText,Date birthday){
    	this.id = id;
    	this.code = code;
    	this.name = name;
    	this.sex = new Dict(sexId,sexText);
    	this.position = new Dict(positionId,positionText);
    	this.birthday = birthday;
    }
    public Employee(long id ,String code,String name,long orgId,String orgShortName){
    	this.id = id;
    	this.code = code;
    	this.name = name;
    	if(orgId!=0){
    		Organization org = new Organization(orgId,"",orgShortName);
    		this.organization = org;
    	}else{
    		this.organization = null;
    	}
    }
    public Employee(long id ,String code,String name,Dict sex,long orgId,String orgShortName){
    	this.id = id;
    	this.code = code;
    	this.name = name;
    	this.sex = sex;
    	if(orgId!=0){
    		Organization org = new Organization(orgId,"",orgShortName);
    		this.organization = org;
    	}else{
    		this.organization = null;
    	}
    }

    public Employee(long id,String code,String name,long orgId,String dictId,Date turnAwayDate,boolean turnAway){
    	this.id= id;
    	this.name = name;
    	this.code = code;
    	if(orgId!=0){
    		Organization org = new Organization(orgId,dictId);
    		this.organization = org;
    	}else{
    		this.organization=null;
    	}
    	this.turnAwayDate = turnAwayDate;
    	this.turnAway = turnAway;
    }
    public Employee(long id,String code,String name,long orgId,String orgName,String dictId){
    	this.id = id;
    	this.name = name;
    	this.code = code;
    	if(orgId!=0){
    		Organization org = new Organization(orgId,dictId);
    		this.organization = org;
    	}else{
    		this.organization=null;
    	}
    }
    @Override
    public String toString() {
    	return "员工:"+name;
    }
    
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

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

	public Dict getSex() {
		return sex;
	}

	public void setSex(Dict sex) {
		this.sex = sex;
	}

	public Dict getPost() {
		return post;
	}

	public void setPost(Dict post) {
		this.post = post;
	}
	
	

	public Dict getPosition() {
		return position;
	}

	public void setPosition(Dict position) {
		this.position = position;
	}

	public Dict getEducation() {
		return education;
	}

	public void setEducation(Dict education) {
		this.education = education;
	}

	public Dict getPeopleType() {
		return peopleType;
	}

	public void setPeopleType(Dict peopleType) {
		this.peopleType = peopleType;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
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

	public Dict getStatus() {
		return status;
	}

	public void setStatus(Dict status) {
		this.status = status;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	public void setHireDate(Date hireDate) {
		this.hireDate = hireDate;
	}

	public Date getHireDate() {
		return hireDate;
	}	

	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	public void setPhotoId(String photoId) {
		this.photoId = photoId;
	}

	public String getPhotoId() {
		return photoId;
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

	public Date getTurnAwayDate() {
		return turnAwayDate;
	}

	public void setTurnAwayDate(Date turnAwayDate) {
		this.turnAwayDate = turnAwayDate;
	}

	@Override
	public boolean equals(Object obj) {
		Employee e = (Employee)obj;
		if(this.getCode().equals(e.getCode())){
			return true;
		}
		return false;
	}

	public void setTurnAway(boolean turnAway) {
		this.turnAway = turnAway;
	}

	public boolean getTurnAway() {
		return turnAway;
	}

	public Dict getPostType() {
		return postType;
	}

	public void setPostType(Dict postType) {
		this.postType = postType;
	}

	public Organization getBranchLeader() {
		return branchLeader;
	}

	public void setBranchLeader(Organization branchLeader) {
		this.branchLeader = branchLeader;
	}

	public double getSalary() {
		return salary;
	}

	public void setSalary(double salary) {
		this.salary = salary;
	}

    public List<MeetOrTrain> getMeetOrTrain() {
		return meetOrTrain;
	}

	public void setMeetOrTrain(List<MeetOrTrain> meetOrTrain) {
		this.meetOrTrain = meetOrTrain;
	}

	public double getRegionalAllowance() {
		return regionalAllowance;
	}

	public void setRegionalAllowance(double regionalAllowance) {
		this.regionalAllowance = regionalAllowance;
	}
	
	public double getAnnualLeave() {
		return annualLeave;
	}

	public void setAnnualLeave(double annualLeave) {
		this.annualLeave = annualLeave;
	}
}
