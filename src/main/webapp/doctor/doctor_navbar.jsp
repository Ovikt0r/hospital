<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="my" uri="http://oviktor.com/tags/currentTime" %>
<c:set var="language"
       value="${not empty param.language ? param.language : not empty language ? language : pageContext.request.locale}"
       scope="session"/>
<fmt:setLocale value="${language}"/>
<fmt:setBundle basename="localization"/>
<%@ page isELIgnored="false" %>
<nav class="navbar navbar-dark bg-dark">
    <div class="container-fluid">
            <a class="navbar-brand" href="controller?action=mainPage"> Hospital inc.</a>
                <a class="brand-text" style="text-align: center; color: white">
                    <my:currentTime format="dd-MM-yyyy"/>
                </a>
            <button class="navbar-toggler" type="button" data-bs-toggle="offcanvas" data-bs-target="#offcanvasDarkNavbar" aria-controls="offcanvasDarkNavbar">
                <span class="navbar-toggler-icon"></span>
            </button>
        <div class="offcanvas offcanvas-end text-bg-dark" tabindex="-1" id="offcanvasDarkNavbar" aria-labelledby="offcanvasDarkNavbarLabel">
            <div class="offcanvas-header">
                <h5 class="offcanvas-title" id="offcanvasDarkNavbarLabel"><fmt:message key="hello"/> ${sessionScope.currentUserLastName} ${sessionScope.currentUserFirstName}!</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="offcanvas" aria-label="Close"></button>
            </div>
            <div class="offcanvas-body">
                <ul class="navbar-nav justify-content-end flex-grow-1 pe-3">
                    <li class="nav-item dropdown">
                        <a class="nav-link dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                            &#x1F469;&#x200D;&#x2695;&#xFE0F; <fmt:message key="navbar.nurses"/>
                        </a>
                        <ul class="dropdown-menu dropdown-menu-dark">
                            <li><a class="dropdown-item" href="controller?action=nurses"><fmt:message key="navbar.get"/></a></li>
                        </ul>
                    </li>
                    <li class="nav-item dropdown">
                        <a class="nav-link dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                            &#129298; <fmt:message key="navbar.patients"/>
                        </a>
                        <ul class="dropdown-menu dropdown-menu-dark">
                            <li><a class="dropdown-item" href="controller?action=patients"><fmt:message key="navbar.get"/></a></li>
                            <li><a class="dropdown-item" href="controller?action=createPatientPage"><fmt:message key="navbar.create"/></a></li>
                        </ul>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link active" aria-current="page" href="controller?action=logout"><fmt:message key="navbar.logout"/></a>
                    </li>
                </ul>
                <jsp:include page="../common/language_switcher.jsp"/>
            </div>
        </div>
    </div>
</nav>