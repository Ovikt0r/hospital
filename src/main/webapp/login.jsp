<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="language"
       value="${not empty param.language ? param.language : not empty language ? language : pageContext.request.locale}"
       scope="session"/>
<fmt:setLocale value="${language}"/>
<fmt:setBundle basename="localization"/>
<%@ page isELIgnored="false" %>

<style>
    body {
        font-family: "Lato", sans-serif;
    }


    .main-head {
        height: 150px;
        background: #FFF;

    }

    .sidenav {
        height: 100%;
        background-color: #30d5c8;
        overflow-x: hidden;
        padding-top: 20px;
    }


    .main {
        padding: 0px 10px;
    }

    @media screen and (max-height: 450px) {
        .sidenav {
            padding-top: 15px;
        }
    }

    @media screen and (max-width: 450px) {
        .login-form {
            margin-top: 10%;
        }

        .register-form {
            margin-top: 10%;
        }
    }

    @media screen and (min-width: 768px) {
        .main {
            margin-left: 40%;
        }

        .sidenav {
            width: 40%;
            position: fixed;
            z-index: 1;
            top: 0;
            left: 0;
        }

        .login-form {
            margin-top: 80%;
        }

        .register-form {
            margin-top: 20%;
        }
    }


    .login-main-text {
        margin-top: 20%;
        padding: 60px;
        color: #fff;
    }

    .login-main-text h2 {
        font-weight: 300;
    }

    .btn-black {
        background-color: #30d5c8 !important;
        color: #fff;
    }
</style>
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
    <link href="//maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css" rel="stylesheet" id="bootstrap-css">
    <script src="//maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js"></script>
    <script src="//code.jquery.com/jquery-1.11.1.min.js"></script>
</head>
<body>

<div class="sidenav">
    <div class="login-main-text">
        <h2>Hospital inc.<br> <fmt:message key="login.page"/></h2>
        <p><fmt:message key="login.text"/></p>
    </div>
</div>
<div class="main">
    <jsp:include page="common/language_switcher.jsp"/>
    <c:if test="${sessionScope.messageError != null}">
        <div class="alert alert-danger" role="alert">
            <fmt:message key="${sessionScope.messageError}"/>
        </div>
    </c:if>
    <div class="col-md-6 col-sm-12">
        <div class="login-form">
            <form action="controller?action=login" method="post">
                <div class="form-group">
                    <label for="login"><fmt:message key="email"/></label>
                    <input id="login" type="email" class="form-control" name="login" placeholder=<fmt:message key="placeholder.login"/>>
                </div>
                <div class="form-group">
                    <label for="password"><fmt:message key="password"/></label>
                    <input id="password" type="password" class="form-control" name="password" placeholder=<fmt:message key="placeholder.password"/>>
                </div>
                <button type="submit" class="btn btn-dark"><fmt:message key="button.login"/></button>
            </form>
        </div>
    </div>
</div>
</body>
</html>