package cn.rongcloud.sealmicandroid.ui.login;

import android.text.TextUtils;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import cn.rongcloud.sealmicandroid.R;
import cn.rongcloud.sealmicandroid.SealMicApp;
import cn.rongcloud.sealmicandroid.bean.repo.NetResult;
import cn.rongcloud.sealmicandroid.bean.repo.UserLoginRepo;
import cn.rongcloud.sealmicandroid.bean.repo.VisitorLoginRepo;
import cn.rongcloud.sealmicandroid.common.NetStateLiveData;
import cn.rongcloud.sealmicandroid.im.IMClient;
import cn.rongcloud.sealmicandroid.manager.CacheManager;
import cn.rongcloud.sealmicandroid.model.UserModel;
import cn.rongcloud.sealmicandroid.net.client.HttpClient;
import cn.rongcloud.sealmicandroid.util.PatternUtil;
import cn.rongcloud.sealmicandroid.util.ToastUtil;
import cn.rongcloud.sealmicandroid.util.log.SLog;
import io.rong.imlib.RongIMClient;

/**
 * 登录VM
 */
public class LoginViewModel extends ViewModel {

    private MutableLiveData<String> mobileNameMutableLiveData;
    private MutableLiveData<String> authCodeMutableLiveData;
    private MutableLiveData<Boolean> clickAuthCodeButtonLiveData;
    private MutableLiveData<Boolean> loginStatusLiveData;
    private UserModel userModel;

    private NetStateLiveData<UserLoginRepo> userLoginRepoLiveData;
    private NetStateLiveData<VisitorLoginRepo> visitorLoginRepoNetStateLiveData;
    private Observer<UserLoginRepo> userLoginRepoObserver;
    private Observer<VisitorLoginRepo> visitorLoginRepoObserver;

    private NetStateLiveData<NetResult<Void>> sendCodeNetStateLiveData;

    public LoginViewModel(UserModel userModel) {
        this.mobileNameMutableLiveData = new MutableLiveData<>();
        this.authCodeMutableLiveData = new MutableLiveData<>();
        this.userModel = userModel;
    }

    public MutableLiveData<String> getMobileNameMutableLiveData() {
        return mobileNameMutableLiveData;
    }

    public MutableLiveData<String> getAuthCodeMutableLiveData() {
        return authCodeMutableLiveData;
    }

    public MutableLiveData<Boolean> getLoginStatusLiveData() {
        if (loginStatusLiveData == null) {
            this.loginStatusLiveData = new MutableLiveData<>();
        }
        return loginStatusLiveData;
    }

    public void setLoginStatusLiveData(boolean loginStatus) {
        if (loginStatusLiveData == null) {
            this.loginStatusLiveData = new MutableLiveData<>();
        }
        this.loginStatusLiveData.postValue(loginStatus);
    }

    public MutableLiveData<Boolean> getClickAuthCodeButtonLiveData() {
        if (clickAuthCodeButtonLiveData == null) {
            this.clickAuthCodeButtonLiveData = new MutableLiveData<>();
        }
        return clickAuthCodeButtonLiveData;
    }

    public void setMobileNameMutableLiveData(String mobile) {
        if (mobileNameMutableLiveData == null) {
            this.mobileNameMutableLiveData = new MutableLiveData<>();
        }
        mobileNameMutableLiveData.postValue(mobile);
    }

    public void setAuthCodeMutableLiveData(String authCode) {
        if (authCodeMutableLiveData == null) {
            this.authCodeMutableLiveData = new MutableLiveData<>();
        }
        authCodeMutableLiveData.postValue(authCode);
    }

    public void setClickAuthCodeButtonLiveData(boolean isClick) {
        if (clickAuthCodeButtonLiveData == null) {
            this.clickAuthCodeButtonLiveData = new MutableLiveData<>();
        }
        clickAuthCodeButtonLiveData.postValue(isClick);
    }

    public NetStateLiveData<NetResult<Void>> getSendCodeNetStateLiveData() {
        if (sendCodeNetStateLiveData == null) {
            sendCodeNetStateLiveData = new NetStateLiveData<>();
        }
        return sendCodeNetStateLiveData;
    }

    public void login(String mobile, String authCode) {
        if (!TextUtils.isEmpty(mobile)
                && !TextUtils.isEmpty(authCode)) {
            if (PatternUtil.isMobile(mobile)) {
                setMobileNameMutableLiveData(mobile);
                setAuthCodeMutableLiveData(authCode);
                userLoginRepoLiveData = userModel.login(mobile, authCode);
                //用户登录观察者
                userLoginRepoObserver = new Observer<UserLoginRepo>() {
                    @Override
                    public void onChanged(UserLoginRepo userLoginRepo) {
                        if (userLoginRepo != null) {
                            final String imToken = userLoginRepo.getImToken();
                            final String authorization = userLoginRepo.getAuthorization();
                            final String userId = userLoginRepo.getUserId();
                            final String userName = userLoginRepo.getUserName();
                            final String portrait = userLoginRepo.getPortrait();
                            final int userType = userLoginRepo.getType();
                            IMClient.getInstance().disconnect();
                            IMClient.getInstance().connect(imToken, new RongIMClient.ConnectCallback() {
                                @Override
                                public void onSuccess(String s) {
                                    SLog.e(SLog.TAG_SEAL_MIC, "用户登录并连接IM");
                                    //清空本地游客信息，保存用户信息
                                    CacheManager.getInstance().cacheToken(imToken);
                                    CacheManager.getInstance().cacheAuth(authorization);
                                    CacheManager.getInstance().cacheUserId(userId);
                                    CacheManager.getInstance().cacheUserName(userName);
                                    CacheManager.getInstance().cacheUserPortrait(portrait);
                                    CacheManager.getInstance().cacheUserType(userType);
                                    CacheManager.getInstance().cacheIsLogin(true);
                                    //设置请求header
                                    HttpClient.getInstance().setAuthHeader(authorization);
                                    ToastUtil.showToast(R.string.login_success);
                                    setLoginStatusLiveData(true);
                                }

                                @Override
                                public void onError(RongIMClient.ConnectionErrorCode connectionErrorCode) {
                                    if (connectionErrorCode.getValue() == 20005) {
                                        ToastUtil.showToast(
                                                SealMicApp.getApplication().getResources().getString(R.string.send_code_invalid));
                                    }
                                }

                                @Override
                                public void onDatabaseOpened(RongIMClient.DatabaseOpenStatus databaseOpenStatus) {
                                }

                            });
                        }
                    }
                };
                userLoginRepoLiveData.observeForever(userLoginRepoObserver);
            } else {
                ToastUtil.showToast(R.string.input_right_mobile_error);
            }
        } else {
//            ToastUtil.showToast(R.string.mobile_or_authcode_notnull);
        }
    }

    public void obtainAuthCode(String mobile) {
        if (TextUtils.isEmpty(mobile)) {
            ToastUtil.showToast(R.string.mobile_not_empty);
            return;
        }
        if (!PatternUtil.isMobile(mobile)) {
            ToastUtil.showToast(R.string.input_right_mobile_error);
            return;
        }
        setMobileNameMutableLiveData(mobile);
        setClickAuthCodeButtonLiveData(true);
        sendCodeNetStateLiveData = userModel.sendCode(mobile);
        sendCodeNetStateLiveData.getNetStateMutableLiveData().observeForever(new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if (sendCodeNetStateLiveData.isSuccess()) {
                    SLog.e(SLog.TAG_SEAL_MIC, "验证码发送成功");
                }
            }
        });
    }

    public void visitorLogin() {
        if (!TextUtils.isEmpty(CacheManager.getInstance().getUserId())) {
            return;
        }
        visitorLoginRepoNetStateLiveData = userModel.visitorLogin();
        //游客登录观察者
        visitorLoginRepoObserver = new Observer<VisitorLoginRepo>() {
            @Override
            public void onChanged(VisitorLoginRepo visitorLoginRepo) {
                if (visitorLoginRepo != null) {
                    final String imToken = visitorLoginRepo.getImToken();
                    final String authorization = visitorLoginRepo.getAuthorization();
                    final String userId = visitorLoginRepo.getUserId();
                    final String userName = visitorLoginRepo.getUserName();
                    final String userPortrait = visitorLoginRepo.getPortrait();
                    final int userType = visitorLoginRepo.getType();
                    IMClient.getInstance().disconnect();
                    IMClient.getInstance().connect(imToken, new RongIMClient.ConnectCallback() {
                        @Override
                        public void onSuccess(String s) {
                            SLog.e(SLog.TAG_SEAL_MIC, "游客登录并连接IM");
                            //保存游客信息
                            CacheManager.getInstance().cacheToken(imToken);
                            CacheManager.getInstance().cacheAuth(authorization);
                            CacheManager.getInstance().cacheUserId(userId);
                            CacheManager.getInstance().cacheUserName(userName);
                            CacheManager.getInstance().cacheUserPortrait(userPortrait);
                            CacheManager.getInstance().cacheUserType(userType);
                            //设置请求header
                            HttpClient.getInstance().setAuthHeader(authorization);
                            SLog.e(SLog.TAG_SEAL_MIC, "authorization: " + authorization);
                            setLoginStatusLiveData(true);
                        }

                        @Override
                        public void onError(RongIMClient.ConnectionErrorCode connectionErrorCode) {

                        }

                        @Override
                        public void onDatabaseOpened(RongIMClient.DatabaseOpenStatus databaseOpenStatus) {

                        }

                    });
                }
            }
        };
        visitorLoginRepoNetStateLiveData.observeForever(visitorLoginRepoObserver);
    }

    /**
     * 因为在VM中LiveData调用的是observeForever，所以在VM结束时请务必务必务必对应做remove
     */
    @Override
    protected void onCleared() {
        super.onCleared();
        if (userLoginRepoLiveData != null && userLoginRepoObserver != null) {
            userLoginRepoLiveData.removeObserver(userLoginRepoObserver);
            userLoginRepoObserver = null;
        }
        if (visitorLoginRepoNetStateLiveData != null && userLoginRepoObserver != null) {
            visitorLoginRepoNetStateLiveData.removeObserver(visitorLoginRepoObserver);
            visitorLoginRepoObserver = null;
        }
    }
}
