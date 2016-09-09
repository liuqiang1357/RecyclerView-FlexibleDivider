package com.yqritc.recyclerviewflexibledivider;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.DimenRes;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by yqritc on 2015/01/15.
 */
public class VerticalDividerItemDecoration extends FlexibleDividerDecoration {

    private MarginProvider mMarginProvider;

    protected VerticalDividerItemDecoration(Builder builder) {
        super(builder);
        mMarginProvider = builder.mMarginProvider;
    }

    @Override
    protected Rect getDividerBound(int position, int count, boolean after, RecyclerView parent, View child) {
        Rect bounds = new Rect(0, 0, 0, 0);
        int transitionX = (int) ViewCompat.getTranslationX(child);
        int transitionY = (int) ViewCompat.getTranslationY(child);
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
        bounds.top = parent.getPaddingTop() +
                mMarginProvider.dividerTopMargin(position, count, after, parent) + transitionY;
        bounds.bottom = parent.getHeight() - parent.getPaddingBottom() -
                mMarginProvider.dividerBottomMargin(position, count, after, parent) + transitionY;

        int dividerSize = getDividerSize(position, count, after, parent);
        boolean isReverseLayout = isReverseLayout(parent);

        if (mDividerType == DividerType.DRAWABLE) {
            // set left and right position of divider
            if (isReverseLayout == after) {
                bounds.right = child.getLeft() - params.leftMargin + transitionX;
                bounds.left = bounds.right - dividerSize;
            } else {
                bounds.left = child.getRight() + params.rightMargin + transitionX;
                bounds.right = bounds.left + dividerSize;
            }
        } else {
            // set center point of divider
            int halfSize = dividerSize / 2;
            if (isReverseLayout == after) {
                bounds.left = child.getLeft() - params.leftMargin - halfSize + transitionX;
            } else {
                bounds.left = child.getRight() + params.rightMargin + halfSize + transitionX;
            }
            bounds.right = bounds.left;
        }

        if (mPositionInsideItem) {
            if (isReverseLayout) {
                bounds.left += dividerSize;
                bounds.right += dividerSize;
            } else {
                bounds.left -= dividerSize;
                bounds.right -= dividerSize;
            }
        }

        return bounds;
    }

    @Override
    protected void setItemOffsets(Rect outRect, int position, int count, RecyclerView parent) {

        boolean drawBefore = mVisibilityProvider.shouldDrawDivider(position, count, false, parent);
        boolean drawAfter = mVisibilityProvider.shouldDrawDivider(position, count, true, parent);

        if (mPositionInsideItem || !drawBefore && !drawAfter) {
            outRect.set(0, 0, 0, 0);
            return;
        }

        int beforeSize = drawBefore ? getDividerSize(position, count, false, parent) : 0;
        int AfterSize = drawAfter ? getDividerSize(position, count, true, parent) : 0;

        if (isReverseLayout(parent)) {
            outRect.set(AfterSize, 0, beforeSize, 0);
        } else {
            outRect.set(beforeSize, 0, AfterSize, 0);
        }
    }

    private int getDividerSize(int position, int count, boolean after, RecyclerView parent) {
        if (mSizeProvider != null) {
            return mSizeProvider.dividerSize(position, count, after, parent);
        } else if (mPaintProvider != null) {
            return (int) mPaintProvider.dividerPaint(position, count, after, parent).getStrokeWidth();
        } else if (mDrawableProvider != null) {
            Drawable drawable = mDrawableProvider.drawableProvider(position, count, after, parent);
            return drawable.getIntrinsicWidth();
        }
        throw new RuntimeException("failed to get size");
    }

    /**
     * Interface for controlling divider margin
     */
    public interface MarginProvider {

        /**
         * Returns top margin of divider.
         *
         * @param position Divider position (or group index for GridLayoutManager)
         * @param count
         * @param after
         * @param parent   RecyclerView   @return top margin
         */
        int dividerTopMargin(int position, int count, boolean after, RecyclerView parent);

        /**
         * Returns bottom margin of divider.
         *
         * @param position Divider position (or group index for GridLayoutManager)
         * @param count
         * @param after
         * @param parent   RecyclerView  @return bottom margin
         */
        int dividerBottomMargin(int position, int count, boolean after, RecyclerView parent);
    }

    public static class Builder extends FlexibleDividerDecoration.Builder<Builder> {

        private MarginProvider mMarginProvider = new MarginProvider() {
            @Override
            public int dividerTopMargin(int position, int count, boolean after, RecyclerView parent) {
                return 0;
            }

            @Override
            public int dividerBottomMargin(int position, int count, boolean after, RecyclerView parent) {
                return 0;
            }
        };

        public Builder(Context context) {
            super(context);
        }

        public Builder margin(final int topMargin, final int bottomMargin) {
            return marginProvider(new MarginProvider() {
                @Override
                public int dividerTopMargin(int position, int count, boolean after, RecyclerView parent) {
                    return topMargin;
                }

                @Override
                public int dividerBottomMargin(int position, int count, boolean after, RecyclerView parent) {
                    return bottomMargin;
                }
            });
        }

        public Builder margin(int verticalMargin) {
            return margin(verticalMargin, verticalMargin);
        }

        public Builder marginResId(@DimenRes int topMarginId, @DimenRes int bottomMarginId) {
            return margin(mResources.getDimensionPixelSize(topMarginId),
                    mResources.getDimensionPixelSize(bottomMarginId));
        }

        public Builder marginResId(@DimenRes int verticalMarginId) {
            return marginResId(verticalMarginId, verticalMarginId);
        }

        public Builder marginProvider(MarginProvider provider) {
            mMarginProvider = provider;
            return this;
        }

        public VerticalDividerItemDecoration build() {
            checkBuilderParams();
            return new VerticalDividerItemDecoration(this);
        }
    }
}