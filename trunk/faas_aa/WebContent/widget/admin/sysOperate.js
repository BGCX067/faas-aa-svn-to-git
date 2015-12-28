SysEmployeeTree = Ext.extend(Ext.app.BaseFuncPanel,{
	paging:false,
	initComponent : function(){
		Ext.apply(this,{
			gridConfig: {
				sm:new Ext.grid.RowSelectionModel({singleSelect:true}),
				cm:new Ext.grid.ColumnModel([
					new Ext.grid.RowNumberer(),
					{header: '用户名称',dataIndex:'name',sortable:true},
					{header: '用户名',dataIndex:'userName'}
				]),	
				storeMapping:[
					'id','name','userName'
				]
			},
			buttonConfig:[{xtype:'f-search',id:'sysqueryName',width:150}],
			winConfig : {
				height: 260,
				desc : '新增，修改角色信息，并为角色分配权限',
				bigIconClass : 'roleIcon'
			},
			url:ctx+'/user',
			listUrl:'/getUserIsSysManager'
		});
		SysEmployeeTree.superclass.initComponent.call(this);
		
		this.getSelectionModel().on('rowselect',function(sm,rowIndex,record){
			this.listPrivileges();
		},this); 
	},
	listPrivileges:function(){
		var priviTree = Ext.getCmp('operateOrganizationList');
		priviTree.organizationId = this.selectedId;		
		priviTree.loadRoot({id : this.selectedId });
	}
});

SysOrganizationTree = Ext.extend(Ext.tree.TreePanel,{
	initComponent : function(){
		Ext.apply(this, {
			animate:false,
			tbar:[
				new Ext.app.clpsAllBt({tree:this}),
				new Ext.app.expandAllBt({tree:this}),'-',
				new Ext.app.Button({
					text:'保存修改',
					iconCls:'accept',
					prililegeCode:this.funcCode+'_mod',
					scope:this,
					handler:this.saveTree
	          })
	        ],
	        loader: new Ext.tree.TreeLoader({
//	        	baseAttrs: {uiProvider: Ext.ux.TreeCheckNodeUI },
				dataUrl :ctx+'/organization/listOrganizations'
	        }),
	        root: new Ext.tree.AsyncTreeNode({ id:'0',checked:false })
		});
		SysOrganizationTree.superclass.initComponent.call(this);
		
		this.getLoader().on('beforeload',function(node){
			if(!this.organizationId){
				return false;
			}else	
				this.body.mask('正在加载...', 'x-mask-loading');
		},this);
		
		this.getLoader().on('load',function(node){
			this.body.unmask();
		},this);
		
		this.on('checkchange',function(node,checked){
			var as = node;
			var sa = checked;
			var childrens = node.childNodes;
			for(var i = 0;i<childrens.length;i++){
				var child = childrens[i];
				var attri = child.attributes;
				childrens[i].ui.toggleCheck(checked);   
				childrens[i].attributes.checked = checked;
			}
		});
	},
	saveTree:function(){
			//alert(this.getChecked('id'));
			if(this.root.childNodes.length >0){
				Ext.Ajax.request({
					url:ctx+'/user/updateOrganizations',
					params: { id:this.organizationId,checkedId:this.getChecked('id')},
					scope:this,
					success:function(response, options) {
						this.loadRoot(this.organizationId);
						setTimeout("App.msg('修改成功保存!')", 500);
					},        	
					failure:function(response, options) {
						App.msg('系统出现错误','error');
					}
				});
			}else{
				Ext.Msg.alert('操作提示', '没有选中的节点，不能提交！');
			}
		}
});

SysOperate = Ext.extend(Ext.Panel,{
	layout:'border',
	closable: true,
	hideMode:'offsets',
	initComponent : function(){
		this.employeeList = new SysEmployeeTree({
			id:'operateEmployeeList',
			funcCode: this.funcCode,
			region:'west',
			title: '员工列表',
			split:true,
			width: 250,
			minSize: 175,
			maxSize: 400
		});
		this.organizationList = new SysOrganizationTree({
			id:'operateOrganizationList',
			funcCode: this.funcCode,
			region:'center',
			title:'可操作部门'
		});
		this.items=[this.employeeList,this.organizationList];
		SysOperate.superclass.initComponent.call(this);
	},
	loadData:function(){
		this.employeeList.loadData();
	}
});

