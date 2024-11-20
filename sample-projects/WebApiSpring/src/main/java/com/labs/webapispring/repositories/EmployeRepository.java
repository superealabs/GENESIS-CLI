package com.labs.webapispring.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.labs.webapispring.models.Employe;


public interface EmployeRepository extends JpaRepository<Employe, Long> {
}
