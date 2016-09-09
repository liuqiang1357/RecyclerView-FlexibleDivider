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
public class HorizontalDividerItemDecoration extends FlexibleDividerDecoration {

    private MarginProvider mMarginProvider;

    protected HorizontalDividerItemDecoration(Builder builder) {
        super(builder);
        mMarginProvider = builder.mMarginProvider;
    }

    @Override
    protected Rect getDividerBound(int position, int count, boolean after, RecyclerView parent, View child) {
        Rect bounds = new Rect(0, 0, 0, 0);
        int transitionX = (int) ViewCompat.getTranslationX(child);
        int transitionY = (int) ViewCompat.getTranslationY(child);
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
        bounds.left = parent.getPaddingLeft() +
                mMarginProvider.dividerLeftMargin(position, count, after, parent) + transitionX;
        bounds.right = parent.getWidth() - parent.getPaddingRight() -
                mMarginProvider.dividerRightMargin(position, count, after, parent) + transitionX;

        int dividerSize = getDividerSize(position, count, after, parent);
        boolean isReverseLayout = isReverseLayout(parent);

        if (mDividerType == DividerType.DRAWABLE) {
            // set top and bottom position of divider
            if (isReverseLayout == after) {
                bounds.bottom = child.getTop() - params.topMargin + transitionY;
                bounds.top = bounds.bottom - dividerSize;
            } else {
                bounds.top = child.getBottom() + params.bottomMargin + transitionY;
                bounds.bottom = bounds.top + dividerSize;
            }
        } else {
            // set center point of divider
            int halfSize = dividerSize / 2;
            if (isReverseLayout == after) {
                bounds.top = child.getTop() - params.topMargin - halfSize + transitionY;
            } else {
                bounds.top = child.getBottom() + params.bottomMargin + halfSize + transitionY;
            }
            bounds.bottom = bounds.top;
        }

        if (mPositionInsideItem) {
            if (isReverseLayout) {
                bounds.top += dividerSize;
                bounds.bottom += dividerSize;
            } else {
                bounds.top -= dividerSize;
                bounds.bottom -= dividerSize;
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
            outRect.set(0, AfterSize, 0, beforeSize);
        } else {
            outRect.set(0, beforeSize, 0, AfterSize);
        }
    }

    private int getDividerSize(int position, int count, boolean after, RecyclerView parent) {
        if (mSizeProvider != null) {
            return mSizeProvider.dividerSize(position, count, after, parent);
        } else if (mPaintProvider != null) {
            return (int) mPaintProvider.dividerPaint(position, count, after, parent).getStrokeWidth();
        } else if (mDrawableProvider != null) {
            Drawable drawable = mDrawableProvider.drawableProvider(position, count, after, parent);
            return drawable.getIntrinsicHeight();
        }
        throw new RuntimeException("failed to get size");
    }

    /**
     * Interface for controlling divider margin
     */
    public interface MarginProvider {

        /**
         * Returns left margin of divider.
         *
         * @param position Divider position (or group index for GridLayoutManager)
         * @param count
         * @param after
         * @param parent   RecyclerView  @return left margin
         */
        int dividerLeftMargin(int position, int count, boolean after, RecyclerView parent);

        /**
         * Returns right margin of divider.
         *
         * @param position Divider position (or group index for GridLayoutManager)
         * @param count
         * @param after
         * @param parent   RecyclerView  @return right margin
         */
        int dividerRightMargin(int position, int count, boolean after, RecyclerView parent);
    }

    public static class Builder extends FlexibleDividerDecoration.Builder<Builder> {

        private MarginProvider mMarginProvider = new MarginProvider() {
            @Override
            public int dividerLeftMargin(int position, int count, boolean after, RecyclerView parent) {
                return 0;
            }

            @Override
            public int dividerRightMargin(int position, int count, boolean after, RecyclerView parent) {
                return 0;
            }
        };

        public Builder(Context context) {
            super(context);
        }

        public Builder margin(final int leftMargin, final int rightMargin) {
            return marginProvider(new MarginProvider() {
                @Override
                public int dividerLeftMargin(int position, int count, boolean after, RecyclerView parent) {
                    return leftMargin;
                }

                @Override
                public int dividerRightMargin(int position, int count, boolean after, RecyclerView parent) {
                    return rightMargin;
                }
            });
        }

        public Builder margin(int horizontalMargin) {
            return margin(horizontalMargin, horizontalMargin);
        }

        public Builder marginResId(@DimenRes int leftMarginId, @DimenRes int rightMarginId) {
            return margin(mResources.getDimensionPixelSize(leftMarginId),
                    mResources.getDimensionPixelSize(rightMarginId));
        }

        public Builder marginResId(@DimenRes int horizontalMarginId) {
            return marginResId(horizontalMarginId, horizontalMarginId);
        }

        public Builder marginProvider(MarginProvider provider) {
            mMarginProvider = provider;
            return this;
        }

        public HorizontalDividerItemDecoration build() {
            checkBuilderParams();
            return new HorizontalDividerItemDecoration(this);
        }
    }
}