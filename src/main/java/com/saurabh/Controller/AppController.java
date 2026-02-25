package com.saurabh.Controller;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.saurabh.entity.Application;
import com.saurabh.entity.ApplyDTO;
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
