package cn.rongcloud.sealmic.net;

import android.content.Context;
import android.content.SharedPreferences;

import cn.rongcloud.sealmic.constant.NetConstant;
import cn.rongcloud.sealmic.net.retrofit.RetrofitClient;

import static android.content.Context.MODE_PRIVATE;


public class HttpClient {
    private static final String TAG = "HttpClient";
    private static HttpClient sInstance;
    private Context mContext;
    private RetrofitClient mClient;
    private SealMicRequest mRequest;

    private HttpClient() {
    }

    public void init(Context context) {
        mContext = context;
        mClient = new RetrofitClient(context, SealMicUrls.DOMAIN);
        mRequest = new SealMicRequest(mClient);
    }

    public static HttpClient getInstance() {
        if (sInstance == null) {
            synchronized (HttpClient.class) {
                if (sInstance == null) {
                    sInstance = new HttpClient();
                }
            }
        }

        return sInstance;
    }

    public SealMicRequest getRequest() {
        return mRequest;
    }

    /**
     * 设置用户登录认证
     *
     * @param auth
     */
    public void setAuthHeader(String auth) {
        SharedPreferences.Editor config = mContext.getSharedPreferences(NetConstant.SP_NAME_NET, MODE_PRIVATE)
                .edit();
        config.putString(NetConstant.SP_KEY_NET_HEADER_AUTH, auth);
        config.commit();
    }

    /**
     * 清除包括cookie和登录认证
     */
    public void clearRequestCache() {
        SharedPreferences.Editor config = mContext.getSharedPreferences(NetConstant.SP_NAME_NET, MODE_PRIVATE)
                .edit();
        config.remove(NetConstant.SP_KEY_NET_HEADER_AUTH);
        config.remove(NetConstant.SP_KEY_NET_COOKIE_SET);
        config.commit();
    }
}
