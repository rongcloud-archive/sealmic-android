package cn.rongcloud.sealmicandroid.common.adapter;

import android.content.Context;
import android.view.View;
import android.widget.CompoundButton;

import androidx.core.content.ContextCompat;

import cn.rongcloud.sealmicandroid.R;
import cn.rongcloud.sealmicandroid.manager.CacheManager;
import cn.rongcloud.sealmicandroid.rtc.RTCClient;
import cn.rongcloud.sealmicandroid.ui.widget.ListItemSwitchButton;

/**
 * 切换开关风格的dialog列表
 */
public class SwitchBaseDialogAdapter extends BaseDialogListAdapter {

    private String[] datas;
    private Context context;
    private OnSwitchButtonChangeListener onSwitchButtonChangeListener;
    private ListItemSwitchButton itemSwitchButton;

    public void setOnSwitchButtonChangeListener(OnSwitchButtonChangeListener onSwitchButtonChangeListener) {
        this.onSwitchButtonChangeListener = onSwitchButtonChangeListener;
    }

    public interface OnSwitchButtonChangeListener {

        /**
         * 选中框状态改变监听
         *
         * @param position
         * @param buttonView
         * @param isChecked
         */
        void onCheckedChanged(int position, CompoundButton buttonView, boolean isChecked);
    }

    public SwitchBaseDialogAdapter(Context context, int resource, String[] objects) {
        super(context, resource, objects);
        this.datas = objects;
        this.context = context;
    }

    @Override
    public void initView(final int i, View view) {
        itemSwitchButton = view.findViewById(R.id.switch_room_setting);
        itemSwitchButton.setContent(datas[i]);
        if (itemSwitchButton.getContent().getText().toString().equals(context.getResources().getString(R.string.allowe_audience_join_mic))) {
            itemSwitchButton.setChecked(CacheManager.getInstance().getRoomDetailRepo().isAllowedFreeJoinMic());
        }
        if (itemSwitchButton.getContent().getText().toString().equals(context.getResources().getString(R.string.allowe_audience_join_room))) {
            itemSwitchButton.setChecked(CacheManager.getInstance().getRoomDetailRepo().isAllowedJoinRoom());
        }
        if (itemSwitchButton.getContent().getText().toString().equals(context.getResources().getString(R.string.user_receiver))) {
            //是否为扬声器播放，true为扬声器播放
            boolean isSpeakerphoneOn = RTCClient.getInstance().isSpeakerphoneOn(context);
            itemSwitchButton.setChecked(!isSpeakerphoneOn);
        }
        if (itemSwitchButton.getContent().getText().toString().equals(context.getResources().getString(R.string.open_debug))) {
            itemSwitchButton.setChecked(CacheManager.getInstance().getIsOpenDebug());
        }
        itemSwitchButton.setContentColor(ContextCompat.getColor(context, android.R.color.white));
        itemSwitchButton.setSwitchButtonChangedListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                onSwitchButtonChangeListener.onCheckedChanged(i, buttonView, isChecked);
            }
        });
    }

    public ListItemSwitchButton getButton() {
        return itemSwitchButton;
    }
}
