package cn.rongcloud.sealmicandroid.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import cn.rongcloud.sealmicandroid.R;
import cn.rongcloud.sealmicandroid.manager.ThreadManager;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 展示麦位的控件，包括说话时的动画
 */
public class CustomDynamicAvatar extends RelativeLayout {

    private RelativeLayout itembg;
    private RelativeLayout itemRel;
    private View bgInside;
    private View bgExternal;
    private CircleImageView img;
    private ImageView lockWheet;
    private CircleImageView lockImg;
    private CircleImageView addUser;
    private Animation externalAnimation;
    private RelativeLayout itemMain;
    //是否是主麦位
    private boolean aBoolean = false;
    private RelativeLayout itemImgMic;

    public CustomDynamicAvatar(Context context) {
        this(context, null);
    }

    public CustomDynamicAvatar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomDynamicAvatar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CustomDynamicAvatarMic);
        aBoolean = ta.getBoolean(R.styleable.CustomDynamicAvatarMic_isMainMic, false);
        initView(context);
        ta.recycle();
    }

    private void initView(final Context context) {
        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.room_item_icon, this, true);
        itemMain = inflate.findViewById(R.id.chatroom_user_item_main);
        itembg = inflate.findViewById(R.id.chatroom_user_item_bg);
        itemRel = inflate.findViewById(R.id.chatroom_user_item_rel);
        itemImgMic = inflate.findViewById(R.id.chatroom_user_item_imgwheet);
        bgInside = inflate.findViewById(R.id.chatroom_user_item_bginside);
        bgExternal = inflate.findViewById(R.id.chatroom_user_item_bgexternal);
        img = inflate.findViewById(R.id.chatroom_user_item_img);
        lockWheet = inflate.findViewById(R.id.chatroom_user_item_lockwheet);
        lockImg = inflate.findViewById(R.id.chatroom_user_item_lock);
        addUser = inflate.findViewById(R.id.chatroom_user_item_add);

        externalAnimation = AnimationUtils.loadAnimation(context, R.anim.alpha);

        if (aBoolean) {
            //设置为主麦位
            setMainMic();
        }


    }

    /**
     * 用户下麦
     */
    public void micDelUser() {
        ThreadManager.getInstance().runOnUIThread(new Runnable() {
            @Override
            public void run() {
                //停止用户发言
                stopSpeak();
                //改变状态
                unLockMic();
            }
        });
    }

    /**
     * 用户上麦
     */
    public void micAddUser() {
        ThreadManager.getInstance().runOnUIThread(new Runnable() {
            @Override
            public void run() {
                itemRel.setVisibility(VISIBLE);
                img.setVisibility(VISIBLE);
                addUser.setVisibility(GONE);
                lockImg.setVisibility(GONE);
            }
        });

    }

    /**
     * 用户发言
     */
    public void startSpeak() {
        ThreadManager.getInstance().runOnUIThread(new Runnable() {
            @Override
            public void run() {
                //改变状态
                micAddUser();
                //判断用户头像是否在显示
                if (itemRel.getVisibility() == VISIBLE) {
                    //执行渐变动画，达到波动效果
                    itembg.setVisibility(VISIBLE);
                    bgInside.startAnimation(externalAnimation);
                    bgExternal.startAnimation(externalAnimation);
                }
            }
        });

    }

    /**
     * 用户停止发言
     */
    public void stopSpeak() {
        ThreadManager.getInstance().runOnUIThread(new Runnable() {
            @Override
            public void run() {
                itembg.setVisibility(GONE);
                externalAnimation.cancel();
            }
        });
    }

    /**
     * 闭麦
     */
    public void bankMic() {
        ThreadManager.getInstance().runOnUIThread(new Runnable() {
            @Override
            public void run() {
                //判断用户头像是否显示
                if (itemRel.getVisibility() == VISIBLE) {
                    lockWheet.setVisibility(VISIBLE);
                }
            }
        });

    }

    /**
     * 关闭闭麦
     */
    public void unBankMic() {
        ThreadManager.getInstance().runOnUIThread(new Runnable() {
            @Override
            public void run() {
                lockWheet.setVisibility(GONE);
            }
        });

    }

    /**
     * 锁麦
     */
    public void lockMic() {
        ThreadManager.getInstance().runOnUIThread(new Runnable() {
            @Override
            public void run() {
                //锁麦停止发言，状态改变
                stopSpeak();
                lockImg.setVisibility(VISIBLE);
                addUser.setVisibility(GONE);
                itemRel.setVisibility(GONE);
                itembg.setVisibility(GONE);
            }
        });

    }


    /**
     * 解锁麦位
     */
    public void unLockMic() {
        ThreadManager.getInstance().runOnUIThread(new Runnable() {
            @Override
            public void run() {
                addUser.setVisibility(VISIBLE);
                lockImg.setVisibility(GONE);
                lockWheet.setVisibility(GONE);
                itemRel.setVisibility(GONE);
                itembg.setVisibility(GONE);
            }
        });

    }

    /**
     * 返回Image View,并且把用户头像显示出来
     *
     * @return 展示用户头像的ImageView
     */
    public ImageView getUserImg() {
        micAddUser();
        return img;
    }

    private int getDp(int sourceId) {
        return getResources().getDimensionPixelSize(sourceId);
    }

    /**
     * 设置为主麦位,改变控件的大小以及边距
     */
    public void setMainMic() {
        //改变父控件的大小
        ViewGroup.LayoutParams mainParams = itemMain.getLayoutParams();
        mainParams.height = getDp(R.dimen.dimen_99);
        mainParams.width = getDp(R.dimen.dimen_99);
        itemMain.setLayoutParams(mainParams);
        //改变头像和外围动画的大小
        RelativeLayout.LayoutParams relParams = (LayoutParams) itemRel.getLayoutParams();
        relParams.width = getDp(R.dimen.dimen_99);
        relParams.height = getDp(R.dimen.dimen_99);
        itemRel.setLayoutParams(relParams);
        RelativeLayout.LayoutParams bgParams = (LayoutParams) itembg.getLayoutParams();
        bgParams.width = getDp(R.dimen.dimen_99);
        bgParams.height = getDp(R.dimen.dimen_99);
        itembg.setLayoutParams(bgParams);
        //改变头像旁边发言内围动画的大小
        RelativeLayout.LayoutParams insideParams = (LayoutParams) bgInside.getLayoutParams();
        insideParams.height = getDp(R.dimen.dimen_87);
        insideParams.width = getDp(R.dimen.dimen_87);
        bgInside.setLayoutParams(insideParams);
        //改变头像旁边发言外围动画的大小
        RelativeLayout.LayoutParams externalParams = (LayoutParams) bgExternal.getLayoutParams();
        externalParams.height = getDp(R.dimen.dimen_99);
        externalParams.width = getDp(R.dimen.dimen_99);
        bgExternal.setLayoutParams(externalParams);
        //改变头像的大小
        RelativeLayout.LayoutParams imgParams = (LayoutParams) img.getLayoutParams();
        imgParams.height = getDp(R.dimen.dimen_79);
        imgParams.width = getDp(R.dimen.dimen_79);
        img.setLayoutParams(imgParams);
        RelativeLayout.LayoutParams imgWheetParams = (LayoutParams) itemImgMic.getLayoutParams();
        imgWheetParams.width = getDp(R.dimen.dimen_79);
        imgWheetParams.height = getDp(R.dimen.dimen_79);
        itemImgMic.setLayoutParams(imgWheetParams);
        //改变闭麦标识的外边距
        RelativeLayout.LayoutParams lockWheetParams = (LayoutParams) lockWheet.getLayoutParams();
        lockWheetParams.rightMargin = getDp(R.dimen.dimen_3);
        lockWheetParams.bottomMargin = getDp(R.dimen.dimen_3);
        lockWheet.setLayoutParams(lockWheetParams);
        //改变闭麦标识的大小
        RelativeLayout.LayoutParams lockImgParams = (LayoutParams) lockImg.getLayoutParams();
        lockImgParams.width = getDp(R.dimen.dimen_79);
        lockImgParams.height = getDp(R.dimen.dimen_79);
        lockImg.setLayoutParams(lockImgParams);
        //改变上麦标识的大小
        RelativeLayout.LayoutParams adduserParams = (LayoutParams) addUser.getLayoutParams();
        adduserParams.width = getDp(R.dimen.dimen_79);
        adduserParams.height = getDp(R.dimen.dimen_79);
        addUser.setLayoutParams(adduserParams);


    }

    private int getPixelsFromDp(int i) {
        DisplayMetrics metrics = new DisplayMetrics();
        return (i * metrics.densityDpi) / DisplayMetrics.DENSITY_DEFAULT;
    }

}
