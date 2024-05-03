<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="language"
       value="${not empty param.language ? param.language : not empty language ? language : pageContext.request.locale}"
       scope="session"/>
<fmt:setLocale value="${language}"/>
<fmt:setBundle basename="localization"/>
<%@ page isELIgnored="false" %>
<div class="create-doctor-form ms-3 mt-3">
    <h2><fmt:message key="update.appointment"/></h2>
    <form method="post" action="controller?action=updateAppointment&appointmentId=${param.appointmentId}">
        <div class="mb-3 w-25">
            <label for="appointmentType" class="form-label"><fmt:message key="choose.appointment.type"/></label>
            <select class="form-select" id="appointmentType" name="appointmentType">
                <option selected
                        value="${requestScope.selectedType.id()}">${requestScope.selectedType.value()}</option>
                <c:forEach var="item" items="${requestScope.appointmentOptions}">
                    <option value=${item.id()}>${item.value()}</option>
                </c:forEach>
            </select>
        </div>
        <div class="mb-3 w-25">
            <label for="appointmentDateTime" class="form-label"><fmt:message key="datetime"/></label>
            <input type="datetime-local" class="form-control" id="appointmentDateTime" name="appointmentDateTime"
                   value="${requestScope.currentAppointment.appointmentDate}" required>
        </div>
        <button type="submit" class="btn btn-primary"><fmt:message key="button.submit"/></button>
    </form>
</div>

