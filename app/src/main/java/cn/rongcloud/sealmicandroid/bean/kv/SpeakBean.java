package cn.rongcloud.sealmicandroid.bean.kv;

import java.io.Serializable;

public class SpeakBean implements Serializable {
    private int speaking;
    private int position;

    public int getSpeaking() {
        return speaking;
    }

    public void setSpeaking(int speaking) {
        this.speaking = speaking;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return "SpeakBean{" +
                "speaking=" + speaking +
                ", position=" + position +
                '}';
    }

    public SpeakBean(int speaking, int position) {
        this.speaking = speaking;
        this.position = position;
    }
}
