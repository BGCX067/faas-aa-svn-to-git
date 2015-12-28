WorkOverTimeWin = Ext.extend(Ext.app.FormWindow,{
	initComponent:function(){
	Ext.apply(this,{
		winConfig:{
			height: 420,
			width:430,
			title: '加班申请表',
			desc : '加班申请表填写'
		},
		formConfig:{
			items:[{
				border : false,
				defaults : {
		    		border : false
		    	},
				layout: 'form',
				items: [
					{xtype: 'f-AttOrganizationEmployee', id: 'workOverTime_employee', fieldLabel: '申请员工', 
						hiddenName: 'employeeId',allowBlank: false,width:200},
					{xtype:'f-text',name:'overTime_id',id:'workovertime_id',hidden:true},
					{xtype:'f-text',name:'operateApplyType',id:'workoverTime_operateApplyType',hidden:true},
					{xtype:'f-text',name:'applyStatus',id:'workovertime_applyStatus',hidden:true},
					{xtype:'f-text',name:'operateApplyType',value:'overTime',hidden:true},
					{xtype: 'f-date',fieldLabel: '开始日期',name: 'workOverTimeDate',width:200,allowBlank: false},
					{xtype: 'f-date',fieldLabel: '结束日期',name: 'eWorkOverTimeDate',width:200,allowBlank: false},
					{xtype: 'timefield',width:200,id:'workOverTimeStart',fieldLabel: '加班起始时间',name: 'workOverTimeStart',format:'H:i',increment:30,allowBlank: false},
					{xtype: 'timefield',width:200,id:'workOverTimeEnd',fieldLabel: '加班结束时间',name: 'workOverTimeEnd',format:'H:i',increment:30,allowBlank: false},
					{xtype: 'f-textarea',width:200,fieldLabel: '申请原因',id:'workOverTimeReason',name: 'workOverTimeReason',height:50,allowBlank: false},
					{xtype: 'treeField',width:200,fieldLabel: '部门',id:'organizationOne',listHeight:240,hiddenName: 'organization'
					    ,name:'organizationOne',dataUrl : ctx+'/organization/getOrganizationTree',readOnly:false},
					{xtype: 'f-select',width:200,fieldLabel:'审批人',allowBlank: false,hiddenName:'leaveApprove1',id:'workoverTime_leaveApprove1',relativeField:'organizationOne',dataUrl:'/employee/getHolidayApplyByOrganization'}
				]
			}],
			buttons:[{
				xtype:'f-button',
				text:"保存",
				scope:this,
				handler:function(){
					this.formPanel.getForm().submit({
						waitMsg:'保存中...',
						url:ctx+'/holidayApply/'+(this.overtime?this.overtime:'workOverTime'),
						scope:this,
						success:function(form, action) {
							this.close();
							Ext.getCmp('holidayApplyTreeList').listHolidayApplyList();
						}
					});
				}
			},{
				xtype:'f-button',
				text:"关闭",
				scope:this,
				handler:function(){this.close();}
			}]
		}
	});
	WorkOverTimeWin.superclass.initComponent.call(this);
	}
});

BrowseViewWin =Ext.extend(Ext.app.FormWindow,{
	initComponent:function(){
		Ext.apply(this,{
			winConfig : {
			height : 600,
			width : 600,
			title : '请假审批详细信息',
			desc : '中国建设银行深圳市分行员工请假审批表'
		},
		formConfig : {
			items : [{
				xtype:'fieldset',
				title: '基本信息',autoHeight:true,
				items :[{
					layout:'column',
					border : false,
					defaults : {
		                border : false
		            },
		            items:[{
						width:300,
				        layout: 'form',
				        defaults : {
				         	msgTarget : 'under',
				        	width : 280
				        },
						items: [
						    {xtype: 'f-text',fieldLabel: '姓名',name: 'name',width:100,readOnly:true},
						    {xtype: 'f-text',fieldLabel: '岗位',name: 'position',width:100,readOnly:true},
						    {xtype: 'f-text',fieldLabel: '参加工作时间',name: 'joinWorkDate',width:100,readOnly:true},
						    {xtype: 'f-text',fieldLabel: '假期结束时间',name: 'endDate',readOnly:true,width:100},
						    {xtype: 'f-text',fieldLabel: '假期开始时间',name: 'startDate',width:100,readOnly:true},
						    {xtype: 'f-text',fieldLabel: '拟往地点',name: 'site',width:100,readOnly:true},
						    {xtype: 'f-text',fieldLabel: '外出联系电话',name: 'goOutPhone',width:100,readOnly:true},
						    {xtype: 'f-text',fieldLabel: '详细事由',name: 'applyReason',readOnly:true,width:100}
						]
					},{
						width:300,
				        layout: 'form',
				        columnWidth:.9,
				        defaults : {
				         	msgTarget : 'under',
				        	width :280
				        },
						items: [
						    {xtype: 'f-text',fieldLabel: '单位',name: 'organization',readOnly:true,width:100,id:'BrowseView_organization'},
						    {xtype: 'f-text',fieldLabel: '婚姻状况',name: 'isMarry',readOnly:true,width:100},
						    {xtype: 'f-text',fieldLabel: '正式调入时间',name: 'hireDate',readOnly:true,width:100},
						    {xtype: 'f-text',fieldLabel: '申请日期',name:'applyDate',readOnly:true,widht:100},
						    {xtype: 'f-text',fieldLabel: '回行上班时间',name: 'backDate',readOnly:true,width:100},
						    {xtype: 'f-text',fieldLabel: '拟休假类别',name: 'leaveType',readOnly:true,width:100,id:'BrowseView_leaveType'},
						    {xtype: 'f-text',fieldLabel: '休假天数',name: 'leaveDays',readOnly:true,width:100},
						    {xtype: 'f-text',name:'leaveApplyType',width:100,hidden:true}
						]
					}]								
				}]								
			},{
				id:'BrowseViewWin_approveProcess',
				xtype:'fieldset',
				title: '审批过程',
				autoHeight:true,
				items :[
					
				]
			}]
		},
		buttons : [{
			text: '打印',
			scope:this,
			handler : function(){
				var gird = Ext.getCmp('holidayApplyList'); 
				var record = gird.store.getById(gird.selectedId)
				var r = this.formPanel.getForm().getValues();
				var paramsMap = '';
				for(var element in r) {
					if(paramsMap) {
						paramsMap += '&';
					}
					paramsMap += element + '=' + encodeURI(r[element]);
				}
				var approveProcesses = publicProcess.approveProcess;
				for(var i=0;i<approveProcesses.length;i++){
					var ap = approveProcesses[i];
					for(var element in ap) {
						if(paramsMap) {
							paramsMap += '&';
						}
						paramsMap += (element+i)+ '=' + encodeURI(ap[element]);
					}
				}
				if(record.data.oldOrNew == "NEW"){
					window.open( ctx+"/newHoliday.jsp?"+paramsMap , "请假单","width=680,height=700,toolbar=no,scrollbars=no,menubar=yes,screenX=300,screenY=20");
				}else{
					window.open( ctx+"/holiday.jsp?"+paramsMap , "请假单","width=680,height=700,toolbar=no,scrollbars=no,menubar=yes,screenX=300,screenY=20");
				}
			}
		}]
		});
		BrowseViewWin.superclass.initComponent.call(this);
	}
});

OverTimeViewWin =  Ext.extend(Ext.app.FormWindow,{
	initComponent:function(){
	
	Ext.apply(this,{
		winConfig : {
			height : 600,
			width : 600,
			title : '加班审批详细信息',
			desc : '中国建设银行深圳市分行员工加班审批表'
		},
		formConfig : {
			items : [{
				xtype:'fieldset',
				title: '基本信息',autoHeight:true,
				items :[{
					layout:'column',
					border : false,
					defaults : {
		                border : false
		            },
		            items:[{
						width:300,
				        layout: 'form',
				        defaults : {
				         	msgTarget : 'under',
				        	width : 280
				        },
						items: [
						    {xtype: 'f-text',fieldLabel: '姓名',name: 'name',readOnly:true},
						    {xtype: 'f-text',fieldLabel: '岗位',name: 'position',readOnly:true},
						    {xtype: 'f-text',fieldLabel: '开始日期',name: 'workOverTime',readOnly:true},
						    {xtype: 'f-text',fieldLabel: '开始时间',name: 'workOverTimeStart',readOnly:true},
						    {xtype: 'f-text',fieldLabel: '详细事由',name: 'applyReason',readOnly:true}
						]
					},{
						width:300,
				        layout: 'form',
				        columnWidth:.9,
				        defaults : {
				         	msgTarget : 'under',
				        	width :280
				        },
						items: [
						    {xtype: 'f-text',fieldLabel: '单位',name: 'organization',readOnly:true,id:'overTimeView_organization'},
						    {xtype: 'f-text',fieldLabel: '申请时间',name: 'applyDate',readOnly:true},
						    {xtype: 'f-text',fieldLabel: '结束日期',name: 'eWorkOverTimeDate',readOnly:true},
						    {xtype: 'f-text',fieldLabel: '结束时间',name: 'workOverTimeEnd',readOnly:true}
						]
					}]
				}]								
			},{
				id:'OverTimeViewWin_approveProcess',
				xtype:'fieldset',
				title: '审批过程',
				autoHeight:true,
				items :[
					
				]
			}]
		},
		buttons : [{
			text: '打印',
			scope:this,
			handler : function(){
				var gird = Ext.getCmp('holidayApplyList'); 
				var record = gird.store.getById(this.selectedId)
				var r = this.formPanel.getForm().getValues();
				var paramsMap = '';
				for(var element in r) {
					if(paramsMap) {
						paramsMap += '&';
					}
					paramsMap += element + '=' + encodeURI(r[element]);
				}
				var approveProcesses = publicProcess.approveProcess;
				for(var i=0;i<approveProcesses.length;i++){
					var ap = approveProcesses[i];
					for(var element in ap) {
						if(paramsMap) {
							paramsMap += '&';
						}
						paramsMap += (element+i)+ '=' + encodeURI(ap[element]);
					}
				}
				window.open( ctx+"/workOverTime.jsp?"+paramsMap , "加班申请单","width=680,height=700,toolbar=no,scrollbars=no,menubar=no,screenX=300,screenY=20");
			}
		}]
	});
		OverTimeViewWin.superclass.initComponent.call(this);
	}
});


OrganizationLeader = Ext.extend(Ext.app.FormWindow,{
	initComponent:function(){
			Ext.apply(this,{
			winConfig : {
				height : 500,
				width : 720,
				title : '请假申请',
				desc : '请选择不同的请假单申请'
			},
			buttons:['all'],
			formConfig:{
				items: [{
					layout:'form',
					border : false,
					defaults : {
                		border : false
                	},
                	items: [{
    					layout:'column',
    					border : false,
    					defaults : {
                    		border : false
                    	},
    					items :[{
    						width:320,
    	                	layout: 'form',
    	                	defaults : {
    	                		msgTarget : 'under',
    	                		width : 220
    	                	},
    						items: [
    							{xtype: 'f-AttOrganizationEmployee', id: 'HolidayApplay_employee', fieldLabel: '申请员工', 
    									hiddenName: 'leaveApply',allowBlank: false},
    							{xtype: 'f-dict',fieldLabel: '请假类型',hiddenName: 'leaveType',kind: 'leaveType',allowBlank: false},
    							{xtype: 'f-text',name:'operateApplyType',value:'holiday',hidden:true},
    							{xtype: 'f-select',fieldLabel:'婚姻状况',name:'isMarry',hiddenName:'isMarry',data:[['YES','已婚'],['NO','未婚']],allowBlank: false},
    							{xtype: 'f-date',id:'holidayStartDate',fieldLabel: '开始日期',name: 'startDate',allowBlank: false},
    							{xtype: 'f-date',id:'holidayEndDate',fieldLabel: '结束日期',name: 'endDate',allowBlank: false},
    							{xtype: 'f-number',fieldLabel: '请假天数',name: 'leaveDays',allowBlank: false},
    							{xtype: 'f-date',fieldLabel: '回行上班时间',name: 'backDate',allowBlank: false},
    							{xtype: 'f-text',fieldLabel: '拟往地点',name: 'site',allowBlank: false},
    							{xtype: 'f-number',fieldLabel: '外出联系电话',name: 'goOutPhone',allowBlank: false},
    							{xtype: 'f-textarea',fieldLabel: '申请原因',name: 'applyReason',height:50,allowBlank: false}
    						]
    					},{
    						width:300,
    						layout: 'form',
    						defaults : {
    							msgTarget : 'under',
    							width : 320
    						},
    						items:this.approveList
    					}]
    				}]
				}]
			},
			buttons:[{
				xtype:'f-button',
				text:"保存",
				scope:this,
				handler:function(){
				
				var as = Ext.get('organizationOne');
				var sa = as.dom.value;
				var ss = as.getValue();
					if(this.saveType=='update'){
						this.formPanel.getForm().submit({           
							waitMsg:'保存中...',
							url:ctx+'/holidayApply/update',
							params: this.ajaxParams,
							scope:this,
							success:function(form, action) {
								this.close();
								Ext.getCmp('holidayApplyTreeList').listHolidayApplyList();
							}
						});
					}else{
						this.formPanel.getForm().submit({           
							waitMsg:'保存中...',
							url:ctx+'/holidayApply/create',
							scope:this,
							success:function(form, action) {
								this.close();
								Ext.getCmp('holidayApplyTreeList').listHolidayApplyList();
							}
						});
					}
				}
			},{
				xtype:'f-button',
				text:"关闭",
				scope:this,
				handler:function(){this.close();}
			}]
		});
		OrganizationLeader.superclass.initComponent.call(this);
	}
});



HolidayVirgation =  Ext.extend(Ext.app.FormWindow,{
	initComponent:function(){
		Ext.apply(this,{
			winConfig : {
				height : 200,
				width : 326,
				title : '请假申请',
				desc : '请选择不同的请假单申请'
			},
			formConfig : {
				layout: 'form',
		        defaults : {
		         	msgTarget : 'under',
		        	width : 280
		        },
				items : [
				         {xtype:'f-button',text:'一级机构、二级部（中心）负责人请假申请',handler:this.leaderWinShow},
				         {xtype:'f-button',text:'各网点负责人请假申请',handler:this.branchWinShow},
				         {xtype:'f-button',text:'普通员工请假申请',handler:this.employeeWinShow}
				]
			}
		});
		HolidayVirgation.superclass.initComponent.call(this);
	},
	employeeWinShow:function(){
		var employeeWin = new OrganizationLeader({id:'employeeWin',
			approveList:employeeApproveList
		});
		employeeWin.show();
		Ext.getCmp('HolidayVirgation').close();
	},
	branchWinShow:function(){
		var branchWin = new OrganizationLeader({id:'branchWin',
			approveList : brancaApproveList});
		branchWin.show();
		Ext.getCmp('HolidayVirgation').close();
	},
	leaderWinShow:function(){
		var leaderWin = new OrganizationLeader({id:'leaderWin',
			approveList : leaderApproveList});
		leaderWin.show();
		Ext.getCmp('HolidayVirgation').close();
	}
});

HolidayApplyGrid = Ext.extend(Ext.app.BaseFuncPanel,{
	initComponent : function(){
		var TypeRenderer = function(v){
			return v.text;
		}
		this.fillApplyBt = new Ext.app.Button({
			id:'HolidayApply_fillApplyBt',
			text : '填申请表',
            tooltip : '填写请假申请表',
            enableOnEmpty:true,
            iconCls : 'add',
            privilegeCode: this.funcCode + '_add',
			scope:this,
			handler:this.virgation	
//			handler:this.prepareAdd	
		});
		this.workOverTimeBt = new Ext.app.Button({
			id:'HolidayApply_workOverTime',
			text:'填加班申请表',
			tooltip:'填写加班申请表',
			enableOnEmpty:true,
			iconCls:'add',
			privilegeCode:this.funcCode+"_workOverTime",
			scope:this,
			handler:this.addWorkOverTime
		});
		this.editApplyBt = new Ext.app.Button({
			id:'HolidayApply_editApplyBt',
			text : '修改',
            tooltip : '修改选中的请假审批流程',
            iconCls : 'pencil',
			scope:this,
			privilegeCode: this.funcCode + '_edit',
			hidden : true,
			handler:this.editApplyFlowHandler	
		});
		this.approveBt = new Ext.app.Button({
			id:'HolidayApply_approveBt',
			text : '审批',
            tooltip : '审批请假申请表',
            iconCls : 'approve',
			scope:this,
			privilegeCode: this.funcCode + '_approve',
			hidden : true,
			handler:this.approveHandler	
		});
		this.delApplyBt = new Ext.app.Button({
			id:'HolidayApply_delApplyBt',
			text : '删除',
            tooltip : '删除选中请假申请表',
            iconCls : 'remove',
			scope:this,
			privilegeCode: this.funcCode + '_del',
			hidden : true,
			handler:this.delApplyHandler	
		});
		this.browseViewBt = new Ext.app.Button({
			id:'HolidayApply_browseViewBt',
			text:'详细信息',
			tooltip:'浏览详细的请假审批表信息',
			iconCls : 'search',
			scope:this,
			privilegeCode: this.funcCode + '_browseView',
			handler:this.browseViewHandler	
		});
		this.placeOnFileBt = new Ext.app.Button({
			id:'HolidayApply_placeOnFileBt',
			text:'归档',
			tooltip:'将选中请假单归档',
			iconCls : 'placeOnFile',
			scope:this,
			privilegeCode:this.funcCode+"_placeOnFile",
			handler:this.placeOnFile
		});
		this.cancelBt = new Ext.app.Button({
			id:'HolidayApply_cancelBt',
			text:'销假',
			tooltip:'将选中请假单销假',
			iconCls : 'cancelHoliday',
			scope:this,
			hidden:true,
			privilegeCode:this.funcCode+"_cancelBt",
			handler:this.cancelHoliday
		});
		this.bossAffirmBt = new Ext.app.Button({
			id:'HolidayApply_bossAffirmBt',
			text:'办公室审核',
			tooltip:'选中由办公室审核',
			iconCls:'approve',
			scope:this,
//			hidden:true,
			privilegeCode:this.funcCode+"_bossAffirm",
			handler:this.bossAffirmFunction
		});
		var applyReasonRenderer = function(v,metaData, record){
			if(record.data.applyReason!=""){
				return String.format('<span class="applyReason"  title="全部内容:{0}">{1}</span>',record.data.applyReason,record.data.applyReason);
			}else{
				return v;
			}
		};
		Ext.apply(this,{
			gridConfig:{
				sm:new Ext.grid.RowSelectionModel(),
				cm:new Ext.grid.ColumnModel([
					new Ext.grid.RowNumberer(),
					{header:'请假流水号',dataIndex:'id',hidden:true},
					{header: '员工编号',dataIndex:'code',sortable:true},
					{header: '申请人',dataIndex:'name',sortable:true},
					{header: '部门',dataIndex:'organization',renderer:TypeRenderer},
					{header: '申请日期',dataIndex:'applyDate',sortable:true},
					{header: '类型',dataIndex:'applyType'},
					{header: '请假类型',dataIndex:'leaveType',renderer:TypeRenderer,id:'cm_leaveType'},
					{header: '开始日期',dataIndex:'startDate',id:'cm_startDate'},
					{header: '结束日期',dataIndex:'endDate',id:'cm_endDate'},
					{header: '销假结束日期',dataIndex:'cancelDate',id:'cm_cancelDate'},
					{header: '开始日期',dataIndex:'workOverTime',id:'cm_workOverTime',hidden:true},
					{header: '结束日期',dataIndex:'eWorkOverTimeDate',id:'cm_eWorkOverTimeDate',hidden:true},
					{header: '开始时间',dataIndex:'workOverTimeStart',id:'cm_workOverTimeStart',hidden:true},
					{header: '结束时间',dataIndex:'workOverTimeEnd',id:'cm_workOverTimeEnd',hidden:true},
					{header: '请假天数',dataIndex:'leaveDays',id:'cm_leaveDays'},
					{header: '审批状态',dataIndex:'applyStatus',renderer:TypeRenderer},
					{header: '申请原因',dataIndex:'applyReason',renderer:applyReasonRenderer},
					{header: '当前审批人',dataIndex:'currentApprove',renderer:TypeRenderer}
				]),	
				storeMapping:[
					'id','code','name','leaveApply','cancelDate','organization','position','goOutPhone','site','backDate','node','applyDate','leaveType','startDate','endDate','joinWorkDate','hireDate','isCertigier','oldOrNew','leaveApplyType'
					,'isMarry','leaveDays','applyStatus','workOverTime','eWorkOverTimeDate','certigier','bossAffirm','applyReason','currentApprove','leaveApprove1','leaveApprove2','leaveApprove3','leaveApprove4','leaveApprove5','approveProcess','applyType','workOverTimeStart','workOverTimeEnd'
				]
			},
			winConfig : {
				height: 490,width:730,
				desc : '填写请假申请人的请假信息',
				bigIconClass : 'employeeIcon'
			},	
			buttonConfig:[this.fillApplyBt,this.workOverTimeBt,this.editApplyBt,this.cancelBt,
			              this.approveBt,this.delApplyBt,this.browseViewBt,this.bossAffirmBt],
			formConfig:{
				items: [{
					layout:'form',
					border : false,
					defaults : {
                		border : false
                	},
                	items: [{
    					layout:'column',
    					border : false,
    					defaults : {
                    		border : false
                    	},
    					items :[{
    						width:320,
    	                	layout: 'form',
    	                	defaults : {
    	                		msgTarget : 'under',
    	                		width : 220
    	                	},
    						items: [
    							{xtype: 'f-AttOrganizationEmployee', id: 'HolidayApplay_employee', fieldLabel: '申请员工', 
    									hiddenName: 'leaveApply',allowBlank: false},
    							{xtype: 'f-dict',fieldLabel: '请假类型',hiddenName: 'leaveType',kind: 'leaveType',allowBlank: false},
    							{xtype: 'f-text',name:'operateApplyType',value:'holiday',hidden:true},
    							{xtype: 'f-select',fieldLabel:'婚姻状况',name:'isMarry',hiddenName:'isMarry',data:[['YES','已婚'],['NO','未婚']],allowBlank: false},
    							{xtype: 'f-date',id:'holidayStartDate',fieldLabel: '开始日期',name: 'startDate',allowBlank: false},
    							{xtype: 'f-date',id:'holidayEndDate',fieldLabel: '结束日期',name: 'endDate',allowBlank: false},
    							{xtype: 'f-number',fieldLabel: '请假天数',name: 'leaveDays',allowBlank: false},
    							{xtype: 'f-date',fieldLabel: '回行上班时间',name: 'backDate',allowBlank: false},
    							{xtype: 'f-text',fieldLabel: '拟往地点',name: 'site',allowBlank: false},
    							{xtype: 'f-number',fieldLabel: '外出联系电话',name: 'goOutPhone',allowBlank: false},
    							{xtype: 'f-textarea',fieldLabel: '申请原因',name: 'applyReason',height:50,allowBlank: false}
    						]
    					},{
    						width:300,
    						layout: 'form',
    						defaults : {
    							msgTarget : 'under',
    							width : 320
    						},
    						items:[
    				    	    {xtype: 'treeField',width:150,fieldLabel: '部门',id:'organizationOne',listHeight:240,hiddenName: 'organization'
    						    ,name:'organizationOne',dataUrl : ctx+'/organization/getOrganizationTree',readOnly:false},
    						    {xtype: 'f-select',width:150,fieldLabel:'第一审批人',hiddenName:'leaveApprove1',allowBlank: false,relativeField:'organizationOne',dataUrl:'/employee/getHolidayApplyByOrganization'},
    				    	    {xtype: 'treeField',width:150,fieldLabel: '部门',id:'organizationTwo',listHeight:240,hiddenName: 'organization2'
    						    ,name:'organizationTwo',dataUrl : ctx+'/organization/getOrganizationTree',readOnly:false,
    						    listeners:{
    						    	select:function(combo, record, index){
    						    		if(record.id=="H-0"&&record.text=="取消选择"){
    						    			Ext.getCmp('leaveApproveTwo').setValue({id:'',text:''});
    						    			Ext.getCmp('leaveApproveThree').setValue({id:'',text:''});
    						    			Ext.getCmp('leaveApproveFour').setValue({id:'',text:''});
    						    		}
    						    	}
    						    }},
    						    {xtype: 'f-select',width:150,id:'leaveApproveTwo',fieldLabel:'第二审批人',hiddenName:'leaveApprove2',relativeField:'organizationTwo',dataUrl:'/employee/getHolidayApplyByOrganization'},
    				    	    {xtype: 'treeField',width:150,fieldLabel: '部门',id:'organizationThree',listHeight:240,hiddenName: 'organization3'
    						    ,name:'organizationThree',dataUrl : ctx+'/organization/getOrganizationTree',readOnly:false,
    						    listeners:{
    						    	select:function(combo, record, index){
    						    		if(record.id=="H-0"&&record.text=="取消选择"){
    						    			Ext.getCmp('leaveApproveFour').setValue({id:'',text:''});
    						    			Ext.getCmp('leaveApproveThree').setValue({id:'',text:''});
    						    		}
    						    	}
    						    }},
    						    {xtype: 'f-select',width:150,id:'leaveApproveThree',fieldLabel:'第三审批人',hiddenName:'leaveApprove3',relativeField:'organizationThree',dataUrl:'/employee/getHolidayApplyByOrganization'},
    				    	    {xtype: 'treeField',width:150,fieldLabel: '部门',id:'organizationFour',listHeight:240,hiddenName: 'organization4'
    						    ,name:'organizationFour',dataUrl : ctx+'/organization/getOrganizationTree',readOnly:false,
    						    listeners:{
    						    	select:function(combo, record, index){
    						    		if(record.id=="H-0"&&record.text=="取消选择"){
    						    			Ext.getCmp('leaveApproveFour').setValue({id:'',text:''});
    						    		}
    						    	}
    						    }},
    						    {xtype: 'f-select',width:150,id:'leaveApproveFour',fieldLabel:'第四审批人',hiddenName:'leaveApprove4',relativeField:'organizationFour',dataUrl:'/employee/getHolidayApplyByOrganization'},
    				    	    {xtype: 'treeField',width:150,fieldLabel: '部门',id:'organizationFive',listHeight:240,hiddenName: 'organization5'
    						    ,name:'organizationFive',dataUrl : ctx+'/organization/getOrganizationTree',readOnly:false,
    						    listeners:{
    						    	select:function(combo, record, index){
    						    		if(record.id=="H-0"&&record.text=="取消选择"){
    						    			Ext.getCmp('certigierFive').setValue({id:'',text:''});
    						    		}
    						    	}
    						    }},
    						    {xtype: 'f-select',width:150,fieldLabel:'授权人确认',id:'certigierFive',hiddenName:'certigier',relativeField:'organizationFive',dataUrl:'/employee/getEmployeeByOrganization'},
    						    {xtype: 'f-select',width:150,fieldLabel:'行长办公室审核',hiddenName:'bossAffirm',dataUrl:'/employee/getBoss'}
    						]
    					}]
    				},{xtype:'f-text',name:'textName',width:470,readOnly:true,value:'领导请假离行期间工作安排，需要授权人确认，请选择授权人确认！'},
    				{xtype:'f-text',name:'boss',width:470,readOnly:true,value:'假单如需行长办公室审核，请在行长办公室审核中选择审核人！'}]
				}]
			},
			url:ctx+'/holidayApply'	
		});
		HolidayApplyGrid.superclass.initComponent.call(this);
		this.getSelectionModel().on('rowselect',function(sm,rowIndex,record){
			if(record.data.bossAffirm!=""){
				Ext.getCmp('HolidayApply_bossAffirmBt').setVisible(true);
			}else{
				Ext.getCmp('HolidayApply_bossAffirmBt').setVisible(false);
			}
		},this);
	},
	virgation:function(){
		var virgationWin = new HolidayVirgation({id:'HolidayVirgation'});
		virgationWin.show();
	},
	getData:function(id){
		var taskParam = {nodeId:id};
		this.loadData(taskParam);		
	},
	cancelHoliday:function(){
		var x = this.getSelectionModel().getSelected();
		var id = x.get('id');
		this.cancelWin = new Ext.app.FormWindow({
			winConfig : {
				height : 300,
				width : 395,
				title : '销假',
				desc : '选择销假时间'
			},
			formConfig : {
				items : [
					{xtype: 'f-date',id:'cancelStartDate',fieldLabel: '开始日期',name: 'cancelStartDate',readOnly:true},
					{xtype: 'f-date',id:'cancelEndDate',fieldLabel: '结束日期',name: 'cancelEndDate',allowBlank: false},
					{xtype: 'f-text',id:'cancel_days', fieldLabel:'请假天数',name:'cancelDays',allowBlank:false},
					{xtype: 'treeField',width:150,fieldLabel: '部门',id:'organizationCancel',listHeight:240,hiddenName: 'organization'
						    ,name:'organizationCancel',dataUrl : ctx+'/organization/getOrganizationTree',readOnly:false},
					{xtype: 'f-select',width:150,fieldLabel:'第一审批人',hiddenName:'cancelApprove',allowBlank: false,relativeField:'organizationCancel',dataUrl:'/employee/getHolidayApplyByOrganization'}
				]
			},
			buttons : [{
				text: '确定',
				scope:this,
				handler : function(){
					this.cancelWin.formPanel.getForm().submit({
			            waitMsg:'保存中...',
						url:ctx+'/holidayApply/holidayCancel?id='+id,
						scope:this,
						success:function(form, action) {
							this.cancelWin.close();
							this.getData(3);
			            }
			        });
				}
			}]
		});
		this.cancelWin.show();
		var record = this.store.getById(this.selectedId);
		var startDate = record.data.startDate;
		var endDate = record.data.endDate;
		var days = record.data.leaveDays;
		Ext.getCmp('cancelStartDate').setValue(startDate);
		Ext.getCmp('cancelEndDate').setValue(endDate);
		Ext.getCmp('cancel_days').setValue(days);
//		this.cancelWin.formPanel.getForm().loadRecord(record);
	},
	bossAffirmFunction:function(){
		var record = this.store.getById(this.selectedId);
		var x = this.getSelectionModel().getSelected();
		var id = x.get('id');
		this.bossAffirmWin= new Ext.app.FormWindow({
			winConfig:{
				height : 300,
				width : 395,
				title : '请假审批',
				desc : '选择请假单审批同意或者不同意和审批意见'
			},
			formConfig:{
				items:[
				     {xtype:'f-select',fieldLabel:'分管行领导',dataUrl:'/employee/getLeader',hiddenName:'assistant',allowBlank: false,
				    	 listeners:{
					    	select:function(combo, record, index){
				    	 		if(record.data.id=="H-0"){
				    	 			Ext.getCmp('deputyGovernor').setVisible(false);
				    	 		}else{
				    	 			Ext.getCmp('deputyGovernor').setVisible(true);
				    	 		}
					    	}
					    }},
				     {xtype:'f-textarea',fieldLabel: '分管行领导意见',id:'deputyGovernor',hiddenName:'deputyGovernor',height:50,hidden:true},
				     {xtype:'f-select',fieldLabel:'分行行长',hiddenName:'leader',dataUrl:'/employee/getLeaderB',
				    	 listeners:{
					    	select:function(combo, record, index){
				    	 		if(record.data.id=="H-0"){
				    	 			Ext.getCmp('president').setVisible(false);
				    	 		}else{
				    	 			Ext.getCmp('president').setVisible(true);
				    	 		}
					    	}
					    }},
				     {xtype:'f-textarea',fieldLabel: '分行行长意见',id:'president',name:'president',height:50,hidden:true}
				]
			},
			buttons:[{
				text: '确定',
					scope:this,
					handler : function(){
						var x = this.getSelectionModel().getSelected();
						var id = x.get('id');
						var params = this.bossAffirmWin.formPanel.getForm().getValues();
						this.bossAffirmWin.formPanel.getForm().submit({           
				            waitMsg:'保存中...',
							url:ctx+'/holidayApply/holidayApproveEntering?id='+id,
							scope:this,
							success:function(form, action) {
								this.bossAffirmWin.close();
								this.getData(1);
				            }
				        });
					}
				}
			]
		});
		this.bossAffirmWin.show();
	},
	approveHandler:function(){
		var x = this.getSelectionModel().getSelected();
		var id = x.get('id');
		this.approveWin = new Ext.app.FormWindow({
			winConfig : {
				height : 250,
				width : 395,
				title : '请假审批',
				desc : '选择请假单审批同意或者不同意和审批意见'
			},
			formConfig : {
				items : [{
		 			xtype:'f-select',id:'holiday_approveStatus',hiddenName:'approveStatus',fieldLabel:'审批',
		 			data:[
		 				['AGREE','同意'],
		 				['INTENDAGREE','拟同意'],
		 				['DISAGREE','不同意']
		 			]
				},{xtype: 'f-textarea',fieldLabel: '意见',id:'suggestion',name: 'suggestion',height:50}
				]
			},
			buttons : [{
				text: '确定',
				scope:this,
				handler : function(){
					var as = this.approveWin.formPanel.getForm().getValues();
					if(as.approveStatus==""&&as.suggestion==""){
						App.msg("请选择同意或不同意，或填入审批意见");
					}else{
						this.approveWin.formPanel.getForm().submit({           
				            waitMsg:'保存中...',
							url:ctx+'/holidayApply/holidayApprove?id='+id,
							scope:this,
							success:function(form, action) {
								this.approveWin.close();
								this.getData(1);
				            }
				        });
					}
				}
			}]
		});
		this.approveWin.show();
	},
	editApplyFlowHandler:function(){
		var record = this.store.getById(this.selectedId);
		type=record.data.applyType;
		if(type=='请假'){
			if(record.data.oldOrNew=="OLD"){
				this.edit();
			}else{
				if(record.data.leaveApplyType=="employee"){
					var employeeWin = new OrganizationLeader({id:'employeeWin',
						approveList:employeeApproveList
					});
					employeeWin.show();
					employeeWin.saveType = 'update';
					employeeWin.ajaxParams = {};
					employeeWin.ajaxParams['id'] = this.selectedId;
					employeeWin.formPanel.getForm().loadRecord(record);
					this.fireEvent('afterload',this.win);
				}
				if(record.data.leaveApplyType=="branch"){
					var branchWin = new OrganizationLeader({id:'branchWin',
						approveList: brancaApproveList
					});
					branchWin.show();
					branchWin.ajaxParams = {};
					branchWin.ajaxParams['id'] = this.selectedId;
					branchWin.saveType = "update";
					branchWin.formPanel.getForm().loadRecord(record);
					this.fireEvent('afterload',this.win);
				}
				if(record.data.leaveApplyType=="leader"){
					var leaderWin = new OrganizationLeader({id:'leaderWin',
						approveList: leaderApproveList
					});
					leaderWin.show();
					leaderWin.ajaxParams = {};
					leaderWin.ajaxParams['id'] = this.selectedId;
					leaderWin.saveType = "update";
					leaderWin.formPanel.getForm().loadRecord(record);
					this.fireEvent('afterload',this.win);
				}
				
			}
		}
		if(type=='加班'){
		var as = new WorkOverTimeWin({id:'workovertimeWin'});
			Ext.getCmp('workOverTimeReason').setValue(record.data.applyReason);
			Ext.getCmp('workOverTime_employee').setValue(record.data.leaveApply);
			as.formPanel.getForm().loadRecord(record);
			as.show();
			var leaveApply = record.data.leaveApply;
			Ext.getCmp('workOverTime_employee').setValue(leaveApply);
			Ext.getCmp('workoverTime_leaveApprove1').setValue(record.data.leaveApprove1);
			Ext.getCmp('workovertimeWin').overtime = 'update';
			Ext.getCmp('workovertime_applyStatus').setValue(record.data.applyStatus.id);
			Ext.getCmp('workoverTime_operateApplyType').setValue("overTime");
			Ext.getCmp('workovertime_id').setValue(record.data.id);
		}
	},
	addWorkOverTime:function(){
		var as = new WorkOverTimeWin();
		as.show();
	},
	delApplyHandler:function(){
		this.prepareDel();
	},
	oTypeRenderer:function(v){
		alert(v.text);
		return v.text;
	},
	placeOnFile:function(){
		Ext.Ajax.request({
			url:ctx+'/holidayApply/placeOnFile',
			scope:this,
			params:{id:this.selectedId},
			success:function(response, options) {
				this.loadData({nodeId:2});
			}
		});
	},
	browseViewHandler:function(){
		var record = this.store.getById(this.selectedId);
		if(record.data.applyType=="加班"){
			var overtime = new OverTimeViewWin();
			overtime.show();
			Ext.Ajax.request({
				url : ctx + '/holidayApply/getProcessById', //URL参数是要提交到的页面
				params: {   //params是一个需要提交的参数集，使用逗号分隔
					holidayApplyId : this.selectedId
				},
				scope : this,
				success : function(response,action){  //当回调成功返回后要执行的函数
					publicProcess = Ext.util.JSON.decode(response.responseText);  //获取服务器端的回调参数值
					var approveProcesses = publicProcess.approveProcess;
//					var data = txt.data;
					var component = Ext.getCmp('OverTimeViewWin_approveProcess');
					for(var i=0;i<approveProcesses.length;i++){
						
						component.insert(i,{html:"意见："+approveProcesses[i].suggestion+"<br>"
							+"同意与否："+approveProcesses[i].approveStatus+"<br>"
							+"签名："+approveProcesses[i].leaveApprove+"<br>"
							+"日期："+approveProcesses[i].approveDate
						});
					}
					component.doLayout();
				}
			});
			overtime.formPanel.getForm().loadRecord(record);
			Ext.getCmp('overTimeView_organization').setValue(record.get('organization').text);
		}else{
			var browser = new BrowseViewWin();
			browser.show();
			Ext.Ajax.request({
				url : ctx + '/holidayApply/getProcessById',
				params: {
					holidayApplyId : this.selectedId
				},
				scope : this,
				success : function(response,action){
					publicProcess = Ext.util.JSON.decode(response.responseText);
					var approveProcesses = publicProcess.approveProcess;
					var component = Ext.getCmp('BrowseViewWin_approveProcess');
					for(var i=0;i<approveProcesses.length;i++){
						
						component.insert(i,{html:"意见："+approveProcesses[i].suggestion+"<br>"
							+"同意与否："+approveProcesses[i].approveStatus+"<br>"
							+"签名："+approveProcesses[i].leaveApprove+"<br>"
							+"日期："+approveProcesses[i].approveDate
						});
					}
					component.doLayout();
				}
			});
			browser.formPanel.getForm().loadRecord(record);
			Ext.getCmp('BrowseView_organization').setValue(record.get('organization').text);
			Ext.getCmp('BrowseView_leaveType').setValue(record.get('leaveType').text);
		}
		
		
		
	}
});

HolidayApplyTree = Ext.extend(Ext.tree.TreePanel,{	
	closable : true,
	border : true,
	initComponent : function(){
		Ext.apply(this, {
			expandable:true,
			loader: new Ext.tree.TreeLoader(),
			root: new Ext.tree.AsyncTreeNode({
				expanded: true,
	            children: [{
	            	id:1,
	                text: '我的任务列表',
	                leaf: true
	            }, {
	            	id:2,
	                text: '待审批列表',
	                leaf: true
	            }, {
	            	id:3,
	                text: '已审批通过',
	                leaf: true
	            },{
	            	id:4,
	                text: '审批未通过',
	                leaf: true
	            }]
			})
		});
		HolidayApplyTree.superclass.initComponent.call(this);									
		this.on('click',function(node,e){
			this.applyId = node.id;
			var id = node.id;
			this.listHolidayApplyList(id);
			Ext.getCmp('HolidayApply_approveBt').setVisible(false);
			Ext.getCmp('HolidayApply_editApplyBt').setVisible(false);
			Ext.getCmp('HolidayApply_delApplyBt').setVisible(false);
			Ext.getCmp('HolidayApply_placeOnFileBt').setVisible(false);
			Ext.getCmp('HolidayApply_cancelBt').setVisible(false);
			if(1==id){
				Ext.getCmp('HolidayApply_approveBt').setVisible(true);
				Ext.getCmp('HolidayApply_editApplyBt').setVisible(true);
			}
			else if(2==id){
				Ext.getCmp('HolidayApply_editApplyBt').setVisible(true);
				Ext.getCmp('HolidayApply_delApplyBt').setVisible(true);
				Ext.getCmp('HolidayApply_placeOnFileBt').setVisible(true);
			}else if(3==id){
				Ext.getCmp('HolidayApply_editApplyBt').setVisible(true);
				Ext.getCmp('HolidayApply_cancelBt').setVisible(true);
				Ext.getCmp('HolidayApply_delApplyBt').setVisible(true);
			}
			else if(4==id){
				Ext.getCmp('HolidayApply_delApplyBt').setVisible(true);
				Ext.getCmp('HolidayApply_editApplyBt').setVisible(true);
			}
		},this);
	},
	listHolidayApplyList : function(nodeId){
		var form = Ext.getCmp('searchModel').getForm().getValues();
//		var employeeId = Ext.getCmp('holiday_userName').getValue();
//		var sdate = Ext.getCmp('sdate').getValue();
//		var edate = Ext.getCmp('ddate').getValue();
//		var applyType = Ext.getCmp('select_applyType').getValue();
//		var leaveType = Ext.getCmp('search_LeaveType').getValue();
		Ext.getCmp('holidayApplyList').loadData({employeeId:form.employee,sdate:form.sdate,edate:form.edate,nodeId:nodeId,applyType:form.applyType,leaveType:form.search_LeaveType});
	}
});

HolidaySearchPanel = Ext.extend(Ext.form.FormPanel,{
	initComponent:function(){
		Ext.apply(this,{
			title:'条件查询',
			border:true,
			labelAlign: 'right',
			labelWidth:60,
			buttonAlign:'center',
			bodyStyle: 'padding: 10px',
			defaults:{
				width: 150
			},
			items:[
			    {xtype:'f-AttOrganizationEmployee',fieldLabel:'用户名',id:'holiday_userName',hiddenName: 'employee',width:120},
			    {xtype:'f-date',fieldLabel:'起始日期',id:'sdate',name:'sdate',width:120,value:'',
					listeners:{
							scope:this,
							select:function(dateObj,date){
								var ss=Ext.getCmp('ddate').getValue();
								if(ss!=""){
									if(date>ss){
										Ext.getCmp('sdate').setValue('');
										App.msg('起始日期大于结束日期');
									}
								}
							}
						}
					},
					{xtype:'f-date',fieldLabel:'结束日期',id:'ddate',name:'ddate',width:120,
					listeners:{
						scope:this,
						select:function(dateObj,date){
							var as=Ext.getCmp('sdate').getValue();
							if(as!=""){
								if(date<as){
									Ext.getCmp('ddate').setValue('');
									App.msg('结束日期小于起始日期');
								}
							}
						}
					}},
					{xtype:'f-select',hiddenName:'applyType',fieldLabel:'查询类型',width:120,id:'select_applyType',value:'holiday',
			 			data:[
			 				['holiday','请假'],
			 				['overTime','加班']
			 			],
			 			listeners : {
			 				select: function(combo, record, index) {
								var cm = Ext.getCmp('holidayApplyList').gridConfig.cm;
		 						var leaveType = cm.getIndexById('cm_leaveType');
		 						var startDate = cm.getIndexById('cm_startDate');
		 						var endDate = cm.getIndexById('cm_endDate');
		 						var startTime = cm.getIndexById('cm_workOverTimeStart');
		 						var endTime = cm.getIndexById('cm_workOverTimeEnd');
		 						var leaveDays = cm.getIndexById('cm_leaveDays');
		 						var cancelDate = cm.getIndexById('cm_cancelDate');
		 						var workOverTime = cm.getIndexById('cm_workOverTime');
		 						var eWorkOverTime = cm.getIndexById('cm_eWorkOverTimeDate');
			 					if(record.data.id=="overTime"){
			 						cm.setHidden(leaveType,true);
			 						cm.setHidden(startDate,true);
			 						cm.setHidden(endDate,true);
			 						cm.setHidden(leaveDays,true);
			 						cm.setHidden(startTime,false);
			 						cm.setHidden(endTime,false);
			 						cm.setHidden(cancelDate,true);
			 						cm.setHidden(workOverTime,false);
			 						cm.setHidden(eWorkOverTime,false);
			 						Ext.getCmp('search_LeaveType').setVisible(false);
			 					}else if(record.data.id=="holiday"){
			 						cm.setHidden(startTime,true);
			 						cm.setHidden(endTime,true);
			 						cm.setHidden(workOverTime,true);
			 						cm.setHidden(eWorkOverTime,true);
			 						cm.setHidden(leaveType,false);
			 						cm.setHidden(startDate,false);
			 						cm.setHidden(endDate,false);
			 						cm.setHidden(leaveDays,false);
			 						cm.setHidden(cancelDate,false);
			 						Ext.getCmp('search_LeaveType').setVisible(true);
			 					}
			 					Ext.getCmp('searchModel').searchHoliday();
							}
						}
					},
					{xtype: 'f-dict',fieldLabel: '请假类型',hiddenName: 'search_LeaveType',kind: 'leaveType',width:120},
					{xtype: 'button', text: '查询考勤信息',handler: this.searchHoliday,scope:this,width:100,style:'margin:40px;'}
			]
		});
		HolidaySearchPanel.superclass.initComponent.call(this);
	},
	searchHoliday:function(){
		var form = Ext.getCmp('searchModel').getForm().getValues();
		var as = Ext.getCmp('holidayApplyTreeList').applyId;
		if(!as){
			as = 1;
		}
//		var employeeId = Ext.getCmp('holiday_userName').getValue();
//		var sdate = Ext.getCmp('sdate').getValue();
//		var edate = Ext.getCmp('ddate').getValue();
//		var applyType = Ext.getCmp('select_applyType').getValue();
//		var leaveType = Ext.getCmp('search_LeaveType').getValue();
		Ext.getCmp('holidayApplyList').loadData({employeeId:form.employee,sdate:form.sdate,edate:form.edate,nodeId:as,applyType:form.applyType,leaveType:form.search_LeaveType});
	}
});

HolidayApply = Ext.extend(Ext.Panel,{
	layout:'border',
	closable: true,
	hideMode:'offsets',
	initComponent : function(){
		HolidayTypeAndSearch = Ext.extend(Ext.Panel,{
			layout:'border',
			closable: true,
			hideMode:'offsets',
			initComponent : function(){
				this.applyTreeList = new HolidayApplyTree({
					id:'holidayApplyTreeList',
					region:'north',
					height:250
				});
				this.searchModel = new HolidaySearchPanel({
					id:'searchModel',
					region:'center',
					title:'查询面板'
				});
				this.items=[this.applyTreeList,this.searchModel];
				HolidayTypeAndSearch.superclass.initComponent.call(this);
			}
			
		});
		this.holidayTypeAndSearch = new HolidayTypeAndSearch({
			id:'holidayTypeAndSearch',
			region:'west',
			title: '任务列表',
			split:true,
			collapsible: true, 
			collapseMode: 'mini',
			width: 220,
			minSize: 175,
			maxSize: 400
		});
		this.holidayApplyList = new HolidayApplyGrid({
			id:'holidayApplyList',
			funcCode: this.funcCode,
			region:'center',			
			title: '请假列表'
		});
		this.items = [this.holidayTypeAndSearch,this.holidayApplyList];
		HolidayApply.superclass.initComponent.call(this);
    },
	loadData:function(){
		this.holidayApplyList.loadData({nodeId:1});
		Ext.getCmp('HolidayApply_approveBt').setVisible(true);
	}
});

