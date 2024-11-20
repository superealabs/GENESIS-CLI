package com.labs.webapispring.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.labs.webapispring.models.ExampleTable;
import org.springframework.web.bind.annotation.*;
import com.labs.webapispring.services.ExampleTableService;
import java.util.List;

@RestController
@RequestMapping("/exampletables")
public class ExampleTableController  {
	private final ExampleTableService exampletableService;

	public ExampleTableController(ExampleTableService exampletableService) {
	   this.exampletableService = exampletableService;
	}

	@GetMapping
	public ResponseEntity<List<ExampleTable>> getAllExampleTables() {
	   List<ExampleTable> exampletables = exampletableService.getAllExampleTable();
	   return exampletables.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(exampletables);
	}

	@GetMapping("/{id}")
	public ResponseEntity<ExampleTable> getExampleTableById(@PathVariable Long id) {
	   ExampleTable exampletable = exampletableService.getExampleTableById(id);
	   return ResponseEntity.ok(exampletable);
	}

	@PostMapping
	public ResponseEntity<ExampleTable> createExampleTable(@RequestBody ExampleTable exampletable) {
	   ExampleTable newExampleTable = exampletableService.createExampleTable(exampletable);
	   return ResponseEntity.status(HttpStatus.CREATED).body(newExampleTable);
	}

	@PutMapping("/{id}")
	public ResponseEntity<ExampleTable> updateExampleTable(@PathVariable Long id, @RequestBody ExampleTable exampletable) {
	   ExampleTable updateExampleTable = exampletableService.updateExampleTable(id, exampletable);
	   return ResponseEntity.ok(updateExampleTable);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteExampleTableById(@PathVariable Long id) {
	   exampletableService.deleteExampleTable(id);
	   return ResponseEntity.noContent().build();
	}

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
	}

}
