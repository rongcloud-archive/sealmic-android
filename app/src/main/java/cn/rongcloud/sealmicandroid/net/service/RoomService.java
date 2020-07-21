package cn.rongcloud.sealmicandroid.net.service;

import java.util.List;

import cn.rongcloud.sealmicandroid.bean.repo.CreateRoomRepo;
import cn.rongcloud.sealmicandroid.bean.repo.NetResult;
import cn.rongcloud.sealmicandroid.bean.repo.RoomDetailRepo;
import cn.rongcloud.sealmicandroid.bean.repo.RoomListRepo;
import cn.rongcloud.sealmicandroid.bean.repo.RoomMemberRepo;
import cn.rongcloud.sealmicandroid.bean.req.CreateRoomReq;
import cn.rongcloud.sealmicandroid.bean.req.MessageBroadCastReq;
import cn.rongcloud.sealmicandroid.bean.req.RoomBanUserReq;
import cn.rongcloud.sealmicandroid.bean.req.RoomKickUserReq;
import cn.rongcloud.sealmicandroid.bean.req.RoomSettingReq;
import cn.rongcloud.sealmicandroid.common.NetStateLiveData;
import cn.rongcloud.sealmicandroid.net.SealMicUrl;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * 房间模块请求封装
 */
public interface RoomService {

    @POST(SealMicUrl.ROOM_CREATE)
    NetStateLiveData<CreateRoomRepo> createRoom(@Body CreateRoomReq createRoomReq);

    @PUT(SealMicUrl.ROOM_SETTING)
    NetStateLiveData<NetResult<Void>> roomSetting(@Body RoomSettingReq roomSettingReq);

    @GET(SealMicUrl.ROOM_LIST)
    NetStateLiveData<RoomListRepo> roomList(@Query("fromRoomId") String fromRoomId, @Query("size") int size);

    @GET(SealMicUrl.ROOM_DETAIL)
    NetStateLiveData<RoomDetailRepo> roomDetail(@Path("roomId") String roomId);

    @GET(SealMicUrl.ROOM_MEMBERS)
    NetStateLiveData<NetResult<List<RoomMemberRepo.MemberBean>>> roomMembers(@Path("roomId") String roomId);

    @GET(SealMicUrl.ROOM_APPLY_MIC_MEMBERS)
    NetStateLiveData<NetResult<List<RoomMemberRepo.MemberBean>>> micMembers(@Path("roomId") String roomId);

    @GET(SealMicUrl.ROOM_GAG_MEMBERS)
    NetStateLiveData<NetResult<List<RoomMemberRepo.MemberBean>>> gagMembers(@Path("roomId") String roomId);

    @POST(SealMicUrl.ROOM_USER_KICK)
    NetStateLiveData<NetResult<Void>> kickMember(@Body RoomKickUserReq roomKickUserReq);

    @POST(SealMicUrl.ROOM_USER_GAG)
    NetStateLiveData<NetResult<Void>> banMember(@Body RoomBanUserReq roomBanUserReq);

    @POST(SealMicUrl.ROOM_MESSAGE_BROADCAST)
    NetStateLiveData<NetResult<Void>> messageBroadcast(@Body MessageBroadCastReq micAcceptReq);

}
