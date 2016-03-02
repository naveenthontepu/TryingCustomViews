package com.thontepu.naveen.tryingcustomviews.Views;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.thontepu.naveen.tryingcustomviews.R;

public class BottomSheet extends RelativeLayout {
    public BottomSheet(Context context) {
        super(context);
    }

    int hidden,shown;
    int hsize,ssize;
    String TAG = "com.thontepu.naveen";
    float ry,my,dy,uy,dby=0;
    View shownView;
    View hiddenView;
    int inHeight,finalHeight,height;
    boolean setinheight = true;
    int animationDuration;


    public BottomSheet(Context context, AttributeSet attrs) {
        super(context, attrs);
        inti(context, attrs);
    }

    public BottomSheet(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BottomSheet(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        inti(context, attrs);
    }

    private void inti(Context context,AttributeSet attrs){
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,R.styleable.BottomSheet,0,0);
        try {
            hidden = a.getResourceId(R.styleable.BottomSheet_hiddenElement,0);
            shown = a.getResourceId(R.styleable.BottomSheet_showingElement,0);
            hsize = (int)a.getDimension(R.styleable.BottomSheet_hiddenElementSize, (float) 1.0);
            ssize = (int) a.getDimension(R.styleable.BottomSheet_shownElementSize,(float)1.0);
            animationDuration = a.getInt(R.styleable.BottomSheet_animationDuration,300);
            Log.i(TAG,"hidden = "+hidden);
            Log.i(TAG,"shown = "+shown);
            Log.i(TAG,"ssize = "+ssize);
            Log.i(TAG,"hsize = "+hsize);
            Log.i(TAG,"animationDuration = "+animationDuration);
            LayoutInflater inflater =(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            shownView = inflater.inflate(shown,null);
            shownView.setId(shown);
            hiddenView = inflater.inflate(hidden,null);
            hiddenView.setId(hidden);
            addView(shownView,getshownLayoutParams());
            addView(hiddenView,getHiddenLayoutParams(shownView.getId()));
            height = ssize;
            inHeight = ssize;
            finalHeight = ssize+hsize;
        }finally {
            a.recycle();
        }
    }

    private LayoutParams getshownLayoutParams(){
        LayoutParams layoutParams;
        layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,  ssize);
        layoutParams.addRule(ALIGN_PARENT_TOP);
        return layoutParams;
    }

    private LayoutParams getHiddenLayoutParams(int shownId){
        LayoutParams layoutParams;
        layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, hsize);
        layoutParams.addRule(BELOW,shownId);
        return layoutParams;
    }

    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params) {
//        Log.i(TAG,"ssize1 =  "+ssize);
        params.height = height;
        super.setLayoutParams(params);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        Log.i(TAG,"onTouchEvent");
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                setinheight = false;
                ry = event.getY();
                Log.i(TAG, "ACTION DOWN");
                return true;
            case MotionEvent.ACTION_MOVE:
                Log.i(TAG,"ACTION_MOVE");
                my = event.getY();
                dy = my - ry;
                height = height - (int) dy;
                if (height <= finalHeight && height >= inHeight) {
                    Log.i(TAG, "dy = " + dy + "(int)dy = " + (int) dy + " my=" + my + " y=" + ry);
                    my = ry;
                    dby = dy;
                    changeLayout(height);
                } else {
                    if (height > finalHeight) {
                        height = finalHeight;
                    } else if (height < inHeight) {
                        height = inHeight;
                    }
                }
                return true;
            case MotionEvent.ACTION_UP:
                Log.i(TAG,"ACTION_UP");
                uy = event.getY();
                dby = 0;
                if (ry != uy) {
                    if (dy < 0) {
                        animatelayout(height, finalHeight);
                        return true;
                    } else if (dy > 0) {
                        animatelayout(height, inHeight);
                        return true;
                    }
                }
                return true;
        }
        return super.onTouchEvent(event);
    }
    public void animatelayout(int initialHeight, int fheight){
        ValueAnimator animator = ValueAnimator.ofInt(initialHeight, fheight);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                ViewGroup.LayoutParams lp = getLayoutParams();
                height = (Integer) animation.getAnimatedValue();
                setLayoutParams(lp);
            }
        });
        animator.setDuration(animationDuration);
        animator.start();
        height = fheight;
    }
    public void changeLayout(int height) {
        Log.i(TAG, "change layout height = " + height);
        if(height<=finalHeight&&height>=inHeight) {
            ViewGroup.LayoutParams rl = getLayoutParams();
            setLayoutParams(rl);
        }
    }
}
