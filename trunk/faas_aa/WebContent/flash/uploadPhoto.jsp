<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>

<script type="text/javascript">
	var swfVersionStr = "10.0.0";
	var xiSwfUrlStr = ctx+"/flash/"+"playerProductInstall.swf";
	var flashvars = {};
	var params = {};
	params.quality = "high";
	params.bgcolor = "#337da7";
	params.allowscriptaccess = "sameDomain";
	params.allowfullscreen = "true";
	var attributes = {};
	attributes.id = "UploadPhoto";
	attributes.name = "UploadPhoto";
	attributes.align = "middle";
	swfobject.embedSWF(
    	ctx+"/flash/"+"UploadPhoto.swf", "flashContent", 
    	"100%", "100%", 
    	swfVersionStr, xiSwfUrlStr,flashvars, params, attributes);
	swfobject.createCSS("#flashContent", "display:block;text-align:left;");
</script>
<div id="flashContent">
	<p>
		loading... 
	</p>
</div>