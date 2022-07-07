package cn.gpnusz.courseservice.config;

import cn.gpnusz.courseservice.util.SnowFlake;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author h0ss
 * @description 查询课程详情的线程池配置
 * @date 2022/3/20 - 23:25
 */
@Configuration
public class ThreadPoolConfig {

    @Value("${ThreadPool.core}")
    private int core;

    @Value("${ThreadPool.maximum}")
    private int maximum;

    @Value("${ThreadPool.keep-alive}")
    private long keepAlive;

    @Resource
    private SnowFlake snowFlake;

    @Bean("CourseDetailThreadPool")
    public ThreadPoolExecutor initThreadPool() {
        MDC.put("LOG_ID", String.valueOf(snowFlake.nextId()));
        return new ThreadPoolExecutor(core, maximum, keepAlive,
                TimeUnit.SECONDS, new ArrayBlockingQueue<>(maximum * 10),
                r -> new Thread(r, "Compute-Course-Detail-Thread"));
    }
}
