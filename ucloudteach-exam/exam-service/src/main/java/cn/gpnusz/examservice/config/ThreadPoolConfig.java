package cn.gpnusz.examservice.config;

import cn.gpnusz.examservice.util.SnowFlake;
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
 * @description 并发批阅试卷的线程池配置
 * @date 2022/4/6 - 21:49
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

    @Bean("ExamCheckThreadPool")
    public ThreadPoolExecutor initThreadPool() {
        MDC.put("LOG_ID", String.valueOf(snowFlake.nextId()));
        return new ThreadPoolExecutor(core, maximum, keepAlive,
                TimeUnit.SECONDS, new ArrayBlockingQueue<>(maximum * 10),
                r -> new Thread(r, "Check-Exam-Thread"));
    }
}
