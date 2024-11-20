package com.labs.webapispring.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.labs.webapispring.models.House;


public interface HouseRepository extends JpaRepository<House, Long> {
}
