<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ page errorPage="error_page.jsp"%>

<c:set var="list" value="${requestScope.list }" />
<c:set var="action_status" value="${requestScope.action_status }" />
<c:set var="is_successful" value="${requestScope.is_successful }" />

<c:if test="${sessionScope['lang'] == null }">
	<c:set var="lang" value="en" scope="session" />
</c:if>

<c:if test="${param.lang != null }">
	<c:set var="lang" value="${param.lang}" scope="session" />
</c:if>

<fmt:setLocale value="${sessionScope['lang']}" />
<fmt:setBundle basename="lesson_msg" />

<c:if test="${sessionScope['search_lesson'] == null }">
	<c:set var="search_lesson" scope="session" value="ex" />
</c:if>

<c:if test="${param.q != null }">
	<c:set var="search_lesson" scope="session" value="${param.q }" />
</c:if>

<!DOCTYPE html>
<html>
<head>
<meta http-equiv='Content-Type' content='text/html; charset=UTF-8' />
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet" href="https://www.w3schools.com/w3css/4/w3.css">
<title><fmt:message key="lesson.title_lesson_manager" /></title>
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
	<c:if test="${action_status != null }">
		<div
			class="w3-panel ${(is_successful) ? 'w3-green' : 'w3-red'}  w3-display-container">
			<span onclick="this.parentElement.style.display='none'"
				class="w3-button w3-large w3-display-topright">&times;</span>
			<h3>
				<c:out value="${action_status }" />
			</h3>
		</div>
	</c:if>
	
	
	<div class="w3-container" style="overflow-x: auto;">
		<h2>
			<fmt:message key="lesson.header_all_lesson" />
		</h2>
		
		<div class="w3-row">	
			<div class="w3-bar w3-row w3-right">
				<div class="w3-dropdown-click w3-right w3-center">
					<img alt="vn" onclick="openSelectLang()" 
								src="${(sessionScope['lang'] == 'vn') ? 'images/vn_img.jpg' : 'images/en_img.jpg' }"
								class=" w3-button"
								style="height: 40px"/>
					<div id="lang_selector" 
							class="w3-dropdown-content w3-bar-block w3-border" 
							style="right:0; min-width: 70px;">
						<a class="w3-button" 
							style="display: block;"
							href="?lang=vn&page=${page }&q=${sessionScope['search_lesson']}">
							<img alt="vn" src="images/vn_img.jpg"								
								style="width: 70px; display: block;">
						</a> 
						<a class="w3-button"
							style="display: block;"
							href="?lang=en&page=${page }&q=${sessionScope['search_lesson']}">
							<img alt="vn" src="images/en_img.jpg"
								style="width: 70px; display: block;">
						</a>
					</div>
				</div>
				<div class="w3-third w3-right"  style="display: inline; height: 100%;">
					<div class="w3-row">
						<div class="w3-col" style="width: 80%">
							<input id="txt_search" 
								style="height: 40px;" type="search"
								value="${sessionScope['search_lesson'] }" 
								class="w3-border w3-input w3-hover-khaki" placeholder="Search..">
						</div>
						<div class="w3-col" style="width: 20%;">
							<a style="height: 40px; width: 100%"
								onclick="gotoSearch()"
							  	href="#" class="w3-button w3-green">Go</a>
						</div>
					</div>
				</div>				
			</div>				
		</div>

		<div style="overflow-x: auto;">
			<table class="w3-table-all">
				<tr>
					<th><fmt:message key="lesson.field_lesson_id" /></th>
					<th><fmt:message key="lesson.field_lesson_name" /></th>
					<th><fmt:message key="lesson.field_lesson_course" /></th>
					<th><fmt:message key="lesson.field_lesson_status" /></th>
					<th style="width: 20%"><fmt:message key="lesson.field_action" /></th>
				</tr>
								
				<c:forEach items="${list }" var="item">
					<tr>
						<td style="vertical-align: middle;"><c:out value="${item.getLesson_id() }" /></td>
						<td style="vertical-align: middle;"><c:out value="${item.getLesson_name() }" /></td>
						<td style="vertical-align: middle;"><c:out value="${item.getLesson_course() }" /></td>
						<td style="vertical-align: middle;"><c:out value="${item.getLesson_status() }" /></td>
						<td class="w3-row">
							<button
								onclick=""
								class="w3-button w3-half w3-light-green">Edit</button>
							<button
								onclick=""
								class="w3-button w3-half w3-red">Delete</button>
						</td>
					</tr>
				</c:forEach>
			</table>
		</div>
		
		<div class="w3-container w3-margin-top">
			<button onclick="openAddModal()" class="w3-btn w3-teal">
				<fmt:message key="lesson.btn_add_new" />
			</button>
			<a href="?_action=main" class="w3-btn w3-teal"><fmt:message
					key="lesson.btn_back" /></a>
		</div>
	</div>
</body>
</html>