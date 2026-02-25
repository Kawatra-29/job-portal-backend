package com.saurabh;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.InstanceSupplier;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link DataLoader}.
 */
@Generated
public class DataLoader__BeanDefinitions {
  /**
   * Get the bean definition for 'dataLoader'.
   */
  public static BeanDefinition getDataLoaderBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(DataLoader.class);
    InstanceSupplier<DataLoader> instanceSupplier = InstanceSupplier.using(DataLoader::new);
    instanceSupplier = instanceSupplier.andThen(DataLoader__Autowiring::apply);
    beanDefinition.setInstanceSupplier(instanceSupplier);
    return beanDefinition;
  }
}
