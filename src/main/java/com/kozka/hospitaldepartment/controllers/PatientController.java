package com.kozka.hospitaldepartment.controllers;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import com.kozka.hospitaldepartment.entities.User;
import com.kozka.hospitaldepartment.entities.UserRole;
import com.kozka.hospitaldepartment.services.UserService;
import com.kozka.hospitaldepartment.utils.ControllerUtil;
import lombok.AllArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Comparator;
import java.util.List;

/**
 * @author Kozka Ivan
 */
@Controller
@RequestMapping("/patients")
@AllArgsConstructor
public class PatientController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public String getAllPatients(
            @RequestParam(
                    value = "or",
                    required = false,
                    defaultValue = ""
            ) String order,
            @PageableDefault(
                    sort = {"assignmentDate"},
                    direction = Sort.Direction.DESC,
                    size = 1
            ) Pageable pageable,
            Model model
    ) {
        var patients = userService.getAllActivePatients();
        var current = userService.getCurrentLoggedUser();

        switch (order) {
            case "al":
                patients.sort(Comparator.comparing(User::getFullName).reversed());
                break;
            case "br":
                patients.sort(Comparator.comparing(User::getBirth));
                break;
        }

        Page<User> page = userService.getPageFor(patients, pageable);

        model.addAttribute("page", page);
        model.addAttribute("current_logged_in", current);
        model.addAttribute("order", "or=" + order);

        return "admin/patients";
    }

    @GetMapping("/add")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String addPatient(
            Model model
    ) {
        var user = new User();
        user.setUserRole(UserRole.PATIENT);
        model.addAttribute("new_user", user);
        model.addAttribute("current_logged_in",
                userService.getCurrentLoggedUser());
        return "admin/users-add";
    }

    @GetMapping("/{id}/export-health-card")
    public ResponseEntity<InputStreamResource> exportHealthCard(
            @PathVariable("id") User patient
    ) {

        List<String[]> healthCard = ControllerUtil.healthCardToString(
                patient, userService.getHealthCardFor(patient)
        );

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();

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

            InputStreamResource in = new InputStreamResource(
                    new ByteArrayInputStream(out.toByteArray())
            );

            HttpHeaders respHeaders = new HttpHeaders();
            MediaType mediaType = MediaType.parseMediaType("application/pdf");
            respHeaders.setContentType(mediaType);
            respHeaders.setContentDispositionFormData("attachment", "health_card.pdf");

            return new ResponseEntity<>(in, respHeaders, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();

            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
