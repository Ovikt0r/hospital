package com.oviktor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oviktor.annotation.handler.TransactionalInvocationHandler;
import com.oviktor.dao.*;
import com.oviktor.mapper.UserMapper;
import com.oviktor.service.*;
import com.oviktor.service.impl.*;
import lombok.AccessLevel;
import lombok.Getter;

import java.lang.reflect.Proxy;

@Getter
public class ApplicationContext {

    @Getter(value = AccessLevel.PRIVATE)
    private static ApplicationContext INSTANCE;

    private final UserDao userDao;
    private final DoctorDao doctorDao;
    private final AppointmentDao appointmentDao;
    private final DiagnosisDao diagnosisDao;
    private final UsersDoctorsDao usersDoctorsDao;
    private final SecurityDao securityDao;
    private final AppointmentPermissionDao appointmentPermissionDao;

    private final UserMapper userMapper;
    private final ObjectMapper objectMapper;

    private final MedicalStaffService medicalStaffService;
    private final AppointmentService appointmentService;
    private final PatientService patientService;
    private final DiagnosisService diagnosisService;
    private final SecurityService securityService;
    private final DischargePdfService dischargePdfService;


    private ApplicationContext() {
        this.objectMapper = new ObjectMapper();
        this.userMapper = UserMapper.INSTANCE;

        this.userDao = new UserDao();
        this.doctorDao = new DoctorDao();
        this.appointmentDao = new AppointmentDao();
        this.diagnosisDao = new DiagnosisDao();
        this.usersDoctorsDao = new UsersDoctorsDao();
        this.securityDao = new SecurityDao();
        this.appointmentPermissionDao = new AppointmentPermissionDao();



        TransactionalInvocationHandler<DischargePdfService> dischargePdfServiceHandler =
                new TransactionalInvocationHandler<>(new DischargePdfServiceImpl(userDao,appointmentDao,diagnosisDao,usersDoctorsDao));
        this.dischargePdfService = (DischargePdfService) Proxy.newProxyInstance(
                DischargePdfServiceImpl.class.getClassLoader(),
                new Class[]{DischargePdfService.class},
                dischargePdfServiceHandler);


        TransactionalInvocationHandler<MedicalStaffService> medicalStaffServiceHandler =
                new TransactionalInvocationHandler<>(new MedicalStaffServiceImpl(userDao, doctorDao, userMapper));

        this.medicalStaffService = (MedicalStaffService) Proxy.newProxyInstance(
                MedicalStaffServiceImpl.class.getClassLoader(),
                new Class[]{MedicalStaffService.class},
                medicalStaffServiceHandler);

        TransactionalInvocationHandler<DiagnosisService> diagnosisServiceHandler =
                new TransactionalInvocationHandler<>(new DiagnosisServiceImpl(appointmentDao, diagnosisDao, usersDoctorsDao));
        this.diagnosisService = (DiagnosisService) Proxy.newProxyInstance(
                DiagnosisServiceImpl.class.getClassLoader(),
                new Class[]{DiagnosisService.class},
                diagnosisServiceHandler);

        TransactionalInvocationHandler<PatientService> patientServiceHandler =
                new TransactionalInvocationHandler<>(new PatientServiceImpl(userDao, appointmentDao, diagnosisDao, usersDoctorsDao, userMapper));
        this.patientService = (PatientService) Proxy.newProxyInstance(
                PatientServiceImpl.class.getClassLoader(),
                new Class[]{PatientService.class},
                patientServiceHandler);

        TransactionalInvocationHandler<AppointmentService> appointmentServiceHandler =
                new TransactionalInvocationHandler<>(new AppointmentServiceImpl(appointmentDao, usersDoctorsDao));
        this.appointmentService = (AppointmentService) Proxy.newProxyInstance(
                AppointmentServiceImpl.class.getClassLoader(),
                new Class[]{AppointmentService.class},
                appointmentServiceHandler);

        TransactionalInvocationHandler<SecurityService> securityServiceHandler =
                new TransactionalInvocationHandler<>(new SecurityServiceImpl(securityDao));
        this.securityService = (SecurityService) Proxy.newProxyInstance(
                SecurityServiceImpl.class.getClassLoader(),
                new Class[]{SecurityService.class},
                securityServiceHandler);


    }

    public static ApplicationContext getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ApplicationContext();
        }
        return INSTANCE;
    }
}
