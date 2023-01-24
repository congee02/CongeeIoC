package com.congee02.spring.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author congee(congee02 @ 163.com)
 * @date 1/24/2023 3:25 PM
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ComponentScan {

    /**
     * @return 需要组件扫描的包
     */
    String value() default "";

}
