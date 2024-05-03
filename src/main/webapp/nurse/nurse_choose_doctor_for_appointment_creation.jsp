<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Hospital</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css" rel="stylesheet"
          integrity="sha384-rbsA2VBKQhggwzxH7pPCaAqO46MgnOM80zW1RWuH61DGLwZJEdK2Kadq2F9CUG65" crossorigin="anonymous">
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/js/bootstrap.bundle.min.js"
            integrity="sha384-kenU1KFdBIe4zVF0s0G1M5b4hcpxyD9F7jL+jjXkk+Q2h455rYXK/7HAuoJl+0I4"
            crossorigin="anonymous"></script>
    <link
            href="https://fonts.googleapis.com/css?family=Roboto:300,400,500,700&display=swap"
            rel="stylesheet"
    />
</head>
<body>
<div>
    <jsp:include page="nurse_navbar.jsp"/>
    <jsp:include page="../common/choose_doctor_paged_page.jsp">
        <jsp:param name="action" value="makeAppointmentForPatientPage"/>
        <jsp:param name="page_action" value="chooseDoctorForAppointmentCreationPage"/>
        <jsp:param name="dataToSave" value="&patientId=${param.patientId}"/>
    </jsp:include>
</div>
</body>
</html>
