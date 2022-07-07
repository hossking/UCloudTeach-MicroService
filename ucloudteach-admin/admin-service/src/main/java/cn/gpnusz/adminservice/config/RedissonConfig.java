package cn.gpnusz.adminservice.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author h0ss
 * @description redisson配置类
 * @date 2022/5/7 - 21:28
 */
@Configuration
public class RedissonConfig {

    @Value("${sa-token.alone-redis.host}")
    private String host;

    @Value("${sa-token.alone-redis.port}")
    private Integer port;

    @Value("${sa-token.alone-redis.password}")
    private String password;

    @Value("${sa-token.alone-redis.database}")
    private Integer db;

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        SingleServerConfig serverConfig = config.useSingleServer();
        serverConfig.setAddress("redis://" + host + ":" + port);
        serverConfig.setDatabase(db);
        serverConfig.setPassword(password);
        return Redisson.create(config);
    }
}
