package com.labs.webapispring.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.labs.webapispring.models.Home;


public interface HomeRepository extends JpaRepository<Home, Long> {
}
