package io.rong.imkit.emoticon;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import io.rong.imkit.R;
import io.rong.imkit.utilities.RongUtils;

public class EmoticonTabAdapter {
    private View mContainer;
    private IEmoticonTab mCurrentTab;
    private ViewPager mViewPager;
    private TabPagerAdapter mAdapter;
    private ViewGroup mScrollTab;
    private int selected = 0;
    private View mTabAdd;
    private View mTabSetting;
    private boolean mTabBarEnabled = true;
    private boolean mInitialized;
    private boolean mAddEnabled = false;
    private boolean mSettingEnabled = false;
    private IEmoticonClickListener mEmoticonClickListener;
    private IEmoticonSettingClickListener mEmoticonSettingClickListener;
    private LinkedHashMap<String, List<IEmoticonTab>> mEmotionTabs;
    private View extraTabBarItem;

    public EmoticonTabAdapter() {
        mEmotionTabs = new LinkedHashMap<>();
    }

    public boolean isInitialized() {
        return mInitialized;
    }

    public void setOnEmoticonClickListener(IEmoticonClickListener listener) {
        mEmoticonClickListener = listener;
    }

    public void setOnEmoticonSettingClickListener(IEmoticonSettingClickListener listener) {
        mEmoticonSettingClickListener = listener;
    }

    public void setCurrentTab(IEmoticonTab tab, String tag) {
        if (mEmotionTabs.containsKey(tag)) {
            mCurrentTab = tab;
            if (mAdapter != null && mViewPager != null) {
                int index = getIndex(tab);
                if (index >= 0) {
                    mViewPager.setCurrentItem(index);
                    mCurrentTab = null;
                }
            }
        }
    }

    public void bindView(ViewGroup viewGroup) {
        mInitialized = true;
        mContainer = initView(viewGroup.getContext(), viewGroup);
    }

    public void initTabs(List<IEmoticonTab> tabs, String tag) {
        if (tabs != null) {
            mEmotionTabs.put(tag, tabs);
        }
    }

    public void refreshTabIcon(IEmoticonTab tab, Drawable drawable) {
        int index = getIndex(tab);
        if (index >= 0) {
            View child = mScrollTab.getChildAt(index);
            ImageView iv = child.findViewById(R.id.rc_emoticon_tab_iv);
            iv.setImageDrawable(drawable);
        }
    }

    public boolean addTab(int index, IEmoticonTab tab, String tag) {
        List<IEmoticonTab> tabs = mEmotionTabs.get(tag);
        if (tabs == null) {
            tabs = new ArrayList<>();
            tabs.add(tab);
            mEmotionTabs.put(tag, tabs);
        } else {
            int count = tabs.size();
            if (index <= count)
                tabs.add(index, tab);
            else
                return false;
        }
        int idx = getIndex(tab);
        if (mAdapter != null && mViewPager != null) {
            View view = getTabIcon(mViewPager.getContext(), tab);
            mScrollTab.addView(view, idx);
            mAdapter.notifyDataSetChanged();
            mViewPager.setCurrentItem(idx <= selected ? selected + 1 : selected);
        }
        return true;
    }

    public void addTab(IEmoticonTab tab, String tag) {
        List<IEmoticonTab> tabs = mEmotionTabs.get(tag);
        if (tabs == null) {
            tabs = new ArrayList<>();
            tabs.add(tab);
            mEmotionTabs.put(tag, tabs);
        } else {
            tabs.add(tab);
        }
        int idx = getIndex(tab);
        if (mAdapter != null && mViewPager != null) {
            View view = getTabIcon(mViewPager.getContext(), tab);
            mScrollTab.addView(view, idx);
            mAdapter.notifyDataSetChanged();
            mViewPager.setCurrentItem(idx <= selected ? selected + 1 : selected);
        }
    }

    public List<IEmoticonTab> getTagTabs(String tag) {
        return mEmotionTabs.get(tag);
    }

    public int getTagTabIndex(String tag) {
        Set<String> keys = mEmotionTabs.keySet();
        List<String> list = new ArrayList<>(keys);
        return list.indexOf(tag);
    }

    private int getIndex(IEmoticonTab tab) {
        return getAllTabs().indexOf(tab);
    }

    private List<IEmoticonTab> getAllTabs() {
        Collection<List<IEmoticonTab>> c = mEmotionTabs.values();
        List<IEmoticonTab> list = new ArrayList<>();
        for (List<IEmoticonTab> tabs : c) {
            for (int i = 0; tabs != null && i < tabs.size(); i++) {
                list.add(tabs.get(i));
            }
        }
        return list;
    }

    public LinkedHashMap<String, List<IEmoticonTab>> getTabList() {
        return mEmotionTabs;
    }

    private IEmoticonTab getTab(int index) {
        return getAllTabs().get(index);
    }

    public boolean removeTab(IEmoticonTab tab, String tag) {
        if (!mEmotionTabs.containsKey(tag)) return false;
        boolean result = false;
        List<IEmoticonTab> list = mEmotionTabs.get(tag);
        int index = getIndex(tab);
        if (list != null && list.remove(tab)) {
            mScrollTab.removeViewAt(index);
            mAdapter.notifyDataSetChanged();
            result = true;
            if (selected == index) {
                mViewPager.setCurrentItem(selected);
                onPageChanged(-1, selected);
            }
        }
        return result;
    }

    public void setVisibility(int visibility) {
        if (mContainer != null) {
            if (visibility == View.VISIBLE) {
                mContainer.setVisibility(View.VISIBLE);
            } else {
                mContainer.setVisibility(View.GONE);
            }
        }
    }

    public int getVisibility() {
        return mContainer != null ? mContainer.getVisibility() : View.GONE;
    }

    public void setTabViewEnable(boolean enable) {
        mTabBarEnabled = enable;
    }

    public void setAddEnable(boolean enable) {
        mAddEnabled = enable;
        if (mTabAdd != null) {
            mTabAdd.setVisibility(enable ? View.VISIBLE : View.GONE);
        }
    }

    public void setSettingEnable(boolean enable) {
        mSettingEnabled = enable;
        if (mTabSetting != null) {
            mTabSetting.setVisibility(enable ? View.VISIBLE : View.GONE);
        }
    }

    public void addExtraTab(Context context, Drawable drawable, View.OnClickListener clickListener) {
        extraTabBarItem = getTabIcon(context, drawable);
        extraTabBarItem.setOnClickListener(clickListener);
    }

    private View initView(Context context, ViewGroup parent) {
        View container = LayoutInflater.from(context).inflate(R.layout.rc_ext_emoticon_tab_container, null);
        int height = (int) context.getResources().getDimension(R.dimen.rc_extension_board_height);
        container.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height));
        mViewPager = container.findViewById(R.id.rc_view_pager);
        mScrollTab = container.findViewById(R.id.rc_emotion_scroll_tab);
        mTabAdd = container.findViewById(R.id.rc_emoticon_tab_add);
        mTabAdd.setVisibility(mAddEnabled ? View.VISIBLE : View.GONE);
        mTabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEmoticonClickListener != null) {
                    mEmoticonClickListener.onAddClick(v);
                }
            }
        });
        mTabSetting = container.findViewById(R.id.rc_emoticon_tab_setting);
        mTabSetting.setVisibility(mSettingEnabled ? View.VISIBLE : View.GONE);
        mTabSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEmoticonSettingClickListener != null) {
                    mEmoticonSettingClickListener.onSettingClick(v);
                }
            }
        });
        LinearLayout tabBar = container.findViewById(R.id.rc_emotion_tab_bar);
        if (mTabBarEnabled) {
            tabBar.setVisibility(View.VISIBLE);
            if (extraTabBarItem != null && mAddEnabled) {
                tabBar.addView(extraTabBarItem, 1);
            }
        } else {
            tabBar.setVisibility(View.GONE);
        }

        for (IEmoticonTab tab : getAllTabs()) {
            View view = getTabIcon(context, tab);
            mScrollTab.addView(view);
        }
        mAdapter = new TabPagerAdapter();
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(6);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                onPageChanged(selected, position);
                selected = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        int index;
        if (mCurrentTab != null && (index = getIndex(mCurrentTab)) >= 0) {
            mCurrentTab = null;
            onPageChanged(-1, index);
            mViewPager.setCurrentItem(index);
        } else {
            onPageChanged(-1, 0);
        }
        parent.addView(container);
        return container;
    }

    private View getTabIcon(Context context, IEmoticonTab tab) {
        Drawable drawable = tab.obtainTabDrawable(context);
        return getTabIcon(context, drawable);
    }

    private View getTabIcon(Context context, Drawable drawable) {
        View item = LayoutInflater.from(context).inflate(R.layout.rc_ext_emoticon_tab_item, null);
        item.setLayoutParams(new RelativeLayout.LayoutParams(RongUtils.dip2px(60), RongUtils.dip2px(36)));
        ImageView iv = item.findViewById(R.id.rc_emoticon_tab_iv);
        iv.setImageDrawable(drawable);
        item.setOnClickListener(tabClickListener);
        return item;
    }

    private void onPageChanged(int pre, int cur) {
        int count = mScrollTab.getChildCount();
        if (count > 0 && cur < count) {
            if (pre >= 0 && pre < count) {
                ViewGroup preView = (ViewGroup) mScrollTab.getChildAt(pre);
                preView.setBackgroundColor(Color.TRANSPARENT);
            }
            if (cur >= 0) {
                ViewGroup curView = (ViewGroup) mScrollTab.getChildAt(cur);
                curView.setBackgroundColor(curView.getContext().getResources().getColor(R.color.rc_EmoticonTab_bg_select_color));
                int w = curView.getMeasuredWidth();
                if (w != 0) {
                    int screenW = RongUtils.getScreenWidth();
                    if (mAddEnabled) {
                        int addW = mTabAdd.getMeasuredWidth();
                        screenW = screenW - addW;
                    }
                    HorizontalScrollView scrollView = (HorizontalScrollView) mScrollTab.getParent();
                    int scrollX = scrollView.getScrollX();
                    int offset = scrollX - (scrollX / w) * w;
                    if (cur * w < scrollX) {
                        scrollView.smoothScrollBy(offset == 0 ? -w : -offset, 0);
                    } else if (cur * w - scrollX > screenW - w) {
                        scrollView.smoothScrollBy(w - offset, 0);
                    }
                }
            }
        }
        if (cur >= 0 && cur < count) {
            IEmoticonTab curTab = getTab(cur);
            if (curTab != null) curTab.onTableSelected(cur);
        }
    }


    private View.OnClickListener tabClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int count = mScrollTab.getChildCount();
            if (count > 0) {
                for (int i = 0; i < count; i++) {
                    if (v.equals(mScrollTab.getChildAt(i))) {
                        mViewPager.setCurrentItem(i);
                        break;
                    }
                }
            }
        }
    };

    private class TabPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return getAllTabs().size();
        }

        @NonNull
        @Override
        public View instantiateItem(ViewGroup container, int position) {
            IEmoticonTab tab = getTab(position);
            View view = tab.obtainTabPager(container.getContext());
            if (view.getParent() == null) {
                container.addView(view);
            }
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, @NonNull Object object) {
            View layout = (View) object;
            container.removeView(layout);
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return POSITION_NONE;
        }
    }
}
