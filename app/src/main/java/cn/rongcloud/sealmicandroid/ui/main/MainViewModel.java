package cn.rongcloud.sealmicandroid.ui.main;

import android.view.View;

import androidx.lifecycle.ViewModel;

import cn.rongcloud.sealmicandroid.manager.NavOptionsRouterManager;

/**
 * 主界面VM
 */
public class MainViewModel extends ViewModel {

    private int position;

    public MainViewModel() {
    }

    public void gotoLoginFragment(View view) {
        NavOptionsRouterManager.getInstance().gotoLoginFragmentFromMain(view);
    }

    public void setMainListPosition(int position) {
        this.position = position;
    }

    public int getMainListPosition() {
        return position;
    }
}
