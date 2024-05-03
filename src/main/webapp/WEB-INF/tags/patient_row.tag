<%@ attribute name="patient" type="com.oviktor.dto.PatientDto" required="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setLocale value="${sessionScope.language}"/>
<fmt:setBundle basename="localization"/>
<%@ tag isELIgnored="false" %>

<td>${patient.lastName}</td>
<td>${patient.firstName}</td>
<td>${patient.phone}</td>
<td>${patient.email}</td>
<td>${patient.birthDate}</td>
<c:choose>
    <c:when test="${patient.isTreated==true}">
        <td><fmt:message key="patient.is.being.treated.true"/></td>
    </c:when>
    <c:otherwise>
        <td><fmt:message key="patient.is.being.treated.false"/></td>
    </c:otherwise>
</c:choose>
