<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="language"
       value="${not empty param.language ? param.language : not empty language ? language : pageContext.request.locale}"
       scope="session"/>
<fmt:setLocale value="${language}"/>
<fmt:setBundle basename="localization"/>
<%@ page isELIgnored="false" %>
<div class="create-doctor-form ms-3 mt-3">
    <h2><fmt:message key="create.appointment"/></h2>
    <form method="post" action="controller?action=${param.action}">
        <c:if test="${param.createFor == 'PATIENT'}">
            <div class="mb-3 w-25">
                <label class="form-label"><fmt:message key="choose.doctor"/></label>

                    <jsp:include page="choose_doctor_button.jsp">
                        <jsp:param name="action" value="chooseDoctorToAppointToPatientPage"/>
                        <jsp:param name="dataToSave" value="&patientId=${requestScope.patient.id}"/>
                    </jsp:include>
                </c:if>
            </div>
            <div class="mb-3 w-25">
                <label for="patientId" class="form-label"><fmt:message key="choose.patient"/></label>
                <select class="form-select" id="patientId" name="patientId">
                    <option value="${requestScope.patient.id}">${requestScope.patient.lastName} ${requestScope.patient.firstName}</option>
                </select>
            </div>
        <button type="submit" class="btn btn-primary"><fmt:message key="button.submit"/></button>
    </form>
</div>
