<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet" href="https://www.w3schools.com/w3css/4/w3.css">
<meta charset="ISO-8859-1">
<title>Status Define</title>
</head>
<body>
	<c:set var="list" value="${requestScope.list }" />
	<div class="w3-container">
		<h2>All Status Defines</h2>

		<table class="w3-table-all">
			<tr>
				<th>Status ID</th>
				<th>Status Description</th>
			</tr>

			<c:forEach items="${list }" var="item">
				<tr>
					<td><c:out value="${item.getResponse_id() }" /></td>
					<td><c:out value="${item.getResponse_description() }" /></td>
				</tr>
			</c:forEach>
		</table>
	</div>

</body>
</html>