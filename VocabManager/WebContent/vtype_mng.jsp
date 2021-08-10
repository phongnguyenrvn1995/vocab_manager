<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ page errorPage="error_page.jsp"%>


<c:set var="list" value="${requestScope.list }" />
<c:set var="action_status" value="${requestScope.action_status }" />
<c:set var="is_successful" value="${requestScope.is_successful }" />
<c:set var="total_page" value="${requestScope.total_page }" />
<c:set var="page" value="${(empty requestScope.page) ? 1 : requestScope.page}" />

<c:if test="${sessionScope['lang'] == null }">
	<c:set var="lang" value="en" scope="session" />
</c:if>

<c:if test="${param.lang != null }">
	<c:set var="lang" value="${param.lang}" scope="session" />
</c:if>

<fmt:setLocale value="${sessionScope['lang']}" />
<fmt:setBundle basename="vtype_msg" />

<c:if test="${sessionScope['search'] == null }">
	<c:set var="search" scope="session" value="ex" />
</c:if>

<c:if test="${param.q != null }">
	<c:set var="search" scope="session" value="${param.q }" />
</c:if>

<!DOCTYPE html>
<html>
<head>
<meta http-equiv='Content-Type' content='text/html; charset=UTF-8' />
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet" href="https://www.w3schools.com/w3css/4/w3.css">
<title><fmt:message key="vtype.title_vocab_type_manager" /></title>
<script type="text/javascript">
	function openDelModal(id, desc) {
		document.getElementById('del_modal').style.display='block';
		document.getElementById('del_desc').innerHTML = desc;
		var del_form = document.getElementById('del_form');
		del_form['action'] = 'VTypeController';
		del_form['method'] = 'POST';
		del_form['_action'].value = 'delete';
		del_form['v_id'].value = id;
		del_form['page'].value = ${page};
	}

	function openUpdateModal(id, desc) {
		document.getElementById('update_modal').style.display='block';
		document.getElementById('update_desc').innerHTML = desc;

		var update_form = document.getElementById('update_form');
		update_form['action'] = 'VTypeController';
		update_form['method'] = 'POST';
		update_form['_action'].value = 'update';
		update_form['v_id'].value = id;
		update_form['page'].value = ${page};
	}

	function openAddModal() {
		document.getElementById('add_modal').style.display='block';

		var update_form = document.getElementById('add_form');
		update_form['action'] = 'VTypeController';
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
		window.location.href = 'VTypeController?&q=' + search_value;
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
			<fmt:message key="vtype.header_all_vocab_type" />
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
								value="${sessionScope['search'] }" 
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
		<div style="overflow-x:auto;">
			<table class="w3-table-all">
				<tr>
					<th><fmt:message key="vtype.field_type_id" /></th>
					<th><fmt:message key="vtype.field_description" /></th>
					<th style="width: 20%"><fmt:message key="vtype.field_action" /></th>
				</tr>
	
				<c:forEach items="${list }" var="item">
					<tr>
						<td style="vertical-align: middle;"><c:out
								value="${item.getVocab_type_id() }" /></td>
						<td style="vertical-align: middle;"><c:out
								value="${item.getVocab_type_name() }" /></td>
						<td class="w3-row">
							<button
								onclick="openUpdateModal(${item.getVocab_type_id()}, '${item.getVocab_type_name() }')"
								class="w3-button w3-half w3-light-green">Edit</button>
							<button
								onclick="openDelModal(${item.getVocab_type_id()}, '${item.getVocab_type_name() }')"
								class="w3-button w3-half w3-red">Delete</button>
						</td>
					</tr>
				</c:forEach>
			</table>
		</div>
		<div>
			<div class="w3-display-container" style="height: 50px;">
				<div class="w3-display-bottomright">
					<a href="VTypeController?page=1&q=${sessionScope['search']}" class="w3-button">«</a>
					<c:forEach var="idx" begin="${(page - 2) < 1 ? 1 : (page - 2)}"
						end="${(page + 2) > total_page ? total_page : (page + 2)}">
						<a href="VTypeController?page=${idx }&q=${sessionScope['search']}"
							class="w3-button ${(idx == page) ? 'w3-green' : ''} "> <c:out
								value="${idx }" />
						</a>
					</c:forEach>
					<a href="VTypeController?page=${total_page }&q=${sessionScope['search']}" class="w3-button">»</a>
				</div>
			</div>
		</div>
		<div class="w3-container w3-margin-top">
			<button onclick="openAddModal()" class="w3-btn w3-teal">
				<fmt:message key="vtype.btn_add_new" />
			</button>
			<a href="VTypeController?_action=main" class="w3-btn w3-teal"><fmt:message
					key="vtype.btn_back" /></a>
		</div>
	</div>


	<div id="del_modal" class="w3-modal">
		<div class="w3-modal-content w3-animate-top w3-card-4"
			style="width: 50%">
			<header class="w3-container w3-teal">
				<span
					onclick="document.getElementById('del_modal').style.display='none'"
					class="w3-button w3-display-topright">&times;</span>
				<h2 class="w3-center">
					<fmt:message key="vtype.header_delete" />
				</h2>
			</header>
			<div class="w3-container">
				<p class="w3-center">
					<fmt:message key="vtype.hint_do_you_want_to_delete" />
					[<span id="del_desc"></span>]?
				</p>
			</div>
			<div style="display: none;">
				<form id="del_form">
					<input name="_action"> <input name="v_id"> <input
						name="page">
				</form>
			</div>
			<footer class="w3-container w3-padding-16">
				<div class="w3-row" style="align-items: center;">
					<p class="w3-quarter"></p>
					<div class="w3-half  w3-center">
						<button onclick="document.getElementById('del_form').submit()"
							class="w3-button w3-light-grey" style="width: 40%">
							<fmt:message key="vtype.btn_yes" />
						</button>
						<button
							onclick="document.getElementById('del_modal').style.display='none'"
							class="w3-button w3-light-grey" style="width: 40%">
							<fmt:message key="vtype.btn_no" />
						</button>
					</div>
					<p class="w3-quarter"></p>
				</div>
			</footer>
		</div>
	</div>

	<div id="update_modal" class="w3-modal">
		<div class="w3-modal-content w3-animate-top w3-card-4"
			style="width: 50%">
			<header class="w3-container w3-teal">
				<span
					onclick="document.getElementById('update_modal').style.display='none'"
					class="w3-button w3-display-topright">&times;</span>
				<h2 class="w3-center">
					<fmt:message key="vtype.header_update" />
				</h2>
			</header>
			<form id="update_form" class="w3-container">
				<div class="w3-row">
					<div class="w3-margin-top  w3-margin-bottom  w3-rest w3-pale-blue">
						<h2 class="w3-center">
							[<span id="update_desc"></span>]
						</h2>
					</div>
				</div>
				<label class="w3-text-teal"><b><fmt:message
							key="vtype.field_new_description" /></b></label>
				<div class="w3-row">
					<input type="hidden" name="_action"> <input type="hidden"
						name="v_id"> <input type="hidden" name="page"> <input
						name="vocab_type_desc"
						class="w3-input w3-border w3-light-grey w3-rest w3-margin-top"
						type="text" maxlength=20>
				</div>
			</form>
			<footer class="w3-container w3-padding-16">
				<div class="w3-row" style="align-items: center;">
					<p class="w3-quarter"></p>
					<div class="w3-half  w3-center">
						<button onclick="document.getElementById('update_form').submit()"
							class="w3-button w3-light-grey" style="width: 40%">
							<fmt:message key="vtype.btn_update" />
						</button>
						<button
							onclick="document.getElementById('update_modal').style.display='none'"
							class="w3-button w3-light-grey" style="width: 40%">
							<fmt:message key="vtype.btn_cancel" />
						</button>
					</div>
					<p class="w3-quarter"></p>
				</div>
			</footer>
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
					<fmt:message key="vtype.header_add_new_type" />
				</h2>
			</header>
			<form id="add_form" class="w3-container">
				<div class="w3-row w3-margin-top">
					<label class="w3-text-teal"><b><fmt:message
								key="vtype.field_description" /></b></label>
				</div>
				<div class="w3-row">
					<input type="hidden" name="_action"> <input
						name="vocab_type_desc"
						class="w3-input w3-border w3-light-grey w3-rest w3-margin-top"
						type="text" maxlength=20>
				</div>
			</form>
			<footer class="w3-container w3-padding-16">
				<div class="w3-row" style="align-items: center;">
					<p class="w3-quarter"></p>
					<div class="w3-half  w3-center">
						<button onclick="document.getElementById('add_form').submit()"
							class="w3-button w3-light-grey" style="width: 40%">
							<fmt:message key="vtype.add" />
						</button>
						<button
							onclick="document.getElementById('add_modal').style.display='none'"
							class="w3-button w3-light-grey" style="width: 40%">
							<fmt:message key="vtype.btn_cancel" />
						</button>
					</div>
					<p class="w3-quarter"></p>
				</div>
			</footer>
		</div>
	</div>
</body>
</html>