package cn.gpnusz.courseservice.config;

import cn.gpnusz.ucloudteachcommon.util.RedisKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;

/**
 * @author h0ss
 * @description 消息队列配置
 * @date 2022/3/17 - 15:56
 */
@Configuration
public class MqConfig {
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    /* ---------------交换机常量---------------*/

    public static final String ORDER_EXCHANGE = "orderExchange";
    public static final String CERT_EXCHANGE = "certMailExchange";
    public static final String COMMON_DEAD_LETTER_EXCHANGE = "deadLetterExchange";

    /* ---------------队列常量---------------*/

    public static final String QUEUE_ORDER_CREATE_NAME = "orderCreateQueue";
    public static final String QUEUE_ORDER_FINISH_NAME = "orderFinishQueue";
    public static final String QUEUE_CERT_NAME = "certMailQueue";
    public static final String QUEUE_ORDER_DEAD_NAME = "orderDeadQueue";
    public static final String QUEUE_CERT_DEAD_NAME = "certDeadQueue";

    /* ---------------交换机定义---------------*/

    @Bean("certMailExchange")
    public Exchange certExchange() {
        return ExchangeBuilder.topicExchange(CERT_EXCHANGE).durable(true).build();
    }

    @Bean("orderExchange")
    public Exchange orderExchange() {
        return ExchangeBuilder.topicExchange(ORDER_EXCHANGE).durable(true).build();
    }

    @Bean("deadLetterExchange")
    public Exchange commonDeadLetterExchange() {
        return ExchangeBuilder.topicExchange(COMMON_DEAD_LETTER_EXCHANGE).durable(true).build();
    }

    /*  ---------------队列定义--------------- */

    @Bean("certMailQueue")
    public Queue certCreateQueue() {
        return QueueBuilder
                .durable(QUEUE_CERT_NAME)
                .withArgument("x-dead-letter-exchange", COMMON_DEAD_LETTER_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", "cert.dead")
                .build();
    }

    @Bean("orderCreateQueue")
    public Queue orderCreateQueue() {
        return QueueBuilder
                .durable(QUEUE_ORDER_CREATE_NAME)
                .withArgument("x-dead-letter-exchange", COMMON_DEAD_LETTER_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", "order.dead")
                .build();
    }

    @Bean("orderFinishQueue")
    public Queue orderFinishQueue() {
        return QueueBuilder
                .durable(QUEUE_ORDER_FINISH_NAME)
                .withArgument("x-dead-letter-exchange", COMMON_DEAD_LETTER_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", "order.dead")
                .build();
    }

    @Bean("orderDeadQueue")
    public Queue orderDeadQueue() {
        return QueueBuilder.durable(QUEUE_ORDER_DEAD_NAME).build();
    }

    @Bean("certDeadQueue")
    public Queue certDeadQueue() {
        return QueueBuilder.durable(QUEUE_CERT_DEAD_NAME).build();
    }


    /*  ---------------交换机队列绑定--------------- */

    @Bean
    public Binding certBinding(@Qualifier("certMailQueue") Queue queue, @Qualifier("certMailExchange") Exchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("cert.mail").noargs();
    }

    @Bean
    public Binding orderCreateBinding(@Qualifier("orderCreateQueue") Queue queue, @Qualifier("orderExchange") Exchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("order.create").noargs();
    }

    @Bean
    public Binding orderFinishBinding(@Qualifier("orderFinishQueue") Queue queue, @Qualifier("orderExchange") Exchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("order.finish").noargs();
    }

    @Bean
    public Binding orderDeadBinding(@Qualifier("orderDeadQueue") Queue orderDeadQueue, @Qualifier("deadLetterExchange") Exchange deadLetterExchange) {
        return BindingBuilder
                .bind(orderDeadQueue)
                .to(deadLetterExchange)
                .with("order.dead")
                .noargs();
    }

    @Bean
    public Binding certDeadBinding(@Qualifier("certDeadQueue") Queue certDeadQueue, @Qualifier("deadLetterExchange") Exchange deadLetterExchange) {
        return BindingBuilder
                .bind(certDeadQueue)
                .to(deadLetterExchange)
                .with("cert.dead")
                .noargs();
    }

    /*  ---------------消息确认回调--------------- */

    @Bean
    public RabbitTemplate createRabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        // 设置序列化方式
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        //设置开启Mandatory，才能触发回调函数，无论消息推送结果怎么样都会强制调用回调函数
        rabbitTemplate.setMandatory(true);
        // 设置确认发送到交换机的回调函数
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            final Logger LOG = LoggerFactory.getLogger(this.getClass());

            @Override
            public void confirm(CorrelationData correlationData, boolean b, String s) {
                LOG.info("正在执行消息确认回调方法");
                if (b) {
                    LOG.info("消息发送成功，详情:{}", correlationData);
                } else {
                    LOG.info("发送失败，原因: {}, 详情:{}", s, correlationData);
                    // 记录到redis中 判断消息来源
                    if (ObjectUtils.isEmpty(correlationData.getReturned())) {
                        ReturnedMessage rm = correlationData.getReturned();
                        String id = correlationData.getId();
                        stringRedisTemplate.opsForSet().add(RedisKeyUtil.getMsgKey(rm.getReplyText()), id);
                    }
                }
            }
        });

        //设置确认消息已发送到队列的回调
        rabbitTemplate.setReturnsCallback(new RabbitTemplate.ReturnsCallback() {
            final Logger LOG = LoggerFactory.getLogger(this.getClass());

            @Override
            public void returnedMessage(ReturnedMessage returnedMessage) {
                LOG.info("消息：{}", returnedMessage.getMessage());
                LOG.info("回应码：{}", returnedMessage.getReplyCode());
                LOG.info("回应信息：{}", returnedMessage.getReplyText());
                LOG.info("交换机：{}", returnedMessage.getExchange());
                LOG.info("路由键：{}", returnedMessage.getRoutingKey());
            }
        });
        return rabbitTemplate;
    }
}
