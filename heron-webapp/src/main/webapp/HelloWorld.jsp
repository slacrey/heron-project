<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 13-10-28
  Time: 上午11:45
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title></title>
</head>
<body>
hello
<%
    Object ido = request.getAttribute("id");
    if (ido == null) {
        ido = request.getParameter("id");
    }
%>
<br/>
<%=ido.toString()%>
<br/>
${test}
</body>
</html>