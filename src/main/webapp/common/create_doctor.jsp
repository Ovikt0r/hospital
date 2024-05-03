<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="language"
       value="${not empty param.language ? param.language : not empty language ? language : pageContext.request.locale}"
       scope="session"/>
<fmt:setLocale value="${language}"/>
<fmt:setBundle basename="localization"/>
<%@ page isELIgnored="false" %>
<div class="create-doctor-form ms-3 mt-3">
  <h2><fmt:message key="create.doctor"/></h2>
  <form method="post" action="controller?action=createDoctor">
    <div class="form-floating mb-3 w-25">
      <input type="text" class="form-control" id="lastName" name="lastName" placeholder="Last name" required>
      <label for="lastName" class="form-label"><fmt:message key="name.last"/></label>
    </div>
    <div class="form-floating mb-3 w-25">
      <input type="text" class="form-control" id="firstName" name="firstName" placeholder="First name" required>
      <label for="firstName" class="form-label"><fmt:message key="name.first"/></label>
    </div>
    <div class="form-floating mb-3 w-25">
      <input type="text" class="form-control" id="phone" name="phone" placeholder="Phone number" required>
      <label for="phone" class="form-label"><fmt:message key="phone"/></label>
    </div>
    <div class="form-floating mb-3 w-25">
      <input type="email" class="form-control" id="email" aria-describedby="emailHelp" name="email"
             placeholder="Email" required>
      <label for="email" class="form-label"><fmt:message key="email"/></label>
      <div id="emailHelp" class="form-text"><fmt:message key="email.help"/></div>
    </div>
    <div class="form-floating mb-3 w-25">
      <input type="password" class="form-control" id="password" name="password" placeholder="Password" required>
      <label for="password" class="form-label"><fmt:message key="password"/></label>
    </div>
    <div class="form-floating mb-3 w-25">
      <input type="date" class="form-control" id="birthday" name="birthday" placeholder="Day of birth" required>
      <label for="birthday" class="form-label"><fmt:message key="birthday"/></label>
    </div>
    <div class="mb-3 w-25">
      <label for="categoryId" class="form-label"><fmt:message key="doctors.category"/></label>
      <select class="form-select" id="categoryId" name="categoryId" required>
        <c:forEach var="item" items="${requestScope.categories}">
          <option value=${item.id()}><fmt:message key="${item.value()}"/></option>
        </c:forEach>
      </select>
    </div>
    <button type="submit" class="btn btn-primary"><fmt:message key="button.submit"/></button>
  </form>
</div>
