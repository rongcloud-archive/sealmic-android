package cn.rongcloud.sealmicandroid.manager;

import android.os.Bundle;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import cn.rongcloud.sealmicandroid.R;
import cn.rongcloud.sealmicandroid.common.constant.SealMicConstant;
import cn.rongcloud.sealmicandroid.common.constant.UserRoleType;
import cn.rongcloud.sealmicandroid.util.ButtonDelayUtil;

/**
 * 默认的navigation跳转配置，默认Fragment之间跳转时的转场动画
 */
public class NavOptionsRouterManager {

    private NavOptionsRouterManager() {
    }

    private static class DefaultNavOptionsHelper {
        private static final NavOptionsRouterManager INSTANCE = new NavOptionsRouterManager();
    }

    public static NavOptionsRouterManager getInstance() {
        return DefaultNavOptionsHelper.INSTANCE;
    }

    private NavOptions getDefaultNavOptions() {
        return new NavOptions.Builder()
                .setPopUpTo(R.id.mainFragment, false)
                .setLaunchSingleTop(true)
                .setEnterAnim(R.anim.nav_animation_pop_enter)
                .setExitAnim(R.anim.nav_animation_exit)
                .setPopEnterAnim(R.anim.nav_animation_enter)
                .setPopExitAnim(R.anim.nav_animation_pop_exit)
                .build();
    }

    private NavOptions getMainNavOptions() {
        return new NavOptions.Builder()
                .setPopUpTo(R.id.mainFragment, false)
                .setLaunchSingleTop(true)
                .setEnterAnim(R.anim.nav_animation_enter)
                .setExitAnim(R.anim.nav_animation_exit)
                .setPopEnterAnim(R.anim.nav_animation_pop_enter)
                .setPopExitAnim(R.anim.nav_animation_pop_exit)
                .build();
    }

    /**
     * 跳转至登录界面
     */
    public void gotoLoginFragmentFromMain(View view) {
        Bundle bundle = new Bundle();
        Navigation.findNavController(view).navigate(R.id.action_mainFragment_to_loginFragment, bundle, getMainNavOptions());
    }

    /**
     * 跳转至登录界面
     */
    public void gotoLoginFragmentFromChatRoom(View view) {
        Navigation.findNavController(view).navigate(R.id.action_chatRoomFragment_to_loginFragment);
    }

    /**
     * 跳转至登录界面
     */
    public void gotoLoginFragmentFromCreateRoom(View view) {
        Navigation.findNavController(view).navigate(R.id.action_createRoomFragment_to_loginFragment);
    }

    /**
     * 跳转至房间页面主界面
     */
    public void gotoMainFragmentFromLogin(View view) {
        Bundle bundle = new Bundle();
        Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_mainFragment, bundle, getDefaultNavOptions());
    }

    /**
     * 跳转至创建房间界面
     */
    public void gotoCreateRoomFragment(View view) {
        Navigation.findNavController(view).navigate(R.id.action_mainFragment_to_createRoomFragment);
    }

    /**
     * 跳转至聊天室界面并回退返回栈
     */
    public void gotoChatRoomFragmentAndBackStack(View view, String roomId, String roomName, String roomTheme, UserRoleType userRoleType) {
        Bundle bundle = new Bundle();
        bundle.putString(SealMicConstant.ROOM_ID, roomId);
        bundle.putString(SealMicConstant.ROOM_NAME, roomName);
        bundle.putString(SealMicConstant.ROOM_THEME, roomTheme);
        bundle.putSerializable(SealMicConstant.ROOM_USER_ROLE, userRoleType);
        //防止在设备占用率较高时连续点击跳转出现概率性崩溃的情况，加入防止连续点击，间隔一秒
        if (ButtonDelayUtil.isNormalClick()) {
            Navigation.findNavController(view).navigate(
                    R.id.action_createRoomFragment_to_chatRoomFragment, bundle, getDefaultNavOptions());
        }
    }

    /**
     * 跳转至聊天室界面
     */
    public void gotoChatRoomFragment(View view, String roomId, String roomName, String roomTheme, UserRoleType userRoleType) {
        Bundle bundle = new Bundle();
        bundle.putString(SealMicConstant.ROOM_ID, roomId);
        bundle.putString(SealMicConstant.ROOM_NAME, roomName);
        bundle.putString(SealMicConstant.ROOM_THEME, roomTheme);
        bundle.putSerializable(SealMicConstant.ROOM_USER_ROLE, userRoleType);
        NavController navController = Navigation.findNavController(view);
        if (navController.getCurrentDestination() != null) {
            if (navController.getCurrentDestination().getId() == R.id.mainFragment) {
                navController.navigate(R.id.action_mainFragment_to_chatRoomFragment, bundle);
            }
        }
    }

    /**
     * 返回上一页
     */
    public void backUp(View view) {
        Navigation.findNavController(view).navigateUp();
    }

    /**
     * 清空fragment返回栈
     */
    public void clearBackStack(View view) {
        Navigation.findNavController(view).popBackStack();
    }

}
