package album.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ViewSwitcher;

import com.bumptech.glide.Glide;
import com.danale.localfile.bean.Media;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Description :
 * CreateTime : 2018/1/24 11:38
 *
 * @author ningerlei@danale.com
 * @version <v1.0>
 * @Editor : Administrator
 * @ModifyTime : 2018/1/24 11:38
 * @ModifyDescription :
 */

public class GalleryImageView extends ImageSwitcher implements ViewSwitcher.ViewFactory {

    List<Media> mediaList = new ArrayList<>();
    private boolean isFactoryNull = true;

    public GalleryImageView(Context context) {
        this(context, null);
    }

    public GalleryImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setInAnimation(AnimationUtils.loadAnimation(getContext(), com.danale.local.R.anim.slide_in_left));
        setOutAnimation(AnimationUtils.loadAnimation(getContext(), com.danale.local.R.anim.slide_out_right));
    }

    public void setMediaList(List<Media> mediaList) {
        this.mediaList.clear();
        this.mediaList.addAll(mediaList);
        if (isFactoryNull) {
            isFactoryNull = false;
            setFactory(this);
        }
    }

    public void startGallery() {
        mGalleryStop = false;
        startTimer();
    }

    public void stopGallery() {
        mGalleryStop = true;
        stopTimer();
    }

    private int mCurrentPosition;
    private boolean mGalleryStop;

    public int getCurrentPosition() {
        return mCurrentPosition;
    }

    Timer mGalleryTimer;
    TimerTask mGalleryTimerTask;

    public void startTimer() {
        if (mGalleryTimerTask == null) {
            mGalleryTimerTask = new TimerTask() {
                @Override
                public void run() {
                    if (!mGalleryStop) {
                        mGalleryHandle.sendEmptyMessage(mCurrentPosition + 1);
                    }
                }
            };
        }
        if (mGalleryTimer == null) {
            mGalleryTimer = new Timer();
            mGalleryTimer.schedule(mGalleryTimerTask, 2000, 3000);
        }
    }

    public void stopTimer() {
        if (mGalleryTimer != null) {
            mGalleryTimer.cancel();
            mGalleryTimer = null;
        }
        if (mGalleryTimerTask != null) {
            mGalleryTimerTask.cancel();
            mGalleryTimerTask = null;
        }
    }

    @SuppressLint("HandlerLeak")
    Handler mGalleryHandle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (!mGalleryStop) {
                int position = msg.what;
                if (position >= mediaList.size()) {
                    position = 0;
                }
                mCurrentPosition = position;
                com.danale.localfile.bean.Media media = mediaList.get(position);
                setImageURI(media.getUri());
            }
        }
    };

    @Override
    public View makeView() {
        ImageView imageView = new ImageView(getContext());
        Glide.with(getContext()).asDrawable().load(mediaList.get(0).getUri()).into(imageView);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setLayoutParams(new ImageSwitcher.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        return imageView ;
    }
}
