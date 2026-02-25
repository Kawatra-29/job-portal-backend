package com.saurabh.Controller;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.InstanceSupplier;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link AppController}.
 */
@Generated
public class AppController__BeanDefinitions {
  /**
   * Get the bean definition for 'appController'.
   */
  public static BeanDefinition getAppControllerBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(AppController.class);
    InstanceSupplier<AppController> instanceSupplier = InstanceSupplier.using(AppController::new);
    instanceSupplier = instanceSupplier.andThen(AppController__Autowiring::apply);
    beanDefinition.setInstanceSupplier(instanceSupplier);
    return beanDefinition;
  }
}
