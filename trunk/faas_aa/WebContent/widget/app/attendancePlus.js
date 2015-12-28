OrganizationEmployeeTree = Ext.extend(Ext.app.BaseFuncTree,{
	initComponent : function(){
		Ext.apply(this, {
			winConfig : {
				title:'补刷卡',
				iconCls:'vcard',
				desc:'当员工因特殊情况没有时人工增加一条刷卡记录',
				height: 330
			},
			formConfig:{
				items: [
				    {xtype: 'f-OrganizationEmployee',id:'attendancePlus_name',fieldLabel: '员工姓名',hiddenName: 'employee',allowBlank:false},
					{xtype: 'f-date',fieldLabel: '起始日期',id:'checkInDate',name: 'checkInDate',allowBlank:false,
						listeners:{
							scope:this,
							select:function(dateObj,date){
								var ss=Ext.getCmp('endCheckInDate').getValue();
								if(date>ss){
									App.msg('起始时间大于结束时间');
								}
							}
						}
					},
					{xtype: 'f-date',fieldLabel: '结束日期',id:'endCheckInDate',name: 'endCheckInDate',allowBlank:false,
						listeners:{
							scope:this,
							select:function(dateObj,date){
								var ss=Ext.getCmp('checkInDate').getValue();
								if(ss>date){
									App.msg('起始时间大于结束时间');
								}
							}
						}
					},
					{xtype:'timefield',fieldLabel:'刷卡时间',format:'H:i',increment:10,name:'checkInTime',width:230,allowBlank:false},
					{xtype: 'f-textarea',fieldLabel: '补刷卡理由',name: 'reason',allowBlank:false}
				]
			},
			buttonConfig:[{xtype : 'f-button',text:'补刷卡',iconCls:'vcard',scope:this,privilegeCode: this.funcCode + '_prepareAdd',handler:this.prepareAdd}],
			rootConfig:{
				id:'f-0',
	        	checked:false 
			},
			 loader: new Ext.tree.TreeLoader({            
            	baseAttrs: { uiProvider: Ext.ux.TreeCheckNodeUI }
	        }),
			url:ctx+'/shiftConfig',
			listUrl:'/listEmployees'
		});
    	OrganizationEmployeeTree.superclass.initComponent.call(this);
    },
    prepareAdd : function(){
    	this.ajaxParams = {};
		this.saveType = 'add';		
		this.showWin();
    },
    saveItem : function(){
    	this.ajaxParams = {};
		this.fireEvent('beforesave',this.win);	
		this.saveBt.disable();
		this.win.formPanel.getForm().submit({           
            waitMsg:'保存中...',
			url:ctx+'/attendanceLog/create' + urlPostPrefix,
			params : this.ajaxParams ,
			scope:this,
			success:function(form, action) {
				this.closeWin();
			},        	
            failure:function(form, action) {
            }
        });
    },
    showWin:function(){
    	if(this.fireEvent('beforewinshow', this) !== false && this.formConfig){
			this.createWin();
			this.win.show();
			var node = this.getSelectionModel().getSelectedNode();
			Ext.getCmp('attendancePlus_name').setValue({id:node.id.split('-')[1],text:node.text.split("<")[0]});
			this.fireEvent('winshow',this.win);	
		}	
    }
});

AttendancePlusEditWindow = Ext.extend(Ext.app.FormWindow,{
	initComponent : function(){
		Ext.apply(this, {
			winConfig:{
				title:'修改员工考勤增补信息',
				iconCls:'calendar',
				bigIconClass : 'calendarIcon',
				desc:'可对员工出差,请假,加班等情况进行录入',
				width:400,
			    height:400
			},
			//layout:'fit',
			formConfig:{
				buttons:[{
					text:'保存',
					scope:this,
					handler:this.submitForm
				}],
				items:[
				{
					xtype:'hidden',
            		id:'AttendancePlusForm-employeeIds',
            		name:'employeeIds'
				},{
					xtype:'f-textarea',
            		fieldLabel:'员工',
            		id:'AttendancePlusForm-employeeNames',
            		name:'employeeNames',
            		readOnly:true
				},{
					xtype:'hidden',
            		name:'timeRange',
            		value:'ALL_DAY'
				}
				,{
					xtype:'f-text',
            		fieldLabel:'起始日期',
            		id:'AttendancePlusForm-startDate',
            		name:'startDate',
            		readOnly:true
				},{
					xtype:'f-date',
            		fieldLabel:'结束日期',
            		id:'AttendancePlusForm-endDate',
            		name:'endDate',
            		readOnly:false
				},{
            		xtype:'radiogroup',
            		fieldLabel:'考勤增补类型',
            		id:'AttendancePlusForm-plusTypeGroup',
            		columns: 2,
            		items:[
            			{boxLabel:'出差',name:'attendancePlusType',inputValue:'TRAVEL'},
//            			{boxLabel:'培训',name:'attendancePlusType',inputValue:'TRAIN'},
            			{boxLabel:'加班',name:'attendancePlusType',inputValue:'OVERTIME',listeners:{
            				scope:this,
            				check:function(checkBox,flat){
            				Ext.getCmp('overTimeSet').setVisible(false);
            					if(flat){
            						Ext.getCmp('overTimeSet').setVisible(true);
            					}else{
//            						Ext.getCmp('overTimeSet').setVisible(false);
            						Ext.getCmp('overTimeSet').hidden = true;
            					}
            				}
            			}},
            			{boxLabel:'请假',name:'attendancePlusType',inputValue:'LEAVE',listeners:{
            				scope:this,
            				check:function(checkBox,flat){
            					Ext.getCmp('leavePlus').setVisible(false);
            					if(flat){
            						Ext.getCmp('leavePlus').setVisible(true);
            					}else{
            						Ext.getCmp('leavePlus').hidden = true;
            					}
            				}
            			}},
            			{boxLabel:'其他',name:'attendancePlusType',inputValue:'OTHER',listeners:{
            				scope:this,
            				check:function(checkBox,flat){
            					var reason = Ext.getCmp('reason');
            					reason.setVisible(false);
            					if(flat){
            						reason.setVisible(true);
            					}else{
            						reason.setVisible(false);
            						reason.hidden = true;
            					}
            				}
            			}},
            			{boxLabel:'取消该日考勤增补',name:'attendancePlusType',inputValue:'NONE',
            				id:'AttendancePlusForm-NONE-radio',hidden:true}
            		]
            	},{
            		xtype:'fieldset',id:'overTimeSet',layout:'form',hidden:true,title: '加班时间',bodyStyle: 'padding: 5px',Width:150,Height:100,
            		items:[
						{xtype:'timefield',fieldLabel:'始',format:'H:i',increment:10,name:'overTimeStart',width:150},
						{xtype:'timefield',fieldLabel:'末',format:'H:i',increment:10,name:'overTimeEnd',width:150}
            		]
            	},{
            		xtype:'fieldset',id:'reason',layout:'form',hidden:true,title:'其他原因',bodyStyle:'padding: 5px',Width:150,Height:100,
            		items:[
            		    {xtype:'f-textarea',fieldLabel: '原因',name: 'reason',height:50}
            		]
            	},{
            		xtype:'fieldset',id:'leavePlus',layout:'form',hidden:true,title:'请假类型',bodyStyle:'padding: 5px',Width:150,Height:100,
            		items:[
            		   {xtype: 'f-dict',fieldLabel: '请假类型',hiddenName: 'leaveType',kind: 'leaveType'}
            		]
            	}]
			}
		});
    	AttendancePlusEditWindow.superclass.initComponent.call(this);
    },
    initDateValue:function(d){
    	Ext.getCmp('AttendancePlusForm-startDate').setValue(d.startDate);
    	Ext.getCmp('AttendancePlusForm-endDate').setValue(d.endDate);
    	Ext.getCmp('AttendancePlusForm-employeeIds').setValue(d.employeeIds);
    	Ext.getCmp('AttendancePlusForm-employeeNames').setValue(d.employeeNames.split('<')[0]);
    },
    submitForm:function(){
    	var as = this.formPanel.getForm();
    	this.formPanel.getForm().submit({
    		url: ctx+'/attendancePlus/createPlusInfos',
    		scope:this,
    		success:function(){
    			this.close();
    			Ext.getCmp('MonthPanel').reloadMonthView();
    		}
    	});
    }
});

MonthPanel = Ext.extend(Ext.Panel,{
    initComponent : function(){
		Ext.apply(this, {
			ctCls:'ext-cal-monthview x-unselectable',
			html:'loading',
			monitorResize:true,
		    tpl:new Ext.calendar.BoxLayoutTemplate({
		    	id:'attendancePlusMonthView',
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
			    },'->']
			}
		});
		
		this.plusInfoStore = new Ext.data.JsonStore({
	        id: 'plusInfoStore',
			url: ctx+'/attendancePlus/getInfos',
	        root: 'data',
	        fields: [
	        	'date','timeRange','type'
	        ]
	    });
		
		MonthPanel.superclass.initComponent.call(this);
		
//		this.addEvents('dayclick');
    },
    afterRender: function() {
    	MonthPanel.superclass.afterRender.call(this);
    	
    	this.showMonthView(this.employeeId,new Date().getFirstDateOfMonth());
    	
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
    showMonthView:function(employeeId,startDate){
    	this.employeeId = employeeId;
    	this.startDate =  startDate;
    	this.plusInfoStore.load({
    		params:{
    			employeeId:employeeId,
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
    updateTitle:function(){
    	var tree = Ext.getCmp('OrganizationEmployeeTree');
		var node = tree.getSelectionModel().getSelectedNode();
		var employeeName = node.text;
		var month = this.startDate.format('Y年m月');;
		this.setTitle(employeeName+'-'+month);
		
    },
    reloadMonthView:function(){
    	this.showMonthView(this.employeeId,this.startDate);
    },
    onPrevClick: function() {
    	this.showMonthView(this.employeeId,this.tpl.startDate.add(Date.MONTH,-1));
    },

    // private
    onNextClick: function() {
        this.showMonthView(this.employeeId,this.tpl.startDate.add(Date.MONTH,1));
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

AttendancePlus = Ext.extend(Ext.Panel,{
	layout:'border',
	closable: true,
	hideMode:'offsets',
	initComponent : function(){
		
		this.monthPanel = new MonthPanel({
	            id:'MonthPanel',
	            title:'日历',
	            region:'center'
			})
		
		this.left = new OrganizationEmployeeTree({
			funcCode: this.funcCode,
			id:'OrganizationEmployeeTree',
			region:'west',
			title: '员工列表',
			split:true,
			width: 220,
			minSize: 175,
			maxSize: 400
		});
		
		this.right = new Ext.Panel({
			layout:'card',
			layoutConfig:{
				deferredRender:true
			},
			activeItem:0,
            id:'AttendancePluscalendarPanel',
            region:'center',
            defaults :{
            	border:false
            },
            items:[{
            	xtype:'panel',
            	html:'<div class="selectTips">请在左边的员工列表选择一位员工</div>'
            },this.monthPanel]
		});
		
		this.left.on('click',function(node,e){
    		if(node.attributes.type == 'employee'){
    			this.loadMonthView(node);
    		}
    	},this);
		
		this.monthPanel.on('dayclick',this.showEditWindow,this);
		
		this.items = [this.left,this.right];
		AttendancePlus.superclass.initComponent.call(this);
			
    },
	loadData:function(){
		
	},
	showEditWindow:function(date, el,type){
		var tree = Ext.getCmp('OrganizationEmployeeTree');
		var type = el.getAttribute('plusInfoType');
		var node = tree.getSelectionModel().getSelectedNode();
		//
		this.employeeIds = [];
		this.employeeNames = [];
		var nodes = this.left.getChecked();
		for(var i=0;i < nodes.length;i++){
			if(nodes[i].attributes.type != 'organization'){
    			this.employeeIds.push(nodes[i].id.split('-')[1]);
    			this.employeeNames.push(nodes[i].text);
    		}
		}
		//
		if(node.attributes.type == 'employee'){
			var editWin = new AttendancePlusEditWindow();
	        editWin.show();
	        editWin.initDateValue({
	      		startDate: date.format('Y-m-d'),
	      		endDate: date.format('Y-m-d'),
	      		employeeIds: this.employeeIds.join(','),
	      		employeeNames: this.employeeNames.join(',')
//	      		employeeIds: node.id.split('-')[1],
//	      		employeeNames: node.text
	        });
	        if(type != 'NONE'){
	        	//Ext.getCmp('AttendancePlusForm-startDate').set
    			Ext.getCmp('AttendancePlusForm-endDate').hide();
    			Ext.getCmp('AttendancePlusForm-NONE-radio').show();
	        }
		}
	},
    loadMonthView:function(node){
    	Ext.getCmp('MonthPanel').employeeId = node.id.split('-')[1];
    	if(Ext.getCmp('AttendancePluscalendarPanel').layout.activeItem.id == 'MonthPanel'){
    		Ext.getCmp('MonthPanel').reloadMonthView();
    	}else{
	    	Ext.getCmp('AttendancePluscalendarPanel').layout.setActiveItem(1); 
    	}
    }
});