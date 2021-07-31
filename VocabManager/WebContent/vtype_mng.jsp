<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page errorPage="error_page.jsp"%>
<!DOCTYPE html>
<html>
<head>
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet" href="https://www.w3schools.com/w3css/4/w3.css">
<meta charset="ISO-8859-1">
<title>Vocab Type Manager</title>
<script type="text/javascript">
	function openDel(id, desc) {
		document.getElementById('del_modal').style.display='block';
		document.getElementById('del_desc').innerHTML = desc;
		var del_form = document.getElementById('del_form');
		del_form['action'] = 'VTypeController';
		del_form['method'] = 'POST';
		del_form['_action'].value = 'delete';
		del_form['v_id'].value = id;
	}
</script>
</head>
<body>
	<c:set var="list" value="${requestScope.list }" />
	<c:set var="action_status" value="${requestScope.action_status }" />
	<c:set var="total_page" value="${requestScope.total_page }"/>
	<c:set var="page" value="${(empty requestScope.page) ? 1 : requestScope.page}"/>
	
	<c:if test="${action_status != null }">
		<div class="w3-panel w3-green w3-display-container">
			<span onclick="this.parentElement.style.display='none'"
				class="w3-button w3-large w3-display-topright">&times;</span>
			<h3>
				<c:out value="${action_status }" />
			</h3>
		</div>
	</c:if>
	<div class="w3-container" style="overflow-x: auto;">
		<h2>All Vocab Type</h2>

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
						<button class="w3-button w3-half w3-light-green">Edit</button>
						<button 
							onclick="openDel(${item.getVocab_type_id()}, '${item.getVocab_type_name() }')" 
							class="w3-button w3-half w3-red">Delete</button>
					</td>
				</tr>
			</c:forEach>
		</table>
		<div>
			<div class="w3-display-container" style="height: 50px;">
				<div class="w3-display-bottomright">
					<a href="VTypeController?page=1" class="w3-button">«</a>
					<c:forEach var="idx" begin="1" end="${total_page }">
						<a href="VTypeController?page=${idx }" 
								class="w3-button ${(idx == page) ? 'w3-green' : ''} ">
							<c:out value="${idx }"/>
						</a>
					</c:forEach>
					<a href="VTypeController?page=${total_page }" class="w3-button">»</a>
				</div>
			</div>
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

	<form class="w3-container" action="VTypeController" method="post">
		<div class="w3-row">
			<div class="w3-margin-top  w3-half w3-pale-blue">
				<h2>Add a new Type</h2>
			</div>
		</div>
		<label class="w3-text-teal"><b>First Name</b></label>
		<div class="w3-row">
			<input name="vocab_type_desc"
				class="w3-input w3-border w3-light-grey w3-half" type="text">
			<input name="_action" value="save" type="hidden">
		</div>
		<button class="w3-btn w3-blue-grey w3-margin-top">Commit</button>
	</form>
</body>
</html>