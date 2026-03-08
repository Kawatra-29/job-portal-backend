package com.saurabh.Controller;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.saurabh.DTOs.ApplyDTO;
import com.saurabh.entity.Application;
import com.saurabh.service.AppService;

@RestController
@RequestMapping("/applications")
public class AppController {

	@Autowired
	private AppService appService;

	@PostMapping
	public void apply(@RequestBody ApplyDTO app) {
		appService.apply(app);
	}

	@GetMapping("/{id}")
	public Optional<Application> getAppById(@PathVariable int id) {
		return appService.getAppById(id);
	}
}

//APPLICATION APIs — /api/applications
//16. POST /api/applications — Apply for a job (CANDIDATE)
//17. GET /api/applications/my — Get all applications of logged-in candidate (CANDIDATE)
//18. GET /api/applications/{id} — Get application by ID (CANDIDATE/ADMIN)
//19. PUT /api/applications/{id}/status — Update application status: SHORTLISTED/REJECTED/SELECTED (EMPLOYER/ADMIN)
//20. DELETE /api/applications/{id} — Withdraw/delete application (CANDIDATE)
