OrganizationWin = Ext.extend(Ext.Window,{
	width:500,
	height:400,
	title:'组织机构',
	iconCls:'config',
	cls:'ConfigTemplateWin',
	layout:'border',
	initComponent : function(){
		this.items = [{
	        	xtype:'treepanel',
	        	id:'OrganizationWin_OrganizationTree',
				region:'east',
				title:'部门列表',
	        	split:true,
	        	width:232,
	        	margins:'3 0 0 0',
	        	autoScroll: true,
	        	enableDD:true,
	        	containerScroll : true,
		        loader: new Ext.tree.TreeLoader({
		        	dataUrl:ctx+'/organization/getOrganizationByRole'
		        }),
		        root: new Ext.tree.AsyncTreeNode({
		            id:'0'
		        }),
		        rootVisible: false
	        },{
	        	xtype:'treepanel',
	        	id:'OrganizationWin_EmployeeTree',
	        	region:'center',
	        	title:'人员列表',
	        	margins:'3 0 0 3',
	        	width:160,
	        	autoScroll: true,
	        	enableDD:true,
	        	containerScroll : true,
	        	dragConfig : {
	        		ddGroup:'shiftConfig'
	        	},
	        	dropConfig: {
	        		appendOnly:true,
	        		ddGroup:'shiftConfig',
	        		allowContainerDrop:true
	        	},
	        	tbar : [
	        	    {xtype:'f-select',id:'search_type',name:'select',fieldLabel:'查询',value:'BRANCH',
        	    	data:[
        	    	    ['BRANCH','部门当天未排班'],
        	    	    ['ALL_BANK','所有当天没有排班人员'],
        	    	    ['LOW_THREE_DAYS','所有本月周末排班不足3天']
        	    	],
        	    	listeners:{
	        	    	scope : this,
	        	    	select : function(index,record){
	        	    		var date = Ext.getCmp('branchDatePicker').getValue().format('Y-m-d');
		        	    	Ext.getCmp('OrganizationWin_EmployeeTree').getLoader().baseParams = 
		    				{ organizationId : Ext.getCmp('OrganizationWin_EmployeeTree').organizationId,date:date,selectType:record.data.id};
	        	    		Ext.getCmp('OrganizationWin_EmployeeTree').getLoader().load(Ext.getCmp('OrganizationWin_EmployeeTree').root);
	        	    	}
	        	    }
	        	    }
	        	],
		        loader: new Ext.tree.TreeLoader({
		        	dataUrl:ctx+'/workingArea/listUnShiftEmployees'
		        }),
		        root: new Ext.tree.AsyncTreeNode({id:'0'})
	        }];
	
		OrganizationWin.superclass.initComponent.call(this);
		Ext.getCmp('OrganizationWin_EmployeeTree').getLoader().on('beforeload',function(node){
			if(!Ext.getCmp('OrganizationWin_EmployeeTree').organizationId){
				return false;
			}
		},this);
		Ext.getCmp('OrganizationWin_OrganizationTree').on('click',function(e){
			var type=Ext.getCmp('search_type').value;
			Ext.getCmp('OrganizationWin_EmployeeTree').organizationId = e.id;
			var date = Ext.getCmp('branchDatePicker').getValue().format('Y-m-d');
			Ext.getCmp('OrganizationWin_EmployeeTree').getLoader().baseParams = 
				{ organizationId : Ext.getCmp('OrganizationWin_EmployeeTree').organizationId,date:date,selectType:type};
			Ext.getCmp('OrganizationWin_EmployeeTree').getLoader().load(Ext.getCmp('OrganizationWin_EmployeeTree').root);
		});		
	}
});

ConfigTemplateWin = Ext.extend(Ext.Window,{
	width:600,
	height:400,
	title:'排班模板',
	iconCls:'config',
	modal:true,
	cls:'ConfigTemplateWin',
	layout:'border',
	initComponent : function(){
		this.items = [{
			xtype:'tabpanel',
			id:'weeksTabPanel',
			activeItem:0,
			height:60,
			region:'north',
			items:[{
				title:'一',dayInWeek:1,
				html:'<div class="selectTips">'+loginUser.organizationName+'-星期一</div>'
			},{
				title:'二',dayInWeek:2,
				html:'<div class="selectTips">'+loginUser.organizationName+'-星期二</div>'
			},{
				title:'三',dayInWeek:3,
				html:'<div class="selectTips">'+loginUser.organizationName+'-星期三</div>'
			},{
				title:'四',dayInWeek:4,
				html:'<div class="selectTips">'+loginUser.organizationName+'-星期四</div>'
			},{
				title:'五',dayInWeek:5,
				html:'<div class="selectTips">'+loginUser.organizationName+'-星期五</div>'
			},{
				title:'六',dayInWeek:6,
				html:'<div class="selectTips">'+loginUser.organizationName+'-星期六</div>'
			},{
				title:'日',dayInWeek:7,
				html:'<div class="selectTips">'+loginUser.organizationName+'-星期日</div>'
			}]
		},{
			region:'center',
			layout:'border',
			border:false,
			items:[new Ext.app.BaseFuncPanel({
				id:'ShiftConfigBundleGrid',
				title:'模板列表',
				region:'west',
				margins:'3 3 0 0',
				width:160,
				paging:false,
				gridConfig:{
					cm:new Ext.grid.ColumnModel([
						{header: '模板名称',dataIndex:'name'}
					]),	
					storeMapping:[
						'id','name'
					]
				},
				//buttonConfig : ['add','edit'],
				winConfig : {
					height: 330
				},
				formConfig:{
					items: [
						{xtype: 'f-text',fieldLabel: '模板名称',name: 'name',allowBlank: false}
					]
				},
				url:ctx+'/shiftConfigBundle'	
			}),{
	        	xtype:'treepanel',
	        	id:'ConifgTemplatesTree',
				region:'center',
				title:'排班情况',
	        	split:true,
	        	margins:'3 0 0 0',
	        	autoScroll: true,
	        	enableDD:true,
	        	containerScroll : true,
	        	tbar:[{
        			xtype : 'f-button',
        			id : 'copyShiftconfigButton',
        			text : '复制',
        			iconCls:'copy',
        			tooltip:'复制当天的排班',
        			scope : this,
        			handler : this.copyCurrentDayShiftconfigTemplate
        		},'-',{
        			xtype : 'f-button',
        			id : 'pasteshiftconfigButton',
        			text : '粘贴',
        			iconCls:'paste',
        			tooltip:'把复制的排班粘贴到该天的排版',
        			scope : this,
        			handler : this.pasteShiftconfigTemplateTocurrentDay
        		},'-',{
				xtype:'f-button',
				id:'deleteShiftConfigTemplateButton',
				scope:this,
				text:'清除',
				iconCls:'remove',
				tooltip:'清除当天所有人的排班',
//				privilegeCode: this.funcCode + '_delAll',
				handler:this.delAllShiftConfigTemplate
			}],
	        	dragConfig : {
	        		ddGroup:'configTemplate'
	        	},
	        	dropConfig: {
	        		ddGroup:'configTemplate',	
	        		appendOnly:true
	        	},
		        loader: new Ext.tree.TreeLoader({
		        	dataUrl:ctx+'/workingArea/getConifgTemplates'
		        }),
		        root: new Ext.tree.AsyncTreeNode({
		            id:'0'
		        }),
		        rootVisible: false
	        },{
	        	xtype:'treepanel',
	        	id:'availableEmployeesInTemplateTree',
	        	region:'east',
	        	title:'该部门可用人员',
	        	margins:'3 0 0 3',
	        	width:160,
	        	autoScroll: true,
	        	enableDD:true,
	        	containerScroll : true,
	        	dragConfig : {
	        		ddGroup:'configTemplate'
	        	},
	        	dropConfig: {
	        		appendOnly:true,
	        		ddGroup:'configTemplate',
	        		allowContainerDrop:true
	        	},
		        loader: new Ext.tree.TreeLoader({
		        	dataUrl:ctx+'/workingArea/availableEmployeesInTemplate'
		        }),
		        root: new Ext.tree.AsyncTreeNode({
		            id:'0'
		        }),
		        rootVisible: false
	        }]
		}];
		
		ConfigTemplateWin.superclass.initComponent.call(this);
		
		Ext.getCmp('weeksTabPanel').on('tabchange',function(){
			var tab = Ext.getCmp('weeksTabPanel').layout.activeItem;
			var day  = tab.dayInWeek;
			Ext.getCmp('ShiftConfigBundleGrid').selectedDay = day;
			Ext.getCmp('ShiftConfigBundleGrid').loadData({selectedDay : day});
			
		},this);
		
		Ext.getCmp('ShiftConfigBundleGrid').on('beforesave',function(){
			this.ajaxParams.selectedDay = this.selectedDay;
		});
		
		//阻止树自动加载
		Ext.getCmp('ConifgTemplatesTree').getLoader().on('beforeload',function(node){
			if(!Ext.getCmp('ConifgTemplatesTree').bundleId){
				return false;
			}
		},this);
		
		
		Ext.getCmp('availableEmployeesInTemplateTree').getLoader().on('beforeload',function(node){
			if(!Ext.getCmp('availableEmployeesInTemplateTree').bundleId){
				return false;
			}
		},this);
		//选中模板时加载排班树和可用员工树
		Ext.getCmp('ShiftConfigBundleGrid').getSelectionModel().on('rowselect',function(sm,rowIndex,record){
			Ext.getCmp('ConifgTemplatesTree').bundleId = record.id;
			Ext.getCmp('ConifgTemplatesTree').loadRoot({bundleId : record.id});
			
			Ext.getCmp('availableEmployeesInTemplateTree').bundleId = record.id;
			Ext.getCmp('availableEmployeesInTemplateTree').loadRoot({bundleId : record.id});
		});
		//排版模版中的时间设置
		Ext.getCmp('ConifgTemplatesTree').on('contextmenu', function (node, event) {
			event.preventDefault();  //屏蔽默认右键菜单
          	node.select();
          
      		if(node.attributes.iconCls =='userman'||node.attributes.iconCls =="employee_female"){
    			var employeeId = node.attributes.id;
    			
    			var bundleId = this.bundleId;
      			this.setTemplementTimeWin = new Ext.app.FormWindow({
      				winConfig : {
      					height : 230,
      					width : 270,
      					title : '设置时间',
      					desc : '设置指定员工上下班时间'
      				},
      				formConfig : {
      					items : [{
      						xtype:'timefield',
      	            		fieldLabel:'上班时间',
//      	            		value:'08:30',
      	            		format:'H:i',
      	            		increment:30,
      	            		id:'Templement-startTime',
      	            		width:100,
      	            		name:'startTime'
      					},{
      						xtype:'f-number',
      						fieldLabel:'午休小时',
      		          		id:'Templement-bunchBreakTime',
      		          		width:100,
//      		          	    value:'1',
      		          		name:'bunchBreakTime'
      					},{
      						xtype:'timefield',
      	            		fieldLabel:'下班时间',
      	            		id:'Templement-endTime',
//      	            		value:'17:30',
      	            		format:'H:i',
      	            		increment:30,
      	            		width:100,
      	            		name:'endTime'
      					}]
      				},
      				buttons : [{     
      					text: '确定',
      					scope:this,
      					handler : function(){
      						this.setTemplementTimeWin.formPanel.getForm().submit({           
      				            waitMsg:'保存中...',
      							url:ctx+'/workingArea/changeTemplateTime?employeeId='+employeeId+'&bundleId='+bundleId,
      							scope:this,
      							success:function(form, action) {
      								this.setTemplementTimeWin.close();
      				            }
      				        });
      					}
      				}]
      			});
      			
      		//我添加的东西
      	    	this.setTemplementTimeWin.on('render',function(){
      				Ext.Ajax.request({
      					url :ctx+'/workingArea/getTemplateTime?employeeId='+employeeId+'&bundleId='+bundleId,
      					success:function(form,action){
      						var data=Ext.util.JSON.decode(form.responseText);
      						Ext.getCmp('Templement-startTime').setValue(data.startTime);
      						Ext.getCmp('Templement-endTime').setValue(data.endTime);
      						Ext.getCmp('Templement-bunchBreakTime').setValue(data.bunchBreakTime);
      					}
      				});
      			});
      			this.setTemplementTimeWin.show();
          		return;
          	}
          	var childNodes = node.childNodes;
          	if(childNodes.length>0&&childNodes[0].attributes.iconCls =='workingArea'){
          		return;
          	}
		});
		//========= end =========
		
		
		
		
		//模板拖放设置
		Ext.getCmp('ConifgTemplatesTree').on('nodedrop',function(e){
			var areaId = e.target.id;
			var employeeId = e.data.node.id;
			Ext.Ajax.request({
				url :ctx+'/workingArea/appendEmployeeTemplate',
				params:{
					areaId:areaId, 
					employeeId:employeeId,
//					startTime:'08:30',
//					endTime:'17:30',
					bundleId : this.bundleId
				}
			});
			
		});
		
		Ext.getCmp('availableEmployeesInTemplateTree').on('nodedrop',function(e){
			var areaId = e.target.id;
			var employeeId = e.data.node.id;
			Ext.Ajax.request({
				url :ctx+'/workingArea/removeEmployeeTemplate',
				params:{
					employeeId : employeeId,
					bundleId : this.bundleId
				}
			});
		});
		
		
	},
	copyCurrentDayShiftconfigTemplate:function(){
		var tab = Ext.getCmp('weeksTabPanel').layout.activeItem;
		var dayInWeek  = tab.dayInWeek;
		var week = tab.title;
		var bundleId=Ext.getCmp('ShiftConfigBundleGrid').selectedId;
		if(''==dayInWeek){
			App.msg('请选择某一天的某一个模板，不能为空');
		}
		this.copyTemplate = {dayInWeek:dayInWeek,week:week,bundleId:bundleId};
		App.msg('成功复制了星期'+week+'的排班模板');
					
	},
	pasteShiftconfigTemplateTocurrentDay:function(){
		var currentDayInWeek = Ext.getCmp('weeksTabPanel').layout.activeItem.dayInWeek;
		var currentWeek = Ext.getCmp('weeksTabPanel').layout.activeItem.title;
		Ext.Msg.confirm("温馨提示","确定要星期"+this.copyTemplate.week+"的排班模板复制到星期"+currentWeek,function(btn){
			if(btn=='yes'){
				var dayInWeek = this.copyTemplate.dayInWeek;
				var bundleId = this.copyTemplate.bundleId;
				Ext.Ajax.request({
					url :ctx+'/workingArea/pasteShiftConfigTemplateToCurrentDay',
					scope:this,
					params:{
						currentDayInWeek :currentDayInWeek,
						dayInWeek:dayInWeek,
						bundleId:bundleId
					},
					success:function(form,action){
						var sa = Ext.getCmp('ConifgTemplatesTree');
						sa.loadRoot({bundleId : bundleId});
						var ss = Ext.getCmp('availableEmployeesTree');
						Ext.getCmp('availableEmployeesInTemplateTree').loadRoot({bundleId : bundleId});
						App.msg('星期'+this.copyTemplate.week+'的排班成功粘贴到星期'+currentWeek);
					}
				});
			}
		},this);
	},
	delAllShiftConfigTemplate:function(){
		var currentDayInWeek = Ext.getCmp('weeksTabPanel').layout.activeItem.dayInWeek;
		var bundleId = Ext.getCmp('ShiftConfigBundleGrid').selectedId;
		Ext.Msg.confirm("温馨提示","您确定要清除模板中每个星期本天的排版吗？",function(btn){
			if(btn=='yes'){
				Ext.Ajax.request({
					url :ctx+'/workingArea/removeAllTemplateEmployee',
					scope:this,
					params:{
						currentDayInWeek :currentDayInWeek,
						bundleId : bundleId
					},
					success:function(form,action){
						var sa = Ext.getCmp('ConifgTemplatesTree');
						sa.loadRoot({bundleId : bundleId});
						var ss = Ext.getCmp('availableEmployeesTree');
						Ext.getCmp('availableEmployeesInTemplateTree').loadRoot({bundleId : bundleId});
						App.msg('成功清除排班！');
					}
				});
			}
		});
	}
});

WorkingAreaConfigTree = Ext.extend(Ext.app.BaseFuncTree,{
	border:false,
	initComponent : function(){
		Ext.apply(this,{
			winConfig : {
				height: 200,
				title:'工作区域管理',
				desc : '维护工作区域信息',
				bigIconClass : 'dictIcon'
			},
			formConfig:{
				items: [
					{xtype:'f-text',fieldLabel:'名称',name: 'text',emptyText:'请输入工作区域名称',allowBlank:false}
				]
			},
			rootConfig: {id:'0' },
			url:ctx+'/workingArea'
		});
		WorkingAreaConfigTree.superclass.initComponent.call(this);
		
		this.on('winshow',function(grid){
			/*if(this.saveType == 'update'){
				Ext.getCmp('workingAreaTypeField').setReadOnly(true);
			}*/
		},this);
	}
});

BranchCalendarPanel = Ext.extend(Ext.Panel,{
	layout:'anchor',
	initComponent : function(){
		
		Ext.apply(this,{
			bbar:[{
				text:'设置排班模板',			
				iconCls:'config',
				scope:this,
				handler:this.configTemplate
			
			},{
				text:'设置工作区域',			
				iconCls:'config',
				scope:this,
				handler:this.configWorkingArea
			
			}]
		});
		this.items = [{
        	xtype:'datepicker',
        	id:'branchDatePicker',
        	cls:'branchDatePicker',
        	border:false,
        	startDay:1,
        	showToday:false,
        	format:'Y-m-d'
        },{
        	xtype:'panel',
        	border:false,
        	cls:'branchStatusPanel',
        	anchor:'100% -177',
        	items:[{
        		xtype : 'button',
        		id : 'applyTemplateButton',
        		text : '应用排班模板',
        		style : 'margin-left:50px;margin-top:20px;',
        		scope : this,
        		handler : this.applyTemplate
        	},{
        		xtype : 'f-button',
        		id : 'exportConfigStatButton',
        		text : '导出本月排班情况表',
        		style : 'margin-left:30px;margin-top:20px;',
        		scope : this,
        		handler : this.exportConfigStat
        	},{
        		xtype : 'f-button',
        		id : 'exportConfigAllBankStatButton',
        		privilegeCode:this.funcCode+'_export',
        		text : '导出全行本月排班情况表',
        		style : 'margin-left:30px;margin-top:20px;',
        		scope : this,
        		handler : this.exportConfigAllStat
        	},{
        		xtype:'f-button',
        		id : 'defaultTime',
        		privilegeCode:this.funcCode+'_defaultTime',
        		text:'设置默认排班时间',
        		scope:this,
        		style : 'margin-left:30px;margin-top:20px;',
        		handler : this.setDefaultTime
        	}]
        }];
		BranchCalendarPanel.superclass.initComponent.call(this);
		
		Ext.getCmp('branchDatePicker').on('select',function(picker,date){
			Ext.getCmp('BranchShiftTreePanel').loadByDate(date);
			this.checkTemplateApplyStatus();
		},this);
		
		//应用模板相关
		this.on('render',this.checkTemplateApplyStatus,this);
	},
	checkTemplateApplyStatus:function(){
		Ext.Ajax.request({
			url : ctx + '/workingArea/checkTemplateApplyStatus',
			params : {
				yearMonth :  Ext.getCmp('branchDatePicker').getValue().format('Y-m')
			},
			success : function(response,options){
				var jo = Ext.decode(response.responseText);
		/////////////////2012-10-23///////////////////////////////////////		
				 Ext.getCmp('applyTemplateButton').setDisabled(false);
				Ext.getCmp('exportConfigStatButton').setDisabled(false); 
		////////////////////////////////////////////////////////		
//				if(jo.applyed){
//					Ext.getCmp('applyTemplateButton').setDisabled(true);
//					Ext.getCmp('exportConfigStatButton').setDisabled(false);
//				}else{
//					Ext.getCmp('applyTemplateButton').setDisabled(false);
//					Ext.getCmp('exportConfigStatButton').setDisabled(true);
//				}
			}
		});
	},
	configTemplate:function(){
		var win = new ConfigTemplateWin();
		win.show();
	},
	applyTemplate:function(){
		App.msg("操作成功，此操作可能需要一段时间，请耐心等待");
		Ext.Ajax.request({
			url : ctx + '/workingArea/applyTemplate',
			params : {
				yearMonth :  Ext.getCmp('branchDatePicker').getValue().format('Y-m')
			},
			scope : this,
			success : function(response,options){
				this.checkTemplateApplyStatus();
			}
		});
	},
	exportConfigStat:function(){
		/*Ext.Ajax.request({
			url : ctx + '/workingArea/exportConfigStat',
			params : {
				yearMonth :  Ext.getCmp('branchDatePicker').getValue().format('Y-m')
			},
			scope : this,
			success : function(response,options){
				
			}
		});*/
		document.location.href = ctx + '/workingArea/exportConfigStat?yearMonth='+Ext.getCmp('branchDatePicker').getValue().format('Y-m');
	},
	exportConfigAllStat:function(){
		document.location.href = ctx + '/workingArea/exportConfigAllStat?yearMonth='+Ext.getCmp('branchDatePicker').getValue().format('Y-m');
	},
	configWorkingArea:function(){
		var win = new Ext.Window({
			id:'workTreeWindow',
			layout:'fit',
			title:'工作区域管理',
			iconCls:'config',
			modal:true,
			width:440,
			height:300,
			items:new WorkingAreaConfigTree()
		});
		win.show();
		win.on('beforeclose',function(p){
			Ext.getCmp('workingAreaTree').loadRoot();
		});
	},
	setDefaultTime:function(){
		var as = 'as';
		this.win = new Ext.app.FormWindow({
			winConfig : {
				height : 220,
				width : 270,
				title : '设置时间',
				desc : '设置指定员工上下班时间'
			},
			formConfig : {
				items : [{
				xtype:'timefield',
          		fieldLabel:'上班时间',
          		format:'H:i',
          		id:'DStartTime',
          		increment:30,
          		width:100,
          		name:'startTime'
				},{
					xtype:'f-number',
					fieldLabel:'午休小时',
	          		id:'DbunchBreakTime',
	          		width:100,
//	          		value:'1',
	          		name:'bunchBreakTime'
				},{
				xtype:'timefield',
          		fieldLabel:'下班时间',
          		format:'H:i',
          		id:'DEndTime',
          		increment:30,
          		width:100,
          		name:'endTime'
				}]
			},
			buttons : [{     
				text: '确定',
				scope:this,
				handler : function(){
//					var startTime = Ext.getCmp('DStartTime').getValue();
//					var endTime = Ext.getCmp('DEndTime').getValue();
					this.win.formPanel.getForm().submit({           
			            waitMsg:'保存中...',
						url:ctx+'/workingArea/setDefaultTime',
						scope:this,
						success:function(form, action) {
							this.win.close();
			            }
			        });
				}
			}]
		});
		this.win.on('render',function(){
			Ext.Ajax.request({
				url :ctx+'/workingArea/getDefaultTime',
				success:function(form,action){
					var data=Ext.util.JSON.decode(form.responseText);
					Ext.getCmp('DStartTime').setValue(data.startTime);
					Ext.getCmp('DEndTime').setValue(data.endTime);
					Ext.getCmp('DbunchBreakTime').setValue(data.bunchBreakTime);
				}
			});
		});
		this.win.show();
	}
	
});
// =====================  for center =====================

BranchShiftTreePanel = Ext.extend(Ext.Panel,{
	layout:'border',
	initComponent : function(){
		var orgId = "";
		this.areaTree = new Ext.tree.TreePanel({
			id:'workingAreaTree',
        	xtype:'treepanel',
			region:'west',
			title:'排班情况',
        	split:true,
        	width:355,
        	autoScroll: true,
        	enableDD:true,
        	containerScroll : true,
        	tbar:[new Ext.Toolbar.TextItem('网点：'),
          		{
        		xtype: 'treeField',
        		width:150,
        		id:'workOrganizationId',
        		fieldLabel: '网点',
        		listHeight:240,
        		emptyText:'请选择要排班的网点',
        		hiddenName: 'organization',
//				name:'organization',
        		privilegeCode:this.funcCode+'_areaChange',
				dataUrl : ctx+'/organization/getOrganizationByRole',
				readOnly:false,
				listeners : {
        			select:function(combo, record, index){
        				orgId=record.id;
        				Ext.getCmp('workingAreaTree').loadRoot({selectedDate:Ext.getCmp('branchDatePicker').getValue().format('Y-m-d'),organizationId:record.id});
        				Ext.getCmp('availableEmployeesTree').loadRoot({selectedDate:Ext.getCmp('branchDatePicker').getValue().format('Y-m-d'),organizationId:record.id});
        			}
        		}
			},{
				xtype:'f-button',
				id:'BranchShiftConfig_del',
				scope:this,
				text:'清除',
				iconCls:'remove',
				tooltip:'清除当天所有人的排班',
				privilegeCode: this.funcCode + '_delAll',
				handler:this.delAllShiftConfig
//				handler:this.isDelAllShiftConfig
			},'-',{
        		xtype : 'f-button',
        		id : 'copyShiftconfigButton',
        		text : '复制',
        		iconCls:'copy',
        		tooltip:'复制当天的排班',
//        		style : 'margin-left:30px;margin-top:20px;',
        		scope : this,
        		handler : this.copyCurrentDayShiftconfig
        	},'-',{
        		xtype : 'f-button',
        		id : 'pasteshiftconfigButton',
        		text : '粘贴',
        		iconCls:'paste',
        		tooltip:'把复制的排班粘贴到该天的排版',
//        		style : 'margin-left:30px;margin-top:20px;',
        		scope : this,
//        		handler : this.pasteShiftconfigToDay
        		handler : this.isPasteShiftconfigToDay
        	}],
        	dragConfig : {
        		ddGroup:'shiftConfig'
        	},
        	dropConfig: {
        		ddGroup:'shiftConfig',
        		appendOnly:true
        	},
	        loader: new Ext.tree.TreeLoader({
	        	dataUrl:ctx+'/workingArea/getbranchShiftConfig',
	        	baseParams:{ 
	        		selectedDate : Ext.getCmp('branchDatePicker').getValue().format('Y-m-d')
	        	}
	        }),
	        root: new Ext.tree.AsyncTreeNode({
	            id:'0'
	        }),
	        rootVisible: false
        });
        
        this.employeeTree = new Ext.tree.TreePanel({
        	id:'availableEmployeesTree',
        	xtype:'treepanel',
        	region:'center',
        	title:'该部门可用人员',
        	autoScroll: true,
        	enableDD:true,
        	containerScroll : true,
        	dragConfig : {
        		ddGroup:'shiftConfig'
        	},
        	dropConfig: {
        		allowContainerDrop:true,
        		ddGroup:'shiftConfig',
        		appendOnly:true
        	},
	        loader: new Ext.tree.TreeLoader({
	        	dataUrl:ctx+'/workingArea/availableEmployees',
	        	baseParams:{ 
	        		selectedDate : Ext.getCmp('branchDatePicker').getValue().format('Y-m-d')
	        	}
	        }),
	        root: new Ext.tree.AsyncTreeNode({
	            id:'0'
	        }),
	        rootVisible: false
        });
        
		this.items = [this.areaTree,this.employeeTree];
		
		BranchShiftTreePanel.superclass.initComponent.call(this);
		var areaRightClick = new Ext.menu.Menu({
		    id: 'areaRightClick',
		    items: [{
		        text: '添加外部人员',
		        iconCls: 'add',
		        handler: this.addEmloyee
		    }]
		});
		var employeeRightClick = new Ext.menu.Menu({
		    id: 'employeeRightClick',
		    items: [{
		        text: '设置时间',
		        iconCls: 'config',
		        handler: this.setTime
		    },{
		    	text: '删除',
		        iconCls: 'remove',
		        handler: this.removeEmloyee
		    }]
		});
		this.areaTree.on('nodedrop',function(e){
			var areaId = e.target.id;
			var employeeId = e.data.node.id;
			var organizationId = "";
			var org = Ext.getCmp('OrganizationWin_EmployeeTree');
			if(org){
				organizationId = Ext.getCmp('OrganizationWin_EmployeeTree').organizationId;
			}
			Ext.Ajax.request({
				url :ctx+'/workingArea/appendEmployee',
				params:{
					areaId:areaId, 
					employeeId:employeeId,
					organizationId:organizationId,
					orgId: orgId,
					startTime:'08:30',
					endTime:'17:30',
					selectedDate : Ext.getCmp('branchDatePicker').getValue().format('Y-m-d')
				}
			});
			
		},this);
		
		this.employeeTree.on('nodedrop',function(e){
			var areaId = e.target.id;
			var employeeId = e.data.node.id;
			Ext.Ajax.request({
				url :ctx+'/workingArea/removeEmployee',
				params:{
					employeeId:employeeId,
					selectedDate : Ext.getCmp('branchDatePicker').getValue().format('Y-m-d')
				}
			});
			
		},this);
		
      	this.areaTree.on('contextmenu', function (node, event) {
      	  	event.preventDefault();  //屏蔽默认右键菜单
          	node.select();
      		if(node.attributes.iconCls =='userman'||node.attributes.iconCls =="employee_female"){
          		employeeRightClick.showAt(event.getXY());
          		return;
          	}
          	var childNodes = node.childNodes;
          	if(childNodes.length>0&&childNodes[0].attributes.iconCls =='workingArea'){
          		return;
          	}
            if(!Ext.getCmp('addEmp')){
              	areaRightClick.showAt(event.getXY());
          	}
      	});
	},
	delAllShiftConfig:function(){
		Ext.Msg.confirm("温馨提示","您确定要清除今天该网点的全部排班吗？",function(btn){
			if(btn=='yes'){
				var orgId = Ext.getCmp('workOrganizationId').getValue();
				if(orgId){
					Ext.Ajax.request({
						url :ctx+'/workingArea/removeAllEmployee',
						scope:this,
						params:{
							selectedDate : Ext.getCmp('branchDatePicker').getValue().format('Y-m-d'),
							organizationId:orgId
						},
						success:function(form,action){
							var sa = Ext.getCmp('workingAreaTree');
							var date = Ext.getCmp('branchDatePicker').getValue().format('Y-m-d');
							sa.loadRoot({selectedDate:date,organizationId:orgId});
							var ss = Ext.getCmp('availableEmployeesTree');
							Ext.getCmp('availableEmployeesTree').loadRoot({selectedDate:date});
						}
					});
				}else{
					App.msg('请选择要清除的网点');
				}
			}
		});
	},
	isDelAllShiftConfig:function(){
		Ext.Msg.confirm("温馨提示","您确定要清除今天该网点的全部排班吗？",function(btn){
			if(btn=='yes'){
				this.delAllShiftConfig;
			}
		});
	},
	addEmloyee:function(event){
		var ow = new OrganizationWin({id:'addEmp'});
		var height = document.body.clientHeight;
		var width = document.body.clientWidth;
		ow.setPosition(width-500, (height-400)/2);
    	ow.show();
    },
    setTime:function(){
    	var node = Ext.getCmp('workingAreaTree').getSelectionModel().getSelectedNode();
    	var employeeId = node.id;
    	var date = Ext.getCmp('branchDatePicker').getValue().format('Y-m-d')
    	this.setTimeWin = new Ext.app.FormWindow({
			winConfig : {
				height : 260,
				width : 395,
				title : '设置时间',
				desc : '设置指定员工上下班时间'
			},
			formConfig : {
				items : [{
					xtype:'timefield',
            		fieldLabel:'上班时间',
//            		value:'08:30',
            		id:'T-startTime',
            		format:'H:i',
            		increment:30,
            		width:230,
            		name:'startTime'
				},{
					xtype:'f-number',
					fieldLabel:'午休小时',
	          		id:'T-bunchBreakTime',
	          		width:230,
//	          		value:'1',
	          		name:'bunchBreakTime'
				},{
					xtype:'timefield',
            		fieldLabel:'下班时间',
            		id:'T-endTime',
//            		value:'17:30',
            		format:'H:i',
            		increment:30,
            		width:230,
            		name:'endTime'
				}]
			},
			buttons : [{
				text: '确定',
				scope:this,
				handler : function(){
					this.setTimeWin.formPanel.getForm().submit({           
			            waitMsg:'保存中...',
						url:ctx+'/workingArea/setEmployeeTime?employeeId='+employeeId+'&date='+date,
						scope:this,
						success:function(form, action) {
							App.msg("更新的信息以保存");
							this.setTimeWin.close();
			            }
			        });
				}
			}]
		});
    	
    	//我添加的东西
    	this.setTimeWin.on('render',function(){
			Ext.Ajax.request({
				url :ctx+'/workingArea/getEmployeeTime?employeeId='+employeeId+'&date='+date,
				success:function(form,action){
					var data=Ext.util.JSON.decode(form.responseText);
					Ext.getCmp('T-startTime').setValue(data.startTime);
					Ext.getCmp('T-endTime').setValue(data.endTime);
					Ext.getCmp('T-bunchBreakTime').setValue(data.bunchBreakTime);
				}
			});
		});
    	
		this.setTimeWin.show();
    },
    removeEmloyee:function(e){
    	var node = Ext.getCmp('workingAreaTree').getSelectionModel().getSelectedNode();
    	var areaId = node.parentNode.id;
		var employeeId = node.id;
		var parentNode = node.parentNode;
		parentNode.removeChild(node);
		var empTree = Ext.getCmp('availableEmployeesTree').getRootNode();
		if(!node.attributes.organization){
			empTree.appendChild(
				new Ext.tree.TreeNode({
					id:node.id,
	              	text:node.text, 
	              	iconCls:node.attributes.iconCls,
	              	leaf:node.leaf,
	              	draggable:true 
        		})
        	);
    	}
		Ext.Ajax.request({
			url :ctx+'/workingArea/removeEmployee',
			params:{
				employeeId:employeeId,
				selectedDate : Ext.getCmp('branchDatePicker').getValue().format('Y-m-d')
			}
		});
    },
	loadByDate:function(date){
		Ext.getCmp('workingAreaTree').loadRoot({selectedDate:date.format('Y-m-d')});
		Ext.getCmp('availableEmployeesTree').loadRoot({selectedDate:date.format('Y-m-d')});
	},
	copyCurrentDayShiftconfig:function(){
		var selectDate = Ext.getCmp('branchDatePicker').getValue().format('Y-m-d');
		var orgId = Ext.getCmp('workOrganizationId').getValue(); 
		if(orgId){
			this.copy={selectDate:selectDate,orgId:orgId};
			App.msg('成功复制了'+selectDate+'的排班');
		}else{
			this.copy = {selectDate:selectDate,orgId:orgId};
			App.msg('默认为当前登录用户的网点，可以选择网点查看其它网点排班');
		}
	},
	isPasteShiftconfigToDay:function(){
		var currentDate = Ext.getCmp('branchDatePicker').getValue().format('Y-m-d');
		Ext.Msg.confirm("温馨提示","确定要把"+this.copy.selectDate+"天排班复制到"+currentDate,function(btn){
			if(btn=='yes'){
				var selectDate = this.copy.selectDate;
				var orgId = this.copy.orgId;
				Ext.Ajax.request({
					url :ctx+'/workingArea/pasteShiftConfigToDay',
					scope:this,
					params:{
						selectedDate :selectDate,
						organizationId:orgId,
						currentDate:currentDate
					},
					success:function(form,action){
						var sa = Ext.getCmp('workingAreaTree');
						sa.loadRoot({selectedDate:currentDate,organizationId:orgId});
						var ss = Ext.getCmp('availableEmployeesTree');
						Ext.getCmp('availableEmployeesTree').loadRoot({selectedDate:currentDate});
						App.msg(selectDate+'天的排班成功粘贴到'+currentDate);
					}
				});
			}
		},this);
	}
});


BranchShiftConfig = Ext.extend(Ext.Panel,{
	layout:'border',
	closable: true,
	hideMode:'offsets',
	initComponent : function(){
		
		this.left = new BranchCalendarPanel({
			funcCode: this.funcCode,
			id:'BranchCalendarPanel',
			region:'west',
			margins:'0 5 0 0',
			width: 190
		});
		
		this.center = new BranchShiftTreePanel({
			id:'BranchShiftTreePanel',
			border:false,
            region:'center',
            funcCode: this.funcCode
		});
		
		
		this.items = [this.left,this.center];
		
		BranchShiftConfig.superclass.initComponent.call(this);
	},
	loadData:function(){
		//Ext.getCmp('BranchShiftTreePanel').loadByDate(new Date());
	}
	
});
