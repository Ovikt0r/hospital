<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="language"
       value="${not empty param.language ? param.language : not empty language ? language : pageContext.request.locale}"
       scope="session"/>
<fmt:setLocale value="${language}"/>
<fmt:setBundle basename="localization"/>
<%@ page isELIgnored="false" %>
<div>
    <h5 class="my-3"><fmt:message key="card.hospital.card"/></h5>
    <div class="btn-group">
        <button type="button" class="btn btn-primary dropdown-toggle" data-bs-toggle="dropdown" aria-expanded="false">
            <fmt:message key="paged.sort.by"/>
        </button>
        <ul class="dropdown-menu">
            <li><a class="dropdown-item"
                   href="controller?action=${param.action}&sorting=asc&sortBy=date${requestScope.dataToSave}">
                <fmt:message key="paged.patients.sort.date.asc"/>
            </a></li>
            <li><a class="dropdown-item"
                   href="controller?action=${param.action}&sorting=desc&sortBy=date${requestScope.dataToSave}">
                <fmt:message key="paged.patients.sort.date.desc"/>
            </a></li>
        </ul>
    </div>
    <table class="table table-hover table-striped">
        <thead>
        <tr>
            <th scope="col">#</th>
            <th scope="col"><fmt:message key="patient"/></th>
            <th scope="col"><fmt:message key="doctor"/></th>
            <th scope="col"><fmt:message key="appointment.type"/></th>
            <th scope="col"><fmt:message key="diagnosis.type"/></th>
            <th scope="col"><fmt:message key="datetime"/></th>
        </tr>
        </thead>
        <tbody>
        <c:forEach begin="0" end="${requestScope.pagedAppointments.appointments().size()}" varStatus="loop"
                   var="item" items="${requestScope.pagedAppointments.appointments()}">
            <tr
                    onclick="location.href='controller?action=appointmentCard&appointmentId=${item.id}'" ${item.canceled ? 'style="background-color:#FF0000"' : ''}
            >
                <th scope="row">${loop.index + 1}</th>
                <td>
                    <a
                            <c:if test="${param.role != 'PATIENT'}">
                                href="controller?action=patientCard&patientId=${item.patientId}"
                            </c:if>
                    >
                            ${item.patientLastName} ${item.patientFirstName}
                    </a>
                </td>
                <td>
                    <a
                            <c:if test="${param.role != 'PATIENT'}">
                                href="controller?action=doctor&doctorId=${item.doctorId}"
                            </c:if>
                    >
                            ${item.doctorLastName} ${item.doctorFirstName}
                    </a>
                </td>
                <c:choose>
                    <c:when test="${item.appointmentType == 'CONSULTATION'}">
                        <td>
                            <fmt:message key="appointment-type.consultation"/>
                        </td>
                    </c:when>
                    <c:when test="${item.appointmentType == 'OPERATION'}">
                        <td>
                            <fmt:message key="appointment-type.operation"/>
                        </td>
                    </c:when>
                    <c:when test="${item.appointmentType == 'PROCEDURE'}">
                        <td>
                            <fmt:message key="appointment-type.procedure"/>
                        </td>
                    </c:when>
                    <c:when test="${item.appointmentType == 'MEDICATION'}">
                        <td>
                            <fmt:message key="appointment-type.medication"/>
                        </td>
                    </c:when>
                </c:choose>
                <c:choose>
                    <c:when test="${item.diagnosis.diagnosisType() == 'TREATING'}">
                        <td>
                            <fmt:message key="diagnosis-type.treating"/>
                        </td>
                    </c:when>
                    <c:when test="${item.diagnosis.diagnosisType() == 'SOUND_HEALING'}">
                        <td>
                            <fmt:message key="diagnosis-type.sound_healing"/>
                        </td>
                    </c:when>
                    <c:when test="${item.diagnosis.diagnosisType() == 'MEDITATION'}">
                        <td>
                            <fmt:message key="diagnosis-type.meditation"/>
                        </td>
                    </c:when>
                    <c:when test="${item.diagnosis.diagnosisType() == 'OPERATION'}">
                        <td>
                            <fmt:message key="diagnosis-type.operation"/>
                        </td>
                    </c:when>
                    <c:when test="${item.diagnosis.diagnosisType() == 'CLINICAL_EXAMINATION'}">
                        <td>
                            <fmt:message key="diagnosis-type.clinical_examination"/>
                        </td>
                    </c:when>
                    <c:when test="${item.diagnosis.diagnosisType() == 'TREATING_IS_FINISHED'}">
                        <td>
                            <fmt:message key="diagnosis-type.treating_is_finished"/>
                        </td>
                    </c:when>
                    <c:when test="${item.diagnosis.diagnosisType() == 'INTENSIVE_TREATING'}">
                        <td>
                            <fmt:message key="diagnosis-type.intensive_treating"/>
                        </td>
                    </c:when>
                </c:choose>
                <td>${item.dateAndTime}</td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    <nav aria-label="Doctors search">
        <ul class="pagination">
            <c:forEach begin="1" end="${requestScope.pagedAppointments.numOfPages()}" varStatus="loop">
                <li class="page-item ${requestScope.pagedAppointments.pageNum() == loop.index ? "active" : ""}">
                    <a class="page-link"
                       href="controller?action=${param.action}&sorting=${requestScope.appointmentsSorting}&doctorId=${param.doctorId}&pageNum=${loop.index}">
                            ${loop.index}
                    </a>
                </li>
            </c:forEach>
        </ul>
    </nav>
</div>
