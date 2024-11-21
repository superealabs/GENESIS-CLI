package labs.layout1.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import labs.layout1.models.Departement;


public interface DepartementRepository extends JpaRepository<Departement, Long> {
}



