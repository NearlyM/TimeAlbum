package com.danale.localfile.wedgit;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.danale.local.R;
import com.danale.localfile.constant.CardMode;
import com.danale.localfile.bean.Media;
import com.danale.localfile.constant.MediaType;
import com.danale.localfile.util.ContextUtils;

/**
 * Created by kevin on 9/26/16.
 */

public class MediaBox extends RelativeLayout implements Checkable, GestureDetector.OnGestureListener {

    private GestureDetector mGestureDetector;
    private ImageView mMediaImage;
    private ImageView mCheckTagIcon;

    public MediaBox(Context context) {
        this(context, null);
    }

    public MediaBox(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MediaBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        setFocusable(true);
        setFocusableInTouchMode(true);
        setClickable(true);
        mGestureDetector = new GestureDetector(getContext(), this);
    }

    private CardMode mCardMode = CardMode.READ;

    public void setCardMode(CardMode mode) {
        mCardMode = mode;
        if (mCardMode == CardMode.READ) {
            setChecked(false);
        }
    }

    private Media mMedia;

    public void bind(Media media) {
        this.mMedia = media;

        bindImage(media);
    }

    private void bindImage(Media media) {

        setDefaultImage();

        bindThumb(media);

        bindMediaType(media);

        bindCheckTag();
    }

    private void setDefaultImage() {
        setBackgroundResource(R.drawable.default_picture);
    }

    private void bindCheckTag() {
        mCheckTagIcon = new ImageView(getContext());
        mCheckTagIcon.setImageResource(R.drawable.ic_selected);
        LayoutParams checkTagLp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        checkTagLp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        checkTagLp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        checkTagLp.setMargins(0, 0, 10, 10);
        mCheckTagIcon.setLayoutParams(checkTagLp);
        addView(mCheckTagIcon);
        mCheckTagIcon.setVisibility(View.GONE);
    }

    private void bindThumb(Media media) {
        mMediaImage = new ImageView(getContext());
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mMediaImage.setLayoutParams(lp);
        mMediaImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        addView(mMediaImage);
        Glide.with(getContext()).load(media.getUri()).apply(new RequestOptions().placeholder(R.drawable.default_picture).centerCrop()).into(mMediaImage);
    }

    private void bindMediaType(Media media) {

        if (media.getMediaType() == MediaType.RECORD) {
            RelativeLayout mediaTypeParent = new RelativeLayout(getContext());
            LayoutParams parentLP = new LayoutParams(LayoutParams.MATCH_PARENT, ContextUtils.dp2px(getContext(), 15));
            parentLP.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            mediaTypeParent.setLayoutParams(parentLP);

            ImageView mediaTypeIV = new ImageView(getContext());
            mediaTypeIV.setImageResource(R.drawable.file_video);
            LayoutParams mediaTypeIVLP = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            mediaTypeIVLP.addRule(RelativeLayout.CENTER_VERTICAL);
            mediaTypeIVLP.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            mediaTypeIVLP.leftMargin = ContextUtils.dp2px(getContext(), 5);
            mediaTypeIV.setLayoutParams(mediaTypeIVLP);
            mediaTypeParent.addView(mediaTypeIV);

            addView(mediaTypeParent);
        }
    }


    private boolean isChecked;
    private int mBorderWidth = 2;

    @Override
    public void setChecked(boolean checked) {
        isChecked = checked;
        mMedia.setSelected(isChecked);
        if (isChecked) {
            setBackgroundColor(Color.GREEN);
            mMediaImage.setPadding(mBorderWidth, mBorderWidth, mBorderWidth, mBorderWidth);
            mCheckTagIcon.setVisibility(View.VISIBLE);
        } else {
            setDefaultImage();
            mMediaImage.setPadding(0, 0, 0, 0);
            mCheckTagIcon.setVisibility(View.GONE);
        }
        if(null!=mOnItemSelectChangedListener){
            mOnItemSelectChangedListener.onSelect(mMedia);
        }
    }

    @Override
    public boolean isChecked() {
        return isChecked;
    }

    @Override
    public void toggle() {
        setChecked(!isChecked());
    }


    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        Log.d("FlexMediaCard", "onSingleTapUp");
        if (mCardMode == CardMode.EDIT) {
            toggle();
        } else {
            //TODO go to detail
            if(null!=mOnItemClickListener){
                mOnItemClickListener.onItemClick(mMedia);
            }
        }
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

    private FlexMediaCard.OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(FlexMediaCard.OnItemClickListener listener){
        this.mOnItemClickListener = listener;
    }

    private FlexMediaCard.OnItemSelectChangedListener mOnItemSelectChangedListener;
    public void setOnItemSelectChangedListener(FlexMediaCard.OnItemSelectChangedListener listener){
        this.mOnItemSelectChangedListener = listener;
    }
}
