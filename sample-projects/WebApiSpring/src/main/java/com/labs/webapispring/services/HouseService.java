package com.labs.webapispring.services;

import org.springframework.data.domain.Sort;
import com.labs.webapispring.models.House;
import org.springframework.stereotype.Service;
import com.labs.webapispring.repositories.HouseRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

@Service
public class HouseService {
    private final HouseRepository houseRepository;

    @Autowired
    public HouseService(HouseRepository houseRepository) {
        this.houseRepository = houseRepository;
    }

    public List<House> getAllHouse() {
        return houseRepository.findAll(Sort.by(Sort.Direction.ASC, "houseId"));
    }

    public House getHouseById(Long houseId) {
        Optional<House> house = houseRepository.findById(houseId);
        if (house.isPresent()) {
            return house.get();
        } else {
            throw new RuntimeException("House not found with houseId : " + houseId);
        }
    }

    public House createHouse(House house) {
        return houseRepository.save(house);
    }

    public House updateHouse(Long houseId, House house) {
        Optional<House> existingHouse = houseRepository.findById(houseId);
        if (existingHouse.isPresent()) {
            house.setHouseId(houseId);
            return houseRepository.save(house);
        } else {
            throw new RuntimeException("House not found with houseId : " + houseId);
        }
    }

    public void deleteHouse(Long houseId) {
        houseRepository.deleteById(houseId);
    }

}
