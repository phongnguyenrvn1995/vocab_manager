<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page errorPage="error_page.jsp" %>
<!DOCTYPE html>
<html>
<head>
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet" href="https://www.w3schools.com/w3css/4/w3.css">
<meta charset="ISO-8859-1">
<script type="text/javascript">
	function gotoCourseMng() {
		window.location.href = "CourseController";
	}
	
	function gotoLessonMng() {
		window.location.href = "LessonController";
	}
	
	function gotoVocabMng() {
		window.location.href = "VocabController";
	}
	
	function gotoRespMng() {
		window.location.href = "ResponseController";
	}
	
	function gotoStatusMng() {
		window.location.href = "StatusController";
	}
	
	function gotoVTypeMng() {
		window.location.href = "VTypeController";
	}
</script>
<title>Select student</title>
</head>
<body>
	<div class="w3-row">
		<div onclick="gotoRespMng();" class="w3-button w3-green w3-third">
			<h3>Response Manager</h3>
		</div>
		<div onclick="gotoStatusMng();" class="w3-button w3-blue w3-third">
			<h3>Status Manager</h3>
		</div>
		<div onclick="gotoVTypeMng();" class="w3-button w3-yellow w3-third">
			<h3>Vocab Type Manager</h3>
		</div>
	</div>
	<div class="w3-row">
		<div onclick="gotoCourseMng();" class="w3-button w3-light-green w3-third">
			<h3>Course Manager</h3>
		</div>
		<div onclick="gotoLessonMng();" class="w3-button w3-light-blue w3-third">
			<h3>Lesson Manager</h3>
		</div>
		<div onclick="gotoVocabMng();" class="w3-button w3-pale-yellow w3-third">
			<h3>Vocab Manager</h3>
		</div>
	</div>
</html>