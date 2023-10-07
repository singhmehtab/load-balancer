package com.project.loadbalancer;

import com.project.loadbalancer.life_checker.LifeChecker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class LoadBalancerApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(LoadBalancerApplication.class, args);
        context.getBean("life-checker", LifeChecker.class).checkLifeAndUpdateUrls();
    }

}
