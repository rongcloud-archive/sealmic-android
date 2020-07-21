package cn.rongcloud.sealmicandroid.bean.repo;

import java.util.List;

/**
 * 房间列表响应体
 */
public class RoomListRepo extends NetResult<RoomListRepo>{


    /**
     * totalCount : 8
     * rooms : [{"roomId":"D3cANO3lTHQqZ4ojHj-gKM","roomName":"这是什么神仙初恋","themePictureUrl":"http://120.92.13.89/static/room/1.png","allowedJoinRoom":true,"allowedFreeJoinMic":true,"updateDt":1593583269676},{"roomId":"kc54_VuGTiIv82UnHhq5MM","roomName":"未闻花名招人","themePictureUrl":"http://120.92.13.89/static/room/2.png","allowedJoinRoom":true,"allowedFreeJoinMic":true,"updateDt":1593583269813},{"roomId":"kgbhnQHCScUpIK_DldqaRE","roomName":"初见综合娱乐","themePictureUrl":"http://120.92.13.89/static/room/3.png","allowedJoinRoom":true,"allowedFreeJoinMic":true,"updateDt":1593583269821},{"roomId":"Tngu3pzIS2ongNT0LXUmco","roomName":"天天点歌厅","themePictureUrl":"http://120.92.13.89/static/room/4.png","allowedJoinRoom":true,"allowedFreeJoinMic":true,"updateDt":1593583269826},{"roomId":"uf84mB69RaMulcEK6n-y3Y","roomName":"百度点唱 最美奇遇","themePictureUrl":"http://120.92.13.89/static/room/5.png","allowedJoinRoom":true,"allowedFreeJoinMic":true,"updateDt":1593583269831},{"roomId":"D3wbI8KtTl8q6OLGd4_TQU","roomName":"你哥哥","themePictureUrl":"http://120.92.13.89/static/room/8.png","allowedJoinRoom":true,"allowedFreeJoinMic":true,"updateDt":1593755929665},{"roomId":"v5m3iUaFQ7opj_J1gWaP7M","roomName":"ing民工","themePictureUrl":"http://120.92.13.89/static/room/5.png","allowedJoinRoom":true,"allowedFreeJoinMic":true,"updateDt":1593756592495},{"roomId":"BzPxL4RaRFsqkynSUFxqiU","roomName":"学校","themePictureUrl":"http://120.92.13.89/static/room/8.png","allowedJoinRoom":true,"allowedFreeJoinMic":true,"updateDt":1593757520034}]
     */

    private int totalCount;
    private List<RoomsBean> rooms;

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public List<RoomsBean> getRooms() {
        return rooms;
    }

    public void setRooms(List<RoomsBean> rooms) {
        this.rooms = rooms;
    }

    public static class RoomsBean {
        /**
         * roomId : D3cANO3lTHQqZ4ojHj-gKM
         * roomName : 这是什么神仙初恋
         * themePictureUrl : http://120.92.13.89/static/room/1.png
         * allowedJoinRoom : true
         * allowedFreeJoinMic : true
         * updateDt : 1593583269676
         */

        private String roomId;
        private String roomName;
        private String themePictureUrl;
        private boolean allowedJoinRoom;
        private boolean allowedFreeJoinMic;
        private long updateDt;

        public String getRoomId() {
            return roomId;
        }

        public void setRoomId(String roomId) {
            this.roomId = roomId;
        }

        public String getRoomName() {
            return roomName;
        }

        public void setRoomName(String roomName) {
            this.roomName = roomName;
        }

        public String getThemePictureUrl() {
            return themePictureUrl;
        }

        public void setThemePictureUrl(String themePictureUrl) {
            this.themePictureUrl = themePictureUrl;
        }

        public boolean isAllowedJoinRoom() {
            return allowedJoinRoom;
        }

        public void setAllowedJoinRoom(boolean allowedJoinRoom) {
            this.allowedJoinRoom = allowedJoinRoom;
        }

        public boolean isAllowedFreeJoinMic() {
            return allowedFreeJoinMic;
        }

        public void setAllowedFreeJoinMic(boolean allowedFreeJoinMic) {
            this.allowedFreeJoinMic = allowedFreeJoinMic;
        }

        public long getUpdateDt() {
            return updateDt;
        }

        public void setUpdateDt(long updateDt) {
            this.updateDt = updateDt;
        }
    }
}
