package cn.rongcloud.sealmicandroid.common.factory.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import cn.rongcloud.sealmicandroid.R;
import cn.rongcloud.sealmicandroid.common.factory.dialog.base.FullDialogFactory;

/**
 * 选中的礼物后的dialog
 */
public class SelectedGiftDialogFactory extends FullDialogFactory {

    private Drawable selectedGift;
    private TextView selectedTitleTextView;
    private ImageView selectedImg;
    private TextView selectedName;

    public SelectedGiftDialogFactory setSelectedGift(Drawable selectedGift) {
        this.selectedGift = selectedGift;
        return this;
    }

    @Override
    public Dialog buildDialog(FragmentActivity context) {
        final Dialog selectedGiftDialog = super.buildDialog(context);
        selectedGiftDialog.setContentView(R.layout.dialog_selected_gift);
        selectedImg = selectedGiftDialog.findViewById(R.id.iv_selected_gift);
        selectedName = selectedGiftDialog.findViewById(R.id.tv_selected_gift_name);
        selectedTitleTextView = selectedGiftDialog.findViewById(R.id.tv_selected_gift_title);
        selectedImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedGiftDialog.cancel();
            }
        });
        if (selectedGift != null) {
            selectedImg.setImageDrawable(selectedGift);
        }
        WindowManager.LayoutParams layoutParams = selectedGiftDialog.getWindow().getAttributes();
        layoutParams.width = context.getResources().getDisplayMetrics().widthPixels;
        layoutParams.height = context.getResources().getDisplayMetrics().heightPixels;
        RelativeLayout constraintLayout = selectedGiftDialog.findViewById(R.id.layout_selected_gift);
        constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedGiftDialog.cancel();
            }
        });
        selectedGiftDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                new CountDownTimer(3 * 1000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                        if (selectedGiftDialog.isShowing()) {
                            selectedGiftDialog.cancel();
                        }
                    }
                }.start();
            }
        });
        return selectedGiftDialog;
    }

    public void setGiftContent(String title) {
        selectedTitleTextView.setText(title);
    }

    public void setGiftTitle(String name) {
        selectedName.setText(name + " ");
    }

    public void setGiftImg(int resouce) {

    }

}
