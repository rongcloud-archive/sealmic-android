package cn.rongcloud.sealmicandroid.model;

import cn.rongcloud.sealmicandroid.bean.repo.VersionCheckRepo;
import cn.rongcloud.sealmicandroid.common.NetStateLiveData;
import cn.rongcloud.sealmicandroid.net.client.RetrofitClient;
import cn.rongcloud.sealmicandroid.net.service.AppService;

/**
 * APP版本管理模块数据层(M层)
 */
public class AppModel {

    private AppService appService;

    public AppModel(RetrofitClient client) {
        appService = client.createService(AppService.class);
    }

    public NetStateLiveData<VersionCheckRepo> checkVersion(String platform, Long versionCode) {
        return appService.versionCheck(platform, versionCode);
    }
}
