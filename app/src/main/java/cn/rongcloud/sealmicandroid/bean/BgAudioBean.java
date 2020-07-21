package cn.rongcloud.sealmicandroid.bean;

/**
 * 伴音选项
 */
public class BgAudioBean {

    private boolean selected;
    private String content;

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isSelected() {
        return selected;
    }

    public String getContent() {
        return content;
    }
}
