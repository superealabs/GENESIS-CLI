package com.labs.webapispring.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.labs.webapispring.models.ExampleTable;


public interface ExampleTableRepository extends JpaRepository<ExampleTable, Long> {
}
