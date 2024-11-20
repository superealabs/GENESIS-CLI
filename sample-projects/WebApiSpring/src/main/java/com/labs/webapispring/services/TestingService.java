package com.labs.webapispring.services;

import org.springframework.data.domain.Sort;
import com.labs.webapispring.models.Testing;
import org.springframework.stereotype.Service;
import com.labs.webapispring.repositories.TestingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Optional;

@Service
public class TestingService  {
	private final TestingRepository testingRepository;

	@Autowired
	public TestingService(TestingRepository testingRepository) {
	   this.testingRepository = testingRepository;
	}

	public List<Testing> getAllTesting() {
	   return testingRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
	}

	public Testing getTestingById(Long id) {
	   Optional<Testing> testing = testingRepository.findById(id);
	   if (testing.isPresent()) {
	     return testing.get();
	   } else {
	     throw new RuntimeException("Testing not found with id : " + id);
	   }
	}

	public Testing createTesting(Testing testing) {
	   return testingRepository.save(testing);
	}

	public Testing updateTesting(Long id, Testing testing) {
	   Optional<Testing> existingTesting = testingRepository.findById(id);
			if (existingTesting.isPresent()) {
			    testing.setId(id);
	           return testingRepository.save(testing);
	       } else {
	         throw new RuntimeException("Testing not found with id : " + id);
	       }
	}

	public void deleteTesting(Long id) {
	   testingRepository.deleteById(id);
	}

}
