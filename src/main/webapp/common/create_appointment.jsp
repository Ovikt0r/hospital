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
                <c:if test="${param.role == 'ADMIN' || param.role == 'NURSE'}">
                    <jsp:include page="choose_doctor_button.jsp">
                        <jsp:param name="action" value="chooseDoctorForAppointmentCreationPage"/>
                        <jsp:param name="dataToSave" value="&patientId=${requestScope.patient.id}"/>
                    </jsp:include>
                </c:if>
                <c:if test="${param.role == 'DOCTOR'}">
                    <jsp:include page="doctor_field.jsp"/>
                </c:if>
            </div>
            <div class="mb-3 w-25">
                <label for="patientId" class="form-label"><fmt:message key="choose.patient"/></label>
                <select class="form-select" id="patientId" name="patientId">
                    <option value="${requestScope.patient.id}">${requestScope.patient.lastName} ${requestScope.patient.firstName}</option>
                </select>
            </div>
        </c:if>
        <c:if test="${param.createFor == 'DOCTOR'}">
            <div class="mb-3 w-25">
                <label for="doctorId" class="form-label"><fmt:message key="choose.doctor"/></label>
                <select class="form-select" id="doctorId" name="doctorId">
                    <option value="${requestScope.doctor.id}">${requestScope.doctor.lastName} ${requestScope.doctor.firstName}</option>
                </select>
            </div>
            <div class="mb-3 w-25">
                <label for="patientId" class="form-label"><fmt:message key="choose.patient"/></label>
                <jsp:include page="choose_patient_button.jsp">
                    <jsp:param name="action" value="choosePatientForAppointmentCreationPage"/>
                    <jsp:param name="dataToSave" value="&doctorId=${requestScope.doctor.id}"/>
                </jsp:include>
            </div>
        </c:if>

        <div class="mb-3 w-25">
            <label for="appointmentTypeId" class="form-label"><fmt:message key="choose.appointment.type"/></label>
            <select class="form-select" id="appointmentTypeId" name="appointmentTypeId">
                <c:forEach var="item" items="${requestScope.appointmentTypes}">
                    <option value=${item.id()}><fmt:message key="${item.value()}"/></option>
                </c:forEach>
            </select>
        </div>
        <div class="mb-3 w-25">
            <label for="appointmentDateTime" class="form-label"><fmt:message key="choose.appointment.datetime"/></label>
            <input type="datetime-local" class="form-control" id="appointmentDateTime" name="appointmentDateTime"
                   required>
        </div>
        <button type="submit" class="btn btn-primary"><fmt:message key="button.submit"/></button>
    </form>
</div>
