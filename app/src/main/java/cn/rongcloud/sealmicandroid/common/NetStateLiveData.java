package cn.rongcloud.sealmicandroid.common;

import androidx.lifecycle.MutableLiveData;

/**
 * 网络请求对应的响应状态的定制版LiveData
 */
public class NetStateLiveData<T> extends MutableLiveData<T> {

    private MutableLiveData<Integer> netStateMutableLiveData;

    public MutableLiveData<Integer> getNetStateMutableLiveData() {
        if (netStateMutableLiveData == null) {
            netStateMutableLiveData = new NetStateLiveData<>();
        }
        return netStateMutableLiveData;
    }

    public boolean isSuccess() {
        return getNetStateMutableLiveData() != null
                && getNetStateMutableLiveData().getValue() != null
                && getNetStateMutableLiveData().getValue() == 10000;
    }

    public void clearState() {
        netStateMutableLiveData.setValue(null);
    }

    private void checkNetStateMutableLiveData() {
        if (netStateMutableLiveData == null) {
            netStateMutableLiveData = new MutableLiveData<>();
        }
    }

    public void postValueAndSuccess(int state, T value) {
        super.postValue(value);
        checkNetStateMutableLiveData();
        if (netStateMutableLiveData == null) {
            netStateMutableLiveData = new MutableLiveData<>();
        }
        netStateMutableLiveData.postValue(state);
    }

    public void postSuccess(int state) {
        checkNetStateMutableLiveData();
        if (netStateMutableLiveData == null) {
            netStateMutableLiveData = new MutableLiveData<>();
        }
        netStateMutableLiveData.postValue(state);
    }

    public void postError(int state) {
        checkNetStateMutableLiveData();
        if (netStateMutableLiveData == null) {
            netStateMutableLiveData = new MutableLiveData<>();
        }
        netStateMutableLiveData.postValue(state);
    }

}
