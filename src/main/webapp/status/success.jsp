<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="language"
       value="${not empty param.language ? param.language : not empty language ? language : pageContext.request.locale}"
       scope="session"/>
<fmt:setLocale value="${language}"/>
<fmt:setBundle basename="localization"/>
<%@ page isELIgnored="false" %>
<jsp:include page="status_page_style.jsp"/>
<html>
<head>
    <title>Hospital</title>
</head>
<body>
<div class="container">
    <iframe src="https://giphy.com/embed/jTnGaiuxvvDNK" width="366" height="480" frameBorder="0" class="giphy-embed"
            allowFullScreen></iframe>
    <h1>
        <span>200</span>
        <br/>
        Success
    </h1>
    <p><fmt:message key="status.success"/></p>
    <p><a href="controller?action=mainPage"><fmt:message key="status.main-page"/></a></p>
</div>
</body>
</html>
