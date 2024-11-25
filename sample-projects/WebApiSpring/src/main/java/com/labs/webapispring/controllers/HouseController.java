package com.labs.webapispring.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.labs.webapispring.models.House;
import org.springframework.web.bind.annotation.*;
import com.labs.webapispring.services.HouseService;

import java.util.List;

@RestController
@RequestMapping("/houses")
public class HouseController {
    private final HouseService houseService;

    public HouseController(HouseService houseService) {
        this.houseService = houseService;
    }

    @GetMapping
    public ResponseEntity<List<House>> getAllHouses() {
        List<House> houses = houseService.getAllHouse();
        return houses.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(houses);
    }

    @GetMapping("/{houseId}")
    public ResponseEntity<House> getHouseById(@PathVariable Long houseId) {
        House house = houseService.getHouseById(houseId);
        return ResponseEntity.ok(house);
    }

    @PostMapping
    public ResponseEntity<House> createHouse(@RequestBody House house) {
        House newHouse = houseService.createHouse(house);
        return ResponseEntity.status(HttpStatus.CREATED).body(newHouse);
    }

    @PutMapping("/{houseId}")
    public ResponseEntity<House> updateHouse(@PathVariable Long houseId, @RequestBody House house) {
        House updateHouse = houseService.updateHouse(houseId, house);
        return ResponseEntity.ok(updateHouse);
    }

    @DeleteMapping("/{houseId}")
    public ResponseEntity<Void> deleteHouseById(@PathVariable Long houseId) {
        houseService.deleteHouse(houseId);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

}
