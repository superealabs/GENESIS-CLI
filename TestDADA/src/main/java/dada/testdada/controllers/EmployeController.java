package dada.testdada.controllers;

import org.springframework.ui.Model;
import dada.testdada.models.Employe;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import dada.testdada.services.EmployeService;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@Controller
@RequestMapping("/employes")
public class EmployeController  {
	private final EmployeService employeService;

	@Autowired
	public EmployeController(EmployeService employeService) {
	   this.employeService = employeService;
	}

	@GetMapping
	public String getAllEmployes(Model model) {
	   List<Employe> employes = employeService.getAllEmploye();
	   model.addAttribute("employes", employes);
	   return "employes/list-employe";
	}

	@GetMapping("/{id}")
	public String getEmployeById(@PathVariable Long id, Model model) {
	   Employe employe = employeService.getEmployeById(id);
	   model.addAttribute("employe", employe);
	   return "employes/view-list-employe";
	}

	@PostMapping
	public String createEmploye(@RequestBody Employe employe, Model model) {
	   Employe newEmploye = employeService.createEmploye(employe);
	   model.addAttribute("newEmploye", newEmploye);
	   return "employes/create-list-employe";
	}

	@PutMapping("/{id}")
	public String updateEmploye(@PathVariable Long id, @RequestBody Employe employe, Model model) {
	   Employe updateEmploye = employeService.updateEmploye(id, employe);
	   model.addAttribute("updateEmploye", updateEmploye);
	   return "employes/update-list-employe";
	}

	@DeleteMapping("/{id}")
	public String deleteEmployeById(@PathVariable Long id, Model model) {
	   employeService.deleteEmploye(id);
	   model.addAttribute("message", "Employe deleted successfully");
	   return "redirect:/employes";
	}

}

