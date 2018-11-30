package com.example.moan.mogmussic.music.view;

import com.example.moan.mogmussic.music.LrcRow;

import java.util.List;

public interface ILrcView {
    /**
     * @param lrcRows all the lyrics to be shown
     */
    void setLrc(List<LrcRow> lrcRows);

    /**
     * @param time highlight the very lyrics to the given time
     */
    void seekLrcToTime(long time);

    /**
     * @param iLrcViewListener callback the method
     */
    void setListener(ILrcViewListener iLrcViewListener);
}
