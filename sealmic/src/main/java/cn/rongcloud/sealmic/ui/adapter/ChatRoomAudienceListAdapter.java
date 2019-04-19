package cn.rongcloud.sealmic.ui.adapter;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.rongcloud.sealmic.R;
import cn.rongcloud.sealmic.model.UserInfo;

public class ChatRoomAudienceListAdapter extends BaseAdapter {

    /**
     * 监听加载更多
     */
    public interface OnLoadMore {
        void onLoadingMore(UserInfo lastUserInfo);
    }


    private static final int TYPE_LOADING_MORE = 0;
    private static final int TYPE_NORMAL = 1;

    private boolean expandable = false;//
    private OnLoadMore loader;

    private List<UserInfo> datas;

    public ChatRoomAudienceListAdapter(OnLoadMore loader) {
        this.loader = loader;
    }

    @Override
    public int getCount() {
        int c = datas == null ? 0 : datas.size();
        return expandable ? c + 1 : c;
    }

    @Override
    public Object getItem(int position) {
        return datas == null || datas.size() <= position ? null : datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        switch (getItemViewType(position)) {
            case TYPE_LOADING_MORE:
                if (convertView == null || !(convertView instanceof TextView)) {
                    convertView = new TextView(parent.getContext());
                    ((TextView) convertView).setGravity(Gravity.CENTER);
                    ((TextView) convertView).setText(R.string.loading_for_wait);
                }
                if (loader != null && (datas != null && datas.size() > 0)) {
                    loader.onLoadingMore(datas.get(datas.size() - 1));
                }
                break;
            case TYPE_NORMAL:
                if (convertView == null || (convertView instanceof TextView)) {
                    convertView = createView(parent);
                }
                UserInfo info = datas.get(position);
                ViewHolder viewHolder = (ViewHolder) convertView.getTag();
                updateView(info, viewHolder, parent);
                break;
        }
        return convertView;
    }

    private View createView(ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View contentView = inflater.inflate(R.layout.chatromm_item_audience_info, parent, false);
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.nickNameTv = contentView.findViewById(R.id.tv_nickname);
        viewHolder.avatarIv = contentView.findViewById(R.id.iv_avatar);
        contentView.setTag(viewHolder);
        return contentView;
    }

    private void updateView(UserInfo info, ViewHolder viewHolder, ViewGroup parent) {
        viewHolder.nickNameTv.setText(info.getNickName());
        viewHolder.avatarIv.setImageDrawable(parent.getResources().getDrawable(info.getAvatarResourceId()));
    }

    @Override
    public int getViewTypeCount() {
        return expandable ? 2 : 1;// 数据小于一页标准
    }

    @Override
    public int getItemViewType(int position) {
        int c = datas == null ? 0 : datas.size();
        if (c == position && expandable) {
            return TYPE_LOADING_MORE;
        }
        return TYPE_NORMAL;
    }

    public void setDatas(List<UserInfo> datas) {
        this.datas = datas;
    }

    private class ViewHolder {
        ImageView avatarIv;
        TextView nickNameTv;
    }


    public boolean isExpandable() {
        return expandable;
    }

    /**
     * 设置是否展示加载更多界面， 在更新界面之前设置
     *
     * @param expandable
     */
    public void setExpandable(boolean expandable) {
        this.expandable = expandable;
    }

}
