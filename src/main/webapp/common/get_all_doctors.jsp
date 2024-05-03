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
<jsp:include page="removeParam.jsp"/>
<div>
    <div class="btn-group">
        <button type="button" class="btn btn-primary dropdown-toggle" data-bs-toggle="dropdown" aria-expanded="false">
            <fmt:message key="paged.sort.by"/>
        </button>
        <ul class="dropdown-menu">
            <li><a class="dropdown-item"
                   href="controller?action=doctors&sorting=asc&sortBy=name${requestScope.categoryId != null ? requestScope.categoryId : ''}">
                <fmt:message key="paged.sort.name.asc"/>
            </a></li>
            <li><a class="dropdown-item"
                   href="controller?action=doctors&sorting=desc&sortBy=name${requestScope.categoryId != null ? requestScope.categoryId : ''}">
                <fmt:message key="paged.sort.name.desc"/>
            </a></li>
            <li><a class="dropdown-item"
                   href="controller?action=doctors&sorting=asc&sortBy=num${requestScope.categoryId != null ? requestScope.categoryId : ''}">
                <fmt:message key="paged.doctors.sort.num.asc"/>
            </a></li>
            <li><a class="dropdown-item"
                   href="controller?action=doctors&sorting=desc&sortBy=num${requestScope.categoryId != null ? requestScope.categoryId : ''}">
                <fmt:message key="paged.doctors.sort.num.desc"/>
            </a></li>
        </ul>
    </div>
    <form class="w3-bar-item w3-button w3-hover-white">
        <label>
            <select class="form-select form-select-lg" id="categoryId" name="categoryId" onchange="{
                window.location.href=removeParam('categoryId', window.location.href) + '&categoryId=' + document.getElementById('categoryId').value
            }">
                <option value="0"
                        <c:if test="${requestScope.categoryId == null}">
                            selected
                        </c:if>
                ><fmt:message key="placeholder.all"/></option>
                <c:forEach items="${requestScope.categories}" var="item">
                    <option value="${item.id()}"
                            <c:if test="${requestScope.categoryId.id != null && requestScope.categoryId.id == item.id()}">
                                selected
                            </c:if>
                    ><fmt:message key="${item.value()}"/></option>
                </c:forEach>
            </select>
        </label>
    </form>
    <table class="table table-hover table-striped">
        <thead>
        <tr>
            <th scope="col">#</th>
            <th scope="col"><fmt:message key="name.last"/></th>
            <th scope="col"><fmt:message key="name.first"/></th>
            <th scope="col"><fmt:message key="phone"/></th>
            <th scope="col"><fmt:message key="email"/></th>
            <th scope="col"><fmt:message key="birthday"/></th>
            <th scope="col"><fmt:message key="doctors.num"/></th>
            <th scope="col"><fmt:message key="doctors.category"/></th>
        </tr>
        </thead>
        <tbody>
        <c:forEach begin="0" end="${requestScope.pagedDoctors.doctors().size()}" varStatus="loop"
                   var="item" items="${requestScope.pagedDoctors.doctors()}">
            <tr onclick="location.href='controller?action=doctor&doctorId=${item.id}'">
                <th scope="row">${loop.index + 1}</th>
                <tags:doctor_row doctorDto="${item}"/>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    <nav aria-label="Doctors search">
        <ul class="pagination">
            <c:forEach begin="1" end="${requestScope.pagedDoctors.numOfPages()}" varStatus="loop">
                <li class="page-item ${requestScope.pagedDoctors.pageNum() == loop.index ? "active" : ""}">
                    <a class="page-link"
                       href="controller?action=doctors&sorting=${requestScope.doctors_sorting}&sortBy=${requestScope.doctors_sort_by}&pageNum=${loop.index}">
                            ${loop.index}
                    </a>
                </li>
            </c:forEach>
        </ul>
    </nav>
</div>