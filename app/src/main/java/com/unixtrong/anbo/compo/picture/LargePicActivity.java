package com.unixtrong.anbo.compo.picture;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.unixtrong.anbo.R;
import com.unixtrong.anbo.handler.PicLoader;
import com.unixtrong.anbo.tools.Utils;

public class LargePicActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, View.OnClickListener {

    private ViewPager mPager;
    private LinearLayout mIndicatorLayout;
    private TextView mProgressTextView;
    private String[] mPicUrls;
    private int mCurrentIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_large_pic);

        Intent intent = getIntent();
        mCurrentIndex = intent.getIntExtra("cur", 0);
        mPicUrls = intent.getStringArrayExtra("urls");
        initView();
    }

    private void initView() {
        mPager = (ViewPager) findViewById(R.id.vp_pic_large);
        mIndicatorLayout = (LinearLayout) findViewById(R.id.ll_pic_indicator);
        mProgressTextView = (TextView) findViewById(R.id.tv_pic_progress);

        mPager.setAdapter(new Adapter());
        mPager.addOnPageChangeListener(this);
        int index = 0;
        while (index++ < mPicUrls.length) {
            ImageView imageView = new ImageView(this);
            imageView.setImageResource(R.drawable.selector_pics_indicator);
            int wrap = LinearLayout.LayoutParams.WRAP_CONTENT;
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(wrap, wrap);
            layoutParams.setMarginStart(Utils.dip(this, 8));
            imageView.setEnabled(false);
            mIndicatorLayout.addView(imageView, layoutParams);
        }
        mPager.setCurrentItem(mCurrentIndex);
        mIndicatorLayout.getChildAt(mCurrentIndex).setEnabled(true);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        for (int i = 0; i < mPicUrls.length; i++) {
            mIndicatorLayout.getChildAt(i).setEnabled(position == i);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onClick(View v) {
        finish();
    }

    private class Adapter extends PagerAdapter {

        private DisplayMetrics mScreenSize = Utils.getScreenSize(LargePicActivity.this);
        private LayoutInflater mInflater = LayoutInflater.from(LargePicActivity.this);
        private ImageView[] mImageViews = new ImageView[mPicUrls.length];

        @Override
        public int getCount() {
            return mPicUrls.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            if (mImageViews[position] == null) {
                mImageViews[position] = (ImageView) mInflater.inflate(R.layout.layout_image_large, null);
                mImageViews[position].setOnClickListener(LargePicActivity.this);
            }
            ImageView imageView = mImageViews[position];
            int w = mScreenSize.widthPixels;
            int h = mScreenSize.heightPixels;
            String imageUrl = getImageUrl(position);
            PicLoader.with(LargePicActivity.this).bind(imageUrl, imageView, R.mipmap.ic_launcher, w, h);
            container.addView(imageView);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }

        private String getImageUrl(int position) {
            return mPicUrls[position];
        }
    }
}
