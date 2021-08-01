<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page errorPage="error_page.jsp"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv='Content-Type' content='text/html; charset=UTF-8' />
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet" href="https://www.w3schools.com/w3css/4/w3.css">
<title>Vocab Type Manager</title>
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
</script>
</head>
<body>
	<c:set var="list" value="${requestScope.list }" />
	<c:set var="action_status" value="${requestScope.action_status }" />
	<c:set var="is_successful" value="${requestScope.is_successful }" />
	<c:set var="total_page" value="${requestScope.total_page }"/>
	<c:set var="page" value="${(empty requestScope.page) ? 1 : requestScope.page}"/>
	
	<c:if test="${action_status != null }">
		<div class="w3-panel ${(is_successful) ? 'w3-green' : 'w3-red'}  w3-display-container">
			<span onclick="this.parentElement.style.display='none'"
				class="w3-button w3-large w3-display-topright">&times;</span>
			<h3>
				<c:out value="${action_status }" />
			</h3>
		</div>
	</c:if>
	<div class="w3-container" style="overflow-x: auto;">
		<h2>ALL VOCAB TYPE</h2>

		<table class="w3-table-all">
			<tr>
				<th>Type ID</th>
				<th>Description</th>
				<th style="width: 20%">Action</th>
			</tr>

			<c:forEach items="${list }" var="item">
				<tr>
					<td style="vertical-align: middle;"><c:out value="${item.getVocab_type_id() }" /></td>
					<td style="vertical-align: middle;"><c:out value="${item.getVocab_type_name() }" /></td>
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
		<div>
			<div class="w3-display-container" style="height: 50px;">
				<div class="w3-display-bottomright">
					<a href="VTypeController?page=1" class="w3-button">«</a>
					<c:forEach 	var="idx" 
								begin="${(page - 2) < 1 ? 1 : (page - 2)}"
								end="${(page + 2) > total_page ? total_page : (page + 2)}">
						<a href="VTypeController?page=${idx }" 
								class="w3-button ${(idx == page) ? 'w3-green' : ''} ">
							<c:out value="${idx }"/>
						</a>
					</c:forEach>
					<a href="VTypeController?page=${total_page }" class="w3-button">»</a>
				</div>
			</div>
		</div>
		<div class="w3-container w3-margin-top">
  			<button
  					onclick="openAddModal()" 
  					class="w3-btn w3-teal">ADD NEW</button>
  			<a href="VTypeController?_action=main" class="w3-btn w3-teal">BACK</a>
		</div>
	</div>


	<div id="del_modal" class="w3-modal">
		<div class="w3-modal-content w3-animate-top w3-card-4" style="width: 50%">
			<header class="w3-container w3-teal">
				<span onclick="document.getElementById('del_modal').style.display='none'"
					class="w3-button w3-display-topright">&times;</span>
				<h2 class="w3-center">Delete</h2>
			</header>
			<div class="w3-container">
				<p class="w3-center">Do you want to delete [<span id="del_desc"></span>]</p>
			</div>
			<div style="display: none;">
				<form id="del_form">
					<input name="_action">
					<input name="v_id">
					<input name="page">
				</form>
			</div>
			<footer class="w3-container w3-padding-16">
				<div class="w3-row" style="align-items: center;">
					<p class="w3-quarter"></p>
					<div class="w3-half  w3-center">
						<button onclick="document.getElementById('del_form').submit()" class="w3-button w3-light-grey" style="width: 40%">Yes</button>
						<button  onclick="document.getElementById('del_modal').style.display='none'" class="w3-button w3-light-grey" style="width: 40%">No</button>
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
				<h2 class="w3-center">Update</h2>
			</header>
			<form id="update_form" class="w3-container">
				<div class="w3-row">
					<div class="w3-margin-top  w3-margin-bottom  w3-rest w3-pale-blue">
						<h2 class="w3-center">
							[<span id="update_desc"></span>]
						</h2>
					</div>
				</div>
				<label class="w3-text-teal"><b>New Description</b></label>
				<div class="w3-row">
					<input type="hidden" name="_action"> 
					<input type="hidden" name="v_id"> 
					<input type="hidden" name="page">
					<input name="vocab_type_desc"
						class="w3-input w3-border w3-light-grey w3-rest w3-margin-top" type="text"
						maxlength=20>
				</div>
			</form>
			<footer class="w3-container w3-padding-16">
				<div class="w3-row" style="align-items: center;">
					<p class="w3-quarter"></p>
					<div class="w3-half  w3-center">
						<button
							onclick="document.getElementById('update_form').submit()"
							class="w3-button w3-light-grey"
							style="width: 40%">Update</button>
						<button
							onclick="document.getElementById('update_modal').style.display='none'"
							class="w3-button w3-light-grey" style="width: 40%">Cancel</button>
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
				<h2 class="w3-center">Add new Type</h2>
			</header>
			<form id="add_form" class="w3-container">
				<div class="w3-row w3-margin-top">
					<label class="w3-text-teal"><b>Vocab Type Description</b></label>
				</div>
				<div class="w3-row">
					<input type="hidden" name="_action"> 
					<input name="vocab_type_desc"
						class="w3-input w3-border w3-light-grey w3-rest w3-margin-top" type="text"
						maxlength=20>
				</div>
			</form>
			<footer class="w3-container w3-padding-16">
				<div class="w3-row" style="align-items: center;">
					<p class="w3-quarter"></p>
					<div class="w3-half  w3-center">
						<button
							onclick="document.getElementById('add_form').submit()"
							class="w3-button w3-light-grey"
							style="width: 40%">ADD</button>
						<button
							onclick="document.getElementById('add_modal').style.display='none'"
							class="w3-button w3-light-grey" style="width: 40%">Cancel</button>
					</div>
					<p class="w3-quarter"></p>
				</div>
			</footer>
		</div>
	</div>
</body>
</html>