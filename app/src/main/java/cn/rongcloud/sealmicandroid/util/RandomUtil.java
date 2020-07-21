package cn.rongcloud.sealmicandroid.util;

import java.util.Random;

import cn.rongcloud.sealmicandroid.BuildConfig;

/**
 * 随机生成工具类
 */
public class RandomUtil {

    private static final String IMG_ADDRESS = BuildConfig.Img_address;

    private static final String[] USER_FIRST_NAME = {
            "赵", "钱", "孙", "李", "周", "吴", "郑", "王", "冯", "陈", "褚", "卫", "蒋", "沈", "韩",
            "杨", "朱", "秦", "尤", "许",
    };

    private static final String[] USER_LAST_NAME = {"逸君", "云乐", "晏齐", "嘉新", "廷凛", "楚成",
            "庚常", "恒锡", "奕晗", "修广", "姗影", "熙茹", "丽芸", "琪嘉",
            "昕迪", "钧瑶", "缦筠", "华钰", "美媛", "琴明",
    };

    private static final String[] USER_HEAD_IMAGE = {
            IMG_ADDRESS + "static/portrait/1.png",
            IMG_ADDRESS + "static/portrait/2.png",
            IMG_ADDRESS + "static/portrait/3.png",
            IMG_ADDRESS + "static/portrait/4.png",
            IMG_ADDRESS + "static/portrait/5.png",
            IMG_ADDRESS + "static/portrait/6.png",
            IMG_ADDRESS + "static/portrait/7.png",
            IMG_ADDRESS + "static/portrait/8.png",
            IMG_ADDRESS + "static/portrait/9.png",
            IMG_ADDRESS + "static/portrait/10.png",
            IMG_ADDRESS + "static/portrait/11.png",
            IMG_ADDRESS + "static/portrait/12.png",
            IMG_ADDRESS + "static/portrait/13.png",
            IMG_ADDRESS + "static/portrait/14.png",
            IMG_ADDRESS + "static/portrait/15.png",
    };

    private static final String[] ROOM_THEME_IMAGE = {
            IMG_ADDRESS + "static/room/1.png",
            IMG_ADDRESS + "static/room/2.png",
            IMG_ADDRESS + "static/room/3.png",
            IMG_ADDRESS + "static/room/4.png",
            IMG_ADDRESS + "static/room/5.png",
            IMG_ADDRESS + "static/room/6.png",
            IMG_ADDRESS + "static/room/7.png",
            IMG_ADDRESS + "static/room/8.png",
            IMG_ADDRESS + "static/room/9.png",
            IMG_ADDRESS + "static/room/10.png",
            IMG_ADDRESS + "static/room/11.png",
            IMG_ADDRESS + "static/room/12.png",
            IMG_ADDRESS + "static/room/13.png",
            IMG_ADDRESS + "static/room/14.png",
            IMG_ADDRESS + "static/room/15.png",
    };

    public static String getUserName() {
        StringBuilder stringBuilder = new StringBuilder();
        Random random = new Random();
        int firstNameIndex = random.nextInt(USER_FIRST_NAME.length - 1);
        stringBuilder.append(USER_FIRST_NAME[firstNameIndex]);
        int lastNameIndex = random.nextInt(USER_FIRST_NAME.length - 1);
        stringBuilder.append(USER_LAST_NAME[lastNameIndex]);
        return stringBuilder.toString();
    }

    public static String getUserHeadImage() {
        Random random = new Random();
        int randomIndex = random.nextInt(USER_HEAD_IMAGE.length - 1);
        return USER_HEAD_IMAGE[randomIndex];
    }

    public static String getRoomThemeImage() {
        Random random = new Random();
        int randomIndex = random.nextInt(ROOM_THEME_IMAGE.length - 1);
        return ROOM_THEME_IMAGE[randomIndex];
    }
}
