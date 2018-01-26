package com.danale.localfile.wedgit;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Gallery;

/**
 * Created by kevin on 10/24/16.
 */

public class HackyGallery extends Gallery {
    public HackyGallery(Context context) {
        super(context);
    }

    public HackyGallery(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HackyGallery(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        this.onTouchEvent(ev);

        return super.dispatchTouchEvent(ev);
    }
}
