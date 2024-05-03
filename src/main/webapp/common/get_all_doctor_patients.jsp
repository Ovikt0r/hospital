<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="language"
       value="${not empty param.language ? param.language : not empty language ? language : pageContext.request.locale}"
       scope="session"/>
<fmt:setLocale value="${language}"/>
<fmt:setBundle basename="localization"/>
<%@ page isELIgnored="false" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<div>
    <div class="btn-group">
        <button type="button" class="btn btn-primary dropdown-toggle" data-bs-toggle="dropdown" aria-expanded="false">
            <fmt:message key="paged.sort.by"/>
        </button>
        <ul class="dropdown-menu">
            <li><a class="dropdown-item"
                   href="controller?action=doctorPatients&doctorId=${param.doctorId}&sorting=asc&sortBy=name">
                <fmt:message key="paged.sort.name.asc"/>
            </a></li>
            <li><a class="dropdown-item"
                   href="controller?action=doctorPatients&doctorId=${param.doctorId}&sorting=desc&sortBy=name">
                <fmt:message key="paged.sort.name.desc"/>
            </a></li>
            <li><a class="dropdown-item"
                   href="controller?action=doctorPatients&doctorId=${param.doctorId}&sorting=asc&sortBy=birthday">
                <fmt:message key="paged.patients.sort.birthday.asc"/>
            </a></li>
            <li><a class="dropdown-item"
                   href="controller?action=doctorPatients&doctorId=${param.doctorId}&sorting=desc&sortBy=birthday">
                <fmt:message key="paged.patients.sort.birthday.desc"/>
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
            <th scope="col"><fmt:message key="patients.treated"/></th>
        </tr>
        </thead>
        <tbody>
        <c:forEach begin="0" end="${requestScope.pagedPatients.patients().size()}" varStatus="loop"
                   var="patient" items="${requestScope.pagedPatients.patients()}">
            <tr onclick="location.href='controller?action=patientCard&patientId=${patient.id}'">
                <th scope="row">${loop.index + 1}</th>
                <tags:patient_row patient="${patient}"/>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    <nav aria-label="Patients search">
        <ul class="pagination">
            <c:forEach begin="1" end="${requestScope.pagedPatients.numOfPages()}" varStatus="loop">
                <li class="page-item ${requestScope.pagedPatients.pageNum() == loop.index ? "active" : ""}">
                    <a class="page-link"
                       href="controller?action=doctorPatients&doctorId=${param.doctorId}&sorting=${requestScope.patientsSorting}&sortBy=${requestScope.patientsSortBy}&pageNum=${loop.index}">
                            ${loop.index}
                    </a>
                </li>
            </c:forEach>
        </ul>
    </nav>
</div>