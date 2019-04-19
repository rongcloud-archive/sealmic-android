package cn.rongcloud.sealmic.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.List;

import cn.rongcloud.sealmic.R;
import cn.rongcloud.sealmic.ui.ChatRoomBgItem;

public class ChatRoomBGItemAdapter extends BaseAdapter {

    private List<ChatRoomBgItem> list;
    Context context;


    public ChatRoomBGItemAdapter(Context context, List<ChatRoomBgItem> list) {
        if (list.size() >= 31) {
            this.list = list.subList(0, 30);
        } else {
            this.list = list;
        }

        this.context = context;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.chatroom_setting_bg_item, parent, false);
        }
        ImageView ivBG = convertView.findViewById(R.id.iv_bg);
        ImageView ivBGChecked = convertView.findViewById(R.id.iv_bg_checked);
        ivBG.setBackground(context.getResources().getDrawable(list.get(position).getDrawableId()));
        if (list.get(position).isChecked()) {
            ivBGChecked.setVisibility(View.VISIBLE);
        } else {
            ivBGChecked.setVisibility(View.GONE);
        }
        return convertView;
    }

    @Override
    public int getCount() {
        if (list != null) {
            return list.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * 传入新的数据 刷新UI的方法
     */
    public void updateListView(List<ChatRoomBgItem> list) {
        this.list = list;
        notifyDataSetChanged();
    }

}
