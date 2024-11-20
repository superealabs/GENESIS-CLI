package com.labs.webapispring.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.labs.webapispring.models.Testing;
import org.springframework.web.bind.annotation.*;
import com.labs.webapispring.services.TestingService;
import java.util.List;

@RestController
@RequestMapping("/testings")
public class TestingController  {
	private final TestingService testingService;

	public TestingController(TestingService testingService) {
	   this.testingService = testingService;
	}

	@GetMapping
	public ResponseEntity<List<Testing>> getAllTestings() {
	   List<Testing> testings = testingService.getAllTesting();
	   return testings.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(testings);
	}

	@GetMapping("/{id}")
	public ResponseEntity<Testing> getTestingById(@PathVariable Long id) {
	   Testing testing = testingService.getTestingById(id);
	   return ResponseEntity.ok(testing);
	}

	@PostMapping
	public ResponseEntity<Testing> createTesting(@RequestBody Testing testing) {
	   Testing newTesting = testingService.createTesting(testing);
	   return ResponseEntity.status(HttpStatus.CREATED).body(newTesting);
	}

	@PutMapping("/{id}")
	public ResponseEntity<Testing> updateTesting(@PathVariable Long id, @RequestBody Testing testing) {
	   Testing updateTesting = testingService.updateTesting(id, testing);
	   return ResponseEntity.ok(updateTesting);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteTestingById(@PathVariable Long id) {
	   testingService.deleteTesting(id);
	   return ResponseEntity.noContent().build();
	}

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
	}

}
