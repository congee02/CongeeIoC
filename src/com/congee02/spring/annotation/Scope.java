package com.congee02.spring.annotation;

import com.congee02.spring.enums.ScopeEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** 作用域
 * @author congee(congee02 @ 163.com)
 * @date 1/24/2023 4:17 PM
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Scope {
    /**
     * @return 作用域
     */
    ScopeEnum value() default ScopeEnum.SINGLETON;
}
