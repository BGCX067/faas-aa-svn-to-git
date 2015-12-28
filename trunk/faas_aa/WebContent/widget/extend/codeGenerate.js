CodeGenerateFieldGrid = Ext.extend(Ext.grid.EditorGridPanel,{
    //clicksToEdit:1,
    frame : true,
    title : '字段列表',
    style : 'padding : 20px;',
    height : 400,
    initComponent : function(){
    	
    	this.FieldItem = Ext.data.Record.create([
           {name: 'type'},
           {name: 'label'},
           {name: 'name'},
           {name: 'extend'},
           {name: 'allowBlank'}
		]);
		
		Ext.apply(this,{
			store : new Ext.data.JsonStore({
				root: 'root',
		        fields: this.FieldItem,
		        data : {root : []}
			}),
    		cm: new Ext.grid.ColumnModel([{
	        	id:'type',header: "字段类型",dataIndex: 'type',width: 100,
	        	editor: new Ext.app.SelectField({
	        		data : [['text','text'],['dict','dict'],['date','date'],
	        				['textArea','textArea'],['int','int'],['double','double']],
	           		allowBlank: false
	           })
	        },{
	        	header: "字段名",dataIndex: 'name',width: 100,
	        	editor: new Ext.app.TextField({allowBlank: false})
	        },{
	        	header: "字段标签",dataIndex: 'label',width: 150,
	        	editor: new Ext.app.TextField({allowBlank: false})
	        },{
	        	header: "扩展属性 kind/dateType/length",dataIndex: 'extend',width: 200,
	        	editor: new Ext.app.TextField()
	        },{
	        	header: "能否为空",dataIndex: 'allowBlank',width: 100,
	        	editor: new Ext.app.SelectField({
	        		data : [['yes','可以为空'],['no','不能为空']],allowBlank: false
	           })
	        }]),
	        tbar: [{
	            text: '新增',
	            iconCls : 'add',
	            scope : this,
	            handler : this.add
	        },
	        {
	            text: '删除',
	            iconCls : 'remove',
	            scope : this,
	            handler : this.del
	        }]
		});
		CodeGenerateFieldGrid.superclass.initComponent.call(this);
	},
	add : function(){
        var item = new this.FieldItem({
        	type : 'text',
        	allowBlank : 'yes'
        });
        this.stopEditing();
        var count = this.store.getCount();
        this.store.insert(count, item);
        this.startEditing(count, 0);
    },
    del : function(){
    	this.stopEditing();
    	var cell = this.getSelectionModel().getSelectedCell();
    	if(cell){
    		var rowIndex = this.getSelectionModel().getSelectedCell()[0];
        	this.store.removeAt(rowIndex);
    	}else{
    		App.msg("没有可用的数据，请选中一行");
    	}
       
    }
	
});

CodeGenerate = Ext.extend(Ext.form.FormPanel,{
	bodyStyle : 'padding: 20px;',
	closable : true,
	border : true,
	frame:true,
	initComponent : function(){
		
		Ext.apply(this,{
			items: [
				{xtype: 'f-text',fieldLabel: '包前缀',name: 'packagePrefix',value: 'com.fortunes.hmfms',allowBlank: false},
				{xtype: 'panel', border : false, cls : 'commentBox',
					html: '包前缀　：com.fortunes.+项目名+.子模块名    例如com.fortunes.levws.info 如果是小项目，可以不分子模块,如 com.fortunes.levws'},
				{xtype: 'f-text',fieldLabel: '模型名',name: 'modelName',allowBlank: false},
				{xtype: 'panel', border : false, cls : 'commentBox',
					html: '实体名称,对应数据库中的一个表，如Employee,User等'},
				new CodeGenerateFieldGrid({id : 'CodeGenerateFieldGrid',width : 800}),
				{xtype: 'panel', border : false, cls : 'commentBox',
					html: '字段名,字段标签不能为空<br/>当字段类型为text时，扩展属性不用填写<br/>为textArea时，扩展属性length为数据库字段的长度<br/>为dict时，扩展属性kind为字典类型<br/>为date时，扩展属性dateType为[date,time,dateTime]其中一个,默认不填时为date'}
			],
			buttonAlign : 'center',
			buttons : [{
				text : '生成',
				scope : this,
				handler : this.generate
			}]
		});
		CodeGenerate.superclass.initComponent.call(this);
	},
	loadData : function(){
		
	},
	generate : function(){
		var grid = Ext.getCmp('CodeGenerateFieldGrid');
		grid.stopEditing();
		if(grid.store.getCount() <= 0){
			App.msg("未添加任何字段");
			return;
		}
		grid.store.commitChanges();
		var formParmas =  {
			fieldTypes : [],
			fieldNames : [],
			fieldLabels : [],
			fieldExtend : [],
			fieldAllowBlank : []
		}
		grid.store.each(function(record){
			formParmas['fieldTypes'].push(record.data['type']);
			formParmas['fieldNames'].push(record.data['name']);
			formParmas['fieldLabels'].push(record.data['label']);
			formParmas['fieldExtend'].push(record.data['extend']);
			formParmas['fieldAllowBlank'].push(record.data['allowBlank']);
		});
		this.getForm().submit({
			url : ctx + '/codeGenerate/generate',
			params : formParmas,
			success: function(form,action){
			}
		})
	}
	
});
