package cn.rongcloud.sealmicandroid.ui.main;

import androidx.lifecycle.ViewModel;

import cn.rongcloud.sealmicandroid.bean.repo.VersionCheckRepo;
import cn.rongcloud.sealmicandroid.common.NetStateLiveData;
import cn.rongcloud.sealmicandroid.model.AppModel;
import cn.rongcloud.sealmicandroid.util.DeviceUtil;

/**
 * 检查版本VM
 */
public class AppVersionViewModel extends ViewModel {

    private AppModel appModel;

    public AppVersionViewModel(AppModel appModel) {
        this.appModel = appModel;
    }

    public NetStateLiveData<VersionCheckRepo> checkVersion() {
        long versionCode = DeviceUtil.getAppVersionCode();
        return appModel.checkVersion("Android", versionCode);
    }

}
