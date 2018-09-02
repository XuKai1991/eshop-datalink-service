package com.eshop.eshopdatalinkservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;

@SpringBootApplication
@EnableFeignClients
@EnableHystrix
@EnableCaching
public class EshopDatalinkServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EshopDatalinkServiceApplication.class, args);
    }
}
