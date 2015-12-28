
Dict = Ext.extend(Ext.app.BaseFuncTree,{
	initComponent : function(){
		Ext.apply(this,{
			winConfig : {
				height: 240,
				desc : '维护数据字典信息',
				bigIconClass : 'dictIcon'
			},
			formConfig:{
				items: [
					{xtype:'f-text',fieldLabel:'名称',name: 'text',emptyText:'请输入字典名称',allowBlank:false},
					{xtype:'f-textarea',fieldLabel:'描述',name: 'description'}
				]
			},
			rootConfig: { id:'0' },
			url:ctx+'/dict'
		});
		Dict.superclass.initComponent.call(this);
	}
	
});


