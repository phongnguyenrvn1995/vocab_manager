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

	function openUpdateModal(vocab_id, vocab_type, vocab_lesson, vocab_en, vocab_ipa, vocab_vi, vocab_description, vocab_sound_url) {
		document.getElementById('update_modal').style.display = 'block';

		var txt_search = document.getElementById('txt_search');
		var search_value = txt_search.value;
		var update_form = document.getElementById('update_form');
		update_form['action'] = 'VocabController';
		update_form['method'] = 'POST';
		update_form['page'].value = ${page };
		update_form['_action'].value = 'update';
		update_form['q'].value = search_value;
		update_form['vt_id'].value = '${vt_id }';
		update_form['lesson_id'].value = '${lesson_id }';

		update_form['vocab_id'].value          =  vocab_id;
		update_form['vocab_type'].value        =  vocab_type;
		update_form['vocab_lesson'].value      =  vocab_lesson;
		update_form['vocab_en'].value          =  vocab_en;
		update_form['vocab_ipa'].value         =  vocab_ipa;
		update_form['vocab_vi'].value          =  vocab_vi;
		update_form['vocab_description'].value =  vocab_description;
		update_form['vocab_sound_url'].value   =  vocab_sound_url;
		
	}
	
	function openDelModal(id, name) {
		document.getElementById('del_modal').style.display='block';
		document.getElementById('del_desc').innerHTML = name;
		var txt_search = document.getElementById('txt_search');
		var search_value = txt_search.value;
		var del_form = document.getElementById('del_form');

		del_form['action'] = 'VocabController';
		del_form['method'] = 'POST';
		del_form['page'].value = ${page };
		del_form['_action'].value = 'delete';
		del_form['q'].value = search_value;
		del_form['vt_id'].value = '${vt_id }';
		del_form['lesson_id'].value = '${lesson_id }';
        
		del_form['vocab_id'].value = id;
	}
	
	function openAddModal() {
		document.getElementById('add_modal').style.display = 'block';
	
		var update_form = document.getElementById('add_form');
		update_form['action'] = 'VocabController';
		update_form['method'] = 'POST';
		update_form['_action'].value = 'save';
		update_form['q'].value = '';
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
					<th style="width: 20%"><fmt:message key="vocab.field_vocab_description" /></th>
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
						<td class="w3-row" style="vertical-align: middle;">
							<button
								onclick="openUpdateModal('${item.getVocab_id() }', '${item.getVocab_type()}', '${item.getVocab_lesson()}',
														'${item.getVocab_en() }', '${item.getVocab_ipa() }', '${item.getVocab_vi() }', 
														'${item.getVocab_description() }', '${item.getVocab_sound_url() }')"
								class="w3-button w3-half w3-light-green">Edit</button>
							<button
								onclick="openDelModal('${item.getVocab_id() }', '${item.getVocab_en() }')"
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
	
	
	
	<div id="add_modal" class="w3-modal" style="padding-top: 10px;">
		<div class="w3-modal-content w3-animate-top w3-card-4"
			style="width: 50%">
			<header class="w3-container w3-teal">
				<span
					onclick="document.getElementById('add_modal').style.display='none'"
					class="w3-button w3-display-topright">&times;</span>
				<h2 class="w3-center">
					<fmt:message key="vocab.header_add_new_vocab" />
				</h2>
			</header>
			<form id="add_form" enctype = "multipart/form-data" class="w3-container">
				<input type="hidden" name="_action"> 
				<input type="hidden" name="q"> 
				<div class="w3-row w3-margin-top">
					<label class="w3-text-teal">
						<b><fmt:message	key="vocab.field_vocab_en" /></b>
					</label>
				</div>
				<div class="w3-row">
					<input name="vocab_en"
						class="w3-input w3-border w3-light-grey w3-rest"
						type="text" maxlength=20 >
				</div>
				
				<div class="w3-row w3-margin-top">
					<label class="w3-text-teal">
						<b><fmt:message	key="vocab.field_vocab_vi" /></b>
					</label>
				</div>
				<div class="w3-row">
					<input name="vocab_vi"
						class="w3-input w3-border w3-light-grey w3-rest"
						type="text" maxlength=20 >
				</div>
				
				<div class="w3-row w3-margin-top">
					<label class="w3-text-teal">
						<b><fmt:message	key="vocab.field_vocab_type" /></b>
					</label>
				</div>				
				<div class="w3-row">
					<select class="w3-select w3-border" name="vocab_type">
						<c:forEach items="${vocab_types }" var="vt_item">	
							<option value="${vt_item.getVocab_type_id() }">${vt_item.getVocab_type_name() }</option>
						</c:forEach>
					</select> 
				</div>
				
				<div class="w3-row w3-margin-top">
					<label class="w3-text-teal">
						<b><fmt:message	key="vocab.field_vocab_ipa" /></b>
					</label>
				</div>
				<div class="w3-row">
					<input name="vocab_ipa"
						class="w3-input w3-border w3-light-grey w3-rest"
						type="text" maxlength=20 >
				</div>
				
				<div class="w3-row w3-margin-top">
					<label class="w3-text-teal">
						<b><fmt:message	key="vocab.field_vocab_description" /></b>
					</label>
				</div>
				<div class="w3-row">
					<input name="vocab_description"
						class="w3-input w3-border w3-light-grey w3-rest"
						type="text" maxlength=200 >
				</div>
				
				<div class="w3-row w3-margin-top">
					<label class="w3-text-teal">
						<b><fmt:message	key="vocab.field_vocab_lesson" /></b>
					</label>
				</div>		
				<div class="w3-row">
					<select class="w3-select w3-border" name="vocab_lesson">		
						<c:forEach items="${lessons }" var="l_item">		
							<option value="${l_item.getLesson_id()  }">${l_item.getLesson_name() }</option>
						</c:forEach>
					</select> 
				</div>
				
				<div class="w3-row w3-margin-top">
					<label class="w3-text-teal">
						<b><fmt:message	key="vocab.field_vocab_sound_url" /></b>
					</label>
				</div>
				<div class="w3-row">
					<input name="vocab_sound_url"
						class="w3-input w3-border w3-light-grey w3-rest"
						type="file" maxlength=20 >
				</div>
			</form>
			<footer class="w3-container w3-padding-16">
				<div class="w3-row" style="align-items: center;">
					<p class="w3-quarter"></p>
					<div class="w3-half  w3-center">
						<button onclick="document.getElementById('add_form').submit()"
							class="w3-button w3-light-grey" style="width: 40%">
							<fmt:message key="vocab.btn_add" />
						</button>
						<button
							onclick="document.getElementById('add_modal').style.display='none'"
							class="w3-button w3-light-grey" style="width: 40%">
							<fmt:message key="vocab.btn_cancel" />
						</button>
					</div>
					<p class="w3-quarter"></p>
				</div>
			</footer>
		</div>
	</div>
	
	
	<div id="update_modal" class="w3-modal" style="padding-top: 10px;">
		<div class="w3-modal-content w3-animate-top w3-card-4"
			style="width: 50%">
			<header class="w3-container w3-teal">
				<span
					onclick="document.getElementById('update_modal').style.display='none'"
					class="w3-button w3-display-topright">&times;</span>
				<h2 class="w3-center">
					<fmt:message key="vocab.header_update_vocab" />
				</h2>
			</header>
			<form id="update_form" enctype = "multipart/form-data" class="w3-container">
			
				<input type="hidden" name="_action"> 
				<input type="hidden" name="page"> 
				<input type="hidden" name="q"> 
				<input type="hidden" name="vocab_id"> 
				<input type="hidden" name="vt_id"> 
				<input type="hidden" name="lesson_id"> 
				
				<div class="w3-row w3-margin-top">
					<label class="w3-text-teal">
						<b><fmt:message	key="vocab.field_vocab_en" /></b>
					</label>
				</div>
				<div class="w3-row">
					<input name="vocab_en"
						class="w3-input w3-border w3-light-grey w3-rest"
						type="text" maxlength=20 >
				</div>
				
				<div class="w3-row w3-margin-top">
					<label class="w3-text-teal">
						<b><fmt:message	key="vocab.field_vocab_vi" /></b>
					</label>
				</div>
				<div class="w3-row">
					<input name="vocab_vi"
						class="w3-input w3-border w3-light-grey w3-rest"
						type="text" maxlength=20 >
				</div>
				
				<div class="w3-row w3-margin-top">
					<label class="w3-text-teal">
						<b><fmt:message	key="vocab.field_vocab_type" /></b>
					</label>
				</div>				
				<div class="w3-row">
					<select class="w3-select w3-border" name="vocab_type">
						<c:forEach items="${vocab_types }" var="vt_item">	
							<option value="${vt_item.getVocab_type_id() }">${vt_item.getVocab_type_name() }</option>
						</c:forEach>
					</select> 
				</div>
				
				<div class="w3-row w3-margin-top">
					<label class="w3-text-teal">
						<b><fmt:message	key="vocab.field_vocab_ipa" /></b>
					</label>
				</div>
				<div class="w3-row">
					<input name="vocab_ipa"
						class="w3-input w3-border w3-light-grey w3-rest"
						type="text" maxlength=20 >
				</div>
				
				<div class="w3-row w3-margin-top">
					<label class="w3-text-teal">
						<b><fmt:message	key="vocab.field_vocab_description" /></b>
					</label>
				</div>
				<div class="w3-row">
					<input name="vocab_description"
						class="w3-input w3-border w3-light-grey w3-rest"
						type="text" maxlength=200 >
				</div>
				
				<div class="w3-row w3-margin-top">
					<label class="w3-text-teal">
						<b><fmt:message	key="vocab.field_vocab_lesson" /></b>
					</label>
				</div>		
				<div class="w3-row">
					<select class="w3-select w3-border" name="vocab_lesson">		
						<c:forEach items="${lessons }" var="l_item">		
							<option value="${l_item.getLesson_id()  }">${l_item.getLesson_name() }</option>
						</c:forEach>
					</select> 
				</div>
				
				<div class="w3-row w3-margin-top">
					<label class="w3-text-teal">
						<b><fmt:message	key="vocab.field_vocab_sound_url" /></b>
					</label>
				</div>
				<div class="w3-row">
					<input name="vocab_sound_url"
						class="w3-input w3-border w3-light-grey w3-rest"
						type="file" maxlength=20 >
				</div>
				
			</form>
			<footer class="w3-container w3-padding-16">
				<div class="w3-row" style="align-items: center;">
					<p class="w3-quarter"></p>
					<div class="w3-half  w3-center">
						<button onclick="document.getElementById('update_form').submit()"
							class="w3-button w3-light-grey" style="width: 40%">
							<fmt:message key="vocab.btn_update" />
						</button>
						<button
							onclick="document.getElementById('update_modal').style.display='none'"
							class="w3-button w3-light-grey" style="width: 40%">
							<fmt:message key="vocab.btn_cancel" />
						</button>
					</div>
					<p class="w3-quarter"></p>
				</div>
			</footer>
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
					<fmt:message key="vocab.header_delete" />
				</h2>
			</header>
			<div class="w3-container">
				<p class="w3-center">
					<fmt:message key="vocab.hint_do_you_want_to_delete" />
					[<span id="del_desc"></span>]?
				</p>
			</div>
			<div style="display: none;">
				<form id="del_form">
					<input type="hidden" name="_action"> 
					<input type="hidden" name="page"> 
					<input type="hidden" name="q"> 
					<input type="hidden" name="vocab_id"> 
					<input type="hidden" name="vt_id"> 
					<input type="hidden" name="lesson_id"> 
				</form>
			</div>
			<footer class="w3-container w3-padding-16">
				<div class="w3-row" style="align-items: center;">
					<p class="w3-quarter"></p>
					<div class="w3-half  w3-center">
						<button onclick="document.getElementById('del_form').submit()"
							class="w3-button w3-light-grey" style="width: 40%">
							<fmt:message key="vocab.btn_yes" />
						</button>
						<button
							onclick="document.getElementById('del_modal').style.display='none'"
							class="w3-button w3-light-grey" style="width: 40%">
							<fmt:message key="vocab.btn_no" />
						</button>
					</div>
					<p class="w3-quarter"></p>
				</div>
			</footer>
		</div>
	</div>
</body>
</html>