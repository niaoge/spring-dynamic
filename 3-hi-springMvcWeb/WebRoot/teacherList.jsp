<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<script type="text/javascript" src="../js/jquery-1.7.1.js"></script>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<script type="text/javascript">
	function del(id){
		$.get("/springDynamicMvc/teacher/delTeacher?id=" + id,function(data){
			if("success" == data.result){
				alert("delete finished ");
				window.location.reload();
			}else{
				alert("delete error");
			}
		});
	}
</script>
</head>
<body>
	<h2><a href="/springDynamicMvc/teacher/toAddTeacher">to add teacher>>></a></h2>
	
	<table border="1">
		<tbody>
			<tr>
				<th>name</th>
				<th>page</th>
				<th>birthday</th>
				<th>option</th>
			</tr>
			<c:if test="${!empty teacherList }">
				<c:forEach items="${teacherList }" var="teacher">
					<tr>
						<td>${teacher.teacherName }</td>
						<td>${teacher.age }</td>
						<td>${teacher.birthday }</td>
						<td>
							<a href="/springDynamicMvc/teacher/getTeacher?id=${teacher.id }">edit</a>
							<a href="javascript:del('${teacher.id }')">del</a>
						</td>
					</tr>				
				</c:forEach>
			</c:if>
		</tbody>
	</table>
</body>
</html>