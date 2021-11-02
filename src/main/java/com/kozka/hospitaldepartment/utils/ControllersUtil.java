package com.kozka.hospitaldepartment.utils;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import com.kozka.hospitaldepartment.entities.Assignment;
import com.kozka.hospitaldepartment.entities.AssignmentType;
import com.kozka.hospitaldepartment.entities.User;
import com.kozka.hospitaldepartment.services.UserService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Kozka Ivan
 */
@Service
public class ControllersUtil {
    public static Map<String, String> getErrorsMap(BindingResult bindingResult) {
        return bindingResult.getFieldErrors().stream()
                .collect(Collectors.toMap(
                        fieldError -> fieldError.getField(),
                        FieldError::getDefaultMessage
                ));
    }

    public static List<String[]> healthCardToStringsArray(User patient, List<Assignment> healthCard) {
        return healthCard.stream()
                .map(a -> {
                    String date = a.getAssignmentDate().format(
                            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
                    );

                    String asType = a.getAssgType()
                            .toStringFirstUpper() + ": " + a.getDescription();

                    String assigner = "Appointed by: "
                            + a.getAssigner().getFirstName()
                            + " "
                            + a.getAssigner().getLastName()
                            + (a.getAssigner().getSpecialization() != null ?
                            (" (" + a.getAssigner().getSpecialization().namePar() + ")") : "");
                    String assigned = "Held by: "
                            + a.getAssigned().getFirstName()
                            + " "
                            + a.getAssigned().getLastName()
                            + (a.getAssigned().getSpecialization() != null ? (
                            " (" + a.getAssigned().getSpecialization().namePar() + ")") : "");

                    String conclusion = "Conclusion: " + a.getConclusion();

                    return new String[]{date, asType, assigner, assigned, conclusion};
                }).collect(Collectors.toList());
    }

    public static void sortingByOrder(String order, List<User> users) {
        switch (order) {
            case "al":
                users.sort(Comparator.comparing(User::getFullName).reversed());
                break;
            case "br":
                users.sort(Comparator.comparing(User::getBirth));
                break;
        }
    }

    public static Document createDocumentFromHealthCard(
            User patient,
            List<String[]> healthCard,
            ByteArrayOutputStream out

    ) throws DocumentException {

        Document document = new Document();
        PdfWriter.getInstance(
                document, out
        );

        document.open();

        Font fontTitle = FontFactory.getFont(
                FontFactory.COURIER_BOLDOBLIQUE,
                28, BaseColor.DARK_GRAY
        );
        Font fontHead = FontFactory.getFont(
                FontFactory.COURIER_BOLDOBLIQUE,
                16, BaseColor.BLACK
        );
        Font fontBody = FontFactory.getFont(
                FontFactory.COURIER_OBLIQUE,
                14, BaseColor.BLACK
        );

        Chunk titleChunk = new Chunk(patient.getFullName(), fontTitle);

        document.add(titleChunk);
        document.add(Paragraph.getInstance("\n"));
        document.add(Paragraph.getInstance("\n"));

        for (String[] text : healthCard) {
            for (int i = 0; i < text.length; i++) {
                String line = text[i];
                Chunk chunk;

                if (i == 0) chunk = new Chunk(line, fontHead);
                else {
                    document.add(Paragraph.getInstance("        "));
                    chunk = new Chunk(line, fontBody);
                }

                document.add(chunk);

                document.add(Paragraph.getInstance("\n"));
            }
            document.add(Paragraph.getInstance("——————————————————————————————————————————\n"));
        }
        document.close();

        return document;
    }

    public static ResponseEntity<InputStreamResource> getResponseEntityToDownloadHealthCardInPDF(
            User patient, List<String[]> healthCard
    ) throws DocumentException {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document doc = createDocumentFromHealthCard(
                patient,
                healthCard,
                out
        );

        InputStreamResource in = new InputStreamResource(
                new ByteArrayInputStream(out.toByteArray())
        );

        String fileName = "hc_" + patient.getFirstName()
                + "-" + patient.getLastName()
                + "_" + patient.getId() + ".pdf";

        HttpHeaders respHeaders = new HttpHeaders();
        MediaType mediaType = MediaType.parseMediaType("application/pdf");
        respHeaders.setContentType(mediaType);
        respHeaders.setContentDispositionFormData("attachment", fileName);

        return new ResponseEntity<>(in, respHeaders, HttpStatus.OK);
    }

    public static Map<String, Object> assignmentPutAllInModel(
            UserService userService, Assignment assg
    ) {
        Map<String, Object> model = new HashMap<>();

        var current = userService.getCurrentLoggedUser();
        var patients = userService.getAllActivePatientsForDoctor(current);
        var allPatients = userService.getAllActivePatients();
        var doctors = userService.getAllActiveDoctors();
        var allDoctors = userService.getAllActiveDoctorsAndNurses();

        model.put("current_logged_in", current);
        model.put("patients", patients);
        model.put("all_patients", allPatients);
        model.put("all_doctors", allDoctors);
        model.put("doctors", doctors);
        model.put("assignment", assg);

        return model;
    }

    public static void setAssignmentDetails(
            Assignment assg,
            UserService userService,

            Integer asgIdAll,
            Integer asgIdDoc,
            Integer patId
    ) {
        int asgId;

        var aT = assg.getAssgType();
        if (aT == AssignmentType.PROCEDURE
                || aT == AssignmentType.MEDICINE
        ) {
            asgId = asgIdAll;
        } else asgId = asgIdDoc;

        var patient = userService.getUserById(patId);
        var assigned = userService.getUserById(asgId);

        assg.setAssigned(assigned);
        assg.setPatient(patient);
    }
}
