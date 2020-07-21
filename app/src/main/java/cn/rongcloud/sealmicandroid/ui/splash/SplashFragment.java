package cn.rongcloud.sealmicandroid.ui.splash;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import java.util.Timer;
import java.util.TimerTask;

import cn.rongcloud.sealmicandroid.BuildConfig;
import cn.rongcloud.sealmicandroid.R;
import cn.rongcloud.sealmicandroid.common.factory.CommonViewModelFactory;
import cn.rongcloud.sealmicandroid.databinding.FragmentSplashBinding;
import cn.rongcloud.sealmicandroid.ui.login.LoginViewModel;
import cn.rongcloud.sealmicandroid.util.log.SLog;

/**
 * 开屏页
 */
public class SplashFragment extends Fragment {

    private FragmentSplashBinding fragmentSplashBinding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        fragmentSplashBinding =
                DataBindingUtil.inflate(inflater, R.layout.fragment_splash, container, false);
        fragmentSplashBinding.setLifecycleOwner(this);
        return fragmentSplashBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LoginViewModel loginViewModel = new ViewModelProvider(this, new CommonViewModelFactory()).get(LoginViewModel.class);
        loginViewModel.visitorLogin();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Navigation.findNavController(fragmentSplashBinding.getRoot()).navigate(R.id.action_splashFragment_to_mainFragment);
            }
        }, 2000);
        fragmentSplashBinding.tvVersionName.setText(BuildConfig.VERSION_NAME);
        SLog.e(SLog.TAG_SEAL_MIC, "当前时间戳: " + System.currentTimeMillis());
    }

}