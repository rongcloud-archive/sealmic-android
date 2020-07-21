package cn.rongcloud.sealmicandroid.common.divider;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * grid网格列表下均分分布item
 */
public class GridItemDecoration extends RecyclerView.ItemDecoration {

    private int column;
    private int space;

    public GridItemDecoration(int column, int space) {
        this.column = column;
        this.space = space;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int position = parent.getChildAdapterPosition(view);
        // 每列分配的间隙大小，包括左间隙和右间隙
        int colPadding = space * (column + 1) / column;
        // 列索引
        double colIndex = position % column;
        // 列左、右空隙。右间隙=space-左间隙
        outRect.left = (int) (space * (colIndex + 1) - colPadding * colIndex);
        outRect.right = (int) (colPadding * (colIndex + 1) - space * (colIndex + 1));
        // 行间距
        if (position >= column) {
            outRect.top = space;
        }
    }
}
