package cn.rongcloud.sealmicandroid.net.client;

import android.content.Context;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import cn.rongcloud.sealmicandroid.common.constant.SealMicConstant;
import cn.rongcloud.sealmicandroid.common.factory.LiveDataCallFactory;
import cn.rongcloud.sealmicandroid.manager.CacheManager;
import cn.rongcloud.sealmicandroid.util.log.SLog;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 网络请求基础配置
 */
public class RetrofitClient {
    private Retrofit retrofit;

    RetrofitClient(Context context, String baseUrl) {
        OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder()
                .addInterceptor(new AddHeaderInterceptor(context))
                .connectTimeout(SealMicConstant.CONNECT_TIME_OUT, TimeUnit.SECONDS)
                .readTimeout(SealMicConstant.READ_TIME_OUT, TimeUnit.SECONDS)
                .writeTimeout(SealMicConstant.WRITE_TIME_OUT, TimeUnit.SECONDS);

        retrofit = new Retrofit.Builder()
                .client(okHttpBuilder.build())
                //设置网络请求的Url地址
                .baseUrl(baseUrl)
                //设置数据解析器
                .addConverterFactory(GsonConverterFactory.create())
                //retrofit返回的原生Call类型转为LiveData
                .addCallAdapterFactory(new LiveDataCallFactory())
                .build();
    }

    /**
     * request之前添加auth至header
     */
    private class AddHeaderInterceptor implements Interceptor {
        private Context context;

        private AddHeaderInterceptor(Context context) {
            this.context = context;
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request.Builder builder = chain.request().newBuilder();
            //添加用户登录认证
            String auth = CacheManager.getInstance().getAuth();
            SLog.d("auth", auth);
            if (auth != null) {
                builder.addHeader("Authorization", auth);
            }
            builder.addHeader("Content-Type", "application/json");

            return chain.proceed(builder.build());
        }
    }

    public <T> T createService(Class<T> service) {
        return retrofit.create(service);
    }
}
