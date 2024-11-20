package com.labs.webapispring.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.labs.webapispring.models.Testing;


public interface TestingRepository extends JpaRepository<Testing, Long> {
}
