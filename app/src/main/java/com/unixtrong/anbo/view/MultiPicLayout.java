package com.unixtrong.anbo.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.unixtrong.anbo.R;

/**
 * Created by danyun on 2017/8/10
 */

public class MultiPicLayout extends LinearLayout {
    private ImageView[] mImageViews = new ImageView[9];
    private int mFillIndex = 0;

    public MultiPicLayout(Context context) {
        super(context);
        init();
    }

    public MultiPicLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MultiPicLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        Context context = getContext();
        int match = ViewGroup.LayoutParams.MATCH_PARENT;
        int wrap = ViewGroup.LayoutParams.WRAP_CONTENT;
        for (int i = 0; i < 3; i++) {
            LinearLayout rowLayout = new LinearLayout(context);
            rowLayout.setWeightSum(3F);
            for (int j = 0; j < 3; j++) {
                ImageView imageView = mImageViews[mFillIndex++] = new ImageView(context);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setImageResource(R.mipmap.ic_launcher);
                LayoutParams childParams = new LayoutParams(0, wrap);
                childParams.weight = 1;
                rowLayout.addView(imageView, childParams);
            }
            LayoutParams params = new LayoutParams(match, wrap);
            addView(rowLayout, params);
        }
    }

    public ImageView[] getImageViews() {
        return mImageViews;
    }
}
