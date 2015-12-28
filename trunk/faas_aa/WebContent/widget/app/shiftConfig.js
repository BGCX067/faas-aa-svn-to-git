ShiftConfigEmployeeTree = Ext.extend(Ext.tree.TreePanel,{
	initComponent : function(){
		Ext.apply(this, {
			animate:false,
			tbar:[
			      {xtype:'button',id:'checkButton',text:'全选',handler:function(){
			    	  var as = Ext.getCmp('ShiftConfigEmployeeTree').root;
			    	  var childNodes=as.childNodes;
			    	  var check = Ext.getCmp('isChecked').getValue();
			    	  if(check=='noChecked'){
			    		  Ext.getCmp('checkButton').setText('全取');			    	
			    		  if(childNodes.length >0){
				    		  Ext.each(childNodes,function(node){
				    			  node.ui.toggleCheck(true);
					    	  });
				  		  }
			    		  Ext.getCmp('isChecked').setValue('checked');
			    		  Ext.getCmp('ShiftConfig').onTreeClick();
			    	  }else if(check=='checked'){
			    		  Ext.getCmp('checkButton').setText('全选');
			    		  Ext.each(childNodes,function(node){
			    			  node.ui.toggleCheck(false);
				    	  }); 
			    		  Ext.getCmp('isChecked').setValue('noChecked');
			    		  Ext.getCmp('ShiftConfig').onTreeClick();
			    	  }	
			      }
			   },{xtype:'f-text',id:'isChecked',hidden:true,value:'noChecked'}
			   ,'-',{xtype:'f-button',text:'默认排班设置',privilegeCode: this.funcCode + '_working',tooltip:'设置下一月的排班',iconCls:'pencil',scope:this,handler:this.createAllShiftConfig},
			   {xtype:'f-button',text:'取消网点错误排班',privilegeCode:this.funcCode+'_cancalMistake',tooltip:'删除网点在班次批量设置中的错误排版',iconCls:'remove',scope:this,handler:this.deleteMistake}
            ],
            loader: new Ext.tree.TreeLoader({            
            	baseAttrs: { uiProvider: Ext.ux.TreeCheckNodeUI },
				dataUrl :ctx+'/shiftConfig/listEmployees'
	        }),
	        root: new Ext.tree.AsyncTreeNode({ 
	        	id:'f-0',
	        	checked:false 
	        })
		});
		
    	ShiftConfigEmployeeTree.superclass.initComponent.call(this);
    },
	createAllShiftConfig:function(){
    	var sa = this.getChecked('id');
    	Ext.Ajax.request({
    		url: ctx+'/shiftConfig/createNextMonthShiftConfig',
    		params: {employeeIds:this.getChecked('id')},
    		scope:this,
			success:function(response, options){
    			App.msg("下月排班设置成功");
			}
		});
    },
    deleteMistake:function(){
    	Ext.Ajax.request({
    		url: ctx+'/shiftConfig/deleteMistake',
    		scope:this,
			success:function(response, options){
    			App.msg("删除成功");
			}
		});
    }
});


ShiftConfigMonthPanel = Ext.extend(Ext.Panel,{
    initComponent : function(){
		Ext.apply(this, {
			ctCls:'ext-cal-monthview x-unselectable',
			html:'loading',
		    tpl:new Ext.calendar.BoxLayoutTemplate({
		    	id:'shiftConfigMonthView',
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
			    },'->',{
			    	xtype: 'tbtext',
			    	id:'ShiftConfigTipText',
			    	text: '点击日历单元格选择要排班的日期'
			    },{
		    		text:'设置班次时间',
		    		scope:this,
		    		handler:function(){
		    			this.fireEvent('setupclick');
		    		}
		    	}]
			}
		});
		
		ShiftConfigMonthPanel.superclass.initComponent.call(this);
		
		this.addEvents('dayclick','setupclick');
		
    },
    afterRender: function() {
    	ShiftConfigMonthPanel.superclass.afterRender.call(this);
    	
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
    showMonthView:function(startDate){
    	this.startDate =  startDate||this.startDate;
    	var month = this.startDate.format('Y年m月');
    	this.setTitle(month);
//    	this.updateTitle();
		this.tpl.overwrite(this.body,{
			bodyHeight:this.body.getSize().height,
    		startDate:this.startDate,
    		plusInfos:{}
    	});
    },

    reloadMonthView:function(){
    	this.showMonthView(this.startDate);
    },
    updateTitle:function(){
		var month = this.startDate.format('Y年m月');
//		this.setTitle(month+'-'+this.employeeNames);
    },
    onPrevClick: function() {
    	this.showMonthView(this.tpl.startDate.add(Date.MONTH,-1));
    },
    // private
    onNextClick: function() {
        this.showMonthView(this.tpl.startDate.add(Date.MONTH,1));
    },
    getDayViewId:function(date){
    	return this.tpl.id+'-day-'+date.format('Ymd');
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


ShiftConfigEditWindow = Ext.extend(Ext.app.FormWindow,{
	initComponent : function(){
		Ext.apply(this, {
			winConfig:{
				title:'设置员工排班',
				iconCls:'calendar',
				bigIconClass : 'calendarIcon',
				desc:'可设置员工的上下班时间',
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
            		id:'ShiftConfigForm-employeeIds',
            		name:'employeeIds'
				},{
					xtype:'f-textarea',
            		fieldLabel:'员工',
            		id:'ShiftConfigForm-employeeNames',
            		name:'employeeNames',
            		readOnly:true
				},{
					xtype:'f-textarea',
            		fieldLabel:'日期',
            		id:'ShiftConfigForm-dates',
            		name:'dates',
            		readOnly:true
				},{
					xtype:'timefield',
            		fieldLabel:'上班时间',
            		value:'08:30',
            		format:'H:i',
            		increment:30,
            		width:230,
            		id:'ShiftConfigForm-startTime',
            		name:'startTime'
				},{
					xtype:'timefield',
            		fieldLabel:'下班时间',
            		value:'18:00',
            		format:'H:i',
            		increment:30,
            		width:230,
            		id:'ShiftConfigForm-endTime',
            		name:'endTime'
				},{
					xtype:'checkbox',
					boxLabel:'工作到次日',
					name:'nextDay'
				},{
					xtype:'checkbox',
					boxLabel:'取消班次',
					name:'cancelShiftConfig'
				}]
			}
		});
    	ShiftConfigEditWindow.superclass.initComponent.call(this);
    },
    initDateValue:function(d){
    	Ext.getCmp('ShiftConfigForm-employeeIds').setValue(d.employeeIds);
    	Ext.getCmp('ShiftConfigForm-employeeNames').setValue(d.employeeNames);
    	Ext.getCmp('ShiftConfigForm-dates').setValue(d.dates);

    },
    submitForm:function(){
    	this.formPanel.getForm().submit({
    		url: ctx+'/shiftConfig/createShiftConfig',
    		scope:this,
    		success:function(){
    			this.close();
    		}
    	});
    }
});

ShiftConfig = Ext.extend(Ext.Panel,{
	layout:'border',
	closable: true,
	hideMode:'offsets',
	initComponent : function(){
		
		this.left = new ShiftConfigEmployeeTree({
			funcCode: this.funcCode,
			id:'ShiftConfigEmployeeTree',
			region:'west',
			title: '员工列表',
			split:true,
			width: 220,
			minSize: 175,
			maxSize: 400
		});
		
		this.tipsPanel = new Ext.Panel({
			html:'<div class="selectTips">请在左边的员工列表选择一位或者多位员工</div>'
		});
		
		this.shiftConfigMonthPanel = new ShiftConfigMonthPanel({
            id:'ShiftConfigMonthPanel',
            title:'日历',
            region:'center'
		})
		
		this.right = new Ext.Panel({
			layout:'card',
			layoutConfig:{
				deferredRender:true
			},
			activeItem:0,
            id:'shiftConfigCalendarPanel',
            region:'center',
            defaults :{
            	border:false
            },
            items:[
            	this.tipsPanel,
            	this.shiftConfigMonthPanel
            ]
		});
		this.items = [this.left,this.right];
		ShiftConfig.superclass.initComponent.call(this);
		
		this.left.on('checkchange',function(node,checked,e){
    		this.onTreeClick(checked);
    	},this);
    	
    	this.shiftConfigMonthPanel.on('dayclick',this.onDayClick,this);
    	
    	this.shiftConfigMonthPanel.on('setupclick',this.onSetupClick,this);
		
			
    },
	loadData:function(){
		
	},
	showEditWindow:function(date, el){
		var tree = Ext.getCmp('OrganizationEmployeeTree');
		var node = tree.getChecked();
		if(node.attributes.type == 'employee'){
			var editWin = new AttendancePlusEditWindow();
	        editWin.show();
	        editWin.initDateValue({
	      		startDate: date.format('Y-m-d'),
	      		endDate: date.format('Y-m-d'),
	      		employeeId: node.id.split('-')[1],
	      		employeeName: node.text
	        });
		}
	},
	onTreeClick:function(checked){
//    	this.employeeIds = [];
//		this.employeeNames = [];
//		var nodes = this.left.getChecked();
//		for(var i=0;i < nodes.length;i++){
////			if(nodes[0].attributes.type == 'organization'){
////				nodes = nodes[0].childNodes;
////			}
//			if(nodes[i].attributes.type != 'organization'){
//    			this.employeeIds.push(nodes[i].id.split('-')[1]);
//    			this.employeeNames.push(nodes[i].text);
//    			nodes[i].checked=checked;
//    		}
//		}
		this.loadMonthView();
		
    },
    loadMonthView:function(){
    	this.right.layout.setActiveItem(1);
    	this.shiftConfigMonthPanel.startDate = this.shiftConfigMonthPanel.startDate || new Date().getFirstDateOfMonth();
//    	this.shiftConfigMonthPanel.employeeNames = this.employeeNames.join(',');
    	this.shiftConfigMonthPanel.showMonthView();
    	this.selectedDates = [];
    },
   
    onDayClick:function(date, el){
    	var dateString = date.format('Y-m-d');
    	var elId = this.shiftConfigMonthPanel.getDayViewId(date);
    	this.selectedDates = this.selectedDates || [];
    	if(this.selectedDates.indexOf(dateString) > -1){
    		this.selectedDates.remove(dateString);
    		Ext.get(elId).update('');
    	}else{
    		this.selectedDates.push(dateString);
    		Ext.get(elId).update('<div class="selectedDate">上班</div>');
    	}
    },
    onSetupClick:function(){
    	this.employeeIds = [];
		this.employeeNames = [];
		var nodes = this.left.getChecked();
		for(var i=0;i < nodes.length;i++){
			if(nodes[i].attributes.type != 'organization'){
    			this.employeeIds.push(nodes[i].id.split('-')[1]);
    			this.employeeNames.push(nodes[i].text);
    		}
		}
    	if(this.selectedDates.length >0 && this.employeeIds.length > 0){
    		var editWin = new ShiftConfigEditWindow();
	        editWin.show();
	        editWin.initDateValue({
	      		employeeIds: this.employeeIds.join(','),
	      		employeeNames: this.employeeNames.join(','),
	      		dates: this.selectedDates.join(',')
	        });
    	}else{
    		App.msg('未选中员工或者日期!');
    	}
    	
    }
});

