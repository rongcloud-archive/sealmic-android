package cn.rongcloud.sealmic.task.callback;

import cn.rongcloud.sealmic.net.RequestCallBack;
import cn.rongcloud.sealmic.task.ResultCallback;
import cn.rongcloud.sealmic.task.ThreadManager;

public abstract class HandleRequestWrapper<Result, RequestResult> implements RequestCallBack<RequestResult> {
    private ResultCallback<Result> mCallBack;
    private ThreadManager mThreadManager;

    public HandleRequestWrapper(ResultCallback<Result> callBack) {
        mCallBack = callBack;
        mThreadManager = ThreadManager.getInstance();
    }

    @Override
    public void onSuccess(final RequestResult request) {
        mThreadManager.runOnWorkThread(new Runnable() {
            @Override
            public void run() {
                final Result result = handleRequestResult(request);
                mThreadManager.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        if(mCallBack != null) {
                            mCallBack.onSuccess(result);
                        }
                    }
                });
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

    /**
     * 需要预处理请求结果
     *
     * @param result
     */
    public abstract Result handleRequestResult(final RequestResult result);


    public ResultCallback<Result> getCallBack() {
        return mCallBack;
    }

    public ThreadManager getTaskManager() {
        return mThreadManager;
    }
}
