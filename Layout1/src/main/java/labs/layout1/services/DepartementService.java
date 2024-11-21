package labs.layout1.services;

import labs.layout1.models.Departement;
import org.springframework.stereotype.Service;
import labs.layout1.repositories.DepartementRepository;
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
	    return departementRepository.findAll();
	}

	public Departement getDepartementById(Long id) {
	    Optional<Departement> departement = departementRepository.findById(id);
	    if (departement.isPresent()) {
	       return departement.get();
	    } else {
	       throw new RuntimeException("Departement not found with id " + id);
	    }
	}

	public Departement createDepartement(Departement departement) {
	    return departementRepository.save(departement);
	}

	public Departement updateDepartement(Long id, Departement departement) {
	    Optional<Departement> existingDepartement = departementRepository.findById(id);
	       if (existingDepartement.isPresent()) {
		       departement.setDepartementid(id);
	             return departementRepository.save(departement);
	       } else {
	          throw new RuntimeException("Departement not found with id " + id);
	       }
	}

	public void deleteDepartement(Long id) {
	    departementRepository.deleteById(id);
	}

}



