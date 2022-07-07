package cn.gpnusz.ucloudteachasync.mail.service;

import cn.gpnusz.ucloudteachentity.entity.CertMailInfo;
import com.alibaba.fastjson.JSON;
import com.rabbitmq.client.Channel;
import org.apache.commons.io.FileUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Date;
import java.util.UUID;

/**
 * @author h0ss
 * @description 证书邮件消息-消费端
 * @date 2022/4/4 - 11:02
 */
@Component
public class CertMailService {

    @Resource
    private StringRedisTemplate srt;

    @Resource
    private JavaMailSender javaMailSender;

    /**
     * 保证加锁原子性 使用lua脚本
     */
    private static final String MAIL_LOCK_LUA = "if redis.call('get',KEYS[1]) == false then redis.call('set', KEYS[1],ARGV[1]) redis.call('expire',KEYS[1],ARGV[2]) else return 0 end";

    private static final String MAIL_UNLOCK_LUA = "if redis.call('get',KEYS[1]) == ARGV[1] then redis.call('del', KEYS[1])  else return 0 end";

    @RabbitListener(queues = {"certMailQueue"}, ackMode = "MANUAL")
    public void sendCertMail(Message msg, Channel channel) throws IOException {
        // 传输标志
        long deliveryTag = msg.getMessageProperties().getDeliveryTag();
        // 非空判断
        if (!ObjectUtils.isEmpty(msg)) {
            // 消息幂等性保证 这里先获取一次锁 如果已经有锁了则直接退出
            String value = srt.opsForValue().get("key");
            if (value != null) {
                // 确认消息
                channel.basicAck(deliveryTag, false);
                return;
            }
            // 获取uuid 作为加锁标识
            String uuid = UUID.randomUUID().toString();
            // 反序列证书对象
            CertMailInfo info = JSON.parseObject(msg.getBody(), CertMailInfo.class);
            if (info != null) {
                // 获取证书文件
                File file;
                try {
                    file = downloadFile(info.getCertUrl(), info.getCourse());
                } catch (IOException e) {
                    System.out.println("IO异常---记录日志");
                    channel.basicNack(deliveryTag, false, false);
                    return;
                }
                // 获取消息模板
                String text = contentTemplate(info.getUname(), info.getCourse(), info.getWish());
                // 发送邮件之前加分布式锁[lua简单实现]
                DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(MAIL_LOCK_LUA, Long.class);
                // 参数一：redisScript，参数二：key列表，参数三：arg（可多个）
                Long res = srt.execute(redisScript, Collections.singletonList(Long.toString(info.getId())), uuid, 60);
                // 加锁失败 说明消息已经被消费
                if (res != null && res.equals(0L)) {
                    // 确认消息
                    channel.basicAck(deliveryTag, false);
                    return;
                }
                // 加锁成功 发送邮件 获取结果
                boolean sendRes = sendMail(info.getSubject(), info.getTo(), text, file);
                if (sendRes) {
                    // 确认消息
                    channel.basicAck(deliveryTag, false);
                    // 消费成功解锁
                    DefaultRedisScript<Long> script = new DefaultRedisScript<>(MAIL_UNLOCK_LUA, Long.class);
                    // 参数一：redisScript，参数二：key列表，参数三：arg（可多个）
                    srt.execute(script, Collections.singletonList(Long.toString(info.getId())), uuid);
                    return;
                }
            }
        }
        // 如果失败 拒绝消息 转入死信队列 消息补偿
        channel.basicNack(deliveryTag, false, false);
    }

    /**
     * 发送邮件
     *
     * @param subject : 主题
     * @param to      : 目标用户
     * @param text    : 邮件内容
     * @param attach  : 附件
     * @return : boolean
     * @author h0ss
     */
    private boolean sendMail(String subject, String to, String text, File attach) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setSubject(subject);
            helper.setFrom("zebinmail@163.com");
            helper.setTo(to);
            helper.setText(text);
            helper.setSentDate(new Date());
            helper.addAttachment(attach.getName(), attach);
        } catch (MessagingException e) {
            System.out.println("邮件创建失败---记录日志");
            return false;
        }
        javaMailSender.send(mimeMessage);
        return true;
    }

    /**
     * 内容模板
     *
     * @param uname  : 用户名
     * @param course : 课程名
     * @param wish   : 寄语
     * @return : java.lang.String
     * @author h0ss
     */
    private String contentTemplate(String uname, String course, String wish) {
        StringBuffer sb = new StringBuffer();
        sb.append(uname);
        sb.append("同学：\n");
        sb.append("     恭喜你完成了");
        sb.append(course);
        sb.append("的学习。");
        sb.append(wish);
        sb.append('\n');
        sb.append("    附件内含你的证书信息，请注意查收！");
        return sb.toString();
    }

    /**
     * 证书中转下载
     *
     * @param serverUrl : 证书url
     * @param filePath  : 本地地址
     * @return : java.io.File
     * @author h0ss
     */
    public File downloadFile(String serverUrl, String filePath) throws IOException {
        File f = new File(filePath);
        if (!f.exists()) {
            if (!f.createNewFile()) {
                throw new FileNotFoundException();
            }
        }
        URL url = new URL(serverUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5 * 1000);
        //防止屏蔽程序抓取而放回403错误
        conn.setRequestProperty("User-Agent", "Mozilla/4.0(compatible;MSIE 5.0;Windows NT;DigExt)");
        long totalSize = Long.parseLong(conn.getHeaderField("Content-Length"));
        if (totalSize > 0) {
            FileUtils.copyURLToFile(url, f);
            return f;
        } else {
            throw new IOException("can not find serverUrl :{}" + serverUrl);
        }
    }
}
