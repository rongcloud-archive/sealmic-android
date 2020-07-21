package cn.rongcloud.sealmicandroid.bean.repo;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 获取房间成员列表
 */
public class RoomMemberRepo {

    @SerializedName("data")
    private List<MemberBean> roomMemberRepoList;

    public List<MemberBean> getRoomMemberRepoList() {
        return roomMemberRepoList;
    }

    public void setRoomMemberRepoList(List<MemberBean> roomMemberRepoList) {
        this.roomMemberRepoList = roomMemberRepoList;
    }

    /**
     * userId : 5ad537a2-7956-4b75-b1c2-a68c3879944a
     * userName : 陈楚成
     * portrait : http://139.198.21.251:8080/static/portrait/12.png
     */

    public static class MemberBean {
        private String userId;
        private String userName;
        private String portrait;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getPortrait() {
            return portrait;
        }

        public void setPortrait(String portrait) {
            this.portrait = portrait;
        }
    }


}
