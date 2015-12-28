BrowseTimeGridPanel = Ext.extend(Ext.app.BaseFuncPanel,{
	initComponent : function(){
		Ext.apply(this,{
			gridConfig: {
			cm:new Ext.grid.ColumnModel([
			   new Ext.grid.RowNumberer(),
			   {header:'技能组名称',dataIndex:'teamName',sortable:true},
			   {header:'班次名称',dataIndex:'shiftConfigType',sortable:true},
			   {header:'上班时间',dataIndex:'startWorkTime',sortable:true},
			   {header:'下班时间',dataIndex:'endWorkTime',sortable:true}
			]),
			storeMapping:[
				     'id','teamName','shiftConfigType','startWorkTime','endWorkTime'
				]
			},
			winConfig : {
				height: 350, width : 350,
				title:'班次时间设置',
				desc : '增加、修改班次信息',
				bigIconClass : 'employeeIcon'
			},
			buttonConfig : ['all','-',{
				type:'f-button',
				text:'导入班次时间表',
				tooltip : '导入班次时间表信息',
				iconCls : 'excel',
				scope:this,
//				privilegeCode: this.funcCode + '_importEmployee',
				handler:this.importBrowseTimeExcel
			}],
			formConfig:{
				items: [
				     {xtype:'f-text',fieldLabel:'技能组名称',name: 'teamName',id:'teamName',allowBlank: false,width:150},
				     {xtype:'f-text',fieldLabel:'班次名称',name: 'shiftConfigType',id:'shiftConfigType',allowBlank: false,width:150},
				     {xtype:'timefield',fieldLabel:'上班时间',name: 'startWorkTime',id:'startWorkTime',width:150,format:'H:i',increment:30,allowBlank: false},
				     {xtype:'timefield',fieldLabel:'下班时间',name: 'endWorkTime',id:'endWorkTime',width:150,format:'H:i',increment:30,allowBlank: false},
				     {xtype:'checkbox',boxLabel:'工作到次日',name:'nextDay',checked:false}
				]
			},
			url:ctx+'/shiftConfigTime'
		});
		BrowseTimeGridPanel.superclass.initComponent.call(this);
		
	},
	importBrowseTimeExcel:function(){
		this.importWin = new Ext.app.FormWindow({
			winConfig : {
				height : 210,
				width : 395,
				title : '从Excel导入班次时间信息',
				desc : '通过Excel表格导入班次时间表'
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
						url:ctx+'/shiftConfigTime/exportExcel',
						scope:this,
						success:function(form, action) {
							this.importWin.close();
//							App.msg(action.result.msg);
							Ext.MessageBox.show({
           						title: '班次时间表导入提示',
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

BrowseTimewin = Ext.extend(Ext.Window,{
	initComponent:function(){
		Ext.apply(this,{
			layout: this.bannerPanel ? 'anchor' : 'fit',
			maximizable:false,
			buttonAlign:'center',
			resizable:true,
			title:'班次信息',
			modal:true,
			constrainHeader : true,
			maxOnShow:false,
			height : 500,
			width : 600
		});
		var btgp = new BrowseTimeGridPanel();
		this.items = [btgp];
		btgp.loadData();
		BrowseTimewin.superclass.initComponent.call(this);
	}
});

ServiceManagementBrowseEmployeeTree = Ext.extend(Ext.tree.TreePanel,{
	initComponent : function(){
		Ext.apply(this, {
			animate:false,
			tbar:[
			      	{
			      		type:'f-button',
			      		text:'班次时间设置',
			      		id:'browseBt',
			      		iconCls:'clock',
			      		scope:this,
			      		privilegeCode: this.funcCode + '_browseButton',
			      		handler:this.setBrowseTime
			      	},'->',{
			      		type:'f-button',
			      		text:'导入排班表',
			      		tooltip : '导入排班信息表',
			      		iconCls : 'excel',
			      		scope:this,
			      		privilegeCode: this.funcCode + '_importBrowse',
			      		handler:this.importBrowseExcel
			      	}
            ],
            loader: new Ext.tree.TreeLoader({            
				dataUrl :ctx+'/shiftConfig/listEmployeeByOrg',
				baseParams:{checkBox:false}
	        }),
	        root: new Ext.tree.AsyncTreeNode({ 
	        	id:'f-0',
	        	checked:false 
	        }) 
		});
		
		ServiceManagementBrowseEmployeeTree.superclass.initComponent.call(this);
    	
    },
	importBrowseExcel:function(){
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
						url:ctx+'/shiftConfigTime/importBrowseExcel',
						scope:this,
						success:function(form, action) {
							this.importWin.close();
//							App.msg(action.result.msg);
							Ext.MessageBox.show({
           						title: '排班情况导入提示',
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
    setBrowseTime:function(){
    	var BrowseTimeWindow = new BrowseTimewin({id:'BrowseTimewin'});
    	BrowseTimeWindow.show();
    }
});

ServiceManagementBrowseMonthPanel = Ext.extend(Ext.Panel,{
    initComponent : function(){
		Ext.apply(this, {
			ctCls:'ext-cal-monthview x-unselectable',
			html:'loading',
		    tpl:new Ext.calendar.BoxLayoutTemplate({
		    	id:'ShiftBrowseMonthView',
	    		showHeader:true
	    	}),
		    tbar:{
		        cls: 'ext-cal-toolbar',
		        border: false,
		        //buttonAlign: 'center',
		        items: [{
		            id: this.id + '-tb-prev',
					text:'上一月',
		            handler: this.onPrevClick,
		            scope: this,
		            iconCls: 'x-tbar-page-prev'
		        },{
			        id: this.id + '-tb-next',
			        handler: this.onNextClick,
					text:'下一月',
					iconAlign : 'right',
			        scope: this,
			        iconCls: 'x-tbar-page-next'
			    }]
			}
		});
		
		this.shiftInfoStore = new Ext.data.JsonStore({
	        id: 'shiftInfoStore',
//			url: ctx+'/shiftConfig/getInfos',
	        url:ctx+'/shiftConfig/getShiftConfigInfos',
	        root: 'data',
	        fields: [
	        	'date','timeRange','type'
	        ]
	    });
		
		ServiceManagementBrowseMonthPanel.superclass.initComponent.call(this);
		
    },
    exportOrgShift:function(){
    	
    },
    afterRender: function() {
    	ServiceManagementBrowseMonthPanel.superclass.afterRender.call(this);
    	
    	this.showMonthView(new Date().getFirstDateOfMonth(),this.employeeId);
    	
    	this.on({
            'resize': this.refreshView,
            scope: this
        });
    	
    	this.el.on({
            'click': this.onClick,
            scope: this
        });
    },
    refreshView:function(e){
    	//this.reloadMonthView();
    	(function(){
            var ctHeight = this.body.getSize().height;
            var hd = this.el.child('.ext-cal-hd-ct');
            var viewBody = this.el.child('.ext-cal-body-ct');
            if(hd && viewBody){
            	viewBody.setHeight(ctHeight - hd.getHeight());
            }
        }).defer(10, this);
    },
    showMonthView:function(startDate,employeeId){
    	this.startDate = startDate;
    	
    	this.shiftInfoStore.load({
    		params:{
    			employeeId:employeeId||this.employeeId,
    			startDate:startDate.format('Y-m-d'),
    			endDate:startDate.getLastDateOfMonth().format('Y-m-d')
    		},
    		scope:this,
    		callback:function(data,options,success){
    			this.updateTitle();
    			this.tpl.overwrite(this.body,{
    				bodyHeight:this.body.getSize().height,
		    		startDate:startDate,
		    		plusInfos:data
		    	});
    		}
    	});
    },

    reloadMonthView:function(){
    	this.showMonthView(this.startDate,this.employeeId);
    },
    onPrevClick: function() {
    	this.showMonthView(this.tpl.startDate.add(Date.MONTH,-1));
    },
    // private
    onNextClick: function() {
        this.showMonthView(this.tpl.startDate.add(Date.MONTH,1));
    },
    updateTitle:function(){
		var month = this.startDate.format('Y年m月');
		this.setTitle(month+'-'+this.employeeName);
    },
    onClick:function(e){
    	this.dayElIdDelimiter = '-';
    	var el = e.getTarget('td', 3);
        if (el) {
            if (el.id && el.id.indexOf(this.dayElIdDelimiter) > -1) {
                var parts = el.id.split(this.dayElIdDelimiter);
                dt = parts[parts.length - 1];
                var clickedDate = Date.parseDate(dt, 'Ymd');
                if(Ext.isDate(clickedDate)){
                	 this.fireEvent('dayclick', clickedDate, Ext.get(el.id));
                	 return;
                }
            }
        }
    }
});

ShiftEditWindow = Ext.extend(Ext.app.FormWindow,{
	initComponent : function(){
		Ext.apply(this, {
			winConfig:{
				title:'修改员工班次设置信息',
				iconCls:'calendar',
				bigIconClass : 'calendarIcon',
				desc:'可修改或者清除员工的班次设置',
				width:400,
			    height:400
			},
			//layout:'fit',
			formConfig:{
				//border:false,
				//bodyStyle:'padding:10px;',
				buttons:[{
					text:'保存',
					scope:this,
					handler:this.submitForm
				}],
				items:[{
					xtype:'hidden',
					id:'ShiftEdit-id',
            		name:'id'
				},{
					xtype:'hidden',
            		id:'ShiftEdit-employeeId',
            		name:'employeeId'
				},{
					xtype:'f-text',
            		fieldLabel:'员工',
            		id:'ShiftEdit-employeeName',
            		name:'employeeName',
            		readOnly:true
				},{
					xtype:'f-text',
            		fieldLabel:'日期',
            		id:'ShiftEdit-date',
            		name:'date',
            		readOnly:true
				},{
					xtype:'timefield',
            		fieldLabel:'上班时间',
            		format:'H:i',
            		increment:30,
            		width:230,
            		id:'ShiftEdit-startTime',
            		name:'startTime'
				},{
					xtype:'timefield',
            		fieldLabel:'下班时间',
            		format:'H:i',
            		increment:30,
            		width:230,
            		id:'ShiftEdit-endTime',
            		name:'endTime'
				},{
					xtype:'checkbox',
					boxLabel:'工作到次日',
					name:'nextDay',
					checked:false
				},{
					xtype:'button',
					iconCls:'remove',
					style:'margin-left:100px;margin-top:10px;',
            		text:'取消该天的排班设置',
            		width:150,
            		scope:this,
            		handler:this.removeConfig
				}]
			}
		});
    	ShiftEditWindow.superclass.initComponent.call(this);
    },
    initDateValue:function(d){
    	Ext.getCmp('ShiftEdit-date').setValue(d.date);
    	Ext.getCmp('ShiftEdit-startTime').setValue(d.startTime);
    	Ext.getCmp('ShiftEdit-endTime').setValue(d.endTime);
    	Ext.getCmp('ShiftEdit-employeeId').setValue(d.employeeId);
    	Ext.getCmp('ShiftEdit-employeeName').setValue(d.employeeName);
    },
    submitForm:function(){
    	this.formPanel.getForm().submit({
    		url: ctx+'/shiftConfig/updateConfig',
    		scope:this,
    		success:function(){
    			this.close();
    			Ext.getCmp('ServiceManagementBrowseMonthPanel').reloadMonthView();
    		}
    	});
    },
    removeConfig:function(){
    	Ext.Ajax.request({
    		url:ctx+'/shiftConfig/delOrAdd',
    		params:{
    			id : Ext.getCmp('ShiftEdit-id').getValue(),
    			date : Ext.getCmp('ShiftEdit-date').getValue(),
    			employeeId : Ext.getCmp('ShiftEdit-employeeId').getValue()
    		},
    		scope:this,
    		success:function(){
    			this.close();
    			Ext.getCmp('ServiceManagementBrowseMonthPanel').reloadMonthView();
    		}
    	});
    }
});



ServiceManagementBrowse = Ext.extend(Ext.Panel,{
	layout:'border',
	closable: true,
	hideMode:'offsets',
	initComponent : function(){
		
		this.left = new ServiceManagementBrowseEmployeeTree({
			funcCode: this.funcCode,
			id:'ServiceManagementBrowseEmployeeTree',
			region:'west',
			title: '员工列表',
			split:true,
			width: 220,
			minSize: 175,
			maxSize: 400
		});
		
		this.tipsPanel = new Ext.Panel({
			html:'<div class="selectTips">请在左边的员工列表选择一位员工</div>'
		});
		
		this.ServiceManagementBrowseMonthPanel = new ServiceManagementBrowseMonthPanel({
            id:'ServiceManagementBrowseMonthPanel',
            title:'日历',
            region:'center'
		})
		
		this.right = new Ext.Panel({
			layout:'card',
			layoutConfig:{
				deferredRender:true
			},
			activeItem:0,
            id:'shiftBrowseCalendarPanel',
            region:'center',
            defaults :{
            	border:false
            },
            items:[
            	this.tipsPanel,
            	this.ServiceManagementBrowseMonthPanel
            ]
		});
		
		
		this.items = [this.left,this.right];
		ServiceManagementBrowse.superclass.initComponent.call(this);
		
		this.ServiceManagementBrowseMonthPanel.on('dayclick',this.onDayClick,this);
		
		this.left.on('click',function(node,e){
    		this.onTreeClick(node);
    	},this);
    	
			
    },
	loadData:function(){
		
	},
	onDayClick:function(date, el){
    	var tree = Ext.getCmp('ServiceManagementBrowseEmployeeTree');
		var node = tree.getSelectionModel().getSelectedNode();
		if(node.attributes.type == 'employee'){
			var type = el.getAttribute('plusInfoType');
			if(!type)return;
			if(type != 'NONE'){
				var editWin = new ShiftEditWindow();
		        editWin.show();
		        
		        editWin.formPanel.getForm().load({
		        	url: ctx+'/shiftConfig/loadConfig',
		        	params:{
		        		date: date.format('Y-m-d'),
		      			employeeId: node.id.split('-')[1]
		        	}
		        });
			}
		}
    },
	onTreeClick:function(node){
    	if(node.attributes.type == 'employee'){
    		this.ServiceManagementBrowseMonthPanel.employeeId = node.id.split('-')[1];
    		this.ServiceManagementBrowseMonthPanel.employeeName = node.text;
    		if(this.right.layout.activeItem.id == 'ServiceManagementBrowseMonthPanel'){
	    		this.ServiceManagementBrowseMonthPanel.reloadMonthView();
	    	}else{
		    	this.right.layout.setActiveItem(1); 
	    	}
		}
		
    }

});