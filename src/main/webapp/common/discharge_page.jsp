<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="language"
       value="${not empty param.language ? param.language : not empty language ? language : pageContext.request.locale}"
       scope="session"/>
<fmt:setLocale value="${language}"/>
<fmt:setBundle basename="localization"/>
<%@ page isELIgnored="false" %>
<div class="create-doctor-form ms-3 mt-3">
    <h2>Discharge the patient ${requestScope.patient.lastName} ${requestScope.patient.firstName}</h2>
    <form method="post" action="controller?action=discharge&patientId=${param.patientId}">
        <div class="mb-3 w-25">
            <label for="doctorId" class="form-label"><fmt:message key="choose.doctor"/></label>
            <select class="form-select" id="doctorId" name="doctorId">
                <c:forEach var="item" items="${requestScope.doctors}">
                    <option value=${item.id}>${item.medicineCategory} ${item.lastName} ${item.firstName}</option>
                </c:forEach>
            </select>
        </div>
        <div class="mb-3 w-25">
            <label for="text" class="form-label"><fmt:message key="choose.diagnosis"/></label>
            <textarea class="form-control" id="text" name="text" rows="5"></textarea>
        </div>
        <button type="submit" class="btn btn-primary"><fmt:message key="button.submit"/></button>
    </form>
</div>

