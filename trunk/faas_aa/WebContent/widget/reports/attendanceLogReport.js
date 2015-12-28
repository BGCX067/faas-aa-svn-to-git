Ext.app.sYearMonthSelect = Ext.extend(Ext.form.CompositeField,{
	initComponent : function(){
    	Ext.apply(this,{
    		defaults:{
    			width:80
    		},
    		items:[
    			{xtype: 'f-year',name: 'year'},
			    {xtype: 'f-month',name: 'month'}
			]
    	})
        Ext.app.sYearMonthSelect.superclass.initComponent.call(this);
    }
});
Ext.reg('s-yearmonth', Ext.app.sYearMonthSelect);

AttendanceLogReport_QueryPanel = Ext.extend(Ext.form.FormPanel,{
	hideMode: 'visibility',
	initComponent: function() {
		var firstDay = new Date().getFirstDateOfMonth();
		Ext.apply(this, {
			title: '查询条件',
			labelAlign: 'top',
			bodyStyle: 'padding: 20px',
			defaults:{
				width: 170
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
				{xtype: 'treeField',id:'AttendanceLogReport_QueryPanel_organization',fieldLabel: '部门名称',listHeight:240,hiddenName: 'organizations',dataUrl : ctx+'/organization/AttOrganizationTree',readOnly:false},
				{xtype: 'f-AttOrganizationEmployee', id:'AttendanceLogReport_QueryPanel_employee',fieldLabel: '员工姓名', hiddenName: 'employee',allowBlank: false,hidden:true},
				{xtype: 'f-radiogroup', fieldLabel: '报表类型', allowBlank: false, items: [
						{boxLabel: '年度', name: 'reportType', inputValue: 'year'},
						{boxLabel: '月份', name: 'reportType', inputValue: 'yearmonth',checked: true},
						{boxLabel: '自定义', name: 'reportType', inputValue: 'defined'}
					], listeners: {
						scope:this,
						change: this.reportTypeSelectedChanged
					}
				},
				{xtype: 'f-year', id: 'AttendanceLogReport_QueryPanel_year', fieldLabel: '年份', name: 'ayear',format:'Y',allowBlank: false,hidden:true},				
				{xtype: 's-yearmonth', id: 'AttendanceLogReport_QueryPanel_yearmonth', fieldLabel: '月份', name: 'yearmonth',allowBlank: false},
				{xtype: 'f-date', id: 'AttendanceLogReport_QueryPanel_beginDate', fieldLabel: '起始日期', name: 'startDate',allowBlank: false,value:firstDay,hidden:true},
				{xtype: 'f-date', id: 'AttendanceLogReport_QueryPanel_endDate', fieldLabel: '结束日期', name: 'endDate',allowBlank: false,hidden:true},
				{xtype: 'button', text: '统计考勤记录',handler: this.statisticsAttShift,scope:this}
			] 
		});
		AttendanceLogReport_QueryPanel.superclass.initComponent.call(this);
	},
	selectedChanged:function(radiogroup, checked) {
		if(checked.inputValue == 'employee') {
			Ext.getCmp('AttendanceLogReport_QueryPanel_employee').setVisible(true);
			Ext.getCmp('AttendanceLogReport_QueryPanel_organization').setVisible(false);
			Ext.getCmp('verReport').setVisible(false);
			Ext.getCmp('canCelMAffirm').setVisible(false);
			Ext.getCmp('leaderAffirm').setVisible(false);
		}
		else {
			Ext.getCmp('AttendanceLogReport_QueryPanel_employee').setVisible(false);
			Ext.getCmp('AttendanceLogReport_QueryPanel_organization').setVisible(true);
			Ext.getCmp('verReport').setVisible(true);
			Ext.getCmp('canCelMAffirm').setVisible(true);
			Ext.getCmp('leaderAffirm').setVisible(true);
		}
	},
	reportTypeSelectedChanged:function(radiogroup, checked){
		Ext.getCmp('monthReport').setVisible(false);
		Ext.getCmp('canCelMAffirm').setVisible(false);
		Ext.getCmp('leaderAffirm').setVisible(false);
//		Ext.getCmp('holidayExeclInfo').setVisible(false);
		Ext.getCmp('verReport').setVisible(false);
		if(checked.inputValue == 'year') {
			Ext.getCmp('AttendanceLogReport_QueryPanel_year').setVisible(true);
			Ext.getCmp('AttendanceLogReport_QueryPanel_yearmonth').setVisible(false);
			Ext.getCmp('AttendanceLogReport_QueryPanel_beginDate').setVisible(false);
			Ext.getCmp('AttendanceLogReport_QueryPanel_endDate').setVisible(false);
			Ext.getCmp('holidayExeclInfo').setVisible(true);
		}
		else if(checked.inputValue == 'defined') {
			Ext.getCmp('AttendanceLogReport_QueryPanel_year').setVisible(false);
			Ext.getCmp('AttendanceLogReport_QueryPanel_yearmonth').setVisible(false);
			Ext.getCmp('AttendanceLogReport_QueryPanel_beginDate').setVisible(true);
			Ext.getCmp('AttendanceLogReport_QueryPanel_endDate').setVisible(true);
			Ext.getCmp('monthReport').setVisible(false);
//			Ext.getCmp('holidayExeclInfo').setVisible(false);
		}
		else{
			Ext.getCmp('AttendanceLogReport_QueryPanel_year').setVisible(false);
			Ext.getCmp('AttendanceLogReport_QueryPanel_yearmonth').setVisible(true);
			Ext.getCmp('AttendanceLogReport_QueryPanel_beginDate').setVisible(false);
			Ext.getCmp('AttendanceLogReport_QueryPanel_endDate').setVisible(false);
			Ext.getCmp('monthReport').setVisible(true);
//			Ext.getCmp('holidayExeclInfo').setVisible(false);
			Ext.getCmp('verReport').setVisible(true);
			Ext.getCmp('canCelMAffirm').setVisible(true);
			Ext.getCmp('leaderAffirm').setVisible(true);
		}
	},
	//统计考勤记录
	statisticsAttShift:function(){
//		if(!this.getForm().isValid()){
//			return;
//		}
		var grid = Ext.getCmp('AttendanceLogReport_QueryResultPanel');
//		grid.loadData(this.getForm().getValues());
		grid.getAttShifts(this.getForm().getValues());
	},
	computeCurrentMonthAttShift:function(){
		Ext.Ajax.request({
			url:ctx+'/attShift/computeCurrentMonthAttShift',
			scope:this,
			success:function(response, options) {
				App.msg(options.result.msg);
			}
		});
	}	
});

AttendanceLogReport_verReport = Ext.extend(Ext.Window,{
	initComponent:function(){
		var manName = function(v){
		return v.text;}
		Ext.apply(this,{
			layout: this.bannerPanel ? 'anchor' : 'fit',
			maximizable:false,
			buttonAlign:'center',
			resizable:true,
			title:'考勤统计确认',
			modal:true,
			constrainHeader : true,
			maxOnShow:false,
			height : 500,
			width : 1150,
			tbar:[new Ext.Toolbar.TextItem('部门：'),
				{xtype: 'treeField',fieldLabel: '部门名称',id:'logReport_Organization',listHeight:240,hiddenName: 'organizations',
					dataUrl : ctx+'/organization/AttOrganizationTree',readOnly:false,
						listeners:{
							collapse: function(combo) {
								var isAffirm = "";
								var ids = Ext.getCmp('logReport_Organization').getValue();
								if(ids==""){
									isAffirm = 'yes';
								}
								var year = Ext.getCmp('ver_Year').getValue();
								var month = Ext.getCmp('ver_Month').getValue();
								var as = Ext.getCmp('AttendanceLogReport_verReport').store.load({params:{ids:ids,year:year,month:month,isAffirm:isAffirm}});
							}
					}
				},
				new Ext.Toolbar.TextItem('年：')	,{xtype : 'f-year',width:100,id : 'ver_Year'},		
				new Ext.Toolbar.TextItem('月：'),{xtype : 'f-month',width:100,id : 'ver_Month',
					listeners:{
						select: function(combo, record, index) {
								var year = Ext.getCmp('ver_Year').getValue();
								var org = Ext.getCmp('logReport_Organization').getValue();
								var as = Ext.getCmp('AttendanceLogReport_verReport').store.load({params:{ids:org,year:year,month:record.data.field1}});
							}
					}
				},
				{xtype:'f-button',iconCls:'affirm',text:'确认',handler:this.affirmByLeader}]
		});
		this.recordCon= Ext.data.Record.create([
			'employeeCode', 'employeeName', 'deptName', 'lates', 'earlys'
			,'absents', 'lateTimes', 'earlyTimes','weekday','restday','managerAffirm','leaderAffirm'
			,'overTime','holidays','leave','sickLeave','annualLeave','homeLeave','maternityLeave','familyPlanningLeave','travel'
		]);
		this.store = new Ext.data.JsonStore({
		    url: ctx + '/attShift/attShiftStatisticsByOrganization',
			root: 'data',
	        totalProperty: 'totalCount',
	        id: 'id',
	        fields: this.recordCon
		});
		var gridConfig = new Ext.grid.GridPanel({
			store:this.store,
			width:900,
			
			cm: new Ext.grid.ColumnModel([
				{header: '姓名',dataIndex:'employeeName'},
				{header: '工号',dataIndex:'employeeCode'},
				{header: '部门名称', dataIndex: 'deptName'},
				{header: '平日', dataIndex: 'weekday',hidden:true},
				{header: '休息日', dataIndex: 'restday',hidden:true},
				{header: '病假天数', dataIndex: 'sickLeave'},
				{header: '事假天数', dataIndex: 'leave'},
				{header: '探亲假', dataIndex: 'homeLeave'},
				{header: '产假', dataIndex: 'maternityLeave'},
				{header: '计划生育假', dataIndex: 'familyPlanningLeave'},
				{header: '年假天数', dataIndex: 'annualLeave'},
				{header: '旷工天数', dataIndex: 'absents'},
				{header: '迟到次数', dataIndex: 'lates'},
				{header: '早退次数', dataIndex: 'earlys'},
				{header: '休假总天数', dataIndex: 'holidays'},
				{header: '迟到时间', dataIndex: 'lateTimes',hidden:true},
				{header: '早退时间', dataIndex: 'earlyTimes',hidden:true},
				{header: '加班时间', dataIndex: 'overTime',hidden:true},
				{header: '出差天数', dataIndex: 'travel',hidden:true},
				{header: '确认-考勤管理员',dataIndex: 'managerAffirm',renderer:manName},
				{header: '确认-领导',dataIndex:'leaderAffirm',renderer:manName}
			]),
			viewConfig : {
		        forceFit: true,
		        emptyText : '该部门考勤管理员还未确认'
		    }
		});
		this.items = [gridConfig];
//		this.store.load();
		AttendanceLogReport_verReport.superclass.initComponent.call(this);
	},
	affirmByLeader:function(){
		Ext.Msg.confirm("核实确认","你是否真的要确认考勤信息",function(btn){
			if(btn=='yes'){
				var year = Ext.getCmp('ver_Year').getValue();
				var month = Ext.getCmp('ver_Month').getValue();
				var org = Ext.getCmp('logReport_Organization').getValue();
				Ext.Ajax.request({
					url:ctx+'/attShift/affirmLogReportByLeader',
					params:{year:year,month:month,ids:org},
					scope:this,
					success:function(){
						Ext.getCmp('AttendanceLogReport_verReport').store.load({params:{ids:org,year:year,month:month}});
					}
				});
			}
		},this);
	}
});


AttendanceLogReport_QueryResultPanel = Ext.extend(Ext.app.BaseFuncPanel,{
	hideMode:'visibility',
	initComponent: function() {
		var manName = function(v){
		return v.text;}
		this.params = null;		
		Ext.apply(this, {
			gridConfig: {
				cm: new Ext.grid.ColumnModel([
					{header: '员工编号', dataIndex:'employeeCode',sortable:true},
					{header: '员工姓名', dataIndex:'employeeName',sortable:true},
					{header: '部门名称', dataIndex: 'deptName'},
					{header: '平日', dataIndex: 'weekday',hidden:true},
					{header: '休息日', dataIndex: 'restday',hidden:true},
					{header: '病假天数', dataIndex: 'sickLeave'},
					{header: '事假天数', dataIndex: 'leave'},
					{header: '探亲假', dataIndex: 'homeLeave'},
					{header: '产假', dataIndex: 'maternityLeave'},
					{header: '计划生育假', dataIndex: 'familyPlanningLeave'},
					{header: '年假天数', dataIndex: 'annualLeave'},
					{header: '旷工天数', dataIndex: 'absents'},
					{header: '迟到次数', dataIndex: 'lates'},
					{header: '早退次数', dataIndex: 'earlys'},
					{header: '休假总天数', dataIndex: 'holidays'},
					{header: '迟到时间', dataIndex: 'lateTimes',hidden:true},
					{header: '早退时间', dataIndex: 'earlyTimes',hidden:true},
					{header: '加班时间', dataIndex: 'overTime',hidden:true},
					{header: '出差天数', dataIndex: 'travel',hidden:true},
					{header: '确认人', dataIndex: 'managerAffirm',renderer:manName},
					{header: '领导确认',dataIndex: 'leaderAffirm',renderer:manName}
				]),
				storeMapping: [
					'employeeCode', 'employeeName', 'deptName', 'lates', 'earlys'
					,'absents', 'lateTimes', 'earlyTimes','weekday','restday','managerAffirm','leaderAffirm'
					,'overTime','holidays','leave','sickLeave','annualLeave','homeLeave','maternityLeave','familyPlanningLeave','travel'
				]
			},
			buttonConfig:[{
				xtype:'f-button',
				id:'monthReport',
				text : '月报表',
	            tooltip : '选择某年月输出月报表',
	            iconCls : 'excel',
				scope:this,
				privilegeCode: this.funcCode + '_monthReport',
				disabled : true,
				handler:this.exportAttShifExcel
			},{
				xtype:'f-button',
				id:'generateReport',
				text : '输出报表',
	            tooltip : '根据选择条件输出报表',
	            iconCls : 'excel',
				scope:this,
				privilegeCode: this.funcCode + '_outputRport',
				disabled : true,
				handler:this.generateReport
			},{
				xtype:'f-button',
				id:'holidayExeclInfo',
				text:'输出员工请假详细',
				tooltip : '根据某年输出年请假详细报表',
				iconCls : 'excel',
				scope:this,
				privilegeCode: this.funcCode + '_yearReport',
				disabled : true,
				handler:this.yearholidayInfoReport
			},{
				xtype:'f-button',
				id:'holidayExcelAll',
				text:'人员休病假报表',
				tooltip:'输出全行在岗人员休病假明细表',
				iconCls:'excel',
				scope:this,
				privilegeCode:this.funcCode+'_monthHolidayReport',
				disabled:true,
				handler:this.monthHolidayInfoReport
			},{
				xtype:'f-button',
				id:'yearAttendanceExcelAll',
				text:'人员考勤统计表',
				tooltip:'输出全行在岗人员考勤统计表',
				iconCls:'excel',
				scope:this,
				privilegeCode:this.funcCode+'_yearAttendanceReport',
				disabled:true,
				handler:this.yearAttendanceReport
			},{
				xtype:'f-button',
				id:'verReport',
				text:'考勤统计确认',
				tooltip:'确认每月的考勤统计',
				iconCls:'affirm',
				scope:this,
				privilegeCode:this.funcCode+'_verReport',
				disabled:true,
				handler:this.verReport
			},{
				xtype:'f-button',
				id:'canCelMAffirm',
				text:'领导确认',
				tooltip:'领导确认每月的考勤统计',
				iconCls:'affirm',
				scope:this,
				privilegeCode:this.funcCode+'_leaderAffirm',
				disabled:true,
				handler:this.leaderAffirm
			},{
				xtype:'f-button',
				id:'leaderAffirm',
				text:'取消确认',
				tooltip:'领导确认每月的考勤统计',
				iconCls:'remove',
				scope:this,
				privilegeCode:this.funcCode+'_cancelMAffirm',
				disabled:true,
				handler:this.cancelMAffirm
			}],
			listUrl:'/attShiftStatistics',
			url: ctx + '/attShift'
		});
		
		AttendanceStatSearch_QueryResultPanel.superclass.initComponent.call(this);
	},
	verReport:function(){
		Ext.Msg.confirm("核实确认","你是否真的要确认考勤信息",function(btn){
			if(btn=='yes'){
				var p = Ext.getCmp('AttendanceLogReport_QueryPanel').form.getValues();
				Ext.Ajax.request({
					url:ctx+'/attShift/affirmLogReportByManager',
					params:p,
					scope:this,
					success:function(){
						Ext.getCmp('AttendanceLogReport_QueryResultPanel').loadData(p);
					}
				});
			}
		},this);
	},
	cancelMAffirm:function(){
		Ext.Msg.confirm("核实确认","你是否真的要确认考勤信息",function(btn){
			if(btn=='yes'){
				var p = Ext.getCmp('AttendanceLogReport_QueryPanel').form.getValues();
				Ext.Ajax.request({
					url:ctx+'/attShift/removeReport',
					params:p,
					scope:this,
					success:function(){
						Ext.getCmp('AttendanceLogReport_QueryResultPanel').loadData(p);
					}
				});
			}
		},this);
	},
	leaderAffirm:function(){
		var as = new AttendanceLogReport_verReport({id:'AttendanceLogReport_verReport'});
		as.show();
	},
	getAttShifts: function(con) {
		Ext.getCmp('AttendanceLogReport_QueryResultPanel').params = con;
		this.loadData(con);f
	},
	yearholidayInfoReport:function(){
		var p = Ext.getCmp('AttendanceLogReport_QueryResultPanel').params;
		//转换为参数
		var paramsMap = '';
		for(var element in p) {
			if(paramsMap) {
				paramsMap += '&';
			}
			paramsMap += element + '=' + p[element];
		}
		document.location.href = ctx+'/attShift/exportHolidayInfo?'+paramsMap;
	},
	monthHolidayInfoReport:function(){
		var p = Ext.getCmp('AttendanceLogReport_QueryResultPanel').params;
		//转换为参数
		var paramsMap = '';
		for(var element in p) {
			if(paramsMap) {
				paramsMap += '&';
			}
			paramsMap += element + '=' + p[element];
		}
		document.location.href = ctx+'/attShift/exportMonthHolidayInfo?'+paramsMap;
	},
	exportAttShifExcel: function(){
		var p = Ext.getCmp('AttendanceLogReport_QueryResultPanel').params;
		//转换为参数
		var paramsMap = '';
		for(var element in p) {
			if(paramsMap) {
				paramsMap += '&';
			}
			paramsMap += element + '=' + p[element];
		}
		document.location.href = ctx+'/attShift/exportAttShift?'+paramsMap;
	},
	yearAttendanceReport:function(){
		var p = Ext.getCmp('AttendanceLogReport_QueryResultPanel').params;
		//转换为参数
		var paramsMap = '';
		for(var element in p) {
			if(paramsMap) {
				paramsMap += '&';
			}
			paramsMap += element + '=' + p[element];
		}
		document.location.href = ctx+'/attShift/yearAttendanceReport?'+paramsMap;
	},
	
	
	generateReport:function(){
		this.reportWin = new Ext.app.FormWindow({
			winConfig : {
				height : 640,
				width : 680,
				title : '列选择器',
				desc : '选择所需输出的列(可以拖拉或者双击进行选择)'
			},
			formConfig : {
				items : [
					{xtype: 'itemselector', fieldLabel: '列选择', name: 'columns', multiselects: [
						{width: 200, height: 500, store: new Ext.data.ArrayStore({
							data: [],
							fields: ['id', 'text']
						}), displayField: 'text', valueField: 'id'},
						{id:'selectColumns',width: 200, height: 500, store: new Ext.data.JsonStore({
							autoLoad: true,
							url: ctx + '/attShift/getColumns',
//							data:[{id:'name',text:'名称'},{id:'password',text:'密码'}],
							root: 'data',
							fields: ['id', 'text']
						}), displayField: 'text', valueField: 'id'}
					]}
				]
			},
			buttons : [{
				text: '生成报表',
				scope:this,
				handler : function(){
//					var formValues = Ext.getCmp('AttendanceLogReport_QueryResultPanel').params;
					var values = this.reportWin.formPanel.getForm().getValues();
					var p = Ext.getCmp('AttendanceLogReport_QueryResultPanel').params;
					//转换为参数
					var paramsMap = '';
					for(var element in p) {
						if(paramsMap) {
							paramsMap += '&';
						}
						paramsMap += element + '=' + p[element];
					}
					for(var element in values) {
						if(paramsMap) {
							paramsMap += '&';
						}
						paramsMap += element + '=' + values[element];
					}
					document.location.href = ctx+'/attShift/generateReport?'+paramsMap;
				}
			}]
		});
		this.reportWin.show();
	}
});
AttendanceLogReport = Ext.extend(Ext.Panel, {
	hideMode: 'visibility',
	initComponent: function() {
		Ext.apply(this, {
			closable: true,
			layout: 'border',
			items: [
				new AttendanceLogReport_QueryPanel({id:'AttendanceLogReport_QueryPanel',region: 'west', width: 210, split: true, collapsible: true, collapseMode: 'mini'}),
				new AttendanceLogReport_QueryResultPanel({id: 'AttendanceLogReport_QueryResultPanel',funcCode: this.funcCode,title:'查询结果', region: 'center' })
			]
		});
		
		AttendanceLogReport.superclass.initComponent.call(this);
	},
	
	loadData: function() {
	}
})