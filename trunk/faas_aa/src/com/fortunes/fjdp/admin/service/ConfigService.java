package com.fortunes.fjdp.admin.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.fortunes.fjdp.admin.model.Config;
import com.fortunes.fjdp.admin.model.Config.ConfigKey;

import net.fortunes.core.service.GenericService;

@Component
public class ConfigService extends GenericService<Config> {
	
	private Map<ConfigKey, String> cache;
	
	public void updateConfigs(Map<ConfigKey, String> maps) {
		
		for(Map.Entry<ConfigKey, String>  map: maps.entrySet()){
			Config config = getConfigBykey(map.getKey());
			
			config.setLastValue(config.getConfigValue());
			config.setConfigValue(map.getValue());
			getDefDao().update(config);
		}
	}
	
	public void initConfigs(Map<ConfigKey, String> maps){
		for(Map.Entry<ConfigKey, String>  map: maps.entrySet()){
			
			//初始化一个设置项,将现有值,上个值,缺省值都设为相同的
			Config config =  new Config(map.getKey(),map.getValue(),map.getValue(),map.getValue());
			getDefDao().add(config);
		}
	}
	
	public Config getConfigBykey(ConfigKey key) {
		return findUniqueByProperty("configKey", key);
	}
	
	
	/**
	 * 从缓存中得到设置的值,如果缓存还没有生成,则生成缓存再得到设置的值
	 * @param key 设置的key
	 * @return 设置的值
	 */
	public String getConfigValueFromCache(ConfigKey key){
		if(cache == null){
			cache = new HashMap<ConfigKey, String>();
			for(Config c : this.getAll()){
				cache.put(c.getConfigKey(), c.getConfigValue());
			}
		}
		return cache.get(key);
	}
	
	public int getForInt(ConfigKey key){
		return Integer.parseInt(getConfigValueFromCache(key));
	}
	
	public double getForDouble(ConfigKey key){
		return Double.parseDouble(getConfigValueFromCache(key));
	}
	
	public String get(ConfigKey key){
		return getConfigValueFromCache(key);
	}
	
	
	//======================== setter and getter ========================

	
}
