package com.congee02.spring.bean;

import com.congee02.spring.enums.ScopeEnum;

/** bean 的定义
 * @author congee(congee02 @ 163.com)
 * @date 1/24/2023 3:18 PM
 */
public class BeanDefinition {

    /**
     * bean 的类型
     */
    private Class type;

    /**
     * bean 的作用域 {@link ScopeEnum}
     */
    private ScopeEnum scope;


    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public ScopeEnum getScope() {
        return scope;
    }

    public void setScope(ScopeEnum scope) {
        this.scope = scope;
    }
}
