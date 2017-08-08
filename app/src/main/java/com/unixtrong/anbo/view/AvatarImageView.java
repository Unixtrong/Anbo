package com.unixtrong.anbo.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

/**
 * Author(s): danyun
 * Date: 2017/8/9
 */
public class AvatarImageView extends android.support.v7.widget.AppCompatImageView {
    private Paint mPaint;
    private Xfermode mXfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
    private Bitmap mSrcBitmap;
    private Bitmap mMaskBitmap;

    public AvatarImageView(Context context) {
        super(context);
        init();
    }

    public AvatarImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AvatarImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable drawable = getDrawable();
        Bitmap maskBitmap = getMaskBitmap();
        Bitmap bitmap = combineBitmap(drawable, maskBitmap);
        canvas.drawBitmap(bitmap, 0, 0, mPaint);
    }

    private Bitmap combineBitmap(Drawable drawable, Bitmap maskBitmap) {
        if (mSrcBitmap == null || mSrcBitmap.isRecycled()) {
            mSrcBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        }
        Canvas canvas = new Canvas(mSrcBitmap);
        drawable.setBounds(0, 0, getWidth(), getWidth());
        drawable.draw(canvas);

        mPaint.setXfermode(mXfermode);
        canvas.drawBitmap(maskBitmap, 0, 0, mPaint);
        mPaint.setXfermode(null);
        return mSrcBitmap;
    }

    private Bitmap getMaskBitmap() {
        if (mMaskBitmap == null || mMaskBitmap.isRecycled()) {
            mMaskBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(mMaskBitmap);
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(Color.BLACK);
            int cx = getWidth() / 2;
            canvas.drawCircle(cx, cx, cx, paint);
        }
        return mMaskBitmap;
    }
}
