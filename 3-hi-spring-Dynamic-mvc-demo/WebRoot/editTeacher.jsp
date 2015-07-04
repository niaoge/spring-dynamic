<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>  
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<script type="text/javascript" src="../js/jquery-1.7.1.js"></script>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
	<h1>eidt teacher </h1>
	<form action="/springDynamicMvc/teacher/updateTeacher" name="teacherForm" method="post">
		<input type="hidden" name="id" value="${teacher.id }">
		name：<input type="text" name="teacherName" value="${teacher.teacherName }">
		<br/>
		age：<input type="text" name="age" value="${teacher.age }">
		<br/>
 		birthday：<input type="text" name="birthday" value="${teacher.birthday }">
		<br/>
		<input type="submit" value="save" >
	</form>
</body>
</html>