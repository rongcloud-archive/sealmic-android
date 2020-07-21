package cn.rongcloud.sealmicandroid.net.service;

import cn.rongcloud.sealmicandroid.bean.repo.NetResult;
import cn.rongcloud.sealmicandroid.bean.req.MicAcceptReq;
import cn.rongcloud.sealmicandroid.bean.req.MicApplyReq;
import cn.rongcloud.sealmicandroid.bean.req.MicQuitReq;
import cn.rongcloud.sealmicandroid.bean.req.MicStateReq;
import cn.rongcloud.sealmicandroid.bean.req.MicTransferHostReq;
import cn.rongcloud.sealmicandroid.bean.req.MicTransferHostResultReq;
import cn.rongcloud.sealmicandroid.common.NetStateLiveData;
import cn.rongcloud.sealmicandroid.net.SealMicUrl;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.PUT;

/**
 * 麦位模块请求封装
 */
public interface MicService {

    @POST(SealMicUrl.ROOM_MIC_ACCEPT)
    NetStateLiveData<NetResult<Void>> micAccept(@Body MicAcceptReq micAcceptReq);

    @POST(SealMicUrl.ROOM_MIC_REJECT)
    NetStateLiveData<NetResult<Void>> micReject(@Body MicAcceptReq micAcceptReq);

    @POST(SealMicUrl.ROOM_MIC_INVITE)
    NetStateLiveData<NetResult<Void>> micInvite(@Body MicAcceptReq micAcceptReq);

    @POST(SealMicUrl.ROOM_MIC_KICK)
    NetStateLiveData<NetResult<Void>> micKick(@Body MicAcceptReq micAcceptReq);

    @PUT(SealMicUrl.ROOM_MIC_STATE)
    NetStateLiveData<NetResult<Void>> micState(@Body MicStateReq micStateReq);

    @POST(SealMicUrl.ROOM_MIC_TRANSFER_HOST)
    NetStateLiveData<NetResult<Void>> micTransferHost(@Body MicTransferHostReq micTransferHostAgreeReq);

    @POST(SealMicUrl.ROOM_MIC_TRANSFER_HOST_REJECT)
    NetStateLiveData<NetResult<Void>> micTransferHostReject(@Body MicTransferHostResultReq micTransferHostAgreeReq);

    @POST(SealMicUrl.ROOM_MIC_TRANSFER_HOST_ACCEPT)
    NetStateLiveData<NetResult<Void>> micTransferHostAccept(@Body MicTransferHostResultReq micTransferHostAgreeReq);

    @POST(SealMicUrl.ROOM_MIC_TAKE_OVER_HOST)
    NetStateLiveData<NetResult<Void>> micTakeOverHost(@Body MicTransferHostResultReq micTransferHostResultReq);

    @POST(SealMicUrl.ROOM_MIC_TAKE_OVER_HOST_REJECT)
    NetStateLiveData<NetResult<Void>> micTakeOverHostReject(@Body MicAcceptReq micAcceptReq);

    @POST(SealMicUrl.ROOM_MIC_TAKE_OVER_HOST_ACCEPT)
    NetStateLiveData<NetResult<Void>> micTakeOverHostAccept(@Body MicAcceptReq micAcceptReq);

    @POST(SealMicUrl.ROOM_MIC_QUIT)
    NetStateLiveData<NetResult<Void>> micQuit(@Body MicQuitReq micQuitReq);

    @POST(SealMicUrl.ROOM_MIC_APPLY)
    NetStateLiveData<NetResult<Void>> micApply(@Body MicApplyReq micApplyReq);

}
