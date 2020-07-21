package cn.rongcloud.sealmicandroid.common;

/**
 * 全局结果回调
 *
 * @param <Result> 请求成功时的结果类
 */
public interface SealMicResultCallback<Result> {
    void onSuccess(Result result);

    void onFail(int errorCode);
}
