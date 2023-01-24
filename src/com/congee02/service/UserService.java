package com.congee02.service;

import com.congee02.spring.annotation.Autowired;
import com.congee02.spring.annotation.Component;
import com.congee02.spring.annotation.Scope;
import com.congee02.spring.enums.ScopeEnum;

/**
 * @author congee(congee02 @ 163.com)
 * @date 1/24/2023 3:23 PM
 */
@Component("userService")
@Scope(ScopeEnum.PROTOTYPE)
public class UserService {

    @Autowired
    private OrderService orderService;

    public void test() {
        System.out.println(orderService);
    }

}
