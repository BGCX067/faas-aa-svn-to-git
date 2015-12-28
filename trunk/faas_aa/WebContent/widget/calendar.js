Ext.ns('Ext.calendar');

 (function() {
    Ext.apply(Ext.calendar, {
        Date: {
            diffDays: function(start, end) {
                day = 1000 * 60 * 60 * 24;
                diff = end.clearTime(true).getTime() - start.clearTime(true).getTime();
                return Math.ceil(diff / day);
            },

            copyTime: function(fromDt, toDt) {
                var dt = toDt.clone();
                dt.setHours(
                fromDt.getHours(),
                fromDt.getMinutes(),
                fromDt.getSeconds(),
                fromDt.getMilliseconds());

                return dt;
            },

            compare: function(dt1, dt2, precise) {
                if (precise !== true) {
                    dt1 = dt1.clone();
                    dt1.setMilliseconds(0);
                    dt2 = dt2.clone();
                    dt2.setMilliseconds(0);
                }
                return dt2.getTime() - dt1.getTime();
            },

            // private helper fn
            maxOrMin: function(max) {
                var dt = (max ? 0: Number.MAX_VALUE),
                i = 0,
                args = arguments[1],
                ln = args.length;
                for (; i < ln; i++) {
                    dt = Math[max ? 'max': 'min'](dt, args[i].getTime());
                }
                return new Date(dt);
            },

            max: function() {
                return this.maxOrMin.apply(this, [true, arguments]);
            },

            min: function() {
                return this.maxOrMin.apply(this, [false, arguments]);
            }
        }
    });
})();


Ext.calendar.BoxLayoutTemplate = function(config){
    
    Ext.apply(this, config);
    
    var weekLinkTpl = this.showWeekLinks ? '<div id="{weekLinkId}" class="ext-cal-week-link">{weekNum}</div>' : '';
    
    Ext.calendar.BoxLayoutTemplate.superclass.constructor.call(this,
		'<div class="ext-cal-inner-ct {extraClasses}">',
            '<div class="ext-cal-hd-ct ext-cal-month-hd">',
                '<table class="ext-cal-hd-days-tbl" cellpadding="0" cellspacing="0">',
                    '<tbody>',
                        '<tr>',
                            '<tpl for="days">',
                                '<th class="ext-cal-hd-day{[xindex==1 ? " ext-cal-day-first" : ""]}" title="{.:date("l, Y-m-d")}">{.:date("D")}</th>',
                            '</tpl>',
                        '</tr>',
                    '</tbody>',
                '</table>',
            '</div>',
        	'<div class="ext-cal-body-ct" style="height:{[this.getBodyHeight()]};">',
	        '<tpl for="weeks">',
	            '<div id="{[this.id]}-wk-{[xindex-1]}" class="ext-cal-wk-ct" style="top:{[this.getRowTop(xindex, xcount)]}%; height:{[this.getRowHeight(xcount)]}%;">',
	                '<table class="ext-cal-bg-tbl" cellpadding="0" cellspacing="0">',
	                    '<tbody>',
	                        '<tr>',
	                            '<tpl for=".">',
	                                 '<td id="{[this.id]}-day-{date:date("Ymd")}" class="{cellCls}" plusInfoType="{plusInfoType}">{plusInfoText}</td>',
	                            '</tpl>',
	                        '</tr>',
	                    '</tbody>',
	                '</table>',
	                '<table class="ext-cal-evt-tbl" cellpadding="0" cellspacing="0">',
	                    '<tbody>',
	                        '<tr>',
	                            '<tpl for=".">',
	                                '<td id="{[this.id]}-ev-day-{date:date("Ymd")}" class="{titleCls}"><div>{title}</div></td>',
	                            '</tpl>',
	                        '</tr>',
	                    '</tbody>',
	                '</table>',
	            '</div>',
	        '</tpl>',
	        '</div>',
	     '</div>',{
            getRowTop: function(i, ln){
                return ((i-1)*(100/ln));
            },
            getRowHeight: function(ln){
                return 100/ln;
            },
            getBodyHeight: function(){
                return this.bodyHeight-20;
            }
        }
    );
};

Ext.extend(Ext.calendar.BoxLayoutTemplate, Ext.XTemplate, {
    // private
    applyTemplate : function(o){
        
        Ext.apply(this, o);
        
        var typeTexts = {
        	'LEAVE':'请假',
			'SICK_LEAVE':'病假',
			'ANNUAL_LEAVE':'年假',
			'HOME_LEAVE':'探亲假',
			'MATERNITY_LEAVE':'产假',
			'FAMILYPLANNING_LEAVE':'计划生育假',
			'TRAVEL':'出差',
			'TRAIN':'培训',
			'OVERTIME':'加班',
			'WORK':'工作',
			'OTHER':'其他'
        };
        
        var offset =  this.startDate.getDay()-1;
        if (offset < 0) {
            offset += 7;
        }
        this.viewStart = this.startDate.add(Date.DAY, -offset).clearTime(true);

        // start from current month start, not view start:
        var end = this.startDate.add(Date.MONTH, 1).add(Date.SECOND, -1);
        // fill out to the end of the week:
        this.viewEnd = end.add(Date.DAY, 7 - (end.getDay()==0?7:end.getDay()));
        
        
        
        var w = 0, title = '', first = true, isToday = false, showMonth = false, prevMonth = false, nextMonth = false,
            weeks = [[]],
            today = new Date().clearTime(),
            dt = this.viewStart.clone(),
            thisMonth = this.startDate.getMonth();
        
        for(; w < 6 ; w++){
            if(dt >= this.viewEnd){
                break;
            }
            weeks[w] = [];
            
            for(var d = 0; d < 7; d++){
                isToday = dt.getTime() === today.getTime();
                showMonth = first || (dt.getDate() == 1);
                prevMonth = (dt.getMonth() < thisMonth);
                nextMonth = (dt.getMonth() > thisMonth);
                
                if(dt.getDay() == 1){
                    // The ISO week format 'W' is relative to a Monday week start. If we
                    // make this check on Sunday the week number will be off.
                    weeks[w].weekNum = this.showWeekNumbers ? dt.format('W') : '&nbsp;';
                    weeks[w].weekLinkId = 'ext-cal-week-'+dt.format('Ymd');
                }
                
                if(showMonth){
                    if(isToday){
                        title = this.getTodayText();
                    }
                    else{
                        title = dt.format(first ? 'Y-m-d' : 'M j');
                    }
                }
                else{
                    var dayFmt = (w == 0 && this.showHeader !== true) ? 'D j' : 'j';
                    title = isToday ? this.getTodayText() : dt.format(dayFmt);
                }
                
                var info = this.getPlusInfo(dt,this.plusInfos);
                
                weeks[w].push({
                    title: title,
                    date: dt.clone(),
                    plusInfoText:this.getPlusInfoText(info,typeTexts),
                    plusInfoType:info.type,
                    titleCls: 'ext-cal-dtitle ' + (isToday ? ' ext-cal-dtitle-today' : '') + 
                        (w==0 ? ' ext-cal-dtitle-first' : '') +
                        (prevMonth ? ' ext-cal-dtitle-prev' : '') + 
                        (nextMonth ? ' ext-cal-dtitle-next' : '') ,
                    cellCls: 'ext-cal-day' + (isToday ? ' ext-cal-day-today' : '') + 
                        (d==0 ? ' ext-cal-day-first' : '') +
                        (prevMonth ? ' ext-cal-day-prev' : '') +
                        (nextMonth ? ' ext-cal-day-next' : '') +
                        (this.getPlusInfoCls(info))
                });
                dt = dt.add(Date.DAY, 1);
                first = false;
            }
        }
        
        var days = [],
            dt = this.viewStart;
        
        for(var i = 0; i < 7; i++){
            days.push(dt.add(Date.DAY, i));
        }
        
        var extraClasses = this.showHeader === true ? '' : 'ext-cal-noheader';
        
        return Ext.calendar.BoxLayoutTemplate.superclass.applyTemplate.call(this, {
            weeks: weeks,
            days: days,
            extraClasses: extraClasses 
        });
    },
    
    // private
    getTodayText:function(){
        return '今天  '+new Date().format('Y-m-d') ;
    },
    getPlusInfoText:function(info,typeTexts){
    	if(info.type != 'NONE'){
    		return typeTexts[info.type]+('<br/>'+info.timeRange||'');
    	}else{
    		return '&nbsp;&nbsp;';
    	}
    },
    getPlusInfoCls:function(info){
    	return info.type != 'NONE'? ' plugInfo plugInfo-'+info.type : '';
    },
    getPlusInfo:function(date,plusInfos){
    	for(var i = 0;i < plusInfos.length;i++){
    		var info = plusInfos[i];
    		var d = date.format('Y-m-d');
    		if(info.data.date == d){
    			return info.data;
    		}
    	}
    	return {
    		type:'NONE'
    	};
    }
});

Ext.calendar.BoxLayoutTemplate.prototype.apply = Ext.calendar.BoxLayoutTemplate.prototype.applyTemplate;
