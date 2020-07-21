package cn.rongcloud.sealmicandroid.common.adapter;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import cn.rongcloud.sealmicandroid.R;
import cn.rongcloud.sealmicandroid.common.listener.OnClickGiftListener;
import cn.rongcloud.sealmicandroid.databinding.ItemGiftBinding;

/**
 * 礼物dialog adapter
 */
public class GiftDialogAdapter extends RecyclerView.Adapter<GiftDialogAdapter.GiftDialogViewHolder> {

    private List<Drawable> drawables;
    private List<String> strings;
    private int currentPosition = 10;
    private click click;
    protected List<ImageView> imageViews = new ArrayList<>();

    private OnClickGiftListener onClickGifListener;

    public void setOnClickGifListener(OnClickGiftListener onClickGifListener) {
        this.onClickGifListener = onClickGifListener;
    }

    public void setDrawables(List<Drawable> drawables, List<String> strings) {
        this.drawables = drawables;
        this.strings = strings;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public GiftDialogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemGiftBinding itemGiftBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_gift,
                parent,
                false);
        return new GiftDialogViewHolder(itemGiftBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull GiftDialogViewHolder holder, int position) {
        holder.bind(position);
        holder.itemGiftBinding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return drawables == null ? 0 : drawables.size();
    }

    class GiftDialogViewHolder extends RecyclerView.ViewHolder {

        private ItemGiftBinding itemGiftBinding;
        private int position;

        public GiftDialogViewHolder(@NonNull final ItemGiftBinding itemGiftBinding) {
            super(itemGiftBinding.getRoot());
            this.itemGiftBinding = itemGiftBinding;
            itemGiftBinding.ivGift.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickGifListener.onClickGift(v, drawables.get(position), position);
                    if (currentPosition != 10) {
                        imageViews.get(currentPosition).setVisibility(View.GONE);
                    }
                    itemGiftBinding.ivBg.setVisibility(View.VISIBLE);
                    currentPosition = position;
                }
            });
        }

        void bind(int position) {
            this.position = position;
            itemGiftBinding.ivGift.setImageDrawable(drawables.get(position));
            itemGiftBinding.ivGiftName.setText(strings.get(position));
            imageViews.add(itemGiftBinding.ivBg);
        }
    }

    public interface click {
        void onClick(int position);
    }

    public void setClick(GiftDialogAdapter.click click) {
        this.click = click;
    }
}
