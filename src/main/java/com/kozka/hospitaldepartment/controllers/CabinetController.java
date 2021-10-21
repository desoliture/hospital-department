package com.kozka.hospitaldepartment.controllers;

import com.kozka.hospitaldepartment.entities.User;
import com.kozka.hospitaldepartment.services.AssignmentService;
import com.kozka.hospitaldepartment.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Kozka Ivan
 */
@Controller
@RequestMapping("/cab")
public class CabinetController {

    @Autowired
    private AssignmentService assgService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String getCab(Model model) {
        String email = userService.getCurrentAuthEmail();
        User current = userService.getUserByEmail(email);

        model.addAttribute("currentUser", current);
        return "cab";
    }
}
