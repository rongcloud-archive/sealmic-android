package cn.rongcloud.sealmic.ui;

public class ChatRoomBgItem {
    int drawableId;
    boolean checked;

    public ChatRoomBgItem() {
    }

    public ChatRoomBgItem(int drawableId, boolean checked) {
        this.drawableId = drawableId;
        this.checked = checked;
    }

    public int getDrawableId() {
        return drawableId;
    }

    public void setDrawableId(int drawableId) {
        this.drawableId = drawableId;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}