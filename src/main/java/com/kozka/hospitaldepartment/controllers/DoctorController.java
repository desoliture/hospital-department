package com.kozka.hospitaldepartment.controllers;

import com.kozka.hospitaldepartment.entities.Assignment;
import com.kozka.hospitaldepartment.entities.Specialization;
import com.kozka.hospitaldepartment.entities.User;
import com.kozka.hospitaldepartment.entities.UserRole;
import com.kozka.hospitaldepartment.services.AssignmentService;
import com.kozka.hospitaldepartment.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;

/**
 * @author Kozka Ivan
 */
@Controller
@RequestMapping("/docs")
public class DoctorController {

    @Autowired
    private AssignmentService assgService;

    @Autowired
    private UserService userService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'DOCTOR')")
    public String getDocs(Model model) {
        List<User> doctors = userService.getAllActiveDoctors();
        doctors.sort(Comparator.comparing(User::getId));

        String email = userService.getCurrentAuthEmail();
        User current = userService.getUserByEmail(email);

        model.addAttribute("doctors", doctors);
        model.addAttribute("currentUser", current);
        return "doctors";
    }

    @GetMapping("/add")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String addDoc(Model model) {
        model.addAttribute("new_user", new User());
        model.addAttribute("specs", Specialization.values());
        return "add_doc";
    }

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String addDocPost(
            @ModelAttribute("new_user") User user)
    {
        user.setActive(true);
        user.setUserRole(UserRole.DOCTOR);

        userService.save(user);

        return "redirect:/docs";
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String modDoc(
            @PathVariable("id") Integer id,
            Model model)
    {
        User user = userService.getUserById(id);
        model.addAttribute("modify_user", user);
        return "mod_doc";
    }

    @PostMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String modDocPost(
            @ModelAttribute("modify_user") User user)
    {
        userService.save(user);
        return "redirect:/docs";
    }

    @GetMapping("/rem/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String remDoc(
            @PathVariable("id") Integer id)
    {
        userService.ifNotAppointedDeleteElseMakeInactive(id);

        return "redirect:/docs";
    }

    @GetMapping("/inactive")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String getInactive(Model model) {
        List<User> doctors = userService.getAllInactiveDoctors();
        doctors.sort(Comparator.comparing(User::getId));

        String email = userService.getCurrentAuthEmail();
        User current = userService.getUserByEmail(email);

        model.addAttribute("doctors", doctors);
        model.addAttribute("currentUser", current);
        return "inactive_doctors";
    }

    @GetMapping("/act/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String actDoc(
            @PathVariable("id") Integer id)
    {
        userService.makeActive(id);
        return "redirect:/docs/inactive";
    }
}
