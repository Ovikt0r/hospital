package com.oviktor.service.impl;

import com.oviktor.annotation.Transactional;
import com.oviktor.dao.AppointmentDao;
import com.oviktor.dao.DiagnosisDao;
import com.oviktor.dao.UsersDoctorsDao;
import com.oviktor.dto.UserInfo;
import com.oviktor.entity.Appointment;
import com.oviktor.entity.Diagnosis;
import com.oviktor.enums.DiagnosisTypes;
import com.oviktor.enums.Roles;
import com.oviktor.service.DiagnosisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class DiagnosisServiceImpl implements DiagnosisService {

    private final AppointmentDao appointmentDao;
    private final DiagnosisDao diagnosisDao;
    private final UsersDoctorsDao usersDoctorsDao;

    @Transactional
    public void diagnose(UserInfo currentUserInfo, long appointmentId, String text, DiagnosisTypes diagnosisType) {
        if (Roles.valueOf(currentUserInfo.role().toUpperCase()).equals(Roles.DOCTOR)) {
            Appointment appointment = appointmentDao.getAppointmentById(appointmentId);
            if (!usersDoctorsDao.patientIsTreatedByDoctor(appointment.getPatientId(), currentUserInfo.id())) {
                log.error("It is not possible to diagnose because the selected patient is not being treated by the current doctor.");
                throw new RuntimeException();
            }
        }
        Long diagnosisId = diagnosisDao.addDiagnosis(text, diagnosisType, true);
        appointmentDao.addDiagnosis(appointmentId, diagnosisId, true);
    }

    public Diagnosis getDiagnosisById(Long diagnosisId) {
        return diagnosisDao.getDiagnosisById(diagnosisId);
    }

    public void updateDiagnosis(long id, String text, DiagnosisTypes diagnosisType) {
        diagnosisDao.updateDiagnosis(id, text, diagnosisType);
    }
}
