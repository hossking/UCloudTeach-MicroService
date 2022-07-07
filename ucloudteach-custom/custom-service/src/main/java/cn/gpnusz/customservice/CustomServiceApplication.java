package cn.gpnusz.customservice;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@EnableDubbo
@MapperScan("cn.gpnusz.customservice.mapper")
@ServletComponentScan
public class CustomServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustomServiceApplication.class, args);
    }

}
