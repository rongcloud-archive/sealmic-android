package cn.rongcloud.sealmic.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.rongcloud.sealmic.R;
import cn.rongcloud.sealmic.constant.IntentExtra;

public class ChatRoomSettingWindow extends BaseActivity implements OnClickListener {
    private RelativeLayout backgroundLayout;
    private TextView exitTv;
    private String roomId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_dialog);
        backgroundLayout = findViewById(R.id.iv_background_setting);
        backgroundLayout.setOnClickListener(this);
        exitTv = findViewById(R.id.setting_tv_exit_room);
        exitTv.setOnClickListener(this);
        Intent intent = getIntent();
        roomId = intent.getStringExtra(IntentExtra.ROOM_ID);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        finish();
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_background_setting:
                Intent intent = new Intent(this, ChatRoomSettingBackgroundActivity.class);
                intent.putExtra(IntentExtra.ROOM_ID, roomId);
                startActivityForResult(intent, 0);
                break;
            case R.id.setting_tv_exit_room:
                Intent exitIntent = new Intent();
                exitIntent.putExtra(IntentExtra.EXIT_ROOM, true);
                setResult(RESULT_OK, exitIntent);
                finish();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            finish();
        }
    }
}
