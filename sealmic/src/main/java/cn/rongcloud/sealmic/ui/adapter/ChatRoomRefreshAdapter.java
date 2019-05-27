package cn.rongcloud.sealmic.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.rongcloud.sealmic.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import cn.rongcloud.sealmic.model.BaseRoomInfo;

public class ChatRoomRefreshAdapter extends RecyclerView.Adapter<ChatRoomRefreshAdapter.ChatRoomRefreshHolder> {
    private static final String TAG = "ChatRoomRefreshAdapter";
    private Context mContext;
    private List<BaseRoomInfo> mList = new ArrayList<>();
    private OnItemClickListener mOnItemClickListener;

    public ChatRoomRefreshAdapter(Context context) {
        this.mContext = context;
    }


    public void setLoadDataList(List<BaseRoomInfo> list) {
        mList = list;
        notifyDataSetChanged();
    }

    public BaseRoomInfo getItem(int position) {
        if (mList != null) {
            return mList.get(position);
        } else {
            return null;
        }
    }

    @NonNull
    @Override
    public ChatRoomRefreshHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.chatroom_refresh_adapter_item, viewGroup, false);
        return new ChatRoomRefreshHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatRoomRefreshHolder chatRoomRefreshHolder, int i) {
        chatRoomRefreshHolder.imageViewRandom.setImageDrawable(mContext.getResources().getDrawable(mList.get(i).getRoomCoverImageId()));
        chatRoomRefreshHolder.textViewNumber.setText(mContext.getString(R.string.people_count_format, mList.get(i).getMemCount()));
        chatRoomRefreshHolder.textViewSubject.setText(mList.get(i).getSubject());
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    public class ChatRoomRefreshHolder extends RecyclerView.ViewHolder {
        RelativeLayout roomLayout;
        ImageView imageViewRandom;
        TextView textViewNumber;
        TextView textViewSubject;

        public ChatRoomRefreshHolder(@NonNull View itemView) {
            super(itemView);
            roomLayout = (RelativeLayout) itemView.findViewById(R.id.room_layout);
            imageViewRandom = (ImageView) itemView.findViewById(R.id.random_iv);
            textViewNumber = (TextView) itemView.findViewById(R.id.rc_item_number);
            textViewSubject = (TextView) itemView.findViewById(R.id.rv_item_subject);
            roomLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getLayoutPosition();
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(view, pos);
                    }
                }
            });
        }
    }


    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickLitener) {
        this.mOnItemClickListener = mOnItemClickLitener;
    }
}
