<%@ attribute name="doctorDto" type="com.oviktor.dto.DoctorDto" required="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setLocale value="${sessionScope.language}"/>
<fmt:setBundle basename="localization"/>
<%@ tag isELIgnored="false" %>

<td>${doctorDto.lastName}</td>
<td>${doctorDto.firstName}</td>
<td>${doctorDto.phone}</td>
<td>${doctorDto.email}</td>
<td>${doctorDto.birthDate}</td>
<td>${doctorDto.numOfPatients}</td>

<c:forEach items="${requestScope.categories}" var="typeDto">
    <c:if test="${doctorDto.medicineCategory.getId() == typeDto.id()}">
        <td>
            <fmt:message key="${typeDto.value()}"/>
        </td>
    </c:if>
</c:forEach>




