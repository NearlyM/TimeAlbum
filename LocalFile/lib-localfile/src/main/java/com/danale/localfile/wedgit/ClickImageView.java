package com.danale.localfile.wedgit;/**
 * Created by ryan on 16-10-8.
 */

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.danale.localfile.util.ClickViewHelper;

/**
 * Description :<Content><br>
 * Create Time : 16-10-8 上午10:49
 *
 * @author 朱荣坤 zhurongkun@danale.com
 * @version <v1.0>
 * @Editor : 朱荣坤 zhurongkun@danale.com
 * @ModifyTime : 上午10:49
 * @ModifyDescription :DanaleRevolution
 * <Content>
 */
public class ClickImageView extends ImageView {
    ClickViewHelper mHelper;
    public ClickImageView(Context context) {
        super(context);
        mHelper = new ClickViewHelper(this);
    }

    public ClickImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mHelper = new ClickViewHelper(this);
    }

    public ClickImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mHelper = new ClickViewHelper(this);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ClickImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mHelper = new ClickViewHelper(this);
    }

    @Override
    public boolean performClick() {
        mHelper.performClick();
        return super.performClick();
    }

    @Override
    public void setPressed(boolean pressed) {
        super.setPressed(pressed);
//        mHelper.feedback(pressed);
    }
}
