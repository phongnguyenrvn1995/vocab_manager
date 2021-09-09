<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ page errorPage="error_page.jsp"%>

<c:set var="list" value="${requestScope.list }" />
<c:set var="action_status" value="${requestScope.action_status }" />
<c:set var="is_successful" value="${requestScope.is_successful }" />
<c:set var="total_page" value="${requestScope.total_page }" />
<c:set var="page" value="${(empty requestScope.page) ? 1 : requestScope.page}" />
<c:set var="lessons" value="${requestScope.lessons }" />
<c:set var="vocab_types" value="${requestScope.vocab_types }" />


<c:if test="${sessionScope['lang'] == null }">
	<c:set var="lang" value="en" scope="session" />
</c:if>

<c:if test="${param.lang != null }">
	<c:set var="lang" value="${param.lang}" scope="session" />
</c:if>

<fmt:setLocale value="${sessionScope['lang']}" />
<fmt:setBundle basename="vocab_msg" />

<c:if test="${sessionScope['search_vocab'] == null }">
	<c:set var="search_vocab" scope="session" value="" />
</c:if>

<c:if test="${param.q != null }">
	<c:set var="search_vocab" scope="session" value="${param.q }" />
</c:if>

<c:choose>
	<c:when test="${param.lesson_id != null }">
		<c:set var="lesson_id" scope="session" value="${param.lesson_id }" />
	</c:when>
	<c:otherwise>
		<c:set var="lesson_id" scope="session" value="" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${param.vt_id != null}">
		<c:set var="vt_id" scope="session" value="${param.vt_id }" />
	</c:when>
	<c:otherwise>
		<c:set var="vt_id" scope="session" value="" />
	</c:otherwise>
</c:choose>

<!DOCTYPE html>
<html>
<head>
<meta http-equiv='Content-Type' content='text/html; charset=UTF-8' />
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet" href="https://www.w3schools.com/w3css/4/w3.css">
<title><fmt:message key="vocab.title_vocab_manager" /></title>
<script type="text/javascript">
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
		window.location.href = 'VocabController?&q=' + search_value +"&vt_id=${vt_id }&lesson_id=${lesson_id}";
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
			<fmt:message key="vocab.header_all_vocab" />
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
							href="?lang=vn&page=${page }&q=${search_vocab }&vt_id=${vt_id }&lesson_id=${lesson_id}">
							<img alt="vn" src="images/vn_img.jpg"								
								style="width: 70px; display: block;">
						</a> 
						<a class="w3-button"
							style="display: block;"
							href="?lang=en&page=${page }&q=${search_vocab }&vt_id=${vt_id }&lesson_id=${lesson_id}">
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
								value="${sessionScope['search_vocab'] }" 
								class="w3-border w3-input w3-hover-khaki" placeholder="Search..">
						</div>
						<div class="w3-col" style="width: 20%;">
							<a style="height: 40px; width: 100%"
								onclick="gotoSearch()"
							  	href="#" class="w3-button w3-green">Go</a>
						</div>
					</div>
				</div>
				<div class="w3-quarter w3-dropdown-hover" style="height: 100%">
					<button class="w3-button w3-light-blue" style="width: 100%; height: 100%">
						<fmt:message key="vocab.filter_vt" />
						<c:out value=": "/>
						<c:forEach items="${vocab_types }" var="vt_item">
							<c:if test="${vt_item.getVocab_type_id() == vt_id && !empty vt_id}">
								<b><c:out value="${vt_item.getVocab_type_name() }" /></b>
							</c:if>
						</c:forEach>
					</button>
					<div class="w3-dropdown-content w3-bar-block w3-card-4"
						style="max-height: 200px; overflow: scroll; width: inherit;">						
							<a href="?vt_id=&lesson_id=${lesson_id}"  
								class="w3-bar-item w3-button">
								<fmt:message key="vocab.filter_no_filter" />
							</a>
						<c:forEach items="${vocab_types }" var="vt_item">	
							<a href="?vt_id=${vt_item.getVocab_type_id() }&lesson_id=${lesson_id}" 
								class="w3-bar-item w3-button">${vt_item.getVocab_type_name() }</a>		
						</c:forEach>
					</div>
				</div>
				
				<div class="w3-quarter w3-dropdown-hover" style="height: 100%">
					<button class="w3-button w3-cyan"  style="width: 100%; height: 100%">
						<fmt:message key="vocab.filter_lesson" />
						<c:out value=": "/>						
						<c:forEach items="${lessons }" var="l_item">
							<c:if test="${l_item.getLesson_id() == lesson_id  && !empty lesson_id}">
								<b><c:out value="${l_item.getLesson_name() }"/></b>
							</c:if>
						</c:forEach>
					</button>
					<div class="w3-dropdown-content w3-bar-block w3-card-4"
						style="max-height: 200px; overflow: scroll; width: inherit;">
							<a href="?lesson_id=&vt_id=${vt_id }"  
								class="w3-bar-item w3-button">
								<fmt:message key="vocab.filter_no_filter" />
							</a>
						<c:forEach items="${lessons }" var="l_item">
							<a href="?lesson_id=${l_item.getLesson_id() }&vt_id=${vt_id }"  
								class="w3-bar-item w3-button">
								${l_item.getLesson_name() }
							</a>
						</c:forEach>
					</div>
				</div>
			</div>
		</div>
		
		
		<div style="overflow-x: auto;">
			<table class="w3-table-all">
				<tr>
					<th><fmt:message key="vocab.field_vocab_id" /></th>
					<th><fmt:message key="vocab.field_vocab_type" /></th>
					<th><fmt:message key="vocab.field_vocab_lesson" /></th>
					<th><fmt:message key="vocab.field_vocab_en" /></th>
					<th><fmt:message key="vocab.field_vocab_ipa" /></th>
					<th><fmt:message key="vocab.field_vocab_vi" /></th>
					<th><fmt:message key="vocab.field_vocab_description" /></th>
					<th><fmt:message key="vocab.field_vocab_sound_url" /></th>
					<th><fmt:message key="vocab.field_action" /></th>
				</tr>
				<c:forEach items="${list }" var="item">
					<tr>						
						<td style="vertical-align: middle;"><c:out value="${item.getVocab_id() }" /></td>
						<td style="vertical-align: middle;">
							<c:forEach items="${vocab_types }" var="vt_item">	
								<c:if test="${item.getVocab_type() == vt_item.getVocab_type_id() }">
									<c:out value="${vt_item.getVocab_type_name() }" />								
								</c:if>				
							</c:forEach>
						</td>
						<td style="vertical-align: middle;">							
							<c:forEach items="${lessons }" var="l_item">	
								<c:if test="${item.getVocab_lesson() == l_item.getLesson_id() }">
									<c:out value="${l_item.getLesson_name() }" />								
								</c:if>				
							</c:forEach>
						</td>
						<td style="vertical-align: middle;"><c:out value="${item.getVocab_en() }" /></td>
						<td style="vertical-align: middle;"><c:out value="${item.getVocab_ipa() }" /></td>
						<td style="vertical-align: middle;"><c:out value="${item.getVocab_vi() }" /></td>
						<td style="vertical-align: middle;"><c:out value="${item.getVocab_description() }" /></td>
						<td style="vertical-align: middle;"><c:out value="${item.getVocab_sound_url() }" /></td>
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
		
		<div>
			<div class="w3-display-container" style="height: 50px;">
				<div class="w3-display-bottomright">
					<a href="?page=1&q=${search_vocab }&vt_id=${vt_id }&lesson_id=${lesson_id}" class="w3-button">«</a>
					<c:forEach var="idx" begin="${(page - 2) < 1 ? 1 : (page - 2)}"
						end="${(page + 2) > total_page ? total_page : (page + 2)}">
						<a href="?page=${idx }&q=${search_vocab }&vt_id=${vt_id }&lesson_id=${lesson_id}"
							class="w3-button ${(idx == page) ? 'w3-green' : ''} "> <c:out
								value="${idx }" />
						</a>
					</c:forEach>
					<a href="?page=${total_page }&q=${search_vocab }&vt_id=${vt_id }&lesson_id=${lesson_id}" class="w3-button">»</a>
				</div>
			</div>
		</div>
		
		<div class="w3-container w3-margin-top">
			<button onclick="openAddModal()" class="w3-btn w3-teal">
				<fmt:message key="vocab.btn_add_new" />
			</button>
			<a href="?_action=main" class="w3-btn w3-teal"><fmt:message
					key="vocab.btn_back" /></a>
		</div>
	</div>
</body>
</html>