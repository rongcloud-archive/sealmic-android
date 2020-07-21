package cn.rongcloud.sealmicandroid.bean;

public class SendSuperGiftBean {


    /**
     * tag : gift_sportsCar
     * user : {"name":"陈琪嘉","portrait":"http://120.92.13.89/static/portrait/9.png","id":"87f284aa-473f-4d0b-9bf7-b375ace30488"}
     * roomName : hdjxjddjjdjdjdj
     */

    private String tag;
    private UserBean user;
    private String roomName;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public UserBean getUser() {
        return user;
    }

    public void setUser(UserBean user) {
        this.user = user;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public static class UserBean {
        /**
         * name : 陈琪嘉
         * portrait : http://120.92.13.89/static/portrait/9.png
         * id : 87f284aa-473f-4d0b-9bf7-b375ace30488
         */

        private String name;
        private String portrait;
        private String id;

        public UserBean(String name, String portrait, String id) {
            this.name = name;
            this.portrait = portrait;
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPortrait() {
            return portrait;
        }

        public void setPortrait(String portrait) {
            this.portrait = portrait;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }
}
