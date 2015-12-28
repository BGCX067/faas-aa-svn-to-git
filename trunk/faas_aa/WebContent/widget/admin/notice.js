
OrganizationList = Ext.extend(Ext.app.GridSelect,{
	showSaveButton : false,
	initComponent : function(){
		Ext.apply(this,{
			cm:new Ext.grid.ColumnModel([
				new Ext.grid.CheckboxSelectionModel(),
				{header: '机构名称',dataIndex:'text',width: 250,sortable:true},
				{header: '机构代码',dataIndex:'code'}
			]),	
			storeMapping:[
				'text', 'code','checked'
			],
			url:ctx+'/organization/getOrganizations'
		});
		OrganizationList.superclass.initComponent.call(this);
		
	}
});
Ext.reg('organizationList', OrganizationList);

Notice = Ext.extend(Ext.app.BaseFuncPanel,{
	initComponent : function(){
		Ext.apply(this,{
			gridConfig:{
				cm:new Ext.grid.ColumnModel([
					new Ext.grid.RowNumberer(),
					{header: '重要程度',dataIndex:'level',renderer:dictRenderer,width:100},
					{header: '公告内容',dataIndex:'contents',width:300},
					{header: '创建人',dataIndex:'createUser',renderer:dictRenderer},
					{header: '创建时间',dataIndex:'createDateTime'}
				]),	
				storeMapping:[
					'level','contents', 'createUser', 'createDateTime'
				]

			},
			winConfig : {
				height: 410, width: 700,
				bigIconClass : 'noticeIcon',
				desc : '编辑一条消息，并发往指定的机构'
			},
			formConfig:{
				items: [{
					layout: 'column',border: false,
					items : [{
						columnWidth:.5,layout: 'form',border: false,
						items : [
							{xtype: 'f-textarea',fieldLabel: '公告内容',name: 'contents',height:200,allowBlank: false},
							{xtype: 'f-dict',fieldLabel: '重要程度',hiddenName: 'level',kind:'level',allowBlank: false}
						] 
					},{
						columnWidth:.5,layout: 'anchor',border: false,height : 250,
						items : [
							{xtype : 'panel',border : false , height : 20,html : '<p style="color:grey;">请选择公告要发往的机构</p>'},
							{xtype : 'organizationList',id : 'organizationList',anchor: '0 -20'}
						]
					}]
				}]
			},
			url:ctx+'/notice'
		});
		Notice.superclass.initComponent.call(this);
		
		this.on('winshow',function(){
			if(this.saveType == 'add'){
				Ext.getCmp('organizationList').loadData();
			}else{
				Ext.getCmp('organizationList').disable();
			}
		},this);
		
		this.on('beforesave',function(){
			if(this.saveType == 'add'){
				if(!Ext.getCmp('organizationList').getSelectionModel().hasSelection()){
					App.msg('没有任务单位都选中,请至少选择一个单位');
					return false;
				}else{
					this.ajaxParams['organizations'] = Ext.getCmp('organizationList').checkedData;
				}
			}
		},this);
	}
	
});
