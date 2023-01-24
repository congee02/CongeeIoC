package com.congee02;

import com.congee02.service.UserService;
import com.congee02.spring.AppConfig;
import com.congee02.spring.ioc.CongeeApplicationContext;

/**
 * @author congee(congee02 @ 163.com)
 * @date 1/24/2023 3:13 PM
 */
public class Congee02Application {

    public static void main(String[] args) {
        CongeeApplicationContext context = new CongeeApplicationContext(AppConfig.class);
        ((UserService) context.getBean("userService")).test();
    }

}
