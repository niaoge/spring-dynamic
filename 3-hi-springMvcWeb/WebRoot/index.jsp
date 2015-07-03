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
		$.get("/springDynamicMvc/user/delUser?id=" + id,function(data){
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
	<h2><a href="/springDynamicMvc/user/toAddUser">to add user>>></a></h2>
	
	<table border="1">
		<tbody>
			<tr>
				<th>name</th>
				<th>age</th>
				<th>birthday</th>
				<th>option</th>
			</tr>
			<c:if test="${!empty userList }">
				<c:forEach items="${userList }" var="user">
					<tr>
						<td>${user.userName }</td>
						<td>${user.age }</td>
						<td>${user.birthday }</td>
						<td>
							<a href="/springDynamicMvc/user/getUser?id=${user.id }">edit</a>
							<a href="javascript:del('${user.id }')">del</a>
						</td>
					</tr>				
				</c:forEach>
			</c:if>
		</tbody>
	</table>
</body>
</html>