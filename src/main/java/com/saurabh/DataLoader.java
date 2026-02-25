package com.saurabh;

import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.saurabh.entity.Job;
import com.saurabh.repository.jobRepository;

@Component
public class DataLoader implements CommandLineRunner {


    @Autowired
    private jobRepository jobRepo;

    @Override
    public void run(String... args) {
    	
    	
  //  //	
// //   	        "saurabh@gmail.com", "B.Tech", "Java, Spring Boot");
//
//    	Candidate c2 = new Candidate(2, "Rahul", "9876543211", "1 Year",
  //  	        "rahul@gmail.com", "BCA", "React, JavaScript");
////
////    	Candidate c3 = new Candidate(3, "Amit", "9876543212", "3 Years",
////    	        "amit@gmail.com", "MCA", "Python, Django");
////
////    	Candidate c4 = new Candidate(4, "Neha", "9876543213", "2 Years",
////    	        "neha@gmail.com", "B.Tech", "SQL, Data Analysis");
////
////    	Candidate c5 = new Candidate(5, "Priya", "9876543214", "4 Years",
//    	        "priya@gmail.com", "MBA", "Project Management");
//
//    	Candidate c6 = new Candidate(6, "Ankit", "9876543215", "1 Year",
////    	        "ankit@gmail.com", "BCA", "Android, Kotlin");
////
////    	Candidate c7 = new Candidate(7, "Riya", "9876543216", "3 Years",
//////    	        "riya@gmail.com", "M.Tech", "AWS, DevOps");
//////
//////    	Candidate c8 = new Candidate(8, "Karan", "9876543217", "2 Years",
//////    	        "karan@gmail.com", "B.Tech", "Testing, Selenium");
//////
//////    	Candidate c9 = new Candidate(9, "Simran", "9876543218", "5 Years",
//////    	        "simran@gmail.com", "MCA", "AI, ML");
//////
  ////  	Candidate c10 = new Candidate(10, "Vikas", "9876543219", "2 Years",
//  //  	        "vikas@gmail.com", "B.Tech", "Cloud, Azure");
    //    
    //	candidateRepo.saveAll(List.of(c1,c2,c3,c4,c5,c6,c7,c8,c9,c10));
  //      
        Job j1 = new Job(1, "Java Developer", "TCS", "Delhi", "6 LPA", "Full-Time",
                "Spring Boot", "Backend development role",
                LocalDate.now(), LocalDate.now().plusDays(30));

        Job j2 = new Job(2, "Frontend Developer", "Infosys", "Noida", "5 LPA", "Full-Time",
                "React", "UI development role",
                LocalDate.now(), LocalDate.now().plusDays(25));

        Job j3 = new Job(3, "Data Analyst", "Wipro", "Gurgaon", "7 LPA", "Full-Time",
                "SQL", "Data analysis role",
                LocalDate.now(), LocalDate.now().plusDays(20));

        Job j4 = new Job(4, "Python Developer", "HCL", "Delhi", "6.5 LPA", "Full-Time",
                "Django", "Backend Python role",
                LocalDate.now(), LocalDate.now().plusDays(40));

        Job j5 = new Job(5, "DevOps Engineer", "Accenture", "Noida", "8 LPA", "Full-Time",
                "AWS", "CI/CD pipeline management",
                LocalDate.now(), LocalDate.now().plusDays(35));

        Job j6 = new Job(6, "QA Tester", "Capgemini", "Gurgaon", "4 LPA", "Full-Time",
                "Manual Testing", "Software testing role",
                LocalDate.now(), LocalDate.now().plusDays(15));

        Job j7 = new Job(7, "Android Developer", "Tech Mahindra", "Delhi", "5.5 LPA", "Full-Time",
                "Kotlin", "Mobile app development",
                LocalDate.now(), LocalDate.now().plusDays(28));

        Job j8 = new Job(8, "UI/UX Designer", "Zoho", "Noida", "5 LPA", "Full-Time",
                "Figma", "Designing user interfaces",
                LocalDate.now(), LocalDate.now().plusDays(22));

        Job j9 = new Job(9, "Cloud Engineer", "IBM", "Gurgaon", "9 LPA", "Full-Time",
                "Azure", "Cloud infrastructure management",
                LocalDate.now(), LocalDate.now().plusDays(45));

        Job j10 = new Job(10, "AI Engineer", "Microsoft", "Delhi", "12 LPA", "Full-Time",
                "Machine Learning", "AI model development",
                LocalDate.now(), LocalDate.now().plusDays(50));
        jobRepo.save(j1);
        jobRepo.save(j2);
        jobRepo.save(j3);
        jobRepo.save(j4);
        jobRepo.save(j5);
        jobRepo.save(j6);
        jobRepo.save(j7);
        jobRepo.save(j8);
        jobRepo.save(j9);
        jobRepo.save(j10);
    }
}