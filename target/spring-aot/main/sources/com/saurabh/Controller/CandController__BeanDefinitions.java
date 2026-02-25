package com.saurabh.Controller;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.InstanceSupplier;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link CandController}.
 */
@Generated
public class CandController__BeanDefinitions {
  /**
   * Get the bean definition for 'candController'.
   */
  public static BeanDefinition getCandControllerBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(CandController.class);
    InstanceSupplier<CandController> instanceSupplier = InstanceSupplier.using(CandController::new);
    instanceSupplier = instanceSupplier.andThen(CandController__Autowiring::apply);
    beanDefinition.setInstanceSupplier(instanceSupplier);
    return beanDefinition;
  }
}
