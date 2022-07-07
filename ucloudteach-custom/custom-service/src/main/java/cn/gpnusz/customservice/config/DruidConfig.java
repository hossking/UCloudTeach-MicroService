//package cn.gpnusz.customservice.config;
//
//import com.alibaba.druid.pool.DruidDataSource;
//import com.alibaba.druid.support.http.StatViewServlet;
//import com.alibaba.druid.support.http.WebStatFilter;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.boot.web.servlet.FilterRegistrationBean;
//import org.springframework.boot.web.servlet.ServletRegistrationBean;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import javax.servlet.Filter;
//import javax.servlet.Servlet;
//import javax.sql.DataSource;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * @author h0ss
// * @description
// * @date 2022/3/30 - 19:24
// */
//@Configuration
//public class DruidConfig {
////
////    @ConfigurationProperties(prefix = "spring.datasource")
////    @Bean
////    public DataSource druidDataSource() {
////        return new DruidDataSource();
////    }
//
//    /**
//     * 配置管理后台
//     */
//    @Bean
//    public ServletRegistrationBean<Servlet> statViewServlet() {
//        ServletRegistrationBean<Servlet> bean = new ServletRegistrationBean<>(new StatViewServlet(), "/druid/*");
//        Map<String, String> initParams = new HashMap<>(4);
//        initParams.put("loginUsername", "admin");
//        initParams.put("loginPassword", "admin");
//        //默认就是允许所有访问
//        initParams.put("allow", "");
//        //initParams.put("allow", "localhost")：表示只有本机可以访问
//        initParams.put("deny", "");
//        //设置初始化参数
//        bean.setInitParameters(initParams);
//        return bean;
//    }
//
//    /**
//     * 配置 Druid 监控 之  web 监控的 filter
//     * WebStatFilter：用于配置Web和Druid数据源之间的管理关联监控统计
//     */
//    @Bean
//    public FilterRegistrationBean<Filter> webStatFilter() {
//        FilterRegistrationBean<Filter> bean = new FilterRegistrationBean<>();
//        bean.setFilter(new WebStatFilter());
//        //exclusions：设置哪些请求进行过滤排除掉，从而不进行统计
//        Map<String, String> initParams = new HashMap<>(1);
//        initParams.put("exclusions", "*.js,*.css,/druid/*");
//        bean.setInitParameters(initParams);
//        // 所有请求进行监控处理
//        bean.addUrlPatterns("/*");
//        return bean;
//    }
//}
