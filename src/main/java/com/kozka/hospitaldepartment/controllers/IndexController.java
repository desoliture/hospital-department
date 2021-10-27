package com.kozka.hospitaldepartment.controllers;

import com.kozka.hospitaldepartment.entities.Assignment;
import com.kozka.hospitaldepartment.entities.AssignmentType;
import com.kozka.hospitaldepartment.entities.User;
import com.kozka.hospitaldepartment.entities.UserRole;
import com.kozka.hospitaldepartment.services.AssignmentService;
import com.kozka.hospitaldepartment.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
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
public class IndexController {
    @Autowired
    UserService userService;

    @Autowired
    AssignmentService assignmentService;

    @GetMapping
    public String getIndex() {
        return "redirect:/cabinet";
    }

    @GetMapping("/cabinet")
    public String getIndex(Model model) {
        var user = userService.getCurrentLoggedUser();
        model.addAttribute("current_logged_in", user);
        return "patient/cab";
    }

    @GetMapping("/assignments")
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

    @GetMapping("/doctors")
    public String getDoctors(Model model) {
        var doctors = userService.getAllActiveDoctorsAndNurses();
        var current = userService.getCurrentLoggedUser();

        model.addAttribute("current_logged_in", current);
        model.addAttribute("doctors", doctors);
        return "patient/doctors";
    }

    @GetMapping("/users/{id}")
    public String getUser(
            @PathVariable Integer id,
            Model model)
    {
        var user = userService.getUserById(id);
        var current = userService.getCurrentLoggedUser();

        if (user.equals(current)) return "redirect:/";

        model.addAttribute("explored_user", user);
        model.addAttribute("current_logged_in", current);

        return "patient/explore";
    }

    @GetMapping("/users/{id}/edit")
    public String getManage(
            @PathVariable("id") User user,
            Model model
    ) {
        var current = userService.getCurrentLoggedUser();

        model.addAttribute("current_logged_in", current);
        model.addAttribute("editing", user);
        return "patient/edit";
    }

    @PostMapping("/edit")
    public String setManage(@ModelAttribute("editing") User user) {

        var current = userService.getCurrentLoggedUser();
        var edited = userService.getUserById(user.getId());

        if (user.getPass() == null || user.getPass().equals(""))
            user.setPass(edited.getPass());

        if (current.getId().equals(edited.getId())) {

            if (!edited.getEmail().equals(user.getEmail())) {
                SecurityContextHolder.getContext().getAuthentication().setAuthenticated(false);
                SecurityContextHolder.clearContext();
            }
        }

        user.setPass(
                userService.encodePass(
                        user.getPass()
                )
        );

        userService.update(user);

        return "redirect:/";
    }

    @GetMapping("assignments/for-me")
    public String getAssignmentsForMe(Model model) {
        var current = userService.getCurrentLoggedUser();
        var assgs = assignmentService.getAllFor(current);

        assgs.sort(Comparator.comparing(Assignment::getAssignmentDate));

        model.addAttribute("current_logged_in", current);
        model.addAttribute("assignments", assgs);
        model.addAttribute("current_date", LocalDateTime.now().plusDays(1));

        return "staff/assignments-for-me";
    }

    @GetMapping("assignments/by-me")
    public String getAssignmentsByMe(Model model) {
        var current = userService.getCurrentLoggedUser();
        var assgs = assignmentService.getAllBy(current);

        assgs.sort(Comparator.comparing(Assignment::getAssignmentDate));

        model.addAttribute("current_logged_in", current);
        model.addAttribute("assignments", assgs);
        model.addAttribute("current_date", LocalDateTime.now().plusDays(1));

        return "staff/assignments-by-me";
    }

    @GetMapping("/my-patients")
    public String getMyPatients(Model model) {
        var current = userService.getCurrentLoggedUser();
        var patients = userService.getAllActivePatientsForDoctor(current);

        model.addAttribute("current_logged_in", current);
        model.addAttribute("patients", patients);

        return "staff/my-patients";
    }

    @GetMapping("/assignments/add")
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

    @PostMapping("assignments/add")
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

    @GetMapping("/assignments/{id}/edit")
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

    @PostMapping("assignments/{id}/edit")
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

    @GetMapping("/assignments/{id}/hold")
    public String holdAssignment(
            @PathVariable("id") Assignment assg,
            Model model
    ) {
        model.addAttribute("assg_to_hold", assg);
        model.addAttribute("current_logged_in", userService.getCurrentLoggedUser());
        return "staff/assignments-hold";
    }

    @PostMapping("/assignments/{id}/hold")
    public String holdAssignmentPost(
            @ModelAttribute("assg_to_hold") Assignment assg
    ) {
        assg.setCompleted(true);

        assignmentService.update(assg);

        return "redirect:/assignments";
    }

    @GetMapping("/patients")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String getAllPatients(Model model) {
        var patients = userService.getAllActivePatients();
        var current = userService.getCurrentLoggedUser();

        model.addAttribute("patients", patients);
        model.addAttribute("current_logged_in", current);

        return "admin/patients";
    }

    @GetMapping("/patients/add")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String addPatient(
            Model model
    ) {
        model.addAttribute("new_user", new User());
        model.addAttribute("current_logged_in",
                userService.getCurrentLoggedUser());
        return "admin/patients-add";
    }

    @GetMapping("/doctors/add")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String addDoctor(
            Model model
    ) {
        model.addAttribute("new_user", new User());
        model.addAttribute("current_logged_in",
                userService.getCurrentLoggedUser());

        return "admin/doctors-add";
    }

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String addUser(
            @ModelAttribute("new_user") User user,
            @ModelAttribute("user_role")UserRole userRole
    ) {
        user.setActive(true);
        user.setUserRole(userRole);
        user.setPass(
                userService.encodePass(
                        user.getPass()
                )
        );

        userService.save(user);

        return "redirect:/";
    }


    @GetMapping("/assignments/{id}/remove")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'DOCTOR')")
    public String removeAssignment(
            @PathVariable("id") Assignment assignment,
            Model model,
            HttpServletRequest request
    ) {
        String previousUrl = request
                .getHeader("Referer")
                .replace("http://localhost:8080", "");

        model.addAttribute("assg_to_remove", assignment);
        model.addAttribute("previous_url", previousUrl);
        return "admin/assignments-remove";
    }

    @PostMapping("assignments/remove")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'DOCTOR')")
    public String removeAssignment(
            @ModelAttribute Integer id,
            @ModelAttribute("previous_url") String previousUrl
    ) {
        assignmentService.remove(id);

        return "redirect:/" + previousUrl;
    }

    @GetMapping("/users/{id}/remove")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String removeUser(
            @PathVariable("id") User user,
            Model model,
            HttpServletRequest request
    ) {
        String previousUrl = request
                .getHeader("Referer")
                .replace("http://localhost:8080", "");

        if (userService.getCurrentLoggedUser()
                .getId()
                .equals(user.getId()))
            return "redirect:" + previousUrl;

        model.addAttribute("user_to_remove", user);
        model.addAttribute("previous_url", previousUrl);

        return "admin/users-remove";
    }

    @PostMapping("/users/remove")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String removeUser(
            @ModelAttribute("id") Integer id,
            @ModelAttribute("previous_url") String previousUrl
    ) {
        userService.toArchive(id);

        return "redirect:" + previousUrl;
    }

    @GetMapping("/archive")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String getArchive(Model model) {
        model.addAttribute("current_logged_in",
                userService.getCurrentLoggedUser());

        return "admin/archive";
    }

    @GetMapping("/archive/patients")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String getArchivedPatients(Model model) {
        var users = userService.getAllInactivePatients();

        model.addAttribute("archived", users);
        model.addAttribute("current_logged_in", userService.getCurrentLoggedUser());

        return "admin/archived-users";
    }

    @GetMapping("/archive/doctors")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String getArchivedDoctors(Model model) {
        var users = userService.getAllInactiveDoctors();

        model.addAttribute("archived", users);
        model.addAttribute("current_logged_in", userService.getCurrentLoggedUser());

        return "admin/archived-users";
    }

    @GetMapping("/users/{id}/unarchive")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String unarchiveUser(
            @PathVariable("id") User user,
            Model model,
            HttpServletRequest request
    ) {
        String previousUrl = request
                .getHeader("Referer")
                .replace("http://localhost:8080", "");

        if (userService.getCurrentLoggedUser()
                .getId()
                .equals(user.getId()))
            return "redirect:" + previousUrl;

        model.addAttribute("user_to_unarchive", user);
        model.addAttribute("previous_url", previousUrl);

        return "admin/users-unarchive";
    }

    @PostMapping("/users/unarchive")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String unarchiveUser(
            @ModelAttribute("id") Integer id,
            @ModelAttribute("previous_url") String previousUrl
    ) {
        userService.unArchive(id);

        return "redirect:" + previousUrl;
    }

    @GetMapping("/health-card")
    public String getHealthCard(Model model) {
        var user = userService.getCurrentLoggedUser();
        var healthCard = userService.getHealthCardFor(user);

        model.addAttribute("current_logged_in", user);
        model.addAttribute("health_card", healthCard);
        return "patient/health-card";
    }

    @GetMapping("/users/{id}/health-card")
    public String getHealthCard(
            Model model,
            @PathVariable("id") User patient
    ) {
        var user = userService.getCurrentLoggedUser();
        var healthCard = userService.getHealthCardFor(patient);

        model.addAttribute("current_logged_in", user);
        model.addAttribute("health_card", healthCard);
        model.addAttribute("patient", patient);
        return "patient/health-card";
    }
}
