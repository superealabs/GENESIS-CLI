package com.labs.webapispring.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.labs.webapispring.models.Departement;
import org.springframework.web.bind.annotation.*;
import com.labs.webapispring.services.DepartementService;
import java.util.List;

@RestController
@RequestMapping("/departements")
public class DepartementController  {
	private final DepartementService departementService;

	public DepartementController(DepartementService departementService) {
	   this.departementService = departementService;
	}

	@GetMapping
	public ResponseEntity<List<Departement>> getAllDepartements() {
	   List<Departement> departements = departementService.getAllDepartement();
	   return departements.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(departements);
	}

	@GetMapping("/{deptId}")
	public ResponseEntity<Departement> getDepartementById(@PathVariable Long deptId) {
	   Departement departement = departementService.getDepartementById(deptId);
	   return ResponseEntity.ok(departement);
	}

	@PostMapping
	public ResponseEntity<Departement> createDepartement(@RequestBody Departement departement) {
	   Departement newDepartement = departementService.createDepartement(departement);
	   return ResponseEntity.status(HttpStatus.CREATED).body(newDepartement);
	}

	@PutMapping("/{deptId}")
	public ResponseEntity<Departement> updateDepartement(@PathVariable Long deptId, @RequestBody Departement departement) {
	   Departement updateDepartement = departementService.updateDepartement(deptId, departement);
	   return ResponseEntity.ok(updateDepartement);
	}

	@DeleteMapping("/{deptId}")
	public ResponseEntity<Void> deleteDepartementById(@PathVariable Long deptId) {
	   departementService.deleteDepartement(deptId);
	   return ResponseEntity.noContent().build();
	}

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
	}

}
