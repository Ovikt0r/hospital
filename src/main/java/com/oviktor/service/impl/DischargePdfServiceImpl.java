package com.oviktor.service.impl;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import com.oviktor.dao.AppointmentDao;
import com.oviktor.dao.DiagnosisDao;
import com.oviktor.dao.UserDao;
import com.oviktor.dao.UsersDoctorsDao;
import com.oviktor.dto.DoctorDto;
import com.oviktor.dto.PatientDto;
import com.oviktor.dto.UserInfo;
import com.oviktor.entity.Appointment;
import com.oviktor.entity.Diagnosis;
import com.oviktor.enums.Roles;
import com.oviktor.service.DischargePdfService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Objects;
import java.util.Properties;

@Slf4j
@RequiredArgsConstructor
public class DischargePdfServiceImpl implements DischargePdfService {

    private final UserDao userDao;
    private final AppointmentDao appointmentDao;
    private final DiagnosisDao diagnosisDao;
    private final UsersDoctorsDao usersDoctorsDao;
    private static final Properties prop = new Properties();


    public void generatePdf(HttpServletResponse response, String currentLanguage, UserInfo currentUserInfo, Long patientId, Long appointmentId) {
        String fileName;
        ClassLoader classLoader = getClass().getClassLoader();
        if (currentLanguage.equals("uk")) {
            fileName = "/localization.properties";
        } else {
            fileName = "/localization_" + currentLanguage + ".properties";
        }

        URL resourceUrl = classLoader.getResource(fileName);
        String filePath = new File(resourceUrl.getFile()).getPath();
        try (Reader reader = new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8)) {
            prop.load(reader);
        } catch (IOException e) {
            log.error("Some issue happened in processing of load reader object to the property ", e);
            throw new RuntimeException();
        }
        String patientNameField = prop.getProperty("pdf-field.patient.full.name");
        String doctorNameField = prop.getProperty("pdf-field.doctor.full.name");
        String birthDateField = prop.getProperty("pdf-field.patient.birth.date");
        String appointmentField = prop.getProperty("pdf-field.appointment.date");
        String diagnosisField = prop.getProperty("pdf-field.diagnosis");
        String pdfHeader = prop.getProperty("pdf-field.header");

        Appointment appointment = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (isDoctor(currentUserInfo) || isAdmin(currentUserInfo)) {
            appointment = appointmentDao.getAppointmentById(appointmentId);
            if (isDoctor(currentUserInfo) && !usersDoctorsDao.patientIsTreatedByDoctor(
                    appointment.getPatientId(), currentUserInfo.id())) {
                log.error("It is not possible to generate a discharge form because the selected patient is not being treated by the current doctor.");
                throw new RuntimeException();
            }
        }
        PatientDto patient = userDao.getPatientById(patientId);
        String dateOfPatientBirth = patient.getDateOfBirth()
                .format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT));
        String appointmentDate = appointment.getAppointmentDate()
                .format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT));
        DoctorDto doctor = userDao.getDoctorById(appointment.getDoctorId());
        Diagnosis diagnosis = diagnosisDao.getDiagnosisById(appointment.getDiagnosisId());

        try {
            Path stampPath = Paths.get(Objects.requireNonNull(classLoader.getResource("doctor6.png")).toURI());
            Path signPath = Paths.get(Objects.requireNonNull(classLoader.getResource("signature.png")).toURI());
            Document document = new Document();
            PdfWriter.getInstance(document, baos);
            String fontPath = "./times.ttf";
            BaseFont baseFont = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            FontFactory.register(fontPath);
            Font fontHeader = FontFactory.getFont(baseFont.getPostscriptFontName(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 21, Font.BOLD);
            Font fontDiagnoseHeader = FontFactory.getFont(baseFont.getPostscriptFontName(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 18, Font.BOLD);
            Font font = FontFactory.getFont(baseFont.getPostscriptFontName(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 16);
            document.open();
            addPdfHeader(document, pdfHeader, fontHeader);
            addNameParagraph(document, patientNameField, patient.getFirstName(), patient.getLastName(), font);
            addDateParagraph(document, birthDateField, dateOfPatientBirth, font);
            addDateParagraph(document, appointmentField, appointmentDate, font);
            addNameParagraph(document, doctorNameField, doctor.getFirstName(), doctor.getLastName(), font);
            Paragraph diagnoseHeader = new Paragraph(diagnosisField, fontDiagnoseHeader);
            diagnoseHeader.setAlignment(Element.ALIGN_CENTER);
            document.add(diagnoseHeader);
            Paragraph diagnoseContent = new Paragraph(diagnosis.getText(), font);
            document.add(diagnoseContent);
            addSignature(document, stampPath, signPath);
            document.close();
            openInBrowser(response, baos);
            log.info("Discharge pdf file has created successfully!");
        } catch (DocumentException | URISyntaxException | IOException e) {
            log.error("Something went wrong during form the pdf document", e);
            throw new RuntimeException();
        }
    }

    private static void addPdfHeader(Document document, String pdfHeader, Font font) throws DocumentException {
        Paragraph header = new Paragraph(pdfHeader, font);
        header.setAlignment(Element.ALIGN_CENTER);
        document.add(header);
        document.add(new Paragraph("\n\n"));
    }

    private static void addNameParagraph(Document document, String fieldName, String firstName, String lastName, Font font) throws DocumentException {
        Chunk patientName = new Chunk(fieldName, font);
        Chunk patientNameContent = new Chunk("    " + firstName + " " + lastName, font);
        Paragraph patientNamePar = new Paragraph();
        patientNamePar.add(patientName);
        patientNamePar.add(patientNameContent);
        document.add(patientNamePar);
        document.add(new Paragraph("\n\n"));
    }

    private static void addDateParagraph(Document document, String fieldName, String date, Font font) throws DocumentException {
        Chunk fieldNameChunk = new Chunk(fieldName, font);
        Chunk dateContentChunk = new Chunk("    " + date, font);
        Paragraph patientDate = new Paragraph();
        patientDate.add(fieldNameChunk);
        patientDate.add(dateContentChunk);
        document.add(patientDate);
        document.add(new Paragraph("\n\n"));
    }

    private static void addSignature(Document document, Path stamp, Path signature) throws DocumentException, IOException {
        Image stampImage = Image.getInstance(stamp.toAbsolutePath().toString());
        Image singImage = Image.getInstance(signature.toAbsolutePath().toString());
        stampImage.scaleAbsolute(110, 110);
        singImage.scaleAbsolute(110, 110);
        //stampImage.setAbsolutePosition(document.right()-5,stampImage.getAbsoluteY());
        // singImage.setAbsolutePosition(document.left()+5,singImage.getAbsoluteY());
        Chunk stampChunk = new Chunk(stampImage, 300, -250, true);
        Chunk signChunk = new Chunk(singImage, 300, -250, true);
        Paragraph signatureParagraph = new Paragraph();
        signatureParagraph.add(signChunk);
        signatureParagraph.add(stampChunk);
        document.add(signatureParagraph);
    }

    private static void openInBrowser(HttpServletResponse response, ByteArrayOutputStream baos) {
        OutputStream outputStream = null;
        response.setHeader("Content-Disposition", "attachment; filename=\"dischargeRecord.pdf\"");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        try {
            outputStream = response.getOutputStream();
            baos.writeTo(outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean isDoctor(UserInfo userInfo) {
        return Roles.valueOf(userInfo.role().toUpperCase()).equals(Roles.DOCTOR);
    }

    private static boolean isAdmin(UserInfo userInfo) {
        return Roles.valueOf(userInfo.role().toUpperCase()).equals(Roles.ADMIN);
    }
}
