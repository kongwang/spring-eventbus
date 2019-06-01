package com.kong.spring.eventbus.test;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @auther:kong
 * @date :2019/6/2
 * @descption:
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(EventBusConfigurationSelector.class)
public @interface EnableEventBus {
}
