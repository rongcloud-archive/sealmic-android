package cn.rongcloud.sealmicandroid.common.factory.dialog;

import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import cn.rongcloud.sealmicandroid.R;
import cn.rongcloud.sealmicandroid.SealMicApp;
import cn.rongcloud.sealmicandroid.common.Event;
import cn.rongcloud.sealmicandroid.common.adapter.GiftDialogAdapter;
import cn.rongcloud.sealmicandroid.common.adapter.SendMessageAdapter;
import cn.rongcloud.sealmicandroid.common.factory.dialog.base.BottomDialogFactory;
import cn.rongcloud.sealmicandroid.common.listener.OnClickGiftListener;
import cn.rongcloud.sealmicandroid.im.IMClient;
import cn.rongcloud.sealmicandroid.im.message.SendGiftTag;
import cn.rongcloud.sealmicandroid.manager.CacheManager;
import cn.rongcloud.sealmicandroid.util.ToastUtil;
import io.rong.imlib.IRongCoreEnum;
import io.rong.imlib.model.Message;

/**
 * 礼物dialog工厂
 */
public class GiftDialogFactory extends BottomDialogFactory {
    //礼物送给谁
    private String targetName;
    private List<String> strings;
    private CallSendGiftMessage callSendGiftMessage;
    private boolean isSnedSuperCar = false;

    public Dialog buildDialog(final FragmentActivity context, final String targetName) {
        final Dialog dialog = super.buildDialog(context);
        dialog.setContentView(R.layout.dialog_gift);
        RecyclerView chatRoomGiftRecyclerView = dialog.findViewById(R.id.rv_chat_room_gift);
        chatRoomGiftRecyclerView.setLayoutManager(new GridLayoutManager(context, 4));
        GiftDialogAdapter giftDialogAdapter = new GiftDialogAdapter();
        final SelectedGiftDialogFactory selectedGiftDialogFactory = new SelectedGiftDialogFactory();
        final Drawable[] selectedDrawable = {null};
        final int[] selectedPosition = {-1};
        giftDialogAdapter.setOnClickGifListener(new OnClickGiftListener() {
            @Override
            public void onClickGift(View view, Drawable drawable, int position) {
                selectedDrawable[0] = drawable;
                selectedPosition[0] = position;
            }
        });
        List<Drawable> drawables = new ArrayList<>();
        drawables.add(ContextCompat.getDrawable(context, R.mipmap.ic_gift_laugh));
        drawables.add(ContextCompat.getDrawable(context, R.mipmap.ic_gift_honey));
        drawables.add(ContextCompat.getDrawable(context, R.mipmap.ic_gift_ice_cream));
        drawables.add(ContextCompat.getDrawable(context, R.mipmap.ic_gift_saving_pot));
        drawables.add(ContextCompat.getDrawable(context, R.mipmap.ic_gift_air_ticket));
        drawables.add(ContextCompat.getDrawable(context, R.mipmap.ic_gift_box));
        drawables.add(ContextCompat.getDrawable(context, R.mipmap.ic_gift_mini_car));
        drawables.add(ContextCompat.getDrawable(context, R.mipmap.ic_gift_super_car));

        strings = new ArrayList<>();
        strings.add("笑脸");
        strings.add("蜂蜜");
        strings.add("冰淇淋");
        strings.add("存钱罐");
        strings.add("机票");
        strings.add("宝箱");
        strings.add("爱心车");
        strings.add("豪华跑车");

        giftDialogAdapter.setDrawables(drawables, strings);
        chatRoomGiftRecyclerView.setAdapter(giftDialogAdapter);
        TextView userNameTextView = dialog.findViewById(R.id.tv_user_name);
        userNameTextView.setText("礼物");
        TextView sendGiftTextView = dialog.findViewById(R.id.tv_send_gift);
        sendGiftTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击礼物之后: 1. 显示礼物的弹窗动画  2. 发送一条IM消息
                String roomId = CacheManager.getInstance().getRoomId();
                if (selectedDrawable[0] == null || selectedPosition[0] == -1) {
                    ToastUtil.showToast("请先选礼物再赠送");
                    return;
                }
                if (selectedPosition[0] == 7) {
                    //发送的是否是大礼物
                    isSnedSuperCar = true;
                } else {
                    isSnedSuperCar = false;
                }
                final Dialog buildDialog = selectedGiftDialogFactory.setSelectedGift(ContextCompat.getDrawable(SealMicApp.getApplication()
                        , SendGiftTag.getGiftType(getGiftTag(selectedPosition[0])))).buildDialog(context);
                dialog.cancel();
                Message sendGiftMessage;
                String content;
                if (targetName.isEmpty()) {
                    content = " 给 所有人 送了" + strings.get(selectedPosition[0]);
                    selectedGiftDialogFactory.setGiftContent(" 给 所有人 送了" + strings.get(selectedPosition[0]));
                    sendGiftMessage = IMClient.getInstance().getSendGiftMessage(roomId, content, getGiftTag(selectedPosition[0]));
                } else {
                    content = " 给 " + targetName + " 送了" + strings.get(selectedPosition[0]);
                    selectedGiftDialogFactory.setGiftContent(" 给 " + targetName + " 送了" + strings.get(selectedPosition[0]));
                    sendGiftMessage = IMClient.getInstance().getSendGiftMessage(roomId, content, getGiftTag(selectedPosition[0]));
                }
                selectedGiftDialogFactory.setGiftTitle(CacheManager.getInstance().getUserName());
                IMClient.getInstance().sendMessage(sendGiftMessage, new SendMessageAdapter() {
                    @Override
                    public void onSuccess(Message message) {
                        //发送的如果是大礼物就发送事件通知
                        if (isSnedSuperCar) {
                            EventBus.getDefault().post(new Event.EventSendSuperGift());
                        }
                        //回调通知，通知界面把消息插入到消息列表中
                        buildDialog.show();
                        callSendGiftMessage.callMessage(message);
                    }

                    @Override
                    public void onError(Message message, IRongCoreEnum.CoreErrorCode coreErrorCode) {
                        super.onError(message, coreErrorCode);
                        //已经被禁言，发送消息失败
                        if (coreErrorCode.getValue() == IRongCoreEnum.CoreErrorCode.FORBIDDEN_IN_CHATROOM.getValue()) {
                            ToastUtil.showToast(SealMicApp.getApplication().getResources().getString(R.string.cant_speak));
                        }
                    }

                });
                dialog.cancel();
            }
        });
        WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
        layoutParams.width = context.getResources().getDisplayMetrics().widthPixels;
        return dialog;
    }

    public String getGiftTag(int position) {
        if (position == 0) {
            return SendGiftTag.GIFT_SMELL.getTag();
        } else if (position == 1) {
            return SendGiftTag.GIFT_HONEY.getTag();
        } else if (position == 2) {
            return SendGiftTag.GIFT_ICE.getTag();
        } else if (position == 3) {
            return SendGiftTag.GIFT_SAVING_POT.getTag();
        } else if (position == 4) {
            return SendGiftTag.GIFT_AIR_TICKET.getTag();
        } else if (position == 5) {
            return SendGiftTag.GIFT_TREASURE_BOX.getTag();
        } else if (position == 6) {
            return SendGiftTag.GIFT_LOVING_CAR.getTag();
        } else if (position == 7) {
            return SendGiftTag.GIFT_SPORTS_CAR.getTag();
        } else {
            return SendGiftTag.GIFT_SMELL.getTag();
        }
    }

    public void setTargetName(String name) {
        targetName = name;
    }

    public interface CallSendGiftMessage {
        /**
         * 回调发送礼物的Message
         *
         * @param message
         */
        void callMessage(Message message);
    }

    public void setCallSendGiftMessage(CallSendGiftMessage callSendGiftMessage) {
        this.callSendGiftMessage = callSendGiftMessage;
    }
}
