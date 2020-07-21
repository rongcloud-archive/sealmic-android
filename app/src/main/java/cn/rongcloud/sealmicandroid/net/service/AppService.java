package cn.rongcloud.sealmicandroid.net.service;

import cn.rongcloud.sealmicandroid.bean.repo.VersionCheckRepo;
import cn.rongcloud.sealmicandroid.common.NetStateLiveData;
import cn.rongcloud.sealmicandroid.net.SealMicUrl;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * APP版本管理模块接口封装
 */
public interface AppService {

    @GET(SealMicUrl.APP_VERSION_LATEST)
    NetStateLiveData<VersionCheckRepo> versionCheck(@Query("platform") String platform, @Query("versionCode") Long versionCode);

}
