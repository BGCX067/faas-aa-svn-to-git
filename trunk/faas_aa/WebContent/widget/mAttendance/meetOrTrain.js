MeetOrTrainList=Ext.extend(Ext.app.BaseFuncPanel,{
	initComponent : function(){
		veriftyRenderer = function(v,mateDate,record){
			if(v==true){
				return '<span style="color:blue">确认</span>';
			}
			return '<span style="color:red">未确认<font color:"blue">('+record.data.verifyOr+')</font></span>';
		};
		Ext.apply(this,{
			gridConfig: {
				sm:new Ext.grid.RowSelectionModel({singleSelect:true}),
				cm:new Ext.grid.ColumnModel([
					new Ext.grid.RowNumberer(),
					{header: '会议/培训名称',dataIndex:'name',sortable:true},
					{header: '是否确认', dataIndex:'isVerify',renderer:veriftyRenderer}
				]),
				storeMapping:['id','name','address','theme','organization','isVerify','verifyOr']
			},
			winConfig : {
				height: 400, width : 430,
				desc : '新增，修改会议/培训的信息，添加员工到会议/培训'
			},
			buttonConfig : [{xtype:'button',id:'verifyButton',text:'确认',iconCls:'accept',tooltip : '确认会议/培训',handler:this.affirmMeet},
			                {xtype:'button',id:'exportExcel',text:'统计报表',iconCls:'excel',tooltip : '导出查询出的报表',handler:this.exportExcel}],
			url:ctx+'/meetOrTrain'
		}),
		MeetOrTrainList.superclass.initComponent.call(this);
		this.getSelectionModel().on('rowselect',function(sm,rowIndex,record){
			this.areaId = this.selectedId;
			if(record.data.isVerify==true || loginUser.userName!=record.data.verifyOr){
				Ext.getCmp('verifyButton').setDisabled(true);
			}else{
				Ext.getCmp('verifyButton').setDisabled(false);
			}
			Ext.getCmp('attendanceLogList').loadData({meetId:this.areaId});
		},this);
	},
	affirmMeet:function(){
		var areaId=Ext.getCmp('areaList').selectedId;
		Ext.Ajax.request({
			url:ctx+'/meetOrTrain/update',
			params:{areaId:areaId},
			scope:this,
			success:function(response, options){
				Ext.getCmp('areaList').loadData();
			}
		});
	},
	exportExcel:function(){
		var p = Ext.getCmp('Meet_searchModel').getForm().getValues();
		var content = 'sdate='+p.sdate+'&edate='+p.ddate+'&organization='+p.organization+'';
		document.location = ctx+'/meetOrTrain/exportExcel?'+content;
	}
});

MeetOrTrainSearchPanel = Ext.extend(Ext.form.FormPanel,{
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
				{xtype: 'treeField',fieldLabel: '部门',listHeight:240,id:'Meet_organization',hiddenName: 'organization',name:'organization',dataUrl : ctx+'/organization/getOrganizationTree',readOnly:false},
				{xtype:'f-date',fieldLabel:'起始日期',id:'Meet_sdate',name:'sdate',width:150,value:'',
					listeners:{
						scope:this,
						select:function(dateObj,date){
							var ss=Ext.getCmp('Meet_ddate').getValue();
							if(ss!=""){
								if(date>ss){
									Ext.getCmp('Meet_sdate').setValue('');
									App.msg('起始日期大于结束日期');
								}
							}
						}
					}
				},
				{xtype:'f-date',fieldLabel:'结束日期',id:'Meet_ddate',name:'ddate',width:150,
					listeners:{
						scope:this,
						select:function(dateObj,date){
							var as=Ext.getCmp('Meet_sdate').getValue();
							if(as!=""){
								if(date<as){
									Ext.getCmp('Meet_ddate').setValue('');
									App.msg('结束日期小于起始日期');
								}
							}
						}
					}
				},
				{xtype: 'button', text: '查询会议/培训信息',handler: this.searchMeetOrTrain,scope:this,width:100,style:'margin:40px;'}
			]
		});
		MeetOrTrainSearchPanel.superclass.initComponent.call(this);
	},
	searchMeetOrTrain:function(){
		var p = Ext.getCmp('Meet_searchModel').getForm().getValues();
		Ext.getCmp('areaList').loadData(p);
	}
});

AttendanceLogByMeet=Ext.extend(Ext.app.BaseFuncPanel,{
	initComponent : function(){
		verifyRenderer = function(v){
			if(v==false)
				return '<span style="color:red">未确认</span>';
			else
				return '<span style="color:blue">确认</span>';
		};
//		var csm = new Ext.grid.CheckboxSelectionModel({singleSelect:false});
		statusRenderer = function(v,mateDate,record){
			if(record.data.attPlus==true){
				return String.format('<span class="plusCard" title="{0}">{1}</span>补刷',record.data.plusInfo,record.data.status);
			}else{
				return v;
			}
		};
		Ext.apply(this,{
			gridConfig : {
				cm:new Ext.grid.ColumnModel([
					{header: '员工姓名',dataIndex:'name'},
					{header: '员工编号',dataIndex:'code'},
					{header: '部门',dataIndex:'organization'},
					{header: '打卡日期',dataIndex:'attDate'},
					{header: '状态',dataIndex:'status',renderer:statusRenderer}
//					{header: '是否确认',dataIndex:'verify',renderer:verifyRenderer}
				]),
//				sm:csm,
				storeMapping : ['id','name','code','organization','attDate','plusInfo','status','attPlus']
			},
			buttonConfig:[],
			url:ctx+'/moveAttInfo'	
		});
		AttendanceLogByMeet.superclass.initComponent.call(this);
	}
});

MeetOrTrain=Ext.extend(Ext.Panel,{
	layout:'border',
	closable: true,
	hideMode:'offsets',
	initComponent : function(){
		MeetListAndSearchPanel = Ext.extend(Ext.Panel,{
			layout:'border',
			closable: true,
			hideMode:'offsets',
			initComponent : function(){
				this.searchModel = new MeetOrTrainSearchPanel({
					id:'Meet_searchModel',
					region:'center',
					title:'查询面板'
				});
				this.MeetOrTrainList=new MeetOrTrainList({
					funcCode: this.funcCode,
					id:'areaList',
					region:'north',
					height:300
				});
				
				this.items=[this.searchModel,this.MeetOrTrainList];
				MeetListAndSearchPanel.superclass.initComponent.call(this);
			}
		});
		this.MeetOrTrainListAndSearch=new MeetListAndSearchPanel({
			funcCode: this.funcCode,
			id:'areaList',
			region:'west',
			title: '会议/培训列表',
			width:240,
			minSize: 240,
			maxSize: 240,
			split:true,
			collapsible: true, 
			collapseMode: 'mini'
		});
		this.AttendanceLogList=new AttendanceLogByMeet({
			funcCode : this.funcCode,
			id:'attendanceLogList',
			region:'center',
			title:'会议/培训考勤信息'
		});
		this.items=[this.MeetOrTrainListAndSearch,this.AttendanceLogList];
		MeetOrTrain.superclass.initComponent.call(this);
	},
	loadData:function(){
		Ext.getCmp('areaList').loadData();
	}
});
