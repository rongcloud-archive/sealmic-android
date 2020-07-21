package cn.rongcloud.sealmicandroid.common.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import cn.rongcloud.sealmicandroid.R;
import cn.rongcloud.sealmicandroid.SealMicApp;
import cn.rongcloud.sealmicandroid.bean.BgAudioBean;
import cn.rongcloud.sealmicandroid.bean.local.BgmBean;
import cn.rongcloud.sealmicandroid.databinding.ItemBgAudioBinding;
import cn.rongcloud.sealmicandroid.manager.CacheManager;
import cn.rongcloud.sealmicandroid.manager.RoomManager;
import cn.rongcloud.sealmicandroid.rtc.RTCClient;
import cn.rongcloud.sealmicandroid.util.ToastUtil;

/**
 * 声音adapter，目前包括伴音和变声
 */
public class AudioDialogAdapter extends RecyclerView.Adapter<AudioDialogAdapter.AudioDialogViewHolder> {

    private List<BgAudioBean> datas;
    private String roomId;
    private BgmBean bgmBean;
    private String bgmContent;
    private CallItemClick callItemClick;

    public void setDatas(List<BgAudioBean> datas) {
        this.datas = datas;
        bgmBean = CacheManager.getInstance().getBgmBean();
        bgmContent = bgmBean.getBgmContent();
        for (BgAudioBean data : datas) {
            if (data.getContent().equals(bgmContent)) {
                data.setSelected(true);
            }
        }
        notifyDataSetChanged();
    }

    public void resetData(List<BgAudioBean> datas) {
        for (BgAudioBean data : datas) {
            data.setSelected(false);
        }
    }

    @NonNull
    @Override
    public AudioDialogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        roomId = CacheManager.getInstance().getRoomId();
        ItemBgAudioBinding itemBgAudioBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_bg_audio,
                parent,
                false);
        return new AudioDialogViewHolder(itemBgAudioBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull final AudioDialogViewHolder holder, final int position) {
        final BgAudioBean bgAudioBean = datas.get(position);
        final String content = bgAudioBean.getContent();
        holder.itemBgAudioBinding.itemBgAudio.setText(bgAudioBean.getContent());
        holder.itemBgAudioBinding.itemBgAudio.setSelected(bgAudioBean.isSelected());
        holder.itemBgAudioBinding.itemBgAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SealMicApp.getApplication().getResources().getString(R.string.nature).equals(content)) {
                    //自然
                    RTCClient.getInstance().startMix(3);
                } else if (SealMicApp.getApplication().getResources().getString(R.string.train_station).equals(content)) {
                    //火车站
                    RTCClient.getInstance().startMix(2);
                } else if (SealMicApp.getApplication().getResources().getString(R.string.airport).equals(content)) {
                    //机场
                    RTCClient.getInstance().startMix(1);
                } else if (SealMicApp.getApplication().getResources().getString(R.string.non).equals(content)) {
                    //无
                    RTCClient.getInstance().stopMix();
                } else {
                    //缺少素材
                    ToastUtil.showToast(SealMicApp.getApplication().getResources().getString(R.string.non_resource));
                }
                resetData(datas);
                bgAudioBean.setSelected(!holder.itemBgAudioBinding.itemBgAudio.isSelected());
                //保存选中的是哪个伴音，以便下次进来的时候显示选中状态
                CacheManager.getInstance().cacheBgmBean(roomId, content);
                notifyDataSetChanged();
                //回调点击事件
                callItemClick.callClick();
            }
        });
        holder.itemBgAudioBinding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return datas == null ? 0 : datas.size();
    }

    static class AudioDialogViewHolder extends RecyclerView.ViewHolder {

        private ItemBgAudioBinding itemBgAudioBinding;

        public AudioDialogViewHolder(@NonNull ItemBgAudioBinding itemBgAudioBinding) {
            super(itemBgAudioBinding.getRoot());
            this.itemBgAudioBinding = itemBgAudioBinding;
        }
    }

    public void setCallItemClick(CallItemClick callItemClick) {
        this.callItemClick = callItemClick;
    }

    public interface CallItemClick {
        void callClick();
    }
}
