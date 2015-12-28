

ConfigForm = Ext.extend(Ext.form.FormPanel,{
	closable : true,
	border : false,
	monitorValid : true,
	autoScroll : true,
	initComponent : function(){
		
		Ext.apply(this,{
			layout: 'anchor',
			labelAlign: 'top',
			bodyStyle : 'padding: 10px;',
			defaults : {
				collapsed:true,
				bodyStyle : 'padding: 20px;'
			},
			items: [
				{xtype:'f-grouppanel',title :'系统参数',layout:'form',
					items :[
						{xtype: 'f-text',fieldLabel: '管理员邮箱',name: 'ADMIN_EMAIL',vtype:'email',allowBlank: false},
						{xtype: 'f-text',fieldLabel: '应用根目录',name: 'APP_ROOT_DIR',allowBlank: false},
						{xtype: 'f-text',fieldLabel: '临时上传文件夹',name: 'TEMP_UPLOAD_DIR',allowBlank: false}
					]
				},{xtype:'f-grouppanel',title :'业务参数',layout:'form',
					items :[
						{xtype: 'f-number',fieldLabel: '迟到超过（分钟）',name: 'LATE_GREATER_THAN_TIME',allowBlank: false},
						{xtype: 'f-number',fieldLabel: '算旷工（天）',name: 'LATE_GREATER_THAN_TIME_FOR_PARAM',allowBlank: false},
						{xtype: 'f-number',fieldLabel: '早退超过（分钟）',name: 'EARLY_GREATER_THAN_TIME',allowBlank: false},
						{xtype: 'f-number',fieldLabel: '旷工（天）',name: 'EARLY_GREATER_THAN_TIME_FOR_PARAM',allowBlank: false},
						{xtype: 'f-number',fieldLabel: '打卡一次算旷工（天）',name: 'ATTSHIFT_PUSH_CARD_ONLYONE',allowBlank: false}
					]
				},{xtype:'f-grouppanel',title :'薪酬参数',layout:'form',
					items :[
						{xtype: 'f-number',fieldLabel: '迟到早退扣基本工资标准',name: 'LATEOREARLY_DEDUCT_BASIC_SALARY_STANDARD',allowBlank: false},
						{xtype: 'f-number',fieldLabel: '旷工扣基本工资标准',name: 'ABSENT_DEDUCT_BASIC_SALARY_STANDARD',allowBlank: false}
					]
				},{xtype:'f-grouppanel',title :'工作到次日参数',layout:'form',
					items :[
					    {xtype: 'f-text',fieldLabel:'早上上班时间',name:'START_WORK_IN_MORNING_TIME',allowBlank:false},
					    {xtype: 'f-text',fieldLabel:'下午下班时间',name:'OVER_WORK_IN_AFTERNOON_TIME',allowBlank:false},
						{xtype: 'f-number',fieldLabel: '工作到次日最早打卡提前(小时)',name: 'WORK_TO_MORROW_EARLYEST_AHEAD',allowBlank: false},
						{xtype: 'f-number',fieldLabel: '工作到次日最迟打卡推迟到(小时)',name: 'WORK_TO_MORROW_LATEST_REMIT',allowBlank: false}
					]
				}
			],
			buttonAlign : 'center',
			buttons : [{
				text : '保存',
				scope : this,
				formBind: true,
				handler : this.updateConfig
			}]
		});
		ConfigForm.superclass.initComponent.call(this);
	},
	loadData : function(){
		this.getForm().load({
			url : ctx + '/config/loadConfig'
		})
	},
	updateConfig : function(){
		Ext.MessageBox.confirm('保存确认', '你确定要保存你的修改吗?错误的参数设置可能导致系统异常行为',function(btn){
			var o = {};
			this.getForm().items.each(function(f){
	           if(f.isDirty()){
	           		o[f.getName()] = f.getValue();
	           }
	        });
			Ext.Ajax.request({
				url : ctx + '/config/updateConfig',
				params :　o,
				scope : this,
				success : function(){
					this.loadData();
				}
			})
		},this);
	}
	
});

Config = Ext.extend(Ext.Panel,{
	layout : 'anchor',
	border : true,
	closable : true,
	initComponent : function(){
		this.items = [
			{	xtype: 'panel',
				height : 60,
				border : false,
				baseCls : 'fjdp-win-title',
				html : '<div class="fjdp-win-title-content confIcon"><h3>系统及业务参数设置</h3><p>设置系统的各项参数,某些参数必须在服务器重启后才能生效</p></div>'
			},
			new ConfigForm({id : 'configForm',anchor : '0 -60'})
			
		]
		Config.superclass.initComponent.call(this);
	},
	loadData : function(){
		Ext.getCmp('configForm').loadData();
	}
});
