package cn.gpnusz.uploadweb;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDubbo
public class UploadWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(UploadWebApplication.class, args);
    }

}
