package com.deenysoft.mindspeech.widget;

import android.annotation.TargetApi;
import android.graphics.Outline;
import android.os.Build;
import android.view.View;
import android.view.ViewOutlineProvider;

/**
 * Created by shamsadam on 18/09/16.
 */

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class RoundOutlineProvider extends ViewOutlineProvider {

    private final int mSize;

    public RoundOutlineProvider(int size) {
        if (0 > size) {
            throw new IllegalArgumentException("size needs to be > 0. Actually was " + size);
        }
        mSize = size;
    }

    @Override
    public final void getOutline(View view, Outline outline) {
        outline.setOval(0, 0, mSize, mSize);
    }


}
