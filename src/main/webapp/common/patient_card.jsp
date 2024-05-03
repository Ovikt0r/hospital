<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="language"
       value="${not empty param.language ? param.language : not empty language ? language : pageContext.request.locale}"
       scope="session"/>
<fmt:setLocale value="${language}"/>
<fmt:setBundle basename="localization"/>
<%@ page isELIgnored="false" %>
<section>
    <div class="container py-5">
        <div class="row">
            <div class="col-lg-4">
                <div class="card mb-4">
                    <div class="card-body text-center">
                        <img src="https://cdn.shopify.com/s/files/1/1061/1924/products/Sick_Emoji_Icon_2_1024x1024.png?v=1571606114"
                             alt="Avatar"
                             class="rounded-circle img-fluid" style="width: 150px;">
                        <h5 class="my-3">${requestScope.foundPatient.lastName} ${requestScope.foundPatient.firstName}</h5>
                        <div class="d-flex justify-content-center mb-2">

                            <c:if test="${param.role == 'PATIENT'}">
                                <button type="button" class="btn btn-outline-primary"
                                        onclick="location.href='controller?action=patientAppointments&patientId=${requestScope.foundPatient.id}'"
                                ><fmt:message key="card.show.hospital.card"/>
                                </button>
                            </c:if>
                            <c:if test="${param.role == 'ADMIN' || param.role == 'NURSE' || param.role == 'DOCTOR'}">
                            <button type="button" class="btn btn-outline-primary"
                                    onclick="location.href='controller?action=patientAppointments&patientId=${requestScope.foundPatient.id}'" >
                                <fmt:message key="card.appointment.all"/>
                            </button>
                            </c:if>
                            <c:if test="${param.role != 'PATIENT'}">
                                <button type="button" class="btn btn-outline-primary ms-1"
                                        <c:if test="${param.role == 'ADMIN' || param.role == 'NURSE'}">
                                            onclick="location.href='controller?action=makeAppointmentForPatientPage&patientId=${requestScope.foundPatient.id}'"
                                        </c:if>
                                        <c:if test="${param.role == 'DOCTOR'}">
                                            onclick="location.href='controller?action=makeAppointmentForPatientPage&patientId=${requestScope.foundPatient.id}&doctorId=${requestScope.doctorId}'"
                                        </c:if>
                                ><fmt:message key="card.appointment.make"/>
                                </button>
                            </c:if>
                        </div>
                        <c:if test="${param.role != 'PATIENT'}">
                            <div class="d-flex justify-content-center mb-2">
                                <button type="button" class="btn btn-outline-primary ms-1"
                                        <c:choose>
                                            <c:when test="${!requestScope.mayBeDischarged || param.role == 'NURSE'}">
                                                disabled
                                            </c:when>
                                            <c:otherwise>
                                                onclick="location.href='controller?action=dischargePage&patientId=${requestScope.foundPatient.id}'"
                                            </c:otherwise>
                                        </c:choose>
                                ><fmt:message key="card.patients.discharge"/>
                                </button>

                                <button type="button" class="btn btn-outline-primary ms-1"
                                        <c:choose>
                                            <c:when test="${requestScope.mayBeDischarged}">
                                                disabled
                                            </c:when>
                                            <c:otherwise>
                                                onclick="location.href='controller?action=printPdfDischargeForm&patientId=${requestScope.foundPatient.id}&appointmentId=${requestScope.lastAppointmentId}'"
                                            </c:otherwise>
                                        </c:choose>
                                ><fmt:message key="card.patients.get.discharge.form"/>
                                </button>
                            </div>
                        </c:if>
                    </div>
                </div>
            </div>

            <div class="col-lg-8">
                <div class="card mb-4">
                    <div class="card-body">
                        <div class="row">
                            <div class="col-sm-3">
                                <p class="mb-0"><fmt:message key="name.full"/></p>
                            </div>
                            <div class="col-sm-9">
                                <p class="text-muted mb-0">${requestScope.foundPatient.lastName} ${requestScope.foundPatient.firstName}</p>
                            </div>
                        </div>
                        <hr>
                        <div class="row">
                            <div class="col-sm-3">
                                <p class="mb-0"><fmt:message key="email"/></p>
                            </div>
                            <div class="col-sm-9">
                                <p class="text-muted mb-0">${requestScope.foundPatient.email}</p>
                            </div>
                        </div>
                        <hr>
                        <div class="row">
                            <div class="col-sm-3">
                                <p class="mb-0"><fmt:message key="phone"/></p>
                            </div>
                            <div class="col-sm-9">
                                <p class="text-muted mb-0">${requestScope.foundPatient.phone}</p>
                            </div>
                        </div>
                        <hr>
                        <div class="row">
                            <div class="col-sm-3">
                                <p class="mb-0"><fmt:message key="birthday"/></p>
                            </div>
                            <div class="col-sm-9">
                                <p class="text-muted mb-0">${requestScope.foundPatient.birthDate}</p>
                            </div>
                        </div>
                        <hr>
                        <div class="row">
                            <div class="col-sm-3">
                                <p class="mb-0"><fmt:message key="patients.treated"/></p>
                            </div>
                            <div class="col-sm-9">
                                <p class="text-muted mb-0">${requestScope.foundPatient.isTreated}</p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</section>
