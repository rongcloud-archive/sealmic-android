package cn.rongcloud.sealmicandroid.manager;

import cn.rongcloud.sealmicandroid.SealMicApp;
import cn.rongcloud.sealmicandroid.bean.kv.MicBean;
import cn.rongcloud.sealmicandroid.bean.local.BgmBean;
import cn.rongcloud.sealmicandroid.bean.repo.RoomDetailRepo;
import cn.rongcloud.sealmicandroid.common.constant.UserRoleType;
import cn.rongcloud.sealmicandroid.util.SPUtil;

/**
 * 缓存管理类，主要保存一些本应用的关键信息至sp文件
 */
public class CacheManager {

    private static final String IM_TOKEN = "im_token";
    private static final String AUTH = "auth";
    private static final String USER_ID = "user_id";
    private static final String USER_NAME = "user_name";
    private static final String USER_PORTRAIT = "user_portrait";
    private static final String DEVICE_ID = "device_id";
    private static final String ROOM_ID = "room_id";
    private static final String USER_TYPE = "user_type";
    private static final String USER_ROLE_TYPE = "user_role_type";

    private static final String DETAIL_ROOM_ID = "room_id";
    private static final String DETAIL_ROOM_NAME = "detail_room_name";
    private static final String DETAIL_ROOM_THEME_PICTURE_URL = "detail_room_theme_picture_url";
    private static final String DETAIL_ROOM_CREATOR_ID = "detail_room_creator_id";
    private static final String DETAIL_ROOM_ALLOWED_FREE_JOIN_MIC = "detail_room_allowed_free_join_mic";
    private static final String DETAIL_ROOM_ALLOWED_FREE_JOIN_ROOM = "detail_room_allowed_free_join_room";
    private static final String DETAIL_ROOM_CREATE_DT = "detail_room_create_dt";

    private static final String IS_LOGIN = "is_login";

    private static final String MIC_BEAN_USER_ID = "mic_bean_user_id";
    private static final String MIC_BEAN_POSITION = "mic_bean_position";
    private static final String MIC_BEAN_STATE = "mic_bean_state";

    private static final String BGM_ROOM_ID = "bgm_room_id";
    private static final String BGM_CONTENT = "bgm_content";

    private static final String IS_OPEN_DEBUG = "is_open_debug";
    private static final String IS_CONNECT_IM = "is_connect_im";

    private CacheManager() {
    }

    private static class CacheManagerHelper {
        private static final CacheManager INSTANCE = new CacheManager();
    }

    public static CacheManager getInstance() {
        return CacheManagerHelper.INSTANCE;
    }

    public void cacheToken(String imToken) {
        SPUtil.put(SealMicApp.getApplication(), IM_TOKEN, imToken);
    }

    public void cacheAuth(String auth) {
        SPUtil.put(SealMicApp.getApplication(), AUTH, auth);
    }

    public void cacheUserId(String userId) {
        SPUtil.put(SealMicApp.getApplication(), USER_ID, userId);
    }

    public void cacheUserName(String userName) {
        SPUtil.put(SealMicApp.getApplication(), USER_NAME, userName);
    }

    public void cacheUserPortrait(String portrait) {
        SPUtil.put(SealMicApp.getApplication(), USER_PORTRAIT, portrait);
    }

    public void cacheDeviceId(String deviceId) {
        SPUtil.put(SealMicApp.getApplication(), DEVICE_ID, deviceId);
    }

    public void cacheUserType(int type) {
        SPUtil.put(SealMicApp.getApplication(), USER_TYPE, type);
    }

    public String getDeviceId() {
        return (String) SPUtil.get(SealMicApp.getApplication(), DEVICE_ID, "");
    }

    public void cacheRoomId(String id) {
        SPUtil.put(SealMicApp.getApplication(), ROOM_ID, id);
    }

    public String getRoomId() {
        return (String) SPUtil.get(SealMicApp.getApplication(), ROOM_ID, "");
    }

    public String getUserId() {
        return (String) SPUtil.get(SealMicApp.getApplication(), USER_ID, "");
    }

    public String getUserName() {
        return (String) SPUtil.get(SealMicApp.getApplication(), USER_NAME, "");
    }

    public String getUserPortrait() {
        return (String) SPUtil.get(SealMicApp.getApplication(), USER_PORTRAIT, "");
    }

    public int getUserType() {
        return (int) SPUtil.get(SealMicApp.getApplication(), USER_TYPE, -999);
    }

    public String getAuth() {
        return (String) SPUtil.get(SealMicApp.getApplication(), AUTH, "");
    }

    public void cacheUserRoleType(int roleType) {
        SPUtil.put(SealMicApp.getApplication(), USER_ROLE_TYPE, roleType);
    }

    public int getUserRoleType() {
        return (int) SPUtil.get(SealMicApp.getApplication(), USER_ROLE_TYPE, -999);
    }

    public String getToken() {
        return (String) SPUtil.get(SealMicApp.getApplication(), IM_TOKEN);
    }

    /**
     * 保存聊天室详细信息
     */
    public void cacheRoomDetail(RoomDetailRepo roomDetailRepo) {
        SPUtil.put(SealMicApp.getApplication(), DETAIL_ROOM_ID, roomDetailRepo.getRoomId() == null ? "" : roomDetailRepo.getRoomId());
        SPUtil.put(SealMicApp.getApplication(), DETAIL_ROOM_NAME, roomDetailRepo.getRoomName() == null ? "" : roomDetailRepo.getRoomName());
        SPUtil.put(SealMicApp.getApplication(), DETAIL_ROOM_THEME_PICTURE_URL, roomDetailRepo.getThemePictureUrl() == null ? "" : roomDetailRepo.getThemePictureUrl());
        SPUtil.put(SealMicApp.getApplication(), DETAIL_ROOM_CREATOR_ID, roomDetailRepo.getCreatorId() == null ? "" : roomDetailRepo.getCreatorId());
        //是否允许自由上麦 true 允许
        SPUtil.put(SealMicApp.getApplication(), DETAIL_ROOM_ALLOWED_FREE_JOIN_MIC, roomDetailRepo.isAllowedFreeJoinMic());
        //是否允许用户自由加入房间
        SPUtil.put(SealMicApp.getApplication(), DETAIL_ROOM_ALLOWED_FREE_JOIN_ROOM, roomDetailRepo.isAllowedJoinRoom());
        SPUtil.put(SealMicApp.getApplication(), DETAIL_ROOM_CREATE_DT, roomDetailRepo.getCreateDt());
    }

    /**
     * 获取聊天室详细信息
     *
     * @return 缓存的聊天室详细信息
     */
    public RoomDetailRepo getRoomDetailRepo() {
        RoomDetailRepo roomDetailRepo = new RoomDetailRepo();
        String roomId = SPUtil.get(SealMicApp.getApplication(), DETAIL_ROOM_ID);
        String roomName = SPUtil.get(SealMicApp.getApplication(), DETAIL_ROOM_NAME);
        String themePictureUrl = SPUtil.get(SealMicApp.getApplication(), DETAIL_ROOM_THEME_PICTURE_URL);
        String creatorId = SPUtil.get(SealMicApp.getApplication(), DETAIL_ROOM_CREATOR_ID);
        boolean isAllowedJoinMic = (boolean) SPUtil.get(SealMicApp.getApplication(), DETAIL_ROOM_ALLOWED_FREE_JOIN_MIC, false);
        boolean isAllowedJoinRoom = (boolean) SPUtil.get(SealMicApp.getApplication(), DETAIL_ROOM_ALLOWED_FREE_JOIN_ROOM, false);
        long createDt = (long) SPUtil.get(SealMicApp.getApplication(), DETAIL_ROOM_CREATE_DT, 0L);
        roomDetailRepo.setRoomId(roomId);
        roomDetailRepo.setRoomName(roomName);
        roomDetailRepo.setThemePictureUrl(themePictureUrl);
        roomDetailRepo.setCreatorId(creatorId);
        roomDetailRepo.setAllowedFreeJoinMic(isAllowedJoinMic);
        roomDetailRepo.setAllowedJoinRoom(isAllowedJoinRoom);
        roomDetailRepo.setCreateDt(createDt);
        return roomDetailRepo;
    }

    public void cacheIsLogin(Boolean isLogin) {
        SPUtil.put(SealMicApp.getApplication(), IS_LOGIN, isLogin);
    }

    public Boolean getIsLogin() {
        return (Boolean) SPUtil.get(SealMicApp.getApplication(), IS_LOGIN, false);
    }

    public void cacheMicBean(MicBean micBean) {
        if (micBean == null) {
            SPUtil.put(SealMicApp.getApplication(), MIC_BEAN_USER_ID, "");
            SPUtil.put(SealMicApp.getApplication(), MIC_BEAN_POSITION, "");
            SPUtil.put(SealMicApp.getApplication(), MIC_BEAN_STATE, "");
            CacheManager.getInstance().cacheUserRoleType(UserRoleType.AUDIENCE.getValue());
        } else {
            SPUtil.put(SealMicApp.getApplication(), MIC_BEAN_USER_ID, micBean.getUserId());
            SPUtil.put(SealMicApp.getApplication(), MIC_BEAN_POSITION, String.valueOf(micBean.getPosition()));
            SPUtil.put(SealMicApp.getApplication(), MIC_BEAN_STATE, String.valueOf(micBean.getState()));
            CacheManager.getInstance().cacheUserRoleType(
                    micBean.getPosition() == 0
                            ? UserRoleType.HOST.getValue()
                            : UserRoleType.CONNECT_MIC.getValue());
        }
    }

    public MicBean getMicBean() {
        MicBean micBean = new MicBean();
        String userId = SPUtil.get(SealMicApp.getApplication(), MIC_BEAN_USER_ID);
        String position = SPUtil.get(SealMicApp.getApplication(), MIC_BEAN_POSITION);
        String state = SPUtil.get(SealMicApp.getApplication(), MIC_BEAN_STATE);
        micBean.setUserId(userId);
        micBean.setPosition("".equals(position) || position == null ? 0 : Integer.parseInt(position));
        micBean.setState("".equals(state) || state == null ? 0 : Integer.parseInt(state));
        return micBean;
    }

    public void cacheBgmBean(String roomId, String bgmContent) {
        SPUtil.put(SealMicApp.getApplication(), BGM_ROOM_ID, roomId);
        SPUtil.put(SealMicApp.getApplication(), BGM_CONTENT, bgmContent);
    }

    public BgmBean getBgmBean() {
        String roomId = SPUtil.get(SealMicApp.getApplication(), BGM_ROOM_ID);
        String bgmContent = SPUtil.get(SealMicApp.getApplication(), BGM_CONTENT);
        return new BgmBean(roomId, bgmContent);
    }

    public void cacheIsOpenDebug(boolean isOpenDebug) {
        SPUtil.put(SealMicApp.getApplication(), IS_OPEN_DEBUG, isOpenDebug);
    }

    public boolean getIsOpenDebug() {
        return (boolean) SPUtil.get(SealMicApp.getApplication(), IS_OPEN_DEBUG, false);
    }

}
