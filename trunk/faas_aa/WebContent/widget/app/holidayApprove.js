var employeeApproveList = [
		{xtype: 'treeField',width:150,fieldLabel: '部门',id:'organizationOne',listHeight:240,hiddenName: 'organization'
	    ,name:'organizationOne',dataUrl : ctx+'/organization/getOrganizationTree',readOnly:false},
	    {xtype: 'f-select',width:150,fieldLabel:'第一审批人',hiddenName:'leaveApprove1',allowBlank: false,relativeField:'organizationOne',dataUrl:'/employee/getHolidayApplyByOrganization'},
	    {xtype: 'treeField',width:150,fieldLabel: '部门',id:'organizationTwo',listHeight:240,hiddenName: 'organization2'
	    ,name:'organizationTwo',dataUrl : ctx+'/organization/getOrganizationTree',readOnly:false,
	    listeners:{
	    	select:function(combo, record, index){
	    		if(record.id=="H-0"&&record.text=="取消选择"){
	    			Ext.getCmp('leaveApproveTwo').setValue({id:'',text:''});
	    			Ext.getCmp('leaveApproveThree').setValue({id:'',text:''});
	    			Ext.getCmp('leaveApproveFour').setValue({id:'',text:''});
	    		}
	    	}
	    }},
	    {xtype: 'f-select',width:150,id:'leaveApproveTwo',fieldLabel:'第二审批人',hiddenName:'leaveApprove2',relativeField:'organizationTwo',dataUrl:'/employee/getHolidayApplyByOrganization'},
	    {xtype: 'treeField',width:150,fieldLabel: '部门',id:'organizationThree',listHeight:240,hiddenName: 'organization3'
	    ,name:'organizationThree',dataUrl : ctx+'/organization/getOrganizationTree',readOnly:false,
	    listeners:{
	    	select:function(combo, record, index){
	    		if(record.id=="H-0"&&record.text=="取消选择"){
	    			Ext.getCmp('leaveApproveFour').setValue({id:'',text:''});
	    			Ext.getCmp('leaveApproveThree').setValue({id:'',text:''});
	    		}
	    	}
	    }},
	    {xtype: 'f-select',width:150,id:'leaveApproveThree',fieldLabel:'第三审批人',hiddenName:'leaveApprove3',relativeField:'organizationThree',dataUrl:'/employee/getHolidayApplyByOrganization'},
	    {xtype: 'treeField',width:150,fieldLabel: '部门',id:'organizationFour',listHeight:240,hiddenName: 'organization4'
	    ,name:'organizationFour',dataUrl : ctx+'/organization/getOrganizationTree',readOnly:false,
	    listeners:{
	    	select:function(combo, record, index){
	    		if(record.id=="H-0"&&record.text=="取消选择"){
	    			Ext.getCmp('leaveApproveFour').setValue({id:'',text:''});
	    		}
	    	}
	    }},
	    {xtype: 'f-select',width:150,id:'leaveApproveFour',fieldLabel:'第四审批人',hiddenName:'leaveApprove4',relativeField:'organizationFour',dataUrl:'/employee/getHolidayApplyByOrganization'},
	    {xtype: 'f-text',width:150,name:'leaveApplyType',value:'employee',hidden:true}
];

var brancaApproveList = [
    {xtype: 'treeField',width:150,fieldLabel: '部门',id:'organizationOne',listHeight:240,hiddenName: 'organization'
    ,name:'organizationOne',dataUrl : ctx+'/organization/getOrganizationTree',readOnly:false},
    {xtype: 'f-select',width:150,fieldLabel:'授权人确认',hiddenName:'leaveApprove1',allowBlank: false,relativeField:'organizationOne',dataUrl:'/employee/getHolidayApplyByOrganization'},
    {xtype: 'treeField',width:150,fieldLabel: '部门',id:'organizationTwo',listHeight:240,hiddenName: 'organization2'
    ,name:'organizationTwo',dataUrl : ctx+'/organization/getOrganizationTree',readOnly:false,
    listeners:{
    	select:function(combo, record, index){
    		if(record.id=="H-0"&&record.text=="取消选择"){
    			Ext.getCmp('leaveApproveTwo').setValue({id:'',text:''});
    			Ext.getCmp('leaveApproveThree').setValue({id:'',text:''});
    		}
    	}
    }},
    {xtype: 'f-select',width:150,id:'leaveApproveTwo',fieldLabel:'支行意见',hiddenName:'leaveApprove2',relativeField:'organizationTwo',dataUrl:'/employee/getHolidayApplyByOrganization'},
    {xtype: 'treeField',width:150,fieldLabel: '部门',id:'organizationThree',listHeight:240,hiddenName: 'organization3'
    ,name:'organizationThree',dataUrl : ctx+'/organization/getOrganizationTree',readOnly:false,
    listeners:{
    	select:function(combo, record, index){
    		if(record.id=="H-0"&&record.text=="取消选择"){
    			Ext.getCmp('leaveApproveThree').setValue({id:'',text:''});
    		}
    	}
    }},
    {xtype: 'f-select',width:150,id:'leaveApproveThree',fieldLabel:'人力资源部意见',hiddenName:'leaveApprove3',relativeField:'organizationThree',dataUrl:'/employee/getHolidayApplyByOrganization'},
    {xtype: 'f-text',width:150,name:'leaveApplyType',value:'branch',hidden:true}
];

var leaderApproveList = [
    {xtype: 'treeField',width:150,fieldLabel: '部门',id:'organizationOne',listHeight:240,hiddenName: 'organization'
    ,name:'organizationOne',dataUrl : ctx+'/organization/getOrganizationTree',readOnly:false},
    {xtype: 'f-select',width:150,fieldLabel:'授权人确认',hiddenName:'leaveApprove1',allowBlank: false,relativeField:'organizationOne',dataUrl:'/employee/getHolidayApplyByOrganization'},
    {xtype: 'f-select',width:150,fieldLabel:'办公室审核',hiddenName:'bossAffirm',dataUrl:'/employee/getBoss'},
//    ,
//    {xtype: 'treeField',width:150,fieldLabel: '部门',id:'organizationTwo',listHeight:240,hiddenName: 'organization2'
//    ,name:'organizationTwo',dataUrl : ctx+'/organization/getOrganizationTree',readOnly:false,
//    listeners:{
//    	select:function(combo, record, index){
//    		if(record.id=="H-0"&&record.text=="取消选择"){
//    			Ext.getCmp('leaveApproveTwo').setValue({id:'',text:''});
//    			Ext.getCmp('leaveApproveThree').setValue({id:'',text:''});
//    			Ext.getCmp('leaveApproveFour').setValue({id:'',text:''});
//    		}
//    	}
//    }},
//    {xtype: 'f-select',width:150,id:'leaveApproveTwo',fieldLabel:'第一审批人',hiddenName:'leaveApprove2',relativeField:'organizationTwo',dataUrl:'/employee/getHolidayApplyByOrganization'},
//    {xtype: 'treeField',width:150,fieldLabel: '部门',id:'organizationThree',listHeight:240,hiddenName: 'organization3'
//    ,name:'organizationThree',dataUrl : ctx+'/organization/getOrganizationTree',readOnly:false,
//    listeners:{
//    	select:function(combo, record, index){
//    		if(record.id=="H-0"&&record.text=="取消选择"){
//    			Ext.getCmp('leaveApproveFour').setValue({id:'',text:''});
//    			Ext.getCmp('leaveApproveThree').setValue({id:'',text:''});
//    		}
//    	}
//    }},
//    {xtype: 'f-select',width:150,id:'leaveApproveThree',fieldLabel:'第二审批人',hiddenName:'leaveApprove3',relativeField:'organizationThree',dataUrl:'/employee/getHolidayApplyByOrganization'},
//    {xtype: 'treeField',width:150,fieldLabel: '部门',id:'organizationFour',listHeight:240,hiddenName: 'organization4'
//    ,name:'organizationFour',dataUrl : ctx+'/organization/getOrganizationTree',readOnly:false,
//    listeners:{
//    	select:function(combo, record, index){
//    		if(record.id=="H-0"&&record.text=="取消选择"){
//    			Ext.getCmp('leaveApproveFour').setValue({id:'',text:''});
//    		}
//    	}
//    }},
//    {xtype: 'f-select',width:150,id:'leaveApproveFour',fieldLabel:'第三审批人',hiddenName:'leaveApprove4',relativeField:'organizationFour',dataUrl:'/employee/getHolidayApplyByOrganization'},
    {xtype: 'f-text',width:150,name:'leaveApplyType',value:'leader',hidden:true}
];