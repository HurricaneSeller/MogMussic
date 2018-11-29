package com.example.moan.mogmussic.show.showsong;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import com.example.moan.mogmussic.R;

import androidx.annotation.Nullable;

public class ScanView extends View {
    private Paint mPaintPhone = new Paint();
    private Paint mPaintGrid = new Paint();
    private static final String TAG = "moanbigking";

    public ScanView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }


    private void initPaint() {
        mPaintPhone.setColor(getResources().getColor(R.color.colorOrange400));
        mPaintPhone.setStyle(Paint.Style.STROKE);
        mPaintPhone.setStrokeWidth(4f);
        mPaintGrid.setColor(getResources().getColor(R.color.colorOrange400));
        mPaintGrid.setStyle(Paint.Style.STROKE);
        mPaintGrid.setStrokeWidth(1f);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDrawForeground(canvas);
        Path path = new Path();
        drawLeft(canvas);
        drawRight(canvas);
        drawTop(canvas, path);
        drawBottom(canvas, path);
        canvas.drawRect(getWidth() / 4 + 50,
                getWidth() / 4 + 50,
                getWidth() * 3 / 4 - 50,
                getHeight() * 7 / 8 - 50,
                mPaintPhone);
        drawGrid(canvas);
        drawCircle(canvas);
        canvas.save();
    }

    private void drawCircle(Canvas canvas) {
        canvas.drawCircle(getWidth() / 2,
                getHeight() * 7 / 8 - 10,
                20,
                mPaintPhone);
        canvas.drawCircle(getWidth() / 2,
                getWidth() / 4 + 5 ,
                15,
                mPaintPhone);
        canvas.drawCircle(getWidth() / 4 + 100,
                getWidth() / 4 + 5 ,
                8,
                mPaintPhone);
    }

    private void drawGrid(Canvas canvas) {
        int width = (getWidth() * 3 / 4 - 100 - getWidth() / 4) / 17;
        int length = (getHeight() * 7 / 8 - 100 - getWidth() / 4) / 24;
        for (int i = 1; i <= 24; i++) {
            canvas.drawLine(getWidth() / 4 + 50,
                    getWidth() / 4 + 50 + length * i,
                    getWidth() * 3 / 4 - 50,
                    getWidth() / 4 + 50 + length * i,
                    mPaintGrid);
        }
        for (int i = 1; i <= 17; i++) {
            canvas.drawLine(getWidth() / 4 + 50 + width * i,
                    getWidth() / 4 + 50,
                    getWidth() / 4 + 50 + width * i,
                    getHeight() * 7 / 8 - 50,
                    mPaintGrid);
        }
    }

    private void drawLeft(Canvas canvas) {
        canvas.drawLine(getWidth() / 4,
                getWidth() / 4,
                getWidth() / 4,
                getWidth() / 4 + 130,
                mPaintPhone);
        canvas.drawLine(getWidth() / 4 - 4,
                getWidth() / 4 + 130,
                getWidth() / 4 - 4,
                getWidth() / 4 + 230,
                mPaintPhone);
        canvas.drawLine(getWidth() / 4,
                getWidth() / 4 + 230,
                getWidth() / 4,
                getHeight() * 7 / 8,
                mPaintPhone);

    }

    private void drawRight(Canvas canvas) {
        canvas.drawLine(getWidth() * 3 / 4,
                getWidth() / 4,
                getWidth() * 3 / 4,
                getWidth() / 4 + 50,
                mPaintPhone);
        canvas.drawLine(getWidth() * 3 / 4 + 4,
                getWidth() / 4 + 50,
                getWidth() * 3 / 4 + 4,
                getWidth() / 4 + 130,
                mPaintPhone);
        canvas.drawLine(getWidth() * 3 / 4,
                getWidth() / 4 + 130,
                getWidth() * 3 / 4,
                getHeight() * 7 / 8,
                mPaintPhone);
    }

    private void drawTop(Canvas canvas, Path path) {
        path.moveTo(getWidth() / 4,
                getWidth() / 4);
        path.quadTo(getWidth() / 4 + 10,
                getWidth() / 4 - 20,
                getWidth() / 3,
                getWidth() / 4 - 30);
        path.moveTo(getWidth() * 3 / 4,
                getWidth() / 4);
        path.quadTo(getWidth() * 3 / 4 - 10,
                getWidth() / 4 - 20,
                getWidth() * 2 / 3,
                getWidth() / 4 - 30);

        path.moveTo(getWidth() / 3,
                getWidth() / 4 - 30);
        path.quadTo(getWidth() / 2,
                getWidth() / 4 - 40,
                getWidth() * 2 / 3,
                getWidth() / 4 - 30);
        canvas.drawPath(path, mPaintPhone);
    }

    private void drawBottom(Canvas canvas, Path path) {
        path.moveTo(getWidth() / 4,
                getHeight() * 7 / 8);
        path.quadTo(getWidth() / 4 + 10,
                getHeight() * 7 / 8 + 20,
                getWidth() / 3,
                getHeight() * 7 / 8 + 30);

        path.moveTo(getWidth() * 3 / 4,
                getHeight() * 7 / 8);
        path.quadTo(getWidth() * 3 / 4,
                getHeight() * 7 / 8 + 20,
                getWidth() * 2 / 3,
                getHeight() * 7 / 8 + 30);

        path.moveTo(getWidth() / 3,
                getHeight() * 7 / 8 + 30);
        path.quadTo(getWidth() / 2,
                getHeight() * 7 / 8 + 40,
                getWidth() * 2 / 3,
                getHeight() * 7 / 8 + 30);
        canvas.drawPath(path, mPaintPhone);
    }
}
