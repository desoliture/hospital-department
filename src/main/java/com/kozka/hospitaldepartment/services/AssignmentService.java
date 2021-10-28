package com.kozka.hospitaldepartment.services;

import com.kozka.hospitaldepartment.entities.Assignment;
import com.kozka.hospitaldepartment.entities.User;
import com.kozka.hospitaldepartment.repositories.AssignmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;

/**
 * @author Kozka Ivan
 */
@Service
public class AssignmentService {
    @Autowired
    AssignmentRepository assgRepo;

    public Page<Assignment> getAll(Pageable pageable) {
        return assgRepo.findAll(pageable);
    }

    private Page<Assignment> getPage(List<Assignment> assignments, Pageable pageable) {
        List<Assignment> list;
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;

        if (assignments.size() < startItem) {
            list = Collections.emptyList();
        } else {
            int toIndex = Math.min(startItem + pageSize, assignments.size());
            list = assignments.subList(startItem, toIndex);
        }

        return new PageImpl<Assignment>(
                list, PageRequest.of(currentPage, pageSize), assignments.size());
    }

    public Page<Assignment> getAllForPatient(Pageable pageable, User user) {
        var allForPatient = getAllForPatient(user);

        return getPage(allForPatient, pageable);
    }

    public Page<Assignment> getAllForDoctor(Pageable pageable, User user) {
        var allForDoctor = getAllFor(user);

        return getPage(allForDoctor, pageable);
    }

    public Page<Assignment> getAllByDoctor(Pageable pageable, User user) {
        var allByDoctor = getAllBy(user);

        return getPage(allByDoctor, pageable);
    }

    public List<Assignment> getAll() {
        return assgRepo.getAll();
    }

    public List<Assignment> getAllForPatient(User user) {
        return assgRepo.getAllByPatient(user);
    }

    public List<Assignment> getAllFor(User user) {
        return assgRepo.getAllByAssigned(user);
    }

    public List<Assignment> getAllBy(User user) {
        return assgRepo.getAllByAssigner(user);
    }

    public void save(Assignment assg) {
        assgRepo.save(assg);
    }

    @Transactional
    public void update(Assignment updated) {
        assgRepo.setAssignmentDetailsById(
                updated.getAssigned(), updated.getAssigner(),
                updated.getPatient(), updated.getAssgType(),
                updated.getAssignmentDate(), updated.getCreationDate(),
                updated.getDescription(), updated.getCompleted(),
                updated.getConclusion(), updated.getId()
        );
    }

    public void remove(Integer id) {
        assgRepo.deleteById(id);
    }

    public List<Assignment> getHealthCardFor(User user) {
        return assgRepo.getHealthCardForUserById(user.getId());
    }
}
