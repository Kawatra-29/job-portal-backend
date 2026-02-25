package com.saurabh.service;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.InstanceSupplier;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link AppService}.
 */
@Generated
public class AppService__BeanDefinitions {
  /**
   * Get the bean definition for 'appService'.
   */
  public static BeanDefinition getAppServiceBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(AppService.class);
    InstanceSupplier<AppService> instanceSupplier = InstanceSupplier.using(AppService::new);
    instanceSupplier = instanceSupplier.andThen(AppService__Autowiring::apply);
    beanDefinition.setInstanceSupplier(instanceSupplier);
    return beanDefinition;
  }
}
