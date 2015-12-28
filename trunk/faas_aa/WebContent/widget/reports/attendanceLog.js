AttendanceLog_QueryPanel = Ext.extend(Ext.form.FormPanel,{
	hideMode: 'visibility',
	initComponent: function() {
		var firstDay = new Date().getFirstDateOfMonth();
		Ext.apply(this, {
			title: '查询条件',
			labelAlign: 'top',
			bodyStyle: 'padding: 20px',
			layout:'form',
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
				{xtype: 'treeField',id:'AttendanceLog_QueryPanel_organization',fieldLabel: '部门名称',listHeight:240,hiddenName: 'organizations',dataUrl : ctx+'/organization/AttOrganizationTree',readOnly:false},
				{xtype: 'f-AttOrganizationEmployee', id:'AttendanceLog_QueryPanel_employee',fieldLabel: '员工姓名', hiddenName: 'employee',allowBlank: false,hidden:true},
				{xtype: 'f-date', id: 'AttendanceLog_QueryPanel_beginDate', fieldLabel: '起始日期', name: 'beginDate',allowBlank: false,value:firstDay},
				{xtype: 'f-date', id: 'AttendanceLog_QueryPanel_endDate', fieldLabel: '结束日期', name: 'endDate',allowBlank: false},
				{xtype: 'button', text: '原始考勤数据查询',handler: this.displayQueryData,scope:this,width:100,style:'margin:30px;'}
			] 
		});
		
		AttendanceLog_QueryPanel.superclass.initComponent.call(this);
	},	
	selectedChanged:function(radiogroup, checked) {
		if(checked.inputValue == 'employee') {
			Ext.getCmp('AttendanceLog_QueryPanel_employee').setVisible(true);
			Ext.getCmp('AttendanceLog_QueryPanel_organization').setVisible(false);
		}
		else {
			Ext.getCmp('AttendanceLog_QueryPanel_employee').setVisible(false);
			Ext.getCmp('AttendanceLog_QueryPanel_organization').setVisible(true);
		}
	},
	displayQueryData: function() {
		var grid = Ext.getCmp('AttendanceLog_QueryResultPanel');
		grid.loadData(this.getForm().getValues());
	}
//	computeCurrentDayAttShift:function(){
//		Ext.Ajax.request({
//			url:ctx+'/attShift/computeCurrentDayAttShift',
////			params: { checked : selectedIds,areaId:areaId },
//			scope:this,
//			success:function(response, options) {
//				App.msg(options.result.msg);
//			}
//		});
//	}
});


AttendanceLog_QueryResultPanel = Ext.extend(Ext.app.BaseFuncPanel,{
	hideMode:'visibility',
	initComponent : function(){
		
		this.plusCardBt = new Ext.app.Button({
			xtype:'f-button',
			text:'补刷卡',
			iconCls:'vcard',
			scope:this,
			privilegeCode: this.funcCode + '_prepareAdd',
			handler:this.prepareAdd
		});
		
		var checkInTimeRenderer = function(v,metaData, record){
			if(record.data.plusCardMan!=""){
				return String.format('<span class="plusCard" title="补刷原因:{0}">{1}补刷</span>{2}',record.data.reason,record.data.plusCardMan.text,record.data.checkInTime);
			}else{
				return v;
			}
		};
		Ext.apply(this,{
			gridConfig:{
				cm:new Ext.grid.ColumnModel([
					new Ext.grid.RowNumberer(),
					{header: '员工姓名',dataIndex:'employeeName'},
					{header: '员工编号',dataIndex:'employeeCode'},
					{header: '卡号',dataIndex:'cardNo'},
					{header: '打卡日期',dataIndex:'attDate'},
					{header: '刷卡时间',dataIndex:'checkInTime',renderer:checkInTimeRenderer},
					{header: '打卡地点',dataIndex:'pushSite',hidden:true}
				]),	
				storeMapping:[
					'employeeCode','cardNo','employeeName','attDate','checkInTime','plusCardMan','reason','pushSite'
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
					{xtype: 'f-OrganizationEmployee',fieldLabel: '员工姓名',hiddenName: 'employee',allowBlank:false},
					{xtype: 'f-date',fieldLabel: '起始日期',id:'checkInDate',name: 'checkInDate',allowBlank:false,
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
					{xtype: 'f-date',fieldLabel: '结束日期',id:'endCheckInDate',name: 'endCheckInDate',allowBlank:false,
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
//				{xtype:'f-button',text:'补刷卡',iconCls:'vcard',scope:this,privilegeCode: this.funcCode + '_prepareAdd',handler:this.prepareAdd}
				this.plusCardBt
			],
			url:ctx+'/attendanceLog'	
		});
		AttendanceLog_QueryResultPanel.superclass.initComponent.call(this);
	}
	
});

AttendanceLog= Ext.extend(Ext.Panel, {
	hideMode: 'visibility',
	initComponent: function() {
		Ext.apply(this, {
			closable: true,
			layout: 'border',
			items: [
				new AttendanceLog_QueryPanel({
					id:'AttendanceLog_QueryPanel',
					region: 'west', 
					margins:'0 5 0 0',
					width: 210
				}),
				new AttendanceLog_QueryResultPanel({
					id: 'AttendanceLog_QueryResultPanel',
					title:'原始数据', 
					region: 'center',
					autoScroll: true,
					scope:this,
					funcCode:this.funcCode
				})
			]
		});
		
		AttendanceLog.superclass.initComponent.call(this);
	},
	
	loadData: function() {
	}
})
