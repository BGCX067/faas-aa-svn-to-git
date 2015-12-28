package com.fortunes.fjdp.admin.service;

import java.util.Map;

import net.fortunes.core.service.GenericService;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Component;

import com.fortunes.fjdp.admin.model.Log;

@Component
public class LogService extends GenericService<Log> {
	
	protected DetachedCriteria getConditions(String query,Map<String, String> queryMap){
		
		String roles = queryMap.get("roles");
		String userDisplayName = queryMap.get("userDisplayName");
				
		DetachedCriteria criteria = super.getConditions(query, queryMap);
		if(query !=  null){
			criteria.add(Restrictions.ilike("contents", query,MatchMode.ANYWHERE));
		}
		if(roles.indexOf("系统管理员") == -1) {
			criteria.add(Restrictions.eq("opUser", userDisplayName));
		}
		return criteria;
	}
	
	protected Order getOrder(){
		return Order.desc("createTime");
	}
	
}
