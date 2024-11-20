package com.labs.webapispring.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.labs.webapispring.models.Departement;


public interface DepartementRepository extends JpaRepository<Departement, Long> {
}
