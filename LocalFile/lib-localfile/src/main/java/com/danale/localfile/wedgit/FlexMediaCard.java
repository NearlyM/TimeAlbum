package com.danale.localfile.wedgit;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.danale.local.R;
import com.danale.localfile.constant.CardMode;
import com.danale.localfile.bean.Media;
import com.danale.localfile.constant.MediaType;
import com.danale.localfile.util.ContextUtils;
import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kevin on 9/26/16.
 */

public class FlexMediaCard extends LinearLayout {

    private View mCardHeader;
    private View mCardFooter;
    private FlexboxLayout mFlexFlow;
    private int mMeasureScreenWidth;
    private int mMeasureScreenHeight;
    private int mSpace;
    private int mColumn;
    private FlexboxLayout.LayoutParams mMediaBoxLayoutParams, mLeftMediaBoxLayoutParams;

    public FlexMediaCard(Context context) {
        this(context, null);
    }

    public FlexMediaCard(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlexMediaCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public void init(Context context, AttributeSet attrs) {
        mSpace = ContextUtils.dp2px(getContext(), 1);
        setBackgroundColor(Color.WHITE);
        setOrientation(LinearLayout.VERTICAL);
//        setPadding(defaultPadding, 10, defaultPadding, 10);
        getScreenMatrix();
        initColumnNum(context, attrs);
        initMediaBoxLayoutParams();

        mCardHeader = inflate(getContext(), R.layout.local_media_card_header, null);
        mCardFooter = inflate(getContext(), R.layout.local_media_card_footer, null);
        addView(mCardHeader);

        mFlexFlow = new FlexboxLayout(getContext());
        mFlexFlow.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        mFlexFlow.setFlexDirection(FlexboxLayout.FLEX_DIRECTION_ROW);
        mFlexFlow.setAlignContent(FlexboxLayout.ALIGN_CONTENT_CENTER);
        mFlexFlow.setFlexWrap(FlexboxLayout.FLEX_WRAP_WRAP);

        addView(mFlexFlow);
//        addView(mCardFooter);
    }

    private void initColumnNum(Context context, AttributeSet attrs) {

        TypedArray typeArray = context.obtainStyledAttributes(attrs, R.styleable.FlexMediaCard);
        mColumn = typeArray.getInt(R.styleable.FlexMediaCard_column, 4);
        typeArray.recycle();

    }

    private int defaultPadding = /*3*/0;

    private void initMediaBoxLayoutParams() {
        int sideLength = (mMeasureScreenWidth - mSpace * (this.mColumn - 1) - defaultPadding * 2) / this.mColumn;
        Log.d("FlexMediaCard", "sideLength: " + sideLength);
        final int trueWidth = sideLength * 4 + mSpace * 3;
        final int left = mMeasureScreenWidth - trueWidth;
        final int spit = left / 3;
        final int remainder = left % 3;
        mMediaBoxLayoutParams = new FlexboxLayout.LayoutParams(sideLength, sideLength);
        mMediaBoxLayoutParams.leftMargin = mSpace + spit;
        mMediaBoxLayoutParams.topMargin = mSpace;

        mLeftMediaBoxLayoutParams = new FlexboxLayout.LayoutParams(sideLength + remainder, sideLength);
        mLeftMediaBoxLayoutParams.leftMargin = 0;
        mLeftMediaBoxLayoutParams.topMargin = mSpace;
    }

    public MediaBox generateMediaBox(Media media, int index) {

        MediaBox mediaBox = new MediaBox(getContext());
        mediaBox.bind(media);
        mediaBox.setChecked(media.isSelected());
        if (index % mColumn == 0) {
            mediaBox.setLayoutParams(mLeftMediaBoxLayoutParams);
        } else {
            mediaBox.setLayoutParams(mMediaBoxLayoutParams);
        }
        mediaBox.setOnItemClickListener(mOnItemClickListener);
        mediaBox.setOnItemSelectChangedListener(mOnItemSelectChangedListener);
        return mediaBox;
    }


    public void addMediaBox(MediaBox mediaBox) {

        mFlexFlow.addView(mediaBox);
    }

    public void removeHeaderView() {
        removeView(mCardHeader);
    }

    public void setData(String groupId, List<Media> medias) {
        mFlexFlow.removeAllViews();
        List<MediaBox> mediaBoxes = new ArrayList<>();
        int imageCount = 0;
        int recordCount = 0;
        for (Media media :
                medias) {
            if (media.getMediaType() == MediaType.IMAGE)
                imageCount++;
            else if (media.getMediaType() == MediaType.RECORD)
                recordCount++;
            mediaBoxes.add(generateMediaBox(media, medias.indexOf(media)));
        }

        StringBuffer sb = new StringBuffer("");
        sb.append("(");
        if (imageCount != 0) {
            sb.append(imageCount)/*.append(imageCount > 1 ? getResources().getString(R.string.photos) : getResources().getString(R.string.photo))*/;
        }
        if (imageCount != 0 && recordCount != 0) {
            sb.append(",");
        }
        if (recordCount != 0) {
            sb.append(recordCount)/*.append(recordCount > 1 ? getResources().getString(R.string.videos) : getResources().getString(R.string.video))*/;
        }
        sb.append(")");

        bindFlow(groupId, mediaBoxes, sb.toString());
    }

    public void bindFlow(String groupId, List<MediaBox> mediaBoxes, String desc) {
        if (mCardHeader.getParent() != null) {
            TextView mediaGroupTime = (TextView) mCardHeader.findViewById(R.id.media_group_time);
            TextView mediaGroupDesc = (TextView) mCardHeader.findViewById(R.id.media_group_description);
            mediaGroupTime.setText(groupId);
            mediaGroupDesc.setText(desc);
        }

        for (MediaBox mediaBox :
                mediaBoxes) {
            addMediaBox(mediaBox);
        }

        invalidate();
    }


    private void getScreenMatrix() {
        mMeasureScreenWidth = ContextUtils.screenWidth(getContext());
        mMeasureScreenHeight = ContextUtils.screenHeight(getContext());
    }

    private CardMode mCardMode = CardMode.READ;

    public void setCardMode(CardMode mode) {
        mCardMode = mode;
        for (int index = 0; index < mFlexFlow.getChildCount(); index++) {
            ((MediaBox) mFlexFlow.getChildAt(index)).setCardMode(mCardMode);
        }
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
        for (int index = 0; index < mFlexFlow.getChildCount(); index++) {
            ((MediaBox) mFlexFlow.getChildAt(index)).setOnItemClickListener(mOnItemClickListener);
        }
    }

    private OnItemSelectChangedListener mOnItemSelectChangedListener;
    public void setOnItemSelectChangedListener(OnItemSelectChangedListener listener){
        this.mOnItemSelectChangedListener = listener;
        for (int index = 0; index < mFlexFlow.getChildCount(); index++) {
            ((MediaBox) mFlexFlow.getChildAt(index)).setOnItemSelectChangedListener(mOnItemSelectChangedListener);
        }
    }

    public interface OnItemClickListener {

        void onItemClick(Media media);


    }

    public interface OnItemSelectChangedListener{
        void onSelect(Media media);
    }


//    public void selectAll(boolean selected){
//        for (int index = 0; index < mFlexFlow.getChildCount(); index++) {
//            ((MediaBox) mFlexFlow.getChildAt(index)).setChecked(selected);
//        }
//    }
}
