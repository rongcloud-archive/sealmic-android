package cn.rongcloud.sealmic.task.callback;


import cn.rongcloud.sealmic.net.RequestCallBack;
import cn.rongcloud.sealmic.task.ResultCallback;
import cn.rongcloud.sealmic.task.ThreadManager;

/**
 * 将网络请求的结果 RequestCallBack 转换为任务处理结果 RequestCallBack，并切换至主线程
 * @param <RequestResult>
 */
public class RequestWrapper<RequestResult> implements RequestCallBack<RequestResult> {
    private ResultCallback<RequestResult> mCallBack;
    private ThreadManager mThreadManager;

    public RequestWrapper(ResultCallback<RequestResult> callBack) {
        mCallBack = callBack;
        mThreadManager = ThreadManager.getInstance();
    }

    @Override
    public void onSuccess(final RequestResult result) {
        if (mCallBack == null) return;

        mThreadManager.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                mCallBack.onSuccess(result);
            }
        });
    }


    @Override
    public void onFail(final int errorCode) {
        if (mCallBack == null) return;
        mThreadManager.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                mCallBack.onFail(errorCode);
            }
        });
    }

    public ResultCallback<RequestResult> getCallBack() {
        return mCallBack;
    }

    public ThreadManager getTaskManager() {
        return mThreadManager;
    }
}
