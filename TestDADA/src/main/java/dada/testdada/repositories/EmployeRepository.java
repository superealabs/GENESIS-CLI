package dada.testdada.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import dada.testdada.models.Employe;


public interface EmployeRepository extends JpaRepository<Employe, Long> {
}

