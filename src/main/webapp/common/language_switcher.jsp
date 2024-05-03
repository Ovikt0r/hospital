<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="language"
       value="${not empty param.language ? param.language : not empty language ? language : pageContext.request.locale}"
       scope="session"/>
<fmt:setLocale value="${language}"/>
<fmt:setBundle basename="localization"/>
<%@ page isELIgnored="false" %>
<jsp:include page="removeParam.jsp"/>
<script type="text/javascript" src="${pageContext.request.contextPath}removeParam.js"></script>
<form class="w3-bar-item w3-button w3-hover-white">
    <label>
        <select class="form-select form-select-lg" id="language" name="language" onchange="{
                window.location.href=removeParam('language', window.location.href) + '&language=' + document.getElementById('language').value
            }">
            <option value="en" ${language == 'en' ? 'selected' : ''}><fmt:message key="button.lang.en"/></option>
            <option value="ua" ${language == 'ua' ? 'selected' : ''}><fmt:message key="button.lang.ua"/></option>
        </select>
    </label>
</form>
