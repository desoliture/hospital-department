package com.kozka.hospitaldepartment.controllers;

import com.kozka.hospitaldepartment.entities.Assignment;
import com.kozka.hospitaldepartment.entities.AssignmentType;
import com.kozka.hospitaldepartment.entities.UserRole;
import com.kozka.hospitaldepartment.exceptions.ForbiddenException;
import com.kozka.hospitaldepartment.services.AssignmentService;
import com.kozka.hospitaldepartment.services.UserService;
import com.kozka.hospitaldepartment.utils.ControllersUtil;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDateTime;

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
    public String getAssignments(
            Model model,
            @PageableDefault(
                    sort = {"assignmentDate"},
                    direction = Sort.Direction.DESC,
                    size = 1
            ) Pageable pageable
    ) {
        var user = userService.getCurrentLoggedUser();
        Page<Assignment> page = null;

        if (user.getUserRole() == UserRole.ADMIN) {
            page = assignmentService.getAll(pageable);
        } else if (user.getUserRole() == UserRole.PATIENT) {
            page = assignmentService.getAllForPatient(pageable, user);
        }
//        else {
//            page = null;
//        }

        model.addAttribute("current_logged_in", user);
        model.addAttribute("page", page);
        model.addAttribute("current_date", LocalDateTime.now());
        return "patient/assignments";
    }

    @GetMapping("/for-me")
    @PreAuthorize("hasAnyAuthority('DOCTOR', 'NURSE')")
    public String getAssignmentsForMe(
            @PageableDefault(
                    sort = {"assignmentDate"},
                    direction = Sort.Direction.DESC,
                    size = 1
            ) Pageable pageable,
            Model model
    ) {
        var current = userService.getCurrentLoggedUser();

//        if (current.getUserRole() == UserRole.ADMIN) return "redirect:/assignments";

        Page<Assignment> page = assignmentService.getAllForDoctor(pageable, current);

        model.addAttribute("current_logged_in", current);
        model.addAttribute("page", page);
        model.addAttribute("current_date", LocalDateTime.now());

        return "staff/assignments-for-me";
    }

    @GetMapping("/by-me")
    @PreAuthorize("hasAnyAuthority('DOCTOR', 'NURSE')")
    public String getAssignmentsByMe(
            @PageableDefault(
                    sort = {"assignmentDate"},
                    direction = Sort.Direction.DESC,
                    size = 1
            ) Pageable pageable,
            Model model
    ) {
        var current = userService.getCurrentLoggedUser();

        Page<Assignment> page = assignmentService.getAllByDoctor(pageable, current);

        model.addAttribute("current_logged_in", current);
        model.addAttribute("page", page);
        model.addAttribute("current_date", LocalDateTime.now());

        return "staff/assignments-by-me";
    }

    @GetMapping("/add")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'DOCTOR')")
    public String addAssignments(Model model) {
        model.mergeAttributes(ControllersUtil
                .assignmentPutAllInModel(
                        userService, new Assignment()
                )
        );

        return "staff/assignments-add";
    }

    @PostMapping("/add")
    public String addAssignmentsPost(
            @Valid @ModelAttribute("assignment") Assignment assg,
            BindingResult bindingResult,
            @ModelAttribute("pat-id") Integer patId,
            @ModelAttribute("asg-id-all") Integer asgIdAll,
            @ModelAttribute("asg-id-doc") Integer asgIdDoc,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.mergeAttributes(
                    ControllersUtil.assignmentPutAllInModel(
                            userService, assg
                    )
            );

            var errors = ControllersUtil.getErrorsMap(bindingResult);
            model.addAttribute("errors_map", errors);

            return "staff/assignments-add";
        }

        ControllersUtil.setAssignmentDetails(
                assg, userService, asgIdAll, asgIdDoc, patId
        );


        assg.setAssigner(userService.getCurrentLoggedUser());
        assg.setCompleted(false);
        assg.setCreationDate(LocalDateTime.now());


        assignmentService.save(assg);

        return "redirect:/assignments";
    }

    @GetMapping("/{id}/edit")
    public String editAssignment(
            @PathVariable("id") Integer id,
            @PathVariable("id") Assignment assg,
            Model model
    ) {
        assg.setId(id);
        model.mergeAttributes(
                ControllersUtil.assignmentPutAllInModel(
                        userService, assg
                )
        );

        return "staff/assignments-edit";
    }

    @PostMapping("/{id}/edit")
    public String editAssignmentPost(
            @Valid @ModelAttribute("assignment") Assignment assg,
            BindingResult bindingResult,
            Model model,
            @PathVariable("id") Integer id,
            @ModelAttribute("pat-id") Integer patId,
            @ModelAttribute("asg-id-all") Integer asgIdAll,
            @ModelAttribute("asg-id-doc") Integer asgIdDoc
    ) {

        ControllersUtil.setAssignmentDetails(
                assg, userService, asgIdAll, asgIdDoc, patId
        );

        assg.setId(id);

        if (!bindingResult.hasErrors()) {
            assignmentService.update(assg);
            return "redirect:/assignments";
        }

        model.mergeAttributes(
                ControllersUtil.assignmentPutAllInModel(
                        userService, assg
                )
        );

        var errorsMap = ControllersUtil.getErrorsMap(bindingResult);
        model.addAttribute("errors_map", errorsMap);

        return "staff/assignments-edit";
    }

    @GetMapping("/{id}/hold")
    @PreAuthorize("hasAnyAuthority('DOCTOR', 'NURSE')")
    public String holdAssignment(
            @PathVariable("id") Assignment assg,
            Model model
    ) {
        var current = userService.getCurrentLoggedUser();

        if (!assg.getAssigned().getId().equals(current.getId()))
            throw new ForbiddenException("You have no access!");

        model.addAttribute("assg_to_hold", assg);
        model.addAttribute("current_logged_in", userService.getCurrentLoggedUser());
        return "staff/assignments-hold";
    }

    @PostMapping("/{id}/hold")
    @PreAuthorize("hasAnyAuthority('DOCTOR', 'NURSE')")
    public String holdAssignmentPost(
            @ModelAttribute("assg_to_hold") Assignment assg,
            Model model
    ) {
        if (assg.getConclusion() == null
                || assg.getConclusion().equals("")
        ) {
            model.addAttribute("conc_error", "Conclusion can`t be empty!");
            model.addAttribute("assg_to_hold", assg);
            model.addAttribute("current_logged_in", userService.getCurrentLoggedUser());
            return "staff/assignments-hold";
        }

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
        var current = userService.getCurrentLoggedUser();
        if (!current.getUserRole().name().equals("ADMIN")
                && !(assignment.getAssigner().getId().equals(current.getId())))
            throw new ForbiddenException("You have no access!");

        String referer = request
                .getHeader("Referer")
                .replace("http://localhost:8080", "");

        model.addAttribute("current_logged_in", userService.getCurrentLoggedUser());
        model.addAttribute("assg_to_remove", assignment);
        model.addAttribute("previous_url", referer);
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
