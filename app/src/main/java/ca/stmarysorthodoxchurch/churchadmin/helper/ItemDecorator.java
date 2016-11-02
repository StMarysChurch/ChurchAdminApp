package ca.stmarysorthodoxchurch.churchadmin.helper;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ItemDecoration;
import android.util.Log;
import android.view.View;

/**
 * Created by roneythomas on 2016-11-02.
 */

public class ItemDecorator extends ItemDecoration {
    private static final String TAG = "ItemDecorator";
    float margin;

    public ItemDecorator(float v) {
        margin = v;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        Log.d(TAG, "getItemOffsets: " + parent.getAdapter().getItemCount());
        if (parent.getChildCount() < parent.getAdapter().getItemCount()) {
            Log.d(TAG, "getItemOffsets: done");
            outRect.bottom = (int) margin;
        }
    }
}
