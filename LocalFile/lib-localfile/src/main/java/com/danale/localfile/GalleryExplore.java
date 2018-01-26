package com.danale.localfile;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.danale.local.R;
import com.danale.local.R2;
import com.danale.localfile.bean.Media;
import com.danale.localfile.constant.MediaType;
import com.danale.localfile.util.ContextUtils;
import com.danale.localfile.util.DataCache;
import com.danale.localfile.util.FileUtils;
import com.danale.localfile.wedgit.HackyGallery;
import com.danale.localfile.wedgit.HackyViewPager;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by kevin on 9/27/16.
 */

public class GalleryExplore extends Activity {

    @BindView(R2.id.view_pager)
    HackyViewPager mViewPager;
    @BindView(R2.id.back)
    ImageView mBack;
    @BindView(R2.id.title)
    TextView mTitle;
    @BindView(R2.id.thumb_gallery)
    Gallery mThumbGallery;
    @BindView(R2.id.titlebar)
    RelativeLayout mTitlebar;
    @BindView(R2.id.thumb_gallery_bottom)
    HackyGallery mThumbGalleryBottom;
    @BindView(R2.id.share)
    TextView mShare;
    @BindView(R2.id.delete)
    TextView mDelete;
    @BindView(R2.id.popup_bar)
    FrameLayout mPopupBar;
    @BindView(R2.id.index)
    TextView mIndexTv;
    private int mCurrentPlayingIndex;
    List<Media> mSource;
    private int mScreenWidth;

    private static final int STYLE_EDIT = 0x01;
    private static final int STYLE_GALLERY = 0x02;
    private int mCurrentStyle = STYLE_EDIT;
    private Dialog mDeleteDialog;
    private GalleryThumbAdapter mGalleryThumbAdapter;
    private GalleryThumbAdapter mGalleryThumbBottomAdapter;
    private GalleryPagerAdapter mPagerAdapter;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void startActivity(Activity activity, int index, View view, String transitionName) {
        Intent intent = new Intent(activity, GalleryExplore.class);
        intent.putExtra("currentPlayingIndex", index);
        Pair<View, String> viewStringPair = new Pair<>(view, transitionName);
        ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(activity, viewStringPair);
        activity.startActivity(intent, activityOptions.toBundle());
    }

    public static void startActivity(Context context, int index) {
        Intent intent = new Intent(context, GalleryExplore.class);
        intent.putExtra("currentPlayingIndex", index);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("capture", "GalleryExplore onCreate start");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.local_activity_gallery_explore);
        ButterKnife.bind(this);
        resetPortrait();
        setDefault();

        setViewPager();
        setThumbGallery();
        Log.d("capture", "GalleryExplore onCreate over");
    }

    private void setDefault() {
        mScreenWidth = ContextUtils.screenWidth(this);
        mShare.setAlpha(0.2f);
        mShare.setEnabled(false);
        mDelete.setAlpha(1f);
        mDelete.setEnabled(true);
    }

    @OnClick({R2.id.back, R2.id.share, R2.id.delete})
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.back) {
            finish();

        } else if (i == R.id.delete) {
            showDeleteDialog();

        } /*else if (i == R.id.select_all) {
            saveAlbum();

        }*/
    }

    public void saveAlbum() {
        Media media = mSource.get(mCurrentPlayingIndex);
        saveImgToGallery(media);
    }

    /**
     * 保存图片或录像到相册
     */
    public void saveImgToGallery(Media media) {
        Uri uri = null;
        String path = null;
        if (media.getMediaType() == MediaType.RECORD) {
            path = getMatchVideoPath(media);
            uri = Uri.fromFile(new File(path));
            MediaScannerConnection.scanFile(this, new String[] {path},
                new String[]{"video/mp4"}, new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
                    }
                });
        } else {
            path = media.getUri().getPath();
            uri = media.getUri();
            // 把文件插入到系统图库
            try {
                MediaStore.Images.Media.insertImage(getContentResolver(),
                        path, new File(path).getName(), null);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            // 通知图库更新
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
        }
    }

    public void showDeleteDialog() {
        if (null == mDeleteDialog) {
            mDeleteDialog = new Dialog(this);
            mDeleteDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mDeleteDialog.setContentView(R.layout.local_dialog_delete);
            mDeleteDialog.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDeleteDialog.dismiss();
                }
            });
            mDeleteDialog.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Media media = mSource.get(mCurrentPlayingIndex);
                    if (media.getMediaType() == MediaType.IMAGE) {
                        FileUtils.deleteFile(media.getUri().getPath());
                    } else {
                        FileUtils.deleteFile(media.getUri().getPath());
                        FileUtils.deleteFile(getMatchVideoPath(media));
                    }
                    mSource.remove(mCurrentPlayingIndex);
                    mPagerAdapter.notifyDataSetChanged();
                    mDeleteDialog.dismiss();
                    if (mSource.size() == mCurrentPlayingIndex) {
                        mCurrentPlayingIndex -= 1;
                    }

                    if (mSource.size() == 0) {
                        finish();
                        return;
                    }

                    syncTitle(mCurrentPlayingIndex);
                    mGalleryThumbAdapter.notifyDataSetChanged();
                    mViewPager.setAdapter(mPagerAdapter);
                    mViewPager.setCurrentItem(mCurrentPlayingIndex);
                    mGalleryThumbBottomAdapter.notifyDataSetChanged();
                }
            });
        }
        mDeleteDialog.show();
    }

    public String getMatchVideoPath(Media media) {
        String imagePath = media.getUri().getPath();
        return imagePath.replace(FileUtils.VideoThumbsDir, FileUtils.VideoDir).replace(".png", ".mp4");
    }

    private void resetPortrait() {
        int orientation = getResources().getConfiguration().orientation;
//        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        }
    }

    private void setThumbGallery() {
        mGalleryThumbAdapter = new GalleryThumbAdapter(2.0f);
        mThumbGallery.setAdapter(mGalleryThumbAdapter);
        mThumbGallery.setSelection(mCurrentPlayingIndex);
        mThumbGallery.setSpacing(20);
        mThumbGallery.setUnselectedAlpha(1.0f);
        setOnSelectedChanged(mThumbGallery, mThumbGalleryBottom);

        mGalleryThumbBottomAdapter = new GalleryThumbAdapter(4.0f);
        mThumbGalleryBottom.setAdapter(mGalleryThumbBottomAdapter);
        mThumbGalleryBottom.setSelection(mCurrentPlayingIndex);
        mThumbGalleryBottom.setSpacing(10);
        mThumbGalleryBottom.setUnselectedAlpha(1.0f);
        setOnSelectedChanged(mThumbGalleryBottom, mThumbGallery);
    }

    private void setOnSelectedChanged(final Gallery gallery1, final Gallery gallery2) {
        final AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (mCurrentPlayingIndex != position) {
                    mCurrentPlayingIndex = position;
                    syncTitle(position);

                    gallery1.setOnItemSelectedListener(null);
                    gallery2.setSelection(position);
                    gallery1.setOnItemSelectedListener(this);

                    mViewPager.setCurrentItem(position);
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };

        gallery1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (mCurrentPlayingIndex != position) {
                    mCurrentPlayingIndex = position;
                    syncTitle(position);

                    mViewPager.setCurrentItem(position);
                    gallery1.setOnItemSelectedListener(null);
                    gallery2.setSelection(position);
                    gallery1.setOnItemSelectedListener(onItemSelectedListener);
                }
                if (mCurrentStyle == STYLE_GALLERY) {
                    onStyleChanged();
                }

            }
        });

        gallery1.setOnItemSelectedListener(onItemSelectedListener);

    }


    private void setViewPager() {
        mCurrentPlayingIndex = getIntent().getIntExtra("currentPlayingIndex", 0);
        mSource = DataCache.getInstance().cachedMedias;
        //TODO
        syncTitle(mCurrentPlayingIndex);
        mPagerAdapter = new GalleryPagerAdapter();
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setCurrentItem(mCurrentPlayingIndex);
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (mCurrentPlayingIndex != position) {
                    mCurrentPlayingIndex = position;
                    syncTitle(position);
                    mThumbGallery.setSelection(position);
                    mThumbGalleryBottom.setSelection(position);
                }
            }
        });
    }

    public void syncTitle(int position) {

        String name = new File(mSource.get(position).getUri().getPath()).getName();
        mTitle.setText(TextUtils.isEmpty(name) ? "" : name);

        mIndexTv.setText((mCurrentPlayingIndex + 1) + "/" + mSource.size());
    }

    @OnClick(R2.id.back)
    public void onClick() {
        finish();
    }

    private class GalleryThumbAdapter extends BaseAdapter {

        float mFactor;
        private int mW = -1;
        private int mH = -1;

        GalleryThumbAdapter(float factor) {
            mFactor = factor;
        }

        @Override
        public int getCount() {
            return mSource.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Media media = mSource.get(position);
            Uri uri = media.getUri();
            if (-1 == mW) {
                mW = (int) (mScreenWidth / mFactor);
                mH = (int) (mW * 9.0 / 16.0);
            }
            ViewHolder holder;
            if (null == convertView || convertView.getTag() == null) {
                convertView = new RelativeLayout(parent.getContext());
                holder = new ViewHolder();
                Gallery.LayoutParams lp = new Gallery.LayoutParams(mW, mH);
                convertView.setLayoutParams(lp);
                holder.thumb = new ImageView(parent.getContext());
                holder.thumb.setLayoutParams(new FrameLayout.LayoutParams(mW, mH));
                ((RelativeLayout) convertView).addView(holder.thumb);

                RelativeLayout mediaTypeParent = new RelativeLayout(parent.getContext());
                RelativeLayout.LayoutParams parentLP = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, ContextUtils.dp2px(parent.getContext(), 15));
                parentLP.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                mediaTypeParent.setLayoutParams(parentLP);

                ImageView mediaTypeIV = new ImageView(parent.getContext());
                mediaTypeIV.setImageResource(R.drawable.file_video);
                RelativeLayout.LayoutParams mediaTypeIVLP = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                mediaTypeIVLP.addRule(RelativeLayout.CENTER_VERTICAL);
                mediaTypeIVLP.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                mediaTypeIVLP.leftMargin = ContextUtils.dp2px(parent.getContext(), 5);
                mediaTypeIV.setLayoutParams(mediaTypeIVLP);
                mediaTypeParent.addView(mediaTypeIV);

                ((RelativeLayout) convertView).addView(mediaTypeParent);
                holder.mediaTypeTagView = mediaTypeParent;

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.thumb.setScaleType(ImageView.ScaleType.FIT_XY);
            if (media.getMediaType() == MediaType.IMAGE) {
                holder.mediaTypeTagView.setVisibility(View.GONE);
            } else {
                holder.mediaTypeTagView.setVisibility(View.VISIBLE);
            }
//            if (uri != holder.thumb.getTag()) {
                Glide.with(GalleryExplore.this).load(uri)
                        .apply(new RequestOptions().placeholder(R.drawable.default_picture).dontAnimate())
//                        .dontAnimate().placeholder(com.danale.ui.R.drawable.default_picture)
                        .thumbnail(0.1f)
                        .into(holder.thumb);
//            }
//            holder.thumb.setTag(uri);


            return convertView;
        }

    }

    class ViewHolder {
        ImageView thumb;
        View mediaTypeTagView;
    }

    private class GalleryPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mSource.size();
        }

        @Override
        public View instantiateItem(ViewGroup container, final int position) {
            final Media media = mSource.get(position);
            RelativeLayout parent = new RelativeLayout(container.getContext());
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            parent.setLayoutParams(layoutParams);
            final PhotoView photoView = new PhotoView(container.getContext());
            setPhotoView(media, photoView, layoutParams);

            parent.addView(photoView);

            ImageView pauseView = new ImageView(container.getContext());
            setPauseView(media, pauseView);
            parent.addView(pauseView);

            Glide.with(GalleryExplore.this).load(media.getUri())
                    .apply(new RequestOptions().placeholder(R.drawable.default_picture))
//                    .placeholder(com.danale.ui.R.drawable.default_picture)
                    .into(photoView);
            container.addView(parent);
            return parent;
        }

        public void setPauseView(final Media media, ImageView pauseView) {
            pauseView.setImageResource(R.drawable.card_paly);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            pauseView.setLayoutParams(layoutParams);
            pauseView.setClickable(true);

            if (media.getMediaType() == MediaType.IMAGE) {
                pauseView.setVisibility(View.GONE);
            } else {
                pauseView.setVisibility(View.VISIBLE);
            }
            pauseView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startSystemDefaultPlayer(media);
                }
            });
        }

        public void setPhotoView(Media media, final PhotoView photoView, ViewGroup.LayoutParams layoutParams) {
            photoView.setMinimumScale(0.5f);
            photoView.setLayoutParams(layoutParams);


            photoView.setOnScaleChangeListener(new PhotoViewAttacher.OnScaleChangeListener() {
                @Override
                public void onScaleChange(float scaleFactor, float focusX, float focusY) {
                    if (photoView.getScale() < 0.7f && scaleFactor < 1.0f) {
                        if (mCurrentStyle == STYLE_GALLERY) {
                            photoView.setScale(0.5f);
                            mViewPager.setVisibility(View.GONE);
                            mThumbGallery.setVisibility(View.VISIBLE);
                        }
                    }
                }
            });

            if (media.getMediaType() == MediaType.IMAGE) {
                photoView.setZoomable(true);
            } else {
                photoView.setZoomable(false);
            }

            photoView.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
                @Override
                public void onViewTap(View view, float x, float y) {
                    onStyleChanged();
                }
            });
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }

    public void onStyleChanged() {
        Media media = mSource.get(mCurrentPlayingIndex);
        String filePath = media.getUri().getPath();
        String device_id = filePath.substring(filePath.lastIndexOf("/") + 1, filePath.indexOf("_ch"));
        if (mCurrentStyle == STYLE_EDIT) {
            mCurrentStyle = STYLE_GALLERY;
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            mPopupBar.setVisibility(View.GONE);
            mThumbGalleryBottom.setVisibility(View.GONE);
            mViewPager.setBackgroundColor(Color.BLACK);
            mTitlebar.setVisibility(View.GONE);
            mIndexTv.setVisibility(View.GONE);
        } else {
            mCurrentStyle = STYLE_EDIT;
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            mPopupBar.setVisibility(View.VISIBLE);
            mThumbGalleryBottom.setVisibility(View.VISIBLE);
            mViewPager.setBackgroundColor(Color.WHITE);
            mThumbGallery.setVisibility(View.GONE);
            mViewPager.setVisibility(View.VISIBLE);
            mTitlebar.setVisibility(View.VISIBLE);
            mIndexTv.setVisibility(View.VISIBLE);
        }
    }


    public void startSystemDefaultPlayer(Media media) {
//        try {
//            String videoPath = getMatchVideoPath(media);
//            Uri uri = Uri.parse(videoPath);
//            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//            intent.setDataAndType(uri, "video/mp4");
//
//            if (isCallable(intent)) {
//                startActivityWithFriendInfo(intent);
//            } else {
//                intent.setClass(GalleryExplore.this, SimpleVideoViewActivity.class);
////                ToastUtil.showToast(GalleryExplore.this,getString(R.string.no_default_player));
//                startActivityWithFriendInfo(intent);
//            }
//        } catch (Exception e) {
//
//        }
        String filePath = getMatchVideoPath(media);
        String device_id = filePath.substring(filePath.lastIndexOf("/") + 1, filePath.indexOf("_ch"));
        if(!TextUtils.isEmpty(device_id)){
//            Device device = DeviceCache.getInstance().getDevice(device_id);
//            if(device != null && DeviceHelper.isEapilDevice(device)){
//                LocalMediaActivity.startAcitivity(this,getMatchVideoPath(media),device_id);
//            }else{
//                LocalAlarmRecordActivity.startActivity(this, media.getUri().getPath(), VideoDataType.RECORD, getMatchVideoPath(media));
//            }
        }else{
//            LocalAlarmRecordActivity.startActivity(this, media.getUri().getPath(), VideoDataType.RECORD, getMatchVideoPath(media));
        }
    }

    private boolean isCallable(Intent intent) {
        List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

}
