package io.rong.imkit.emoticon;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import io.rong.imkit.R;
import io.rong.imkit.utilities.ExtensionHistoryUtil;
import io.rong.imlib.RongIMClient;

public class EmojiTab implements IEmoticonTab {

    private LayoutInflater mLayoutInflater;
    private LinearLayout mIndicator;
    private int selected = 0;

    private String mUserId;

    private IEmojiItemClickListener mOnItemClickListener;

    private int mEmojiCountPerPage;

    public void setOnItemClickListener(IEmojiItemClickListener clickListener) {
        mOnItemClickListener = clickListener;
    }

    @Override
    public Drawable obtainTabDrawable(Context context) {
        return context.getResources().getDrawable(R.drawable.rc_tab_emoji);
    }

    @Override
    public View obtainTabPager(Context context) {
        mUserId = RongIMClient.getInstance().getCurrentUserId();
        return initView(context);
    }

    @Override
    public void onTableSelected(int position) {

    }

    private View initView(final Context context) {
        int count = AndroidEmoji.getEmojiSize();

        try {
            mEmojiCountPerPage = context.getResources().getInteger(context.getResources().getIdentifier("rc_extension_emoji_count_per_page", "integer", context.getPackageName()));
        } catch (Exception e) {
            mEmojiCountPerPage = 20;
        }

        int pages = count / (mEmojiCountPerPage) + ((count % mEmojiCountPerPage) != 0 ? 1 : 0);

        View view = LayoutInflater.from(context).inflate(R.layout.rc_ext_emoji_pager, null);
        ViewPager viewPager = view.findViewById(R.id.rc_view_pager);
        this.mIndicator = view.findViewById(R.id.rc_indicator);
        mLayoutInflater = LayoutInflater.from(context);

        viewPager.setAdapter(new EmojiPagerAdapter(pages));
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                ExtensionHistoryUtil.setEmojiPosition(context, mUserId, position);
                onIndicatorChanged(selected, position);
                selected = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPager.setOffscreenPageLimit(1);

        initIndicator(pages, mIndicator);
        int position = ExtensionHistoryUtil.getEmojiPosition(context, mUserId);
        viewPager.setCurrentItem(position);
        onIndicatorChanged(-1, position);
        return view;
    }

    private class EmojiPagerAdapter extends PagerAdapter {
        int count;

        public EmojiPagerAdapter(int count) {
            super();
            this.count = count;
        }

        @NonNull
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            GridView gridView = (GridView) mLayoutInflater.inflate(R.layout.rc_ext_emoji_grid_view, null);
            gridView.setAdapter(new EmojiAdapter(position * mEmojiCountPerPage, AndroidEmoji.getEmojiSize()));
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (mOnItemClickListener != null) {
                        int index = position + selected * mEmojiCountPerPage;
                        if (position == mEmojiCountPerPage) {
                            mOnItemClickListener.onDeleteClick();
                        } else {
                            if (index >= AndroidEmoji.getEmojiSize()) {
                                mOnItemClickListener.onDeleteClick();
                            } else {
                                int code = AndroidEmoji.getEmojiCode(index);
                                char[] chars = Character.toChars(code);
                                StringBuilder key = new StringBuilder(Character.toString(chars[0]));
                                for (int i = 1; i < chars.length; i++) {
                                    key.append(chars[i]);
                                }
                                mOnItemClickListener.onEmojiClick(key.toString());
                            }
                        }
                    }
                }
            });
            container.addView(gridView);
            return gridView;
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            return count;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, @NonNull Object object) {
            View layout = (View) object;
            container.removeView(layout);
        }
    }


    private class EmojiAdapter extends BaseAdapter {
        int count;
        int index;

        public EmojiAdapter(int index, int count) {
            this.count = Math.min(mEmojiCountPerPage, count - index);
            this.index = index;
        }

        @Override
        public int getCount() {
            return count + 1;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = mLayoutInflater.inflate(R.layout.rc_ext_emoji_item, null);
                viewHolder.emojiIV = convertView.findViewById(R.id.rc_ext_emoji_item);
                convertView.setTag(viewHolder);
            }
            viewHolder = (ViewHolder) convertView.getTag();
            if (position == mEmojiCountPerPage || position + index == AndroidEmoji.getEmojiSize()) {
                viewHolder.emojiIV.setImageResource(R.drawable.rc_icon_emoji_delete);
            } else {
                viewHolder.emojiIV.setImageDrawable(AndroidEmoji.getEmojiDrawable(parent.getContext(), index + position));
            }

            return convertView;
        }
    }

    private void initIndicator(int pages, LinearLayout indicator) {
        for (int i = 0; i < pages; i++) {
            ImageView imageView = (ImageView) mLayoutInflater.inflate(R.layout.rc_ext_indicator, null);
            imageView.setImageResource(R.drawable.rc_ext_indicator);
            indicator.addView(imageView);
        }
    }

    private void onIndicatorChanged(int pre, int cur) {
        int count = mIndicator.getChildCount();
        if (count > 0 && pre < count && cur < count) {
            if (pre >= 0) {
                ImageView preView = (ImageView) mIndicator.getChildAt(pre);
                preView.setImageResource(R.drawable.rc_ext_indicator);
            }
            if (cur >= 0) {
                ImageView curView = (ImageView) mIndicator.getChildAt(cur);
                curView.setImageResource(R.drawable.rc_ext_indicator_hover);
            }
        }
    }

    private class ViewHolder {
        ImageView emojiIV;
    }
}
