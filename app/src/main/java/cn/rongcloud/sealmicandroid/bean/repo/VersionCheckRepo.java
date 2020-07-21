package cn.rongcloud.sealmicandroid.bean.repo;

public class VersionCheckRepo extends NetResult<VersionCheckRepo> {


    /**
     * platform : Android Q
     * downloadUrl : http://www.baidu.com
     * version : 2.1.1
     * versionCode : 20200617
     * forceUpgrade : true
     * releaseNote : 版本描述
     */

    private String platform;
    private String downloadUrl;
    private String version;
    private String versionCode;
    private boolean forceUpgrade;
    private String releaseNote;

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public boolean isForceUpgrade() {
        return forceUpgrade;
    }

    public void setForceUpgrade(boolean forceUpgrade) {
        this.forceUpgrade = forceUpgrade;
    }

    public String getReleaseNote() {
        return releaseNote;
    }

    public void setReleaseNote(String releaseNote) {
        this.releaseNote = releaseNote;
    }
}

