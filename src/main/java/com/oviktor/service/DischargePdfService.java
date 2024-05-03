package com.oviktor.service;

import com.oviktor.dto.UserInfo;
import jakarta.servlet.http.HttpServletResponse;

public interface DischargePdfService {

    void generatePdf(HttpServletResponse response, String currentLanguage,UserInfo currentUserInfo, Long patientId, Long appointmentId);


}
