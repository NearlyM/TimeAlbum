package com.danale.localfile;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.GenericTransitionOptions;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.danale.local.R;
import com.danale.local.R2;
import com.danale.localfile.bean.Media;
import com.danale.localfile.constant.CardMode;
import com.danale.localfile.constant.MediaType;
import com.danale.localfile.util.ContextUtils;
import com.danale.localfile.util.DateTimeUtils;
import com.danale.localfile.util.FileUtils;
import com.danale.localfile.util.DataCache;
import com.danale.localfile.util.MediaScanner;
import com.danale.localfile.wedgit.FlexMediaCard;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by kevin on 9/26/16.
 */

public class FileExplore extends Fragment {

    @BindView(R2.id.recycle_view)
    RecyclerView mRecycleView;
    @BindView(R2.id.back)
    TextView mBack;
    @BindView(R2.id.edit)
    TextView mEdit;
    @BindView(R2.id.popup_bar)
    View mPopupBar;
    @BindView(R2.id.share)
    TextView mShareBtn;
    @BindView(R2.id.delete)
    TextView mDeleteBtn;
    @BindView(R2.id.select_num)
    TextView mSelectNumTv;
    @BindView(R2.id.tv_nofile_tip)
    TextView mTvNofile;
    @BindView(R2.id.zone_display)
    ImageView mDisplayZone;
    @BindView(R2.id.titlebar)
    RelativeLayout mTitleLayout;
    private View mLayout;
    private CardMode mCardMode;
    private FileExploreAdapter mExploreAdapter;
    private Dialog mDeleteDialog;

    Selector selector;

    public static class Selector implements Serializable {
        private MediaType mediaType;
        private String dateDay;

        private Selector(MediaType mediaType, String dateDay) {
            this.mediaType = mediaType;
            this.dateDay = dateDay;
        }

        public static class Builder {
            private MediaType mediaType = MediaType.HYBIRD;
            private String dateDay = null;
            public Builder setMediaType (MediaType type) {
                this.mediaType = type;
                return this;
            }

            /**
             * 设置要显示的日期，如果time < 0，则显示所有日期
             * @param time
             * @return
             */
            public Builder setDateDay(long time) {
                if (time > 0) {
                    this.dateDay = DateTimeUtils.getDateTime(time, DateTimeUtils.FORMAT_DATE_DOT, null);
                }
                return this;
            }

            public Builder setDateDay(String time) {
                this.dateDay = time;
                return this;
            }

            public Selector build() {
                return new Selector(mediaType, dateDay);
            }
        }
    }

    public static FileExplore newInstance(Selector type) {
        FileExplore fileExplore = new FileExplore();
        Bundle args = new Bundle();
        args.putSerializable("type", type);
        fileExplore.setArguments(args);
        return fileExplore;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mLayout = View.inflate(getContext(), R.layout.local_activity_file_explore, null);
        ButterKnife.bind(this, mLayout);
        initData();
        return mLayout;
    }

    private void initData() {
        selector = (Selector) getArguments().getSerializable("type");
        if (!TextUtils.isEmpty(selector.dateDay)) {
            ViewGroup.LayoutParams layoutParams = mDisplayZone.getLayoutParams();
            layoutParams.width = getResources().getDisplayMetrics().widthPixels;
            layoutParams.height = (int) (layoutParams.width * 9f / 16f);
            mDisplayZone.setLayoutParams(layoutParams);
            mTitleLayout.setVisibility(View.VISIBLE);
            mSelectNumTv.setText(selector.dateDay);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
        scanFiles(DataCache.getInstance().mUsername);
        mGalleryStop = false;
    }

    private void scanFiles(String accountName) {
        MediaScanner.scanFile(getContext(), accountName, selector.mediaType)
                .flatMap(new Func1<TreeMap<String, List<Media>>, Observable<TreeMap<String, List<Media>>>>() {
                    @Override
                    public Observable<TreeMap<String, List<Media>>> call(TreeMap<String, List<Media>> stringListTreeMap) {
                        if (TextUtils.isEmpty(selector.dateDay)) {
                            return Observable.just(stringListTreeMap);
                        } else {
                            TreeMap<String, List<Media>> listTreeMap = new TreeMap<>();
                            listTreeMap.put(selector.dateDay, stringListTreeMap.get(selector.dateDay));
                            if (listTreeMap.size() != 0) {
                                mGalleryHandle.sendEmptyMessage(0);
                            }
                            return Observable.just(listTreeMap);
                        }
                    }
                })
                .subscribe(new Action1<TreeMap<String, List<Media>>>() {
                    @Override
                    public void call(TreeMap<String, List<Media>> treeMap) {
                        if (treeMap.keySet().isEmpty()) {
                            showNoFileTips();
                        } else {
                            mExploreAdapter = new FileExploreAdapter(treeMap);
                            showDataView();
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                });
    }

    private boolean mGalleryStop;
    @SuppressLint("HandlerLeak")
    Handler mGalleryHandle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (!mGalleryStop) {
                int position = msg.what;
                if (mExploreAdapter != null) {
                    final List<Media> mediaList = mExploreAdapter.mediasList.get(0);
                    if (position >= mediaList.size()) {
                        position = 0;
                    }
                    Media media = mediaList.get(position);
                    position++;
                    Glide.with(getContext())
                            .load(media.getUri())
                            .listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    if (dataSource == DataSource.MEMORY_CACHE) {
                                        //当图片位于内存缓存时，glide默认不会加载动画
                                        mDisplayZone.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.scale));
                                    }
                                    return false;
                                }
                            })
                            .apply(new RequestOptions().placeholder(mDisplayZone.getDrawable()))
                            .transition(new GenericTransitionOptions<Drawable>().transition(R.anim.scale))
                            .into(mDisplayZone);
                }
                mGalleryHandle.sendEmptyMessageDelayed(position, 4000);
            }
        }
    };

    private void showDataView() {
        mTvNofile.setVisibility(View.GONE);
        mEdit.setVisibility(View.VISIBLE);
        mRecycleView.setVisibility(View.VISIBLE);
        mRecycleView.setAdapter(mExploreAdapter);
    }

    private void showNoFileTips() {
        mTvNofile.setVisibility(View.VISIBLE);
        mEdit.setVisibility(View.GONE);
        mRecycleView.setVisibility(View.GONE);
    }

    private boolean isEditable;
    private boolean mSelectedAll;

    @OnClick({R2.id.back, R2.id.edit, R2.id.share, R2.id.delete})
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.back) {
            if (isEditable) {
//                changeEditMode();
                toggleSelectedAll(!mSelectedAll);
            } else {
                getActivity().finish();
            }


        } else if (i == R.id.edit) {
            changeEditMode();

        } else if (i == R.id.delete) {
            mExploreAdapter.showDeleteDialog();

        } else if (i == R.id.share) {
            showShare();
        }
    }

    private void toggleSelectedAll(boolean selectedAll) {
        toggleSelectedAllBtnStatus(selectedAll);
        mExploreAdapter.selectAll(mSelectedAll);
//        setTitle(getString(R.string.selected) + mSelectedCount + getString(R.string.item));
    }

    private void showShare() {
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        // title标题，微信、QQ和QQ空间等平台使用
        oks.setTitle(getString(R.string.share));
        // titleUrl QQ和QQ空间跳转链接
        oks.setTitleUrl("http://sharesdk.cn");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("我是分享文本");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url在微信、微博，Facebook等平台中使用
        oks.setUrl("http://sharesdk.cn");
        // comment是我对这条分享的评论，仅在人人网使用
        oks.setComment("我是测试评论文本");
        // 启动分享GUI
        oks.show(getContext());
    }

    private void shareQQ() {
        Platform.ShareParams sp = new Platform.ShareParams();
        sp.setTitle("测试分享的标题");
        sp.setTitleUrl("http://sharesdk.cn"); // 标题的超链接
        sp.setText("测试分享的文本");
        sp.setImageUrl("http://www.someserver.com/测试图片网络地址.jpg");
        sp.setSite("发布分享的网站名称");
        sp.setSiteUrl("发布分享网站的地址");
        Platform qzone = ShareSDK.getPlatform (QQ.NAME);
// 设置分享事件回调（注：回调放在不能保证在主线程调用，不可以在里面直接处理UI操作）
        qzone.setPlatformActionListener (new PlatformActionListener() {
            public void onError(Platform arg0, int arg1, Throwable arg2) {
                //失败的回调，arg:平台对象，arg1:表示当前的动作，arg2:异常信息
            }
            public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
                //分享成功的回调
            }
            public void onCancel(Platform arg0, int arg1) {
                //取消分享的回调
            }
        });
// 执行图文分享
        qzone.share(sp);
    }

    private void toggleSelectedAllBtnStatus(boolean selectedAll) {
        mSelectedAll = selectedAll;

        if (mSelectedAll) {
            mShareBtn.setAlpha(1f);
            mShareBtn.setEnabled(true);
            mDeleteBtn.setAlpha(1f);
            mDeleteBtn.setEnabled(true);
//            mUploadBtn.setAlpha(1f);
//            mUploadBtn.setEnabled(true);
        } else {
            mShareBtn.setAlpha(0.2f);
            mShareBtn.setEnabled(false);
            mDeleteBtn.setAlpha(0.2f);
            mDeleteBtn.setEnabled(false);
        }
    }

    public void changeEditMode() {
        isEditable = !isEditable;
        if (isEditable) {
            mCardMode = CardMode.EDIT;
//            mEdit.setVisibility(View.INVISIBLE);
//            mBack.setImageResource(R.drawable.ic_close);
            mBack.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            mBack.setText("全选");
            mEdit.setText("取消");
            togglePopupBar(View.VISIBLE);
            mRecycleView.setPadding(0, 0, 0, ContextUtils.dp2px(getContext(), 60));
//            setTitle(getString(R.string.selected) + mSelectedCount + getString(R.string.item));
        } else {
            mCardMode = CardMode.READ;
//            mEdit.setVisibility(View.VISIBLE);
//            mBack.setImageResource(R.drawable.icon_return);
            mEdit.setText("选择");
            mBack.setText("");
            mBack.setBackgroundResource(R.drawable.icon_return);
            togglePopupBar(View.GONE);
            mRecycleView.setPadding(0, 0, 0, 0);
            toggleSelectedAll(false);
//            setTitle(getString(R.string.local_file));
        }
        for (int index = 0; index < mRecycleView.getChildCount(); index++) {
            ((FlexMediaCard) mRecycleView.getChildAt(index)).setCardMode(mCardMode);
        }
    }

    public void setTitle(String str) {
        if (isEditable) {
            mSelectNumTv.setText(str);
        } else {
            mSelectNumTv.setText(R.string.local_file);
        }
    }

    private int mSelectedCount;

    private void togglePopupBar(int visibility) {
        mPopupBar.setVisibility(visibility);
    }

    private class FileExploreAdapter extends RecyclerView.Adapter {

        private List<String> dates;

        private List<List<Media>> mediasList;


        public FileExploreAdapter(TreeMap<String, List<Media>> mediasMap) {

            initDataList(mediasMap);
        }

        private void initDataList(TreeMap<String, List<Media>> mediasMap) {
            dates = new ArrayList<>();
            mediasList = new ArrayList<>();
            Iterator<String> it = mediasMap.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next();
                dates.add(key);
                sortMedialist(mediasMap.get(key));
                mediasList.add(mediasMap.get(key));
            }
        }


        private void sortMedialist(List<Media> list){
            Collections.sort(list);

        }

        public void clear() {
            dates.clear();
            mediasList.clear();

        }


        public void selectAll(boolean selected) {
            mSelectedCount = 0;
            for (List<Media> medias : mediasList) {
                for (Media media : medias) {
                    media.setSelected(selected);
                    if (selected)
                        mSelectedCount++;
                }
            }
            notifyDataSetChanged();
        }

        public boolean isAllSelected() {
            mSelectedCount = 0;
            boolean isAllSelected = true;
            if (!mediasList.isEmpty()) {
                for (List<Media> medias : mediasList) {
                    for (Media media : medias) {
                        if (media.isSelected()) {
                            mSelectedCount++;
                        } else {
                            isAllSelected = false;
                        }

                    }
                }
            } else {
                isAllSelected = false;
            }

            return isAllSelected;
        }

        public void performDelete() {
            List<Media> medias;
            for (int groupIndex = mediasList.size() - 1; groupIndex >= 0; groupIndex--) {

                medias = mediasList.get(groupIndex);
                List<Media> removeList = new ArrayList<>();
                for (Media media : medias) {
                    if (media.isSelected()) {
                        if (media.getMediaType() == MediaType.IMAGE) {

                            FileUtils.deleteFile(media.getUri().getPath());
                        } else {
                            FileUtils.deleteFile(media.getUri().getPath());
                            FileUtils.deleteFile(getMatchVideoPath(media));
                        }
                        removeList.add(media);
                    }

                }
                medias.removeAll(removeList);

                if (medias.size() == 0) {
                    dates.remove(groupIndex);
                    mediasList.remove(medias);
                }

            }


            notifyDataSetChanged();
            syncSelectedUIStatus();
            if (mediasList.size() == 0) {
                changeEditMode();
                showNoFileTips();
            }
        }

        public void showDeleteDialog() {
            if (null == mDeleteDialog) {
                mDeleteDialog = new Dialog(getContext());
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
                        mExploreAdapter.performDelete();
                        mDeleteDialog.dismiss();
                    }
                });
            }
            mDeleteDialog.show();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            FlexMediaCard flexMediaCard = new FlexMediaCard(parent.getContext());
            flexMediaCard.setOnItemSelectChangedListener(new FlexMediaCard.OnItemSelectChangedListener() {
                @Override
                public void onSelect(Media media) {

                    syncSelectedUIStatus();
                    Log.e("local", "item selected");
                }
            });
            return new MediaHolder(flexMediaCard);
        }

        public void syncSelectedUIStatus() {
            if (isAllSelected()) {

                toggleSelectedAllBtnStatus(true);
            } else {
                toggleSelectedAllBtnStatus(false);
            }

            if (mSelectedCount == 0) {
                mShareBtn.setAlpha(0.2f);
                mShareBtn.setEnabled(false);
                mDeleteBtn.setAlpha(0.2f);
                mDeleteBtn.setEnabled(false);
            } else {
                mShareBtn.setAlpha(1f);
                mShareBtn.setEnabled(true);
                mDeleteBtn.setAlpha(1f);
                mDeleteBtn.setEnabled(true);

            }
//            mSelectNumTv.setText(getString(R.string.selected) + mSelectedCount + getString(R.string.item));

//            setTitle(getString(R.string.selected) + mSelectedCount + getString(R.string.item));

        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            String date = dates.get(position);
            List<Media> medias = mediasList.get(position);
            if (!TextUtils.isEmpty(selector.dateDay)) {
                ((FlexMediaCard) holder.itemView).removeHeaderView();
            }
            ((FlexMediaCard) holder.itemView).setData(date, medias);
            ((FlexMediaCard) holder.itemView).setCardMode(mCardMode);

            onItemClick(holder);
        }

        private void onItemClick(final RecyclerView.ViewHolder holder) {
            ((FlexMediaCard) holder.itemView).setOnItemClickListener(new FlexMediaCard.OnItemClickListener() {
                @Override
                public void onItemClick(Media media) {
                    DataCache.getInstance().cachedMedias = flatData(mediasList);
                    GalleryExplore.startActivity(getContext(), DataCache.getInstance().cachedMedias.indexOf(media));
                }
            });
        }

        public String getMatchVideoPath(Media media) {
            String imagePath = media.getUri().getPath();
            return imagePath.replace(FileUtils.VideoThumbsDir, FileUtils.VideoDir).replace(".png", ".mp4");
        }


        LinkedList<Media> flatData(List<List<Media>> meidas) {
            LinkedList<Media> source = new LinkedList<>();
            for (List list :
                    meidas) {
                source.addAll(list);
            }
            return source;
        }


        @Override
        public int getItemCount() {
            return null == dates || dates.size() == 0 ? 0 : dates.size();
        }

        class MediaHolder extends RecyclerView.ViewHolder {

            public MediaHolder(View itemView) {
                super(itemView);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mGalleryStop = true;
    }
}
