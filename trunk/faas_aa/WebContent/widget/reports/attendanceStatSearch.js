Ext.app.OrganizationSelect = Ext.extend(Ext.app.MultiSelectField, {
	initComponent : function(){
		this.store = new Ext.data.JsonStore({
		    url: ctx+'/organization/getOrganizations',
			root:'data',
		    fields: ['id', 'text','checked']
		});
		
		Ext.app.OrganizationSelect.superclass.initComponent.call(this);				
	}
});
Ext.reg('f-organization', Ext.app.OrganizationSelect);
AttendanceStatSearch_QueryPanel = Ext.extend(Ext.form.FormPanel,{
	hideMode: 'visibility',
	initComponent: function() {
		var firstDay = new Date().getFirstDateOfMonth();
		Ext.apply(this, {
			title: '查询条件',
			labelAlign: 'top',
			bodyStyle: 'padding: 20px',
			defaults:{
				width: 160
			},
			items: [
				{xtype: 'f-radiogroup', fieldLabel: '查询类型', allowBlank: false, items: [
						{boxLabel: '部门', name: 'searchType', inputValue: 'organization',checked: true},
						{boxLabel: '员工', name: 'searchType', inputValue: 'employee'}
					], listeners: {
						scope:this,
						change: this.selectedChanged
					}
				},
				{xtype: 'treeField',id:'AttendanceStatSearch_QueryPanel_organization',fieldLabel: '部门名称',listHeight:240,hiddenName: 'organizations',dataUrl : ctx+'/organization/AttOrganizationTree',readOnly:false},
				{xtype: 'f-AttOrganizationEmployee', id:'AttendanceStatSearch_QueryPanel_employee',fieldLabel: '员工姓名', hiddenName: 'employee',allowBlank: false,hidden:true},
				{xtype: 'f-date', id: 'AttendanceStatSearch_QueryPanel_beginDate', fieldLabel: '起始日期', name: 'beginDate',allowBlank: false,value:firstDay},
				{xtype: 'f-date', id: 'AttendanceStatSearch_QueryPanel_endDate', fieldLabel: '结束日期', name: 'endDate',allowBlank: false},
				{xtype: 'f-select',fieldLabel:'查看类型',id:'attendance_isVer',hiddenName:'isVer',value:'all',
				data:[
						['all','全部'],
		 				['ver','确认'],
		 				['notVer','未确认']
		 			]},
				//添加一个选择框
		 		{
            		xtype:'fieldset',id:'remove',layout:'column',title:'排除',Width:150,Height:50,
            		items:[
            		   {xtype: 'checkbox',boxLabel:'正常工作   ',name:'check',id:'normal',
							listeners : {
							scope:this,
							check : function(){
								var as=Ext.getCmp('notWell');
								if(Ext.getCmp('normal').checked){
									as.setValue('true');
								}else{
									as.setValue('false');
								}
							}
						}},
						{xtype:'checkbox',boxLabel:'休息',name:'check',id:'rest',
							listeners : {
								scope:this,
								check : function(){
									var as=Ext.getCmp('notRest');
									if(Ext.getCmp('rest').checked){
										as.setValue('true');
									}else{
										as.setValue('false');
									}
								}
							}
						}
            		]
            	},
				{xtype: 'f-text',name:'notRest',id:'notRest',hidden:true,value:'false'},
				{xtype: 'f-text',name:'notWell',id:'notWell',hidden:true,value:'false'},
				{xtype: 'button', text: '查询考勤信息',handler: this.displayQueryData,scope:this,width:100,style:'margin:30px;'}
			] 
		});
		
		AttendanceStatSearch_QueryPanel.superclass.initComponent.call(this);
	},	
	selectedChanged:function(radiogroup, checked) {
		if(checked.inputValue == 'employee') {
			Ext.getCmp('AttendanceStatSearch_QueryPanel_employee').setVisible(true);
			Ext.getCmp('AttendanceStatSearch_QueryPanel_organization').setVisible(false);
		}
		else {
			Ext.getCmp('AttendanceStatSearch_QueryPanel_employee').setVisible(false);
			Ext.getCmp('AttendanceStatSearch_QueryPanel_organization').setVisible(true);
		}
	},
	displayQueryData: function() {
		var grid = Ext.getCmp('AttendanceStatSearch_QueryResultPanel');
		grid.loadData(this.getForm().getValues());
	},
	computeCurrentDayAttShift:function(){
		Ext.Ajax.request({
			url:ctx+'/attShift/computeCurrentDayAttShift',
//			params: { checked : selectedIds,areaId:areaId },
			scope:this,
			success:function(response, options) {
				App.msg(options.result.msg);
			}
		});
	}
});
AttendanceStatSearch_QueryResultPanel = Ext.extend(Ext.app.BaseFuncPanel,{
//	hideMode:'visibility',
	initComponent: function() {
		//添加导出按钮
		this.params = null;
		this.exportExcel = new Ext.app.Button({
			id:'AttendanceStatSearch_Excel',
			text : '导出Excel',
	        tooltip : '导出考勤信息',
	        iconCls : 'excel',
			scope:this,
			disabled : true,
			handler:this.exportAttShifExcel
		});
		this.importExcel = new Ext.app.Button({
			id:'import_Excel',
			text : '导入Excel',
	        tooltip : '导如更新后考勤信息',
	        iconCls : 'excel',
	        privilegeCode:this.funcCode + '_import',
			scope:this,
			disabled : true,
			handler:this.importAttShifExcel
		});
		this.affirmBt = new Ext.app.Button({
			id:'AttendanceStatSearch_Affirm',
			text : '确认',
			tooltip : '确认考勤信息，确认后不能更改',
			iconCls : 'affirm',
			scope:this,
			disabled : true,
			privilegeCode:this.funcCode + '_affirm',
			handler:this.askUser
		});
		this.cancelAffirm = new Ext.app.Button({
			id:'AttendanceStatSearch_cancelAffirm',
			text:'取消确认',
			tooltip:'取消确认考勤信息',
			iconCls : 'remove',
			scope:this,
			disabled:true,
			privilegeCode:this.funcCode + '_cancelAffirm',
			handler:this.askUser
		});
		this.pushCardBt = new Ext.app.Button({
			id:'AttendanceStatSearch_pushCard',
			text:'补刷卡',
			tooltip:'不刷卡',
			iconCls:'vcard',
			scope:this,
			privilegeCode: this.funcCode + '_pushCard',
			handler:this.prepareAdd
		});
		var detailsRenderer = function(v){
			return String.format('<span id="attendanceDetails{0}" class="attendanceDetails" onmouseover="Ext.getCmp(\'AttendanceStatSearch_QueryResultPanel\').showDeatils({0})" onmouseout="Ext.getCmp(\'AttendanceStatSearch_QueryResultPanel\').hideDetails()">{1}</span>',v.id,v.status);
		};
		var affirmRender = function(v){
			if(v){
				return '<span style="color:blue">已确认</span>';
			}
			return '<span style="color:red">未确认</span>';
		}
		
		this.tips = new Ext.ToolTip({
			width:200,
			tpl:new Ext.XTemplate([
				'<tpl for=".">',
				'<div class="attendanceDetailsTip"><p>上班时间:{startTime}</p>',
	        	'<p>下班时间:{endTime}</p>'+
	        	'<p>签到时间:{checkInTime}</p>',
	        	'<p>签退时间:{checkOutTime}</p>',
	        	'<p>迟到时间:{lateTime}</p>',
	        	'<p>早退时间:{earlyTime}</p>',
	        	'<p>加班时间:{overTime}</p></div>',
				'</tpl>'
			])
		});
		
		Ext.apply(this, {
			gridConfig: {
				cm: new Ext.grid.ColumnModel([
					{header: '员工编号', dataIndex:'employeeCode',sortable:true},
					{header: '员工姓名', dataIndex:'employeeName',sortable:true},
					{header: '部门名', dataIndex: 'deptName'},
					{header: '考勤日期', dataIndex: 'attDate'},
					
					/*{header: '上班时间', dataIndex: 'startTime',hidden:true},
					{header: '下班时间', dataIndex: 'endTime',hidden:true},
					{header: '签到时间', dataIndex: 'checkInTime'},
					{header: '签退时间', dataIndex: 'checkOutTime'},
					{header: '迟到', dataIndex: 'late',hidden:true},
					{header: '早退', dataIndex: 'early',hidden:true},
					{header: '迟到时间', dataIndex: 'lateTime'},
					{header: '早退时间', dataIndex: 'earlyTime'},
					{header: '旷工与否', dataIndex: 'absent',hidden:true},
					{header: '加班时间', dataIndex: 'overTime',hidden:true},
					{header: '平日', dataIndex: 'weekday',hidden:true},
					{header: '休息日', dataIndex: 'restday',hidden:true}*/
					{header: '状态', dataIndex: 'details',width:200,renderer:detailsRenderer},
					{header: '是否确认',dataIndex:'affirm',renderer:affirmRender}
				]),
				storeMapping: [
					'employeeCode', 'employeeName','deptName', 'attDate', 'checkInTime', 'checkOutTime', 
					'startTime', 'endTime','late','early','lateTime','earlyTime','absent','overTime'
					,'weekday','restday','details','affirm'
				]
			},
			winConfig : {
				title:'补刷卡',
				iconCls:'vcard',
				desc:'当员工因特殊情况没有时人工增加一条刷卡记录',
				height: 330
			},
			formConfig:{
				items: [
					{xtype: 'f-OrganizationEmployee',id:'AttendanceStatSearch_employee',fieldLabel: '员工姓名',hiddenName: 'employee',allowBlank:false},
					{xtype: 'f-date',fieldLabel: '起始日期',id:'AttendanceStatSearch_checkInDate',name: 'checkInDate',allowBlank:false,
						listeners:{
							scope:this,
							select:function(dateObj,date){
								var ss=Ext.getCmp('endCheckInDate').getValue();
								if(date>ss){
									App.msg('起始时间大于结束时间');
								}
							}
						}
					},
					{xtype: 'f-date',fieldLabel: '结束日期',id:'AttendanceStatSearch_endCheckInDate',name: 'endCheckInDate',allowBlank:false,
						listeners:{
							scope:this,
							select:function(dateObj,date){
								var ss=Ext.getCmp('checkInDate').getValue();
								if(ss>date){
									App.msg('起始时间大于结束时间');
								}
							}
						}
					},
					{xtype:'timefield',fieldLabel:'刷卡时间',format:'H:i',increment:10,name:'checkInTime',width:230,allowBlank:false},
					{xtype: 'f-textarea',fieldLabel: '补刷卡理由',name: 'reason',allowBlank:false}
				]
			},
			buttonConfig:[
				this.exportExcel,this.importExcel,this.affirmBt,this.cancelAffirm,'->',this.pushCardBt
			],
			url: ctx + '/attShift'
		});
		
		AttendanceStatSearch_QueryResultPanel.superclass.initComponent.call(this);
	},
	prepareAdd : function(){
		this.ajaxParams = {};
		this.saveType = 'add';
		this.saveId = '';
		this.showWin();
		var as = this.selectedId;
		var record = this.store.getById(this.selectedId);
		var employeeCode = record.data.employeeCode;
		Ext.Ajax.request({
			url:ctx+'/employee/getEmployeeByCode',
				params:{employeeCode:employeeCode},
				scope:this,
				success:function(response,obj){
					var employee=Ext.util.JSON.decode(response.responseText);
					Ext.getCmp('AttendanceStatSearch_employee').setValue(employee);
				}
		});
		Ext.getCmp('AttendanceStatSearch_checkInDate').setValue(record.data.attDate);
		Ext.getCmp('AttendanceStatSearch_endCheckInDate').setValue(record.data.attDate);
	},
	saveItem : function(){
		this.win.formPanel.getForm().submit({
            waitMsg:'保存中...',
			url:ctx+'/attendanceLog/create' + urlPostPrefix,
			params: this.ajaxParams,
			scope:this,
			success:function(form, action) {
				this.closeWin();
				this.loadData();
				this.fireEvent('afteradd',form,action);
            },        	
            failure:function(form, action) {
            	this.saveBt.enable();
            }
		})
	},
	hideDetails:function(){
		this.tips.hide();
	},
	showDeatils:function(id){
		var v = Ext.getCmp('AttendanceStatSearch_QueryResultPanel').store.getById(id).data.details;
		//App.msg('222');
		this.tips.showBy('attendanceDetails'+id);
		this.tips.update(v);
	},
	getAttShifts: function(startDate,endDate,organizations,employeeName) {
		var p = {startDate:startDate,endDate:endDate,organizations:organizations,employeeName:employeeName};
		Ext.getCmp('AttendanceStatSearch_QueryResultPanel').params = p;
		this.loadData(p);
	},
	//添加exportAttShifExcel方法
	exportAttShifExcel:function(){
		var as = Ext.getCmp('AttendanceStatSearch_QueryPanel');
		var p = Ext.getCmp('AttendanceStatSearch_QueryPanel').getForm().getValues();
		var organizations = p.organizations!=null?p.organizations:"";
		var employee = p.employee!=null?p.employee:"";
		var content = 'startDate='+p.beginDate+'&endDate='+p.endDate+'&searchType='+p.searchType+'&notWell='+p.notWell
		+'&organizations='+encodeURI(encodeURI(organizations))+'&employee='+encodeURI(encodeURI(employee))+'';
		document.location = ctx+'/attShift/exportAttendance?'+content;
	},
	importAttShifExcel:function(){
	this.importWin = new Ext.app.FormWindow({
			winConfig : {
				height : 210,
				width : 395,
				title : '从Excel导入员工基本信息',
				desc : '通过Excel表格导入员工信息到员工表'
			},
			formConfig : {
				fileUpload : true,
				items : [
		 			{xtype: 'f-upload',fieldLabel: '选择导入文件',name: 'excelFile',allowBlank: false}
				]
			},
			buttons : [{
				text: '确定',
				scope:this,
				handler : function(){
					this.importWin.formPanel.getForm().submit({           
			            waitMsg:'保存中...',
						url:ctx+'/attShift/importedAttshift',
						scope:this,
						success:function(form, action) {
							this.importWin.close();
							Ext.MessageBox.show({
           						title: '批量导入温馨提示',
           						msg: action.result.msg,
					           	buttons: Ext.MessageBox.OK,
					           	icon: Ext.MessageBox.INFO
       						});
							this.loadData();
			            }
			        });
				}
			}]
		});	
		this.importWin.show();
	},
	askUser:function(btn){
		this.ajaxParams = {};
		if(this.fireEvent('beforedel') !== false){
			if(btn.iconCls=='affirm'){
				Ext.MessageBox.confirm('确认考勤记录','您确实要确认该考勤记录吗?该操作不能撤销!',this.affirmAttendanceInfo,this);
			}else if(btn.iconCls=='remove'){
				Ext.MessageBox.confirm('取消已确认考勤记录','您确实要取消以确认该考勤记录吗?该操作不能撤销!',this.cancelAffirmInfo,this);
			}
		}
	},
	affirmAttendanceInfo:function(btn){
		if(btn=='yes'){
			var p = Ext.getCmp('AttendanceStatSearch_QueryPanel').getForm().getValues();
			Ext.Ajax.request({
				url:ctx+'/attShift/affirmAttendanceInfo',
				params:p,
				scope:this,
				success:function(){
					Ext.getCmp('AttendanceStatSearch_QueryResultPanel').loadData(p);
				}
			});
		}
		
	},
	cancelAffirmInfo:function(btn){
		if(btn=='yes'){
			var p = Ext.getCmp('AttendanceStatSearch_QueryPanel').getForm().getValues();
			Ext.Ajax.request({
				url:ctx+'/attShift/cancelAffirmAttendanceInfo',
				params:p,
				scope:this,
				success:function(){
					Ext.getCmp('AttendanceStatSearch_QueryResultPanel').loadData(p);
				}
			});
		}
	}
});

AttendanceStatSearch = Ext.extend(Ext.Panel, {
	hideMode: 'visibility',
	initComponent: function() {
		Ext.apply(this, {
			closable: true,
			layout: 'border',
			items: [
				new AttendanceStatSearch_QueryPanel({
					id:'AttendanceStatSearch_QueryPanel',
					region: 'west', 
					margins:'0 5 0 0',
					width: 210
				}),
				new AttendanceStatSearch_QueryResultPanel({
					id: 'AttendanceStatSearch_QueryResultPanel',
					title:'查询结果', 
					region: 'center',
					funcCode: this.funcCode,
					autoScroll: true
				})
			]
		});
		
		AttendanceStatSearch.superclass.initComponent.call(this);
	},
	
	loadData: function() {
	}
})