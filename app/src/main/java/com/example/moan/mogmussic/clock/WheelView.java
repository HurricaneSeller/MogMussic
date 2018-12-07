package com.example.moan.mogmussic.clock;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class WheelView extends View {
    private List<String> mDataList;
    private Paint mPaint;
    private int mMaxTextSize = 80;
    private int mMinTextSize = 40;
    private final int mMaxTextAlpha = 255;
    private final int mMinTextAlpha = 120;
    private final int mPaddingY = 10;
    private int mMaxTextRow = 0;
    private float mMoveLen = 0;
    private IOnSelectListener mIOnSelectListener;
    private String TAG = "moanbigking";
    private int width;
    private int height;


    public WheelView(Context context) {
        super(context);
        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextSize(mMaxTextSize);
    }

    public WheelView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void setOnclickListener(IOnSelectListener onclickListener) {
        this.mIOnSelectListener = onclickListener;
    }

    private void performSelect() {
        if (mIOnSelectListener != null) {
            mIOnSelectListener.onSelect(mDataList.get(mMaxTextRow));
        }
    }

    public void setData(@NonNull List<String> data) {
        this.mDataList = data;
        mMaxTextRow = mDataList.size() / 2;
        invalidate();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        if (mDataList == null) {
            return;
        }
        int rowY;
        final int rowX = width / 2;
        int rowNum;
        String highLightText = mDataList.get(mMaxTextRow);
        mPaint.setAlpha(mMaxTextAlpha);
        mPaint.setTextSize(mMaxTextSize);

        Paint.FontMetricsInt fontMetricsInt = mPaint.getFontMetricsInt();
        int maxRowY = height / 2 - (fontMetricsInt.bottom + fontMetricsInt.top) / 2;
        mPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(highLightText, rowX, maxRowY, mPaint);

        mPaint.setAlpha(mMinTextAlpha);
        mPaint.setTextSize(mMinTextSize);
        mPaint.setTextAlign(Paint.Align.CENTER);
        rowNum = mMaxTextRow - 1;
        rowY = mMinTextSize;
        if (rowNum >= 0) {
            String text = mDataList.get(rowNum);
            canvas.drawText(text, rowX, rowY, mPaint);
        }

        rowNum = mMaxTextRow + 1;
        rowY = height;
        if (rowNum < mDataList.size()) {
            String text = mDataList.get(rowNum);
            canvas.drawText(text, rowX, rowY, mPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mMoveLen = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float y = event.getY();
                float offset = y - mMoveLen;
                if (Math.abs(offset) < 10) {
                    return true;
                }
                int rowOffSet = Math.abs((int) offset / mMinTextSize);
                if (offset < 0) {
                    mMaxTextRow = mMaxTextRow + rowOffSet > mDataList.size() - 1 ? mDataList.size() - 1 : mMaxTextRow
                            + rowOffSet;
                } else if (offset > 0) {
                    mMaxTextRow = mMaxTextRow - rowOffSet < 0 ? 0 : mMaxTextRow - rowOffSet;
                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                performSelect();
                break;
            default:
                return super.onTouchEvent(event);
        }
        return true;
    }

    public void increaseRowNum() {
        mMaxTextRow = mMaxTextRow + 1 > mDataList.size() - 1 ? mDataList.size() - 1 : mMaxTextRow + 1;
        performSelect();
        invalidate();
    }

    public void decreaseRowNum() {
        mMaxTextRow = mMaxTextRow - 1 < 0 ? 0 : mMaxTextRow - 1;
        performSelect();
        invalidate();
    }

    public interface IOnSelectListener {
        void onSelect(String text);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        height = mMaxTextSize + mMinTextSize * 2 + mPaddingY * 2;
        width = View.MeasureSpec.getSize(widthMeasureSpec);
        setMeasuredDimension(width, height);
    }
}
