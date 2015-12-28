BranchShiftAnalysisStatGrid = Ext.extend(Ext.app.BaseFuncPanel,{
	initComponent : function(){
		Ext.apply(this,{
			title:'人员列表',
			region:'center',
//			width:220,
			buttonConfig : [{
				text : '情况说明',
				iconCls : 'pencil',
				prililegeCode:this.funcCode+'_explain',
				scope : this,
				disabled : true,
				handler: this.edit
			},{
				xtype:'f-button',
				text : '生成报表',
	            tooltip : '根据查询出来的数据输出报表',
	            iconCls : 'excel',
				scope:this,
				disabled : true,
				handler:this.generateReport
			},'->',{
				text : '重新统计',
				iconCls:'chart',
				prililegeCode:this.funcCode+'_statistics',
				scope : this,
				enableOnEmpty:true,
				handler : this.reCalculate
			}],
			gridConfig:{
				sm:new Ext.grid.RowSelectionModel(),
				cm:new Ext.grid.ColumnModel([
					new Ext.grid.RowNumberer(),
					{header: '所属行',dataIndex:'orgName'},
					{header: '姓名',dataIndex:'name'},
					{header: '岗位',dataIndex:'postionText'},
		            {header: '总计排班统计(日)',dataIndex:'workdays',id:'workdays-id'},
		            {header: '周末排班统计(日)',dataIndex:'weekdays',id:'weekdays-id'},
		            {header: '排班时间统计（小时）',dataIndex:'branchShiftTimeTotal',id:'branchShiftTimeTotal-id'},
		            {header: '派驻网点',dataIndex:'branchShiftState',id:'branchShiftState-id'},
		            {header: '派驻网点排班统计(小时)',dataIndex:'branchShiftTimeTotal',id:'branchShiftTimeTotal-id2'},
		            {header: '休息日统计(日)',dataIndex:'restdays',id:'restdays-id'},
		            {header: '描述',dataIndex:'notesDesc'}
				]),	
				storeMapping:[
					'name', 'workdays','weekdays','notesDesc','orgName','postionText','branchShiftTimeTotal',
					'restdays','branchShiftTimeTotal','branchShiftState','id'
				]
			},
			winConfig : {
				height : 210,
				desc : '描述排班过多或者过长的原因',
				bigIconClass : 'resetKeyIcon',
				title : '原因描述'
			},
			formConfig : {
				items : [{
					xtype : 'f-textarea',
					fieldLabel : '原因描述',
					name : 'notesDesc',
					allowBlank : false
				}]
			},
			url: ctx + '/branchShiftAnalysis'
	    });
	    
		BranchShiftAnalysisStatGrid.superclass.initComponent.call(this);
	},
	reCalculate : function(){
		var r = Ext.getCmp('BranchShiftAnalysis_left').getSelectionModel().getSelected();
		var orgValue = Ext.getCmp('BranchShiftAnalysis_organization').getValue();
		if(r){
			Ext.Ajax.request({
				url : ctx + '/branchShiftAnalysis/reCalculate',
				params: { 
					analysisType : r.data.analysisType,
					days : r.data.days,
					yearMonth : Ext.getCmp('BranchShiftAnalysisYear').getValue() + '-' + Ext.getCmp('BranchShiftAnalysisMonth').getValue(),
					organizationId:orgValue
				},
				scope : this,
				success : function(){
					var a = Ext.getCmp('BranchShiftAnalysis_center');
					var b = a.getStore();
					Ext.getCmp('BranchShiftAnalysis_center').getStore().reload();
				}
			});
		}else{
			App.msg('请先选择左边的统计类型');			
		}
	},
	generateReport:function(){
		var r = Ext.getCmp('BranchShiftAnalysis_left').getSelectionModel().getSelected();
		var orgValue = Ext.getCmp('BranchShiftAnalysis_organization').getValue();
		var yearMonth  = Ext.getCmp('BranchShiftAnalysisYear').getValue() + '-' + Ext.getCmp('BranchShiftAnalysisMonth').getValue();
		var paramsMap = "analysisType="+r.data.analysisType+"&days="+r.data.days+"&yearMonth="+yearMonth+"&organizationId="+orgValue;
		document.location.href = ctx+'/branchShiftAnalysis/generateReport?'+paramsMap;
	}
});

BranchShiftAnalysis = Ext.extend(Ext.Panel,{
	layout:'border',
	closable: true,
	hideMode:'offsets',
	initComponent : function(){
		var twoTbar=new Ext.Toolbar({  
   			items:[  
        		new Ext.Toolbar.TextItem('部门：'),
        		{
	        		xtype: 'treeField',
	        		fieldLabel: '部门',
	        		listHeight:240,
	        		emptyText:'请选择要查询的部门',
	        		id:'BranchShiftAnalysis_organization',
	        		hiddenName: 'organization',
					name:'organization',
					dataUrl : ctx+'/organization/getOrganizationByRole',
					readOnly:false
				}
        	]
		}); 
		this.left = new Ext.grid.GridPanel({
			id:'BranchShiftAnalysis_left',
			title:'排班情况列表',
			region:'west',
			width:220,
			margins:'0 5 0 0',
			tbar : [{
				xtype : 'f-year',
				id : 'BranchShiftAnalysisYear',
				width:80
			},{
				xtype : 'f-month',
				id : 'BranchShiftAnalysisMonth',
				width:80
			}],
	        store: new Ext.data.ArrayStore({
	        	autoDestroy : true,
		        fields : [
		           'titles','analysisType','days'
		        ],
		        data : [
		        	['全行排班情况','BRANCH_IN_TOTAL',30],
		        	['外行顶班情况','OTHER_BRANCH_IN_TOTAL',8],
		        	['周末排班超过3天','MORE_IN_WEEKEND',3],
		        	['周末排班0天','EQUAL_IN_WEEKEND',0],
		        	['周末排班不足3天','LESS_IN_TOTAL',3],
		        	['休息日超过8天','REST_MORE_THAN',8]
		        ]
		    }),
		    viewConfig: {
			    forceFit: true
			},
	        columns: [
	            {header: '类型',dataIndex:'titles'}
	        ]
	    });
		
		
		this.center = new BranchShiftAnalysisStatGrid({id:'BranchShiftAnalysis_center'});
	    
		this.items = [this.left,this.center];
		
		BranchShiftAnalysis.superclass.initComponent.call(this);
		
		this.left.getSelectionModel().on('rowselect',function(sm,rowIndex,r){
			this.searchStat(r);
			var cm = Ext.getCmp('BranchShiftAnalysis_center').gridConfig.cm;
			var weekdaysIndex = cm.getIndexById('weekdays-id');
			var workdaysIndex = cm.getIndexById('workdays-id');
			var restdaysIndex = cm.getIndexById('restdays-id');
			var branchShiftTimeTotalIndex = cm.getIndexById('branchShiftTimeTotal-id');
			var branchShiftTimeTotalIndex2 = cm.getIndexById('branchShiftTimeTotal-id2');
			var branchShiftStateIndex =  cm.getIndexById('branchShiftState-id');
			 
			if(loginUser.ownRole("总行管理员")||loginUser.ownRole("系统管理员")){
				
				if(r.data.analysisType=='REST_MORE_THAN'){
					cm.setHidden(weekdaysIndex,true);
					cm.setHidden(workdaysIndex,true);
					cm.setHidden(restdaysIndex,false);
					cm.setHidden(branchShiftTimeTotalIndex,true);
				}else if(r.data.analysisType=='OTHER_BRANCH_IN_TOTAL'){
					cm.setHidden(weekdaysIndex,true);
					cm.setHidden(workdaysIndex,true);
					cm.setHidden(restdaysIndex,true);
					cm.setHidden(branchShiftTimeTotalIndex,true);
				}else{
					cm.setHidden(restdaysIndex,true);
					cm.setHidden(weekdaysIndex,false);
					cm.setHidden(workdaysIndex,false);
					cm.setHidden(branchShiftTimeTotalIndex,false);
	 				cm.setHidden(branchShiftStateIndex,true);
					cm.setHidden(branchShiftTimeTotalIndex2,true);
		  		}
			}else{
				cm.setHidden(branchShiftTimeTotalIndex,true);
				if(r.data.analysisType=='REST_MORE_THAN'){
					cm.setHidden(weekdaysIndex,true);
					cm.setHidden(workdaysIndex,true);
					cm.setHidden(restdaysIndex,false);
				}else{
					cm.setHidden(restdaysIndex,true);
					cm.setHidden(weekdaysIndex,false);
					cm.setHidden(workdaysIndex,false);
					
				}
			}
			
		},this);
		this.left.on('render',function(){
			twoTbar.render(this.left.tbar);
		},this);
	},
	searchStat : function(r){
		var orgValue = Ext.getCmp('BranchShiftAnalysis_organization').getValue();
//		if(orgValue == ''){
//			App.msg('请选择要查询的部门');
//		}else{
			this.center.getStore().load({
				params: { 
					analysisType : r.data.analysisType,
					days : r.data.days,
					yearMonth : Ext.getCmp('BranchShiftAnalysisYear').getValue() + '-' + Ext.getCmp('BranchShiftAnalysisMonth').getValue(),
					organizationId:orgValue
				}
			});
//		}
	},
	loadData:function(){
	}
	
});