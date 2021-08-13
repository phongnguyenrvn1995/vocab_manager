<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ page errorPage="error_page.jsp"%>



<c:set var="list" value="${requestScope.list }" />
<c:set var="statuses" value="${requestScope.statuses }" />
<c:if test="${sessionScope['lang'] == null }">
	<c:set var="lang" value="en" scope="session" />
</c:if>

<c:if test="${param.lang != null }">
	<c:set var="lang" value="${param.lang}" scope="session" />
</c:if>

<fmt:setLocale value="${sessionScope['lang']}" />
<fmt:setBundle basename="course_msg" />

<c:if test="${sessionScope['search_course'] == null }">
	<c:set var="search_course" scope="session" value="ex" />
</c:if>

<c:if test="${param.q != null }">
	<c:set var="search_course" scope="session" value="${param.q }" />
</c:if>


<!DOCTYPE html>
<html>
<head>
<meta http-equiv='Content-Type' content='text/html; charset=UTF-8' />
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet" href="https://www.w3schools.com/w3css/4/w3.css">
<title><fmt:message key="course.title_course_manager" /></title>
<script type="text/javascript">	

	function openAddModal() {
		document.getElementById('add_modal').style.display='block';
	
		var update_form = document.getElementById('add_form');
		update_form['action'] = 'CourseController';
		update_form['method'] = 'POST';
		update_form['_action'].value = 'save';
	}
	
	function openSelectLang() {
	  var x = document.getElementById("lang_selector");
	  if (x.className.indexOf("w3-show") == -1) {
	    x.className += " w3-show";
	  } else { 
	    x.className = x.className.replace(" w3-show", "");
	  }
	}

	function gotoSearch() {
		var txt_search = document.getElementById('txt_search');
		var search_value = txt_search.value;
		window.location.href = 'CourseController?&q=' + search_value;
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
			<fmt:message key="course.header_all_courses" />
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
							href="?lang=vn&page=${page }">
							<img alt="vn" src="images/vn_img.jpg"								
								style="width: 70px; display: block;">
						</a> 
						<a class="w3-button"
							style="display: block;"
							href="?lang=en&page=${page }">
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
								value="${sessionScope['search_course'] }" 
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
					<th><fmt:message key="course.field_course_id" /></th>
					<th><fmt:message key="course.field_course_name" /></th>
					<th><fmt:message key="course.field_course_desc" /></th>
					<th><fmt:message key="course.field_course_date_create" /></th>
					<th><fmt:message key="course.field_course_status" /></th>
					<th style="width: 20%"><fmt:message key="course.field_action" /></th>
				</tr>
				
				<c:forEach items="${list }" var="item">
					<tr>
						<td style="vertical-align: middle;"><c:out value="${item.getCourse_id() }" /></td>
						<td style="vertical-align: middle;"><c:out value="${item.getCourse_name() }" /></td>
						<td style="vertical-align: middle;"><c:out value="${item.getCourse_description() }" /></td>
						<td style="vertical-align: middle;"><c:out value="${item.getCourse_date_creat() }" /></td>
						<td style="vertical-align: middle;">
							<c:forEach items="${statuses }" var="s_item">	
								<c:if test="${s_item.getStatus_id() == item.getCourse_status() }">
									<c:set value="${s_item.getStatus_id() }" var="temp" />
									<p class="${(temp == 0) ?  'w3-text-green' : 'w3-text-red' }">
										<c:out value="${s_item.getStatus_description() }" />
									</p>									
								</c:if>				
							</c:forEach>
						</td>
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
				<fmt:message key="course.btn_add_new" />
			</button>
			<a href="VTypeController?_action=main" class="w3-btn w3-teal"><fmt:message
					key="course.btn_back" /></a>
		</div>
	</div>
	
	<div id="add_modal" class="w3-modal">
		<div class="w3-modal-content w3-animate-top w3-card-4"
			style="width: 50%">
			<header class="w3-container w3-teal">
				<span
					onclick="document.getElementById('add_modal').style.display='none'"
					class="w3-button w3-display-topright">&times;</span>
				<h2 class="w3-center">
					<fmt:message key="course.header_add_new_course" />
				</h2>
			</header>
			<form id="add_form" class="w3-container">
				<input type="hidden" name="_action"> 
				<div class="w3-row w3-margin-top">
					<label class="w3-text-teal">
						<b><fmt:message	key="course.field_course_name" /></b>
					</label>
				</div>
				<div class="w3-row">
					<input name="course_name"
						class="w3-input w3-border w3-light-grey w3-rest"
						type="text" maxlength=20 >
				</div>
				<div class="w3-row w3-margin-top">
					<label class="w3-text-teal">
						<b><fmt:message	key="course.field_course_desc" /></b>
					</label>
				</div>
				<div class="w3-row">
					<input name="course_desc"
						class="w3-input w3-border w3-light-grey w3-rest"
						type="text" maxlength=50 >
				</div>
				<div class="w3-row w3-margin-top">
					<c:forEach items="${statuses }" var="item">	
						<div class="w3-col" style="width:150px">
							<input class="w3-radio" type="radio" name="course_status" value="${item.getStatus_id() }" checked>
							<label>${item.getStatus_description() }</label>
						</div>					
					</c:forEach>
				</div>
			</form>
			<footer class="w3-container w3-padding-16">
				<div class="w3-row" style="align-items: center;">
					<p class="w3-quarter"></p>
					<div class="w3-half  w3-center">
						<button onclick="document.getElementById('add_form').submit()"
							class="w3-button w3-light-grey" style="width: 40%">
							<fmt:message key="course.btn_add" />
						</button>
						<button
							onclick="document.getElementById('add_modal').style.display='none'"
							class="w3-button w3-light-grey" style="width: 40%">
							<fmt:message key="course.btn_cancel" />
						</button>
					</div>
					<p class="w3-quarter"></p>
				</div>
			</footer>
		</div>
	</div>
	
</body>
</html>