package cn.rongcloud.sealmic.net;

import java.util.HashMap;
import java.util.List;

import cn.rongcloud.sealmic.model.UserInfo;
import cn.rongcloud.sealmic.net.model.CreateRoomResult;
import cn.rongcloud.sealmic.net.model.LoginResult;
import cn.rongcloud.sealmic.model.DetailRoomInfo;
import cn.rongcloud.sealmic.model.BaseRoomInfo;
import cn.rongcloud.sealmic.net.retrofit.CallBackWrapper;
import cn.rongcloud.sealmic.net.retrofit.RetrofitClient;
import cn.rongcloud.sealmic.net.retrofit.RetrofitUtil;
import okhttp3.RequestBody;

/**
 * 使用 Retrofit 实现对用户信息的请求
 */
public class SealMicRequest {
    private RetrofitClient mClient;
    private SealMicService mService;

    public SealMicRequest(RetrofitClient client) {
        mClient = client;
        mService = mClient.createService(SealMicService.class);
    }

    /**
     * 登录
     *
     * @param deviceId
     * @param callBack
     */
    public void login(String deviceId, RequestCallBack<LoginResult> callBack) {
        HashMap<String, String> paramsMap = new HashMap<>();
        paramsMap.put("deviceId", deviceId);
        RequestBody body = RetrofitUtil.createJsonRequest(paramsMap);
        mService.login(body).enqueue(new CallBackWrapper<>(callBack));
    }

    /**
     * 创建房间
     *
     * @param subject  房间主题
     * @param type     房间类型
     * @param callBack
     */
    public void createRoom(String subject, int type, RequestCallBack<CreateRoomResult> callBack) {
        HashMap<String, String> paramsMap = new HashMap<>();
        paramsMap.put("subject", subject);
        paramsMap.put("type", String.valueOf(type));
        RequestBody body = RetrofitUtil.createJsonRequest(paramsMap);
        mService.createRoom(body).enqueue(new CallBackWrapper<>(callBack));
    }

    /**
     * 获取房间列表
     *
     * @param callBack
     */
    public void getChatRoomList(RequestCallBack<List<BaseRoomInfo>> callBack) {
        mService.getRoomList().enqueue(new CallBackWrapper<>(callBack));
    }

    /**
     * 获取房间详情
     */
    public void getChatRoomDetail(String roomId, RequestCallBack<DetailRoomInfo> callBack) {
        mService.getRoomDetailInfo(roomId).enqueue(new CallBackWrapper<>(callBack));
    }

    /**
     * 获取房间用户列表
     *
     * @param roomId
     * @param callBack
     */
    public void getChatRoomUserList(String roomId, RequestCallBack<List<UserInfo>> callBack) {
        mService.getRoomUserList(roomId).enqueue(new CallBackWrapper<>(callBack));
    }

    /**
     * 销毁房间
     *
     * @param roomId
     * @param callBack
     */
    public void destroyRoom(String roomId, RequestCallBack<Boolean> callBack) {
        HashMap<String, String> paramsMap = new HashMap<>();
        paramsMap.put("roomId", roomId);
        RequestBody body = RetrofitUtil.createJsonRequest(paramsMap);
        mService.destroyRoom(body).enqueue(new CallBackWrapper<>(callBack));
    }

    /**
     * 加入房间
     *
     * @param roomId
     * @param callBack
     */
    public void joinRoom(String roomId, RequestCallBack<DetailRoomInfo> callBack) {
        HashMap<String, String> paramsMap = new HashMap<>();
        paramsMap.put("roomId", roomId);
        RequestBody body = RetrofitUtil.createJsonRequest(paramsMap);
        mService.joinChatRoom(body).enqueue(new CallBackWrapper<>(callBack));
    }

    /**
     * 离开房间
     *
     * @param roomId
     * @param callBack
     */
    public void leaveRoom(String roomId, RequestCallBack<Boolean> callBack) {
        HashMap<String, String> paramsMap = new HashMap<>();
        paramsMap.put("roomId", roomId);
        RequestBody body = RetrofitUtil.createJsonRequest(paramsMap);
        mService.leaveChatRoom(body).enqueue(new CallBackWrapper<>(callBack));
    }

    /**
     * 当前用户（非房主）上麦
     *
     * @param roomId
     * @param position
     * @param callBack
     */
    public void joinMic(String roomId, int position, RequestCallBack<Boolean> callBack) {
        HashMap<String, String> paramsMap = new HashMap<>();
        paramsMap.put("roomId", roomId);
        paramsMap.put("targetPosition", String.valueOf(position));
        RequestBody body = RetrofitUtil.createJsonRequest(paramsMap);
        mService.joinChatMic(body).enqueue(new CallBackWrapper<>(callBack));
    }

    /**
     * 当前用户（非房主）下麦
     *
     * @param roomId
     * @param position
     * @param callBack
     */
    public void leaveMic(String roomId, int position, RequestCallBack<Boolean> callBack) {
        HashMap<String, String> paramsMap = new HashMap<>();
        paramsMap.put("roomId", roomId);
        paramsMap.put("targetPosition", String.valueOf(position));
        RequestBody body = RetrofitUtil.createJsonRequest(paramsMap);
        mService.leaveChatMic(body).enqueue(new CallBackWrapper<>(callBack));
    }

    /**
     * 当前用户更改麦位位置, 即跳麦
     *
     * @param roomId       当前房间 id
     * @param fromPosition
     * @param toPosition
     * @param callBack
     */
    public void changMicPosition(String roomId, int fromPosition, int toPosition, RequestCallBack<Boolean> callBack) {
        HashMap<String, String> paramsMap = new HashMap<>();
        paramsMap.put("roomId", roomId);
        paramsMap.put("fromPosition", String.valueOf(fromPosition));
        paramsMap.put("toPosition", String.valueOf(toPosition));
        RequestBody body = RetrofitUtil.createJsonRequest(paramsMap);
        mService.changeChatMicPosition(body).enqueue(new CallBackWrapper<>(callBack));
    }

    /**
     * 房主更改麦位状态
     *
     * @param roomId
     * @param position
     * @param userId
     * @param cmd
     * @param callBack
     */
    public void controlMicPosition(String roomId, int position, String userId, int cmd, RequestCallBack<Boolean> callBack) {
        HashMap<String, String> paramsMap = new HashMap<>();
        paramsMap.put("roomId", roomId);
        paramsMap.put("targetPosition", String.valueOf(position));
        paramsMap.put("targetUserId", userId);
        paramsMap.put("cmd", String.valueOf(cmd));
        RequestBody body = RetrofitUtil.createJsonRequest(paramsMap);
        mService.controlChatMic(body).enqueue(new CallBackWrapper<>(callBack));
    }

    /**
     * 设置房间背景
     *
     * @param roomId
     * @param backgroundIndex
     * @param callBack
     */
    public void setRoomBackground(String roomId, int backgroundIndex, RequestCallBack<Boolean> callBack) {
        HashMap<String, String> paramsMap = new HashMap<>();
        paramsMap.put("roomId", roomId);
        paramsMap.put("bgId", String.valueOf(backgroundIndex));
        RequestBody body = RetrofitUtil.createJsonRequest(paramsMap);
        mService.setRoomBackground(body).enqueue(new CallBackWrapper<>(callBack));
    }

}
