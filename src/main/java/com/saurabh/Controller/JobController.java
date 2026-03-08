package com.saurabh.Controller;

//import java.util.List;
//import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
//import com.saurabh.entity.Job;
//import com.saurabh.service.JobService;

@RestController
@RequestMapping("/jobs")
public class JobController {

	@Autowired // to inject jobservice bean into this class(DI)
	//private JobService jobService;

	@PreAuthorize("hasAnyRole('ADMIN','RECRUITER')")
	@PostMapping("/jobs")
	public String createJob(){
		return null;
	}

}













//JOB APIs — /api/jobs
//5. POST /api/jobs — Create a new job posting (EMPLOYER/ADMIN)
//6. GET /api/jobs — Get all job listings (public)
//7. GET /api/jobs/{id} — Get single job details (public)
//8. PUT /api/jobs/{id} — Update job posting (EMPLOYER/ADMIN)
//9. DELETE /api/jobs/{id} — Delete a job posting (EMPLOYER/ADMIN)
//10. GET /api/jobs/search?title=&location=&skill= — Search/filter jobs (public)

