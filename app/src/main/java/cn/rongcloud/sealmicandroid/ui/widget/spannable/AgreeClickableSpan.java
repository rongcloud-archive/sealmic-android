package cn.rongcloud.sealmicandroid.ui.widget.spannable;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import androidx.annotation.NonNull;

import cn.rongcloud.sealmicandroid.manager.NavOptionsRouterManager;


/**
 * Created by fushangkai on 2021/03/08
 */
public class AgreeClickableSpan extends ClickableSpan {

    private View view;

    public AgreeClickableSpan(View view) {
        this.view = view;
    }

    @Override
    public void updateDrawState(@NonNull TextPaint ds) {
        ds.setUnderlineText(false);
    }

    @Override
    public void onClick(@NonNull View widget) {
        NavOptionsRouterManager.getInstance().gotoAgreeFragmentFromLogin(view);
    }
}
