<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>

<html>
<head>
  	<title>FJDP</title>
  	<s:include value="/common/header.jsp" />
  	<STYLE type="text/css">
  		.res-block {
		    padding-top:5px;
		    background:transparent url(block-top.gif) no-repeat;
		    width:210px;
		    margin-bottom:15px;
		}
		
		.res-block-inner {
		    padding:6px 11px;
		    background:transparent url(block-bottom.gif) no-repeat left bottom;
		}
  	</STYLE>
	
</head>
<body>
<div class="res-block">
    <div class="res-block-inner">
        <h3>Additional Resources</h3>
        <ul>
            <li><a href="http://dev.sencha.com/deploy/dev/examples/" target="_blank">Ext JS 3.3 Samples</a></li>
            <li><a href="http://www.sencha.com/learn/Ext_FAQ" target="_blank">Ext JS FAQ</a></li>
            <li><a href="http://www.sencha.com/learn/Tutorials" target="_blank">Ext JS Tutorials</a></li>
            <li><a href="http://www.sencha.com/learn" target="_blank">Sencha Learning Center</a></li>
            <li><a href="http://www.sencha.com/learn/Ext_Manual" target="_blank">Sencha Community Manual</a></li>
            <li><a href="http://www.sencha.com/blog/" target="_blank">Sencha Blog</a></li>
        </ul>
    </div>
</div>
</body>
</html>

