package labs.layout1.services;

import labs.layout1.models.Employe;
import org.springframework.stereotype.Service;
import labs.layout1.repositories.EmployeRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeService {
    private final EmployeRepository employeRepository;

    @Autowired
    public EmployeService(EmployeRepository employeRepository) {
        this.employeRepository = employeRepository;
    }

    public List<Employe> getAllEmploye() {
        return employeRepository.findAll();
    }

    public Employe getEmployeById(Long id) {
        Optional<Employe> employe = employeRepository.findById(id);
        if (employe.isPresent()) {
            return employe.get();
        } else {
            throw new RuntimeException("Employe not found with id " + id);
        }
    }

    public Employe createEmploye(Employe employe) {
        return employeRepository.save(employe);
    }

    public Employe updateEmploye(Long id, Employe employe) {
        Optional<Employe> existingEmploye = employeRepository.findById(id);
        if (existingEmploye.isPresent()) {
            employe.setEmployeid(id);
            return employeRepository.save(employe);
        } else {
            throw new RuntimeException("Employe not found with id " + id);
        }
    }

    public void deleteEmploye(Long id) {
        employeRepository.deleteById(id);
    }

}

