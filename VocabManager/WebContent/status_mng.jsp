<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ page errorPage="error_page.jsp" %>

<c:set var="list" value="${requestScope.list }" />
<c:if test="${sessionScope['lang'] == null }">
	<c:set var="lang" value="en" scope="session" />
</c:if>

<c:if test="${param.lang != null }">
	<c:set var="lang" value="${param.lang}" scope="session" />
</c:if>

<fmt:setLocale value="${sessionScope['lang']}" />
<fmt:setBundle basename="status_msg" />

<!DOCTYPE html>
<html>
<head>
<meta http-equiv='Content-Type' content='text/html; charset=UTF-8' />
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet" href="https://www.w3schools.com/w3css/4/w3.css">
<link rel="stylesheet" href="https://www.w3schools.com/w3css/4/w3.css">
<title><fmt:message key="status.title_status_define" /></title>
<script type="text/javascript">
function openSelectLang() {
  var x = document.getElementById("lang_selector");
  if (x.className.indexOf("w3-show") == -1) {
    x.className += " w3-show";
  } else { 
    x.className = x.className.replace(" w3-show", "");
  }
}
</script>
</head>
<body>
	<div class="w3-container" style="overflow-x: auto;">
		<h2><fmt:message key="status.header_all_status_defines" /></h2>
		<div class="w3-row">
			<div class="w3-dropdown-click w3-right w3-center">
				<img alt="vn" onclick="openSelectLang()"
					src="${(sessionScope['lang'] == 'vn') ? 'images/vn_img.jpg' : 'images/en_img.jpg' }"
					class=" w3-button" style="width: 70px" />
				<div id="lang_selector"
					class="w3-dropdown-content w3-bar-block w3-border"
					style="right: 0; min-width: 70px;">
					<a href="?lang=vn"> <img alt="vn" src="images/vn_img.jpg"
						class=" w3-button" style="width: 70px">
					</a> <a href="?lang=en"> <img alt="vn" src="images/en_img.jpg"
						class=" w3-button" style="width: 70px">
					</a>
				</div>
			</div>
		</div>
		<div style="overflow-x: auto;">
			<table class="w3-table-all">
				<tr>
					<th><fmt:message key="status.field_status_id" /></th>
					<th><fmt:message key="status.field_status_description" /></th>
				</tr>
	
				<c:forEach items="${list }" var="item">
					<tr>
						<td><c:out value="${item.getStatus_id() }" /></td>
						<td><c:out value="${item.getStatus_description() }" /></td>
					</tr>
				</c:forEach>
			</table>
		</div>
		
		<div class="w3-container w3-margin-top">
			<a href="StatusController?_action=main" class="w3-btn w3-teal"><fmt:message
					key="status.btn_back" /></a>
		</div>
	</div>
</body>
</html>