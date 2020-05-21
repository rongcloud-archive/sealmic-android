package cn.rongcloud.sealmicandroid.model.viewmodel;

import android.view.View;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.navigation.Navigation;

import cn.rongcloud.sealmicandroid.R;

public class MainViewModel extends ViewModel {

    private MutableLiveData<Integer> likeCount;

    public MainViewModel() {
        this.likeCount = new MutableLiveData<>();
        this.likeCount.setValue(0);
    }

    public MutableLiveData<Integer> getLikeCount() {
        if (likeCount == null) {
            this.likeCount = new MutableLiveData<>();
        }
        return likeCount;
    }

    public void gotoChatRoomFragment(View view) {
        this.likeCount.setValue(this.likeCount.getValue() == null ? 0 : this.likeCount.getValue() + 1);
        Navigation.findNavController(view).navigate(R.id.action_mainFragment_to_chatRoomFragment);
    }
}
