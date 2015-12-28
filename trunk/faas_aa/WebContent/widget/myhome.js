Ext.ns('Myhome');

Myhome.Welcome = Ext.extend(Ext.Panel,{
	border : false,
	bodyStyle  : 'padding: 5px 10px',
	html : '<div class="msg-body"><p>欢迎您，<span class="userName">'+loginUser.userName+'</span>，来自<span class="organizationName">'+loginUser.organizationName+'</span></p></div>'
	//<div>您还有<span class="annualLeave">'+loginUser.annualLeave+'</span>天年休假</div>
});

Myhome.LoginStat = Ext.extend(Ext.app.Portal,{
	id : 'loginStat-portal',
	title: '登陆信息统计',
	privilegeCode : 'LoginStat_view',
	autoLoad: {
    	url : ctx+'/myhome/loginStat',
    	scripts : true
    }
});

Myhome.Notice = Ext.extend(Ext.app.Portal,{
	id : 'notice-portal',
	title: '公告信息',
	privilegeCode : 'NoticePortal_view',
	autoLoad: {
    	url : ctx+'/myhome/noticeList',
    	scripts : true
    },
    tag: function(bt,messageId){
    	Ext.Ajax.request({
			url: ctx+'/myhome/tagAsReaded' ,
			params: {id : messageId},
			scope:this,
			success:function(response, options) {
				Ext.get(bt).hide(true);
			},        	
			failure:function(response, options) {
				Ext.get(bt).show(true);
			}
		});
    }
});

MyHome = Ext.extend(Ext.Panel,{
	id:'myHomePanel',
	title: '我的主页',
	iconCls:'userman',
	autoScroll:true,
	border:true,
	bodyStyle: 'padding: 10px ',
	layout: 'anchor',
	initComponent : function(){
		
		this.items = [
			new Myhome.Welcome({anchor: '-10'}),
			new Myhome.LoginStat({anchor: '-10'}),
			new Myhome.Notice({anchor: '-10'})
		];
    	MyHome.superclass.initComponent.call(this);
    }

});

