package com.fortunes.faas.service;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Component;

import com.fortunes.faas.model.MoveAttInfo;

import net.fortunes.core.service.GenericService;

@Component
@SuppressWarnings("unchecked")
public class MoveAttInfoService extends GenericService<MoveAttInfo>{
	public void bathMoveAttInfo(List<MoveAttInfo> list) throws Exception{
		int count=0;
		for(MoveAttInfo m : list){
			add(m);
			++count;
		}
	}
	
	@Override
	protected DetachedCriteria getConditions(String query,Map<String, String> queryMap) {
		DetachedCriteria criteria = super.getConditions(query, queryMap);
		if(queryMap != null){
			if(StringUtils.isNotEmpty(queryMap.get("meetId"))){
				criteria.add(Restrictions.eq("meetOrTrain.id", Long.parseLong(queryMap.get("meetId"))));
			}
		}
		return criteria;
	}
}
