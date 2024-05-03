<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="language"
       value="${not empty param.language ? param.language : not empty language ? language : pageContext.request.locale}"
       scope="session"/>
<fmt:setLocale value="${language}"/>
<fmt:setBundle basename="localization"/>
<%@ page isELIgnored="false" %>
<html>
<head>
    <title>Hospital</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css" rel="stylesheet"
          integrity="sha384-rbsA2VBKQhggwzxH7pPCaAqO46MgnOM80zW1RWuH61DGLwZJEdK2Kadq2F9CUG65" crossorigin="anonymous">
    <link href="https://fonts.googleapis.com/css?family=Roboto:300,400,500,700&display=swap"
          rel="stylesheet"/>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/js/bootstrap.bundle.min.js"
            integrity="sha384-kenU1KFdBIe4zVF0s0G1M5b4hcpxyD9F7jL+jjXkk+Q2h455rYXK/7HAuoJl+0I4"
            crossorigin="anonymous"></script>
</head>
<body>
<jsp:include page="admin_navbar.jsp"/>
<h1><fmt:message key="hello"/> ${sessionScope.currentUserLastName} ${sessionScope.currentUserFirstName}!</h1>
<section>
    <div class="container py-5">
        <div class="row">
            <div class="col-lg-4">
                <div class="card mb-4">
                    <div class="card-body text-center">
                        <img src="http://s3.amazonaws.com/pix.iemoji.com/images/emoji/apple/ios-12/256/man-technologist.png"
                             alt="Avatar"
                             class="rounded-circle img-fluid" style="width: 150px;">
                        <h5 class="my-3">${requestScope.foundAdmin.lastName} ${requestScope.foundAdmin.firstName}</h5>
                    </div>
                </div>
            </div>
            <div class="col-lg-8">
                <div class="card mb-4">
                    <div class="card-body">
                        <div class="row">
                            <div class="col-sm-3">
                                <p class="mb-0"><fmt:message key="name.full"/></p>
                            </div>
                            <div class="col-sm-9">
                                <p class="text-muted mb-0">${requestScope.foundAdmin.lastName} ${requestScope.foundAdmin.firstName}</p>
                            </div>
                        </div>
                        <hr>
                        <div class="row">
                            <div class="col-sm-3">
                                <p class="mb-0"><fmt:message key="email"/></p>
                            </div>
                            <div class="col-sm-9">
                                <p class="text-muted mb-0">${requestScope.foundAdmin.email}</p>
                            </div>
                        </div>
                        <hr>
                        <div class="row">
                            <div class="col-sm-3">
                                <p class="mb-0"><fmt:message key="phone"/></p>
                            </div>
                            <div class="col-sm-9">
                                <p class="text-muted mb-0">${requestScope.foundAdmin.phone}</p>
                            </div>
                        </div>
                        <hr>
                        <div class="row">
                            <div class="col-sm-3">
                                <p class="mb-0"><fmt:message key="birthday"/></p>
                            </div>
                            <div class="col-sm-9">
                                <p class="text-muted mb-0">${requestScope.foundAdmin.birthDate}</p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</section>
</body>
</html>
