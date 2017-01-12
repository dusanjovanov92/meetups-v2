package com.dusanjovanov.meetups3.decorations;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by duca on 12/1/2017.
 */
public class HorizontalDividerItemDecoration extends RecyclerView.ItemDecoration {
    private Drawable divider;
    private boolean withHeader;

    public HorizontalDividerItemDecoration(Drawable divider, boolean withHeader) {
        this.divider = divider.mutate();
        this.withHeader = withHeader;
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();
        int childCount = parent.getChildCount();
        int i = 0;
        if (withHeader) {
            i = 1;
        }

        for (; i < childCount - 1; i++) {

            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams params =
                    (RecyclerView.LayoutParams) child.getLayoutParams();
            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + divider.getIntrinsicHeight();
            divider.setBounds(left, top, right, bottom);
            divider.draw(c);

        }
    }
}
