package com.kong.spring.eventbus;

import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author:kong
 * @date :2019/6/2
 * @descption:
 */
public class EventBusConfigurationSelector implements DeferredImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata annotationMetadata) {
        return new String[]{EventBusBootstrapConfiguration.class.getName()};
    }
}
