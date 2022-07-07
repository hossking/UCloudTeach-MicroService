package cn.gpnusz.ucloudteachorder;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("cn.gpnusz.ucloudteachorder.mapper")
public class UcloudteachOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(UcloudteachOrderApplication.class, args);
    }

}
