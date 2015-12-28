package com.fortunes.fjdp.admin.action;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.hibernate.tool.hbm2x.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fortunes.fjdp.admin.model.Config;
import com.fortunes.fjdp.admin.model.Config.ConfigKey;
import com.fortunes.fjdp.admin.service.ConfigService;

import net.fortunes.core.action.BaseAction;
import net.sf.json.JSONObject;

@Component @Scope("prototype")
public class ConfigAction extends BaseAction {
	
	@Resource private ConfigService configService; 
		
	public String updateConfig() throws Exception{
		Map<ConfigKey, String> maps = new EnumMap<ConfigKey, String>(ConfigKey.class);
		for(ConfigKey configKey : ConfigKey.values()){
			if(StringUtils.isNotEmpty(p(configKey.name()))){
				maps.put(configKey, p(configKey.name()));
			}
		}
		configService.updateConfigs(maps);
		setJsonMessage(true, "系统参数成功更新!");
		return render(jo);
	}
	
	public String loadConfig() throws Exception{
		List<Config> configs = configService.getAll();
		JSONObject data = new JSONObject();
		for(Config config : configs){
			data.put(config.getConfigKey().name(), config.getConfigValue());
		}
		setJsonMessage(true, data);
		return render(jo);
	}
	
	public String restoreLastConfig(){
		return null;
	}
	
	public String restoreDefaultConfig() {
		return null;
	}
	

	//================== setter and getter ===================
	
	

}
