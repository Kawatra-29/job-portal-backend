package com.saurabh.Controller;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.AutowiredFieldValueResolver;
import org.springframework.beans.factory.support.RegisteredBean;

/**
 * Autowiring for {@link CandController}.
 */
@Generated
public class CandController__Autowiring {
  /**
   * Apply the autowiring.
   */
  public static CandController apply(RegisteredBean registeredBean, CandController instance) {
    AutowiredFieldValueResolver.forRequiredField("candidateRepository").resolveAndSet(registeredBean, instance);
    AutowiredFieldValueResolver.forRequiredField("candidateService").resolveAndSet(registeredBean, instance);
    return instance;
  }
}
