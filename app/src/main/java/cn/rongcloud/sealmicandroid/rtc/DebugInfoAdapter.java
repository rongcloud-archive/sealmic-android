package cn.rongcloud.sealmicandroid.rtc;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.rongcloud.rtc.api.report.StatusBean;
import cn.rongcloud.sealmicandroid.R;

public class DebugInfoAdapter extends BaseAdapter {
    private Context context;
    private List<StatusBean> statusBeanList = new ArrayList<>();

    private static final String INVALID = "--";

    public DebugInfoAdapter(Context context) {
        this.context = context;
    }

    public void setStatusBeanList(List<StatusBean> statusBeanList) {
        this.statusBeanList = statusBeanList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return statusBeanList == null ? 0 : statusBeanList.size();
    }

    @Override
    public StatusBean getItem(int position) {
        return statusBeanList == null ? null : statusBeanList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    ViewHolder holder;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.debug_info_list_item, null);
            holder.userIdView = convertView.findViewById(R.id.debug_info_uid);
            holder.mediaTypeView = convertView.findViewById(R.id.debug_info_media_type);
            holder.codecView = convertView.findViewById(R.id.debug_info_codec);
            holder.resolutionView = convertView.findViewById(R.id.debug_info_resolution);
            holder.fpsView = convertView.findViewById(R.id.debug_info_fps);
            holder.bitrateView = convertView.findViewById(R.id.debug_info_bitrate);
            holder.lostRateView = convertView.findViewById(R.id.debug_info_lost_rate);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        updateViewWithData(position);

        return convertView;
    }

    private void updateViewWithData(int position) {

        try {
            StatusBean statusBean = getItem(position);
            if (statusBean != null) {
                holder.userIdView.setText(statusBean.isSend ? "本地" : statusBean.id);
                if (!TextUtils.isEmpty(statusBean.mediaType)) {
                    holder.mediaTypeView.setText(("video".equals(statusBean.mediaType) ? "视频" : "音频") + ((statusBean.isSend ? "发送" : "接收")));
                }

                if (!TextUtils.isEmpty(statusBean.codecName)) {
                    holder.codecView.setText(statusBean.codecName);
                } else {
                    holder.codecView.setText(INVALID);
                }

                if (!TextUtils.isEmpty(statusBean.mediaType)) {
                    if (0 == statusBean.frameHeight && 0 == statusBean.frameWidth || !"video".equals(statusBean.mediaType)) {
                        holder.resolutionView.setText(INVALID);
                    } else {
                        holder.resolutionView.setText((statusBean.frameHeight + "x" + statusBean.frameWidth));
                    }
                }

                if (!TextUtils.isEmpty(statusBean.mediaType)) {
                    holder.fpsView.setText("video".equals(statusBean.mediaType) ? statusBean.frameRate + "" : "--");
                }

                holder.bitrateView.setText(statusBean.bitRate + "");
                holder.lostRateView.setText(statusBean.packetLostRate + "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static class ViewHolder {
        public TextView userIdView;
        public TextView mediaTypeView;
        public TextView codecView;
        public TextView resolutionView;
        public TextView fpsView;
        public TextView bitrateView;
        public TextView lostRateView;
    }
}
