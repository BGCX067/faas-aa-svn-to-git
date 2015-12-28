Ext.app.AreaSelect = Ext.extend(Ext.app.MultiSelectField, {
	initComponent : function(){	
		
		this.store = new Ext.data.JsonStore({
		    url: ctx+'/area/getAreasByEmployee',
			root:'data',
		    fields: ['id', 'text','checked']			
		});
		
		Ext.app.AreaSelect.superclass.initComponent.call(this);
		
		this.store.on('beforeload',function(store,o){
			this.store.baseParams['id'] = Ext.getCmp('Employee').saveId;
		},this);
	}
});
Ext.reg('f-areaByEmployee', Ext.app.AreaSelect);

function okCloseJsWin(flexObject){
	var photoId = flexObject.photoId;
	var o = Ext.getCmp('photoField');
	o.setValue(photoId);
	o.uploadWin.close();
}
function cancelCloseJsWin(){
	var o = Ext.getCmp('photoField');
	o.uploadWin.close();
}

PhotoField = Ext.extend(Ext.form.Field, {
	noPhotoUrl : ctx+'/include/image/nophoto3.gif',
	photoUrlPrifix : ctx+'/employee/photo?photoId=',
    actionMode: 'wrap',
    initComponent : function() {
        this.photoArea = new Ext.Panel({
			border : false,
	    	html : ''
		});
		this.photoAreaTpl = new Ext.XTemplate(
			'<div class="photo"><img src="{url}" /></div>'
		);
        this.photoPanel = new Ext.Panel({
        	border : false,
        	items : [this.photoArea,{
				autoHeight:true,
				border:false,
				items :[{
					xtype:'f-button',
					text: '设置相片',
					style : 'margin:4 0 0 28;',
					scope:this,
					handler:this.setPhoto	
				}]
			}]
        });
        PhotoField.superclass.initComponent.call(this);
    },    
    
    onRender : function(ct, position){
        this.autoCreate = {
            id: this.id,
            name: this.name,
            type: 'hidden',
            tag: 'input'    
        };
        PhotoField.superclass.onRender.call(this, ct, position);
        this.wrap = this.el.wrap();
        this.photoPanel.render(this.wrap);
        this.photoAreaTpl.overwrite(this.photoArea.body,{url:this.noPhotoUrl});
    },
    
    beforeDestroy : function(){
        Ext.destroy(this.photoPanel);
        PhotoField.superclass.beforeDestroy.call(this);
    },
    setPhoto:function(){
    	this.uploadWin = new Ext.app.FormWindow({
			iconCls : 'picture',
			winConfig : {
				height : 520,
				width : 660,
				title : '设置人员相片',
				desc : '上传并设置人员相片',
				bigIconClass : 'pictureIcon'
			},
			formConfig : {
				fileUpload : true,
				items : [
					{xtype:'panel',height:400,width:640,
						border: false, autoLoad: {url: ctx + '/flash/uploadPhoto.jsp', scripts: true}}
				]
			}
		});
		this.uploadWin.show();
    },
    
	setupPhoto : function(){
		this.uploadWin = new Ext.app.FormWindow({
			iconCls : 'picture',
			winConfig : {
				height : 210,
				width : 395,
				title : '设置人员相片',
				desc : '上传并设置人员相片',
				bigIconClass : 'pictureIcon'
			},
			formConfig : {
				fileUpload : true,
				items : [
		 			{xtype: 'f-upload',fieldLabel: '上传相片',name: 'photoFile',allowBlank: false}
				]
			},
			buttons : [{
				text: '确定',
				scope:this,
				handler : function(){
					this.uploadWin.formPanel.getForm().submit({           
			            waitMsg:'保存中...',
						url:ctx+'/employee/setupPhoto',
						scope:this,
						success:function(form, action) {
							this.uploadWin.close();
							this.setValue(action.result.photoId);
							App.msg(action.result.msg);
			            }
			        });
				}
			}]
		});
		this.uploadWin.show();
    },
    setValue : function(photoId){
        this.photoAreaTpl.overwrite(this.photoArea.body,{
        	url : photoId? this.photoUrlPrifix + photoId:this.noPhotoUrl});
        return PhotoField.superclass.setValue.call(this, photoId);
    }
});

Ext.reg('f-photo', PhotoField);

Employee = Ext.extend(Ext.app.BaseFuncPanel,{
	initComponent : function(){
		var emailLink = function(v){
		    return !v? "" : String.format('<span><a href="mailto:{0}" target="_blank" class="emailLink">{0}</a></span>',v);
		}
		
		var qq = function(v){
			return !v ? "" : String.format('<span><a target=blank href=tencent://message/?uin={0}><img border="0" SRC=http://wpa.qq.com/pa?p=1:{0}:5 alt="QQ号:{0}"></a></span>',v);
		}
		
		var employeeStatus = function(v){
			var text = v['text']||'';
			var map = {
				'离职' : 'red',
				'试用' : 'blue'
			}	 
			return String.format('<span style="color:{0}">{1}</span>',map.text||'black',text);
		}
		var organizationRenderer = function(v){
			return v.text;
		}
		var areasRenderer = function(v){
			var re = [];
			for(var r in v){
				if(v[r].text){
					re.push(v[r].text);
				}		
			}
			return re.join(',');
		}
		var annualLeaveRenderer = function(v){
			return v==0?String.format('<span style="color:{0}">无</span>','red'):String.format('剩余(<span style="color:{0}">{1}</span>)天','red',v);
		}
		Ext.apply(this,{
			gridConfig:{
				sm:new Ext.grid.RowSelectionModel(),
				cm:new Ext.grid.ColumnModel([
					new Ext.grid.RowNumberer(),
					{header: '姓名',dataIndex:'name',sortable:true},
					{header: '工号',dataIndex:'code',sortable:true},
					{header: '卡号',dataIndex:'card',sortable:true},
//					{header: '考勤区域',dataIndex:'areas',sortable:true,renderer:areasRenderer},
					{header: '部门',dataIndex:'organization',renderer:organizationRenderer},
					{header: '岗位',dataIndex:'position',renderer:organizationRenderer},
					{header: '岗位类型',dataIndex:'postType',renderer:organizationRenderer},
					{header: '员工类别',dataIndex:'peopleType',renderer:organizationRenderer},
					{header: '性别',dataIndex:'sex',renderer:dictRenderer},
					{header: '出生日期',dataIndex:'birthday'},
					{header: '参加工作时间',dataIndex:'joinWorkDate'},
					{header: '入职日期',dataIndex:'hireDate'},
					{header: '年休假',dataIndex:'annualLeave',renderer:annualLeaveRenderer},
					{header: '办公电话',dataIndex:'phone',hidden:true},
					{header: '手机',dataIndex:'mobile',hidden:true},
					{header: 'qq',dataIndex:'qq',hidden:true},
					{header: '离职日期',dataIndex:'turnAwayDate',hidden:true,id:'head_turnAwayDate'}
				]),	
				storeMapping:[
					'code','card', 'name','areas','organization','position','postType','education','peopleType', 'sex', 'phone', 'mobile', 'status', 'qq'
					, 'hireDate', 'email','photoId','birthday','joinWorkDate','turnAway','turnAwayDate','annualLeave'
				]
			},
			winConfig : {
				height: 500,width:650,
				desc : '新增，修改员工的的信息',
				bigIconClass : 'employeeIcon'
			},
			buttonConfig : ['all','-',
				{
					type:'f-button',
					text:'年假计算',
					tooltip:'根据来建行时间，和去年请病假天数计算',
					scope:this,
					iconCls : 'annual',
					privilegeCode:this.funcCode+"_annualLeaveJob",
					handler:this.annualLeave
				},'-',
				{
					type:'f-button',
					text:'导出备份',
					tooltip:'导出备份员工信息',
					iconCls:'excel',
					scope:this,
					privilegeCode:this.funcCode+"_exportEmployee",
					handler:this.exportEmployee
				},
				{
					type:'f-button',
					text:'导入员工',
					tooltip : '导入员工信息',
					iconCls : 'excel',
					scope:this,
					privilegeCode: this.funcCode + '_importEmployee',
					handler:this.importExcel
				},
				{
					type:'f-button',
					text:'薪资导入',
					tooltip : '导入员工基本工资',
					iconCls : 'excel',
					scope:this,
					privilegeCode: this.funcCode + '_importSalary',
					handler:this.importSalary
				},'-',{
					type:'f-button',
					text:'导出员工卡号',
					tooltip : '导入门禁系统中全部员工的卡号',
					iconCls : 'excel',
					scope:this,
					privilegeCode: this.funcCode + '_exportAllEmployeesCard',
					handler:this.exportNewEmployeesCard
				},'->',{
				
		 			xtype:'f-select',hiddenName:'turnAway',fieldLabel:'员工离职与否',width:80,id:'Employee_turnAway',value:0,
		 			data:[
		 				['0','未离职'],
		 				['1','已离职']
		 			],
		 			listeners : {
		 				select: function(combo, record, index) {
		 					Ext.getCmp('Employee_turnAway').setValue(record.data.id);
							Ext.getCmp('Employee').loadData();
						}
					}
				},'-',
				{xtype : 'f-search',emptyText : '请输入姓名或者工号'}
			],
			formConfig:{
				items: [{
					layout:'column',
					border : false,
					defaults : {
                		border : false
                	},
					items :[{
						width:300,
	                	layout: 'form',
	                	defaults : {
	                		msgTarget : 'under',
	                		width : 200
	                	},
						items: [
							{xtype: 'f-text',fieldLabel: '姓名',name: 'name',emptyText: '请输入员工姓名',allowBlank: false}, 
							{xtype: 'f-text',fieldLabel: '工号',name: 'code',vtype: 'digital',allowBlank: false},
							{xtype: 'f-text',fieldLabel: '卡号',name: 'card'},
//							{xtype: 'f-select',dataUrl:'/organization/getOrganizations',storeFields:['id','text','code'],
//								fieldLabel: '部门',hiddenName: 'organization',id:'organizationSelect',allowBlank: false,listeners : {}},
							{xtype: 'treeField',fieldLabel: '部门',listHeight:240,hiddenName: 'organization',allowBlank: false
								,name:'organization',dataUrl : ctx+'/organization/getOrganizationTree',readOnly:false},
//							{xtype: 'f-areaByEmployee',fieldLabel: '考勤区域',hiddenName: 'areas'
//								,emptyText: '可以选择一个或多个区域',id:'areasSelect',listeners : {}},
//							{xtype: 'treeField',fieldLabel: '考勤区域',listHeight:240,hiddenName: 'areas'
//								,name:'areas',dataUrl : ctx+'/area/multiAreaTree',readOnly:false},
							{xtype: 'f-dict',fieldLabel: '岗位',hiddenName: 'position',kind: 'position'}, 
							{xtype: 'f-dict',fieldLabel: '岗位类型',hiddenName: 'postType',kind: 'postType'}, 
							{xtype: 'f-dict',fieldLabel: '员工类别',hiddenName: 'peopleType',kind: 'peopleType'}
						]
					},{
						xtype : 'f-photo',
						id:'photoField',
						name: 'photoId',
						allowBlank: false,
						columnWidth:.9
					}]
				},{
					layout:'column',
					border : false,
					defaults : {
                		border : false
                	},
					items :[{
						width:300,
	                	layout: 'form',
	                	defaults : {
	                		msgTarget : 'under',
	                		width : 200
	                	},
						items: [
							{xtype: 'f-dict',fieldLabel: '性别',hiddenName: 'sex',kind: 'sex'}, 
							{xtype: 'f-text',fieldLabel: '办公电话',name: 'phone'},
							{xtype: 'f-text',fieldLabel: '手机',name: 'mobile'},
							{
					 			xtype:'f-select',hiddenName:'turnAway',fieldLabel:'离职与否',id:'EmployeeForm_turnAway',value:0,
					 			data:[
					 				['0','未离职'],
					 				['1','已离职']
					 			],
					 			listeners : {
					 				select: function(combo, record, index) {
					 					var id = record.data.id;
					 					if(id==0)Ext.getCmp('Employee_turnAwayDate').setValue("");
									}
								}
							}
						]
					},{
						layout:'form',
						border : false,
						defaults : {
							width : 200
						},
						items :[
							{xtype: 'f-date',fieldLabel: '出生日期',name: 'birthday'},
							{xtype: 'f-date',fieldLabel: '参加工作时间',name: 'joinWorkDate'},
							{xtype: 'f-date',fieldLabel: '入职日期',name: 'hireDate'},
							{xtype: 'f-date',fieldLabel: '离职日期',name: 'turnAwayDate',id:'Employee_turnAwayDate',value:""}
						]
					}]
				}]
			},
			url:ctx+'/employee'	
		});
		Employee.superclass.initComponent.call(this);
		
		//添加一个渲染后可以根据权限或角色选择
		this.on('render',function(){
			if(loginUser.ownRole("普通员工")){
				var sa = this.topToolbar;
				sa.setVisible(false);
			}
		});
		
		this.store.on('beforeload',function(store,options){
			var v = Ext.getCmp('Employee_turnAway');
			options.params.turnAway = v.value;
			var cm = Ext.getCmp('Employee').gridConfig.cm;
			var index = cm.getIndexById('head_turnAwayDate');
			cm.setHidden(index,true);
			if(v.value>0){
				cm.setHidden(index,false);
			}
		},this); 
	},
	exportEmployee:function(){
		document.location.href = ctx+'/employee/exportEmployee'
	},
	exportNewEmployeesCard:function(){
		document.location.href = ctx+'/console/getClosureEmployeesCard'
	},
	importExcel:function(){
		this.importWin = new Ext.app.FormWindow({
			winConfig : {
				height : 210,
				width : 395,
				title : '从Excel导入员工基本信息',
				desc : '通过Excel表格导入员工信息到员工表'
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
						url:ctx+'/employee/exportExcelToEmployee',
						scope:this,
						success:function(form, action) {
							this.importWin.close();
//							App.msg(action.result.msg);
							Ext.MessageBox.show({
           						title: '员工导入提示',
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
	},
	importSalary:function(){
		this.importSalaryWin = new Ext.app.FormWindow({
			winConfig : {
				height : 210,
				width : 395,
				title : '从Excel导入基本工资',
				desc : '格式头部：员工编号、员工姓名、基本工资、区域津贴'
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
					this.importSalaryWin.formPanel.getForm().submit({           
			            waitMsg:'保存中...',
						url:ctx+'/employee/importSalaryToEmployee',
						scope:this,
						success:function(form, action) {
							this.importSalaryWin.close();
							Ext.MessageBox.show({
           						title: '员工导入提示',
           						msg: action.result.msg,
					           	buttons: Ext.MessageBox.OK,
					           	icon: Ext.MessageBox.INFO
       						});
			            }
			        });
				}
			}]
		});	
		this.importSalaryWin.show();
	},
	annualLeave:function(){
		Ext.Ajax.request({
			url:ctx+'/employee/annualLeave',
			scope:this,
			success:function(form,action){
				this.loadData();
			}
		});
	}
});
