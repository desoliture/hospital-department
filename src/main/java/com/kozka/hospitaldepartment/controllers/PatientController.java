package com.kozka.hospitaldepartment.controllers;

import com.itextpdf.text.*;
import com.kozka.hospitaldepartment.entities.User;
import com.kozka.hospitaldepartment.entities.UserRole;
import com.kozka.hospitaldepartment.exceptions.ForbiddenException;
import com.kozka.hospitaldepartment.exceptions.UnreachablePatientException;
import com.kozka.hospitaldepartment.services.UserService;
import com.kozka.hospitaldepartment.utils.ControllersUtil;
import lombok.AllArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

        ControllersUtil.sortingByOrder(order, patients);

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
        var logged = userService.getCurrentLoggedUser();
        String loggedRole = logged.getUserRole().name();

        if (!(loggedRole.equals("PATIENT")
                && logged.getId().equals(patient.getId())))
            throw new ForbiddenException(
                    "Attempt to export a health card that doesn't belong to user!"
            );
        else if (!(logged.getId().equals(patient.getId())
                || userService.isDoctorAssignedToPatient(logged, patient)))
            throw new UnreachablePatientException(
                    "This patient isn't appointed to you!"
            );

        List<String[]> healthCard = ControllersUtil.healthCardToStringsArray(
                patient, userService.getHealthCardFor(patient)
        );

        try {
            return ControllersUtil.getResponseEntityToDownloadHealthCardInPDF(
                    patient,
                    healthCard
            );
        } catch (DocumentException e) {
            throw new RuntimeException(
                    "An error occurred during export of health card!", e
            );
        }
    }
}
