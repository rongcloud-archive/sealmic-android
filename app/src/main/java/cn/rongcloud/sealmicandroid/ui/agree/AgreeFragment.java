package cn.rongcloud.sealmicandroid.ui.agree;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import cn.rongcloud.sealmicandroid.R;
import cn.rongcloud.sealmicandroid.databinding.FragmentAgreeBinding;
import cn.rongcloud.sealmicandroid.manager.NavOptionsRouterManager;

/**
 * Created by fushangkai on 2021/03/09
 */
public class AgreeFragment extends Fragment {

    private FragmentAgreeBinding fragmentAgreeBinding;
    private WebView agreeWebview;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentAgreeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_agree, container, false);
        return fragmentAgreeBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadWhiteWebView();
        fragmentAgreeBinding.agreeTitle.getImgBack().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavOptionsRouterManager.getInstance().backUp(getView());
            }
        });
    }

    private void loadWhiteWebView() {
        if (agreeWebview == null) {
            agreeWebview = fragmentAgreeBinding.agreeWebview;
        }
//       agreeWebview.setVerticalScrollbarOverlay(true);
        agreeWebview.getSettings().setJavaScriptEnabled(true);
////        agreeWebview.getSettings().setUseWideViewPort(true);
////        agreeWebview.getSettings().setLoadWithOverviewMode(true);
        agreeWebview.getSettings().setBuiltInZoomControls(true);
        agreeWebview.getSettings().setDisplayZoomControls(false);
        if (Build.VERSION.SDK_INT >= 21) {
            agreeWebview.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        agreeWebview.getSettings().setAllowFileAccess(true);
        agreeWebview.getSettings().setAllowFileAccessFromFileURLs(true);
        agreeWebview.getSettings().setSupportZoom(true);
        agreeWebview.getSettings().setDatabaseEnabled(true);
        agreeWebview.getSettings().setDomStorageEnabled(true);
        agreeWebview.getSettings().setAppCacheEnabled(true);
        agreeWebview.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ONLY);

        agreeWebview.loadUrl("file:///android_asset/web/agreement_zh.html");
    }
}
