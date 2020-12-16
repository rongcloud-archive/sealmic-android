package cn.rongcloud.sealmicandroid;

import android.Manifest;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import cn.rongcloud.sealmicandroid.manager.CacheManager;
import cn.rongcloud.sealmicandroid.ui.widget.CustomTitleBar;
import cn.rongcloud.sealmicandroid.ui.widget.SwitchButton;
import cn.rongcloud.sealmicandroid.util.CheckPermissionUtil;
import cn.rongcloud.sealmicandroid.util.ZipUtil;
import cn.rongcloud.sealmicandroid.util.log.SLog;
import io.rong.common.FileUtils;
import io.rong.imlib.common.BuildVar;

public class DebugActivity extends FragmentActivity {
    private static final String TAG = "DebugActivity";
    private static final int TYPE_SINGLE_TITLE = 0;
    private static final int TYPE_SWITCH = 1;
    private ArrayList<ItemUI> mData;
    private Set<String> debugFilesPath;

    private interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    private interface Action {
        void action();
    }

    private final OnItemClickListener mListener = new OnItemClickListener() {
        @Override
        public void onItemClick(View v, int position) {
            Log.i(TAG, "onItemClick position = " + position);
            Action action = mData.get(position).getAction();
            if (action != null) {
                action.action();
            }
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.debug_activity);
        mData = new ArrayList<>();
        debugFilesPath = new HashSet<>();
        initData();
        RecyclerView recyclerView = findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new DebugAdapter(mListener));
        CustomTitleBar customTitleBar = findViewById(R.id.debug_title);
        customTitleBar.setTitleClickListener(new CustomTitleBar.TitleClickListener() {
            @Override
            public void onLeftClick() {
                closeDebugModule();
            }

            @Override
            public void onRightClick() {

            }

            @Override
            public void onTitleLongClick() {

            }
        });
    }

    class DebugAdapter extends RecyclerView.Adapter<DebugAdapter.ItemHolder> implements View.OnClickListener {
        private OnItemClickListener mOnItemClickListener = null;

        public DebugAdapter(OnItemClickListener mOnItemClickListener) {
            this.mOnItemClickListener = mOnItemClickListener;
        }

        @Override
        public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = getLayoutInflater().inflate(R.layout.item_app_debug_text, parent, false);
            ItemHolder holder = new ItemHolder(v);
            if (v != null) {
                v.setOnClickListener(this);
            }
            return holder;
        }

        @Override
        public void onBindViewHolder(ItemHolder holder, int position) {
            int type = getItemViewType(position);
            ItemUI itemUI = mData.get(position);
            holder.title.setText(itemUI.getTitle());
            switch (type) {
                case TYPE_SINGLE_TITLE:
                    holder.switchButton.setVisibility(View.GONE);
                    break;
                case TYPE_SWITCH:
                    holder.switchButton.setVisibility(View.VISIBLE);
                    holder.switchButton.setChecked(itemUI.getState());
                    if (itemUI.isEnableOnCheckedChange()) {
                        holder.switchButton.setOnCheckedChangeListener(itemUI.onCheckedChangeListener);
                    } else {
                        holder.switchButton.setOnCheckedChangeListener(null);
                    }
                default:
                    break;
            }
            holder.itemView.setTag(position);
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(v, (int) v.getTag());
            }
        }

        class ItemHolder extends RecyclerView.ViewHolder {
            TextView title;
            ImageView imageView;
            SwitchButton switchButton;

            public ItemHolder(View itemView) {
                super(itemView);
                title = itemView.findViewById(R.id.title);
                switchButton = itemView.findViewById(R.id.switch_button);
            }
        }

        @Override
        public int getItemViewType(int position) {
            return mData.get(position).getType();
        }
    }


    private class ItemUI {
        private String title;
        private boolean state;
        private boolean enableOnCheckedChange;
        private int type;
        private Action action;
        private CompoundButton.OnCheckedChangeListener onCheckedChangeListener;

        ItemUI(int stringId, int type, Action action) {
            this.title = getResources().getString(stringId);
            this.type = type;
            this.action = action;
        }

        ItemUI(int stringId, int type, CompoundButton.OnCheckedChangeListener onCheckedChangeListener) {
            this.title = getResources().getString(stringId);
            this.type = type;
            this.onCheckedChangeListener = onCheckedChangeListener;
        }

        public String getTitle() {
            return title;
        }

        public int getType() {
            return type;
        }

        public boolean getState() {
            return state;
        }

        public void setState(boolean state) {
            this.state = state;
        }

        boolean isEnableOnCheckedChange() {
            return enableOnCheckedChange;
        }

        void setEnableOnCheckedChange(boolean enableOnCheckedChange) {
            this.enableOnCheckedChange = enableOnCheckedChange;
        }

        public void setAction(Action action) {
            this.action = action;
        }

        public @Nullable
        Action getAction() {
            return action;
        }
    }

    private String createLogFilePath() {
        String logcatDir = FileUtils.getCachePath(getApplicationContext(), "ronglog");
        String logcatFilePath = logcatDir + "/SealMicLog.log";
        SLog.i(TAG, "logcatFilePath: " + logcatFilePath);
        return logcatFilePath;
    }

    private String createIMLogFilePath() {
        File file = SealMicApp.getApplication().getExternalFilesDir("RLog");
        if (file == null) {
            return "";
        }
        String logcatFilePath = file.getPath() + "/r.log";
        SLog.i(TAG, "logcatFilePath: " + logcatFilePath);
        return logcatFilePath;
    }

    private void getLogInfo() {
        //Create a file
        File sealfile = new File(createLogFilePath());
        File imFile = new File(createIMLogFilePath());
        //write log to the above file, and then clear the log
        try {
            Runtime.getRuntime().exec("logcat -d -v threadtime -f "
                    + sealfile.getAbsolutePath());
            Runtime.getRuntime().exec("logcat -d -v threadtime -f "
                    + imFile.getAbsolutePath());
            debugFilesPath.add(sealfile.getAbsolutePath());
//            debugFilesPath.add(imFile.getAbsolutePath());
            Toast.makeText(this, R.string.about_debug_export_logcat,
                    Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            SLog.e(TAG, e.toString());
        }
    }

    private String createZipDebugFilePath() {
        String zipDir = FileUtils.getCachePath(getApplicationContext(), "debugZipFile");
        String zipFilePath = zipDir + File.separator + "RongDebugInfo.zip";
        SLog.i(TAG, "zipFilePath: " + zipFilePath);
        return zipFilePath;
    }

    private String createZipIMDebugFilePath() {
        String zipIMDir = FileUtils.getCachePath(getApplicationContext(), "debugZipFile");
        String zipFilePath = zipIMDir + File.separator + "RongIMDebugInfo.zip";
        SLog.i(TAG, "zipFilePath: " + zipFilePath);
        return zipFilePath;
    }

    private String getToken() {
        return CacheManager.getInstance().getToken();
    }

    private void exportDebugFile() {
        String zipFilePath = createZipDebugFilePath();
        exportCacheFile();
        exportRLogFile();
        exportIMLogFile();
        String zipIMFilePath = createZipIMDebugFilePath();

        ZipUtil.zip(debugFilesPath
                .toArray(new String[debugFilesPath.size()]), zipIMFilePath);
        ZipUtil.zip(debugFilesPath
                .toArray(new String[debugFilesPath.size()]), zipFilePath);

        Toast.makeText(this, "压缩地址为: " + zipFilePath, Toast.LENGTH_SHORT).show();
    }

    private void exportIMLogFile() {
        File file = SealMicApp.getApplication().getExternalFilesDir("RLog");
        if (file == null) {
            return;
        }
        if (file.exists()) {
            for (File subFile : file.listFiles()) {
                if (subFile.exists()) {
                    debugFilesPath.add(subFile.getPath());
                }
            }
        }
    }

    public void exportCacheFile() {
        SharedPreferences preferences = CacheManager.getInstance().getSharedPreferencesFile();
        File prefsPath = new File(FileUtils.getCachePath(SealMicApp.getApplication()));
        File prefsFile = new File(prefsPath, "SealMic_SharedPreferences.xml");
        try {
            FileWriter fileWriter = new FileWriter(prefsFile);
            PrintWriter printWriter = new PrintWriter(fileWriter);

            Map<String, ?> prefsMap = preferences.getAll();
            for (Map.Entry<String, ?> entry : prefsMap.entrySet()) {
                printWriter.println(entry.getKey() + ": " + entry.getValue().toString());
            }

            printWriter.close();
            fileWriter.close();
            debugFilesPath.add(prefsFile.getAbsolutePath());
        } catch (Exception e) {
            SLog.e(getClass().getName(), e.toString());
        }
    }

    private void exportRLogFile() {
        String logcatDir = FileUtils.getCachePath(getApplicationContext(), "ronglog");
        String sLogFilePath = logcatDir + "/RongLog_" + BuildVar.SDK_VERSION.replace(".", "_") + ".log";
        File fLogFile = new File(sLogFilePath);
        if (fLogFile.exists()) {
            debugFilesPath.add(sLogFilePath);
        }
        String sDebugLogFilePath = logcatDir + "/Debug.rlog";
        File fDebugLogFile = new File(sDebugLogFilePath);
        if (fDebugLogFile.exists()) {
            debugFilesPath.add(sDebugLogFilePath);
        }
    }

    private void closeDebugModule() {
        debugFilesPath = null;
        CacheManager.getInstance().cacheDebugMode(false);
        Toast.makeText(this,
                R.string.about_closed_debug, Toast.LENGTH_SHORT).show();
        finish();
    }

    private void initData() {
        mData.add(new ItemUI(R.string.about_debug_logcat, TYPE_SINGLE_TITLE, new Action() {
            @Override
            public void action() {
                getLogInfo();
            }
        }));

        mData.add(new ItemUI(R.string.about_debug_zip_files, TYPE_SINGLE_TITLE, new Action() {
            @Override
            public void action() {
                String[] permissions = {
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE};
                if (!CheckPermissionUtil.requestPermissions(DebugActivity.this, permissions, CacheManager.REQUEST_CODE_PERMISSION)) {
                    return;
                }
                exportDebugFile();
            }
        }));

        mData.add(new ItemUI(R.string.about_closed_debug, TYPE_SINGLE_TITLE, new Action() {
            @Override
            public void action() {
                closeDebugModule();
            }
        }));
    }
}