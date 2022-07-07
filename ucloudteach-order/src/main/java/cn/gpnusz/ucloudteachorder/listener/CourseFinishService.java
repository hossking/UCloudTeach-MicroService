package cn.gpnusz.ucloudteachorder.listener;

import cn.gpnusz.ucloudteachorder.entity.CourseRecord;
import cn.gpnusz.ucloudteachorder.entity.CourseRecordExample;
import cn.gpnusz.ucloudteachorder.mapper.CourseRecordMapper;
import cn.gpnusz.ucloudteachorder.util.SnowFlake;
import com.alibaba.fastjson.JSON;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author h0ss
 * @description 消费完成订单任务
 * @date 2022/3/18 21:11
 */
@Component
public class CourseFinishService {

    private static final Logger LOG = LoggerFactory.getLogger(CourseFinishService.class);

    @Resource
    private CourseRecordMapper courseRecordMapper;

    @Resource
    private SnowFlake snowFlake;

    /**
     * 监听订单创建队列 消费订单信息写入数据库
     *
     * @param msg     : 订单消息
     * @param channel : 信道
     * @author h0ss
     */
    @RabbitListener(queues = "orderFinishQueue", ackMode = "MANUAL")
    public void getMsg(Message msg, Channel channel) throws IOException {
        MDC.put("LOG_ID", String.valueOf(snowFlake.nextId()));
        LOG.info("接受到消息：{}", msg);
        long deliveryTag = msg.getMessageProperties().getDeliveryTag();
        if (!ObjectUtils.isEmpty(msg)) {
            CourseRecord cr = JSON.parseObject(msg.getBody(), CourseRecord.class);
            if (cr != null) {
                // 更新记录状态
                CourseRecordExample example = new CourseRecordExample();
                CourseRecordExample.Criteria criteria = example.createCriteria();
                criteria.andIdEqualTo(cr.getId());
                // 更新订单状态以及更新时间
                cr.setOrderStatus(1);
                Date now = new Date();
                cr.setUpdateDate(now);
                cr.setUpdateTime(now);
                // 先查询记录信息 再进行更新/插入
                long record = courseRecordMapper.countByExample(example);
                int result = 0;
                // 存在记录
                if (record != 0L) {
                    result = courseRecordMapper.updateByExample(cr, example);
                } else {
                    // 新增记录
                    result = courseRecordMapper.insert(cr);
                }
                if (result != 0) {
                    // 手动确认 deliveryTag运输标志  multiple确认之前的多个
                    LOG.info("订单ID {} 状态更新成功", cr.getId());
                    channel.basicAck(deliveryTag, true);
                    return;
                }
            }
        }
        // 手动拒绝 deliveryTag运输标志  multiple确认之前的多个  requeue重新发送入队列
        channel.basicNack(deliveryTag, true, false);
    }
}
