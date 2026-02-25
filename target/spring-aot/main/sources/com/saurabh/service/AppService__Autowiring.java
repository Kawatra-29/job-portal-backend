package com.saurabh.service;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.AutowiredFieldValueResolver;
import org.springframework.beans.factory.support.RegisteredBean;

/**
 * Autowiring for {@link AppService}.
 */
@Generated
public class AppService__Autowiring {
  /**
   * Apply the autowiring.
   */
  public static AppService apply(RegisteredBean registeredBean, AppService instance) {
    AutowiredFieldValueResolver.forRequiredField("appRepository").resolveAndSet(registeredBean, instance);
    AutowiredFieldValueResolver.forRequiredField("candidateRepository").resolveAndSet(registeredBean, instance);
    AutowiredFieldValueResolver.forRequiredField("jobRepository").resolveAndSet(registeredBean, instance);
    return instance;
  }
}
