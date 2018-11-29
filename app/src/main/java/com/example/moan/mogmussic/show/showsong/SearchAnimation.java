package com.example.moan.mogmussic.show.showsong;

import android.view.animation.Animation;
import android.view.animation.Transformation;

public class SearchAnimation extends Animation {
    private int radii;

    public SearchAnimation(int radii) {
        this.radii = radii;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        float d = 360 * interpolatedTime;
        d = (d >= 360) ? d - 360 : d;
        int[] ps = getNewPosition((int) d, radii);
        t.getMatrix().setTranslate(ps[0], ps[1] - radii);
    }

    private int[] getNewPosition(int newAngle, int r) {
        int newX, newY;
        newX = (int) (r * (Math.sin(newAngle * Math.PI / 180)));
        newY = (int) (r * (Math.cos(newAngle * Math.PI / 180)));
        return new int[]{newX, newY};
    }
}


