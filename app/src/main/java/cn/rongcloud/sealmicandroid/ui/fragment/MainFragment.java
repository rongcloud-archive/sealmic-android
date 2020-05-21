package cn.rongcloud.sealmicandroid.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import cn.rongcloud.sealmicandroid.R;
import cn.rongcloud.sealmicandroid.databinding.MainFragmentBinding;
import cn.rongcloud.sealmicandroid.lifecycle.MainObserver;
import cn.rongcloud.sealmicandroid.model.viewmodel.MainViewModel;

/**
 * 主界面
 *
 * @author yangyi
 */
public class MainFragment extends Fragment {

    private MainViewModel mainViewModel;
    private MainFragmentBinding mainFragmentBinding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLifecycle().addObserver(new MainObserver(MainFragment.class.getSimpleName()));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        mainFragmentBinding =
                DataBindingUtil.inflate(inflater, R.layout.main_fragment, container, false);
        mainFragmentBinding.setMain(mainViewModel);
        mainFragmentBinding.setLifecycleOwner(this);
        return mainFragmentBinding.getRoot();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //TODO
    }

}
