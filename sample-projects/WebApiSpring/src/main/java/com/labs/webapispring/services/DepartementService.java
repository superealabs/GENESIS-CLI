package com.labs.webapispring.services;

import org.springframework.data.domain.Sort;
import com.labs.webapispring.models.Departement;
import org.springframework.stereotype.Service;
import com.labs.webapispring.repositories.DepartementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Optional;

@Service
public class DepartementService  {
	private final DepartementRepository departementRepository;

	@Autowired
	public DepartementService(DepartementRepository departementRepository) {
	   this.departementRepository = departementRepository;
	}

	public List<Departement> getAllDepartement() {
	   return departementRepository.findAll(Sort.by(Sort.Direction.ASC, "deptId"));
	}

	public Departement getDepartementById(Long deptId) {
	   Optional<Departement> departement = departementRepository.findById(deptId);
	   if (departement.isPresent()) {
	     return departement.get();
	   } else {
	     throw new RuntimeException("Departement not found with deptId : " + deptId);
	   }
	}

	public Departement createDepartement(Departement departement) {
	   return departementRepository.save(departement);
	}

	public Departement updateDepartement(Long deptId, Departement departement) {
	   Optional<Departement> existingDepartement = departementRepository.findById(deptId);
			if (existingDepartement.isPresent()) {
			    departement.setDeptId(deptId);
	           return departementRepository.save(departement);
	       } else {
	         throw new RuntimeException("Departement not found with deptId : " + deptId);
	       }
	}

	public void deleteDepartement(Long deptId) {
	   departementRepository.deleteById(deptId);
	}

}
