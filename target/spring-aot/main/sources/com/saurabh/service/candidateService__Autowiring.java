package com.saurabh.service;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.AutowiredFieldValueResolver;
import org.springframework.beans.factory.support.RegisteredBean;

/**
 * Autowiring for {@link candidateService}.
 */
@Generated
public class candidateService__Autowiring {
  /**
   * Apply the autowiring.
   */
  public static candidateService apply(RegisteredBean registeredBean, candidateService instance) {
    AutowiredFieldValueResolver.forRequiredField("candidateRepository").resolveAndSet(registeredBean, instance);
    AutowiredFieldValueResolver.forRequiredField("mapper").resolveAndSet(registeredBean, instance);
    return instance;
  }
}
