<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="language"
       value="${not empty param.language ? param.language : not empty language ? language : pageContext.request.locale}"
       scope="session"/>
<fmt:setLocale value="${language}"/>
<fmt:setBundle basename="localization"/>
<%@ page isELIgnored="false" %>
<div class="create-doctor-form ms-3 mt-3">
    <h2><fmt:message key="create.diagnosis"/></h2>
    <form method="post" action="controller?action=${param.action}">
        <div class="mb-3 w-25">
            <label for="diagnosisType" class="form-label"><fmt:message key="choose.diagnosis.type"/></label>
            <select class="form-select" id="diagnosisType" name="diagnosisType">
                <c:if test="${requestScope.currentDiagnosis != null}">
                    <option selected
                            value="${requestScope.selectedType.id()}">${requestScope.selectedType.value()}</option>
                </c:if>
                <c:forEach var="item" items="${requestScope.diagnosisOptions}">
                    <option value=${item.id()}>${item.value()}</option>
                </c:forEach>
            </select>
        </div>
        <div class="mb-3 w-25">
            <label for="text" class="form-label"><fmt:message key="choose.diagnosis"/></label>
            <textarea class="form-control" id="text" name="text" rows="5"><c:if
                    test="${requestScope.currentDiagnosis != null}">${requestScope.currentDiagnosis.text}</c:if></textarea>
        </div>
        <button type="submit" class="btn btn-primary"><fmt:message key="button.submit"/></button>
    </form>
</div>

