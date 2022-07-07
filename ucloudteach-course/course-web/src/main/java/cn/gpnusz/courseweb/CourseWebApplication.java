package cn.gpnusz.courseweb;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDubbo
public class CourseWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(CourseWebApplication.class, args);
    }

}
