package cn.rongcloud.sealmicandroid.net.client;

import android.content.Context;
import android.content.SharedPreferences;

import cn.rongcloud.sealmicandroid.common.constant.SealMicConstant;
import cn.rongcloud.sealmicandroid.model.AppModel;
import cn.rongcloud.sealmicandroid.model.MicModel;
import cn.rongcloud.sealmicandroid.model.RoomModel;
import cn.rongcloud.sealmicandroid.model.UserModel;
import cn.rongcloud.sealmicandroid.net.SealMicUrl;

import static android.content.Context.MODE_PRIVATE;

/**
 * 网络请求与数据层(M层)对接类
 */
public class HttpClient {
    private static final String TAG = "HttpClient";
    private UserModel userModel;
    private RoomModel roomModel;
    private MicModel micModel;
    private AppModel appModel;
    private SharedPreferences cookieSharedPreferences;

    private HttpClient() {
    }

    public void init(Context context) {
        cookieSharedPreferences = context.getSharedPreferences(SealMicConstant.SP_NAME_NET, MODE_PRIVATE);
        RetrofitClient client = new RetrofitClient(context, SealMicUrl.DOMAIN);
        //与数据层交互
        userModel = new UserModel(client);
        roomModel = new RoomModel(client);
        micModel = new MicModel(client);
        appModel = new AppModel(client);
    }

    private static class HttpClientHelper {
        private static final HttpClient INSTANCE = new HttpClient();
    }

    public static HttpClient getInstance() {
        return HttpClientHelper.INSTANCE;
    }

    public UserModel getUserModel() {
        return userModel;
    }

    public RoomModel getRoomModel() {
        return roomModel;
    }

    public MicModel getMicModel() {
        return micModel;
    }

    public AppModel getAppModel() {
        return appModel;
    }

    /**
     * 设置用户登录认证
     */
    public void setAuthHeader(String auth) {
        SharedPreferences.Editor config = cookieSharedPreferences.edit();
        config.putString(SealMicConstant.SP_KEY_NET_HEADER_AUTH, auth);
        config.apply();
    }

    /**
     * 清除包括cookie和登录认证
     */
    public void clearRequestCache() {
        SharedPreferences.Editor config = cookieSharedPreferences.edit();
        config.remove(SealMicConstant.SP_KEY_NET_HEADER_AUTH);
        config.remove(SealMicConstant.SP_KEY_NET_COOKIE_SET);
        config.apply();
    }
}
