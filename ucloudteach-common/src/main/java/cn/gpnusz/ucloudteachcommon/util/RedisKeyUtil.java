package cn.gpnusz.ucloudteachcommon.util;

/**
 * @author h0ss
 * @description redis key值工具类
 * @date 2022/3/15 - 15:57
 */
public class RedisKeyUtil {
    public static final String ZSET_KEY_SWIPE = "ZSET_KEY_SWIPE";

    public static final String ZSET_KEY_GRID = "ZSET_KEY_GRID";

    public static final String ZSET_KEY_HOT_COURSE = "ZSET_KEY_HOT_COURSE";

    public static final String PREFIX_SET_KEY_MSG = "SET_KEY_MSG";

    public static final String PREFIX_HOT_COURSE = "HOT_COURSE";

    public static final String PREFIX_UPLOAD_FINISH = "UPLOAD_FINISH";

    public static final String PREFIX_UPLOAD_SLICE = "UPLOAD_SLICE";

    public static final String PREFIX_UPLOAD_ID_OBS = "UPLOAD_ID_OBS";

    public static final String PREFIX_ROLE_LIST = "ROLE_LIST";

    public static final String PREFIX_EXAM_ANSWER = "EXAM_ANSWER";

    public static final String PREFIX_EXAM_CHECK = "EXAM_CHECK";

    public static final String KEY_EXAM_ID = "examId";

    public static final String KEY_PAPER_ID = "paperId";

    public static final String KEY_USER_ID = "userId";

    public static final String PK_LOGIN = "id";

    public static final String PK_UPLOAD = "hash";

    public static final String TABLE_COURSE = "course";

    public static final String PK_COURSE = "id";

    /**
     * 获取日榜单对应的zset的key值
     * ps: 值为课程id
     *
     * @param day : 日期
     * @return : java.lang.String
     * @author h0ss
     */
    public static String getHotDayKey(Integer day) {
        StringBuffer sb = new StringBuffer();
        sb.append(PREFIX_HOT_COURSE);
        sb.append(':');
        sb.append(day);
        return sb.toString();
    }

    /**
     * 课程信息key
     *
     * @param courseId : 课程id
     * @return : java.lang.String
     * @author h0ss
     */
    public static String getCourseKey(String courseId) {
        StringBuffer sb = new StringBuffer();
        sb.append(TABLE_COURSE);
        sb.append(':');
        sb.append(PK_COURSE);
        sb.append(':');
        sb.append(courseId);
        return sb.toString();
    }

    /**
     * 补偿消息的key
     *
     * @param type : 消息类型
     * @return : java.lang.String
     * @author h0ss
     */
    public static String getMsgKey(String type) {
        StringBuffer sb = new StringBuffer();
        sb.append(PREFIX_SET_KEY_MSG);
        sb.append(':');
        sb.append(type);
        return sb.toString();
    }

    /**
     * 生成文件hash的key
     *
     * @param hash : 文件hash值
     * @return : java.lang.String
     * @author h0ss
     */
    public static String getFinishHashKey(String hash) {
        StringBuffer sb = new StringBuffer();
        sb.append(PREFIX_UPLOAD_FINISH);
        sb.append(':');
        sb.append(PK_UPLOAD);
        sb.append(':');
        sb.append(hash);
        return sb.toString();
    }

    /**
     * 获取分片上传文件列表
     *
     * @param hash : 文件hash值
     * @return : java.lang.String
     * @author h0ss
     */
    public static String getSliceListHashKey(String hash) {
        StringBuffer sb = new StringBuffer();
        sb.append(PREFIX_UPLOAD_SLICE);
        sb.append(':');
        sb.append(PK_UPLOAD);
        sb.append(':');
        sb.append(hash);
        return sb.toString();
    }

    /**
     * 生成保存obs上传id的key
     *
     * @param hash : 文件哈希值
     * @return : java.lang.String
     * @author h0ss
     */
    public static String getUploadIdKey(String hash) {
        StringBuffer sb = new StringBuffer();
        sb.append(PREFIX_UPLOAD_ID_OBS);
        sb.append(':');
        sb.append(PK_UPLOAD);
        sb.append(':');
        sb.append(hash);
        return sb.toString();
    }

    /**
     * 获取存储身份列表的key
     *
     * @param loginId : 用户登录id
     * @return : java.lang.String
     * @author h0ss
     */
    public static String getRoleListKey(String loginId) {
        StringBuffer sb = new StringBuffer();
        sb.append(PREFIX_ROLE_LIST);
        sb.append(':');
        sb.append(PK_LOGIN);
        sb.append(':');
        sb.append(loginId);
        return sb.toString();
    }

    /**
     * 用户暂存试题答案的key
     *
     * @param userId  : 用户id
     * @param paperId : 试卷id
     * @return : java.lang.String
     * @author h0ss
     */
    public static String getExamStoreKey(String userId, String paperId) {
        StringBuffer sb = new StringBuffer();
        sb.append(PREFIX_EXAM_ANSWER);
        sb.append(':');
        sb.append(KEY_PAPER_ID);
        sb.append(':');
        sb.append(paperId);
        sb.append(':');
        sb.append(KEY_USER_ID);
        sb.append(':');
        sb.append(userId);
        return sb.toString();
    }

    /**
     * 教师暂存批阅信息的key
     *
     * @param userId : 用户id
     * @param examId : 考试信息id
     * @return : java.lang.String
     * @author h0ss
     */
    public static String getCheckStoreKey(String userId, String examId) {
        StringBuffer sb = new StringBuffer();
        sb.append(PREFIX_EXAM_CHECK);
        sb.append(':');
        sb.append(KEY_EXAM_ID);
        sb.append(':');
        sb.append(examId);
        sb.append(':');
        sb.append(KEY_USER_ID);
        sb.append(':');
        sb.append(userId);
        return sb.toString();
    }

}
