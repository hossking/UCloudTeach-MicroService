package cn.gpnusz.customweb;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDubbo
public class CustomWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustomWebApplication.class, args);
    }

}
