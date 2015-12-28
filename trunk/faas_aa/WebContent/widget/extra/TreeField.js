Ext.form.TreeField = Ext.extend(Ext.form.TriggerField,  {  
    readOnly : true , 
    editable:false,
    displayField : 'text',  
    valueField : 'id',  
    hiddenName : null,  
    listWidth : null,  
    minListWidth : 50,  
    listHeight : 340,  
    minListHeight : 50,  
    dataUrl : null,  
    tree : null,  
    value : "",  
    displayValue : null,  
    baseParams : {},  
    valueList : [],
	textList : [],
    treeRootConfig : {  
        id : ' ',  
        text : 'please select...',  
        draggable:false  
    },  
    
    defaultAutoCreate : {tag: "input", type: "text", size: "24", autocomplete: "off"},  
  
    initComponent : function(){ 
        Ext.form.TreeField.superclass.initComponent.call(this);  
        this.addEvents(  
                'select',  
                'expand',  
                'collapse',  
                'beforeselect'       
        );  
    },  
    initList : function(){  
        if(!this.list){  
            var cls = 'x-treefield-list';  
  
            this.list = new Ext.Layer({  
                shadow: this.shadow, cls: [cls, this.listClass].join(' '), constrain:false  
            });  
  
            var lw = this.listWidth || Math.max(this.wrap.getWidth(), this.minListWidth);  
            this.list.setWidth(lw);  
            this.list.swallowEvent('mousewheel');  
              
            this.innerList = this.list.createChild({cls:cls+'-inner'});  
            this.innerList.setWidth(lw - this.list.getFrameWidth('lr'));  
            this.innerList.setHeight(this.listHeight || this.minListHeight);  
            if(!this.tree){  
                this.tree = this.createTree(this.innerList);      
            }  
            this.tree.on('click',this.select,this);  
            this.tree.on('checkchange',this.checkchange,this);
            this.tree.render();  
        }  
    },  
    onRender : function(ct, position){  
        Ext.form.TreeField.superclass.onRender.call(this, ct, position);  
        if(this.hiddenName){  
            this.hiddenField = this.el.insertSibling({tag:'input',   
                                                     type:'hidden',   
                                                     name: this.hiddenName,   
                                                     id: (this.hiddenId||this.hiddenName)},  
                    'before', true);  
            this.hiddenField.value =  
                this.hiddenValue !== undefined ? this.hiddenValue :  
                this.value !== undefined ? this.value : '';  
            this.el.dom.removeAttribute('name');  
        }  
        if(Ext.isGecko){  
            this.el.dom.setAttribute('autocomplete', 'off');  
        }  
  
        this.initList();  
    },  
    select : function(node){  
        if(this.fireEvent('beforeselect', node, this)!= false){  
            this.onSelect(node);  
            this.fireEvent('select', this, node);  
        }  
    },  
    checkchange:function(node){
    	if (!node.leaf) {
			this.setValues(node);
			node.expand(true, false, function() {
				node.eachChild(function(child) {
					child.ui.toggleCheck(node.attributes.checked);
					child.attributes.checked = node.attributes.checked;
					child.fireEvent('checkchange', child,node.attributes.checked);
				});
			});

		} else {
			this.setValues(node);
		}
    },
    onSelect:function(node){  
        this.setValue(node);  
        this.collapse();  
    },  
    createTree:function(el){ 
    	var Tree = Ext.tree;
        var tree = new Tree.TreePanel({  
            el:el,  
            autoScroll:true,  
            animate:true,
            height: this.listHeight,
            containerScroll: true,   
            loader: new Tree.TreeLoader({  
                dataUrl : this.dataUrl,  
                baseParams : this.baseParams  
            })  
        });  
      
        var root = new Tree.AsyncTreeNode(this.treeRootConfig);  
        tree.setRootNode(root);  
        return tree;  
    },  
  
    getValue : function(){  
        if(this.valueField){  
            return typeof this.hiddenField.value != 'undefined' ? this.hiddenField.value : '';  
        }else{  
            return Ext.form.TreeField.superclass.getValue.call(this);  
        }  
    },  
    setValues:function(node){
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

		var str = this.textList.toString();
		this.setRawValue(str);

		if (this.hiddenField) {
			this.hiddenField.value = this.valueList.toString();
		}
	},
    setValue : function(node){  
        var text = '',value = '';  
        if(node &&Object.prototype.toString.apply(node) === '[object Array]'){
        	for(var i= 0;i<node.length; i++){
        		if(text) {
					text += ',';
				}
				text += node[i].text
				if(value) {
					value += ',';
				}
				value += node[i].id
        	}
        }
        else if(node && typeof node == 'object'){  
            text = node[this.displayField];  
            value = node[this.valueField || this.displayField];  
        }
        else{  
            text = node;  
            value = node;  
                  
        }
        
		this.setRawValue(text);

		if (this.hiddenField) {
			this.hiddenField.value = value;
		}
    },  
    onResize: function(w, h){  
        Ext.form.TreeField.superclass.onResize.apply(this, arguments);  
        if(this.list && this.listWidth == null){  
            var lw = Math.max(w, this.minListWidth);  
            this.list.setWidth(lw);  
            this.innerList.setWidth(lw - this.list.getFrameWidth('lr'));  
        }  
    },  
    validateBlur : function(){  
        return !this.list || !this.list.isVisible();     
    },  
    onDestroy : function(){  
        if(this.list){  
            this.list.destroy();  
        }  
        if(this.wrap){  
            this.wrap.remove();  
        }  
        Ext.form.TreeField.superclass.onDestroy.call(this);  
    },  
    collapseIf : function(e){  
        if(!e.within(this.wrap) && !e.within(this.list)){  
            this.collapse();  
        }  
    },  
  
    collapse : function(){  
        if(!this.isExpanded()){  
            return;  
        }  
        this.list.hide();  
        Ext.getDoc().un('mousewheel', this.collapseIf, this);  
        Ext.getDoc().un('mousedown', this.collapseIf, this);  
        this.fireEvent('collapse', this);  
    },  
    expand : function(){  
        if(this.isExpanded() || !this.hasFocus){  
            return;  
        }
        this.onExpand();  
        this.list.alignTo(this.wrap, this.listAlign);  
        this.list.show();  
        Ext.getDoc().on('mousewheel', this.collapseIf, this);  
        Ext.getDoc().on('mousedown', this.collapseIf, this);  
        this.fireEvent('expand', this);  
    },  
    onExpand : function(){  
        var doc = Ext.getDoc();  
        this.on('click',function(){alert(111)},this);  
    },  
    isExpanded : function(){  
        return this.list && this.list.isVisible();  
    },  
    onTriggerClick : function(){  
        if(this.disabled){  
            return;  
        }  
        if(this.isExpanded()){  
            this.collapse();  
        }else {  
            this.onFocus({});  
            this.expand();  
        }  
        this.el.focus();  
    }  
});  
Ext.reg('treeField', Ext.form.TreeField); 