<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
<title>FJDP管理控制平台</title>
<s:include value="/common/header.jsp" />
<script type="text/javascript">


Ext.onReady(function(){

	var intiDbPanel = new Ext.Panel({
		frame:true,
		height: 200,
		width: 400,
		title:'数据库初始化',
		layout:'anchor',
		html:'初始化本系统的数据，完全初始化会删除表并重新建立表再插入初始化数据，菜单权限字典初始化只会新增和更新（不会删除）菜单权限字典，不会将表整个重建',
		buttons:[{
			xtype:'button',
			text:'完全初始化',
			scope:this,
			handler:function(){
				Ext.Msg.show({
				   title:'完全初始化确认',
				   msg: '您确实要完全初始化吗？这个操作将重新构建数据库，以前所有的数据都会丢失!',
				   buttons: Ext.Msg.YESNO,
				   fn: function(buttonId){
					   if(buttonId == 'yes'){
						  	Ext.Ajax.request({
								url : ctx+'/console/initDb',
								success:function(){
									App.msg("成功更新");
								}
							})
						}
					},
					icon: Ext.MessageBox.WARNING
				});
			}
		},{
			xtype:'button',
			text:'菜单权限字典初始化',
			scope:this,
			handler:function(){
				Ext.Ajax.request({
					url : ctx+'/console/rebuildDb',
					success:function(){
						App.msg("成功更新");
					}
				})
			}
		}]
	});
	intiDbPanel.render('intiDbPanel');

	var hqlPanel = new Ext.form.FormPanel({
		frame:true,
		height: 200,
		width: 400,
		title:'hql解释器',
		layout:'anchor',
		items:[{
			xtype:'textarea',
			width: 380,
			height: 120,
			name:'hql'
		}],
		buttons:[{
			xtype:'button',
			text:'执行',
			scope:this,
			handler:function(){
				hqlPanel.getForm().submit({
					url : ctx+'/console/executeHql',
					success:function(){
						App.msg("成功更新");
					}
				})
			}
		}]
	});
	hqlPanel.render('hqlPanel');

	var computeAttPanel = new Ext.form.FormPanel({
		frame:true,
		height: 120,
		width: 400,
		title:'天考勤计算',
		html:'计算选择到某一天的考勤信息，同时也会处理昨天排班是次日到考勤信息',
		layout:'anchor',
		items:[
			{xtype: 'f-date',fieldLabel: '计算日期',name: 'date',allowBlank: false}
		],
		buttons:[{
			xtype:'button',
			text:'执行',
			scope:this,
			handler:function(){
			computeAttPanel.getForm().submit({
					url : ctx+'/console/attShiftDayCompute',
					success:function(response, options){
						App.msg(options.result.msg);
					}
				})
			}
		}]
	});
	computeAttPanel.render('computeAttPanel');

	var computeMonthAttPanel = new Ext.form.FormPanel({
		frame:true,
		height: 120,
		width: 400,
		title:'月考勤计算',
		html:'统计某一年某一个月的考勤信息',
		layout:'anchor',
		items:[
			{xtype: 'f-yearmonth',fieldLabel: '计算日期',name: 'yearmonth',allowBlank: false}
		],
		buttons:[{
			xtype:'button',
			text:'执行',
			scope:this,
			handler:function(){
			computeMonthAttPanel.getForm().submit({
					url : ctx+'/console/attShiftMonthCompute',
					success:function(response, options){
						App.msg(options.result.msg);
					}
				})
			}
		}]
	});
	computeMonthAttPanel.render('computeMonthAttPanel');

	var syncEmployeeCard = new Ext.form.FormPanel({
		frame:true,
		height: 120,
		width: 400,
		title:'同步人员卡号',
		html:'根据服务器配置的门襟数据库地址同步人员卡号',
		layout:'anchor',
		buttons:[{
			xtype:'button',
			text:'执行',
			scope:this,
			handler:function(){
				syncEmployeeCard.getForm().submit({
					url : ctx+'/console/syncClosureEmployeesCard',
					success:function(response, options){
						App.msg(options.result.msg);
					}
				});
			}
		}]
	});
	syncEmployeeCard.render('syncEmployeeCard');

	var syncLogs = new Ext.form.FormPanel({
		frame:true,
		height: 120,
		width: 400,
		title:'同步门襟打卡记录',
		html:'根据服务器配置的门襟数据库地址同步人员最早和最晚打卡记录',
		layout:'anchor',
		items:[
				{xtype: 'f-date',fieldLabel: '计算日期',name: 'date',allowBlank: false}
		],
		buttons:[{
			xtype:'button',
			text:'执行',
			scope:this,
			handler:function(){
				syncLogs.getForm().submit({
					url : ctx+'/console/syncLogs',
					success:function(response, options){
						App.msg(options.result.msg);
					}
				});
			}
		}]
	});
	syncLogs.render('syncLogs');

	//area2OrgsUpdateWin.show();
	var area2Orgs = new  Ext.form.FormPanel({
		frame:true,
		height: 120,
		width: 400,
		title:'上传区域与组织架构关系表',
		html:'上传区域与组织架构关系表，用于初始化区域，excel表格格式为：第一列为区域名称、第二列为部门名称，多对多的关系',
		layout:'anchor',
		buttons:[{
				xtype:'f-button',text:'上传Excel',scope:this,
				handler:function(){
					var area2OrgsUpdateWin = new Ext.app.FormWindow({
						iconCls : 'picture',
						winConfig : {
							height : 210,
							width : 395,
							title : '上传区域与组织架构关系表',
							desc : '上传区域与组织架构关系表'
						},
						formConfig : {
							fileUpload : true,
							items : [
					 			{xtype: 'f-upload',fieldLabel: '上传Excel',name: 'area2OrgsFile',allowBlank: false}
							]
						},
						buttons : [{
							text: '确定',
							scope:this,
							handler : function(){
								area2OrgsUpdateWin.formPanel.getForm().submit({           
									url:ctx+'/console/area2Orgs',
									scope:this,
									success:function(form, action) {
										area2OrgsUpdateWin.close();
						            }
						        });
							}
						}]
					});
					area2OrgsUpdateWin.show();
				}
			}
		]
	});
	area2Orgs.render('area2Orgs');

	//上传组织机构excel表格面板
	var organizationPanel = new Ext.form.FormPanel({
		frame:true,
		height: 120,
		width: 400,
		title:'上传组织机构关系表',
		html:'上传组织机构关系表，用于初始化组织机构列表，excel表格格式为：第一列为一级机构名称、第二列为一级机构下的子机构名称，一对多的关系',
		layout:'anchor',
		buttons:[{
				xtype:'f-button',text:'上传Excel',scope:this,
				handler:function(){
					var organizationUpdateWin = new Ext.app.FormWindow({
						iconCls : 'picture',
						winConfig : {
							height : 210,
							width : 395,
							title : '上传组织机构关系表',
							desc : '上传组织机构关系表'
						},
						formConfig : {
							fileUpload : true,
							items : [
					 			{xtype: 'f-upload',fieldLabel: '上传Excel',name: 'organizationFile',allowBlank: false}
							]
						},
						buttons : [{
							text: '确定',
							scope:this,
							handler : function(){
							organizationUpdateWin.formPanel.getForm().submit({           
									url:ctx+'/console/exportExcelToOrganization',
									scope:this,
									success:function(form, action) {
										organizationUpdateWin.close();
						            }
						        });
							}
						}]
					});
					organizationUpdateWin.show();
				}
			}
		]
	});
	organizationPanel.render('organization');

	var SchedulePanel = new Ext.form.FormPanel({
		frame:true,
		width: 400,
		title:'非网点月排班',
		html:'手动排定某个月非网点排班',
		layout:'anchor',
		items:[
				{xtype: 'f-yearmonth',fieldLabel: '计算日期',name: 'yearmonth',allowBlank: false}
		],
		buttons:[{
			xtype:'button',
			text:'执行',
			scope:this,
			handler:function(){
			SchedulePanel.getForm().submit({
					url : ctx+'/console/batchCreateAuto',
					success:function(response, options){
						App.msg(options.result.msg);
					}
				});
			}
		}]
	});
	SchedulePanel.render('schedule');

	var createUser = new Ext.form.FormPanel({
		frame:true,
		width:400,
		title:'生成用户',
		html:'手动生成用户',
		layout:'anchor',
		buttons:[{
			xtype:'button',
			text:'执行',
			scope:this,
			handler:function(){
			var userUpdateWin = new Ext.app.FormWindow({
				iconCls : 'picture',
				winConfig : {
					height : 210,
					width : 395,
					title : '上传组织机构关系表',
					desc : '上传组织机构关系表'
				},
				formConfig : {
					fileUpload : true,
					items : [
			 			{xtype: 'f-upload',fieldLabel: '上传Excel',name: 'userFile',allowBlank: false},
					]
				},
				buttons : [{
					text: '前',
					scope:this,
					handler : function(){
					userUpdateWin.formPanel.getForm().submit({           
							url:ctx+'/console/importUserAndCard',
							scope:this,
							success:function(form, action) {
								userUpdateWin.close();
				            }
				        });
					}
				},{
					text: '后',
					scope:this,
					handler : function(){
					userUpdateWin.formPanel.getForm().submit({           
							url:ctx+'/console/importUserAndCard1',
							scope:this,
							success:function(form, action) {
								userUpdateWin.close();
				            }
				        });
					}
				},{
					text: '用户生成前',
					scope:this,
					handler : function(){
					userUpdateWin.formPanel.getForm().submit({           
							url:ctx+'/console/importUserAndCard2',
							scope:this,
							success:function(form, action) {
								userUpdateWin.close();
				            }
				        });
					}
				},{
					text: '用户生成中',
					scope:this,
					handler : function(){
					userUpdateWin.formPanel.getForm().submit({           
							url:ctx+'/console/importUserAndCard3',
							scope:this,
							success:function(form, action) {
								userUpdateWin.close();
				            }
				        });
					}
				},{
					text: '用户生成后',
					scope:this,
					handler : function(){
					userUpdateWin.formPanel.getForm().submit({           
							url:ctx+'/console/importUserAndCard4',
							scope:this,
							success:function(form, action) {
								userUpdateWin.close();
				            }
				        });
					}
				}]
			});
			userUpdateWin.show();
		}
			}
		]
	});
	createUser.render('createUser');

	var yearTask = new Ext.form.FormPanel({
		frame:true,
		width:400,
		title:'计算员工年假',
		html:'计算员工年假',
		layout:'anchor',
		buttons:[{
			xtype:'button',
			text:'执行',
			scope:this,
			handler:function(){
				createUser.getForm().submit({
					url:ctx+"/console/setCard",
					success:function(response,options){
					}
				});
			}
		}]
	});
	yearTask.render('yearTask');

	var totalTask = new Ext.form.FormPanel({
		frame:true,
		width:400,
		title:'计算部门报表',
		html:'部门报表',
		layout:'anchor',
		buttons:[{
			xtype:'button',
			text:'执行',
			scope:this,
			handler:function(){
				createUser.getForm().submit({
					url:ctx+"/console/getTotal",
					success:function(response,options){
					}
				});
			}
		}]
	});
	totalTask.render('totalTask');
});
</script>

</head>
<body style="padding:10px;">
<div id="intiDbPanel"></div>
<div id="hqlPanel"></div>
<div id="computeAttPanel"></div>
<div id="computeMonthAttPanel"></div>
<div id="syncEmployeeCard"></div>
<div id="syncLogs"></div>
<div id="area2Orgs"></div>
<div id="organization"></div>
<div id="schedule"></div>
<div id="createUser"></div>
<div id="yearTask"></div>
<div id="totalTask"></div>
</body>
</html>

