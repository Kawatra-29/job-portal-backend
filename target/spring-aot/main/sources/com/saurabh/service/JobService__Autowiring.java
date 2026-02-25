package com.saurabh.service;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.AutowiredFieldValueResolver;
import org.springframework.beans.factory.support.RegisteredBean;

/**
 * Autowiring for {@link JobService}.
 */
@Generated
public class JobService__Autowiring {
  /**
   * Apply the autowiring.
   */
  public static JobService apply(RegisteredBean registeredBean, JobService instance) {
    AutowiredFieldValueResolver.forRequiredField("jobRepo").resolveAndSet(registeredBean, instance);
    return instance;
  }
}
