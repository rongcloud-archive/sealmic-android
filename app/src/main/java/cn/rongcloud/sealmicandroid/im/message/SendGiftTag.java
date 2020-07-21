package cn.rongcloud.sealmicandroid.im.message;

import cn.rongcloud.sealmicandroid.R;

/**
 * 送出不同的礼物时的枚举，依照此来展示不同的动画
 */
public enum SendGiftTag {

    /**
     * 笑脸
     */
    GIFT_SMELL("gift_smell"),
    /**
     * 冰淇凌
     */
    GIFT_ICE("gift_ice"),
    /**
     * 机票
     */
    GIFT_AIR_TICKET("gift_airTicket"),
    /**
     * 爱心车
     */
    GIFT_LOVING_CAR("gift_lovingCar"),
    /**
     * 蜂蜜
     */
    GIFT_HONEY("gift_honey"),
    /**
     * 存钱罐
     */
    GIFT_SAVING_POT("gift_savingPot"),
    /**
     * 宝箱
     */
    GIFT_TREASURE_BOX("gift_treasureBox"),
    /**
     * 豪华跑车
     */
    GIFT_SPORTS_CAR("gift_sportsCar");

    private String tag;

    SendGiftTag(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }

    /**
     * 接受的是哪个礼物
     */
    public static int getGiftType(String tag) {
        if (GIFT_SMELL.getTag().equals(tag)) {
            //笑脸
            return R.mipmap.get_gift_smell;
        } else if (GIFT_ICE.getTag().equals(tag)) {
            //冰淇淋
            return R.mipmap.get_gift_ice;
        } else if (GIFT_AIR_TICKET.getTag().equals(tag)) {
            //机票
            return R.mipmap.get_gift_airticket;
        } else if (GIFT_LOVING_CAR.getTag().equals(tag)) {
            //爱心车
            return R.mipmap.get_gift_lovingcar;
        } else if (GIFT_HONEY.getTag().equals(tag)) {
            //蜂蜜
            return R.mipmap.get_gift_honey;
        } else if (GIFT_SAVING_POT.getTag().equals(tag)) {
            //存钱罐
            return R.mipmap.get_gift_savinpot;
        } else if (GIFT_TREASURE_BOX.getTag().equals(tag)) {
            //宝箱
            return R.mipmap.get_gift_box;
        } else if (GIFT_SPORTS_CAR.getTag().equals(tag)) {
            //豪华跑车
            return R.mipmap.get_gift_sportscar;
        } else {
            return R.mipmap.get_gift_smell;
        }

    }
}
