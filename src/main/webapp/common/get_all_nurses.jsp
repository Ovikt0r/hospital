<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="language"
       value="${not empty param.language ? param.language : not empty language ? language : pageContext.request.locale}"
       scope="session"/>
<fmt:setLocale value="${language}"/>
<fmt:setBundle basename="localization"/>
<%@ page isELIgnored="false" %>


<div>
    <div class="btn-group">
        <button type="button" class="btn btn-primary dropdown-toggle" data-bs-toggle="dropdown" aria-expanded="false">
            <fmt:message key="paged.sort.by"/>
        </button>
        <ul class="dropdown-menu">
            <li><a class="dropdown-item" href="controller?action=nurses&sorting=asc">
                <fmt:message key="paged.sort.name.asc"/>
            </a></li>
            <li><a class="dropdown-item" href="controller?action=nurses&sorting=desc">
                <fmt:message key="paged.sort.name.desc"/>
            </a></li>
        </ul>
    </div>
    <table class="table table-hover table-striped">
        <thead>
        <tr>
            <th scope="col">#</th>
            <th scope="col"><fmt:message key="name.last"/></th>
            <th scope="col"><fmt:message key="name.first"/></th>
            <th scope="col"><fmt:message key="phone"/></th>
            <th scope="col"><fmt:message key="email"/></th>
            <th scope="col"><fmt:message key="birthday"/></th>
        </tr>
        </thead>
        <tbody>
        <c:forEach begin="0" end="${requestScope.pagedNurses.nurses().size()}" varStatus="loop"
                   var="item" items="${requestScope.pagedNurses.nurses()}">
            <tr>
                <th scope="row">${loop.index + 1}</th>
                <td>${item.lastName}</td>
                <td>${item.firstName}</td>
                <td>${item.phone}</td>
                <td>${item.email}</td>
                <td>${item.birthDate}</td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    <nav aria-label="Patients search">
        <ul class="pagination">
            <c:forEach begin="1" end="${requestScope.pagedNurses.numOfPages()}" varStatus="loop">
                <li class="page-item ${requestScope.pagedNurses.pageNum() == loop.index ? "active" : ""}">
                    <a class="page-link" href="controller?action=nurses&sorting=&pageNum=${loop.index}">
                            ${loop.index}
                    </a>
                </li>
            </c:forEach>
        </ul>
    </nav>
</div>