package com.saurabh.Specification;

import com.saurabh.ENUMS.JobType;
import com.saurabh.ENUMS.WorkMode;
import com.saurabh.ENUMS.JobStatus;
import com.saurabh.Entity.Job;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;
import java.util.ArrayList;
import java.util.List;

public class JobSpecification {

    public static Specification<Job> filter(
            String title,
            String location,
            JobType jobType,
            WorkMode workMode,
            JobStatus status
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (title != null && !title.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%"));
            }
            if (location != null && !location.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("location")), "%" + location.toLowerCase() + "%"));
            }
            if (jobType != null) {
                predicates.add(cb.equal(root.get("jobType"), jobType));
            }
            if (workMode != null) {
                predicates.add(cb.equal(root.get("workMode"), workMode));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}