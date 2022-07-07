package cn.gpnusz.ucloudteachorder.listener;

import cn.gpnusz.ucloudteachorder.entity.CourseRecord;
import cn.gpnusz.ucloudteachorder.mapper.CourseRecordMapper;
import cn.gpnusz.ucloudteachorder.util.SnowFlake;
import com.alibaba.fastjson.JSON;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @author h0ss
 * @description 消费创建订单任务
 * @date 2022/3/16 20:41
 */
@Component
public class CourseOrderService {

    private static final Logger LOG = LoggerFactory.getLogger(CourseOrderService.class);

    @Resource
    private CourseRecordMapper courseRecordMapper;

    @Resource
    private SnowFlake snowFlake;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 监听订单创建队列 消费订单信息写入数据库
     *
     * @param msg     : 订单消息
     * @param channel : 信道
     * @author h0ss
     */
    @RabbitListener(queues = "orderCreateQueue", ackMode = "MANUAL")
    public void getMsg(Message msg, Channel channel) throws IOException {
        MDC.put("LOG_ID", String.valueOf(snowFlake.nextId()));
        LOG.info("接受到消息：{}", msg);
        long deliveryTag = msg.getMessageProperties().getDeliveryTag();
        if (!ObjectUtils.isEmpty(msg)) {
            CourseRecord ccr = JSON.parseObject(msg.getBody(), CourseRecord.class);
            // 添加未付款订单时需要考虑消息幂等性问题
            if (ccr != null) {
                String key = "course_record:id:" + ccr.getId();
                String res = stringRedisTemplate.opsForValue().get(key);
                if ("null".equals(res)) {
                    int result = courseRecordMapper.insert(ccr);
                    // 手动确认 deliveryTag运输标志  multiple确认之前的多个
                    if (result != 0) {
                        channel.basicAck(deliveryTag, true);
                        return;
                    }
                } else {
                    channel.basicAck(deliveryTag, true);
                    return;
                }
            }
        }
        // 手动拒绝 deliveryTag运输标志  multiple确认之前的多个  requeue重新发送入队列
        channel.basicNack(deliveryTag, true, false);
    }
}
