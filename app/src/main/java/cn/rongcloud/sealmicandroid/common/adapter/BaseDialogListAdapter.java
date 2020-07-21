package cn.rongcloud.sealmicandroid.common.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.constraintlayout.widget.ConstraintLayout;

/**
 * dialog列表适配器
 */
public abstract class BaseDialogListAdapter extends BaseAdapter {

    private Context mContext;
    private int mResource;
    private String[] mDatas;

    public BaseDialogListAdapter(Context context, int resource, String[] objects) {
        this.mContext = context;
        this.mResource = resource;
        this.mDatas = objects;
    }

    @Override
    public int getCount() {
        return mDatas.length;
    }

    @Override
    public Object getItem(int i) {
        return mDatas != null ? mDatas[i] : null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ConstraintLayout root = (ConstraintLayout) inflater.inflate(mResource, viewGroup, false);
        initView(i, root);
        return root;
    }

    /**
     * 初始化view方法，方便定制拓展
     *
     * @param i    position
     * @param view view
     */
    public abstract void initView(int i, View view);
}
