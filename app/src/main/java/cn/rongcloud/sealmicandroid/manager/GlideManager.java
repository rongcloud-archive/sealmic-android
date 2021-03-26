package cn.rongcloud.sealmicandroid.manager;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.util.concurrent.ExecutionException;

import cn.rongcloud.sealmicandroid.R;
import cn.rongcloud.sealmicandroid.common.SealMicResultCallback;

/**
 * Glide加载图片管理类，主要是封装一些通用的加载图片的方法
 */
public class GlideManager {

    private GlideManager() {
    }

    private static class GlideManagerHelper {
        private static final GlideManager INSTANCE = new GlideManager();
    }

    public static GlideManager getInstance() {
        return GlideManagerHelper.INSTANCE;
    }

    /**
     * 设置圆角bitmap
     */
    public void setRadiusImage(View view, int roundingRadius, String imgUrl, ImageView imageView) {
        Glide.with(view)
                .load(imgUrl)
                .centerCrop()
                .placeholder(R.drawable.placeholder_room_theme)
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(roundingRadius)))
                .into(imageView);
    }

    /**
     * 设置bitmap
     */
    public void setBitmap(final View view, final int width, final int height, final String imgUrl, final SealMicResultCallback<Bitmap> callback) {
        ThreadManager.getInstance().runOnUIThread(new Runnable() {
            @Override
            public void run() {
                Bitmap resource = null;
                try {
                    resource = Glide.with(view).asBitmap().load(imgUrl).submit(width, height).get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                final Bitmap finalResource = resource;
                ThreadManager.getInstance().runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        callback.onSuccess(finalResource);
                    }
                });
            }
        });
    }

    /**
     * 设置url
     */
    public void setUrlImage(final View view, final String url, final ImageView imageView) {
        ThreadManager.getInstance().runOnUIThread(new Runnable() {
            @Override
            public void run() {
                imageView.post(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(view).load(url).centerCrop().placeholder(R.drawable.placeholder_room_theme).into(imageView);

                    }
                });
            }
        });
    }
}
