package cn.rongcloud.sealmicandroid.common;

import java.util.HashMap;
import java.util.List;

import cn.rongcloud.rtc.api.report.StatusReport;
import cn.rongcloud.sealmicandroid.bean.kv.MicBean;
import cn.rongcloud.sealmicandroid.bean.repo.RoomMemberRepo;
import cn.rongcloud.sealmicandroid.common.constant.UserRoleType;
import cn.rongcloud.sealmicandroid.im.message.HandOverHostMessage;
import cn.rongcloud.sealmicandroid.im.message.KickMemberMessage;
import cn.rongcloud.sealmicandroid.im.message.SendBroadcastGiftMessage;
import cn.rongcloud.sealmicandroid.im.message.SendGiftMessage;
import cn.rongcloud.sealmicandroid.im.message.TakeOverHostMessage;
import io.rong.imlib.model.Message;
import io.rong.message.ChatRoomKVNotiMessage;

/**
 * 事件总线EventBus 事件类
 */
public class Event {

    /**
     * 聊天界面消息列表数据更新事件
     */
    public static class EventImList {
        private Message message;

        public Message getMessage() {
            return message;
        }

        public EventImList(Message message) {
            this.message = message;
        }
    }

    /**
     * 输入声音的更新事件
     */
    public static class EventAudioInputLevel {
        private int position;
        private int inputLevel;

        public EventAudioInputLevel(int position, int inputLevel) {
            this.position = position;
            this.inputLevel = inputLevel;
        }

        public int getPosition() {
            return position;
        }

        public int getInputLevel() {
            return inputLevel;
        }
    }


    /**
     * 麦位状态更新事件
     */
    public static class EventLockMic {
        private int state;

        public EventLockMic(int state) {
            this.state = state;
        }

        public int getState() {
            return state;
        }
    }

    /**
     * 聊天界面消息列表数据更新事件
     */
    public static class EventKvMessage {
        private ChatRoomKVNotiMessage chatRoomKVNotiMessage;

        public ChatRoomKVNotiMessage getChatRoomKVNotiMessage() {
            return chatRoomKVNotiMessage;
        }

        public EventKvMessage(ChatRoomKVNotiMessage chatRoomKVNotiMessage) {
            this.chatRoomKVNotiMessage = chatRoomKVNotiMessage;
        }
    }

    /**
     * 送出礼物时的事件
     */
    public static class EventGiftMessage {
        private String tag;
        private String content;
        private Message message;

        public EventGiftMessage(SendGiftMessage giftMessage, Message message) {
            this.tag = giftMessage.getTag();
            this.content = giftMessage.getContent();
            this.message = message;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }

        public void setContent(String content) {
            this.content = content;
        }


        public String getTag() {
            return tag;
        }

        public String getContent() {
            return content;
        }

        public Message getMessage() {
            return message;
        }
    }

    /**
     * 接收超级礼物广播的事件
     */
    public static class EventBroadcastGiftMessage {
        private SendBroadcastGiftMessage eventBroadcastGiftMessage;

        public EventBroadcastGiftMessage(SendBroadcastGiftMessage eventBroadcastGiftMessage) {
            this.eventBroadcastGiftMessage = eventBroadcastGiftMessage;
        }

        public SendBroadcastGiftMessage getEventBroadcastGiftMessage() {
            return eventBroadcastGiftMessage;
        }
    }

    /**
     * 收到消息撤回的广播事件
     */
    public static class EventBroadcastRecallMessage {
        private Message eventBroadRecallMessage;

        public EventBroadcastRecallMessage(Message eventBroadRecallMessage) {
            this.eventBroadRecallMessage = eventBroadRecallMessage;
        }

        public Message getEventBroadRecallMessage() {
            return eventBroadRecallMessage;
        }
    }

    /**
     * 点击送出超级礼物的通知事件
     */
    public static class EventSendSuperGift {

        public EventSendSuperGift() {
        }
    }


    /**
     * 麦位状态信息的更新事件
     */
    public static class EventMicKVMessage {
        private ChatRoomKVNotiMessage chatRoomKVNotiMessage;

        public EventMicKVMessage(ChatRoomKVNotiMessage chatRoomKVNotiMessage) {
            this.chatRoomKVNotiMessage = chatRoomKVNotiMessage;
        }

        public ChatRoomKVNotiMessage getChatRoomKVNotiMessage() {
            return chatRoomKVNotiMessage;
        }
    }

    /**
     * 房间成员变更的更新事件
     */
    public static class EventMemberChangeMessage {
        private KickMemberMessage roomMemberChangeMessage;

        public EventMemberChangeMessage(KickMemberMessage roomMemberChangeMessage) {
            this.roomMemberChangeMessage = roomMemberChangeMessage;
        }

        public KickMemberMessage getRoomMemberChangeMessage() {
            return roomMemberChangeMessage;
        }
    }

    /**
     * 转让主持人更新事件
     */
    public static class EventHandOverHostMessage {
        private HandOverHostMessage handOverHostMessage;

        public EventHandOverHostMessage(HandOverHostMessage handOverHostMessage) {
            this.handOverHostMessage = handOverHostMessage;
        }

        public HandOverHostMessage getHandOverHostMessage() {
            return handOverHostMessage;
        }
    }

    /**
     * 接管主持人更新事件
     */
    public static class EventTakeOverHostMessage {
        private TakeOverHostMessage takeOverHostMessage;

        public EventTakeOverHostMessage(TakeOverHostMessage takeOverHostMessage) {
            this.takeOverHostMessage = takeOverHostMessage;
        }

        public TakeOverHostMessage getTakeOverHostMessage() {
            return takeOverHostMessage;
        }
    }

    /**
     * 通话数据解析的事件通知
     */
    public static class EventMicStatusReport {
        private StatusReport statusReport;

        public EventMicStatusReport(StatusReport statusReport) {
            this.statusReport = statusReport;
        }

        public StatusReport getStatusReport() {
            return statusReport;
        }
    }

    /**
     * 用户是否正在讲话的事件通知
     */
    public static class EventAudioReceivedLevel {
        private HashMap<String, String> audioLevel;

        public EventAudioReceivedLevel(HashMap<String, String> audioLevel) {
            this.audioLevel = audioLevel;
        }

        public HashMap<String, String> getAudioLevel() {
            return audioLevel;
        }
    }

    /**
     * 用户角色变更对应的事件通知
     */
    public static class EventUserRoleType {
        private UserRoleType userRoleType;
        private boolean isMicOpen;

        public EventUserRoleType(UserRoleType userRoleType, boolean isMicOpen) {
            this.userRoleType = userRoleType;
            this.isMicOpen = isMicOpen;
        }

        public UserRoleType getUserRoleType() {
            return userRoleType;
        }

        public boolean isMicOpen() {
            return isMicOpen;
        }
    }

    /**
     * 麦位有更新时，对外抛出MicBean，对外抛出的为主持人或者主播，都应该在在线列表当中排除
     */
    public static class EventMicBean {
        private MicBean micBean;

        public EventMicBean(MicBean micBean) {
            this.micBean = micBean;
        }

        public MicBean getMicBean() {
            return micBean;
        }
    }

    /**
     * 主持人操作用户状态时的事件
     */
    public static class EventUserLineStatusChange {
        /**
         * 被禁言的事件
         */
        private BankMicBean bankMicBean;
        private UnBankBean unBankBean;
        /**
         * 被禁言用户的集合
         */
        private MicBankUserStatus micBankUserStatus;

        public static class MicBankUserStatus {
            private List<RoomMemberRepo.MemberBean> memberBeanList;

            public MicBankUserStatus(List<RoomMemberRepo.MemberBean> memberBeanList) {
                this.memberBeanList = memberBeanList;
            }

            public List<RoomMemberRepo.MemberBean> getMemberBeanList() {
                return memberBeanList;
            }
        }

        /**
         * 在线列表用户的集合（过滤掉禁言列表之后的）
         */
        private MicUserStatus micUserStatus;

        public static class MicUserStatus {
            private List<RoomMemberRepo.MemberBean> memberBeanList;

            public MicUserStatus(List<RoomMemberRepo.MemberBean> memberBeanList) {
                this.memberBeanList = memberBeanList;
            }

            public List<RoomMemberRepo.MemberBean> getMemberBeanList() {
                return memberBeanList;
            }
        }

        /**
         * 在线列表用户的集合（过滤掉禁言列表和在麦位上的人之后的）
         */
        private MicUserFilterBankAndMic micUserFilterBankAndMic;

        public static class MicUserFilterBankAndMic {
            private List<RoomMemberRepo.MemberBean> memberBeanList;

            public MicUserFilterBankAndMic(List<RoomMemberRepo.MemberBean> memberBeanList) {
                this.memberBeanList = memberBeanList;
            }

            public List<RoomMemberRepo.MemberBean> getMemberBeanList() {
                return memberBeanList;
            }
        }

        /**
         * 被解禁用户的类
         */
        public static class UnBankBean {
            private RoomMemberRepo.MemberBean memberBean;

            public UnBankBean(RoomMemberRepo.MemberBean memberBean) {
                this.memberBean = memberBean;
            }

            public RoomMemberRepo.MemberBean getMemberBean() {
                return memberBean;
            }

            public void setMemberBean(RoomMemberRepo.MemberBean memberBean) {
                this.memberBean = memberBean;
            }
        }

        /**
         * 被禁言用户的类
         */
        public static class BankMicBean {

            private RoomMemberRepo.MemberBean memberBean;

            public BankMicBean(RoomMemberRepo.MemberBean memberBean) {
                this.memberBean = memberBean;
            }

            public RoomMemberRepo.MemberBean getMemberBean() {
                return memberBean;
            }

            public void setMemberBean(RoomMemberRepo.MemberBean memberBean) {
                this.memberBean = memberBean;
            }
        }

        /**
         * 用户邀请连麦成功之后的事件通知
         */
        public static class ConnectMicBean {
            public ConnectMicBean() {
            }
        }
    }

    /**
     * 用户被踢后的事件通知
     */
    public static class UserGoOutBean {

        public UserGoOutBean() {
        }
    }

    /**
     * 用户token失效后的事件通知
     */
    public static class UserTokenLose {

    }

}

