
${modelName} = Ext.extend(Ext.app.BaseFuncPanel,{
	initComponent : function(){
		Ext.apply(this,{
			gridConfig:{
				cm:new Ext.grid.ColumnModel([
					new Ext.grid.RowNumberer(),
				<#list fields as field>
					{header: '${field.label}',dataIndex:'${field.name}'<#if field.type == "dict">,renderer:dictRenderer</#if>}<#if field_has_next>,</#if>
				</#list> 
				]),	
				storeMapping:[
					<#list fields as field>'${field.name}'<#if field_has_next>,</#if></#list>
				]
			},
			winConfig : {
				height: 330
			},
			formConfig:{
				items: [
				<#list fields as field>
					{xtype: 'f-<#if field.type == 'int' || field.type == 'double'>number<#else>${field.type?lower_case}</#if>',fieldLabel: '${field.label}',<#if field.type == "dict">hiddenName<#else>name</#if>: '${field.name}'<#if field.type == "dict">,kind : '${field.extend}'</#if><#if !field.allowEmpty>,allowBlank: false</#if>}<#if field_has_next>,</#if>
				</#list> 
 
				]
			},
			url:ctx+'/${modelName?uncap_first}'	
		});
		${modelName}.superclass.initComponent.call(this);
	}
	
});
