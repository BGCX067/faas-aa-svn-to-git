package net.fortunes.core.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.hsqldb.lib.StringUtil;

import net.fortunes.core.CachedValue;
import net.fortunes.core.log.annotation.LoggerMethod;
import net.fortunes.util.PinYin;

public abstract class GenericCacheService<E> extends GenericService<E> {
	
	public static String ALL_QUERY_KEY = "[allQuery]";
	
	private Map<String,CachedValue> cache;
	
	
	
	public Map<String, CachedValue> getCache(){
		if(cache == null){
			cache = new HashMap<String, CachedValue>();
			List<E> list = getAll();
			for(E e : list){
				addCache(e);
			}
			return cache;
		}else{
			return cache;
		}
	}
	
	public Map<String, CachedValue> search(String keyword){
		return search(keyword, null,true);
	}
	
	public Map<String, CachedValue> search(String keyword,boolean matchPinyin){
		return search(keyword, null,matchPinyin);
	}
	
	public Map<String, CachedValue> search(String keyword,String relativeId){
		return search(keyword, relativeId,true);
	}
	
	public Map<String, CachedValue> search(String keyword,String relativeId,boolean matchPinyin){
		//当查询关键字为ALL_QUERY_KEY或者""及relativeId为空时返回所有记录
		if((StringUtil.isEmpty(keyword)||keyword.equals(ALL_QUERY_KEY)) 
				&& StringUtils.isEmpty(relativeId)){
			return getCache();
		}
		
		Map<String,CachedValue> map = new HashMap<String, CachedValue>();
		
		for(Entry<String,CachedValue>  entry : getCache().entrySet()){
			//有相关id时
			if(StringUtils.isNotEmpty(relativeId)){
				//有相关id时及keyword为空时
				if(StringUtil.isEmpty(keyword)||keyword.equals(ALL_QUERY_KEY)){
					if(entry.getValue().getRelativeId().equalsIgnoreCase(relativeId)){
						map.put(entry.getKey(), entry.getValue());
					}
				//有相关id时且keyword不为空时
				}else{
					if(entry.getValue().match(keyword,matchPinyin) && entry.getValue().getRelativeId().equalsIgnoreCase(relativeId)){
						map.put(entry.getKey(), entry.getValue());
					}
				}
			}else{
				if(entry.getValue().match(keyword,matchPinyin)){
					map.put(entry.getKey(), entry.getValue());
				}
			}
		}
		return map;
	}
	
	@LoggerMethod(operateName = "新增")
	public E add(E entity) throws Exception{
		getDefDao().add(entity);
		addCache(entity);
		return entity;
	}
	
	@LoggerMethod(operateName = "删除")
	public void del(E entity) throws Exception{
		if(entity == null)
			return;
		removeCache(entity);
		getDefDao().del(entity);
	}
	
	@LoggerMethod(operateName = "修改")
	public E update(E entity){
		getDefDao().update(entity);
		updateCache(entity);
		return entity;
	}
	
	protected void addCache(E entity){
		String id = null;
		String name = null;
		String pinyin = null;
		String code = null;
		try {
			id = BeanUtils.getSimpleProperty(entity, "id");
			name = BeanUtils.getSimpleProperty(entity, "name");
			code = BeanUtils.getSimpleProperty(entity, "code");
			if(name == null){
				pinyin = "";
			}else{
				pinyin = PinYin.toPinYinString(name);
			}
		} catch( Exception e) {
			e.printStackTrace();
		} 
		getCache().put(id, new CachedValue(pinyin,name,code));
	}
	
	protected void removeCache(E entity){
		String id = null;
		try {
			id = BeanUtils.getSimpleProperty(entity, "id");
		} catch( Exception e) {
			e.printStackTrace();
		} 
		getCache().remove(id);
	}
	
	protected void updateCache(E entity){
		addCache(entity);
	}
	

}
