var urlPostPrefix = '';
Ext.app.BaseFuncPanel = Ext.extend(Ext.grid.GridPanel, {
	closable: true,
	border:true,
	itemSize:30,
	paging:true,	
	loadFromGrid:true,
	itemStepButton : false,
	pagingBar : true,
	
	initComponent : function(){
		this.recordConfig = Ext.data.Record.create(this.gridConfig.storeMapping);
		this.store = new Ext.data.JsonStore({
		    url: this.url+(this.listUrl?this.listUrl:'/list') + urlPostPrefix,
			root: 'data',
	        totalProperty: 'totalCount',
	        id: 'id',
	        fields: this.recordConfig
		});
		
		//分页配置
		this.pagingToolBar = new Ext.PagingToolbar({
            pageSize: this.itemSize,
            store: this.store,
            displayInfo: true,
			emptyMsg: '没有可以显示的数据'
	    });
		this.noPagingToolBar = new Ext.app.noPagingToolbar({
			 store: this.store
		});
		
		Ext.apply(this, this.gridConfig || {}, {    
	        sm: new Ext.grid.RowSelectionModel({singleSelect:true}),        
	        viewConfig: { forceFit : true ,emptyText : '没有可用的数据'},
	        autoScroll:true,
	        loadMask: true,
	        tbar: this.getTopButtons()
		});
		
		//是否显示分页工具栏
		if(this.pagingBar){
			Ext.apply(this, this.gridConfig || {}, {    
		        bbar: this.paging == true ? this.pagingToolBar : this.noPagingToolBar
			});
		}
		
		if(this.itemStepButton){
			this.includeItemStepButton();
		}
		
		Ext.app.BaseFuncPanel.superclass.initComponent.call(this);
		
		//添加事件
		this.addEvents(
			'beforewinshow',
			'winshow',
			'beforedel',
			'beforesave',
			'afteradd',
			'afterload',
			'afterupdate'
        );
		
        //捕获事件		
		this.getSelectionModel().on('rowselect',function(sm,rowIndex,record){
			this.itemSelect();
		},this); 
		
		//点击刷新按钮时应用上次查询的条件
		this.pagingToolBar.on('beforechange',function(toolBar,params){
			Ext.applyIf(params,this.store.lastOptions.params);
		},this); 
				
		this.store.on('load',function(store,records,options){
			if(this.store.getTotalCount() > 0){
				this.getTopToolbar().setDisabled(false);
				if(!this.getSelectionModel().hasSelection()){
					//当是以按"上一条"方式翻页时选中最后一条
					if(this.stepPrevious){
						this.getSelectionModel().selectLastRow();
						this.stepPrevious = false;
					}else{
						this.getSelectionModel().selectFirstRow();
					}
					
				}
			}else{
				//disable every button except other widget
				//this.getTopToolbar().setDisabled(true);
				this.getTopToolbar().items.each(function(item,index,length){
					if((item.xtype == 'button'||item.type == 'button') && (item.enableOnEmpty != true))
						item.disable();
				});
			}
		},this); 
		
		this.on('rowdblclick',function(grid,rowIndex,e){
			if(!this.editBt.disabled &&  this.editBt.isVisible() ){
				this.edit();
			}
		},this);
    },
    onDestroy : function(){
        if(this.win)
			this.win.destroy();
		this.getSelectionModel().purgeListeners();
		this.store.purgeListeners();
        Ext.app.BaseFuncPanel.superclass.onDestroy.call(this);
    },
	itemSelect : function(){
		this.selectedRecord = this.getSelectionModel().getSelected();
		this.selectedId = this.selectedRecord.id;
	},
	includeItemStepButton : function(){
		this.prevItem = {
			text : '上一条',
			id : 'recognized_prev',
			iconCls : 'x-tbar-page-prev',
			scope : this,
			handler : function(){
				if(!this.getSelectionModel().selectPrevious()){
					if(!this.pagingToolBar.previousPage()){
						App.msg("已到达最前一条");
					}else{
						this.stepPrevious = true;
					}
				}
		    }
		}
		this.nextItem = {
			text : '下一条' ,
			id : 'recognized_next',
			cls : 'right-icon',
			iconCls : 'x-tbar-page-next',
			scope : this,
			handler : function(){
				if(!this.getSelectionModel().selectNext()){
					if(!this.pagingToolBar.nextPage()){
						App.msg("已到达最后一条");
					}
				}
		    }
		}
	},
	getTopButtons : function(){
		this.addBt = new Ext.app.Button({
			text:'新增',
            tooltip:'新增一条新记录',
            iconCls:'add',
            enableOnEmpty : true,
			privilegeCode:this.funcCode ? this.funcCode+'_add' : '',
			scope:this,
			handler:this.prepareAdd
		});
		
		this.delBt = new Ext.app.Button({
			text:'删除',
            tooltip:'删除选中的已有记录',
            iconCls:'remove',
			privilegeCode:this.funcCode ? this.funcCode+'_del': '',
			disabled : true,
			scope:this,
			handler:this.prepareDel
		});
		
		this.editBt = new Ext.app.Button({
			text:'修改',
            tooltip:'修改选中的记录',
            iconCls:'pencil',
            privilegeCode:this.funcCode ? this.funcCode+'_edit': '',
			scope:this,
			disabled : true,
			handler:this.edit
		});
		
		//按钮的配置
		var customBt = [];
		if(this.buttonConfig){
			for(var i=0;i < this.buttonConfig.length;i++){
				if(typeof(this.buttonConfig[i]) == 'string' && this.buttonConfig[i] == 'all'){
					customBt = [ this.addBt,this.delBt,this.editBt ]
				}else if(typeof(this.buttonConfig[i]) == 'string' && this.buttonConfig[i] == 'add'){
					customBt.push(this.addBt);
				}else if(typeof(this.buttonConfig[i]) == 'string' && this.buttonConfig[i] == 'del'){
					customBt.push(this.delBt);
				}else if(typeof(this.buttonConfig[i]) == 'string' && this.buttonConfig[i] == 'edit'){
					customBt.push(this.editBt);
				}else if(typeof(this.buttonConfig[i]) == 'string' ){
					customBt.push(this.buttonConfig[i]);
				}else if(typeof(this.buttonConfig[i]) == 'object' && this.buttonConfig[i].xtype == 'f-search'){
					customBt.push('查询：');
					this.buttonConfig[i].store = this.store;
					this.buttonConfig[i].pageParams = {start: 0,limit: this.itemSize} ;
					customBt.push(this.buttonConfig[i]);
				}else if(typeof(this.buttonConfig[i]) == 'object' && this.buttonConfig[i].xtype){
					customBt.push(this.buttonConfig[i]);
				}else if(typeof(this.buttonConfig[i]) == 'object'){
					customBt.push(new Ext.app.Button(this.buttonConfig[i]));
				}
			}
			
		}else{
			customBt = [ this.addBt,this.delBt,this.editBt ]
		}
		delete this.buttonConfig;
		return customBt;
	},
	createWin : function(){
		var winConf = Ext.apply({},this.winConfig||{},{
			iconCls : this.iconCls,
			title : this.title,
			keys: [{
	            key: Ext.EventObject.ENTER,
	            fn: this.saveItem,
	            scope: this
	        }]
		});
		
		var formConf = Ext.apply({},this.formConfig||{},{
			buttons : this.getWinButtons(this.winConfig.buttons)
		});
		
		this.win = new Ext.app.FormWindow({
			winConfig :  winConf,
			formConfig : formConf
		});
	},
	getWinButtons : function(buttonConfigs){
		this.saveBt = new Ext.app.Button({
			text:'保存',
            tooltip:'保存修改的内容',
            minWidth:75,
            type : 'submit',
            formBind: true,
			scope:this,
			handler:this.saveItem
		});
		this.closeBt = new Ext.app.Button({
			text:'关闭',
            tooltip:'关闭该对话框',
            minWidth:75,
			scope:this,
			handler: function(){
				this.closeWin();
			}
		});
		
		var winButtons = [];
		if(buttonConfigs){
			for(var i=0;i < buttonConfigs.length;i++){
				if(typeof(buttonConfigs[i]) == 'string' && buttonConfigs[i] == 'all'){
					winButtons = [this.saveBt];
				}else if(typeof(buttonConfigs[i]) == 'string' && buttonConfigs[i] == 'save'){
					winButtons.push(this.saveBt);
				}else if(typeof(buttonConfigs[i]) == 'string' && buttonConfigs[i] == 'close'){
					winButtons.push(this.closeBt);
				}else if(typeof(buttonConfigs[i]) == 'object'){
					winButtons.push(buttonConfigs[i]);
				}
			}
		}else{
			winButtons = [this.saveBt];
		}
		return winButtons;
	},
	showWin : function(){
		if(this.fireEvent('beforewinshow', this) !== false && this.formConfig){
			this.createWin();
			this.win.show();			
			this.fireEvent('winshow',this.win);	
		}		
	},
	closeWin : function(){
		if(this.win){
			this.win.close();
			delete this.win;
		}
	},
	prepareAdd : function(){
		this.ajaxParams = {};
		this.saveType = 'add';
		this.saveId = '';
		this.showWin();			
	},
	saveItem : function(){
		if(!this.saveBt || this.saveBt.disabled){
			return;
		}
		this.ajaxParams = {};
		if(this.fireEvent('beforesave',this.win) !== false){
			this.saveBt.disable();
			if(this.saveType == 'add'){
				this.ajaxParams['parentId'] = this.parentId;
				this.win.formPanel.getForm().submit({
		            waitMsg:'保存中...',
					url:this.url+'/create' + urlPostPrefix,
					params: this.ajaxParams,
					scope:this,
					success:function(form, action) {
						this.closeWin();
						this.loadData();
						this.fireEvent('afteradd',form,action);
		            },        	
		            failure:function(form, action) {
		            	this.saveBt.enable();
		            }
		        });
			}else{
				this.ajaxParams['id'] = this.selectedId;
				this.win.formPanel.getForm().submit({           
		            waitMsg:'保存中...',
					url:this.url+'/update' + urlPostPrefix,
					params: this.ajaxParams,
					scope:this,
					success:function(form, action) {
						this.closeWin();
						this.loadData();
						this.fireEvent('afterupdate',form,action);
		            },        	
		            failure:function(form, action) {
		            	this.saveBt.enable();
		            }
		        });
			}		
		}
		
		
	},
	prepareDel : function(){
		this.ajaxParams = {};
		if(this.fireEvent('beforedel') !== false){
			Ext.MessageBox.confirm('删除确认','您确实要删除该记录吗?该操作不能撤销!',this.delItem,this);
		}			
	},
	delItem : function(btn){	
		if(btn == 'yes'){
			this.ajaxParams['id'] = this.selectedId;
			Ext.Ajax.request({
				url:this.url+'/del' + urlPostPrefix,
				params: this.ajaxParams,
				scope:this,
				success:function(response, options) {
					this.loadData();
				}
			});

		}	
	},
	edit : function(){
		this.ajaxParams = {};
		this.saveType = 'update';
		this.saveId = this.selectedId;
		this.showWin();
		this.ajaxParams['id'] = this.selectedId;
		if(this.loadFromGrid){
			var record = this.store.getById(this.selectedId)
			this.win.formPanel.getForm().loadRecord(record);
			this.fireEvent('afterload',this.win);	
		}else{
			this.win.formPanel.getForm().load({
				url:this.url+'/edit' + urlPostPrefix,
				params : this.ajaxParams,
	            waitMsg:'加载中...',
				scope:this,
				success:function(form, action) {
					this.fireEvent('afterload',this.win);
				}
			});	
		}	
	},
	loadData : function(o,callback,scope){
		var storeParams = {};
		if(o){
			Ext.apply(storeParams,o);
		}
		if(this.store.lastOptions && this.store.lastOptions.params){
			Ext.applyIf(storeParams,this.store.lastOptions.params);
		}
		if(this.paging){
			storeParams.start = storeParams.start||0;
			storeParams.limit = storeParams.limit||this.itemSize;
		}
		this.store.load({params: storeParams,callback :callback,scope:scope});
	}
});

Ext.app.BaseFuncTree = Ext.extend(Ext.tree.TreePanel, {	
	closable : true,
	border : true,
	reloadAfterUpdate : false,
	initComponent : function(){
		Ext.apply(this, {			
			loader: new Ext.tree.TreeLoader({
		        dataUrl:this.url+(this.listUrl?this.listUrl:'/listTree') + urlPostPrefix
			}),
			root: new Ext.tree.AsyncTreeNode(this.rootConfig||{}),	
	        tbar: this.getTopButtons()
		});
		
		Ext.app.BaseFuncTree.superclass.initComponent.call(this);    	  	
		
		this.addEvents(
			'beforewinshow',
			'winshow',
			'beforedel',
			'beforesave'
        );
        
         //捕获事件		
		this.on('dblclick', function(node, e){
			if(!this.editBt.disabled &&  this.editBt.isVisible()){
				this.edit();
			}
		},this);
		
		this.on('click', function(node, e){
			this.itemSelect(node);
			this.getTopToolbar().setDisabled(false);
		},this);
		
		this.getLoader().on('beforeload',function(node){
		},this);
		
		this.getLoader().on('load',function(node){
			this.getTopToolbar().setDisabled(true);		
    		this.addBt.setDisabled(false);
    		this.refreshBt.setDisabled(false);
		},this);
		
		this.on('beforedestroy',function(cmp){
			if(this.win)
				this.win.destroy();
		},this);
				
    },
    getTopButtons : function(){
    	this.addBt = new Ext.app.Button({
			text:'新增',
            tooltip:'新增一条新记录',
            iconCls:'add',
			privilegeCode:this.funcCode?this.funcCode+'_add':'',
			scope:this,
			handler:this.prepareAdd
		});
		
		this.delBt = new Ext.app.Button({
			text:'删除',
            tooltip:'删除选中的已有记录',
            iconCls:'remove',
			privilegeCode:this.funcCode?this.funcCode+'_del':'',
			scope:this,
			handler:this.prepareDel
		});
		
		this.editBt = new Ext.app.Button({
			text:'修改',
            tooltip:'修改选中的记录',
            iconCls:'pencil',
            privilegeCode:this.funcCode?this.funcCode+'_edit':'',
			scope:this,
			handler:this.edit
		});
		
		this.refreshBt = new Ext.app.Button({
			text:'刷新',
            tooltip:'刷新数据',
            iconCls:'refresh',
			scope:this,
			handler:this.loadRoot
		});
		
		//按钮的配置
		var customBt = [];
		if(this.buttonConfig){
			for(var i=0;i < this.buttonConfig.length;i++){
				if(typeof(this.buttonConfig[i]) == 'string' && this.buttonConfig[i] == 'all'){
					customBt = [ this.addBt,this.delBt,this.editBt,'-',this.refreshBt]
				}else if(typeof(this.buttonConfig[i]) == 'string' && this.buttonConfig[i] == 'add'){
					customBt.push(this.addBt);
				}else if(typeof(this.buttonConfig[i]) == 'string' && this.buttonConfig[i] == 'del'){
					customBt.push(this.delBt);
				}else if(typeof(this.buttonConfig[i]) == 'string' && this.buttonConfig[i] == 'edit'){
					customBt.push(this.editBt);
				}else if(typeof(this.buttonConfig[i]) == 'string' && this.buttonConfig[i] == 'refresh'){
					customBt.push(this.refreshBt);
				}else if(typeof(this.buttonConfig[i]) == 'string' ){
					customBt.push(this.buttonConfig[i]);
				}else if(typeof(this.buttonConfig[i]) == 'object' && this.buttonConfig[i].xtype){
					customBt.push(this.buttonConfig[i]);
				}else if(typeof(this.buttonConfig[i]) == 'object'){
					customBt.push(new Ext.app.Button(this.buttonConfig[i]));
				}
			}			
		}else{
			customBt = [ this.addBt,this.delBt,this.editBt,'-',this.refreshBt ]
		}
		delete this.buttonConfig;
		return customBt;
    },
	itemSelect : function(selectedNode){
		this.selectedId = selectedNode.id;
	},
	createWin : function(){		
		var winConf = Ext.apply({},this.winConfig||{},{
			iconCls : this.iconCls,
			title : this.title,
			buttons : this.getWinButtons(this.winConfig.buttons),
			keys: [{
	            key: Ext.EventObject.ENTER,
	            fn: this.saveItem,
	            scope: this
	        }]
		});
		
		var formConf = Ext.apply({},this.formConfig||{},{
		});
		
		this.win = new Ext.app.FormWindow({
			winConfig :  winConf,
			formConfig : formConf
		});
	},
	getWinButtons : function(buttonConfigs){
		this.saveBt = new Ext.app.Button({
			text:'保存',
            tooltip:'保存修改的内容',
            minWidth:75, 
			scope:this,
			handler:this.saveItem
		});
		this.closeBt = new Ext.app.Button({
			text:'关闭',
            tooltip:'关闭该对话框',
            minWidth:75,
			scope:this,
			handler: function(){
				this.closeWin();
			}
		});
		
		var winButtons = [];
		if(buttonConfigs){
			for(var i=0;i < buttonConfigs.length;i++){
				if(typeof(buttonConfigs[i]) == 'string' && buttonConfigs[i] == 'all'){
					winButtons = [this.saveBt,this.closeBt];
				}else if(typeof(buttonConfigs[i]) == 'string' && buttonConfigs[i] == 'save'){
					winButtons.push(this.saveBt);
				}else if(typeof(buttonConfigs[i]) == 'string' && buttonConfigs[i] == 'close'){
					winButtons.push(this.closeBt);
				}else if(typeof(buttonConfigs[i]) == 'object'){
					winButtons.push(buttonConfigs[i]);
				}
			}
		}else{
			winButtons = [this.saveBt,this.closeBt];
		}
		return winButtons;
	},
	showWin : function(){
		if(this.fireEvent('beforewinshow', this) !== false && this.formConfig){
			this.createWin();
			this.win.show();
			this.fireEvent('winshow',this.win);	
		}	
	},
	closeWin : function(){
		if(this.win){
			this.win.close();
			delete this.win;
		}
	},
	setParentBeforeAdd : function(){		
		if(this.getSelectionModel().getSelectedNode()){
			this.ajaxParams['parentId'] = this.selectedId;
		}else{
			this.ajaxParams['parentId'] = this.getRootNode().id;
		}
	},
	setParentBeforeMod : function(){		
		this.ajaxParams['parentId'] = this.getSelectionModel().getSelectedNode().parentNode.id;
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
		if(this.saveType == 'add'){
			this.setParentBeforeAdd();
			this.win.formPanel.getForm().submit({           
	            waitMsg:'保存中...',
				url:this.url+'/create' + urlPostPrefix,
				params : this.ajaxParams ,
				scope:this,
				success:function(form, action) {
					this.closeWin();
					if(this.reloadAfterUpdate)
						this.loadRoot();
					else
						this.appendChild(action.result.entity);
				},        	
	            failure:function(form, action) {
	            	this.saveBt.enable();
	            }
	        });
		}else{
			this.setParentBeforeMod();
			this.ajaxParams['id'] = this.selectedId;
			this.win.formPanel.getForm().submit({           
	            waitMsg:'保存中...',
				url:this.url+'/update' + urlPostPrefix,
				params : this.ajaxParams ,
				scope:this,
				success:function(form, action) {
					this.closeWin();
					if(this.reloadAfterUpdate)
						this.loadRoot();
					else
						this.updateNode(action.result.entity);
	            },        	
	            failure:function(form, action) {
	            	this.saveBt.enable();
	            }
	        });
		}		
		
	},
	appendChild : function(nodeJson){
		var newNode = new Ext.tree.TreeNode(nodeJson);
		var parentNode = this.getSelectionModel().getSelectedNode()||this.root;
		if(parentNode.isLeaf()){
			this.getLoader().load(parentNode);
		}else{
			parentNode.appendChild(newNode);
			var textEl = Ext.get(newNode.getUI().getTextEl());
			if(textEl)
				textEl.highlight();
		}
		
	},
	updateNode : function(nodeJson){
		var node = this.getSelectionModel().getSelectedNode();
		node.setText(nodeJson.text);
		//Ext.get(node.getUI().getTextEl()).highlight();
	},
	prepareDel : function(){
		this.ajaxParams = {};
		if(this.fireEvent('beforedel') !== false){
			Ext.MessageBox.confirm('删除确认','您确实要删除该记录吗?该操作不能撤销!',this.delItem,this);
		}		
	},
	delItem : function(btn){
		if(btn == 'yes'){ 
			this.ajaxParams['id'] = this.selectedId;
			Ext.Ajax.request({
				url:this.url+'/del' + urlPostPrefix,
				params: this.ajaxParams,
				scope:this,
				success:function(response , options) {
					if(this.reloadAfterUpdate)
						this.loadRoot();
					else{
						var node = this.getSelectionModel().getSelectedNode();
						var parent = node.parentNode;
						node.remove();
						Ext.get(parent.getUI().getTextEl()).highlight();
					}
				}
			});
		}	
	},
	edit : function(){
		this.ajaxParams = {};
		this.saveType = 'update';
		this.showWin();
		this.ajaxParams['id'] = this.selectedId;
		this.win.formPanel.getForm().load({
			url:this.url+'/edit' + urlPostPrefix,
			params : this.ajaxParams,
            waitMsg:'加载中...',
			scope:this,
			success:function(form, action) {
			}
		});	
	},
	loadData : function(){
		//mainPanel里面会调用这个方法，但是Tree它会自动加载数据，所以这个方法不做任何操作
	}
});


/**
 * 按钮
 * 
 */
Ext.app.Button = Ext.extend(Ext.Button,{
	initComponent : function(){
        Ext.app.Button.superclass.initComponent.call(this);
		this.store = Ext.StoreMgr.lookup('loginStore');	
        this.on('beforerender', function(c){
        	if(this.privilegeCode){
				return loginUser.ownPrivilege(this.privilegeCode);
			}
        }, this);
    }
});
Ext.reg('f-button', Ext.app.Button);


/**
 * 折叠全部按钮(用于树)
 * 
 */
Ext.app.clpsAllBt = Ext.extend(Ext.Button,{
	initComponent : function(){
		Ext.apply(this,{
			text:'折叠全部',
            iconCls:'collapse-all',
			scope:this,
			handler:function(){
				this.tree.collapseAll();
			}
		});
		Ext.app.clpsAllBt.superclass.initComponent.call(this);
	}
});	
Ext.reg('f-clpsAllBt',Ext.app.clpsAllBt);

/**
 * 展开全部按钮(用于树)
 * 
 */
Ext.app.expandAllBt = Ext.extend(Ext.Button,{
	initComponent : function(){
		Ext.apply(this,{
			text:'展开全部',
            iconCls:'expand-all',
			scope:this,
			handler:function(){
				this.tree.expandAll();
			}
		});
		Ext.app.expandAllBt.superclass.initComponent.call(this);
	}
});	
Ext.reg('f-expandAllBt',Ext.app.expandAllBt);

/**
 * 下拉选择框
 * 
 */
Ext.app.SelectField = Ext.extend(Ext.form.ComboBox, { 
	autoLoad:true,
	forceSelection:true,
	editable :false,
    typeAhead: true,
    triggerAction: 'all',
    width: 230,
    hasRelative: false,
	initComponent : function(){		
		Ext.apply(this,{
			valueField:'id',
		    displayField:'text'	
		});
		if(this.data){
			this.mode = 'local';
			this.store =  new Ext.data.SimpleStore({
				
		        fields: ['id', 'text'],
		        data : this.data
		    });
		}else{
			this.store = new Ext.data.JsonStore({
			    url: ctx + (this.dataUrl || '') + urlPostPrefix,
				root:'data',
			    id: 'id',
		        fields: this.storeFields || ['id','text','code','pinyin','relative']
			});
		}
		
		//如果relative存在
		if(this.hasRelative){
			this.tpl = new Ext.XTemplate(
			    '<tpl for="."><table class="x-combo-list-item" style="width:100%;"><tr>',
			    '<td>{text}</td><td style="text-align:right;color:gray"><tpl for="relative">{text}</tpl></td>',
			    '</tr></table></tpl>'
			)
		}
		
        Ext.app.SelectField.superclass.initComponent.call(this);
        
        this.addEvents(
        	'initvalue'
        );
		
		if(this.relativeField && Ext.getCmp(this.relativeField)){
			this.onlyListRelative = this.onlyListRelative || true;
			if(this.onlyListRelative){
				this.on('beforequery',function(){
					if(Ext.getCmp(this.relativeField).getValue() == ''){
						return false;
					}
				},this);
			}
			this.on('beforequery',function(queryEvent){
	        	this.store.baseParams.relativeId  = Ext.getCmp(this.relativeField).getValue();
	        	this.relativeId = Ext.getCmp(this.relativeField).getValue();
			},this);
			
    	}
		
    },
    setReadOnly : function(readOnly){
        Ext.app.SelectField.superclass.setReadOnly.call(this, readOnly);
        if(readOnly){
        	this.el.addClass('blankReadOnly');
        }else{
        	this.el.removeClass('blankReadOnly');
        }
    },
	doQuery : function(q, forceAll){
		if(this.readOnly){
			return;
		}
        q = Ext.isEmpty(q) ? '' : q;
        var qe = {
            query: q,
            forceAll: forceAll,
            combo: this,
            cancel:false
        };
        if(this.fireEvent('beforequery', qe)===false || qe.cancel){
            return false;
        }
        q = qe.query;
        forceAll = qe.forceAll;
        if(forceAll === true || (q.length >= this.minChars)){
            if(this.lastQuery !== q || this.lastRelativeId !== this.relativeId){
                this.lastQuery = q;
                if(this.mode == 'local'){
                    this.selectedIndex = -1;
                    if(forceAll){
                        this.store.clearFilter();
                    }else{
                        this.store.filter(this.displayField, q);
                    }
                    this.onLoad();
                }else{
                    this.store.baseParams[this.queryParam] = q;
                    this.store.load({
                        params: this.getParams(q)
                    });
                    this.expand();
                }
                this.lastRelativeId = this.relativeId; 
            }else{
                this.selectedIndex = -1;
                this.onLoad();
            }
        }
    },
	onTriggerClick : function(){
        if(this.readOnly){
            return;
        }
        Ext.app.SelectField.superclass.onTriggerClick.call(this);
    },
	setValue : function(v){		
		if(typeof v == 'object'){
			var text = v[this.displayField];
			var value = v[this.valueField]||'';
			this.lastSelectionText = text;
			//设置控件DOM的值
			if (this.hiddenField) {
		    	this.hiddenField.value = value;
		    }
			//设置显示值
			Ext.form.ComboBox.superclass.setValue.call(this, text);
			//设置变量的值
			this.value = value;
			
			this.fireEvent('initvalue', this, v, this.startValue);
		}else{
	        Ext.app.SelectField.superclass.setValue.call(this, v);
		}
    }
});
Ext.reg('f-select', Ext.app.SelectField);

Ext.app.AutoSelect = Ext.extend(Ext.app.SelectField, {
	hideTrigger : true,
	minChars : 1 ,
	queryDelay : 250,
	typeAhead : false,
	lastQuery : '',
	forceSelection : true,
	listEmptyText : '没有合适的记录,请更换关键字',
	editable : true,
	//storeFields : ['id','text','code','pinyin','relative']	,
	initComponent : function(){
		Ext.app.AutoSelect.superclass.initComponent.call(this);
		this.store.on('load',function(store,records,options){
			//重置上次查询关键字，阻止读本地缓存
			this.lastQuery = '';
		},this); 
		
	}
});

Ext.reg('f-autoselect', Ext.app.AutoSelect);

/**
 * 文本框
 * 
 */
Ext.app.NumberField = Ext.extend(Ext.form.NumberField, {
	validateOnBlur:false,
	width: 230,
	minValue : 0,
	
	initComponent : function(){
        Ext.app.NumberField.superclass.initComponent.call(this);
    }
});
Ext.reg('f-number', Ext.app.NumberField);

Ext.app.TextField = Ext.extend(Ext.form.TextField, {
	validateOnBlur:false,
	width: 230,
	
	initComponent : function(){
        Ext.app.TextField.superclass.initComponent.call(this);  
    }
});
Ext.reg('f-text', Ext.app.TextField);

Ext.app.DisplayField = Ext.extend(Ext.form.DisplayField, {
	width: 230
});
Ext.reg('f-display', Ext.app.DisplayField);

/**
 * 文本域
 * 
 */
Ext.app.TextArea = Ext.extend(Ext.form.TextArea, {
	validateOnBlur:false,
	width: 230,
	
	initComponent : function(){
        Ext.app.TextArea.superclass.initComponent.call(this);
    }
});
Ext.reg('f-textarea', Ext.app.TextArea);

/**
 * 日期选择框
 * 
 */
Ext.app.DateField = Ext.extend(Ext.form.DateField, {
	value:new Date(),
	format:'Y-m-d',
	altFormats : 'Ymd|ymd',
	width: 230,
	initComponent : function(){
        Ext.app.DateField.superclass.initComponent.call(this);
    }
});
Ext.reg('f-date', Ext.app.DateField);

/**
 * FormWindow
 * 
 */
Ext.app.FormWindow = Ext.extend(Ext.Window,{
	bannerPanel : true,
	initComponent : function(){
		Ext.apply(this,this.winConfig||{},{
			layout: this.bannerPanel ? 'anchor' : 'fit',
			maximizable:false,
			buttonAlign:'center',
			resizable:true,
			desc : '',
			modal:true,
			constrainHeader : true,
			maxOnShow:false,
			height : 200,
			width : 390
		});
		
		var formConf = Ext.apply({},this.formConfig||{},{
			labelAlign: 'right',
		    labelWidth: 90,
			border : false,
			monitorValid : true,
			buttonAlign:'center',
			layout : 'form',
		   	bodyStyle: 'padding:14px 10px 0 15px',
		   	anchor : this.bannerPanel ? '0 -60' : '',
		   	defaults : { hideMode:'offsets'},
			waitMsgTarget: true
		});
		this.formPanel = new Ext.form.FormPanel(formConf);
		this.items = [
			this.formPanel
		];
		
        Ext.app.FormWindow.superclass.initComponent.call(this);
        
		if(this.bannerPanel){
			this.insert(0,new Ext.Panel({
				height : 60,
				border : false,
				baseCls : 'fjdp-win-title',
				html : '<div class="fjdp-win-title-content '+(this.bigIconClass?this.bigIconClass:'defaultBigIcon')+'"><h3>'+this.winConfig.title+'</h3><p>'+this.desc+'</p></div>'
			}));
		}
        this.on('show',function(window){
			if(this.maxOnShow){
	        	this.maximize();
	        }
	        if(this.formPanel.initialConfig.layout == 'card'){
	        	this.formPanel.getLayout().setActiveItem(0);
	        }
		},this);
   },
 	carNav : function(){
   }
});
Ext.reg('f-formwindow', Ext.app.FormWindow);

Ext.app.GroupPanel = Ext.extend(Ext.Panel, {
	collapsible:true,
	animCollapse: false,
	titleCollapse:true,
	hideCollapseTool: true,
	baseCls:'group-panel'
});
Ext.reg('f-grouppanel', Ext.app.GroupPanel);

/**
 * 下拉多项选择框
 * 
 */
Ext.app.MultiSelectField = Ext.extend(Ext.ux.LovCombo, {   
	width: 230,
	
	initComponent : function(){
		
		Ext.apply(this,{
			triggerAction: 'all',
			lazyInit:false,
			editable :false,
			valueField:'id',
    		displayField:'text'
		});
		
        Ext.app.MultiSelectField.superclass.initComponent.call(this);
        
        this.addEvents(
        	'initvalue'
        );
		
		this.on({
			 scope:this,
			 beforequery:this.onBeforeQuery
		});
		
    },
	onBeforeQuery:function(qe) {
		Ext.app.MultiSelectField.superclass.onBeforeQuery.call(this, qe);
	},
	setValue : function(v){	
		var texts = [];
		var values = [];	
		if(typeof v == 'object'){
			for(var r in v){
				if(v[r][this.displayField]){
					texts.push(v[r][this.displayField]);
					values.push(v[r][this.valueField]);
				}
			}
			
			var value = values.join(',');
			var text = texts.join(',');
			this.lastSelectionText = text;
			
			//设置控件DOM的值
			if (this.hiddenField) {
		    	this.hiddenField.value = value;
		    }
			//设置显示值 
			Ext.form.ComboBox.superclass.setValue.call(this, text);
			//设置变量的值
			this.value = value;
			
			//设置选中状态
			this.store.clearFilter();
			this.store.each(function(record) {
				var checked = !(!value.match(
					 '(^|' + this.separator + ')' + record.get(this.valueField) 
					+'(' + this.separator + '|$)'));

				record.set(this.checkField, checked);
			}, this);
			
			this.fireEvent('initvalue', this, v, this.startValue);
		}else{
	        Ext.app.MultiSelectField.superclass.setValue.call(this, v);
		}
		
    }
});
Ext.reg('f-mselect', Ext.app.MultiSelectField);

Ext.app.CompositeSelect = Ext.extend(Ext.app.AutoSelect,{
	hideTrigger: false,
	allQuery : '[allQuery]',
	triggerClass:'x-form-search-trigger',
	initComponent : function(){
		
		Ext.app.CompositeSelect.superclass.initComponent.call(this);
		
		this.addEvents(
        	'showwin'
        );
        
        this.on('showwin',function(){
    		if(this.relativeField && Ext.getCmp(this.relativeField)){
				this.onlyListRelative = this.onlyListRelative || true;
				if(this.onlyListRelative){
					if(Ext.getCmp(this.relativeField).getValue() == ''){
						return false;
					}
				}
				this.grid.store.on('beforeload',function(store,options){
		        	options.params.relativeId  = Ext.getCmp(this.relativeField).getValue();
				},this);
	    	}
        },this);
		
	},
    onDestroy : function(){
        if(this.win)
			this.win.destroy();
        Ext.app.AutoSelect.superclass.onDestroy.call(this);
    },
	onTriggerClick : function(){
	    if(this.disabled){
	        return;
	    }
	    if(this.isExpanded()){
	        this.collapse();
	        this.el.focus();
	    }else {
	        this.onFocus({});
	        this.showWindow();
	        this.el.focus();
	    }
	},
	showWindow : function(){
		this.createWindow();
		if(this.fireEvent('showwin') !== false){
			this.win.show();
			this.grid.store.load({
				callback :function(records,options){
					if(this.getValue()){
						var r = this.grid.store.getById(this.getValue());
						this.grid.getSelectionModel().selectRecords([r],false);
					}
				},
				scope : this
			});
		}
		
		
	},
	createWindow : function(){
		this.descName = this.descName || this.initialConfig.fieldLabel;
		this.form = new Ext.FormPanel({
			border : false,
			region : 'north',
			labelAlign : 'top',
			style : 'padding:10px 0px 0px 15px;background-color:white;',
			height : 65,
			keys: [{
	            key: Ext.EventObject.ENTER,
	            fn: this.remoteSearch,
	            scope: this
	        }],
        	items: [{
				xtype : 'f-text',
				border : false,
				width : 420,
				height : 24,
				id : 'search-input',
				fieldLabel : '请输入查询关键字',
				emptyText:'请输入'+this.descName+'名称',
            	name: 'company'
			}]
		});
		var keywordRenderer = function(v){
			var keyword = Ext.getCmp('search-input').getValue();
			if(keyword){
				return v.replace(keyword,'<span style="background-color:yellow;">'+keyword+'</span>');
			}else{
				return v;
			}
		}
		
		var greyRenderer = function(v){
			return '<span style="color:grey;">'+v.text+'</span>';
		}
		
		var gridTitle = this.descName+'列表';
		if(this.relativeField && Ext.getCmp(this.relativeField)){
			gridTitle = Ext.getCmp(this.relativeField).getRawValue()+" - "+gridTitle;
		}
		var columns = this.storeColumns || [
            {header: "名称",dataIndex: 'text',menuDisabled:true,renderer : keywordRenderer}
        ]
        if(this.hasRelative){
        	columns.push({header: this.relativeHeader||"",dataIndex: 'relative',align:'left',menuDisabled:true,renderer : greyRenderer});
        }
		this.grid = new Ext.grid.GridPanel({
			region : 'center',
			border : false,
			sm: new Ext.grid.RowSelectionModel({singleSelect:true}),
			store: new Ext.data.JsonStore({
			    url: ctx + this.dataUrl,
			    baseParams : {matchPinyin:false},
				root:'data',
			    id: 'id',
		        fields: this.storeFields || ['id','text','code','pinyin','relative']	
			}),
			viewConfig: { forceFit : true ,emptyText : '没有可用的数据'},
	        columns: columns,
	        title: gridTitle,
	        stripeRows: true
		});
		
		this.grid.on('rowdblclick',function(grid,rowIndex,e){
			this.selectGridItem();
		},this);
		
		this.win = new Ext.Window({
			title : this.descName+'选择框',
			height : 400,
			width : 465,
			minWidth : 465,
			modal : true,
			iconCls : 'search',
			layout : 'border',
			items : [this.form,this.grid],
			buttonAlign : 'center',
			buttons :[{
				text : '确认',
				handler :　this.selectGridItem,
				scope : this
			},{
				text : '清除',
				handler :　this.clearGridItem,
				scope : this
			}]
		});
		
	},
	remoteSearch : function(){
		var keyword = Ext.getCmp('search-input').getValue();
		if(keyword){
			this.grid.store.load({params:{query : keyword}});
		}
	},
	selectGridItem : function(){
		var record = this.grid.getSelectionModel().getSelected();
		if(record){
			this.setValue({
				id : record.id,
				text : record.data.text
			});
			
			this.win.close();
		}
		
		if(String(this.getValue()) !== String(this.startValue)) {
			this.fireEvent('change', this, this.getValue(), this.startValue); 
		}
	},
	clearGridItem : function(){
		this.clearValue();
		this.win.close();
	}
});
Ext.reg('f-compositeSelect', Ext.app.CompositeSelect);


Ext.app.SearchField = Ext.extend(Ext.form.TwinTriggerField, {
    initComponent : function(){
        Ext.app.SearchField.superclass.initComponent.call(this);
		
        this.on('specialkey', function(f, e){
            if(e.getKey() == e.ENTER){
                this.onTrigger2Click();
            }
        }, this);
    },

    validationEvent:false,
    validateOnBlur:false,
    trigger1Class:'x-form-clear-trigger',
    trigger2Class:'x-form-search-trigger',
    hideTrigger1:true,
    width:280,
    hasSearch : false,
    paramName : 'query',

     onTrigger1Click : function(){
        if(this.hasSearch){
            this.el.dom.value = '';
            this.pageParams = {};
            if(this.store.lastOptions && this.store.lastOptions.params){
				Ext.applyIf(this.pageParams,this.store.lastOptions.params);
			}
            this.pageParams[this.paramName] = '';
            this.store.reload({params:this.pageParams});
            this.triggers[0].hide();
            this.hasSearch = false;
        }
    },

    onTrigger2Click : function(){
        var v = this.getRawValue();
        if(v.length < 1){
            this.onTrigger1Click();
            return;
        }
		this.pageParams = this.pageParams||{};
		if(this.store.lastOptions && this.store.lastOptions.params){
			Ext.applyIf(this.pageParams,this.store.lastOptions.params);
		}
        this.pageParams[this.paramName] = v;
        this.store.reload({params:this.pageParams});
        this.pageParams = {};
        this.hasSearch = true;
        this.triggers[0].show();
    }
});
Ext.reg('f-search', Ext.app.SearchField);

Ext.app.noPagingToolbar = Ext.extend(Ext.Toolbar, {
	height:25,
	displayMsg : '共 {0} 条数据',
	emptyMsg : '没有可以显示的数据',
	refreshText:'刷新',
	initComponent : function(){
        Ext.app.noPagingToolbar.superclass.initComponent.call(this);
        this.bind(this.store);
    },
	 bind : function(store){
        store = Ext.StoreMgr.lookup(store);
        store.on("load", this.onLoad, this);
        this.store = store;
    },
	// private
    onRender : function(ct, position){
        Ext.app.noPagingToolbar.superclass.onRender.call(this, ct, position);
		this.loading = this.addButton({
            tooltip: this.refreshText,
            iconCls: "x-tbar-loading",
			scope:this,
            handler: function(){
				this.store.reload();
			}
        });
		
        this.displayEl = Ext.fly(this.el.dom).createChild({cls:'x-paging-info'});
        if(this.dsLoaded){
            this.onLoad.apply(this, this.dsLoaded);
        }
    },

    // private
    updateInfo : function(){
        if(this.displayEl){
            var count = this.store.getCount();
            var msg = count == 0 ?
                this.emptyMsg :
                String.format(this.displayMsg,count);
            this.displayEl.update(msg);
        }
    },

    // private
    onLoad : function(store, r, o){
        if(!this.rendered){
            this.dsLoaded = [store, r, o];
            return;
        }
       this.updateInfo();
    }
});

Ext.app.RadioGroup = Ext.extend(Ext.form.RadioGroup, {
	initComponent : function(){
        Ext.app.RadioGroup.superclass.initComponent.call(this);                                            
    },
    getName: function() {
		return this.items.first().getName();
	}
});
Ext.reg('f-radiogroup', Ext.app.RadioGroup);

Ext.app.uploadField = Ext.extend(Ext.form.FileUploadField,{
	buttonText: '浏览...',
	emptyText: '请选择一个文件',
	width: 230,
	initComponent: function(){
	    Ext.app.uploadField.superclass.initComponent.call(this);
	}
});
Ext.reg('f-upload', Ext.app.uploadField);

Ext.app.DateTime = Ext.extend(Ext.ux.DateTime,{
	width: 230,
	allowBlank : true,
	initComponent: function(){
		Ext.apply(this,{
			otherToNow : false,
			timeFormat:'H:i:s',
			timeConfig: {
                altFormats:'H:i:s',
                allowBlank : this.allowBlank
            },
            dateFormat:'Y-m-d',
            dateConfig: {
                altFormats:'Y-m-d|Y-n-d',
                allowBlank : this.allowBlank
            }
		});
	    Ext.app.DateTime.superclass.initComponent.call(this);
	}
});
Ext.reg('f-dateTime', Ext.app.DateTime);

/**
 * 字典选择框
 * 
 */
Ext.app.DictSelect = Ext.extend(Ext.app.SelectField, {
	initComponent : function(){
        Ext.app.DictSelect.superclass.initComponent.call(this);
		this.store = new Ext.data.JsonStore({
		    url: ctx+'/dict/getDictsByType' + urlPostPrefix,
		    baseParams : { type : this.kind},
			root:'data',
		    fields: ['id', 'text']			
		});
    }
});
Ext.reg('f-dict', Ext.app.DictSelect);


Ext.app.YearSelect = Ext.extend(Ext.form.ComboBox,{
	width: 230,
	forceSelection : false,
	triggerAction : 'all',
	editable : true,
    typeAhead: false,
    increment: 3,
    mode : 'local',
    emptyText :'请输入或者选择年份',
    initComponent : function(){
    	this.value = new Date().getFullYear();
    	var years = [];
    	var year = new Date().getFullYear()-this.increment;
    	for(var i = 0; i<this.increment*2; i++){
    		year = year+1;
    		years.push(year);
    	}
    	this.store = years;
        Ext.app.YearSelect.superclass.initComponent.call(this);
    }
});
Ext.reg('f-year', Ext.app.YearSelect);

Ext.app.SeasonSelect = Ext.extend(Ext.form.ComboBox,{
	width: 230,
	forceSelection : false,
	triggerAction : 'all',
	editable : true,
    typeAhead: false,
    mode : 'local',
    emptyText :'请输入或者选择季度',
    initComponent : function(){
    	this.value = Math.floor(new Date().getMonth()/3)+1;
    	var quarters = [];
    	for(var i = 1; i < 5; i++){
    		quarters.push(i);
    	}
    	this.store = quarters;
        Ext.app.SeasonSelect.superclass.initComponent.call(this);
    }
});
Ext.reg('f-season', Ext.app.SeasonSelect);

Ext.app.MonthSelect = Ext.extend(Ext.form.ComboBox,{
	width: 230,
	forceSelection : false,
	triggerAction : 'all',
	editable : true,
    typeAhead: false,
    mode : 'local',
    emptyText :'请输入或者选择月份',
    initComponent : function(){
    	this.value = new Date().getMonth()+1;
    	var months = [];
    	for(var i = 1; i < 13; i++){
    		months.push(i);
    	}
    	this.store = months;
        Ext.app.MonthSelect.superclass.initComponent.call(this);
    }
});
Ext.reg('f-month', Ext.app.MonthSelect);

Ext.app.YearSeasonSelect = Ext.extend(Ext.form.CompositeField,{
	initComponent : function(){
    	Ext.apply(this,{
    		defaults:{
    			width:112
    		},
    		items:[
    			{xtype: 'f-year',name: 'year'},
			    {xtype: 'f-season',name: 'season'}
			]
    	})
        Ext.app.YearSeasonSelect.superclass.initComponent.call(this);
    }
});
Ext.reg('f-yearseason', Ext.app.YearSeasonSelect);

Ext.app.YearMonthSelect = Ext.extend(Ext.form.CompositeField,{
	initComponent : function(){
    	Ext.apply(this,{
    		defaults:{
    			width:112
    		},
    		items:[
    			{xtype: 'f-year',name: 'year'},
			    {xtype: 'f-month',name: 'month'}
			]
    	})
        Ext.app.YearMonthSelect.superclass.initComponent.call(this);
    }
});
Ext.reg('f-yearmonth', Ext.app.YearMonthSelect);

Ext.app.DatePeriod = Ext.extend(Ext.form.CompositeField,{
	initComponent : function(){
    	Ext.apply(this,{
    		defaults:{
    			width:112
    		},
    		items:[
    			{xtype: 'f-date',name: this.name+'StartDate'},
			    {xtype: 'f-date',name: this.name+'EndDate'}
			]
    	})
        Ext.app.DatePeriod.superclass.initComponent.call(this);
    }
});
Ext.reg('f-dateperiod', Ext.app.DatePeriod);



Ext.app.Portal = Ext.extend(Ext.app.GroupPanel,{
	autoScroll:true,
	autoHeight:true,
    bodyStyle  : 'padding: 20px ',
    initComponent : function(){
    	
    	Ext.app.Portal.superclass.initComponent.call(this);
    	
    	this.on('beforerender',function(){
    		if(this.privilegeCode){
    			this.anchor = null;
				return loginUser.ownPrivilege(this.privilegeCode);
			}
    	},this);
    },
    refresh: function(){
    	this.getUpdater().refresh();
    }
});
Ext.reg('f-protal', Ext.app.Portal);


Ext.app.GridSelect = Ext.extend(Ext.grid.GridPanel, {
	saveText : '确定',
	buttonAlign : 'center',
	autoScroll:true,
	loadMask: true,
	showSaveButton : true,
	initComponent : function(){
		this.store = new Ext.data.JsonStore({
		    url: this.url,
			root: 'data',
	        totalProperty: 'totalCount',
	        id: 'id',
	        fields: Ext.data.Record.create(this.storeMapping)
		});
		
		if(this.showSaveButton){
			this.saveBt = new Ext.app.Button({
				text : this.saveText,
				minWidth:75,
				disabled : true,
				scope : this,
				handler : this.saveData
			});
			this.buttons = [this.saveBt];
		}
		
		Ext.applyIf(this, {    
	        sm: new Ext.grid.CheckboxSelectionModel(),        
	        viewConfig: { forceFit : true ,emptyText : '没有可用的数据'},
	        store : this.store
		});
		
		Ext.app.GridSelect.superclass.initComponent.call(this);
		
		//添加事件
		this.addEvents(
			'savedata'
        );
        
		this.store.on('load',function(store,records,options){
			var records = [];
			this.store.each(function(record){
				if(record.data.checked){
					records.push(record);
				}
			});
			this.getSelectionModel().selectRecords(records,false);
			this.getSelectionModel().on('selectionchange',function(){
				if(this.saveBt)
					this.saveBt.enable();
				var checkedIds = [];
				var checkedRecords = this.getSelectionModel().getSelections();
				for(var i = 0;i < checkedRecords.length; i++){
					checkedIds.push(checkedRecords[i].id);
				}
				this.checkedData = checkedIds;
			},this);
		},this); 
		
    },
	loadData : function(o){
		if(o)
			Ext.apply(this.store.baseParams,o);
		this.store.load();
	},
	saveData : function(){
		this.fireEvent('savedata', this);
	}
});


