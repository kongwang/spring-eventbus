package com.kong.spring.eventbus;

import java.lang.annotation.*;

/**
 * @author: kong wang
 * @date: 2019/5/31
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Subscribe {
}
