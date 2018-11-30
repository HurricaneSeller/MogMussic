package com.example.moan.mogmussic.music.view;

import com.example.moan.mogmussic.music.LrcRow;

/**
 * this method is called when user pull the lyrics ;
 */
public interface ILrcViewListener {
    void onLrcSeeked(int newPosition, LrcRow lrcRow);
}
