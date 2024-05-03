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
            <img src="http://s3.amazonaws.com/pix.iemoji.com/images/emoji/apple/ios-12/256/man-health-worker-light-skin-tone.png"
                 alt="Avatar"
                 class="rounded-circle img-fluid" style="width: 150px;">
            <h5 class="my-3">${requestScope.foundDoctor.lastName} ${requestScope.foundDoctor.firstName}</h5>
            <p class="text-muted mb-1">${requestScope.foundDoctor.medicineCategory.toString()}</p>
            <div class="d-flex justify-content-center mb-2">
              <button type="button" class="btn btn-primary"
                      onclick="location.href='controller?action=doctorAppointments&doctorId=${requestScope.foundDoctor.id}'">
                <fmt:message key="card.appointment.all"/>
              </button>
              <button type="button" class="btn btn-outline-primary ms-1"
                      onclick="location.href='controller?action=makeAppointmentForDoctorPage&doctorId=${requestScope.foundDoctor.id}'">
                <fmt:message key="card.appointment.make"/>
              </button>
            </div>
            <div class="d-flex justify-content-center mb-2">
              <button type="button" class="btn btn-primary"
                      onclick="location.href='controller?action=doctorPatients&doctorId=${requestScope.foundDoctor.id}'">
                <fmt:message key="card.patients.current"/>
              </button>
            </div>
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
                <p class="text-muted mb-0">${requestScope.foundDoctor.lastName} ${requestScope.foundDoctor.firstName}</p>
              </div>
            </div>
            <hr>
            <div class="row">
              <div class="col-sm-3">
                <p class="mb-0"><fmt:message key="email"/></p>
              </div>
              <div class="col-sm-9">
                <p class="text-muted mb-0">${requestScope.foundDoctor.email}</p>
              </div>
            </div>
            <hr>
            <div class="row">
              <div class="col-sm-3">
                <p class="mb-0"><fmt:message key="phone"/></p>
              </div>
              <div class="col-sm-9">
                <p class="text-muted mb-0">${requestScope.foundDoctor.phone}</p>
              </div>
            </div>
            <hr>
            <div class="row">
              <div class="col-sm-3">
                <p class="mb-0"><fmt:message key="birthday"/></p>
              </div>
              <div class="col-sm-9">
                <p class="text-muted mb-0">${requestScope.foundDoctor.birthDate}</p>
              </div>
            </div>
            <hr>
            <div class="row">
              <div class="col-sm-3">
                <p class="mb-0"><fmt:message key="doctors.num"/></p>
              </div>
              <div class="col-sm-9">
                <p class="text-muted mb-0">${requestScope.foundDoctor.numOfPatients}</p>
              </div>
            </div>
            <hr>
            <div class="row">
              <div class="col-sm-3">
                <p class="mb-0"><fmt:message key="doctors.category"/></p>
              </div>
              <div class="col-sm-9">
                <p class="text-muted mb-0">${requestScope.foundDoctor.medicineCategory}</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</section>
