package cn.rongcloud.sealmicandroid.common.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;

import cn.rongcloud.sealmicandroid.R;
import cn.rongcloud.sealmicandroid.common.listener.OnDialogButtonListClickListener;

/**
 * 按钮风格的dialog列表
 */
public class ButtonBaseDialogAdapter extends BaseDialogListAdapter {

    private String[] datas;
    private Context context;
    /**
     * 是否是点击了消息内容弹出dialog
     */
    private boolean isAtMessage;

    public ButtonBaseDialogAdapter(Context context, int resource, String[] objects, boolean isAtMessage) {
        super(context, resource, objects);
        this.datas = objects;
        this.context = context;
        this.isAtMessage = isAtMessage;
    }

    public void setDatas(String[] datas) {
        this.datas = datas;
        notifyDataSetChanged();
    }

    @Override
    public void initView(final int i, View root) {
        Button button = root.findViewById(R.id.dialog_item_btn);
        button.getBackground().setAlpha(100);
        button.setText(datas[i]);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onDialogButtonListClickListener != null) {
                    onDialogButtonListClickListener.onClick(datas[i]);
                }
            }
        });
        String string = button.getText().toString();

        //为了避免点击消息弹出的dialog的条目有两个按钮变颜色，加一个atMessage标识
        if (isAtMessage) {
            if (string.equals(context.getResources().getString(R.string.remove_this_message))) {
                button.setTextColor(Color.parseColor("#2DF3C1"));
            }
        } else {
            if (string.equals(context.getResources().getString(R.string.remove_this_room))) {
                button.setTextColor(Color.parseColor("#2DF3C1"));
            }
        }
    }

    private OnDialogButtonListClickListener onDialogButtonListClickListener;

    public void setOnButtonClickListener(OnDialogButtonListClickListener onDialogButtonListClickListener) {
        this.onDialogButtonListClickListener = onDialogButtonListClickListener;
    }
}
