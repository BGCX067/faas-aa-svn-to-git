package com.fortunes.faas.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Component;

import com.fortunes.faas.model.MeetOrTrain;
import com.fortunes.faas.vo.MeetOrTrainVO;
import com.fortunes.fjdp.admin.model.Employee;
import com.fortunes.fjdp.admin.model.Organization;
import com.fortunes.fjdp.admin.service.OrganizationService;

import net.fortunes.core.service.GenericService;
import net.fortunes.util.Tools;
@Component
@SuppressWarnings("unchecked")
public class MeetOrTrainService extends GenericService<MeetOrTrain>{
	@Resource OrganizationService organizationService;
	
	public long AddMeetOrTrain(MeetOrTrain me){
		long id=0;
		if(me!=null){
			getDefDao().add(me);
			id=me.getId();
		}
		return id;
	}
	
	@Override
	protected DetachedCriteria getConditions(String query,
			Map<String, String> queryMap) {
		// TODO Auto-generated method stub
		DetachedCriteria criteria = super.getConditions(query, queryMap);
		if(queryMap!=null){
			if(StringUtils.isNotEmpty(queryMap.get("employee"))){
				criteria.add(Restrictions.or(
						Restrictions.eq("personnelVerify.id", Long.parseLong(queryMap.get("employee"))),
						Restrictions.eq("departmentVerify.id", Long.parseLong(queryMap.get("employee")))
				));
			}
			if(StringUtils.isNotEmpty(queryMap.get("organization"))){
				Organization o = organizationService.get(queryMap.get("organization"));
				criteria.add(Restrictions.eq("orgName", o.getName()));
			}
			if(StringUtils.isNotEmpty(queryMap.get("sdate"))){
				criteria.add(Restrictions.ge("startDate",Tools.string2Date(queryMap.get("sdate"))));
			}
			if(StringUtils.isNotBlank(queryMap.get("edate"))){
				criteria.add(Restrictions.le("startDate",Tools.string2Date(queryMap.get("edate"))));
			}
		}
//		criteria.addOrder(Order.desc("startDate"));
		return criteria;
	}
	
	public List getMeetByEmployee(Employee e){
		String hql=null;
		hql = "select e from MeetOrTrain as e where e.personnelVerify.id = ? or e.departmentVerify.id = ?";
		List<MeetOrTrain> list = getDefDao().findByQueryString(hql, e.getId(),e.getId());
		return list;
	}

}
