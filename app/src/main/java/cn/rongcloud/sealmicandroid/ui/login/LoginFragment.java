package cn.rongcloud.sealmicandroid.ui.login;

import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.util.Objects;

import cn.rongcloud.sealmicandroid.R;
import cn.rongcloud.sealmicandroid.SealMicApp;
import cn.rongcloud.sealmicandroid.common.adapter.TextWatcherAdapter;
import cn.rongcloud.sealmicandroid.common.factory.CommonViewModelFactory;
import cn.rongcloud.sealmicandroid.common.lifecycle.MainObserver;
import cn.rongcloud.sealmicandroid.databinding.FragmentLoginBinding;
import cn.rongcloud.sealmicandroid.manager.NavOptionsRouterManager;
import cn.rongcloud.sealmicandroid.ui.widget.CustomTitleBar;
import cn.rongcloud.sealmicandroid.ui.widget.spannable.AgreeClickableSpan;
import cn.rongcloud.sealmicandroid.util.KeyBoardUtil;
import cn.rongcloud.sealmicandroid.util.log.SLog;

/**
 * 登录界面
 */
public class LoginFragment extends Fragment {

    private static final long MILLIS_IN_FUTURE = 60000;
    private static final long COUNT_DOWN_INTERVAL = 1000;
    private static final String TAG = "LoginFragment";
    private LoginViewModel loginViewModel;
    private FragmentLoginBinding fragmentLoginBinding;
    //是否在倒计时
    private boolean isCount = false;

    private final String AGREEMENT = "《注册条款》";

    public LoginFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getActivity()).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        getLifecycle().addObserver(new MainObserver(TAG));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        loginViewModel = new ViewModelProvider(this,
                new CommonViewModelFactory()).get(LoginViewModel.class);
        fragmentLoginBinding =
                DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false);
        fragmentLoginBinding.setLoginViewModel(loginViewModel);
        fragmentLoginBinding.setLifecycleOwner(this);
        return fragmentLoginBinding.getRoot();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        KeyBoardUtil.closeKeyBoard(requireActivity(), getView());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        String agreeContent = fragmentLoginBinding.loginAgree.getText().toString();
        if (agreeContent.contains(AGREEMENT)) {
            int i = agreeContent.indexOf(AGREEMENT);
            int endP = i + AGREEMENT.length();
            SpannableString spannableString = new SpannableString(agreeContent);
            //设置开头区域背景
            ForegroundColorSpan colorSpanStart = new ForegroundColorSpan(
                    ContextCompat.getColor(getContext(), R.color.color_5C6970));
            spannableString.setSpan(colorSpanStart, 0, i, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            //设置可点击区域背景
            ForegroundColorSpan colorSpan = new ForegroundColorSpan(
                    ContextCompat.getColor(getContext(), R.color.color_00AAFF));
            spannableString.setSpan(colorSpan, i, endP, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            //设置点击事件
            AgreeClickableSpan customClickableSpan = new AgreeClickableSpan(getView());
            spannableString.setSpan(customClickableSpan, i, endP, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            fragmentLoginBinding.loginAgree.setMovementMethod(LinkMovementMethod.getInstance());
            fragmentLoginBinding.loginAgree.setText(spannableString);
        }
        fragmentLoginBinding.loginTopBar.setTvMiddleIsGong();
        fragmentLoginBinding.loginTopBar.setLeftImage(R.mipmap.ic_back_black);
        fragmentLoginBinding.loginTopBar.setLeftTitle(getString(R.string.login_commit));
        fragmentLoginBinding.loginTopBar.setTitleClickListener(new CustomTitleBar.TitleClickListener() {

            @Override
            public void onLeftClick() {
//                NavOptionsRouterManager.getInstance().backUp(getView());
                NavOptionsRouterManager.getInstance().gotoMainFragmentFromLogin(getView());
            }

            @Override
            public void onRightClick() {
            }

            @Override
            public void onTitleLongClick() {

            }
        });
        fragmentLoginBinding.loginTopBar.hideRightText();
        fragmentLoginBinding.loginCommit.setAlpha(0.5f);
        fragmentLoginBinding.mobileEdit.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable editable) {
                String mobile = fragmentLoginBinding.mobileEdit.getText().toString();
                loginViewModel.setMobileNameMutableLiveData(mobile);
                showButton();
            }
        });
        fragmentLoginBinding.authCodeEdit.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable editable) {
                loginViewModel.setAuthCodeMutableLiveData(fragmentLoginBinding.authCodeEdit.getText().toString());
                showButton();
            }
        });
        loginViewModel.getClickAuthCodeButtonLiveData().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    tickAuthCodeButton();
                }
            }
        });
        //下面两个监听方法是为了键盘弹起是布局不会被顶上去
        fragmentLoginBinding.mobileEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    fragmentLoginBinding.loginNulltitle.setVisibility(View.GONE);
                } else {
                    fragmentLoginBinding.imageView2.setVisibility(View.VISIBLE);
                    fragmentLoginBinding.loginTitle.setVisibility(View.VISIBLE);
                }
            }
        });
        fragmentLoginBinding.authCodeEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    fragmentLoginBinding.loginNulltitle.setVisibility(View.GONE);
                } else {
                    fragmentLoginBinding.imageView2.setVisibility(View.VISIBLE);
                    fragmentLoginBinding.loginTitle.setVisibility(View.VISIBLE);
                }
            }
        });
        loginViewModel.getLoginStatusLiveData().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    KeyBoardUtil.closeKeyBoard(requireActivity(), getView());
                    NavOptionsRouterManager.getInstance().backUp(getView());
                }
            }
        });
//        fragmentLoginBinding.loginAgree.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                NavOptionsRouterManager.getInstance().gotoAgreeFragmentFromLogin(getView());
//            }
//        });
    }

    private void showButton() {
        String mobileTv = fragmentLoginBinding.mobileEdit.getText().toString();
        String authCode = fragmentLoginBinding.authCodeEdit.getText().toString();

        if (TextUtils.isEmpty(mobileTv) || TextUtils.isEmpty(authCode)) {

            fragmentLoginBinding.loginCommit.setAlpha(0.5f);
        } else {
            fragmentLoginBinding.loginCommit.setAlpha(1);
        }

        if (!isCount) {
            if (TextUtils.isEmpty(mobileTv)) {
                fragmentLoginBinding.authCodeButton.setBackground(ContextCompat.getDrawable(SealMicApp.getApplication(), R.mipmap.bg_button_rect));
            } else {
                fragmentLoginBinding.authCodeButton.setBackground(ContextCompat.getDrawable(SealMicApp.getApplication(), R.mipmap.bg_button_rect_select));
            }
        }

    }

    private void tickAuthCodeButton() {
        new CountDownTimer(MILLIS_IN_FUTURE, COUNT_DOWN_INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (getContext() != null) {
                    isCount = true;
                    fragmentLoginBinding.authCodeButton.setText(String.format(getString(R.string.secs),
                            String.valueOf(millisUntilFinished / COUNT_DOWN_INTERVAL)));
                    fragmentLoginBinding.authCodeButton.setClickable(false);
                    fragmentLoginBinding.authCodeButton.setBackground(ContextCompat.getDrawable(SealMicApp.getApplication(), R.mipmap.bg_button_rect));
//                    String authCode = "我是一个验证码";
//                    loginViewModel.setAuthCodeMutableLiveData(authCode);
                }
            }

            @Override
            public void onFinish() {
                isCount = false;
                fragmentLoginBinding.authCodeButton.setClickable(true);
                fragmentLoginBinding.authCodeButton.setText(SealMicApp.getApplication().getResources().getString(R.string.get_auth_code));
                fragmentLoginBinding.authCodeButton.setBackground(ContextCompat.getDrawable(SealMicApp.getApplication(), R.mipmap.bg_button_rect_select));
            }
        }.start();
    }
}
