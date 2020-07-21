package cn.rongcloud.sealmicandroid.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则表达式工具类
 */
public class PatternUtil {

    /**
     * 判断是否包含中文
     */
    public static boolean isContainChinese(String str) {
        final String type = "[\u4e00-\u9fa5]";
        Pattern pattern = Pattern.compile(type);
        Matcher matcher = pattern.matcher(str);
        return matcher.find();
    }

    /**
     * 是否包含字母、数字、下划线
     */
    public static boolean isContainEnglishEtc(String str) {
        final String type = ".*[0-9a-zA-Z_].*";
        Pattern pattern = Pattern.compile(type);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    /**
     * 过滤房间名字
     */
    public static boolean filterRoomName(String roomName) {
        //判断房间名字是否合法
        String filter = stringFilter(roomName);
        if (roomName.equals(filter)) {
            //合法,校验是否包含英文
            boolean containEnglishEtc = isContainEnglishEtc(roomName);
            if (!containEnglishEtc) {
                //不包含，判断中文是否超过十个
                if (roomName.length() > 10) {
                    return false;
                } else {
                    return true;
                }
            } else {
                //包含，则通过
                return true;
            }

        } else {
            return false;
        }
    }

    /**
     * 是否包含字母、数字、下划线
     */
    public static boolean isContainRoomName(String str) {
        final String type = "[^a-zA-Z0-9\u4E00-\u9FA5]";
        Pattern pattern = Pattern.compile(type);
        Matcher matcher = pattern.matcher(str);
        return matcher.find();
    }

    public static String stringFilter(String str) {
        // 只允许字母、数字和汉字其余的还可以随时添加比如下划线什么的，但是注意引文符号和中文符号区别
        String regEx = "[^a-zA-Z0-9_\u4E00-\u9FA5]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }

    /**
     * 是否为纯数字
     */
    public static boolean isNumber(String str) {
        final String type = "[0-9]+";
        Pattern pattern = Pattern.compile(type);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }


    /**
     * 是否为邮箱
     */
    public static boolean isEmail(String str) {
        final String type = "^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$";
        Pattern pattern = Pattern.compile(type);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    /**
     * 判断字符串是否为URL
     *
     * @param urls 需要判断的String类型url
     * @return true:是URL；false:不是URL
     */
    public static boolean isHttpUrl(String urls) {
        final String regex = "(((https|http)?://)?([a-z0-9]+[.])|(www.))"
                + "\\w+[.|\\/]([a-z0-9]{0,})?[[.]([a-z0-9]{0,})]+((/[\\S&&[^,;\u4E00-\u9FA5]]+)+)?([.][a-z0-9]{0,}+|/?)";
        //对比
        Pattern pat = Pattern.compile(regex.trim());
        Matcher mat = pat.matcher(urls.trim());
        return mat.matches();
    }

    /**
     * 是否为手机号
     */
    public static boolean isMobile(String str) {
        final String regex = "^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(17[013678])|(18[0,1,2,3,5-9]))\\d{8}$";
        //对比
        Pattern pat = Pattern.compile(regex.trim());
        Matcher mat = pat.matcher(str.trim());
        return mat.matches();
    }

}
