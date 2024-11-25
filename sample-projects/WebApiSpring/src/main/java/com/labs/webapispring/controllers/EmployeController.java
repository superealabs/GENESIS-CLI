package com.labs.webapispring.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.labs.webapispring.models.Employe;
import org.springframework.web.bind.annotation.*;
import com.labs.webapispring.services.EmployeService;

import java.util.List;

@RestController
@RequestMapping("/employes")
public class EmployeController {
    private final EmployeService employeService;

    public EmployeController(EmployeService employeService) {
        this.employeService = employeService;
    }

    @GetMapping
    public ResponseEntity<List<Employe>> getAllEmployes() {
        List<Employe> employes = employeService.getAllEmploye();
        return employes.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(employes);
    }

    @GetMapping("/{employeId}")
    public ResponseEntity<Employe> getEmployeById(@PathVariable Long employeId) {
        Employe employe = employeService.getEmployeById(employeId);
        return ResponseEntity.ok(employe);
    }

    @PostMapping
    public ResponseEntity<Employe> createEmploye(@RequestBody Employe employe) {
        Employe newEmploye = employeService.createEmploye(employe);
        return ResponseEntity.status(HttpStatus.CREATED).body(newEmploye);
    }

    @PutMapping("/{employeId}")
    public ResponseEntity<Employe> updateEmploye(@PathVariable Long employeId, @RequestBody Employe employe) {
        Employe updateEmploye = employeService.updateEmploye(employeId, employe);
        return ResponseEntity.ok(updateEmploye);
    }

    @DeleteMapping("/{employeId}")
    public ResponseEntity<Void> deleteEmployeById(@PathVariable Long employeId) {
        employeService.deleteEmploye(employeId);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

}
