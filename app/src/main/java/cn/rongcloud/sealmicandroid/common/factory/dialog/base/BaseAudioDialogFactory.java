package cn.rongcloud.sealmicandroid.common.factory.dialog.base;

import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import cn.rongcloud.sealmicandroid.R;
import cn.rongcloud.sealmicandroid.bean.BgAudioBean;
import cn.rongcloud.sealmicandroid.common.adapter.AudioDialogAdapter;
import cn.rongcloud.sealmicandroid.common.divider.GridItemDecoration;

/**
 * 声音dialog工厂
 */
public abstract class BaseAudioDialogFactory extends BottomDialogFactory {

    @Override
    public Dialog buildDialog(FragmentActivity context) {
        final Dialog backgroundAudioDialog = super.buildDialog(context);
        View root = LayoutInflater.from(context).inflate(R.layout.dialog_background_audio, null);
        backgroundAudioDialog.setContentView(root);
        RecyclerView audioDialogRecyclerView = root.findViewById(R.id.rv_dialog_bg_audio);
        TextView dialogTitleText = root.findViewById(R.id.tv_dialog_title);
        dialogTitleText.setText(getTitle());
        List<BgAudioBean> contentList = new ArrayList<>();
        for (String content : getContents()) {
            BgAudioBean bgAudioBean = new BgAudioBean();
            bgAudioBean.setSelected(false);
            bgAudioBean.setContent(content);
            contentList.add(bgAudioBean);
        }
        AudioDialogAdapter audioDialogAdapter = new AudioDialogAdapter();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 3);
        audioDialogRecyclerView.setLayoutManager(gridLayoutManager);
        audioDialogRecyclerView.setAdapter(audioDialogAdapter);
        audioDialogRecyclerView.addItemDecoration(
                new GridItemDecoration(32, 3));
        audioDialogAdapter.setDatas(contentList);
        audioDialogAdapter.setCallItemClick(new AudioDialogAdapter.CallItemClick() {
            @Override
            public void callClick() {
                backgroundAudioDialog.cancel();
            }
        });
        Window window = backgroundAudioDialog.getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.width = context.getResources().getDisplayMetrics().widthPixels;
        return backgroundAudioDialog;
    }

    /**
     * 获取标题
     */
    public abstract String getTitle();

    /**
     * 获取内容
     */
    public abstract String[] getContents();
}
