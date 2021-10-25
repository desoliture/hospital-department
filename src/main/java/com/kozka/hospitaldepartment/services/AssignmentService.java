package com.kozka.hospitaldepartment.services;

import com.kozka.hospitaldepartment.entities.Assignment;
import com.kozka.hospitaldepartment.entities.User;
import com.kozka.hospitaldepartment.repositories.AssignmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

/**
 * @author Kozka Ivan
 */
@Service
public class AssignmentService {
    @Autowired
    AssignmentRepository assgRepo;

    public List<Assignment> getUserMedCard(int id) {
        var assgs =
                assgRepo.getAssignmentsByPatientId(id);
        assgs.sort(Assignment::compareTo);

        return assgs;
    }

    public List<Assignment> getAll() {
        return assgRepo.findAll();
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
}
