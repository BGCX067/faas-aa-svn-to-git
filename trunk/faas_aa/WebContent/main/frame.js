
// Handle this change event in order to restore the UI to the appropriate history state
Ext.History.init();

Ext.History.on('change', function(token){
    if(token){
        Ext.getCmp('mainPanel').setActiveTab(token);
    }else{
        Ext.getCmp('mainPanel').setActiveTab(0);
    }
});

App = function(){
    var msgCt;

    function createBox(s,msgType){
       /* return [
                '<table><tr><td><div class="round-box" >',
                '<div class="content ',msgType,'"><p align="center">',s,'</p></div>',
                '<div class="bottom">',
                '<div class="r4 ',msgType,'"/></div>',
                '<div class="r3 ',msgType,'"/></div>',
                '<div class="r2 ',msgType,'"/></div>',
                '<div class="r1 ',msgType,'"/></div>',
                '</div></div></td></tr></table>'].join('');*/
    	return [
                '<div class="content ',msgType,'"><p align="center">',s,'</p></div>'
				].join('');
    }
    return {
        msg : function(s,error){
            if(!msgCt){
                msgCt = Ext.DomHelper.insertFirst(document.body, {id:'msg-div'}, true);
            }
            if(msgCt.first()){
            	msgCt.first().remove();
            }
            var m = Ext.DomHelper.append(msgCt, {html:createBox(s,error?'errorMsg':'infoMsg')}, true);
            msgCt.alignTo(document,'t-t',[0,-2]);
            m.slideIn('t').pause(2).slideOut("t", {remove:true});

        }
    };
}();

ConfigWin = Ext.extend(Ext.app.FormWindow,{
	id : 'header-config-win',
	title : '修改密码',
	iconCls: 'conf',
	winConfig : {
		title : '修改密码',
		height : 300, width : 390,
		desc : '设置用户密码等个人信息',
		bigIconClass : 'personalConfIcon'
	},
	formConfig :{
		items : [
			{xtype:'fieldset',title: '重设密码',autoHeight:true,
				items :[
					{xtype: 'f-text',fieldLabel: '密码',id:'pswd',name: 'password',inputType:'password',allowBlank: false},
					{xtype:'f-text',fieldLabel:'确认密码',id:'pswdComfirm',name:'password2',inputType:'password',vtype: 'password',initialPassField: 'pswd',allowBlank: false}
				]
			}
		]
	},
	buttons : [{
		text : '确认',
		scope : this,
		handler : function(){
			Ext.getCmp('header-config-win').formPanel.getForm().submit({
				url : ctx + '/system/personalConfig',
				success : function(form,action){
					App.msg(action.result.msg);
					Ext.getCmp('header-config-win').close();
				}
			});
		}
	}]
});

App.Header = Ext.extend(Ext.Panel,{
	initComponent : function(){
		Ext.apply(this,{
			region:'north',
			layout:'anchor',
			border:false,
			height:65,
			items: [{
	            xtype:'box',
	            el:'logo-banner',
	            border:false,
	            anchor: 'none -25'
	        },{
				xtype:'statusbar',
				id:'header-bar',
				busyIconCls:'busyIcon',
				busyText:'数据加载中...',
				items:['当前登陆：'+loginUser.userName,'-',{
					xtype : 'splitbutton',
					id:'userManual',
					text:'用户手册',
					tooltip:'浏览或者下载用户操作手册',  
					iconCls: 'help',
					fileType : 'htm',
					scope : this,
					handler: this.downloadManual,
					menu: new Ext.menu.Menu({
				        items: [
//				        	{text: '打开为网页', iconCls: 'html',fileType : 'htm',handler: this.downloadManual}
//					        {text: '下载为Word文档', iconCls: 'word',fileType : 'doc',handler: this.downloadManual},
					        {text: '下载为pdf文档', iconCls: 'pdf',fileType : 'pdf',handler: this.downloadManual}
				        ]
				   	})
				},'-',{
					id:'settingButton',	
					text:'个人设置',
					tooltip:'设置个人信息',  
					iconCls: 'conf',
					handler: function(){
						var tempWin = new ConfigWin();
						tempWin.show();
					}
				},'-',{
					text:'注销用户',
					tooltip:'注销该用户,并返回到登陆界面',
					iconCls: 'logout',
					handler: function(){
					    Ext.MessageBox.confirm('注销确认', '你确定要注销此用户吗?',function(btn){
							if(btn == 'yes'){
								window.location = ctx+'/system/logout';
								return;
							}
						});
					}
				}]
			}]
		});		
    	App.Header.superclass.initComponent.call(this);					
    },
    downloadManual : function(item){
    	if(item.fileType == "htm")
    		window.open(ctx+'/doc/manual.htm');
    	else
    		window.location = ctx+'/system/downloadManual?fileType='+ item.fileType;
    
    }
});

App.MainPanel = Ext.extend( Ext.TabPanel, {
	plugins: new Ext.ux.TabCloseMenu(),
	initComponent : function(){
    	App.MainPanel.superclass.initComponent.call(this);
		this.on('tabchange', function(tp, tab){
	        Ext.getCmp('navMenu').selectMenu(tab.id);
	        Ext.History.add(tab.id);
	    });		
    },
	
    openTab: function(node){
        var tab = this.getComponent(node.id);
        if(tab){
            this.setActiveTab(tab);
        }else{
    		var newTab = this.createTab(node);
			newTab.loadData();
        }
    },
    createTab : function(node){
    	var funcName = node.id;
 		eval('var newTab = new '+funcName+'({id:"'+funcName+'",funcCode:"'+funcName+'"})');
		newTab.setTitle(node.text);
		newTab.setIconClass(node.attributes.menuIcon);
        this.add(newTab);
        this.setActiveTab(newTab);
        return newTab;
    }
	
});


App.NavMenu = Ext.extend(Ext.tree.TreePanel, {	
	
    initComponent : function(){
    	App.NavMenu.superclass.initComponent.call(this);
		this.getSelectionModel().on('beforeselect', function(sm, node){
        	return node.isLeaf();
		});
		this.on('click', function(node, e){
		     if(node.isLeaf()){
		        e.stopEvent();
		        Ext.getCmp('mainPanel').openTab(node);
		     }
		});
    },
	selectMenu : function(tabid){
		var node = this.getNodeById(tabid);
		if(node){
			node.select();
		}else{
			this.getSelectionModel().clearSelections();
		}
			
	}
});
