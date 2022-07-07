package cn.gpnusz.ucloudteachcommon.util;

import java.security.SecureRandom;

/**
 * @author h0ss
 * @description 生成盐值的工具类
 * @date 2022/03/29 - 19:52
 */
public class RandomKeyUtil {

    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * 获取随机盐值
     *
     * @param length : 盐值长度
     * @return : java.lang.String
     * @author h0ss
     */
    public static String getRandomSalt(int length) {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@";
        return getRandomUtil(str, length);
    }

    /**
     * 获取随机验证码
     *
     * @param length : 验证码长度
     * @return : java.lang.String
     * @author h0ss
     */
    public static String getRandomCode(int length) {
        String str = "0123456789";
        return getRandomUtil(str, length);
    }

    /**
     * 从指定字符集中获取随机字符串
     *
     * @param str    : base字符串
     * @param length : 长度
     * @return : java.lang.String
     * @author h0ss
     */
    public static String getRandomUtil(String str, int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int number = RANDOM.nextInt(str.length());
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

}
