<%@ page import="java.time.LocalDateTime" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="language"
       value="${not empty param.language ? param.language : not empty language ? language : pageContext.request.locale}"
       scope="session"/>
<fmt:setLocale value="${language}"/>
<fmt:setBundle basename="localization"/>
<%@ page isELIgnored="false" %>
<section>
    <div class="container py-5">
        <div class="row">
            <div class="col-lg-8">
                <div class="card mb-4">
                    <div class="card-body">
                        <div class="row">
                            <div class="col-sm-3">
                                <p class="mb-0"><fmt:message key="card.appointment.name.patient"/></p>
                            </div>
                            <div class="col-sm-9">
                                <p class="text-muted mb-0">${requestScope.appointment.patientLastName} ${requestScope.appointment.patientFirstName}</p>
                            </div>
                        </div>
                        <hr>
                        <div class="row">
                            <div class="col-sm-3">
                                <p class="mb-0"><fmt:message key="card.appointment.name.doctor"/></p>
                            </div>
                            <div class="col-sm-9">
                                <p class="text-muted mb-0">${requestScope.appointment.doctorLastName} ${requestScope.appointment.doctorFirstName}</p>
                            </div>
                        </div>
                        <hr>
                        <div class="row">
                            <div class="col-sm-3">
                                <p class="mb-0"><fmt:message key="card.appointment.type"/></p>
                            </div>
                            <div class="col-sm-9">
                                <p class="text-muted mb-0">${requestScope.appointment.appointmentType}</p>
                            </div>
                        </div>
                        <hr>
                        <div class="row">
                            <div class="col-sm-3">
                                <p class="mb-0"><fmt:message key="card.appointment.datetime"/></p>
                            </div>
                            <div class="col-sm-9">
                                <p class="text-muted mb-0">${requestScope.appointment.dateAndTime}</p>
                            </div>
                        </div>
                        <hr>
                        <div class="row">
                            <div class="col-sm-3">
                                <p class="mb-0"><fmt:message key="card.appointment.canceled"/></p>
                            </div>
                            <div class="col-sm-9">
                                <p class="text-muted mb-0">${requestScope.appointment.canceled}</p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-lg-4">
                <div class="card mb-4">
                    <div class="card-body text-center">
                        <img src="http://s3.amazonaws.com/pix.iemoji.com/images/emoji/apple/ios-12/256/man-health-worker-light-skin-tone.png"
                             alt="Avatar"
                             class="rounded-circle img-fluid" style="width: 150px;">
                        <img src="https://cdn.shopify.com/s/files/1/1061/1924/products/Sick_Emoji_Icon_2_1024x1024.png?v=1571606114"
                             alt="Avatar"
                             class="rounded-circle img-fluid" style="width: 150px;">
                        <c:if test="${param.role != 'PATIENT'}">
                            <div class="d-flex justify-content-center mb-2">
                                <button type="button" class="btn btn-primary"
                                        <c:if test="${param.role == 'ADMIN' || param.role == 'DOCTOR'}">
                                            <c:if test="${requestScope.appointment.canceled() ||
                                    requestScope.appointment.appointmentDateTime().isBefore(LocalDateTime.now())}">
                                                disabled
                                            </c:if>
                                            onclick="location.href='controller?action=updateAppointmentPage&appointmentId=${requestScope.appointment.id}'"
                                        </c:if>
                                        <c:if test="${param.role == 'NURSE' || param.role == 'PATIENT'}">
                                            disabled
                                        </c:if>
                                >
                                    <fmt:message key="card.appointment.update"/>
                                </button>
                                <button type="button" class="btn btn-danger ms-1"
                                        <c:if test="${param.role == 'ADMIN' || param.role == 'DOCTOR'}">
                                            <c:if test="${requestScope.appointment.canceled() ||
                                    requestScope.appointment.appointmentDateTime().isBefore(LocalDateTime.now())}">
                                                disabled
                                            </c:if>
                                            onclick="location.href='controller?action=cancelAppointment&appointmentId=${requestScope.appointment.id}'
                                            return confirm('Are you sure you want to continue')"
                                        </c:if>
                                        <c:if test="${param.role == 'NURSE' || param.role == 'PATIENT'}">
                                            disabled
                                        </c:if>
                                    >
                                    <fmt:message key="card.appointment.cancel"/>
                                </button>
                            </div>
                            <div class="d-flex justify-content-center mb-2">
                                <c:if test="${param.role == 'ADMIN' || param.role == 'DOCTOR'}">
                                    <c:if test="${requestScope.appointment.diagnosis == null}">
                                        <button type="button" class="btn btn-outline-primary"
                                                onclick="location.href='controller?action=diagnosisPage&appointmentId=${requestScope.appointment.id}'">
                                            <fmt:message key="card.appointment.diagnosis.set"/>
                                        </button>
                                    </c:if>
                                    <c:if test="${requestScope.appointment.diagnosis != null}">
                                        <button type="button" class="btn btn-outline-primary"
                                                onclick="location.href='controller?action=updateDiagnosisPage&diagnosisId=${requestScope.appointment.diagnosis.id()}'">
                                            <fmt:message key="card.appointment.diagnosis.update"/>
                                        </button>
                                    </c:if>
                                </c:if>
                            </div>
                        </c:if>
                    </div>
                </div>
            </div>
            <c:if test="${requestScope.appointment.diagnosis != null}">
                <div class="col-lg-8">
                    <div class="card mb-4">
                        <div class="card-body">
                            <div class="row">
                                <div class="col-sm-3">
                                    <p class="mb-0"><fmt:message key="card.appointment.diagnosis.type"/></p>
                                </div>
                                <div class="col-sm-9">
                                    <p class="text-muted mb-0">${requestScope.appointment.diagnosis.diagnosisType()}</p>
                                </div>
                            </div>
                            <hr>
                            <div class="row">
                                <div class="col-sm-3">
                                    <p class="mb-0"><fmt:message key="card.appointment.diagnosis.text"/></p>
                                </div>
                                <div class="col-sm-9">
                                    <p class="text-muted mb-0">${requestScope.appointment.diagnosis.text()}</p>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </c:if>
        </div>
    </div>
</section>