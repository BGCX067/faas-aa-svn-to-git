var condition1 = '';
var day = new Date();
var firstDay = day.getYear()+"-01-01";
var v_area = {
	xtype: 'f-area',fieldLabel: '镇(街)', autoHeight: true, id: 'areaId',hiddenName: 'area'
};

var v_fromDateField = {
	xtype: 'f-date',fieldLabel: '起始日期', autoHeight: true, id: 'fromDate',name: 'fromDate',value: firstDay
};
var v_toDateField = {
	xtype: 'f-date',fieldLabel: '截止日期', autoHeight: true, id: 'toDate',name: 'toDate'
};

var v_searchCont1 = {
	xtype: 'fieldset',title : '查询条件', autoHeight: true,id: 'searchId',
		items: [v_area,v_fromDateField,v_toDateField]
}

var v_searchCont3 = {
	xtype: 'fieldset',title : '查询条件', autoHeight: true,id: 'searchId',
		items: [v_fromDateField,v_toDateField]
}

function f_addSearchCont(){
	var condition = '';
	var fromDate = Ext.getCmp('fromDate');
	var toDate = Ext.getCmp('toDate');
	if ((toDate && toDate.getValue() != '') && (fromDate && fromDate.getValue() != '')){
		condition += ' AND PD.TRAN_DATE >= \'' + fromDate.getValue().format('Ymd') + '\'';
		condition += ' AND PD.TRAN_DATE <= \'' + toDate.getValue().format('Ymd') + '\'';
		//condition += ' AND pd.tran_date <= \'' + fromDate.getValue().format('Ymd');
		//condition += '\' AND pd.tran_date <= ' + toDate.getValue()payment_detail.format('Ymd');
	}
	condition1 = ' AND PD.TRAN_DATE <= \'' + toDate.getValue().format('Ymd') + '\'';
	return condition;
}
function f_addSearchCont1(){
	var condition = '';
	var toDate = Ext.getCmp('toDate');
	if ((toDate && toDate.getValue() != '')){
		condition += ' AND PD.TRAN_DATE <= \'' + toDate.getValue().format('Ymd') + '\'';
		//condition += ' AND pd.tran_date <= \'' + fromDate.getValue().format('Ymd');
		//condition += '\' AND pd.tran_date <= ' + toDate.getValue()payment_detail.format('Ymd');
	}
	return condition;
}

function f_addSearchCont2(){
	var condition = '';
	var area = Ext.getCmp('areaId');
	if (area && area.getValue() != ''){
		condition += ' AND AI.AREA_ID = '+area.getValue();
	}
	return condition;
}

function getAreaName(){
	var condition = '';
	var area = Ext.getCmp('areaId');
	if (area && area.getValue() != ''){
		condition += area.getRawValue();
	}
	return condition;
}

function getFormatDate(){
	var t_date = '';
	var fromDate = Ext.getCmp('fromDate');
	var toDate = Ext.getCmp('toDate');
	//alert(fromDate+"   "toDate);
	t_date += fromDate.getValue().format('Y')+'年' + fromDate.getValue().format('m')+'月'+fromDate.getValue().format('d')+'日';
	t_date += ' 至  '+toDate.getValue().format('Y')+'年' + toDate.getValue().format('m')+'月'+toDate.getValue().format('d')+'日';
	return t_date;
}

var r_reports = [{
	text: '各镇（街）住宅专项维修资金缴存情况表',
	func: function(){
    	var houseTotalWin = new Ext.app.FormWindow({
    		bannerPanel: false,
			winConfig: {
				title: '各镇（街）住宅专项维修资金缴存情况查询',
				height: 400,
				width: 500,
				model: true
			},
			formConfig: {
				items: [v_searchCont3]
				//items: [ area,fromDateField,toDateField ]		
			},
			buttons: [{
				id: 'Report-info',
				text: '查询',
				scope: this,
				handler: function(){
					var condition = f_addSearchCont();
					var condition1 = f_addSearchCont1();
					//var userName = loginUser.userName;
					var date = getFormatDate();
					var defineParam = '__report=report/fund_pay.rptdesign&condition=' + condition + '&condition1=' + condition1  + '&date=' + date;
					reportViewer(defineParam);
				}
			}]
		});
		houseTotalWin.show();
	},
    /*func: function(){
		var defineParam = '__report=report/fund_pay.rptdesign';
		reportViewer(defineParam);
	},*/
    icon: 'report2.jpg',
    desc: '正式户+临时户'
},{
    text: '住宅专项维修资金管理专户汇总表',
    func: function(){
    	var houseTotalWin = new Ext.app.FormWindow({
    		bannerPanel: false,
			winConfig: {
				title: '住宅专项维修资金管理专户汇总查询',
				height: 400,
				width: 500,
				model: true
			},
			formConfig: {
				items: [v_searchCont1]
				//items: [ area,fromDateField,toDateField ]		
			},
			buttons: [{
				id: 'Report-info',
				text: '查询',
				scope: this,
				handler: function(){
					var date = getFormatDate();
					var areaId = f_addSearchCont2();//Ext.getCmp('areaId');
					//alert(areaId);
					var area = getAreaName();
					//alert(area);
					var condition = f_addSearchCont();
					var defineParam = '__report=report/fund_total.rptdesign&condition=' + condition + '&areaId=' + areaId+ '&date=' + date + '&area=' + area + '&condition1=' + condition1 ;
					reportViewer(defineParam);
				}
			}]
		});
		houseTotalWin.show();
	},
    icon: 'report2.jpg',
    desc: '正式户+临时户'
},{
    text: '住宅专项维修资金管理专户日报表',
    func: function(){
    	var houseTotalWin = new Ext.app.FormWindow({
    		bannerPanel: false,
			winConfig: {
				title: '住宅专项维修资金管理专户日报表查询（临时户）',
				height: 400,
				width: 500,
				model: true
			},
			formConfig: {
				items: [v_searchCont3]
				//items: [ area,fromDateField,toDateField ]		
			},
			buttons: [{
				id: 'Report-info',
				text: '查询',
				scope: this,
				handler: function(){
					var date = getFormatDate();
					var condition = f_addSearchCont();
					var defineParam = '__report=report/fund_temp_daily.rptdesign&condition=' + condition + '&date=' + date;
					reportViewer(defineParam);
				}
			}]
		});
		houseTotalWin.show();
	},
	icon: 'report2.jpg',
	desc: '临时户'
},{
    text: '住宅专项维修资金管理专户日报表',
    func: function(){
    	var houseTotalWin = new Ext.app.FormWindow({
    		bannerPanel: false,
			winConfig: {
				title: '住宅专项维修资金管理专户日报表查询（正式户）',
				height: 400,
				width: 500,
				model: true
			},
			formConfig: {
				items: [v_searchCont1]
				//items: [ area,fromDateField,toDateField ]		
			},
			buttons: [{
				id: 'Report-info',
				text: '查询',
				scope: this,
				handler: function(){
					var userName = loginUser.userName;
					var date = getFormatDate();
					var areaId = f_addSearchCont2();//Ext.getCmp('areaId');
					//alert(areaId);
					var area = getAreaName();
					//alert(area);
					var condition = f_addSearchCont();
					var defineParam = '__report=report/fund_sub_daily.rptdesign&condition=' + condition + '&areaId=' + areaId+ '&date=' + date + '&area=' + area;
					reportViewer(defineParam);
				}
			}]
		});
		houseTotalWin.show();
	},
	icon: 'report2.jpg',
	desc: '正式户'
},{
    text: '住宅专项维修资金缴纳分户明细表',
    func: function(){
    	var houseTotalWin = new Ext.app.FormWindow({
    		bannerPanel: false,
			winConfig: {
				title: '住宅专项维修资金缴纳分户明细查询',
				height: 400,
				width: 500,
				model: true
			},
			formConfig: {
				items: [v_searchCont1]
				//items: [ area,fromDateField,toDateField ]		
			},
			buttons: [{
				id: 'Report-info',
				text: '查询',
				scope: this,
				handler: function(){
					var condition = f_addSearchCont();
					var date = getFormatDate();
					var areaId = f_addSearchCont2();//Ext.getCmp('areaId');
					//alert(areaId);
					var area = getAreaName();
					//alert(area);
					var defineParam = '__report=report/pay_sub_detail.rptdesign&condition=' + condition + '&areaId=' + areaId+ '&date=' + date + '&area=' + area;
					reportViewer(defineParam);
				}
			}]
		});
		houseTotalWin.show();
	},
	icon: 'report2.jpg',
	desc: '正式户'
},{
    text: '住宅专项维修资金缴纳批量情况表',
    func: function(){
    	var houseTotalWin = new Ext.app.FormWindow({
    		bannerPanel: false,
			winConfig: {
				title: '住宅专项维修资金缴纳批量情况查询',
				height: 400,
				width: 500,
				model: true
			},
			formConfig: {
				items: [v_searchCont3]
				//items: [ area,fromDateField,toDateField ]		
			},
			buttons: [{
				id: 'Report-info',
				text: '查询',
				scope: this,
				handler: function(){
					var userName = loginUser.userName;
					var condition = f_addSearchCont();
					var date = getFormatDate();
					var defineParam = '__report=report/pay_temp_detail.rptdesign&condition=' + condition + '&date=' + date;
					reportViewer(defineParam);
				}
			}]
		});
		houseTotalWin.show();
	},
	icon: 'report2.jpg',
	desc: '临时户'
}];

ReportView = Ext.extend(Ext.DataView, {
	autoHeight: true,
    frame: true,
    cls: 'demos',
    itemSelector: 'dd',
    overClass: 'over',
    tpl: new Ext.XTemplate(
    	'<div class="data-ct">',
            '<tpl for=".">',
                '<dd><img src="'+ctx+'/include/image/report/{icon}"/>',
                    '<div><h4>{text}</h4><p>{desc}</p></div>',
                '</dd>',
            '</tpl>',
            '<div style="clear:left"></div>',
        '</div>'
    ),
    initComponent: function(){
    	ReportView.superclass.initComponent.call(this);
    	this.on('click',function(dataview,index,node,e){
    		dataview.store.getAt(index).data.func();
    	},this);
    }
});

Report = Ext.extend(Ext.Panel, {
	cls: 'dataView',
	border: true,
    closable: true,
    layout: 'fit',
    initComponent: function(){
    	this.items = [
    		new ReportView({
	    		store: new Ext.data.JsonStore({
	    		    fields: ['text','icon','desc','func'],
	    		    data: r_reports
	    		})
    		})
    	];
    	Report.superclass.initComponent.call(this);    	
    },
    loadData: function(){
    }
});