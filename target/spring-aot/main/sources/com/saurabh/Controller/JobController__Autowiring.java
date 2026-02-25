package com.saurabh.Controller;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.AutowiredFieldValueResolver;
import org.springframework.beans.factory.support.RegisteredBean;

/**
 * Autowiring for {@link JobController}.
 */
@Generated
public class JobController__Autowiring {
  /**
   * Apply the autowiring.
   */
  public static JobController apply(RegisteredBean registeredBean, JobController instance) {
    AutowiredFieldValueResolver.forRequiredField("jobService").resolveAndSet(registeredBean, instance);
    return instance;
  }
}
