package dada.testdada.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import dada.testdada.models.Departement;


public interface DepartementRepository extends JpaRepository<Departement, Long> {
}

