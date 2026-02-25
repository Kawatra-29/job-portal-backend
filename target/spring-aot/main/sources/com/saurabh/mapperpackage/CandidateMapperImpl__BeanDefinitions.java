package com.saurabh.mapperpackage;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link CandidateMapperImpl}.
 */
@Generated
public class CandidateMapperImpl__BeanDefinitions {
  /**
   * Get the bean definition for 'candidateMapperImpl'.
   */
  public static BeanDefinition getCandidateMapperImplBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(CandidateMapperImpl.class);
    beanDefinition.setInstanceSupplier(CandidateMapperImpl::new);
    return beanDefinition;
  }
}
