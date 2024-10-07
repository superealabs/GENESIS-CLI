package java..employe.services

import com.testproject.models.Employe;
import org.springframework.stereotype.Service;
import com.testproject.repositories.EmployeRepository;
import org.springframework.beans.factory.annotation.Autowired;

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
	   return employeRepository.findById(id);
	}

	public Employe createEmploye(Employe employe) {
	   return employeRepository.save(employe);
	}

	public Employe updateEmploye(Long id, Employe employe) {
	   Employe existingEmploye = employeRepository.findById(id);
	   existingEmploye = employe;
	   existingEmploye.setIdEmploye(id);
	   return employeRepository.save(existingemploye);
	}

	public void deleteEmploye(Long id) {
	   employeRepository.deleteById(id);
	}

}

