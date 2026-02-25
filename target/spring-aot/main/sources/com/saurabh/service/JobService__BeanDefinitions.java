package com.saurabh.service;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.InstanceSupplier;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link JobService}.
 */
@Generated
public class JobService__BeanDefinitions {
  /**
   * Get the bean definition for 'jobService'.
   */
  public static BeanDefinition getJobServiceBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(JobService.class);
    InstanceSupplier<JobService> instanceSupplier = InstanceSupplier.using(JobService::new);
    instanceSupplier = instanceSupplier.andThen(JobService__Autowiring::apply);
    beanDefinition.setInstanceSupplier(instanceSupplier);
    return beanDefinition;
  }
}
