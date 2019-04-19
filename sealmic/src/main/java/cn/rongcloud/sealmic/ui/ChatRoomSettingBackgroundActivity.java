package cn.rongcloud.sealmic.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import cn.rongcloud.sealmic.R;
import cn.rongcloud.sealmic.constant.IntentExtra;
import cn.rongcloud.sealmic.task.ResultCallback;
import cn.rongcloud.sealmic.task.RoomManager;
import cn.rongcloud.sealmic.ui.adapter.ChatRoomBGItemAdapter;
import cn.rongcloud.sealmic.utils.ToastUtils;

public class ChatRoomSettingBackgroundActivity extends BaseActivity implements OnClickListener {
    private ImageView backIcon;
    private GridView bgGridView;
    private TextView ivSave;
    private String roomId;
    private int checkedIndex = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_chatroom_background);
        backIcon = findViewById(R.id.ib_back);
        bgGridView = findViewById(R.id.gv_chatroom_bg);
        ivSave = findViewById(R.id.iv_save);
        setBGGridViewAdapter();
        backIcon.setOnClickListener(this);
        ivSave.setOnClickListener(this);
        Intent intent = getIntent();
        roomId = intent.getStringExtra(IntentExtra.ROOM_ID);
    }

    private void setBGGridViewAdapter() {
        int[] bgIdArray = {R.drawable.setting_bg_icon_1, R.drawable.setting_bg_icon_2, R.drawable.setting_bg_icon_3,
                R.drawable.setting_bg_icon_4, R.drawable.setting_bg_icon_5, R.drawable.setting_bg_icon_6,
                R.drawable.setting_bg_icon_7, R.drawable.setting_bg_icon_8, R.drawable.setting_bg_icon_9};
        final ArrayList<ChatRoomBgItem> bgGridList = new ArrayList<>();

        for (int i = 0; i < 9; i++) {
            ChatRoomBgItem bgGridItem = new ChatRoomBgItem(bgIdArray[i], false);
            bgGridList.add(bgGridItem);
        }
        final ChatRoomBGItemAdapter chatRoomBGItemAdapter = new ChatRoomBGItemAdapter(this, bgGridList);
        bgGridView.setAdapter(chatRoomBGItemAdapter);
        bgGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                for (int j = 0; j < bgGridList.size(); j++) {
                    ChatRoomBgItem chatRoomBGItem = bgGridList.get(j);
                    if (j == i) {
                        chatRoomBGItem.checked = true;
                        checkedIndex = j;
                    } else {
                        chatRoomBGItem.checked = false;
                    }
                    bgGridList.set(j, chatRoomBGItem);
                }
                chatRoomBGItemAdapter.updateListView(bgGridList);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ib_back:
                finish();
                break;
            case R.id.iv_save:
                RoomManager.getInstance().setRoomBackground(checkedIndex, new ResultCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        finish();
                    }

                    @Override
                    public void onFail(int errorCode) {
                        ToastUtils.showErrorToast(R.string.toast_error_network);
                    }
                });
                break;
        }
    }
}
