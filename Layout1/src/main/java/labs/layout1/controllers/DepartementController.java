package labs.layout1.controllers;

import org.springframework.ui.Model;
import labs.layout1.models.Departement;
import labs.layout1.services.DepartementService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/departements")
public class DepartementController  {
	private final DepartementService departementService;

	public DepartementController(DepartementService departementService) {
	    this.departementService = departementService;
	}

	@GetMapping
	public String getAllDepartements(Model model) {
	    List<Departement> departements = departementService.getAllDepartement();
	    model.addAttribute("departements", departements);
	    return "departements/list-departement";
	}

	@GetMapping("/create-departement")
	public String returnDepartements(Model model) {
	    return "create-departement";
	}

	@GetMapping("/{id}")
	public String getDepartementById(@PathVariable Long id, Model model) {
	    Departement departement = departementService.getDepartementById(id);
	    model.addAttribute("departement", departement);
	    return "departements/view-list-departement";
	}

	@PostMapping
	public String createDepartement(@RequestBody Departement departement, Model model) {
	    Departement newDepartement = departementService.createDepartement(departement);
	    model.addAttribute("newDepartement", newDepartement);
	    return "departements/create-list-departement";
	}

	@PutMapping("/{id}")
	public String updateDepartement(@PathVariable Long id, @RequestBody Departement departement, Model model) {
	    Departement updateDepartement = departementService.updateDepartement(id, departement);
	    model.addAttribute("updateDepartement", updateDepartement);
	    return "departements/update-list-departement";
	}

	@DeleteMapping("/{id}")
	public String deleteDepartementById(@PathVariable Long id, Model model) {
	    departementService.deleteDepartement(id);
	    model.addAttribute("message", "Departement deleted successfully");
	    return "redirect:/departements";
	}

}

