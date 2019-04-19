package cn.rongcloud.sealmic.task;

import cn.rongcloud.sealmic.model.LoginInfo;
import cn.rongcloud.sealmic.model.UserInfo;
import cn.rongcloud.sealmic.net.HttpClient;
import cn.rongcloud.sealmic.net.SealMicRequest;
import cn.rongcloud.sealmic.net.model.LoginResult;
import cn.rongcloud.sealmic.task.callback.HandleRequestWrapper;

/**
 * 用户相关业务处理
 */
public class AuthManager {
    private static AuthManager instance;
    private SealMicRequest mRequest;
    private String currentUserId;

    public static AuthManager getInstance() {
        if (instance == null) {
            synchronized (AuthManager.class) {
                if (instance == null) {
                    instance = new AuthManager();
                }
            }
        }
        return instance;
    }

    private AuthManager() {
        mRequest = HttpClient.getInstance().getRequest();
    }

    /**
     * 用户登录
     *
     * @param deviceId
     * @param callBack
     */
    public void login(String deviceId, ResultCallback<LoginInfo> callBack) {
        mRequest.login(deviceId, new HandleRequestWrapper<LoginInfo, LoginResult>(callBack) {
            @Override
            public LoginInfo handleRequestResult(LoginResult dataResult) {
                if (dataResult != null) {

                    String auth = dataResult.getAuthorization();
                    AuthManager.this.currentUserId = dataResult.getUserId();
                    // 保存用户认证信息,用于请求其他api认证使用
                    if (auth != null) {
                        HttpClient.getInstance().setAuthHeader(auth);
                    }

                    LoginInfo loginInfo = new LoginInfo();
                    UserInfo userInfo = new UserInfo();
                    userInfo.setUserId(currentUserId);
                    loginInfo.setImToken(dataResult.getImToken());
                    loginInfo.setUserInfo(userInfo);

                    return loginInfo;
                }
                return null;
            }
        });
    }

    public String getCurrentUserId() {
        return currentUserId;
    }
}
