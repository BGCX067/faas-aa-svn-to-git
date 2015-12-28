
/**
 * 根据用户选择角色
 * 
 */
Ext.app.RoleSelect = Ext.extend(Ext.app.MultiSelectField, {
	initComponent : function(){	
		
		this.store = new Ext.data.JsonStore({
		    url: ctx+'/role/getRolesByUser',
			root:'data',
		    fields: ['id', 'text','checked']			
		});
		
		Ext.app.RoleSelect.superclass.initComponent.call(this);
		
		this.store.on('beforeload',function(store,o){
			this.store.baseParams['id'] = Ext.getCmp('User').saveId;
		},this);
	}
});
Ext.reg('f-roleByUser', Ext.app.RoleSelect);

User = Ext.extend(Ext.app.BaseFuncPanel,{
	
	initComponent : function(){
		var rolesRender = function(v){
			var re = [];
			for(var r in v){
				if(v[r].text){
					re.push(v[r].text);
				}		
			}
			return re.join(',');
		}	
		var lockedRender = function(v){		
			return v == true ?'<span style="color:red">已锁定</span>' : '';
		}
		var roleRenderer = function(v){
			return v.text;
		}
		Ext.apply(this,{
			url:ctx+'/user',
			gridConfig: {
				cm:new Ext.grid.ColumnModel([
					new Ext.grid.RowNumberer(),
					{header: '用户名',dataIndex:'userName',sortable:true},
					{header: '用户显示名',dataIndex:'userDisplayName',sortable:true},
					{header: '对应员工',dataIndex:'employee',renderer : dictRenderer},
					{header: '最后登陆时间',dataIndex:'lastLoginTime',width:150},
					{header: '所属部门',dataIndex:'organization',renderer:roleRenderer},
					{header: '所属角色',dataIndex:'role',renderer:rolesRender},
//					{header: '所属角色',dataIndex:'role',renderer:dictRenderer,width:200},
					{header: '锁定',dataIndex:'locked',renderer:lockedRender}
				]),	
				storeMapping:[
					'id','userName','userDisplayName','employee','lastLoginTime','role','organization','locked'
				]
			},
			winConfig : {
				height: 440, width : 405,
				desc : '为员工分配用户名，设置密码，并分配角色',
				bigIconClass : 'userIcon'
			},
			formConfig:{
				items: [
					{xtype:'fieldset',title: '对应员工',autoHeight:true,
						items :[
							{xtype: 'f-employee', id:'employeeSelect',fieldLabel: '员工姓名', storeFields:['id','text','code','orgName'],hiddenName: 'employee',allowBlank: false,emptyText:'请选择一个员工',listeners: {}}
						]
					},
					{xtype:'fieldset',title: '登陆信息',autoHeight:true,
						items :[
							{xtype: 'f-text',fieldLabel: '用户名',id: 'userName',name: 'userName',allowBlank: false},
							{xtype: 'f-text',fieldLabel: '用户显示名',id: 'userDisplayName',name: 'userDisplayName',allowBlank: false},
							{xtype:'panel',id:'passwordPanel',autoHeight:true,border:false,layout:'form',
								 items :[
									{xtype: 'f-text',fieldLabel: '密码',id:'pswd',name: 'password',inputType:'password',allowBlank: false},
									{xtype:'f-text',fieldLabel:'确认密码',id:'pswdComfirm',name:'password2',inputType:'password',vtype: 'password',initialPassField: 'pswd',allowBlank: false}
								]
							},
							{xtype:'panel',id:'resetPanel',autoHeight:true,border:false, buttonAlign:'center',hidden:true,
								 buttons :[
									{xtype:'f-button',text: '重设密码',iconCls:'key',scope:this,handler:this.resetPassword}			
								]
							}
						]
					},
					{xtype:'fieldset',title: '选择用户角色',autoHeight:true,
						items :[
							{xtype:'f-roleByUser',fieldLabel: '用户角色',hiddenName:'role',emptyText: '请选择一个用户角色',allowBlank: false}
						]
					}
				]
			},
			buttonConfig : ['all','-',{
				text:'锁定',
				iconCls:'lock',
				tooltip:'锁定所选的用户',
				id:'lockUserBt',
				prililegeCode:this.funcCode+'_lock',
				scope:this,
				handler:this.lockUser
			},{
				text:'解锁',
				iconCls:'key',
				tooltip:'解除所选的用户的锁定',
				id:'unlockUserBt',
				prililegeCode:this.funcCode+'_lock',
				scope:this,
				handler:this.lockUser,
				hidden:true
			},
			{
				text:'导入用户',
				iconCls:'excel',
				tooltip:'导入用户信息',
				id:'importUser',
				prililegeCode:this.funcCode+'_importUser',
				scope:this,
				handler:this.importUser
			},
			'-',{
					xtype:'f-button',
					id:'excelReportUserRole',
					text:'导出excel',
					tooltip : '导出用户角色相关信息',
					iconCls : 'excel',
					scope:this,
					disabled:true,
					hidden:true,
					handler:this.exportUserRoleExcel
			},'->',{
				xtype : 'f-search',
				id:'queryName',
				emptyText : '请输入用户名或者用户显示名'
			},'->',{
				xtype: 'f-select',
				dataUrl:'/role/getRoles',
				storeFields:['id','text','code'],
				hiddenName: 'role',
				id:'roleType',
				width:160,
				emptyText:'请选择一个用户角色',
				listeners : {
					select: function(combo, record, index) {
						Ext.getCmp('excelReportUserRole').setVisible(true);
						Ext.getCmp('User').loadData({type:record.data.id});
						
					}
				}
			},'-']
		});
		User.superclass.initComponent.call(this);
		
		this.getSelectionModel().on('rowselect',function(sm,rowIndex,record){
			var flag = sm.getSelected().data.locked;
			Ext.getCmp('lockUserBt').setVisible(!flag);
			Ext.getCmp('unlockUserBt').setVisible(flag);
		},this); 
				
		this.on('winshow',function(grid){
			if(this.saveType == 'update'){
				Ext.getCmp('passwordPanel').setVisible(false).setDisabled(true);
				Ext.getCmp('resetPanel').setVisible(true).setDisabled(false);
				Ext.getCmp('pswd').setDisabled(true);
				Ext.getCmp('pswdComfirm').setDisabled(true);
				Ext.getCmp('employeeSelect').setReadOnly(true);
				Ext.getCmp('userName').setReadOnly();
			}
			Ext.getCmp('employeeSelect').on('select',function(combo,record,index){
				Ext.getCmp('userName').setValue(record.data.code);
				Ext.getCmp('userDisplayName').setValue(record.data.text);
			
			},this);
			Ext.getCmp('employeeSelect').selectGridItem = function(){
				var employeeSelect = Ext.getCmp('employeeSelect');
				var record = employeeSelect.grid.getSelectionModel().getSelected();
				if(record){
					this.setValue({
						id : record.id,
						text : record.data.text
					});
					Ext.getCmp('userName').setValue(record.data.code);
					Ext.getCmp('userDisplayName').setValue(record.data.text);
					this.win.close();
				}
			}
		},this);
		
	},	
	resetPassword : function(){
		this.resetWin = new Ext.app.FormWindow({
			iconCls : 'key',
			winConfig : {
				height : 210,
				width : 395,
				title : '重设密码',
				desc : '将旧密码作废,重设用户的新密码',
				bigIconClass : 'resetKeyIcon'
			},
			formConfig : {
				items : [
		 			{xtype: 'f-text',fieldLabel: '密码',id:'pswd2',name: 'password',inputType:'password',allowBlank: false},
					{xtype:'f-text',fieldLabel:'确认密码',id:'pswdComfirm2',name:'password2',inputType:'password',vtype: 'password',initialPassField: 'pswd2',allowBlank: false}
				]
			},
			buttons : [{
				text: '确定',
				scope:this,
				handler : function(){
					this.resetWin.formPanel.getForm().submit({           
			            waitMsg:'保存中...',
						url:this.url+'/resetPassword',
						params: { id :this.getSelectionModel().getSelected().id },
						scope:this,
						success:function(form, action) {
							this.resetWin.close();
							App.msg("密码设置成功！");
			            }
			        });
				}
			}]
		});
		this.resetWin.show();
	},
	lockUser : function(){
		Ext.Ajax.request({
			url:this.url+'/lockUser',
			params: {id:this.getSelectionModel().getSelected().id},
			scope:this,
			success:function(response, options){
				this.loadData();
			}
		});
	},
	//添加导出考勤管理员信息方法
	exportUserRoleExcel : function(){
		var p = Ext.getCmp('User').params;
		//转换为参数\
		var as = Ext.getCmp('queryName');
		var qn = Ext.getCmp('queryName').el.dom.value;
		var queryName = qn==as.emptyText?"":qn;
		var roleType = Ext.getCmp('roleType').value;
		roleType = roleType==null?"":roleType;
		var paramsMap = 'roleType='+roleType+'&'+"queryName="+queryName;
		
		document.location.href = ctx+'/user/exportUserRole?'+paramsMap;
	},
	importUser:function(){
		this.importWin = new Ext.app.FormWindow({
			winConfig : {
				height : 210,
				width : 395,
				title : '从Excel导入员工基本信息',
				desc : '通过Excel表格导入用户信息到用户表'
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
						url:ctx+'/user/importUser',
						scope:this,
						success:function(form, action) {
							this.importWin.close();
							Ext.MessageBox.show({
           						title: '用户导入提示',
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
	}
});
