package com.congee02.spring.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** 声明该类为 bean
 * @author congee(congee02 @ 163.com)
 * @date 1/24/2023 3:15 PM
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Component {

    /**
     * @return bean 的名称
     */
    String value() default "";

}
