EmployeeSelect = Ext.extend(Ext.app.CompositeSelect, {
	dataUrl : '/employee/search',
	storeFields : ['id','text','code','pinyin','orgName']	,
	tpl:new Ext.XTemplate(
		    '<tpl for="."><table class="x-combo-list-item" style="width:100%;"><tr>',
		    '<td>{text}</td><td style="text-align:right;color:gray">{orgName}</td>',
		    '</tr></table></tpl>'
	),
	initComponent : function(){
		EmployeeSelect.superclass.initComponent.call(this);
	}
});
Ext.reg('f-employee', EmployeeSelect);

EmployeeSelectByOrganization = Ext.extend(Ext.app.CompositeSelect, {
	dataUrl : '/employee/getEmployeesByOrganization',
	storeFields : ['id','text','code','pinyin','orgName']	,
	tpl:new Ext.XTemplate(
		    '<tpl for="."><table class="x-combo-list-item" style="width:100%;"><tr>',
		    '<td>{text}</td><td style="text-align:right;color:gray">{orgName}</td>',
		    '</tr></table></tpl>'
	),
	initComponent : function(){
		EmployeeSelectByOrganization.superclass.initComponent.call(this);
	}
});
Ext.reg('f-OrganizationEmployee', EmployeeSelectByOrganization);
/**
 * 下面的是根据部门查出不同的人员，人力资源部全部
 */
AttEmployeeSelectByOrganization = Ext.extend(Ext.app.CompositeSelect, {
	dataUrl : '/employee/getAttEmployeeByOrganization',
	storeFields : ['id','text','code','pinyin','orgName']	,
	tpl:new Ext.XTemplate(
		    '<tpl for="."><table class="x-combo-list-item" style="width:100%;"><tr>',
		    '<td>{text}</td><td style="text-align:right;color:gray">{orgName}</td>',
		    '</tr></table></tpl>'
	),
	initComponent : function(){
	AttEmployeeSelectByOrganization.superclass.initComponent.call(this);
	}
});
Ext.reg('f-AttOrganizationEmployee', AttEmployeeSelectByOrganization);


PropertyCompanySelect = Ext.extend(Ext.app.CompositeSelect,{
	dataUrl : '/propertyCompany/getPropertyCompanys'
});
Ext.reg('f-property', PropertyCompanySelect);

DeveloperSelect = Ext.extend(Ext.app.CompositeSelect,{
	dataUrl : '/developer/getDevelopers'
});
Ext.reg('f-developer', DeveloperSelect);

AreaSelect = Ext.extend(Ext.app.CompositeSelect,{
	dataUrl : '/area/getAreas'
});
Ext.reg('f-area', AreaSelect);

ProjectSelect = Ext.extend(Ext.app.CompositeSelect,{
	hasRelative : true,
	relativeHeader : '所属镇(街)',
	dataUrl : '/project/getProjects'
});
Ext.reg('f-project', ProjectSelect);

BuildingSelect = Ext.extend(Ext.app.CompositeSelect,{
	hasRelative : true,
	relativeHeader : '所属项目',
	dataUrl : '/project/getBuildings'
});
Ext.reg('f-building', BuildingSelect);

HouseSelect = Ext.extend(Ext.app.CompositeSelect,{
	hasRelative : true,
	relativeHeader : '所属项目',
	dataUrl : '/house/getHouses'
});
Ext.reg('f-house', HouseSelect);

Ext.ux.xLovTreeCombo = Ext.extend(Ext.form.ComboBox, {
	triggerClass : 'x-form-tree-trigger',
	initList : function() {
		this.list = new Ext.tree.TreePanel({
			floating : true,
			autoHeight : false,
			autoExpand : true,
			autoScroll : true,
			height : 240,
			rootVisible : false,
			containerScroll : true,
			dataUrl : this.url,
			root : {
				nodeType : 'async',
				text : 'root',
				draggable : false,
				id : 'root'
			},

			listeners : {
				checkchange : this.onNodeCheckChange,
				scope : this

			},
			useArrows : true,
			alignTo : function(el, pos) {
				this.setPagePosition(this.el.getAlignToXY(el, pos));
			}
		});
	},

	expand : function() {
		if (!this.list.rendered) {
			this.list.render(document.body);
			this.list.setWidth(this.listWidth);
			this.innerList = this.list.body;
			this.list.hide();
		}
		this.el.focus();
		Ext.ux.xLovTreeCombo.superclass.expand.apply(this, arguments);
	},

	doQuery : function(q, forceAll) {
		this.expand();
	},

	collapseIf : function(e) {
		if (!e.within(this.wrap) && !e.within(this.list.el)) {
			this.collapse();
		}
	},

	valueList : [],
	textList : [],

	getvalueList : function() {
		return this.valueList;
	},

	onNodeCheckChange : function(node, e) {
		if (!node.leaf) {
			node.expand(true, false, function() {
				node.eachChild(function(child) {
					child.ui.toggleCheck(node.attributes.checked);
					child.attributes.checked = node.attributes.checked;
					child.fireEvent('checkchange', child,
							node.attributes.checked);
				});
			});
		} else {
			var nodeValue = node.id;
			var test = this.valueList.indexOf(nodeValue);

			if (test == -1 && node.attributes.checked) {
				this.valueList.push(nodeValue)
				this.textList.push(node.text);
			}

			if (test != -1 && !node.attributes.checked) {
				this.valueList.remove(nodeValue);
				this.textList.remove(node.text);
			}

			//if(window.console){console.log(this.valueList.toString())}共选择了'+this.valueList.length.toString()+'菜单：'+
			var str = this.textList.toString();
			this.setRawValue(str);

			if (this.hiddenField) {
				this.hiddenField.value = node.id;
			}
		}
		//this.collapse();
	},
	url : '',
	reset : function() {

		this.valueList.length = 0;
		this.textList.length = 0;
		this.applyEmptyText();

	},

	resetNode : function(node) {
		this.collapseNode(node);
		this.uncheckNode(node);
	},
	collapseNode : function(node) {
		if (node.isExpanded()) {
			node.collapse();
		}
	},
	uncheckNode : function(node) {
		if (node.getUI().isChecked()) {
			if (window.console) {
				console.log("未能选中此节点ID " + node.attributes.id)
			}
			node.getUI().toggleCheck(false);
		}
	}
});

Ext.reg('multi-treecombo', Ext.ux.xLovTreeCombo);
