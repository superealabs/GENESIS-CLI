package com.labs.webapispring.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.labs.webapispring.models.Home;
import org.springframework.web.bind.annotation.*;
import com.labs.webapispring.services.HomeService;

import java.util.List;

@RestController
@RequestMapping("/homes")
public class HomeController {
    private final HomeService homeService;

    public HomeController(HomeService homeService) {
        this.homeService = homeService;
    }

    @GetMapping
    public ResponseEntity<List<Home>> getAllHomes() {
        List<Home> homes = homeService.getAllHome();
        return homes.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(homes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Home> getHomeById(@PathVariable Long id) {
        Home home = homeService.getHomeById(id);
        return ResponseEntity.ok(home);
    }

    @PostMapping
    public ResponseEntity<Home> createHome(@RequestBody Home home) {
        Home newHome = homeService.createHome(home);
        return ResponseEntity.status(HttpStatus.CREATED).body(newHome);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Home> updateHome(@PathVariable Long id, @RequestBody Home home) {
        Home updateHome = homeService.updateHome(id, home);
        return ResponseEntity.ok(updateHome);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHomeById(@PathVariable Long id) {
        homeService.deleteHome(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

}
