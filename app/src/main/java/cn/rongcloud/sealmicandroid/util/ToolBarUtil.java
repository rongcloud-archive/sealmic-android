package cn.rongcloud.sealmicandroid.util;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ToolBarUtil {
    public static TextView getToolbarTitleView(Context context, Toolbar toolbar) {
        AppCompatActivity activity = (AppCompatActivity) context;
        ActionBar actionBar = activity.getSupportActionBar();
        CharSequence actionbarTitle = null;
        if (actionBar != null) {
            actionbarTitle = actionBar.getTitle();
            actionbarTitle = TextUtils.isEmpty(actionbarTitle) ? toolbar.getTitle() : actionbarTitle;
        }
        if (TextUtils.isEmpty(actionbarTitle)) {
            return null;
        }
        // can't find if title not set
        for (int i = 0; i < toolbar.getChildCount(); i++) {
            View v = toolbar.getChildAt(i);
            if (v != null && v instanceof TextView) {
                TextView t = (TextView) v;
                CharSequence title = t.getText();
                if (!TextUtils.isEmpty(title) && actionbarTitle.equals(title) && t.getId() == View.NO_ID) {
                    //Toolbar does not assign id to views with layout params SYSTEM, hence getId() == View.NO_ID
                    //in same manner subtitle TextView can be obtained.
                    return t;
                }
            }
        }
        return null;
    }

    public static TextView getToolbarSubTitleView(Context context, Toolbar toolbar) {
        AppCompatActivity activity = (AppCompatActivity) context;
        ActionBar actionBar = activity.getSupportActionBar();
        CharSequence actionbarTitle = null;
        if (actionBar != null) {
            actionbarTitle = actionBar.getSubtitle();
            actionbarTitle = TextUtils.isEmpty(actionbarTitle) ? toolbar.getSubtitle() : actionbarTitle;
        }
        if (TextUtils.isEmpty(actionbarTitle)) {
            return null;
        }
        // can't find if title not set
        for (int i = 0; i < toolbar.getChildCount(); i++) {
            View v = toolbar.getChildAt(i);
            if (v != null && v instanceof TextView) {
                TextView t = (TextView) v;
                CharSequence title = t.getText();
                if (!TextUtils.isEmpty(title) && actionbarTitle.equals(title) && t.getId() == View.NO_ID) {
                    //Toolbar does not assign id to views with layout params SYSTEM, hence getId() == View.NO_ID
                    //in same manner subtitle TextView can be obtained.
                    return t;
                }
            }
        }
        return null;
    }

    public static ImageView getToolbarLogoView(Context context, Toolbar toolbar) {
        AppCompatActivity activity = (AppCompatActivity) context;
        ActionBar actionBar = activity.getSupportActionBar();
        CharSequence actionbarTitle = null;

        // can't find if title not set
        for (int i = 0; i < toolbar.getChildCount(); i++) {
            View v = toolbar.getChildAt(i);
            if (v != null && v instanceof ImageView) {
                ImageView t = (ImageView) v;
                return t;
            }
        }
        return null;
    }

    public static ImageButton getNavButtonView(Context context, Toolbar toolbar) {
        AppCompatActivity activity = (AppCompatActivity) context;
        ActionBar actionBar = activity.getSupportActionBar();
        CharSequence actionbarTitle = null;

        // can't find if title not set
        for (int i = 0; i < toolbar.getChildCount(); i++) {
            View v = toolbar.getChildAt(i);
            if (v != null && v instanceof ImageButton) {
                ImageButton t = (ImageButton) v;
                return t;
            }
        }
        return null;
    }
}
