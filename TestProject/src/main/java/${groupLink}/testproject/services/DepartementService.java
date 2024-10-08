package java..departement.services

import com.testproject.models.Departement;
import org.springframework.stereotype.Service;
import com.testproject.repositories.DepartementRepository;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class DepartementService {
	private final DepartementRepository departementRepository;

	@Autowired
	public DepartementService(DepartementRepository departementRepository) {
	   this.departementRepository = departementRepository;
	}

	public List<Departement> getAllDepartement() {
	   return departementRepository.findAll();
	}

	public Departement getDepartementById(Long id) {
	   return departementRepository.findById(id);
	}

	public Departement createDepartement(Departement departement) {
	   return departementRepository.save(departement);
	}

	public Departement updateDepartement(Long id, Departement departement) {
	   Departement existingDepartement = departementRepository.findById(id);
	   existingDepartement = departement;
	   existingDepartement.setIdDepartement(id);
	   return departementRepository.save(existingdepartement);
	}

	public void deleteDepartement(Long id) {
	   departementRepository.deleteById(id);
	}

}

