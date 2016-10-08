package net.dearcode.candy.selfview;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import net.dearcode.candy.R;


/**
 * Created by piyel_000 on 2016/l3/19.
 */
public class FlexiableScrollView extends ScrollView {

    //private ActionBar mActionBar;
    private Drawable mDrawable;
    private View mHead;

    private ImageView header;
    private int originalHeight;
    private int drawableHeight;

    private View pluginLine;

    private TextView tvTitle;

    private boolean top;

    private View headMsg;

    public FlexiableScrollView(Context context, AttributeSet attrs, ImageView header) {
        super(context, attrs);
        this.header = header;
    }

    public FlexiableScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FlexiableScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public FlexiableScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setHeader(ImageView header) {
        this.header = header;
        drawableHeight = header.getHeight();
        originalHeight = header.getHeight();//header.getDrawable().getIntrinsicHeight();
    }

    public void setHeadMsg(View view) {
        this.headMsg = view;
    }



    public FlexiableScrollView(Context context) {
        super(context);
    }

    public FlexiableScrollView(Context context, ImageView header) {
        super(context);
        this.header = header;
        drawableHeight = header.getMeasuredHeight();
        originalHeight= header.getDrawable().getIntrinsicHeight();
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        int iAlphe = evaluateAlpha(t);
        if (null != mHead && null != mDrawable) {
//            mDrawable.setAlpha(iAlphe);
//            mHead.setBackgroundDrawable(mDrawable);

            tvTitle.setTextColor(Color.argb(iAlphe,0,0,0));

            if(iAlphe == 255) {
                top = true;
            }else {
                top = false;
            }
        }
//        if(null != headMsg) {
//            headMsg.setAlpha(iAlphe/255);
//        }
    }

    private int evaluateAlpha(int t) {
        if (t  + getContext().getResources().getDimensionPixelOffset(R.dimen.title_bar_padding_top_with_bar) > originalHeight) {
            //pluginLine.setVisibility(VISIBLE);
            return 255;
        }
        //pluginLine.setVisibility(GONE);
        return (int) (255 * t /(float) originalHeight);
    }

    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
        if (isTouchEvent && deltaY < 0 ) {
            header.getLayoutParams().height += Math.abs(deltaY/3.0f);
//            if (originalHeight + deltaY < drawableHeight) {
//                header.getLayoutParams().height += Math.abs(deltaY);
//            } else {
//                header.getLayoutParams().height = drawableHeight;
//            }
            header.requestLayout();
        }

        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (null==header)return super.onTouchEvent(ev);
        if (ev.getAction()==MotionEvent.ACTION_UP ||ev.getAction()==MotionEvent.ACTION_CANCEL) {
            ValueAnimator animator = ValueAnimator.ofInt(1);
            animator.setDuration(700);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    header.getLayoutParams().height -= animation.getAnimatedFraction() * (header.getLayoutParams().height - originalHeight);
                    header.requestLayout();
                }
            });
            animator.setInterpolator(new OvershootInterpolator());
            animator.start();

        }
        return super.onTouchEvent(ev);
    }

    public void bindActionBar(View supportActionBar, Drawable drawable) {
        mHead = supportActionBar;
        mDrawable = drawable;
        mDrawable.setAlpha(0);
        mHead.setBackgroundDrawable(mDrawable);

//        pluginLine = mHead.findViewById(R.id.ll_otherInfo);
//        tvTitle = (TextView) mHead.findViewById(R.id.tv_titleName);
    }

    public boolean isTop() {
        return top;
    }
}
