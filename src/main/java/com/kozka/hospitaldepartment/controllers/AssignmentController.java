package com.kozka.hospitaldepartment.controllers;

import com.kozka.hospitaldepartment.entities.Assignment;
import com.kozka.hospitaldepartment.entities.AssignmentType;
import com.kozka.hospitaldepartment.services.AssignmentService;
import com.kozka.hospitaldepartment.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Comparator;

/**
 * @author Kozka Ivan
 */
@Controller
@RequestMapping("/assignments")
@AllArgsConstructor
public class AssignmentController {
    private final UserService userService;
    private final AssignmentService assignmentService;

    @GetMapping
    public String getAssignments(Model model) {
        var user = userService.getCurrentLoggedUser();

        var assgs = assignmentService.getAllForPatient(user);
        var allAssgs = assignmentService.getAll();

        assgs.sort(Assignment::compareTo);
        allAssgs.sort(Assignment::compareTo);

        model.addAttribute("current_logged_in", user);
        model.addAttribute("assignments", assgs);
        model.addAttribute("all_assignments", allAssgs);
        model.addAttribute("current_date", LocalDateTime.now());
        return "patient/assignments";
    }

    @GetMapping("/for-me")
    public String getAssignmentsForMe(Model model) {
        var current = userService.getCurrentLoggedUser();
        var assgs = assignmentService.getAllFor(current);

        assgs.sort(Comparator.comparing(Assignment::getAssignmentDate));

        model.addAttribute("current_logged_in", current);
        model.addAttribute("assignments", assgs);
        model.addAttribute("current_date", LocalDateTime.now());

        return "staff/assignments-for-me";
    }

    @GetMapping("/by-me")
    public String getAssignmentsByMe(Model model) {
        var current = userService.getCurrentLoggedUser();
        var assgs = assignmentService.getAllBy(current);

        assgs.sort(Comparator.comparing(Assignment::getAssignmentDate));

        model.addAttribute("current_logged_in", current);
        model.addAttribute("assignments", assgs);
        model.addAttribute("current_date", LocalDateTime.now());

        return "staff/assignments-by-me";
    }

    @GetMapping("/add")
    public String addAssignments(Model model) {
        var current = userService.getCurrentLoggedUser();
        var patients = userService.getAllActivePatientsForDoctor(current);
        var allPatients = userService.getAllActivePatients();
        var doctors = userService.getAllActiveDoctors();
        var allDoctors = userService.getAllActiveDoctorsAndNurses();

        model.addAttribute("current_logged_in", current);
        model.addAttribute("patients", patients);
        model.addAttribute("all_patients", allPatients);
        model.addAttribute("all_doctors", allDoctors);
        model.addAttribute("doctors", doctors);
        model.addAttribute("new_assignment", new Assignment());

        return "staff/assignments-add";
    }

    @PostMapping("/add")
    public String addAssignmentsPost(
            @ModelAttribute("new_assignment") Assignment newAssg,
            @ModelAttribute("pat-id") Integer patId,
            @ModelAttribute("asg-id-all") Integer asgIdAll,
            @ModelAttribute("asg-id-doc") Integer asgIdDoc
    ) {
        if (newAssg.getAssigner() == null)
            newAssg.setAssigner(userService.getCurrentLoggedUser());

        int asgId;

        if (newAssg.getAssgType() == AssignmentType.PROCEDURE
                || newAssg.getAssgType() == AssignmentType.MEDICINE)
        {
            asgId = asgIdAll;
        }
        else asgId = asgIdDoc;

        var patient = userService.getUserById(patId);
        var assigned = userService.getUserById(asgId);

        newAssg.setPatient(patient);
        newAssg.setAssigned(assigned);
        newAssg.setCompleted(false);
        newAssg.setCreationDate(LocalDateTime.now());

        assignmentService.save(newAssg);

        return "redirect:/assignments";
    }

    @GetMapping("/{id}/edit")
    public String editAssignment(
            @PathVariable("id") Integer id,
            @PathVariable("id") Assignment assg,
            Model model
    ) {
        assg.setId(id);
        var current = userService.getCurrentLoggedUser();
        var patients = userService.getAllActivePatientsForDoctor(current);
        var doctors = userService.getAllActiveDoctorsAndNurses();
        var allPatients = userService.getAllActivePatients();


        model.addAttribute("edited_assg", assg);
        model.addAttribute("current_logged_in", current);
        model.addAttribute("patients", patients);
        model.addAttribute("doctors", doctors);
        model.addAttribute("all_patients", allPatients);
        return "staff/assignments-edit";
    }

    @PostMapping("/{id}/edit")
    public String editAssignmentPost(
            @ModelAttribute("edited_assg") Assignment edited,
            @PathVariable("id") Integer id,
            @ModelAttribute("pat-id") Integer patId,
            @ModelAttribute("asg-id") Integer asgId
    ) {
        var patient = userService.getUserById(patId);
        var assigned = userService.getUserById(asgId);

        edited.setId(id);
        edited.setPatient(patient);
        edited.setAssigned(assigned);

        assignmentService.update(edited);

        return "redirect:/assignments";
    }

    @GetMapping("/{id}/hold")
    public String holdAssignment(
            @PathVariable("id") Assignment assg,
            Model model
    ) {
        model.addAttribute("assg_to_hold", assg);
        model.addAttribute("current_logged_in", userService.getCurrentLoggedUser());
        return "staff/assignments-hold";
    }

    @PostMapping("/{id}/hold")
    public String holdAssignmentPost(
            @ModelAttribute("assg_to_hold") Assignment assg
    ) {
        assg.setCompleted(true);

        assignmentService.update(assg);

        return "redirect:/assignments";
    }

    @GetMapping("/{id}/remove")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'DOCTOR')")
    public String removeAssignment(
            @PathVariable("id") Assignment assignment,
            Model model,
            HttpServletRequest request
    ) {
        String previousUrl = request
                .getHeader("Referer")
                .replace("http://localhost:8080", "");

        model.addAttribute("current_logged_in", userService.getCurrentLoggedUser());
        model.addAttribute("assg_to_remove", assignment);
        model.addAttribute("previous_url", previousUrl);
        return "admin/assignments-remove";
    }

    @PostMapping("/remove")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'DOCTOR')")
    public String removeAssignment(
            @ModelAttribute("id") Integer id,
            @ModelAttribute("previous_url") String previousUrl
    ) {
        assignmentService.remove(id);

        return "redirect:" + previousUrl;
    }
}
