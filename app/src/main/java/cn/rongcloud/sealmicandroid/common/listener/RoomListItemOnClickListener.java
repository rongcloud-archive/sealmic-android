package cn.rongcloud.sealmicandroid.common.listener;

import android.view.View;

import cn.rongcloud.sealmicandroid.bean.repo.RoomListRepo;

/**
 * 点击列表项监听
 */
public interface RoomListItemOnClickListener {

    /**
     * 点击
     *
     * @param roomsBean 房间信息
     * @param position  当前列表索引
     */
    void onClick(View view, RoomListRepo.RoomsBean roomsBean, int position);

    /**
     * 点击创建房间
     */
    void onClickCreateRoom();
}
