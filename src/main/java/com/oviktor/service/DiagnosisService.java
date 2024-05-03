package com.oviktor.service;

import com.oviktor.dto.UserInfo;
import com.oviktor.entity.Diagnosis;
import com.oviktor.enums.DiagnosisTypes;


public interface DiagnosisService {

    void diagnose(UserInfo currentUserInfo, long appointmentId, String text, DiagnosisTypes diagnosisType);

    Diagnosis getDiagnosisById(Long diagnosisId);

    void updateDiagnosis(long id, String text, DiagnosisTypes diagnosisType);
}
