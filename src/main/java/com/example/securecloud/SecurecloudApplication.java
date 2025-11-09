package com.example.securecloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.example.securecloud")
@EnableJpaRepositories(basePackages = "com.example.securecloud.repository")
@EntityScan(basePackages = "com.example.securecloud.model")
public class SecurecloudApplication{
    public static void main(String[] args) {
        SpringApplication.run(SecurecloudApplication.class, args);
    }
}
