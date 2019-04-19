package cn.rongcloud.sealmic.net.retrofit;

import cn.rongcloud.sealmic.constant.ErrorCode;
import cn.rongcloud.sealmic.constant.ServerErrorCode;
import cn.rongcloud.sealmic.net.RequestCallBack;
import cn.rongcloud.sealmic.net.model.Result;
import cn.rongcloud.sealmic.utils.log.SLog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CallBackWrapper<R> implements Callback<Result<R>> {
    private RequestCallBack<R> mCallBack;

    public CallBackWrapper(RequestCallBack<R> callBack) {
        mCallBack = callBack;
    }

    @Override
    public void onResponse(Call<Result<R>> call, Response<Result<R>> response) {
        Result<R> body = response.body();
        if (body != null) {
            int errCode = body.getErrCode();
            if (errCode == 0) {
                mCallBack.onSuccess(body.getDataResult());
            } else {
                SLog.e(SLog.TAG_NET, "url:" + call.request().url().toString()
                        + " ,errorMsg:" + body.getErrMsg() + ", errorDetail:" + body.getErrDetail());
                // 将网络请求 errorCode 转换为ErrorCode对应错误枚举
                ErrorCode errorCode;
                switch (errCode) {
                    case ServerErrorCode.MIC_POSITION_HAS_BEEN_HOLD: // 跳麦，麦位上已有人
                        errorCode = ErrorCode.MIC_POSITION_HAS_BEEN_HOLD;
                        break;
                    case ServerErrorCode.ROOM_CREATE_ROOM_OVER_LIMIT: // 创建房间超过上限
                        errorCode = ErrorCode.ROOM_CREATE_ROOM_OVER_LIMIT;
                        break;
                    case ServerErrorCode.ROOM_JOIN_MEMBER_OVER_LIMIT: // 房间人数超过上限
                        errorCode = ErrorCode.ROOM_JOIN_MEMBER_OVER_LIMIT;
                        break;
                    case ServerErrorCode.USER_ALREADY_ON_MIC_POSITION:
                        errorCode = ErrorCode.MIC_USER_ALREADY_ON_OTHER_POSITION;
                        break;
                    default:
                        errorCode = ErrorCode.RESULT_FAILED;
                }
                mCallBack.onFail(errorCode.getCode());
            }
        } else {
            SLog.e(SLog.TAG_NET, "url:" + call.request().url().toString() + ", no response body");
            mCallBack.onFail(ErrorCode.RESULT_ERROR.getCode());
        }
    }

    @Override
    public void onFailure(Call<Result<R>> call, Throwable t) {
        SLog.e(SLog.TAG_NET, call.request().url().toString() + " - " + (t != null ? t.getMessage() : ""));
        mCallBack.onFail(ErrorCode.NETWORK_ERROR.getCode());
    }
}
