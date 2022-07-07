package cn.gpnusz.uploadweb.config;

import com.obs.services.ObsClient;
import com.obs.services.ObsConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author h0ss
 * @description obs配置类
 * @date 2022/4/2 - 16:16
 */
@Configuration
public class ObsConfig {
    @Value("${obs.endPoint}")
    private String endPoint;

    @Value("${obs.ak}")
    private String ak;

    @Value("${obs.sk}")
    private String sk;

    @Value("${obs.socketTimeout}")
    private Integer socketTimeout;

    @Value("${obs.connectionTimeout}")
    private Integer connectionTimeout;

    @Bean
    public ObsClient getObsClient() {
        ObsConfiguration config = new ObsConfiguration();
        config.setSocketTimeout(socketTimeout);
        config.setConnectionTimeout(connectionTimeout);
        config.setEndPoint(endPoint);
        return new ObsClient(ak, sk, config);
    }
}
