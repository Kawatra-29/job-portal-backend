package com.saurabh.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.lang.String;
import org.springframework.aot.generate.Generated;
import org.springframework.data.jpa.repository.aot.AotRepositoryFragmentSupport;
import org.springframework.data.jpa.repository.query.QueryEnhancerSelector;
import org.springframework.data.repository.core.support.RepositoryFactoryBeanSupport;

/**
 * AOT generated JPA repository implementation for {@link candidateRepository}.
 */
@Generated
public class candidateRepositoryImpl__AotRepository extends AotRepositoryFragmentSupport {
  private final RepositoryFactoryBeanSupport.FragmentCreationContext context;

  private final EntityManager entityManager;

  public candidateRepositoryImpl__AotRepository(EntityManager entityManager,
      RepositoryFactoryBeanSupport.FragmentCreationContext context) {
    super(QueryEnhancerSelector.DEFAULT_SELECTOR, context);
    this.entityManager = entityManager;
    this.context = context;
  }

  /**
   * AOT generated implementation of {@link candidateRepository#existsByEmail(java.lang.String)}.
   */
  public boolean existsByEmail(String email) {
    String queryString = "SELECT c.candidateid FROM Candidate c WHERE c.email = :email";
    Query query = this.entityManager.createQuery(queryString);
    query.setParameter("email", email);
    query.setMaxResults(1);

    return !query.getResultList().isEmpty();
  }
}
