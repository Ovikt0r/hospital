<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="language"
       value="${not empty param.language ? param.language : not empty language ? language : pageContext.request.locale}"
       scope="session"/>
<fmt:setLocale value="${language}"/>
<fmt:setBundle basename="localization"/>
<%@ page isELIgnored="false" %>
<div class="mb-3 w-25">
    <c:if test="${param.doctorId == null}">
        <button type="button" class="btn btn-outline-primary ms-1"
                onclick="location.href='controller?action=${param.action}${param.dataToSave}'">
            <fmt:message key="choose.doctor"/>
        </button>
    </c:if>
    <c:if test="${param.doctorId != null}">
        <jsp:include page="doctor_field.jsp"/>
        <button type="button" class="btn btn-outline-primary mt-2"
                onclick="location.href='controller?action=${param.action}${param.dataToSave}'">
            <fmt:message key="choose.doctor.another"/>
        </button>
    </c:if>
</div>
