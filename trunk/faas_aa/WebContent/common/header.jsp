<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="shortcut icon" href="<s:url value="/include/image/icon/favicon.ico"/>" type="image/x-icon" />
<link rel="icon" href="<s:url value="/include/image/icon/favicon32.png"/>" sizes="32x32"/>

<!-- include extJs lib -->
<s:component template="extlib.ftl">
</s:component>

<link rel="stylesheet" type="text/css" href="<s:url value="/include/css/fjdp-all.css"/>" />
<link rel="stylesheet" type="text/css" href="<s:url value="/include/css/app-all.css"/>" />

<script type="text/javascript" src="<s:url value="/include/js/ext-lang-zh_CN.js"/>" charset="utf-8"></script>
<script>
	ctx = '<%=request.getContextPath()%>';
	loginUser = {
		userName : '<s:property value="#session.authedUser.displayName" escape="false"/>',
		annualLeave : '<s:property value="#session.annualLeave" escape="false"/>',
		organizationName : '<s:property value="#session.organizationName" escape="false"/>',
		organizationId : '<s:property value="#session.organizationId" escape="false"/>',
		changed : '<s:property value="#session.authedUser.passwordChanged" escape="false" />',
		privileges : '<s:property value="#session.privilegesString" escape="false" default="[]"/>',
		roles : '<s:property value="#session.rolesString" escape="false" default="[]"/>',
		ownRole : function(roleName){
			return loginUser.roles.indexOf(roleName) >= 0;
		},
		ownPrivilege : function(privilegeCode){
			return loginUser.privileges.indexOf(privilegeCode) >= 0;
		}
	};
	
</script>
<script type="text/javascript" src="<s:url value="/include/js/ext-override.js"/>"></script>
<script type="text/javascript" src="<s:url value="/include/js/ext-extends.js"/>"></script>
<script type="text/javascript" src="<s:url value="/include/js/fjdp-all.js"/>"></script>
<script type="text/javascript" src="<s:url value="/widget/app-all.js"/>"></script>
<script type="text/javascript" src="<s:url value="/main/TabCloseMenu.js"/>"></script>
<script type="text/javascript" src="<s:url value="/widget/extra/multiSelect.js"/>"></script>
<script type="text/javascript" src="<s:url value="/widget/extra/itemSelector.js"/>"></script>
<script type="text/javascript" src="<s:url value="/widget/extra/TreeField.js"/>"></script>
<script type="text/javascript" src="<s:url value="/widget/app/holidayApprove.js"/>"></script>
