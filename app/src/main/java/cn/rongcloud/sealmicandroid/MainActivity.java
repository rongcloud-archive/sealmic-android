package cn.rongcloud.sealmicandroid;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import cn.rongcloud.sealmicandroid.common.lifecycle.MainObserver;
import cn.rongcloud.sealmicandroid.databinding.MainActivityBinding;

/**
 * 项目界面总容器
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivityBinding mainActivityBinding = DataBindingUtil.setContentView(this, R.layout.main_activity);
        mainActivityBinding.setLifecycleOwner(this);
        getLifecycle().addObserver(new MainObserver(MainActivity.class.getSimpleName()));
    }
}
