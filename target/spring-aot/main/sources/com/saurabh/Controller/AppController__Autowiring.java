package com.saurabh.Controller;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.AutowiredFieldValueResolver;
import org.springframework.beans.factory.support.RegisteredBean;

/**
 * Autowiring for {@link AppController}.
 */
@Generated
public class AppController__Autowiring {
  /**
   * Apply the autowiring.
   */
  public static AppController apply(RegisteredBean registeredBean, AppController instance) {
    AutowiredFieldValueResolver.forRequiredField("appService").resolveAndSet(registeredBean, instance);
    return instance;
  }
}
