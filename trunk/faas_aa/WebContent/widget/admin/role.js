

RoleList = Ext.extend(Ext.app.BaseFuncPanel,{
	paging:false,
	initComponent : function(){
		Ext.apply(this, {								
			gridConfig: {
				sm:new Ext.grid.RowSelectionModel({singleSelect:true}),
				cm:new Ext.grid.ColumnModel([
					new Ext.grid.RowNumberer(),
					{header: '角色名称',dataIndex:'nameCn',sortable:true},
					{header: '描述',dataIndex:'description',hidden:true}
				]),	
				storeMapping:[
					'id','nameCn','type','description','operateType'
				]
			},
			winConfig : {
				height: 260,
				desc : '新增，修改角色信息，并为角色分配权限',
				bigIconClass : 'roleIcon'
			},
			formConfig:{
				items: [
					{xtype:'f-text',fieldLabel:'角色名称',name: 'nameCn',emptyText:'请输入角色名称',allowBlank:false},
					{xtype:'f-textarea',fieldLabel:'描述',name: 'description'},
					{xtype:'f-select',hiddenName:'operateType',fieldLabel:'角色类型',value:'GENERAL',
						data:[
				 				['GENERAL','----------'],
				 				['LEAVE','请假审批人']
				 			]
				 	}
				]
			},
			url:ctx+'/role'
		});		
    	RoleList.superclass.initComponent.call(this);
		
		this.getSelectionModel().on('rowselect',function(sm,rowIndex,record){
			this.listPrivileges();
		},this); 
    },
	listPrivileges:function(){
		var priviTree = Ext.getCmp('priviList');
		priviTree.roleId = this.selectedId;		
		priviTree.loadRoot({id : this.selectedId });
	},
	prepareDel : function(){
		this.ajaxParams = {};
		var as = this.selectedRecord;
		if(as.data.type == 'system'){
			App.msg('角色为系统角色，不可删除');
		}else{
			if(this.fireEvent('beforedel') !== false){
				Ext.MessageBox.confirm('删除确认','您确实要删除该记录吗?该操作不能撤销!',this.delItem,this);
			}
		}
	}
});

PriviTree = Ext.extend(Ext.tree.TreePanel,{
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
            	baseAttrs: { uiProvider: Ext.ux.TreeCheckNodeUI },
				dataUrl :ctx+'/role/listPrivileges'
	        }),
	        root: new Ext.tree.AsyncTreeNode({ id:'0',checked:false })
		});
		
    	PriviTree.superclass.initComponent.call(this);
		
		this.getLoader().on('beforeload',function(node){
			if(!this.roleId){
				return false;
			}else	
				this.body.mask('正在加载...', 'x-mask-loading');
		},this);
		
		this.getLoader().on('load',function(node){
			this.body.unmask();
		},this);			
    },
	saveTree:function(){
		//alert(this.getChecked('id'));
		if(this.root.childNodes.length >0){
			Ext.Ajax.request({
				url:ctx+'/role/updatePrivileges',
				params: { id:this.roleId,checkedId:this.getChecked('id')},
				scope:this,
				success:function(response, options) {
					this.loadRoot(this.roleId);
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



Role = Ext.extend(Ext.Panel,{
	layout:'border',
	closable: true,
	hideMode:'offsets',
	initComponent : function(){
		this.roleList = new RoleList({
			funcCode: this.funcCode,
			region:'west',
			title: '角色列表',
			split:true,
			width: 250,
			minSize: 175,
			maxSize: 400
		});
		this.priviList = new PriviTree({
			id:'priviList',
			funcCode: this.funcCode,
			region:'center',			
			title: '权限列表'
		});
		this.items = [this.roleList,this.priviList];
    	Role.superclass.initComponent.call(this);
			
    },
	loadData:function(){
		this.roleList.loadData();
	}
		
});

