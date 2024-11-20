package com.labs.webapispring.services;

import org.springframework.data.domain.Sort;
import com.labs.webapispring.models.Employe;
import org.springframework.stereotype.Service;
import com.labs.webapispring.repositories.EmployeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Optional;

@Service
public class EmployeService  {
	private final EmployeRepository employeRepository;

	@Autowired
	public EmployeService(EmployeRepository employeRepository) {
	   this.employeRepository = employeRepository;
	}

	public List<Employe> getAllEmploye() {
	   return employeRepository.findAll(Sort.by(Sort.Direction.ASC, "employeId"));
	}

	public Employe getEmployeById(Long employeId) {
	   Optional<Employe> employe = employeRepository.findById(employeId);
	   if (employe.isPresent()) {
	     return employe.get();
	   } else {
	     throw new RuntimeException("Employe not found with employeId : " + employeId);
	   }
	}

	public Employe createEmploye(Employe employe) {
	   return employeRepository.save(employe);
	}

	public Employe updateEmploye(Long employeId, Employe employe) {
	   Optional<Employe> existingEmploye = employeRepository.findById(employeId);
			if (existingEmploye.isPresent()) {
			    employe.setEmployeId(employeId);
	           return employeRepository.save(employe);
	       } else {
	         throw new RuntimeException("Employe not found with employeId : " + employeId);
	       }
	}

	public void deleteEmploye(Long employeId) {
	   employeRepository.deleteById(employeId);
	}

}
