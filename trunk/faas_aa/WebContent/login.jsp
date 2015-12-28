<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>

<html>
<head>
	<title>用户登陆</title>
	<s:include value="/common/header.jsp" />
<script>
Ext.onReady(function(){
	var loginform = new Ext.FormPanel({
		//id:'loginform',
		renderTo:'login-form',
	    baseCls: 'x-plain',
	    labelWidth: 60, 
	    defaults: {width: 230},
	    
	    defaultType: 'textfield',
	    items: [{
	            fieldLabel: '用户名',
	            name: 'userName',
	            id:'loginUser',
	            validateOnBlur:false,
	            blankText:'用户名不能为空,请输入您的用户名',
	            allowBlank:false
	        },{
	            fieldLabel: '密码',
	            id:'loginPswd',
	            name: 'password',
	            validateOnBlur:false,
	            blankText:'密码不能为空',
	            allowBlank:false,
	            inputType:'password'		
	        }
	    ]
	});   

	Ext.getCmp('loginUser').focus(true, true);;
	
	function submit(){
		loginform.getForm().submit({
             url: ctx+'/system/login',
             waitTitle: '请稍候...',   
             waitMsg: '正在登陆',
             success: function (form, action) {
                 document.location = ctx+'/system/viewport';
             },
             failure:function(form, action) {
                 if(action.failureType == Ext.form.Action.SERVER_INVALID)
             		Ext.MessageBox.alert('信息', action.result.msg);
             }
         });
	}
	

	var map = new Ext.KeyMap(document, {
	    key: Ext.EventObject.ENTER,
	    fn: submit,
	    scope: this
	});

	Ext.get('login-button').on('click',submit);

});
</script>
</head>
<body class="login-body">
<div class="logo-flag"></div>
<table width= "100%"  height="100%" > 
<tr><td style="text-align:center;">
<div id="login-div">
	<div id="login-form"></div>
	<button id="login-button" class="button blue">登陆</button> 
</div>
</td></tr> 
</table>
<div id="login-copyright" class="login-copyright" >
	<p>深圳方迪计算机系统有限公司</p>
	<p>&copy;1997-2011</p>
</div>
</body>
</html>

