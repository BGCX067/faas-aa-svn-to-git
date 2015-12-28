package ${packagePrefix}.action;

import java.text.ParseException;
import java.util.List;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import com.fortunes.fjdp.AppHelper;
import com.fortunes.fjdp.admin.AdminHelper;
import net.fortunes.core.action.GenericAction;
import net.fortunes.core.service.GenericService;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ${packagePrefix}.model.${modelName};
import ${packagePrefix}.service.${modelName}Service;

@Component @Scope("prototype")
public class ${modelName}Action extends GenericAction<${modelName}> {
	
	private ${modelName}Service ${modelName?uncap_first}Service;
	
	protected void setEntity(${modelName} e) throws ParseException{
<#list fields as field>
	<#if field.type == "text" || field.type == "textArea">
		e.set${field.name?cap_first}(p("${field.name}"));
	<#elseif field.type == "int">
		e.set${field.name?cap_first}(Integer.parseInt(p("${field.name}")));
	<#elseif field.type == "double">
		e.set${field.name?cap_first}(Double.parseDouble(p("${field.name}")));
	<#elseif field.type == "dict">
		e.set${field.name?cap_first}(AppHelper.toDict(p("${field.name}")));
	<#elseif field.type == "date">
		e.set${field.name?cap_first}(AppHelper.toDate(p("${field.name}")));
	</#if>
</#list> 
	}
	
	protected JSONObject toJsonObject(${modelName} e) throws ParseException{
		AdminHelper record = new AdminHelper();
		record.put("id", e.getId());
<#list fields as field>
		record.put("${field.name}", e.get${field.name?cap_first}());
</#list> 		
		return record.getJsonObject();
	}
	
	
	/*=============== setter and getter =================*/
	
	@Override
	public GenericService<${modelName}> getDefService() {
		return ${modelName?uncap_first}Service;
	}
	
	public void set${modelName}Service(${modelName}Service ${modelName?uncap_first}Service) {
		this.${modelName?uncap_first}Service = ${modelName?uncap_first}Service;
	}

	public ${modelName}Service get${modelName}Service() {
		return ${modelName?uncap_first}Service;
	}

}
