package com.saurabh.Controller;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.InstanceSupplier;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link JobController}.
 */
@Generated
public class JobController__BeanDefinitions {
  /**
   * Get the bean definition for 'jobController'.
   */
  public static BeanDefinition getJobControllerBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(JobController.class);
    InstanceSupplier<JobController> instanceSupplier = InstanceSupplier.using(JobController::new);
    instanceSupplier = instanceSupplier.andThen(JobController__Autowiring::apply);
    beanDefinition.setInstanceSupplier(instanceSupplier);
    return beanDefinition;
  }
}
