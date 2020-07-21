# SealMic-Android

## 目录

- [关键业务逻辑说明](#关键业务逻辑说明)
- [用到的技术脚手架](#用到的技术脚手架)
- [关键代码模块说明](#关键代码模块说明)
- [文档链接](#文档链接)
- [联系我们](#联系我们)

### 关键业务逻辑说明

- [登录流程](#登录流程)
- [加入房间流程](#加入房间流程)
- [麦位相关状态改变流程](#麦位相关状态改变流程)
- [观众转换为参会人员流程](#观众转换为参会人员流程)
- [参会人员转为观众流程](#参会人员转为观众流程)
- [正在发言相关处理流程](#正在发言相关处理流程)
- [主持人转让与接管流程](#主持人转让与接管流程)

#### 登录流程

- ![](https://tva1.sinaimg.cn/large/007S8ZIlly1ggtyv2bi38j30u014k113.jpg)

#### 加入房间流程

- 从房间列表进入的房间默认都是观众身份进入，只有从创建房间的入口进入时会以主持人身份进入
- 主持人
  - 根据房间 ID 先加入 IM 聊天室
  - 加入聊天室成功后加入 RTC 聊天室
  - 加入聊天室成功后调用 RTC 接口发布自己的音频流
  - 延迟 1.5-1 秒（时长待定）将发布后的直播 liveUrl 设置到 KV 中
  - 延迟 1.5-1 秒（时长待定）取聊天室中麦位及讲话状态的相关 KV 然后更新一遍麦位当前状态
- 观众
  - 根据房间 ID 加入聊天室
  - 延迟 0.5-1 秒（时长待定）取 KV 中存放的
    liveUrl、麦位、讲话状态的、当前是否有人排麦的 KV，其中 liveUrl 如存在则调用
    RTC 接口订阅直播合流，麦位及讲话状态的 value
    取出后更新一遍麦位当前状态、当前是否有人排麦的 value
    取出后更新右上角小红点状态

#### 麦位相关状态改变流程

- 监听 IM 聊天室中的消息，如果是 KV 更新时发送的消息类型则根据具体更新的 Key
    来分别进行下述处理
- 相关 key 的格式如下:
  ```
  //直播流地址：
  "liveUrl":"http://abc.com"

  //麦位相关：
  //最后的 0-8 表示九个麦位，0 为主持人
  "sealmic_position_0":
   {
	    "userId":"abcde",
		//0：正常状态（此时 userId 存在则说明麦位上有人，否则表示当前为空麦位），1:锁定状态（不允许上人），2:闭麦状态（当前麦位上的用户被禁言）
		"state":0,
		//0-8 表示九个麦位
		"position":0
   }

  //正在发言相关:
  //最后的 0-8 表示九个麦位，0 为主持人
  "speaking_0":
  {
	 //0 表示当前未发言，1 表示正在发言
	 "speaking":0,
	 //0-8 表示九个麦位
	 "position":0
  }

  //是否有人排麦相关：
  //0 表示没人在排麦，1 表示有人在排麦
  "applied_mic_list_empty":0
  ```
- 如果是 liveUrl 更新的 Key
  - 仅观众身份需要处理，如果已经订阅过且更新后的和之前订阅的相同不需要处理，没有订阅过或更新后和之前的不同则需要调用 RTC 接口重新订阅这次更新后的 liveUrl
- 如果是发言状态更新的 Key
- 根据相关 KV 更新 UI 上指定麦位的动画状态即可
- 如果是排麦状态变更的 Key
- 根据 KV 更新右上角小红点状态即可
- 如果是麦位相关更新的 Key
  - 根据具体的 KV 更新对应麦位的 UI
  - 根据更新前后同一麦位的状态改变来进行下述处理（下面只需要处理和自己相关的角色转化逻辑，麦位上非自己的其它用户有上麦下麦时需要做的订阅流操作在
    RTC 房间变动相关的回调中进行处理）:
    ```
    //收到Demo server下发的KV
        ChatRoomKVNotiMessage chatRoomKVNotiMessage = eventMicKVMessage.getChatRoomKVNotiMessage();
        String json = chatRoomKVNotiMessage.getValue();
        String key = chatRoomKVNotiMessage.getKey();
        String extra = chatRoomKVNotiMessage.getExtra();
        //KV中携带的相关类型说明
        KvExtraBean kvExtraBean = new Gson().fromJson(extra, KvExtraBean.class);

        //当前房间id
        String roomId = CacheManager.getInstance().getRoomId();
        //当前用户id
        String userId = CacheManager.getInstance().getUserId();
        //当前登录用户本地保存的麦位信息（如果有则说明当前用户在麦位上）
        MicBean currentMicBean = CacheManager.getInstance().getMicBean();
        //当前用户的角色
        int currentUserType = CacheManager.getInstance().getUserRoleType();

        SpeakBean newSpeakBean = null;
        AppliedMicListBean appliedMicListBean = null;

        final MicBean newMicBean;
        //下发的KV通知共4类KV，分情况解析
        if (key.contains(SealMicConstant.KV_MIC_POSITION_PREFIX)) {
            //此次 KV 消息所携带的最新的麦位信息
            newMicBean = new Gson().fromJson(json, MicBean.class);
            //更新kv时本地更新麦位map
            localMicBeanMap.put(newMicBean.getPosition(), newMicBean);
            if (newMicBean != null) {
                //1. 根据新返回来的KV更新UI
                if (newMicBean.getState() == MicState.NORMAL.getState()) {
                    //用户下麦
                    dynamicAvatarViewList.get(newMicBean.getPosition()).micDelUser();
                    //主持人
                    if (UserRoleType.HOST.isHost(CacheManager.getInstance().getUserRoleType())) {
                        if (newMicBean.getUserId().equals(CacheManager.getInstance().getUserId())) {
                            micTextLayoutList.get(newMicBean.getPosition()).HasMic("号麦位");
                        } else {
                            micTextLayoutList.get(newMicBean.getPosition()).NullMic("号麦位");
                        }
                    }
                    //连麦者
                    if (UserRoleType.CONNECT_MIC.isConnectMic(CacheManager.getInstance().getUserRoleType()) ||
                            UserRoleType.AUDIENCE.isAudience(CacheManager.getInstance().getUserRoleType())) {
                        micTextLayoutList.get(newMicBean.getPosition()).NullMic("号麦位");
                    }
                    List<String> ids = new ArrayList<>();
                    ids.add(newMicBean.getUserId());
                    chatRoomViewModel.userBatch(ids);
                    final MicBean finalNewMicBean = newMicBean;
                    chatRoomViewModel.getUserinfolistRepoLiveData().observe(getViewLifecycleOwner(), new Observer<NetResult<List<RoomMemberRepo.MemberBean>>>() {
                        @Override
                        public void onChanged(NetResult<List<RoomMemberRepo.MemberBean>> listNetResult) {
                            List<RoomMemberRepo.MemberBean> memberBeanList = listNetResult.getData();
                            if (memberBeanList != null && memberBeanList.size() != 0) {
                                RoomMemberRepo.MemberBean memberBean = memberBeanList.get(0);
                                if ("".equals(memberBean.getUserName())) {
                                    micTextLayoutList.get(finalNewMicBean.getPosition()).HasMic(finalNewMicBean.getPosition() + "号麦");
                                } else {
                                    micTextLayoutList.get(finalNewMicBean.getPosition()).HasMic(memberBean.getUserName());
                                }
                                GlideManager.getInstance().setUrlImage(getView(),
                                        memberBean.getPortrait(),
                                        dynamicAvatarViewList.get(finalNewMicBean.getPosition()).getUserImg());

                            }
                        }
                    });
                } else if (newMicBean.getState() == MicState.LOCK.getState()) {
                    //麦位锁定
                    dynamicAvatarViewList.get(newMicBean.getPosition()).lockMic();
                } else if (newMicBean.getState() == MicState.CLOSE.getState()) {
                    //闭麦
                    dynamicAvatarViewList.get(newMicBean.getPosition()).bankMic();
                }

                //2. 被点的人是自己
                if (newMicBean.getUserId().equals(userId)) {
                    if (UserRoleType.AUDIENCE.isAudience(currentUserType)) {
                        //观众上麦
                        chatRoomViewModel.switchMic(roomId, CacheManager.getInstance().getUserRoleType(),
                                newMicBean.getPosition() == 0
                                        ? UserRoleType.HOST.getValue()
                                        : UserRoleType.CONNECT_MIC.getValue(),
                                new SealMicResultCallback<Map<String, String>>() {
                                    @Override
                                    public void onSuccess(Map<String, String> stringStringMap) {
                                        //切换角色上麦成功之后，更新当前用户角色
                                        ThreadManager.getInstance().runOnUIThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                SLog.e(SLog.TAG_SEAL_MIC, "观众上麦成功");
                                                //上麦成功后用户角色变为主持人或者连麦者，同时V层响应变化
                                                EventBus.getDefault().post(
                                                        newMicBean.getPosition() == 0
                                                                ? new Event.EventUserRoleType(UserRoleType.HOST, true)
                                                                : new Event.EventUserRoleType(UserRoleType.CONNECT_MIC, true));
                                                //上麦成功之后默认麦克风可用
                                                fragmentChatRoomBinding.chatroomVoiceIn.setSelected(false);
                                                RTCClient.getInstance().setLocalMicEnable(true);
                                            }
                                        });
                                    }

                                    @Override
                                    public void onFail(int errorCode) {

                                    }
                                });
                    }
                    //当前用户不在麦位或者当前用户所在麦位的状态和新的麦位状态不匹配
                    if (currentMicBean == null || currentMicBean.getState() != newMicBean.getState()) {
                        //根据新麦位的状态更新本地的麦克风状态（禁用或启用）
                        //底部栏图标，控制是否使用麦克风
                        //正常: 启用 打开方法  闭麦: 禁用 关闭方法
                        if (newMicBean.getState() == MicState.NORMAL.getState()) {
                            fragmentChatRoomBinding.chatroomVoiceIn.setSelected(false);
                            RTCClient.getInstance().setLocalMicEnable(true);
                        } else if (newMicBean.getState() == MicState.CLOSE.getState()) {
                            fragmentChatRoomBinding.chatroomVoiceIn.setSelected(true);
                            RTCClient.getInstance().setLocalMicEnable(false);
                        }
                    }

                    //在本地保存的麦位信息改变前将所在麦位的发言状态手动设置为 0 一次，设置到KV里
                    SpeakBean speakBean = new SpeakBean(0, currentMicBean != null ? currentMicBean.getPosition() : 0);
                    String speakingValue = new Gson().toJson(speakBean);
                    IMClient.getInstance().setChatRoomSpeakEntry(
                            roomId,
                            SealMicConstant.KV_SPEAK_POSITION_PREFIX + (currentMicBean != null ? currentMicBean.getPosition() : 0),
                            speakingValue);

                    //将本地保存的当前用户的麦位信息更新为新麦位的信息
                    CacheManager.getInstance().cacheMicBean(newMicBean);
                    //上麦情况下刷新本地map
                    localMicBeanMap.put(newMicBean.getPosition(), newMicBean);

                } else {

                    //如果 changeType 不是 （4，5，6）中的一种并且当前用户的麦位信息存在（也就是当前用户在麦位上）并且新麦位的序号等于当前用户时
                    int changeType = kvExtraBean.getChangeType();
                    //456 3种情况为不用下麦的情况
                    if (changeType != 4
                            && changeType != 5
                            && changeType != 6
                            && currentMicBean != null
                            && currentMicBean.getPosition() == newMicBean.getPosition()) {
                        //主播下麦
                        chatRoomViewModel.switchMic(roomId,
                                CacheManager.getInstance().getUserRoleType(),
                                UserRoleType.AUDIENCE.getValue(),
                                new SealMicResultCallback<Map<String, String>>() {
                                    @Override
                                    public void onSuccess(Map<String, String> stringStringMap) {
                                        //切换角色下麦成功之后，更新当前用户角色
                                        ThreadManager.getInstance().runOnUIThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                SLog.e(SLog.TAG_SEAL_MIC, "主播下麦成功");
                                                //下麦成功后用户角色变为观众，同时V层响应变化
                                                EventBus.getDefault().post(new Event.EventUserRoleType(UserRoleType.AUDIENCE, true));
                                                boolean outSelected = fragmentChatRoomBinding.chatroomVoiceOut.isSelected();
                                                RTCClient.getInstance().setSpeakerEnable(!outSelected);
                                            }
                                        });
                                    }

                                    @Override
                                    public void onFail(int errorCode) {

                                    }
                                });

                        //在本地保存的麦位信息改变前将所在麦位的发言状态手动设置为 0 一次
                        //设置到KV里
                        SpeakBean speakBean = new SpeakBean(0, currentMicBean.getPosition());
                        String speakingValue = new Gson().toJson(speakBean);
                        IMClient.getInstance().setChatRoomSpeakEntry(
                                roomId,
                                SealMicConstant.KV_SPEAK_POSITION_PREFIX + currentMicBean.getPosition(),
                                speakingValue);

                        //更新本地kv列表
                        currentMicBean.setUserId("");
                        //下麦后刷新本地麦位map
                        localMicBeanMap.put(currentMicBean.getPosition(), currentMicBean);
                        //将本地保存的当前用户的麦位信息更新为新麦位的信息
                        CacheManager.getInstance().cacheMicBean(null);
                    }
                }
            }
        }
        if (key.contains(SealMicConstant.KV_SPEAK_POSITION_PREFIX)) {
            //解析下发的正在讲话信息
            //远端用户靠下发的KV设置说话状态
            newSpeakBean = new Gson().fromJson(json, SpeakBean.class);
            CustomDynamicAvatar customDynamicAvatar = dynamicAvatarViewList.get(newSpeakBean.getPosition());
            //1: 正在说话  0: 没有说话
            if (newSpeakBean.getSpeaking() == 1) {
                customDynamicAvatar.startSpeak();
            } else {
                customDynamicAvatar.stopSpeak();
            }

            //本地用户靠音量是否大于1来判断是否显示动画


        }
        if (key.contains(SealMicConstant.KV_APPLIED_MIC_PREFIX)) {
            //解析下发的是否有人排麦信息
            //0 表示没人在排麦，1 表示有人在排麦
            if ("0".equals(json)) {
                fragmentChatRoomBinding.chatRoomTopBar.hideRedDot();
            } else if ("1".equals(json)) {
                fragmentChatRoomBinding.chatRoomTopBar.showRedDot();
            }
        }
        
    ```

#### 观众转换为参会人员流程

- 取消订阅直播合流
- 取消成功后加入 RTC 房间内
- 加入 RTC 房间后订阅房间当前存在的所有音频流
- 加入 RTC 房间后发布自己的音频流
- 用 RTC 发布音频流后返回的 liveUrl 和从 KV 中获取到的 liveUrl 比对，如果不同则主动设置一次 KV 中的 liveUrl

#### 参会人员转为观众流程

- 退出 RTC 房间
- 退出成功后根据 liveUrl 地址订阅直播合流

#### 正在发言相关处理流程

- 用户麦位状态改变时主动将当前麦位上发言状态的 KV 设置为 NO（避免上个麦位用户异常下麦导致这个麦位一直为发言状态）
- 监听 RTC 提供的 SDK 当前状态回调（此回调一秒会触发一次，省去自己写定时器了）
- 在回调中监听发出去的流状态
- 本地保存一下当前的发言状态
- 音量大于 0 则认为正在讲话，如果当前发言状态与本地保存的当前发言状态不相符则认为当前用户发言状态有变动，更新一次发言状态的 KV

#### 主持人转让与接管流程

- 转让流程
  - 主持人调用 server 接口，请求转让主持人
  - server 获知后会下发`RCMic:transferHostMsg`类型的自定义消息，消息中包含操作的发起者和接收者
  - 房间内用户收到消息后根据自己当前的身份以及消息中携带的接收者判断自己是否被邀请接管主持人位置
  - 如果自己是被邀请的一方则弹出提示框，让用户选择接受还是拒绝，两个操作都需要调用 server 接口反馈给 server
  - server 获知结果后会再次下发一条`RCMic:transferHostMsg`消息，消息中包含此次被邀请用户的响应（同意还是拒绝）
  - 主持人在 15 秒之内收到这条消息后就可以根据具体结果取消弹出层并提示用户了
  - 超过 15 秒未收到对方响应则此次转让信息失效
- 接管流程
  - 接管流程和主持人转让流程大致形式一样，只是 server 下发的是`RCMic:takeOverHostMsg`类型的消息，其中的各个字段和之前的消息意义都类似

### 用到的技术脚手架

- Android JetPack
  - LifeCycles
  - DataBinding
  - LiveData
  - Navigation
  - ViewModel
  - WorkManager
- Retrofit
- OkHttp
- Glide
- EventBus

### 关键代码模块说明
```
├── app
│   ├── build.gradle   -------------------------------------------------- //根gradle
│   ├── libs
│   ├── proguard-rules.pro  --------------------------------------------- //混淆规则
│   └── src
│       ├── main
│       │   ├── AndroidManifest.xml  ------------------------------------ //注册表
│       │   ├── assets
│       │   │   ├── airport_gate1.mp3 ----------------------------------- //机场伴音资源
│       │   │   ├── metro_entrance.mp3 ---------------------------------- //火车站伴音资源
│       │   │   └── rain_thunder1.mp3 ----------------------------------- //自然伴音资源
│       │   ├── java
│       │   │   └── cn
│       │   │       └── rongcloud
│       │   │           └── sealmicandroid
│       │   │               ├── MainActivity.java ----------------------- //项目界面总容器
│       │   │               ├── SealMicApp.java ------------------------- //项目application
│       │   │               ├── bean  
│       │   │               │   ├── BgAudioBean.java -------------------- //伴音选项解析类                  
│       │   │               │   ├── SendSuperGiftBean.java ------——------ //超级礼物解析类
│       │   │               │   ├── kv  --------------------------------- //KV存储工具相关的bean
│       │   │               │   │   ├── AppliedMicListBean.java --------- //KV中排麦列表更新解析类
│       │   │               │   │   ├── KvExtraBean.java ---------------- //KV中extra字段解析类
│       │   │               │   │   ├── MicBean.java -------------------- //KV中麦位解析类
│       │   │               │   │   └── SpeakBean.java ------------------ //KV中讲话状态解析类
│       │   │               │   ├── local
│       │   │               │   │   └── BgmBean.java -------------------- //伴音
│       │   │               │   ├── repo  ------------------------------- //接口响应体
│       │   │               │   │   ├── CreateRoomRepo.java ------------- //创建房间response
│       │   │               │   │   ├── NetResult.java ------------------ //网络请求返回的公共response
│       │   │               │   │   ├── RefreshTokenRepo.java ----------- //刷新token接口response
│       │   │               │   │   ├── RoomDetailRepo.java ------------- //房间详情response
│       │   │               │   │   ├── RoomListRepo.java --------------- //房间列表response
│       │   │               │   │   ├── RoomMemberRepo.java ------------- //获取房间成员response
│       │   │               │   │   ├── UserLoginRepo.java -------------- //用户登录response
│       │   │               │   │   ├── VersionCheckRepo.java ----------- //版本更新response
│       │   │               │   │   └── VisitorLoginRepo.java ----------- //游客登录response
│       │   │               │   └── req  -------------------------------- //接口请求体
│       │   │               │       ├── CreateRoomReq.java -------------- //创建房间request
│       │   │               │       ├── MessageBroadCastReq.java -------- //超级礼物消息广播request
│       │   │               │       ├── MicAcceptReq.java --------------- //同意用户上麦request
│       │   │               │       ├── MicApplyReq.java ---------------- //用户申请排麦request
│       │   │               │       ├── MicQuitReq.java ----------------- //主播下麦request
│       │   │               │       ├── MicStateReq.java ---------------- //麦位状态请求request
│       │   │               │       ├── MicTransferHostReq.java --------- //拒绝转让主持人request
│       │   │               │       ├── MicTransferHostResultReq.java --- //同意转让主持人request
│       │   │               │       ├── RoomBanUserReq.java ------------- //用户禁言设置request
│       │   │               │       ├── RoomKickUserReq.java ------------ //将用户踢出房间request
│       │   │               │       ├── RoomSettingReq.java ------------- //房间设置request
│       │   │               │       ├── SendCodeReq.java ---------------- //发送短信验证码request
│       │   │               │       ├── UserInfoReq.java ---------------- //用户信息request
│       │   │               │       ├── UserLoginReq.java --------------- //用户登录request
│       │   │               │       └── VisitorLoginReq.java ------------ //游客登录request
│       │   │               ├── common
│       │   │               │   ├── Event.java -------------------------- //事件总线EventBus事件类
│       │   │               │   ├── MicState.java ----------------------- //麦位状态枚举
│       │   │               │   ├── NetStateLiveData.java --------------- //网络请求对应的响应状态的定制版LiveData
│       │   │               │   ├── SealMicResultCallback.java ---------- //全局结果回调
│       │   │               │   ├── UserType.java ----------------------- //用户类型
│       │   │               │   ├── adapter
│       │   │               │   │   ├── AudioDialogAdapter.java --------- //声音adapter，目前只包括伴音
│       │   │               │   │   ├── BaseDialogListAdapter.java ------ //dialog列表适配器
│       │   │               │   │   ├── ButtonBaseDialogAdapter.java ---- //按钮风格的dialog列表
│       │   │               │   │   ├── ExtensionClickListenerAdapter.java//输入框adapter，适配器模式
│       │   │               │   │   ├── GiftDialogAdapter.java ---------- //礼物dialog adapter
│       │   │               │   │   ├── LiveDataCallAdapter.java -------- //retrofit返回的结果由原生的call转为LiveData
│       │   │               │   │   ├── RTCEventsListenerAdapter.java --- //RTC加入房间对应的回调适配器
│       │   │               │   │   ├── SendMessageAdapter.java --------- //消息送达回调对应的适配器
│       │   │               │   │   ├── SwitchBaseDialogAdapter.java ---- //切换开关风格的dialog列表
│       │   │               │   │   └── TextWatcherAdapter.java --------- //edit宏观适配器，适配器模式，内含3个钩子方法
│       │   │               │   ├── constant
│       │   │               │   │   ├── ErrorCode.java ------------------ //各路回调中的一些通用错误码枚举
│       │   │               │   │   ├── MainLoadData.java --------------- //首页数据拉取方式枚举
│       │   │               │   │   ├── RoomMemberStatus.java ----------- //房间中的成员(观众)状态枚举
│       │   │               │   │   ├── SealMicConstant.java ------------ //本应用全局常量
│       │   │               │   │   ├── SealMicErrorMsg.java ------------ //接口回调错误码和Toast提示信息枚举
│       │   │               │   │   └── UserRoleType.java --------------- //用户角色枚举
│       │   │               │   ├── divider
│       │   │               │   │   ├── GridItemDecoration.java --------- //grid网格列表下均分分布item
│       │   │               │   ├── factory
│       │   │               │   │   ├── CommonViewModelFactory.java ----- //ViewModel传参构造器
│       │   │               │   │   ├── LiveDataCallFactory.java -------- //retrofit返回的结果由原生的call转为LiveData，对应的适配器工厂
│       │   │               │   │   └── dialog -------------------------- //页面弹窗生产工厂
│       │   │               │   │       ├── BgBaseAudioDialogFactory.java //伴音dialog 工厂
│       │   │               │   │       ├── ChangeBaseAudioDialogFactory.java //变声dialog工厂
│       │   │               │   │       ├── ClickMessageDialogFactory.java //点击消息列表弹出的 dialog 工厂
│       │   │               │   │       ├── GiftDialogFactory.java ------- //礼物dialog工厂
│       │   │               │   │       ├── HandOverHostDialogFactory.java //转让主持人弹窗
│       │   │               │   │       ├── MicAudienceFactory.java ------ //观众点击有人时的麦位时触发
│       │   │               │   │       ├── MicConnectDialogFactory.java - //当连麦者点击自己时弹出的dialog
│       │   │               │   │       ├── MicConnectTakeOverDialogFactory.java //连麦者点击主持人麦位，且主持人麦位有人时所对应弹出的dialog
│       │   │               │   │       ├── MicDialogFactory.java -------- //弹出麦位的 dialog 工厂
│       │   │               │   │       ├── MicEnqueueDialogFactory.java - //排麦dialog
│       │   │               │   │       ├── MicSettingDialogFactory.java - //麦位设置dialog
│       │   │               │   │       ├── RoomMemberManagerDialogFactory.java //房间成员管理弹窗
│       │   │               │   │       ├── RoomNoticeDialogFactory.java - //房间公告dialog 工厂
│       │   │               │   │       ├── RoomSettingDialogFactory.java  //主持人设置弹窗
│       │   │               │   │       ├── SelectedGiftDialogFactory.java //选中的礼物后的dialog
│       │   │               │   │       ├── TakeOverHostDialogFactory.java //接管主持人弹窗
│       │   │               │   │       └── base
│       │   │               │   │           ├── BaseAudioDialogFactory.java //声音dialog工厂
│       │   │               │   │           ├── BottomDialogFactory.java -- //从底部弹出的dialog
│       │   │               │   │           ├── CenterDialogFactory.java -- //从中部弹出的dialog
│       │   │               │   │           ├── FullDialogFactory.java ---- //占据全屏的dialog
│       │   │               │   │           └── SealMicDialogFactory.java - //dialog生成工厂，采用工厂方法模式
│       │   │               │   ├── lifecycle ----------------------------- //聊天室页面和主页面生命周期监听
│       │   │               │   │   ├── MainObserver.java ----------------- //主界面观察者
│       │   │               │   │   └── RoomObserver.java ----------------- //聊天室观察者
│       │   │               │   ├── listener
│       │   │               │   │   ├── OnChatRoomTopBarClickListener.java  //聊天室顶部条点击事件
│       │   │               │   │   ├── OnClickGiftListener.java ---------- //点击礼物对应的监听器
│       │   │               │   │   ├── OnDialogButtonListClickListener.java //列表按钮点击的事件监听
│       │   │               │   │   ├── OnHandOverHostDialogClickListener.java //转让主持人dialog点击事件
│       │   │               │   │   ├── OnTakeOverHostDialogClickListener.java //接管主持人dialog点击事件
│       │   │               │   │   └── RoomListItemOnClickListener.java //点击列表项监听
│       │   │               │   ├── service
│       │   │               │   │   └── RTCNotificationService.java ------- //RTC切换至后台之后麦克风的前台通知
│       │   │               │   └── worker
│       │   │               │       └── RongWorker.java -------——---------- //WorkManager初始化类
│       │   │               ├── im ---------------------------------------- //IM交互模块
│       │   │               │   ├── IMClient.java ------------------------- //Rong IM 业务相关封装
│       │   │               │   └── message 
│       │   │               │       ├── HandOverHostMessage.java ---------- //主持人转让通知自定义消息
│       │   │               │       ├── KickMemberMessage.java ------------ //用户被踢出房间通知自定义消息
│       │   │               │       ├── RoomMemberChangedMessage.java ----- //房间成员变动自定义消息
│       │   │               │       ├── SendBroadcastGiftMessage.java ----- //超级礼物广播的自定义消息
│       │   │               │       ├── SendGiftMessage.java -------------- //普通礼物的自定义消息
│       │   │               │       ├── SendGiftTag.java ------------------ //送出不同的礼物时的枚举，依照此来展示不同的动画
│       │   │               │       └── TakeOverHostMessage.java ---------- //主持人接管通知的自定义消息
│       │   │               ├── manager
│       │   │               │   ├── CacheManager.java --------------------- //缓存管理类，主要保存一些本应用的关键信息至sp文件
│       │   │               │   ├── GlideManager.java --------------------- //Glide加载图片管理类，主要是封装一些通用的加载图片的方法
│       │   │               │   ├── NavOptionsRouterManager.java ---------- //默认的navigation跳转配置，默认Fragment之间跳转时的转场动画
│       │   │               │   ├── RoomManager.java ---------------------- //房间进出、上下麦、房间内发送消息等核心操作
│       │   │               │   └── ThreadManager.java -------------------- //线程管理类
│       │   │               ├── model
│       │   │               │   ├── AppModel.java ------------------------- //APP版本管理模块数据层(M层)
│       │   │               │   ├── MicModel.java ------------------------- //麦位模块数据层(M层)
│       │   │               │   ├── RoomModel.java ------------------------ //房间模块数据层(M层)
│       │   │               │   └── UserModel.java ------------------------ //用户模块数据层(M层)
│       │   │               ├── net
│       │   │               │   ├── SealMicUrl.java ----------------------- //seal mic 请求接口地址集合
│       │   │               │   ├── client
│       │   │               │   │   ├── HttpClient.java ------------------- //网络请求与数据层(M层)对接类
│       │   │               │   │   └── RetrofitClient.java --------------- //网络请求基础配置
│       │   │               │   └── service
│       │   │               │       ├── AppService.java -————-------------- //APP版本管理模块接口封装 
│       │   │               │       ├── MicService.java ------------------- //麦位模块请求封装
│       │   │               │       ├── RoomService.java ------------------ //房间模块请求封装
│       │   │               │       └── UserService.java ------------------ //用户模块请求封装
│       │   │               ├── rtc --------------------------------------- //RTC交互模块
│       │   │               │   ├── DebugInfoAdapter.java ----------------- //Debug模式显示
│       │   │               │   └── RTCClient.java ------------------------ //Rong RTC 语音业务封装
│       │   │               ├── ui ---------------------------------------- //所有的UI界面
│       │   │               │   ├── login --------------------------------- //登录
│       │   │               │   │   ├── LoginFragment.java ---------------- //登录界面
│       │   │               │   │   └── LoginViewModel.java
│       │   │               │   ├── main ---------------------------------- //主界面
│       │   │               │   │   ├── AppVersionViewModel.java ---------- //检查版本更新VM
│       │   │               │   │   ├── MainFragment.java
│       │   │               │   │   └── MainViewModel.java
│       │   │               │   ├── room ---------------------------------- //聊天室
│       │   │               │   │   ├── ChatRoomFragment.java 
│       │   │               │   │   ├── ChatRoomViewModel.java
│       │   │               │   │   ├── CreateRoomFragment.java ----------- //创建房间
│       │   │               │   │   ├── CreateRoomViewModel.java
│       │   │               │   │   ├── adapter
│       │   │               │   │   │   ├── BanMemberFragment.java -------- //禁言列表页面
│       │   │               │   │   │   ├── BanMicAdapter.java
│       │   │               │   │   │   ├── ChatRoomRefreshAdapter.java
│       │   │               │   │   │   ├── EnqueueFragment.java ---------- //排麦列表页面
│       │   │               │   │   │   ├── EnqueueMicAdapter.java
│       │   │               │   │   │   ├── OnLineMemberFragment.java ----- //在线列表页面
│       │   │               │   │   │   ├── OnlineRoomMemberAdapter.java
│       │   │               │   │   │   ├── RoomChatListAdapter.java
│       │   │               │   │   │   ├── RoomChatMessageListAdapter.java //聊天室消息列表adapter，Listview
│       │   │               │   │   │   └── RoomMemberManagerDialogAdapter.java //房间成员管理viewpager2对应的adapter
│       │   │               │   │   └── member
│       │   │               │   │       ├── RoomMemberFragment.java ------- //房间观众成员管理
│       │   │               │   │       └── RoomMemberViewModel.java
│       │   │               │   ├── splash -------------------------------- //开屏页
│       │   │               │   │   ├── SplashFragment.java
│       │   │               │   │   └── SplashViewModel.java
│       │   │               │   └── widget -------------------------------- //自定义控件
│       │   │               │       ├── ChatRoomTopBar.java --------------- //聊天室顶部条
│       │   │               │       ├── CustomDynamicAvatar.java ---------- //展示麦位的控件，包括说话时的动画
│       │   │               │       ├── CustomTextView.java
│       │   │               │       ├── CustomTitleBar.java
│       │   │               │       ├── DynamicAvatarView.java ------------ //麦位控件
│       │   │               │       ├── ListItemSwitchButton.java
│       │   │               │       ├── MicTextLayout.java ---------------- //自定义麦位显示用户名称的组合控件
│       │   │               │       ├── RoundRectImageView.java ----------- //自定义的圆角正方形ImageView，可以直接当组件在布局中使用。
│       │   │               │       └── SwitchButton.java ----------------- //状态切换按钮
│       │   │               └── util -------------------------------------- //通用工具类
│       │   │                   ├── BitmapUtil.java
│       │   │                   ├── ButtonDelayUtil.java
│       │   │                   ├── ColorUtil.java
│       │   │                   ├── DeviceUtil.java
│       │   │                   ├── DisplayUtil.java
│       │   │                   ├── HeadsetUtil.java
│       │   │                   ├── KeyBoardUtil.java
│       │   │                   ├── PatternUtil.java
│       │   │                   ├── RandomUtil.java
│       │   │                   ├── SPUtil.java
│       │   │                   ├── SystemUtil.java
│       │   │                   ├── ToastUtil.java
│       │   │                   ├── ToolBarUtil.java
│       │   │                   └── log ----------------------------------- //全局日志类
│       │   │                       ├── ISLog.java 
│       │   │                       ├── SLog.java
│       │   │                       └── SimpleDebugSLog.java
│       │   └── res ------------------------------------------------------- //资源列表
│       │       ├── anim
│       │       ├── drawable
│       │       ├── drawable-v24
│       │       ├── layout
│       │       ├── mipmap-anydpi-v26
│       │       ├── mipmap-hdpi
│       │       ├── mipmap-mdpi
│       │       ├── mipmap-xhdpi
│       │       ├── mipmap-xxhdpi
│       │       ├── mipmap-xxxhdpi
│       │       ├── navigation -------------------------------------------- //navigation导航图
│       │       │   └── nav_main.xml -------------------------------------- //整个应用的页面跳转关系
│       │       └── values
│       │           ├── attrs.xml
│       │           ├── colors.xml
│       │           ├── dimens.xml
│       │           ├── strings.xml
│       │           └── styles.xml
├── build.gradle ---------------------------------------------------------- //app下的gradle
├── gradle ---------------------------------------------------------------- //gradle版本信息
│   └── wrapper
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
├── gradle.properties ----------------------------------------------------- //动态化参数配置项，以及设置融云和其他三方的AppKey
├── gradlew
├── gradlew.bat
├── local.properties
├── settings.gradle ------------------------------------------------------- //参数构建时的控制台输出项
```

### 文档链接
* 关于 Android IM 即时通讯 SDK 的 [开发指南](https://www.rongcloud.cn/docs/android.html)
* 关于 Android 音视频通讯 SDK 的 [开发指南](https://www.rongcloud.cn/docs/android_rtclib.html)
* SealMic Server 源码可以参考[这里](https://github.com/rongcloud/sealmic-server)

### 联系我们
* 如果发现了示例代码的 bug, 欢迎提交 [issue](https://github.com/rongcloud/sealmic-android/issues)
* 如果有售前咨询问题, 可以拨打 13161856839 进行咨询。