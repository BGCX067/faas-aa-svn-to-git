ShiftBrowseEmployeeTree = Ext.extend(Ext.tree.TreePanel,{
	initComponent : function(){
		Ext.apply(this, {
			animate:false,
			tbar:[
            ],
            loader: new Ext.tree.TreeLoader({            
				dataUrl :ctx+'/shiftConfig/listEmployees',
				baseParams:{checkBox:false}
	        }),
	        root: new Ext.tree.AsyncTreeNode({ 
	        	id:'f-0',
	        	checked:false 
	        })
		});
		
    	ShiftBrowseEmployeeTree.superclass.initComponent.call(this);
    	
    }
});

ShiftBrowseMonthPanel = Ext.extend(Ext.Panel,{
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
			url: ctx+'/shiftConfig/getInfos',
	        root: 'data',
	        fields: [
	        	'date','timeRange','type'
	        ]
	    });
		
		ShiftBrowseMonthPanel.superclass.initComponent.call(this);
		
    },
    exportOrgShift:function(){835444088
    	
    },
    afterRender: function() {
    	ShiftBrowseMonthPanel.superclass.afterRender.call(this);
    	
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
    			Ext.getCmp('ShiftBrowseMonthPanel').reloadMonthView();
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
    			Ext.getCmp('ShiftBrowseMonthPanel').reloadMonthView();
    		}
    	});
    }
});



ShiftBrowse = Ext.extend(Ext.Panel,{
	layout:'border',
	closable: true,
	hideMode:'offsets',
	initComponent : function(){
		
		this.left = new ShiftBrowseEmployeeTree({
			funcCode: this.funcCode,
			id:'ShiftBrowseEmployeeTree',
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
		
		this.shiftBrowseMonthPanel = new ShiftBrowseMonthPanel({
            id:'ShiftBrowseMonthPanel',
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
            	this.shiftBrowseMonthPanel
            ]
		});
		
		
		this.items = [this.left,this.right];
		ShiftBrowse.superclass.initComponent.call(this);
		
		this.shiftBrowseMonthPanel.on('dayclick',this.onDayClick,this);
		
		this.left.on('click',function(node,e){
    		this.onTreeClick(node);
    	},this);
    	
			
    },
	loadData:function(){
		
	},
	onDayClick:function(date, el){
    	var tree = Ext.getCmp('ShiftBrowseEmployeeTree');
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
    		this.shiftBrowseMonthPanel.employeeId = node.id.split('-')[1];
    		this.shiftBrowseMonthPanel.employeeName = node.text;
    		if(this.right.layout.activeItem.id == 'ShiftBrowseMonthPanel'){
	    		this.shiftBrowseMonthPanel.reloadMonthView();
	    	}else{
		    	this.right.layout.setActiveItem(1); 
	    	}
		}
		
    }

});

