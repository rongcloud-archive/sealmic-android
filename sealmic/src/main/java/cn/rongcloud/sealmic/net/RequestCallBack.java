package cn.rongcloud.sealmic.net;

/**
 * 网络请求结果接口
 * @param <R>   请求成功时的结果类
 */
public interface RequestCallBack<R> {
    void onSuccess(R result);

    void onFail(int errorCode);
}
