
TimePeriod = Ext.extend(Ext.app.BaseFuncPanel,{
	initComponent : function(){
		Ext.apply(this,{
			gridConfig:{
				cm:new Ext.grid.ColumnModel([
					new Ext.grid.RowNumberer(),
					{header: '开始日期',dataIndex:'startTime'},
					{header: '结束日期',dataIndex:'endTime'},
					{header: '允许偏差checkIn时间',dataIndex:'toleranceStartMinute'},
					{header: '允许偏差checkOut时间',dataIndex:'toleranceEndMinute'}
				]),	
				storeMapping:[
					'startTime','endTime','toleranceStartMinute','toleranceEndMinute'
				]
			},
			winConfig : {
				height: 330
			},
			formConfig:{
				items: [
					{xtype: 'f-date',fieldLabel: '开始日期',name: 'startTime',allowBlank: false},
					{xtype: 'f-date',fieldLabel: '结束日期',name: 'endTime',allowBlank: false},
					{xtype: 'f-number',fieldLabel: '允许偏差checkIn时间',name: 'toleranceStartMinute'},
					{xtype: 'f-number',fieldLabel: '允许偏差checkOut时间',name: 'toleranceEndMinute'}
 
				]
			},
			url:ctx+'/timePeriod'	
		});
		TimePeriod.superclass.initComponent.call(this);
	}
	
});
