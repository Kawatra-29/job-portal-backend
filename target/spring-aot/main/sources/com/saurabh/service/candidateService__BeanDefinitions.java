package com.saurabh.service;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.InstanceSupplier;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link candidateService}.
 */
@Generated
public class candidateService__BeanDefinitions {
  /**
   * Get the bean definition for 'candidateService'.
   */
  public static BeanDefinition getCandidateServiceBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(candidateService.class);
    InstanceSupplier<candidateService> instanceSupplier = InstanceSupplier.using(candidateService::new);
    instanceSupplier = instanceSupplier.andThen(candidateService__Autowiring::apply);
    beanDefinition.setInstanceSupplier(instanceSupplier);
    return beanDefinition;
  }
}
