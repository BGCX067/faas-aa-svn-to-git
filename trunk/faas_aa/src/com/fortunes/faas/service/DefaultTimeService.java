package com.fortunes.faas.service;

import java.util.List;
import java.util.Map;

import org.hibernate.criterion.DetachedCriteria;
import org.springframework.stereotype.Component;

import com.fortunes.faas.model.DefaultTime;

import net.fortunes.core.service.GenericService;
@Component
public class DefaultTimeService extends GenericService<DefaultTime>{
	
	public DefaultTime getDefaultTimeByOrg(Long orgId){
		String hql = "select d from DefaultTime d where d.organization.id = ?";
		List<DefaultTime> defaultTime = this.getDefDao().findByQueryString(hql, orgId);
		return defaultTime.size()>0?defaultTime.get(0):null;
	}
}
