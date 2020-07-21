package cn.rongcloud.sealmicandroid.common.adapter;

import androidx.annotation.NonNull;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import cn.rongcloud.sealmicandroid.bean.repo.NetResult;
import cn.rongcloud.sealmicandroid.common.NetStateLiveData;
import cn.rongcloud.sealmicandroid.common.constant.ErrorCode;
import cn.rongcloud.sealmicandroid.common.constant.SealMicErrorMsg;
import cn.rongcloud.sealmicandroid.manager.ThreadManager;
import cn.rongcloud.sealmicandroid.util.ToastUtil;
import cn.rongcloud.sealmicandroid.util.log.SLog;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * retrofit返回的结果由原生的call转为LiveData
 */
public class LiveDataCallAdapter<R> implements CallAdapter<NetResult<R>, NetStateLiveData<R>> {

    private Type type;

    public LiveDataCallAdapter(Type type) {
        this.type = type;
    }

    @NonNull
    @Override
    public Type responseType() {
        return type;
    }

    @Override
    public NetStateLiveData<R> adapt(final Call<NetResult<R>> call) {
        final NetStateLiveData<R> netStateLiveData = new NetStateLiveData<>();
        //CAS机制保证线程安全
        AtomicBoolean liveDataFlag = new AtomicBoolean(false);
        if (liveDataFlag.compareAndSet(false, true)) {
            call.enqueue(new Callback<NetResult<R>>() {
                @Override
                public void onResponse(Call<NetResult<R>> call, Response<NetResult<R>> response) {
                    //请求成功之后判断响应体
                    final NetResult<R> netResult = response.body();
                    boolean b = response.isSuccessful();
                    ResponseBody r = response.raw().body();
                    if (netResult == null) {
                        //响应体为空
                        SLog.e(SLog.TAG_NET, "url:" + call.request().url().toString() + ", no response body");
                        //响应体为空时成功
                        netStateLiveData.postError(ErrorCode.NETWORK_ERROR.getCode());
                    } else {
                        //响应体不为空
                        if (response.isSuccessful()) {
                            //不为空时成功
                            if (netResult.getData() == null) {
                                //升级版本的接口具有可变性，所以加上一层判断
                                //40002==没有新版本
                                if (netResult.getCode() == SealMicErrorMsg.NO_NEW_VERSION.getCode()) {
                                    //强制返回升级接口的数据
                                    netStateLiveData.postValueAndSuccess(netResult.getCode(), netResult.getData());
                                } else {
                                    //没有返回data字段对应的信息
                                    netStateLiveData.postSuccess(netResult.getCode());
                                }
                            } else {
                                //netResult.getData()有可能是jsonObject，也有可能是jsonArray，解析方式有区别
                                if (netResult.getData() instanceof List) {
                                    //netResult.getData()为jsonArray
                                    netStateLiveData.postValueAndSuccess(netResult.getCode(), (R) netResult);
                                } else {
                                    //netResult.getData()为jsonObject
                                    netStateLiveData.postValueAndSuccess(netResult.getCode(), netResult.getData());
                                }
                            }
                        } else {
                            //不为空时失败
                            SLog.e(SLog.TAG_NET, "url:" + call.request().url().toString()
                                    + " ,errorMsg:" + netResult.getCode() + ", errorDetail:" + netResult.getMsg());
                            netStateLiveData.postError(netResult.getCode());
                        }
                        ThreadManager.getInstance().runOnUIThread(new Runnable() {
                            @Override
                            public void run() {
                                SealMicErrorMsg.fromCode(netResult.getCode());
                            }
                        });
                    }
                }

                @Override
                public void onFailure(Call<NetResult<R>> call, Throwable t) {
                    //压根就请求失败了
                    SLog.e(SLog.TAG_NET, call.request().url().toString() + " - " + t.getMessage());
                    netStateLiveData.postError(ErrorCode.NETWORK_ERROR.getCode());
                }
            });
        }
        return netStateLiveData;
    }

}
