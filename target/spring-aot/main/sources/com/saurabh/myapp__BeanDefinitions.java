package com.saurabh;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link myapp}.
 */
@Generated
public class myapp__BeanDefinitions {
  /**
   * Get the bean definition for 'myapp'.
   */
  public static BeanDefinition getMyappBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(myapp.class);
    beanDefinition.setInstanceSupplier(myapp::new);
    return beanDefinition;
  }
}
