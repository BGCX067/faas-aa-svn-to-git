Man = Ext.extend(Ext.Panel, {
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
				{xtype:'f-grouppanel',title :'考勤计算',layout:'form',id:'attshiftDayComputePanel',
					items :[
						{xtype: 'f-date',fieldLabel: '日期',id: 'Man_Date',allowBlank: false},
						{xtype: 'treeField',fieldLabel: '部门',listHeight:240,hiddenName: 'organization',id:'Man_organization',dataUrl : ctx+'/organization/getOrganizationTree',readOnly:false},
						{xtype: 'f-select',fieldLabel: '员工',relativeField:'Man_organization',id:'Man_employee',dataUrl:'/employee/getEmployeeByOrganization'},
						{xtype:'f-button',text:'执行',handler:this.attShiftDayComputeByNode}
					]
				},{xtype:'f-grouppanel',title :'同步门禁数据',layout:'form',
					items :[
						{xtype: 'f-date',fieldLabel: '计算日期',id: 'Man_syncDate',allowBlank: false},
						{xtype:'f-button',text:'执行',handler:this.syncLogsInSystem}
					]
				},{xtype:'f-grouppanel',title :'清除网点应用排版模版',layout:'form',
					items :[
					    {xtype : 'f-year',fieldLabel:'年',id : 'Man_Year'},
					    {xtype : 'f-month',fieldLabel:'月',id : 'Man_Month'},
					    {xtype : 'treeField',fieldLabel: '部门',listHeight:240,hiddenName: 'Branch_organization',id:'Man_Branch',dataUrl : ctx+'/organization/getOrganizationTree',readOnly:false},
						{xtype : 'f-button',text:'执行',handler:this.askUser,scope:this}
					]
				},{xtype:'f-grouppanel',title:'年假计算',layout:'form',id:'annualCompute',
					items:[
						     {xtype:'treeField',fieldLabel:'部门',ListHeight:240,hiddenName:'Man_organization',id:'Man_organization_annual',dataUrl:ctx+'/organization/getOrganizationTree',readOnly:false},
						     {xtype:'f-select',fieldLabel:'员工',relativeField:'Man_organization_annual',id:'Man_employee_annual',dataUrl:'/employee/getEmployeeByOrganization'},
						     {xtype:'f-button',text:'执行',handler:this.annualComputer}
						]
				},{xtype:'f-grouppanel',title:'考勤机问题',layout:'form',id:'deviceTrouble',
					items:[
					       {xtype:'f-text',width:1000,text:'如果出现大批量考勤机在工作中，则可以点击下面按钮，然后重启考勤机，即可......',readOnly:true},
					       {xtype:'f-button',text:'执行',handler:this.deviceManager}
					]
				},{xtype:'f-grouppanel',title:'工作到次日考勤计算',layout:'form',id:'workingNextDay',
					items:[
					       {xtype:'f-date',fieldLabel:'日期',id:'Man_startComputerDate',allowBlank:false},
					       {xtype:'f-button',text:'执行',handler:this.computerWorknextDay}
					]
				},{xtype:'f-grouppanel',title:'清除员工可操作部门',layout:'form',id:'clearEmployeeOperated',
					items:[
					       {xtype: 'treeField',fieldLabel: '部门',listHeight:240,hiddenName: 'organization',id:'Man_clearOrganization',dataUrl : ctx+'/organization/getOrganizationTree',readOnly:false},
					       {xtype: 'f-select',fieldLabel: '员工',relativeField:'Man_clearOrganization',id:'Man_clearEmployee',dataUrl:'/employee/getEmployeeByOrganization'},
					       {xtype:'f-button',text:'执行',tooltip:'清除员工的可操作部门',handler:this.clearEmployeeOperatedOrganization}
					]
				},{
					xtype : 'f-grouppanel',
					title : '清除员工原来部门的排班',
					layout : 'form',
					items : [
						{
								xtype : 'f-year',
								fieldLabel : '年',
								id : 'Man_OldYear'
						}, {
								xtype : 'f-month',
								fieldLabel : '月',
								id : 'Man_OldMonth'
						}, {
								xtype : 'treeField',
								fieldLabel : '部门',
								listHeight : 240,
								hiddenName : 'Branch_Oldorganization',
								id : 'Man_OldBranch',
								dataUrl : ctx+ '/organization/getOrganizationTree',
								readOnly : false
						}, {
								xtype : 'f-select',
								fieldLabel : '员工',
								relativeField : 'Man_OldBranch',
								id : 'Man_OldOrgEmployee',
								dataUrl : '/employee/getEmployeeByOrganization'
						}, {
								xtype : 'f-button',
								text : '执行',
								handler : this.ClearSchedulTemplate,
								scope : this

						}]
				}]
		});
		Man.superclass.initComponent.call(this);
	},
	attShiftDayComputeByNode:function(){
		var startDate = Ext.getCmp('Man_Date').getValue();
		var organization = Ext.getCmp('Man_organization').getValue();
		var employee = Ext.getCmp('Man_employee').getValue();
		Ext.Ajax.request({
			url:ctx+'/console/attShiftDayComputeByNode',
			scope:this,
			params:{
				date:startDate,
				organization:organization,
				employee:employee
			},
			success:function(form,action){
			}
		});
	},
	syncLogsInSystem:function(){
		var date = Ext.getCmp('Man_syncDate').getValue();
		Ext.Ajax.request({
			url:ctx+'/console/syncLogs',
			scope:this,
			params:{
				date:date
			},
			success:function(form,action){
			}
		});
	},
	askUser:function(){
		Ext.MessageBox.confirm('清楚排版模版','您确定要清楚该网点该月的排版模版吗？如果清楚，则会将该月的该网点的所有排版全部清楚!',this.clearBranchTemplete,this);
	},
	clearBranchTemplete:function(btn){
		if(btn=="yes"){
			var year = Ext.getCmp('Man_Year').getValue();
			var month = Ext.getCmp('Man_Month').getValue();
			var organization = Ext.getCmp('Man_Branch').getValue();
			Ext.Ajax.request({
				url:ctx+'/console/clearBranchTemplete',
				scope:this,
				params:{
					year:year,
					month:month,
					organization:organization
				},
				success:function(form,action){
				}
			});
		}
	},
	annualComputer:function(){
		var organization = Ext.getCmp('Man_organization_annual').getValue();
		var employee = Ext.getCmp('Man_employee_annual').getValue();
		Ext.Ajax.request({
			url:ctx+'/employee/annualLeave',
			scope:this,
			params:{
				organization:organization,
				employee:employee
			},
			success:function(form,action){
				
			}
		});
	},
	deviceManager:function(){
		Ext.Ajax.request({
			url:ctx+'/console/deviceClear',
			scope:this,
			success:function(form,action){
			App.msg('成功');
		}
		});
	},
	computerWorknextDay:function(){
		var startDate = Ext.getCmp('Man_startComputerDate').getValue();
		Ext.Ajax.request({
			url:ctx+'/attShift/computerShiftConfigNextDay',
			scope:this,
			params:{
				startDate:startDate
			},
			success:function(form,action){
				App.msg('成功');
			}
		});
	},
	clearEmployeeOperatedOrganization:function(){
		var organization = Ext.getCmp('Man_clearOrganization').getValue();
		var employee = Ext.getCmp('Man_clearEmployee').getValue();
		Ext.Ajax.request({
			url:ctx+'/console/clearEmployeeoperatedOrganization',
			scope:this,
			params:{
				organization:organization,
				employee:employee
			},
			success:function(form,action){
			}
		});
	},
	ClearSchedulTemplate : function() {
		Ext.MessageBox.confirm('清除排班模版', '您确定要清楚该员工原部门该月的排班模版吗？',
				this.clearOldBranchTemplete, this);
	},
	clearOldBranchTemplete : function(btn) {
		if (btn == "yes") {
			var year = Ext.getCmp('Man_OldYear').getValue();
			var month = Ext.getCmp('Man_OldMonth').getValue();
			var organization = Ext.getCmp('Man_OldBranch').getValue();
			var employee = Ext.getCmp('Man_OldOrgEmployee').getValue();

			Ext.Ajax.request({
						url : ctx + '/console/clearOldBranchTemplete',
						scope : this,
						params : {
							year : year,
							month : month,
							organization : organization,
							employee : employee
						},
						success : function(form, action) {
						}
					});
		}
	}

});