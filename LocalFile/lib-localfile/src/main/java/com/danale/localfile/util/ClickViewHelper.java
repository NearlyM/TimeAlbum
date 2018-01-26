package com.danale.localfile.util;/**
 * Created by ryan on 16-10-8.
 */

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;

/**
 * Description :<Content><br>
 * Create Time : 16-10-8 上午10:04
 *
 * @author 朱荣坤 zhurongkun@danale.com
 * @version <v1.0>
 * @Editor : 朱荣坤 zhurongkun@danale.com
 * @ModifyTime : 上午10:04
 * @ModifyDescription :DanaleRevolution
 * <Content>
 */
public class ClickViewHelper {
    private final View view;
    private boolean mStartPressed;
    private ValueAnimator mFeedbackAnimation;
    private float zoomDegree;
    private boolean mAnimating;

    public ClickViewHelper(View view) {
        this(view, 0.2f);
    }

    public ClickViewHelper(View view, float zoomDegree) {
        this.view = view;
        this.zoomDegree = zoomDegree;
    }

    public void feedback(boolean pressed) {
        float zoomUp = 1 + zoomDegree;
        float zoomIn = 1 - zoomDegree;

        if (mFeedbackAnimation == null) {
            mFeedbackAnimation = ValueAnimator.ofFloat(1.0f, zoomUp);
            mFeedbackAnimation.setDuration(view.getResources().getInteger(android.R.integer.config_shortAnimTime));
            mFeedbackAnimation.setInterpolator(new LinearInterpolator());
            mFeedbackAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float scale = (float) animation.getAnimatedValue();
                    view.setScaleX(scale);
                    view.setScaleY(scale);
                    view.postInvalidate();
                }
            });
        }
        if (pressed) {
            if (mStartPressed) {
                return;
            }
            mFeedbackAnimation.setFloatValues(1.0f, zoomUp);
            mStartPressed = true;
            mFeedbackAnimation.start();
        } else if (mStartPressed) {
            mFeedbackAnimation.setFloatValues(zoomUp, zoomIn, 1.0f);
            mStartPressed = false;
            mFeedbackAnimation.start();
        }
    }

    public void performClick() {
        if (mStartPressed)
            return;
        final float scaleX = view.getScaleX();
        final float scaleY = view.getScaleY();
        ValueAnimator animator = ValueAnimator.ofFloat(0, zoomDegree, 0);
        animator.setDuration(view.getResources().getInteger(android.R.integer.config_mediumAnimTime));
        animator.setInterpolator(new OvershootInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float scale = (float) animation.getAnimatedValue();
                view.setScaleX(scale + scaleX);
                view.setScaleY(scale + scaleY);
                view.postInvalidate();
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mStartPressed = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        mStartPressed = true;
        animator.start();
    }
}
