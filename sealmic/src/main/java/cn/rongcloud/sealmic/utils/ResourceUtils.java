package cn.rongcloud.sealmic.utils;

import android.content.Context;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import cn.rongcloud.sealmic.R;

public class ResourceUtils {
    private List<String> userNickNamePool;
    private List<Integer> userAvatarPool;
    private List<Integer> roomCoverImagePool;
    private List<Integer> roomBgImagePool;
    private static ResourceUtils instance;
    private List<String> roomTitlePool;

    public static ResourceUtils getInstance() {
        if (instance == null) {
            synchronized (ResourceUtils.class) {
                if (instance == null) {
                    instance = new ResourceUtils();
                }
            }
        }
        return instance;
    }

    public void init(Context context) {
        initUserNickPool(context);
        initUserAvatarPool(context);
        initRoomBackgroundImagePool();
        initRoomCoverImagePool(context);
        initRoomTile(context);
    }

    private void initUserNickPool(Context context) {
        String[] stringArray = context.getResources().getStringArray(R.array.user_nick_name_pool);
        userNickNamePool = new ArrayList<>();
        userNickNamePool.addAll(Arrays.asList(stringArray));
    }

    private void initRoomTile(Context context) {
        roomTitlePool = new ArrayList<>();
        String[] topicArray = context.getResources().getStringArray(R.array.room_topic_pool);
        roomTitlePool.addAll(Arrays.asList(topicArray));
    }

    private void initUserAvatarPool(Context context) {
        userAvatarPool = new ArrayList<>();
        userAvatarPool.add(R.drawable.chatroom_user_avatar_01);
        userAvatarPool.add(R.drawable.chatroom_user_avatar_02);
        userAvatarPool.add(R.drawable.chatroom_user_avatar_03);
        userAvatarPool.add(R.drawable.chatroom_user_avatar_04);
        userAvatarPool.add(R.drawable.chatroom_user_avatar_05);
        userAvatarPool.add(R.drawable.chatroom_user_avatar_06);
        userAvatarPool.add(R.drawable.chatroom_user_avatar_07);
        userAvatarPool.add(R.drawable.chatroom_user_avatar_08);
        userAvatarPool.add(R.drawable.chatroom_user_avatar_09);
    }

    private void initRoomBackgroundImagePool() {
        roomBgImagePool = new ArrayList<>();
        roomBgImagePool.add(R.drawable.chatroom_bg_01);
        roomBgImagePool.add(R.drawable.chatroom_bg_02);
        roomBgImagePool.add(R.drawable.chatroom_bg_03);
        roomBgImagePool.add(R.drawable.chatroom_bg_04);
        roomBgImagePool.add(R.drawable.chatroom_bg_05);
        roomBgImagePool.add(R.drawable.chatroom_bg_06);
        roomBgImagePool.add(R.drawable.chatroom_bg_07);
        roomBgImagePool.add(R.drawable.chatroom_bg_08);
        roomBgImagePool.add(R.drawable.chatroom_bg_09);
    }

    private void initRoomCoverImagePool(Context context) {
        roomCoverImagePool = new ArrayList<>();
        roomCoverImagePool.add(R.drawable.chatroom_cover_01);
        roomCoverImagePool.add(R.drawable.chatroom_cover_02);
        roomCoverImagePool.add(R.drawable.chatroom_cover_03);
        roomCoverImagePool.add(R.drawable.chatroom_cover_04);
        roomCoverImagePool.add(R.drawable.chatroom_cover_05);
        roomCoverImagePool.add(R.drawable.chatroom_cover_06);
        roomCoverImagePool.add(R.drawable.chatroom_cover_07);
        roomCoverImagePool.add(R.drawable.chatroom_cover_08);
        roomCoverImagePool.add(R.drawable.chatroom_cover_09);
        roomCoverImagePool.add(R.drawable.chatroom_cover_10);
        roomCoverImagePool.add(R.drawable.chatroom_cover_11);
        roomCoverImagePool.add(R.drawable.chatroom_cover_12);
    }

    /**
     * 获取用户昵称
     * 该方法会根据用户id从预设的昵称中选择一个昵称
     *
     * @param userId
     * @return
     */
    public String getUserName(String userId) {
        if (userId == null) userId = "";

        int asc = getStringLastCharAsc(userId);
        int size = userNickNamePool.size();
        String nickName = userNickNamePool.get(asc % size);
        return nickName;
    }

    /**
     * 获取用户头像
     * 该方法会根据用户id从预设的头像中选择一个头像
     *
     * @param userId
     * @return
     */
    public int getUserAvatarResourceId(String userId) {
        if (userId == null) userId = "";

        int asc = getStringLastCharAsc(userId);
        int size = userAvatarPool.size();
        int avatar = userAvatarPool.get(asc % size);
        return avatar;
    }

    public int getRoomCoverImageId(String roomId) {
        if (roomId == null) roomId = "";

        int size = roomCoverImagePool.size();
        int asc = getStringLastCharAsc(roomId);
        return roomCoverImagePool.get(asc % size);
    }

    public int getRoomBackgroundImageId(int bgId) {
        int size = roomBgImagePool.size();
        bgId = bgId > 0 ? bgId : -bgId;
        return roomBgImagePool.get(bgId % size);
    }

    public String getRandomRoomTopic() {
        int size = roomTitlePool.size();
        int index = Math.abs(new Random().nextInt());
        return roomTitlePool.get(index % size);
    }

    private int getStringLastCharAsc(String str){
        if(TextUtils.isEmpty(str)) return 0;
        int length = str.length();
        char c = str.charAt(length - 1);
        return (int)c;
    }
}
