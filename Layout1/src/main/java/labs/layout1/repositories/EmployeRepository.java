package labs.layout1.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import labs.layout1.models.Employe;


public interface EmployeRepository extends JpaRepository<Employe, Long> {
}

