/**
 * 区域管理
 * @class Area
 * @author liaopeng2008@gmail.com
 * 2011-01-11
 */
AreaEmployeeTree = Ext.extend(Ext.tree.TreePanel,{
	initComponent : function(){
		Ext.apply(this, {
			animate:false,			
            loader: new Ext.tree.TreeLoader({            
            	baseAttrs: { uiProvider: Ext.ux.TreeCheckNodeUI },
				dataUrl :ctx+'/area/listEmployees'
	        }),
	        root: new Ext.tree.AsyncTreeNode({ 
	        	id:'o-0',
	        	checked:false 
	        })
		});
		
    	AreaEmployeeTree.superclass.initComponent.call(this);
    	this.on('checkchange',function(node,e){
    		this.onTreeClick();
    	},this);
    },
    onTreeClick:function(){
    	this.employeeIds = [];
		var nodes = this.getChecked();
		for(var i=0;i < nodes.length;i++){
			if(nodes[i].attributes.type == 'employee'){
    			this.employeeIds.push(nodes[i].id.split('-')[1]);
    		}
		}
    }
}); 
AreaTree = Ext.extend(Ext.app.BaseFuncTree,{
	winConfig : {
		height: 310, width : 390,
		desc : '新增，修改区域的信息，添加员工到区域',
		bigIconClass : 'organizationIcon'
		
	},
	formConfig:{
		items: [
			{xtype:'f-text',fieldLabel:'区域名称',name: 'name',emptyText:'请输入区域名称',allowBlank:false},
			{xtype:'f-text',fieldLabel:'地址',name: 'address'}
		]
	},
	rootConfig: { id:'0' },	
	url:ctx+'/area',
	initComponent : function(){
		this.areaId = "";
		AreaTree.superclass.initComponent.call(this);
		this.on('click',function(node,e){
			Ext.getCmp('AreaEmployeeGridPanel_addEmployeeBt').setVisible(true);
			Ext.getCmp('AreaEmployeeGridPanel_removeBt').setVisible(true);
			this.areaId = node.id;
			if(node.id==2||node.id==3||node.id==4){
				Ext.getCmp('areaList').delBt.disable();
				if(node.id==2||node.id==3){
					Ext.getCmp('areaList').editBt.disable();
					Ext.getCmp('areaList').addBt.disable();
					Ext.getCmp('AreaEmployeeGridPanel_addEmployeeBt').setVisible(false);
					Ext.getCmp('AreaEmployeeGridPanel_removeBt').setVisible(false);
				}
			}
			this.listEmployees(node.id);
		},this);
				
	},
	listEmployees : function(nodeId){
		Ext.getCmp('area_employeeList').getData(nodeId);
	}
});

EmployeeGridPanel = Ext.extend(Ext.app.BaseFuncPanel,{
	initComponent : function(){
		this.moveEmployeesBt = new Ext.app.Button({
			id:'AreaEmployeeGridPanel_addEmployeeBt',
			text : '添加员工',
            tooltip : '添加员工给选中区域',
            iconCls : 'add',
			scope:this,
			privilegeCode: this.funcCode + '_addEmployee',
			disabled : true,
			enableOnEmpty:true,
			handler:this.moveEmployees	
		});
		this.removeBt = new Ext.app.Button({
			id:'AreaEmployeeGridPanel_removeBt',
			text : '移除员工',
            tooltip : '从该区域中移除被选中的员工',
            iconCls : 'remove',
			scope:this,
			privilegeCode: this.funcCode + '_removeEmployee',
			disabled : true,
			handler:this.removeEmployee	
		});
		var csm = new Ext.grid.CheckboxSelectionModel({singleSelect:false});
		Ext.apply(this, {
			closable: false,
			autoScroll: true,
			gridConfig: {
				cm: new Ext.grid.ColumnModel([
					csm,
					{header: '姓名', dataIndex: 'name'},
					{header:'编号',dataIndex:'code'},
					{header:'部门',dataIndex:'organization'}
				]),
				sm:csm,
				storeMapping: [
					'id','name','code','organization'
				]
			},
			buttonConfig:[
				this.moveEmployeesBt,this.removeBt
			],
			listUrl:'/getAreaEmployeesList',
			url: ctx + '/area'
		});
		
		EmployeeGridPanel.superclass.initComponent.call(this);	
		
		this.store.on('load',function(store,records,options){
			this.getSelectionModel().clearSelections();
			if(this.store.getTotalCount()<=0){
				Ext.getCmp('AreaEmployeeGridPanel_addEmployeeBt').setDisabled(false);
			}
		},this); 
	},
	getData:function(areaId){
		var areaParam = {areaId:areaId};
		this.loadData(areaParam);
	},
	moveEmployees:function(){
		this.assignEmployeeWin = new Ext.app.FormWindow({
			winConfig : {
				height : 600,
				width : 395,
				title : '选择员工',
				desc : '根据部门来选择员工'
			},
			formConfig : {
				items : [
					new AreaEmployeeTree({
						id:'AreaEmployeeTree',
						height:450
					})
				]
			},
			buttons : [{
				text: '确定',
				scope:this,
				handler : function(){
					var employeeIds = Ext.getCmp('AreaEmployeeTree').employeeIds;
					var node = Ext.getCmp('areaList').getSelectionModel().getSelectedNode();
					var areaId = node.id;
					if(!areaId||!employeeIds||employeeIds.length==0){
						App.msg("没有选择员工，请先选择");
						return;
					}
					this.assignEmployeeWin.formPanel.getForm().submit({           
			            waitMsg:'保存中...',
						url:ctx+'/area/assignArea',
						scope:this,
						params: { checked : employeeIds,areaId:areaId},
						success:function(form, action) {
							this.assignEmployeeWin.close();
							Ext.getCmp('area_employeeList').getData(areaId);
			            }
			        });
				}
			}]
		});
		this.assignEmployeeWin.show();
	},
	removeEmployee:function(){
		var selectedIds = [];
		var records = this.getSelectionModel().getSelections();
		if(records.length <= 0){
			App.msg("没有选择任何记录，请选择一个或者多个记录");
			return;
		}
		for(i in records){
			if(records[i].id)
				selectedIds.push(records[i].id);
		}
		var areaId = Ext.getCmp('areaList').areaId;			
		Ext.Ajax.request({
			url:this.url+'/removeAreaEmployees',
			params: { checked : selectedIds,areaId:areaId },
			scope:this,
			success:function(response, options) {
				this.loadData();
			}
		});
	}
});

Area = Ext.extend(Ext.Panel,{
	layout:'border',
	closable: true,
	hideMode:'offsets',
	initComponent : function(){
		this.areaList = new AreaTree({
			funcCode: this.funcCode,
			id:'areaList',
			region:'west',
			title: '区域列表',
			split:true,
			collapsible: true, 
			collapseMode: 'mini',
			width: 220,
			minSize: 175,
			maxSize: 400
		});
		this.area_employeeList = new EmployeeGridPanel({
			id:'area_employeeList',
			funcCode: this.funcCode,
			region:'center',			
			title: '员工列表'
		});
		this.items = [this.areaList,this.area_employeeList];
		Area.superclass.initComponent.call(this);
			
    },
	loadData:function(){
		this.areaList.loadData();
	}
		
})