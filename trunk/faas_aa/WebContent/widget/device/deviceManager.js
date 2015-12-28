
DeviceManager = Ext.extend(Ext.app.BaseFuncPanel,{
	initComponent : function(){
		this.uploadBt = new Ext.app.Button({
			id:'DeviceManager_uploadBt',
			text : '上传用户',
            tooltip : '上传用户到对应区域对应考勤机',
            iconCls : 'upload',
            privilegeCode: this.funcCode + '_uploadEmployee',
			scope:this,
			handler:this.uploadEmployeeHandler	
		});
		
		var areaRenderer = function(v){
			return v.text;
		}
		var statusRenderer = function(v){			
			return v==1?'<span style="color:blue">在线</span>':'<span style="color:red">离线</span>';
		}
		Ext.apply(this,{
			gridConfig:{
				cm:new Ext.grid.ColumnModel([
					new Ext.grid.RowNumberer(),
//					{header: '区域名',dataIndex:'area',renderer:areaRenderer},
					{header: '考勤机序列号',dataIndex:'code'},
					{header: 'ip地址',dataIndex:'ip'},
					{header: '型号',dataIndex:'model'},
					{header: '安装位置',dataIndex:'location'},
					{header: '考勤机人员人数',dataIndex:'employeeCount'},
					{header: '考勤记录数',dataIndex:'checkCount'},
					{header: '最后活动时间',dataIndex:'lastActiveTime'},
					{header: '状态',dataIndex:'status',renderer:statusRenderer}
				]),	
				storeMapping:[
					'id','area','ip','port','model','employeeCount','checkCount','lastActiveTime','status','code','location'
				]
			},
			winConfig : {
				height: 330
			},
			buttonConfig:['edit','del','-',this.uploadBt],
			formConfig:{
				items: [
					{xtype: 'f-text',fieldLabel: 'IP地址',name: 'ip',allowBlank: false,readOnly:true},
					{xtype: 'f-text',fieldLabel: '序列号',name: 'code',allowBlank: false,readOnly:true},
					{xtype: 'f-text',fieldLabel: '安装位置',name: 'location'},
					{xtype: 'f-text',fieldLabel: '型号',name: 'model',value:'M880'}
				]
			},
			url:ctx+'/device'	
		});
		DeviceManager.superclass.initComponent.call(this);
	},
	uploadEmployeeHandler:function(){
		var x = this.getSelectionModel().getSelected();
		var id = x.get('id');
		Ext.Ajax.request({
			url:this.url+'/uploadEmployees',
			params: { id:id },
			scope:this,
			success:function(response, options) {
			}
		});
	}
});
