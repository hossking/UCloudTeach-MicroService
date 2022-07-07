package cn.gpnusz.liveweb;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDubbo
public class LiveWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(LiveWebApplication.class, args);
    }

}
