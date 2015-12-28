
/**
 * @class Ext.ux.DateTime
 * @extends Ext.form.Field
 */
Ext.ux.DateTime = Ext.extend(Ext.form.Field, {
    /**
     * @cfg {String/Object} defaultAutoCreate DomHelper element spec
     * Let superclass to create hidden field instead of textbox. Hidden will be submittend to server
     */
     defaultAutoCreate:{tag:'input', type:'hidden'}
    /**
     * @cfg {Number} timeWidth Width of time field in pixels (defaults to 100)
     */
    ,timeWidth:85
    /**
     * @cfg {String} dtSeparator Date - Time separator. Used to split date and time (defaults to ' ' (space))
     */
    ,dtSeparator:' '
    /**
     * @cfg {String} hiddenFormat Format of datetime used to store value in hidden field
     * and submitted to server (defaults to 'Y-m-d H:i:s' that is mysql format)
     */
    ,hiddenFormat:'Y-m-d H:i:s'
    /**
     * @cfg {Boolean} otherToNow Set other field to now() if not explicly filled in (defaults to true)
     */
    ,otherToNow:true
    /**
     * @cfg {Boolean} emptyToNow Set field value to now on attempt to set empty value.
     * If it is true then setValue() sets value of field to current date and time (defaults to false)
     */
    /**
     * @cfg {String} timePosition Where the time field should be rendered. 'right' is suitable for forms
     * and 'below' is suitable if the field is used as the grid editor (defaults to 'right')
     */
    ,timePosition:'right' // valid values:'below', 'right'
    /**
     * @cfg {String} dateFormat Format of DateField. Can be localized. (defaults to 'm/y/d')
     */
    ,dateFormat:'m/d/y'
    /**
     * @cfg {String} timeFormat Format of TimeField. Can be localized. (defaults to 'g:i A')
     */
    ,timeFormat:'g:i A'
    /**
     * @cfg {Object} dateConfig Config for DateField constructor.
     */
    /**
     * @cfg {Object} timeConfig Config for TimeField constructor.
     */

    
    /**
     * private
     * creates DateField and TimeField and installs the necessary event handlers
     */
    ,initComponent:function() {
        // call parent initComponent
        Ext.ux.DateTime.superclass.initComponent.call(this);

        // create DateField
        var dateConfig = Ext.apply({}, {
             id:this.id + '-date'
            ,format:this.dateFormat || Ext.form.DateField.prototype.format
            ,width:this.dateWidth
            ,selectOnFocus:this.selectOnFocus
            ,msgTarget : 'qtip'
            ,listeners:{
                  blur:{scope:this, fn:this.onBlur}
                 ,focus:{scope:this, fn:this.onFocus}
            }
        }, this.dateConfig);
        this.df = new Ext.form.DateField(dateConfig);
        this.df.ownerCt = this;
        delete(this.dateFormat);


        // create TimeField
        var timeConfig = Ext.apply({}, {
             id:this.id + '-time'
            ,format:this.timeFormat || Ext.form.TimeField.prototype.format
            ,width:this.timeWidth
            ,selectOnFocus:this.selectOnFocus
            ,msgTarget : 'qtip'
            ,listeners:{
                  blur:{scope:this, fn:this.onBlur}
                 ,focus:{scope:this, fn:this.onFocus}
            }
        }, this.timeConfig);
        this.tf = new Ext.form.TimeField(timeConfig);
        this.tf.ownerCt = this;
        delete(this.timeFormat);

        // relay events
        this.relayEvents(this.df, ['focus', 'specialkey', 'invalid', 'valid']);
        this.relayEvents(this.tf, ['focus', 'specialkey', 'invalid', 'valid']);

    } // eo function initComponent
    
    
    /**
     * private
     * Renders underlying DateField and TimeField and provides a workaround for side error icon bug
     */
    ,onRender:function(ct, position) {
        // don't run more than once
        if(this.isRendered) {
            return;
        }

        // render underlying hidden field
        Ext.ux.DateTime.superclass.onRender.call(this, ct, position);

        // render DateField and TimeField
        // create bounding table
        var t;
        if('below' === this.timePosition || 'bellow' === this.timePosition) {
            t = Ext.DomHelper.append(ct, {tag:'table',style:'border-collapse:collapse',children:[
                 {tag:'tr',children:[{tag:'td', style:'padding-bottom:1px', cls:'ux-datetime-date'}]}
                ,{tag:'tr',children:[{tag:'td', cls:'ux-datetime-time'}]}
            ]}, true);
        }
        else {
            t = Ext.DomHelper.append(ct, {tag:'table',style:'border-collapse:collapse',children:[
                {tag:'tr',children:[
                    {tag:'td',style:'padding-right:4px', cls:'ux-datetime-date'},{tag:'td', cls:'ux-datetime-time'}
                ]}
            ]}, true);
        }

        this.tableEl = t;
//        this.wrap = t.wrap({cls:'x-form-field-wrap'});
        this.wrap = t.wrap();
        this.wrap.on("mousedown", this.onMouseDown, this, {delay:10});

        // render DateField & TimeField
        this.df.render(t.child('td.ux-datetime-date'));
        this.tf.render(t.child('td.ux-datetime-time'));

        // workaround for IE trigger misalignment bug
        if(Ext.isIE && Ext.isStrict) {
            t.select('input').applyStyles({top:0});
        }

        this.on('specialkey', this.onSpecialKey, this);
        this.df.el.swallowEvent(['keydown', 'keypress']);
        this.tf.el.swallowEvent(['keydown', 'keypress']);

        // create icon for side invalid errorIcon
        if('side' === this.msgTarget) {
            var elp = this.el.findParent('.x-form-element', 10, true);
            this.errorIcon = elp.createChild({cls:'x-form-invalid-icon'});

            this.df.errorIcon = this.errorIcon;
            this.tf.errorIcon = this.errorIcon;
        }

        // setup name for submit
        this.el.dom.name = this.hiddenName || this.name || this.id;

        // prevent helper fields from being submitted
        this.df.el.dom.removeAttribute("name");
        this.tf.el.dom.removeAttribute("name");

        // we're rendered flag
        this.isRendered = true;

        // update hidden field
        this.updateHidden();

    } // eo function onRender
    
    
    /**
     * private
     */
    ,adjustSize:Ext.BoxComponent.prototype.adjustSize
    
    
    /**
     * private
     */
    ,alignErrorIcon:function() {
        this.errorIcon.alignTo(this.tableEl, 'tl-tr', [2, 0]);
    }
    
    
    /**
     * private initializes internal dateValue
     */
    ,initDateValue:function() {
        this.dateValue = this.otherToNow ? new Date() : new Date(1970, 0, 1, 0, 0, 0);
    }
    
    
    /**
     * Calls clearInvalid on the DateField and TimeField
     */
    ,clearInvalid:function(){
        this.df.clearInvalid();
        this.tf.clearInvalid();
    } // eo function clearInvalid
    

    /**
     * @private
     * called from Component::destroy. 
     * Destroys all elements and removes all listeners we've created.
     */
    ,beforeDestroy:function() {
        if(this.isRendered) {
//            this.removeAllListeners();
            this.wrap.removeAllListeners();
            this.wrap.remove();
            this.tableEl.remove();
            this.df.destroy();
            this.tf.destroy();
        }
    } // eo function beforeDestroy

    
    /**
     * Disable this component.
     * @return {Ext.Component} this
     */
    ,disable:function() {
        if(this.isRendered) {
            this.df.disabled = this.disabled;
            this.df.onDisable();
            this.tf.onDisable();
        }
        this.disabled = true;
        this.df.disabled = true;
        this.tf.disabled = true;
        this.fireEvent("disable", this);
        return this;
    } // eo function disable
    
    
    /**
     * Enable this component.
     * @return {Ext.Component} this
     */
    ,enable:function() {
        if(this.rendered){
            this.df.onEnable();
            this.tf.onEnable();
        }
        this.disabled = false;
        this.df.disabled = false;
        this.tf.disabled = false;
        this.fireEvent("enable", this);
        return this;
    } // eo function enable
    
    
    /**
     * private Focus date filed
     */
    ,focus:function() {
        this.df.focus();
    } // eo function focus
    
    
    /**
     * private
     */
    ,getPositionEl:function() {
        return this.wrap;
    }
    
    
    /**
     * private
     */
    ,getResizeEl:function() {
        return this.wrap;
    }
    
    
    /**
     * @return {Date/String} Returns value of this field
     */
    ,getValue:function() {
        // create new instance of date
        return this.dateValue ? new Date(this.dateValue) : '';
    } // eo function getValue
    
    
    /**
     * @return {Boolean} true = valid, false = invalid
     * private Calls isValid methods of underlying DateField and TimeField and returns the result
     */
    ,isValid:function() {
        return this.df.isValid() && this.tf.isValid();
    } // eo function isValid
    
    
    /**
     * Returns true if this component is visible
     * @return {boolean} 
     */
    ,isVisible : function(){
        return this.df.rendered && this.df.getActionEl().isVisible();
    } // eo function isVisible
    
    
    /** 
     * private Handles blur event
     */
    ,onBlur:function(f) {
        // called by both DateField and TimeField blur events

        // revert focus to previous field if clicked in between
        if(this.wrapClick) {
            f.focus();
            this.wrapClick = false;
        }

        // update underlying value
        if(f === this.df) {
            this.updateDate();
        }
        else {
            this.updateTime();
        }
        this.updateHidden();

        // fire events later
        (function() {
            if(!this.df.hasFocus && !this.tf.hasFocus) {
                var v = this.getValue();
                if(String(v) !== String(this.startValue)) {
                    this.fireEvent("change", this, v, this.startValue);
                }
                this.hasFocus = false;
                this.fireEvent('blur', this);
            }
        }).defer(100, this);

    } // eo function onBlur
    
    
    /**
     * private Handles focus event
     */
    ,onFocus:function() {
        if(!this.hasFocus){
            this.hasFocus = true;
            this.startValue = this.getValue();
            this.fireEvent("focus", this);
        }
    }
    
    
    /**
     * private Just to prevent blur event when clicked in the middle of fields
     */
    ,onMouseDown:function(e) {
        if(!this.disabled) {
            this.wrapClick = 'td' === e.target.nodeName.toLowerCase();
        }
    }
    
    
    /**
     * private
     * Handles Tab and Shift-Tab events
     */
    ,onSpecialKey:function(t, e) {
        var key = e.getKey();
        if(key === e.TAB) {
            if(t === this.df && !e.shiftKey) {
                e.stopEvent();
                this.tf.focus();
            }
            if(t === this.tf && e.shiftKey) {
                e.stopEvent();
                this.df.focus();
            }
        }
        // otherwise it misbehaves in editor grid
        if(key === e.ENTER) {
            this.updateValue();
        }

    } // eo function onSpecialKey
    
    
    /**
     * private Sets the value of DateField
     */
    ,setDate:function(date) {
        this.df.setValue(date);
    } // eo function setDate
    
    
    /** 
     * private Sets the value of TimeField
     */
    ,setTime:function(date) {
        this.tf.setValue(date);
    } // eo function setTime
    
    
    /**
     * private
     * Sets correct sizes of underlying DateField and TimeField
     * With workarounds for IE bugs
     */
    ,setSize:function(w, h) {
        if(!w) {
            return;
        }
        if('below' === this.timePosition) {
            this.df.setSize(w, h);
            this.tf.setSize(w, h);
            if(Ext.isIE) {
                this.df.el.up('td').setWidth(w);
                this.tf.el.up('td').setWidth(w);
            }
        }
        else {
            this.df.setSize(w - this.timeWidth - 4, h);
            this.tf.setSize(this.timeWidth, h);

            if(Ext.isIE) {
                this.df.el.up('td').setWidth(w - this.timeWidth - 4);
                this.tf.el.up('td').setWidth(this.timeWidth);
            }
        }
    } // eo function setSize
    
    
    /**
     * @param {Mixed} val Value to set
     * Sets the value of this field
     */
    ,setValue:function(val) {
        if(!val && true === this.emptyToNow) {
            this.setValue(new Date());
            return;
        }
        else if(!val) {
            this.setDate('');
            this.setTime('');
            this.updateValue();
            return;
        }
        if ('number' === typeof val) {
          val = new Date(val);
        }
        val = val ? val : new Date(1970, 0 ,1, 0, 0, 0);
        var da, time;
        if(val instanceof Date) {
            this.setDate(val);
            this.setTime(val);
            this.dateValue = new Date(val);
        }
        else {
            da = val.split(this.dtSeparator);
            this.setDate(da[0]);
            if(da[1]) {
                if(da[2]) {
                    // add am/pm part back to time
                    da[1] += da[2];
                }
                this.setTime(da[1]);
            }
        }
        this.updateValue();
    } // eo function setValue
    
    
    /**
     * Hide or show this component by boolean
     * @return {Ext.Component} this
     */
    ,setVisible: function(visible){
        if(visible) {
            this.df.show();
            this.tf.show();
        }else{
            this.df.hide();
            this.tf.hide();
        }
        return this;
    } // eo function setVisible
    
    ,show:function() {
        return this.setVisible(true);
    } // eo function show

    ,hide:function() {
        return this.setVisible(false);
    } // eo function hide
    
    /**
     * private Updates the date part
     */
    ,updateDate:function() {

        var d = this.df.getValue();
        if(d) {
            if(!(this.dateValue instanceof Date)) {
                this.initDateValue();
                if(!this.tf.getValue()) {
                    this.setTime(this.dateValue);
                }
            }
            this.dateValue.setMonth(0); // because of leap years
            this.dateValue.setFullYear(d.getFullYear());
            this.dateValue.setMonth(d.getMonth());
            this.dateValue.setDate(d.getDate());
        }
        else {
            this.dateValue = '';
            this.setTime('');
        }
    } // eo function updateDate
    
    
    /**
     * private
     * Updates the time part
     */
    ,updateTime:function() {
        var t = this.tf.getValue();
        if(t && !(t instanceof Date)) {
            t = Date.parseDate(t, this.tf.format);
        }
        if(t && !this.df.getValue()) {
            this.initDateValue();
            this.setDate(this.dateValue);
        }
        if(this.dateValue instanceof Date) {
            if(t) {
                this.dateValue.setHours(t.getHours());
                this.dateValue.setMinutes(t.getMinutes());
                this.dateValue.setSeconds(t.getSeconds());
            }
            else {
                this.dateValue.setHours(0);
                this.dateValue.setMinutes(0);
                this.dateValue.setSeconds(0);
            }
        }
    } // eo function updateTime
    
    
    /**
     * private Updates the underlying hidden field value
     */
    ,updateHidden:function() {
        if(this.isRendered) {
            var value = this.dateValue instanceof Date ? this.dateValue.format(this.hiddenFormat) : '';
            this.el.dom.value = value;
        }
    }
    
    
    /**
     * private Updates all of Date, Time and Hidden
     */
    ,updateValue:function() {

        this.updateDate();
        this.updateTime();
        this.updateHidden();

        return;
    } 
    
    
    /**
     * @return {Boolean} true = valid, false = invalid
     * callse validate methods of DateField and TimeField
     */
    ,validate:function() {
        return this.df.validate() && this.tf.validate();
    } 
    
    
    /**
     * Returns renderer suitable to render this field
     * @param {Object} Column model config
     */
    ,renderer: function(field) {
        var format = field.editor.dateFormat || Ext.ux.DateTime.prototype.dateFormat;
        format += ' ' + (field.editor.timeFormat || Ext.ux.DateTime.prototype.timeFormat);
        var renderer = function(val) {
            var retval = Ext.util.Format.date(val, format);
            return retval;
        };
        return renderer;
    } 
    

}); 

Ext.reg('xdatetime', Ext.ux.DateTime);


Ext.form.FileUploadField = Ext.extend(Ext.form.TextField,  {
    /**
     * @cfg {String} buttonText The button text to display on the upload button (defaults to
     * 'Browse...').  Note that if you supply a value for {@link #buttonCfg}, the buttonCfg.text
     * value will be used instead if available.
     */
    buttonText: 'Browse...',
    /**
     * @cfg {Boolean} buttonOnly True to display the file upload field as a button with no visible
     * text field (defaults to false).  If true, all inherited TextField members will still be available.
     */
    buttonOnly: false,
    /**
     * @cfg {Number} buttonOffset The number of pixels of space reserved between the button and the text field
     * (defaults to 3).  Note that this only applies if {@link #buttonOnly} = false.
     */
    buttonOffset: 3,
    /**
     * @cfg {Object} buttonCfg A standard {@link Ext.Button} config object.
     */

    // private
    readOnly: true,
    
    /**
     * @hide 
     * @method autoSize
     */
    autoSize: Ext.emptyFn,
    
    // private
    initComponent: function(){
        Ext.form.FileUploadField.superclass.initComponent.call(this);
        
        this.addEvents(
            /**
             * @event fileselected
             * Fires when the underlying file input field's value has changed from the user
             * selecting a new file from the system file selection dialog.
             * @param {Ext.form.FileUploadField} this
             * @param {String} value The file value returned by the underlying file input field
             */
            'fileselected'
        );
    },
    
    // private
    onRender : function(ct, position){
        Ext.form.FileUploadField.superclass.onRender.call(this, ct, position);
        
        this.wrap = this.el.wrap({cls:'x-form-field-wrap x-form-file-wrap'});
        this.el.addClass('x-form-file-text');
        this.el.dom.removeAttribute('name');
        
        this.fileInput = this.wrap.createChild({
            id: this.getFileInputId(),
            name: this.name||this.getId(),
            cls: 'x-form-file',
            tag: 'input', 
            type: 'file',
            size: 1
        });
        
        var btnCfg = Ext.applyIf(this.buttonCfg || {}, {
            text: this.buttonText
        });
        this.button = new Ext.Button(Ext.apply(btnCfg, {
            renderTo: this.wrap,
            cls: 'x-form-file-btn' + (btnCfg.iconCls ? ' x-btn-icon' : '')
        }));
        
        if(this.buttonOnly){
            this.el.hide();
            this.wrap.setWidth(this.button.getEl().getWidth());
        }
        
        this.fileInput.on('change', function(){
            var v = this.fileInput.dom.value;
            this.setValue(v);
            this.fireEvent('fileselected', this, v);
        }, this);
    },
    
    // private
    getFileInputId: function(){
        return this.id+'-file';
    },
    
    // private
    onResize : function(w, h){
        Ext.form.FileUploadField.superclass.onResize.call(this, w, h);
        
        this.wrap.setWidth(w);
        
        if(!this.buttonOnly){
            var w = this.wrap.getWidth() - this.button.getEl().getWidth() - this.buttonOffset;
            this.el.setWidth(w);
        }
    },
    
    // private
    preFocus : Ext.emptyFn,
    
    // private
    getResizeEl : function(){
        return this.wrap;
    },

    // private
    getPositionEl : function(){
        return this.wrap;
    },

    // private
    alignErrorIcon : function(){
        this.errorIcon.alignTo(this.wrap, 'tl-tr', [2, 0]);
    }
    
});
Ext.reg('fileuploadfield', Ext.form.FileUploadField);

Ext.ux.LovCombo = Ext.extend(Ext.form.ComboBox, {

	// {{{
    // configuration options
	/**
	 * @cfg {String} checkField name of field used to store checked state.
	 * Change it only if it collides with your normal field
	 */
	 checkField:'checked'

	/**
	 * @cfg {String} separator separator to use between values and texts
	 */
    ,separator:','
	 // }}}
    // {{{
    ,initComponent:function() {
        
		// template with checkbox
		if(!this.tpl) {
			this.tpl = 
				 '<tpl for=".">'
				+'<div class="x-combo-list-item">'
				+'<img src="' + Ext.BLANK_IMAGE_URL + '" '
				+'class="ux-lovcombo-icon ux-lovcombo-icon-'
				+'{[values.' + this.checkField + '?"checked":"unchecked"' + ']}">'
				+'<div class="ux-lovcombo-item-text">{' + this.displayField + '}</div>'
				+'</div>'
				+'</tpl>'
			;
		}
 
        // call parent
        Ext.ux.LovCombo.superclass.initComponent.apply(this, arguments);

		// install internal event handlers
		this.on({
			 scope:this
			,beforequery:this.onBeforeQuery
			,blur:this.onRealBlur
		});

		// remove selection from input field
		this.onLoad = this.onLoad.createSequence(function() {
			if(this.el) {
				var v = this.el.dom.value;
				this.el.dom.value = '';
				this.el.dom.value = v;
			}
		});
 
    } // e/o function initComponent
    // }}}
	// {{{
	/**
	 * Disables default tab key bahavior
	 * @private
	 */
	,initEvents:function() {
		Ext.ux.LovCombo.superclass.initEvents.apply(this, arguments);

		// disable default tab handling - does no good
		this.keyNav.tab = false;

	} // eo function initEvents
	// }}}
	// {{{
	/**
	 * clears value
	 */
	,clearValue:function() {
		this.value = '';
		this.setRawValue(this.value);
		this.store.clearFilter();
		this.store.each(function(r) {
			r.set(this.checkField, false);
		}, this);
		if(this.hiddenField) {
			this.hiddenField.value = '';
		}
	} // eo function clearValue
	// }}}
	// {{{
	/**
	 * @return {String} separator (plus space) separated list of selected displayFields
	 * @private
	 */
	,getCheckedDisplay:function() {
		var re = new RegExp(this.separator, "g");
		return this.getCheckedValue(this.displayField);
	} // eo function getCheckedDisplay

	/**
	 * @return {String} separator separated list of selected valueFields
	 * @private
	 */
	,getCheckedValue:function(field) {
		field = field || this.valueField;
		var c = [];

		// store may be filtered so get all records
		var snapshot = this.store.snapshot || this.store.data;

		snapshot.each(function(r) {
			if(r.get(this.checkField)) {
				c.push(r.get(field));
			}
		}, this);

		return c.join(this.separator);
	} 
	/**
	 * beforequery event handler - handles multiple selections
	 * @param {Object} qe query event
	 * @private
	 */
	,onBeforeQuery:function(qe) {
		qe.query = qe.query.replace(new RegExp(this.getCheckedDisplay() + '[ ' + this.separator + ']*'), '');
	} 
	/**
	 * blur event handler - runs only when real blur event is fired
	 */
	,onRealBlur:function() {
		this.list.hide();
		var v = this.getRawValue();
		var va = [];
		this.store.clearFilter();
		this.store.each(function(r) {
			var re = new RegExp(r.get(this.displayField));
			if(v.match(re)) {
				va.push(r.get(this.valueField));
			}
		}, this);
		this.setValue(va.join(this.separator));
		this.lastSelectionText = this.getCheckedDisplay();
	},
	//和ExtJs 3.0 兼容
	beforeBlur : function(){

	}

	/**
	 * Combo's onSelect override
	 * @private
	 * @param {Ext.data.Record} record record that has been selected in the list
	 * @param {Number} index index of selected (clicked) record
	 */
	,onSelect:function(record, index) {
        if(this.fireEvent('beforeselect', this, record, index) !== false){

			// toggle checked field
			record.set(this.checkField, !record.get(this.checkField));

			// display full list
			this.doQuery(this.allQuery);

			// set (update) value and fire event
			this.setValue(this.getCheckedValue());
            this.fireEvent('select', this, record, index);
        }
	}
	/**
	 * Sets the value of the LovCombo
	 * @param {Mixed} v value
	 */
	,setValue:function(v) {
		if(v) {
			v = '' + v;
			if(this.valueField) {
				this.store.clearFilter();
				this.store.each(function(r) {
					var checked = !(!v.match('(^|' + this.separator + ')' + r.get(this.valueField)+'(' + this.separator + '|$)'));

					r.set(this.checkField, checked);
				}, this);
				this.value = this.getCheckedValue();
				this.setRawValue(this.getCheckedDisplay());
				if(this.hiddenField) {
					this.hiddenField.value = this.value;
				}
			}
			else {
				this.value = v;
				this.setRawValue(v);
				if(this.hiddenField) {
					this.hiddenField.value = v;
				}
			}
		}else {
			this.clearValue();
		}
	} 

}); 
 
Ext.reg('lovcombo', Ext.ux.LovCombo);


Ext.ux.TreeCheckNodeUI = function() {
//	'multiple': multiple check model; 'single': single check model; 'cascade':cascade check model
	this.checkModel = 'cascade';
	//only leaf node has checkbox
	Ext.ux.TreeCheckNodeUI.superclass.constructor.apply(this, arguments);
};

Ext.extend(Ext.ux.TreeCheckNodeUI, Ext.tree.TreeNodeUI, {

    renderElements : function(n, a, targetNode, bulkRender){
    	var tree = n.getOwnerTree();
		this.checkModel = tree.checkModel || this.checkModel;
		this.onlyLeafCheckable = tree.onlyLeafCheckable || false;
    	
        // add some indent caching, this helps performance when rendering a large tree
        this.indentMarkup = n.parentNode ? n.parentNode.ui.getChildIndent() : '';

        //var cb = typeof a.checked == 'boolean';
		var cb = (!this.onlyLeafCheckable || a.leaf);
        var href = a.href ? a.href : Ext.isGecko ? "" : "#";
        var buf = ['<li class="x-tree-node"><div ext:tree-node-id="',n.id,'" class="x-tree-node-el x-tree-node-leaf x-unselectable ', a.cls,'" unselectable="on">',
            '<span class="x-tree-node-indent">',this.indentMarkup,"</span>",
            '<img src="', this.emptyIcon, '" class="x-tree-ec-icon x-tree-elbow" />',
            '<img src="', a.icon || this.emptyIcon, '" class="x-tree-node-icon',(a.icon ? " x-tree-node-inline-icon" : ""),(a.iconCls ? " "+a.iconCls : ""),'" unselectable="on" />',
            cb ? ('<input class="x-tree-node-cb" type="checkbox" ' + (a.checked ? 'checked="checked" />' : '/>')) : '',
            '<a hidefocus="on" class="x-tree-node-anchor" href="',href,'" tabIndex="1" ',
             a.hrefTarget ? ' target="'+a.hrefTarget+'"' : "", '><span unselectable="on">',n.text,"</span></a></div>",
            '<ul class="x-tree-node-ct" style="display:none;"></ul>',
            "</li>"].join('');

        var nel;
        if(bulkRender !== true && n.nextSibling && (nel = n.nextSibling.ui.getEl())){
            this.wrap = Ext.DomHelper.insertHtml("beforeBegin", nel, buf);
        }else{
            this.wrap = Ext.DomHelper.insertHtml("beforeEnd", targetNode, buf);
        }
        
        this.elNode = this.wrap.childNodes[0];
        this.ctNode = this.wrap.childNodes[1];
        var cs = this.elNode.childNodes;
        this.indentNode = cs[0];
        this.ecNode = cs[1];
        this.iconNode = cs[2];
        var index = 3;
        if(cb){
            this.checkbox = cs[3];
            Ext.fly(this.checkbox).on('click', this.check.createDelegate(this,[null]));
            index++;
        }
        this.anchor = cs[index];
        this.textNode = cs[index].firstChild;
    },
    
    // private
    check : function(checked){
        var n = this.node;
		var tree = n.getOwnerTree();
		this.checkModel = tree.checkModel || this.checkModel;
		
		if( checked === null ) {
			checked = this.checkbox.checked;
		} else {
			this.checkbox.checked = checked;
		}
		
		n.attributes.checked = checked;
		tree.fireEvent('check', n, checked);
		
		if(!this.onlyLeafCheckable && this.checkModel == 'cascade'){
			var parentNode = n.parentNode;
			if(parentNode !== null) {
				this.parentCheck(parentNode,checked);
			}
			if( !n.expanded && !n.childrenRendered ) {
				n.expand(false,false,this.childCheck);
			}
			else {
				this.childCheck(n);
			}
		}else if(this.checkModel == 'single'){
			var checkedNodes = tree.getChecked();
			for(var i=0;i<checkedNodes.length;i++){
				var node = checkedNodes[i];
				if(node.id != n.id){
					node.getUI().checkbox.checked = false;
					node.attributes.checked = false;
					tree.fireEvent('check', node, false);
				}
			}
		}
        
    },
    
    // private
	childCheck : function(node){
		var a = node.attributes;
		if(!a.leaf) {
			var cs = node.childNodes;
			var csui;
			for(var i = 0; i < cs.length; i++) {
				csui = cs[i].getUI();
				if(csui.checkbox.checked ^ a.checked)
					csui.check(a.checked);
			}
		}
	},
	
	// private
	parentCheck : function(node ,checked){
		var checkbox = node.getUI().checkbox;
		if(typeof checkbox == 'undefined')return ;
		if(!(checked ^ checkbox.checked))return;
		if(!checked && this.childHasChecked(node))return;
		checkbox.checked = checked;
		node.attributes.checked = checked;
		node.getOwnerTree().fireEvent('check', node, checked);
		
		var parentNode = node.parentNode;
		if( parentNode !== null){
			this.parentCheck(parentNode,checked);
		}
	},
	
	// private
	childHasChecked : function(node){
		var childNodes = node.childNodes;
		if(childNodes || childNodes.length>0){
			for(var i=0;i<childNodes.length;i++){
				if(childNodes[i].getUI().checkbox.checked)
					return true;
			}
		}
		return false;
	},

	//private
    toggleCheck : function(value){
    	var cb = this.checkbox;
        if(cb){
            var checked = (value === undefined ? !cb.checked : value);
            this.check(checked);
        }
    }
});

Ext.ux.StatusBar = Ext.extend(Ext.Toolbar, {
 
    cls : 'x-statusbar',

    busyIconCls : 'x-status-busy',

    busyText : 'Loading...',

    autoClear : 5000,

    // private
    activeThreadId : 0,

    // private
    initComponent : function(){
        if(this.statusAlign=='right'){
            this.cls += ' x-status-right';
        }
        Ext.ux.StatusBar.superclass.initComponent.call(this);
    },

    // private
    afterRender : function(){
        Ext.ux.StatusBar.superclass.afterRender.call(this);

        var right = this.statusAlign == 'right';
        this.statusEl = new Ext.Toolbar.TextItem({
            cls: 'x-status-text ' + (this.iconCls || this.defaultIconCls || ''),
            text: this.text || this.defaultText || ''
        });

        if(right){
            this.add('->');
            this.add(this.statusEl);
        }else{
            this.insert(0, this.statusEl);
            this.insert(1, '->');
        }
    },
    setStatus : function(o){
        o = o || {};

        if(typeof o == 'string'){
            o = {text:o};
        }
        if(o.text !== undefined){
            this.setText(o.text);
        }
        if(o.iconCls !== undefined){
            this.setIcon(o.iconCls);
        }

        if(o.clear){
            var c = o.clear,
                wait = this.autoClear,
                defaults = {useDefaults: true, anim: true};

            if(typeof c == 'object'){
                c = Ext.applyIf(c, defaults);
                if(c.wait){
                    wait = c.wait;
                }
            }else if(typeof c == 'number'){
                wait = c;
                c = defaults;
            }else if(typeof c == 'boolean'){
                c = defaults;
            }

            c.threadId = this.activeThreadId;
            this.clearStatus.defer(wait, this, [c]);
        }
        return this;
    },

    clearStatus : function(o){
        o = o || {};

        if(o.threadId && o.threadId !== this.activeThreadId){
            // this means the current call was made internally, but a newer
            // thread has set a message since this call was deferred.  Since
            // we don't want to overwrite a newer message just ignore.
            return this;
        }

        var text = o.useDefaults ? this.defaultText : '',
            iconCls = o.useDefaults ? (this.defaultIconCls ? this.defaultIconCls : '') : '';

        if(o.anim){
            this.statusEl.fadeOut({
                remove: false,
                useDisplay: true,
                scope: this,
                callback: function(){
                    this.setStatus({
	                    text: text,
	                    iconCls: iconCls
	                });
                    this.statusEl.show();
                }
            });
        }else{
            // hide/show the el to avoid jumpy text or icon
            this.statusEl.hide();
	        this.setStatus({
	            text: text,
	            iconCls: iconCls
	        });
            this.statusEl.show();
        }
        return this;
    },

    setText : function(text){
        this.activeThreadId++;
        this.text = text || '';
        if(this.rendered){
            this.statusEl.setText(this.text);
        }
        return this;
    },

   
    getText : function(){
        return this.text;
    },

    
    setIcon : function(cls){
        this.activeThreadId++;
        cls = cls || '';

        if(this.rendered){
	        if(this.currIconCls){
	            this.statusEl.removeClass(this.currIconCls);
	            this.currIconCls = null;
	        }
	        if(cls.length > 0){
	            this.statusEl.addClass(cls);
	            this.currIconCls = cls;
	        }
        }else{
            this.currIconCls = cls;
        }
        return this;
    },

  
    showBusy : function(o){
        if(typeof o == 'string'){
            o = {text:o};
        }
        o = Ext.applyIf(o || {}, {
            text: this.busyText,
            iconCls: this.busyIconCls
        });
        return this.setStatus(o);
    }
});
Ext.reg('statusbar', Ext.ux.StatusBar);

/*!
 * Ext JS Library 3.0.0
 * Copyright(c) 2006-2009 Ext JS, LLC
 * licensing@extjs.com
 * http://www.extjs.com/license
 */
Ext.ns('Ext.ux.tree');

/**
 * @class Ext.ux.tree.ColumnTree
 * @extends Ext.tree.TreePanel
 * 
 * @xtype columntree
 */
Ext.ux.tree.ColumnTree = Ext.extend(Ext.tree.TreePanel, {
    lines : false,
    borderWidth : Ext.isBorderBox ? 0 : 2, // the combined left/right border for each cell
    cls : 'x-column-tree',

    onRender : function(){
        Ext.tree.ColumnTree.superclass.onRender.apply(this, arguments);
        this.headers = this.header.createChild({cls:'x-tree-headers'});

        var cols = this.columns, c;
        var totalWidth = 0;
        var scrollOffset = 19; // similar to Ext.grid.GridView default

        for(var i = 0, len = cols.length; i < len; i++){
             c = cols[i];
             totalWidth += c.width;
             this.headers.createChild({
                 cls:'x-tree-hd ' + (c.cls?c.cls+'-hd':''),
                 cn: {
                     cls:'x-tree-hd-text',
                     html: c.header
                 },
                 style:'width:'+(c.width-this.borderWidth)+'px;'
             });
        }
        this.headers.createChild({cls:'x-clear'});
        // prevent floats from wrapping when clipped
        this.headers.setWidth(totalWidth+scrollOffset);
        this.innerCt.setWidth(totalWidth);
    }
});

Ext.reg('columntree', Ext.ux.tree.ColumnTree);

//backwards compat
Ext.tree.ColumnTree = Ext.ux.tree.ColumnTree;


/**
 * @class Ext.ux.tree.ColumnNodeUI
 * @extends Ext.tree.TreeNodeUI
 */
Ext.ux.tree.ColumnNodeUI = Ext.extend(Ext.tree.TreeNodeUI, {
    focus: Ext.emptyFn, // prevent odd scrolling behavior

    renderElements : function(n, a, targetNode, bulkRender){
        this.indentMarkup = n.parentNode ? n.parentNode.ui.getChildIndent() : '';

        var t = n.getOwnerTree();
        var cols = t.columns;
        var bw = t.borderWidth;
        var c = cols[0];

        var buf = [
             '<li class="x-tree-node"><div ext:tree-node-id="',n.id,'" class="x-tree-node-el x-tree-node-leaf ', a.cls,'">',
                '<div class="x-tree-col" style="width:',c.width-bw,'px;">',
                    '<span class="x-tree-node-indent">',this.indentMarkup,"</span>",
                    '<img src="', this.emptyIcon, '" class="x-tree-ec-icon x-tree-elbow">',
                    '<img src="', a.icon || this.emptyIcon, '" class="x-tree-node-icon',(a.icon ? " x-tree-node-inline-icon" : ""),(a.iconCls ? " "+a.iconCls : ""),'" unselectable="on">',
                    '<a hidefocus="on" class="x-tree-node-anchor" href="',a.href ? a.href : "#",'" tabIndex="1" ',
                    a.hrefTarget ? ' target="'+a.hrefTarget+'"' : "", '>',
                    '<span unselectable="on">', n.text || (c.renderer ? c.renderer(a[c.dataIndex], n, a) : a[c.dataIndex]),"</span></a>",
                "</div>"];
         for(var i = 1, len = cols.length; i < len; i++){
             c = cols[i];

             buf.push('<div class="x-tree-col ',(c.cls?c.cls:''),'" style="width:',c.width-bw,'px;">',
                        '<div class="x-tree-col-text">',(c.renderer ? c.renderer(a[c.dataIndex], n, a) : a[c.dataIndex]),"</div>",
                      "</div>");
         }
         buf.push(
            '<div class="x-clear"></div></div>',
            '<ul class="x-tree-node-ct" style="display:none;"></ul>',
            "</li>");

        if(bulkRender !== true && n.nextSibling && n.nextSibling.ui.getEl()){
            this.wrap = Ext.DomHelper.insertHtml("beforeBegin",
                                n.nextSibling.ui.getEl(), buf.join(""));
        }else{
            this.wrap = Ext.DomHelper.insertHtml("beforeEnd", targetNode, buf.join(""));
        }

        this.elNode = this.wrap.childNodes[0];
        this.ctNode = this.wrap.childNodes[1];
        var cs = this.elNode.firstChild.childNodes;
        this.indentNode = cs[0];
        this.ecNode = cs[1];
        this.iconNode = cs[2];
        this.anchor = cs[3];
        this.textNode = cs[3].firstChild;
    }
});

//backwards compat
Ext.tree.ColumnNodeUI = Ext.ux.tree.ColumnNodeUI;

