package com.example.moan.mogmussic.music.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;

public class NeedleView extends androidx.appcompat.widget.AppCompatImageView {
    private static final String TAG = "moanbigking";
    private ObjectAnimator downAnimator;
    private ObjectAnimator upAnimator;

    public NeedleView(Context context) {
        super(context);
        init();
    }

    public NeedleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    public NeedleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    private void init() {
        downAnimator = ObjectAnimator.ofFloat(this,
                "rotation",
                0f,
                45f);
        downAnimator.setDuration(1000);
        upAnimator = ObjectAnimator.ofFloat(this,
                "rotation",
                45f,
                0f);
        upAnimator.setDuration(1000);
    }

    public void spinDown() {
        downAnimator.start();
    }
    public void spinUp() {
        upAnimator.start();
    }
    public void upThenDown() {
        upAnimator.start();
        upAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                downAnimator.start();
            }
        });
    }
}
