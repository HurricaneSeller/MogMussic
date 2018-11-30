package com.example.moan.mogmussic.music.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.LinearInterpolator;

import de.hdodenhof.circleimageview.CircleImageView;


public class DiskView extends CircleImageView{
    private ObjectAnimator mObjectAnimator;

    public DiskView(Context context) {
        super(context);
        init();
    }

    public DiskView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DiskView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mObjectAnimator = ObjectAnimator.ofFloat(this,
                "rotation",
                0f,
                360f);
        mObjectAnimator.setDuration(100000);
        mObjectAnimator.setInterpolator(new LinearInterpolator());
        mObjectAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        mObjectAnimator.setRepeatMode(ObjectAnimator.REVERSE);
    }

    public void start() {
        mObjectAnimator.start();
    }

    public void pause() {
        mObjectAnimator.pause();
    }

    public void resume() {
        mObjectAnimator.resume();
    }
    public void stop() {
        mObjectAnimator.end();
    }
}
