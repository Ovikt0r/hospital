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
    <iframe src="https://giphy.com/embed/ND6xkVPaj8tHO" width="480" height="271" frameBorder="0"
            class="giphy-embed"
            allowFullScreen></iframe>
    <h1>
        <span>403</span>
        <br/>
        Forbidden
    </h1>
    <p><fmt:message key="status.forbidden"/></p>
    <p><a href="controller?action=mainPage"><fmt:message key="status.main-page"/></a></p>
</div>
</body>
</html>
