package com.labs.webapispring.services;

import org.springframework.data.domain.Sort;
import com.labs.webapispring.models.ExampleTable;
import org.springframework.stereotype.Service;
import com.labs.webapispring.repositories.ExampleTableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Optional;

@Service
public class ExampleTableService  {
	private final ExampleTableRepository exampleTableRepository;

	@Autowired
	public ExampleTableService(ExampleTableRepository exampleTableRepository) {
	   this.exampleTableRepository = exampleTableRepository;
	}

	public List<ExampleTable> getAllExampleTable() {
	   return exampleTableRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
	}

	public ExampleTable getExampleTableById(Long id) {
	   Optional<ExampleTable> exampleTable = exampleTableRepository.findById(id);
	   if (exampleTable.isPresent()) {
	     return exampleTable.get();
	   } else {
	     throw new RuntimeException("ExampleTable not found with id : " + id);
	   }
	}

	public ExampleTable createExampleTable(ExampleTable exampleTable) {
	   return exampleTableRepository.save(exampleTable);
	}

	public ExampleTable updateExampleTable(Long id, ExampleTable exampleTable) {
	   Optional<ExampleTable> existingExampleTable = exampleTableRepository.findById(id);
			if (existingExampleTable.isPresent()) {
			    exampleTable.setId(id);
	           return exampleTableRepository.save(exampleTable);
	       } else {
	         throw new RuntimeException("ExampleTable not found with id : " + id);
	       }
	}

	public void deleteExampleTable(Long id) {
	   exampleTableRepository.deleteById(id);
	}

}
