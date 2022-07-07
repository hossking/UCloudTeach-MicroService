package cn.gpnusz.liveservice.service;

import cn.gpnusz.liveinterface.service.LiveContentService;
import cn.gpnusz.liveservice.entity.LiveContentExample;
import cn.gpnusz.liveservice.mapper.LiveContentMapper;
import cn.gpnusz.liveservice.util.SnowFlake;
import cn.gpnusz.ucloudteachcommon.util.PageInfoUtil;
import cn.gpnusz.ucloudteachentity.common.CommonResp;
import cn.gpnusz.ucloudteachentity.common.PageResp;
import cn.gpnusz.ucloudteachentity.entity.LiveContent;
import cn.gpnusz.ucloudteachentity.req.ApplyTokenReq;
import cn.gpnusz.ucloudteachentity.req.LiveContentQueryReq;
import cn.gpnusz.ucloudteachentity.req.LiveContentSaveReq;
import cn.gpnusz.ucloudteachentity.resp.LiveTokenResp;
import com.github.pagehelper.PageHelper;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

/**
 * @author h0ss
 * @description 直播课程内容业务层
 * @date 2022/4/4 20:49
 */
@Service
@DubboService(interfaceClass = LiveContentService.class, version = "1.0.0", timeout = 10000)
public class LiveContentServiceImpl implements LiveContentService {
    @Resource
    private LiveContentMapper liveContentMapper;

    @Resource
    private LiveTokenServiceImpl liveTokenService;

    @Resource
    private SnowFlake snowFlake;

    private static final Logger LOG = LoggerFactory.getLogger(LiveContentServiceImpl.class);

    private static final Integer MAX_SIZE_PER_PAGE = 100;

    /**
     * 根据传入条件查询直播内容
     *
     * @param lcq : 查询实体
     * @return : cn.gpnusz.ucloudteachentity.common.PageResp<cn.gpnusz.ucloudteachentity.entity.LiveContent>
     * @author h0ss
     */
    @Override
    public PageResp<LiveContent> getAllByCondition(LiveContentQueryReq lcq) {
        LiveContentExample liveContentExample = new LiveContentExample();
        LiveContentExample.Criteria criteria = liveContentExample.createCriteria();
        liveContentExample.setOrderByClause("sort");
        if (!ObjectUtils.isEmpty(lcq.getId())) {
            criteria.andIdEqualTo(lcq.getId());
        }
        if (!ObjectUtils.isEmpty(lcq.getSort())) {
            criteria.andSortEqualTo(lcq.getSort());
        }
        if (!ObjectUtils.isEmpty(lcq.getName())) {
            criteria.andNameLike("%" + lcq.getName() + "%");
        }
        if (!ObjectUtils.isEmpty(lcq.getLiveId())) {
            criteria.andLiveIdEqualTo(lcq.getLiveId());
        }
        if (lcq.getPage() != null && lcq.getSize() != null) {
            if (lcq.getSize() < MAX_SIZE_PER_PAGE) {
                PageHelper.startPage(lcq.getPage(), lcq.getSize());
            } else {
                PageHelper.startPage(lcq.getPage(), MAX_SIZE_PER_PAGE);
            }
        } else {
            PageHelper.startPage(1, MAX_SIZE_PER_PAGE);
        }
        List<LiveContent> contentList = liveContentMapper.selectByExample(liveContentExample);
        LOG.info("查询记录数为:{}", contentList != null ? contentList.size() : 0);
        return PageInfoUtil.getPageInfoResp(contentList, LiveContent.class);
    }

    /**
     * 保存直播内容
     *
     * @param liveContentSaveReq : 保存实体
     * @author h0ss
     */
    @Override
    public void save(LiveContentSaveReq liveContentSaveReq) {
        // 创建一个新对象
        LiveContent liveContent = new LiveContent();
        BeanUtils.copyProperties(liveContentSaveReq, liveContent);
        // 判断是新增还是编辑
        if (liveContent.getId() != null) {
            LiveContentExample liveContentExample = new LiveContentExample();
            LiveContentExample.Criteria criteria = liveContentExample.createCriteria();
            criteria.andIdEqualTo(liveContent.getId());
            liveContentMapper.updateByExample(liveContent, liveContentExample);
        } else {
            // 雪花算法生成id
            liveContent.setId(snowFlake.nextId());
            liveContentMapper.insertSelective(liveContent);
        }
        // TODO 执行发送邮件的逻辑
    }

    /**
     * 删除直播内容
     *
     * @param id : 要删除的直播内容id
     * @author h0ss
     */
    @Override
    public void delete(Long id) {
        LiveContentExample liveContentExample = new LiveContentExample();
        LiveContentExample.Criteria criteria = liveContentExample.createCriteria();
        criteria.andIdEqualTo(id);
        liveContentMapper.deleteByExample(liveContentExample);
    }

    /**
     * 根据id获取课时信息
     *
     * @param contentId : 课时id
     * @return : cn.gpnusz.ucloudteachentity.entity.LiveContent
     * @author h0ss
     */
    @Override
    public LiveContent getContent(Long contentId) {
        LiveContentExample example = new LiveContentExample();
        LiveContentExample.Criteria criteria = example.createCriteria();
        criteria.andIdEqualTo(contentId);
        List<LiveContent> contentList = liveContentMapper.selectByExample(example);
        if (contentList != null && !contentList.isEmpty()) {
            return contentList.get(0);
        }
        return null;
    }

    /**
     * 开始直播
     *
     * @param userId    : 用户id
     * @param courseId  : 课程id
     * @param contentId : 课时id
     * @return : cn.gpnusz.ucloudteachentity.common.CommonResp<cn.gpnusz.ucloudteachentity.resp.LiveTokenResp>
     * @author h0ss
     */
    @Override
    public CommonResp<LiveTokenResp> startLive(Long userId, Long courseId, Long contentId) {
        // 开始获取token
        ApplyTokenReq req = new ApplyTokenReq();
        req.setPublish(Boolean.TRUE);
        String channel = DigestUtils.md5DigestAsHex(Long.toString(courseId).getBytes(StandardCharsets.UTF_8));
        req.setChannel(channel);
        // 重置开始时间
        resetTime(courseId);
        // TODO 开启云端录制功能【另创建接口实现】
        return liveTokenService.getToken(req);
    }

    /**
     * 结束直播
     *
     * @param userId    : 用户id
     * @param courseId  : 课程id
     * @param contentId : 课时id
     * @author h0ss
     */
    @Override
    public void finishLive(Long userId, Long courseId, Long contentId) {
        // 写直播内容表 修改直播时长参数
        LiveContentExample example = new LiveContentExample();
        LiveContentExample.Criteria criteria = example.createCriteria();
        criteria.andIdEqualTo(contentId);
        List<LiveContent> contentList = liveContentMapper.selectByExample(example);
        if (contentList == null || contentList.isEmpty()) {
            return;
        }
        LiveContent content = contentList.get(0);
        long startTime = content.getBeginTime().getTime();
        long curTime = System.currentTimeMillis();
        // 写直播时长
        content.setLiveTime(Integer.valueOf(String.valueOf((curTime - startTime) / 1000)));
        // 写直播状态
        content.setStatus(Boolean.TRUE);
        liveContentMapper.updateByExampleSelective(content, example);
        // TODO 结束云端录制功能【另创建接口实现】
    }

    /**
     * 用户参与直播
     *
     * @param userId    : 用户id
     * @param courseId  : 课程id
     * @param contentId : 课时id
     * @return : cn.gpnusz.ucloudteachentity.common.CommonResp<cn.gpnusz.ucloudteachentity.resp.LiveTokenResp>
     * @author h0ss
     */
    @Override
    public CommonResp<LiveTokenResp> joinLive(Long userId, Long courseId, Long contentId) {
        CommonResp<LiveTokenResp> resp = new CommonResp<>();
        // 检查课时开始时间 token获取交给页面初始化去请求
        LiveContentExample example = new LiveContentExample();
        LiveContentExample.Criteria criteria = example.createCriteria();
        criteria.andIdEqualTo(contentId);
        List<LiveContent> contentList = liveContentMapper.selectByExample(example);
        if (contentList == null || contentList.isEmpty()) {
            resp.setSuccess(false);
            resp.setMessage("获取失败，请稍后重试");
            return resp;
        }
        LiveContent liveContent = contentList.get(0);
        // 检查课时开始时间
        long now = System.currentTimeMillis();
        long target = liveContent.getBeginTime().getTime();
        if (target > now) {
            resp.setSuccess(false);
            resp.setMessage("课时仍未开放，请耐心等待");
            return resp;
        }
        // 开始获取token
        ApplyTokenReq req = new ApplyTokenReq();
        req.setPublish(Boolean.FALSE);
        String channel = DigestUtils.md5DigestAsHex(Long.toString(courseId).getBytes(StandardCharsets.UTF_8));
        req.setChannel(channel);
        return liveTokenService.getToken(req);
    }

    /**
     * 用户查询课时状态
     *
     * @param contentId : 课时id
     * @return : cn.gpnusz.ucloudteachentity.common.CommonResp<java.lang.Boolean>
     * @author h0ss
     */
    @Override
    public CommonResp<Boolean> checkStatus(Long contentId) {
        CommonResp<Boolean> resp = new CommonResp<>();
        resp.setSuccess(false);
        LiveContentExample example = new LiveContentExample();
        LiveContentExample.Criteria criteria = example.createCriteria();
        criteria.andIdEqualTo(contentId);
        List<LiveContent> contentList = liveContentMapper.selectByExample(example);
        if (contentList != null && !contentList.isEmpty()) {
            resp.setContent(contentList.get(0).getStatus());
            return resp;
        }
        resp.setMessage("状态获取失败");
        return resp;
    }

    /**
     * 重置直播开始时间
     *
     * @param courseId : 课程id
     * @author h0ss
     */
    @Override
    public void resetTime(Long courseId) {
        LiveContentExample example = new LiveContentExample();
        LiveContentExample.Criteria criteria = example.createCriteria();
        criteria.andIdEqualTo(courseId);
        List<LiveContent> contentList = liveContentMapper.selectByExample(example);
        if (contentList != null && !contentList.isEmpty()) {
            LiveContent liveContent = contentList.get(0);
            Date now = new Date();
            liveContent.setBeginTime(now);
            liveContent.setBeginDate(now);
            liveContentMapper.updateByExample(liveContent, example);
        }
    }
}
