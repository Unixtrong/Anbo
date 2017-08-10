package com.unixtrong.anbo.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.ViewGroup;

import com.unixtrong.anbo.tools.Lg;
import com.unixtrong.anbo.tools.Utils;

/**
 * Created by danyun on 2017/8/9
 */

public class SinglePicView extends android.support.v7.widget.AppCompatImageView {
    private int mScreenWidth;

    public SinglePicView(Context context) {
        super(context);
        init();
    }

    public SinglePicView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SinglePicView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mScreenWidth = Utils.getScreenSize(getContext()).widthPixels;
    }

    @Override
    public void setImageDrawable(@Nullable Drawable drawable) {
        if (drawable != null) {
            int width = drawable.getIntrinsicWidth();
            int height = drawable.getIntrinsicHeight();
            ViewGroup.LayoutParams params = getLayoutParams();
            if (params != null) {
                if (width > height) {
                    height = mScreenWidth * height / width;
                    width = ViewGroup.LayoutParams.MATCH_PARENT;
                    int minHeight = (int) (mScreenWidth * 0.2F);
                    if (height < minHeight) {
                        height = minHeight;
                    }
                } else if (width < height) {
                    int maxHeight = (int) (mScreenWidth * 0.6F);
                    if (height > maxHeight) {
                        height = maxHeight;
                        width = maxHeight * width / height;
                    }
                    int minWidth = (int) (mScreenWidth * 0.2F);
                    if (width < minWidth) {
                        width = minWidth;
                    }
                } else {
                    width = (int) (mScreenWidth * 0.6F);
                    height = (int) (mScreenWidth * 0.6F);
                }
                params.width = width;
                params.height = height;
                Lg.debug("%s -> params.width: %d, params.height: %d", hashCode(), width, params.height);
            }
        }
        super.setImageDrawable(drawable);
    }
}