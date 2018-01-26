package album.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.danale.localfile.R;
import com.danale.localfile.bean.Media;
import com.danale.localfile.constant.MediaType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import album.widget.GalleryImageView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Description :
 * CreateTime : 2018/1/19 17:03
 *
 * @author ningerlei@danale.com
 * @version <v1.0>
 * @Editor : Administrator
 * @ModifyTime : 2018/1/19 17:03
 * @ModifyDescription :
 */

public class TimesAdapter extends RecyclerView.Adapter<TimesAdapter.TimesHolder> {

    private Context mContext;
    private RecyclerView mParent;
    private List<ItemBean> mItemBeanList = new ArrayList<>();
    private List<List<Media>> mMediaList = new ArrayList<>();

    private boolean mStopGallery = true;

    public TimesAdapter(Context context, RecyclerView recyclerView) {
        this.mContext = context;
        this.mParent = recyclerView;
    }

    public void updateData(final TreeMap<String, List<Media>> mediaTreeMap) {
        Observable.just(mediaTreeMap)
                .flatMap(new Func1<TreeMap<String, List<Media>>, Observable<List<List<Media>>>>() {
                    @Override
                    public Observable<List<List<Media>>> call(TreeMap<String, List<Media>> stringListTreeMap) {
                        Iterator<String> it = stringListTreeMap.keySet().iterator();
                        while (it.hasNext()) {
                            String key = it.next();
                            ItemBean itemBean = new ItemBean();
                            itemBean.dateDay = key;
                            mItemBeanList.add(itemBean);
                            mMediaList.add(stringListTreeMap.get(key));
                        }
                        mHandler.sendEmptyMessage(0);
                        return Observable.just(mMediaList);
                    }
                })
                .flatMap(new Func1<List<List<Media>>, Observable<List<Media>>>() {
                    @Override
                    public Observable<List<Media>> call(List<List<Media>> lists) {
                        return Observable.from(lists);
                    }
                })
                .flatMap(new Func1<List<Media>, Observable<ItemBean>>() {
                    @Override
                    public Observable<ItemBean> call(List<Media> medias) {
                        ItemBean itemBean = mItemBeanList.get(mMediaList.indexOf(medias));
                        for (Media media : medias) {
                            if (media.getMediaType() == MediaType.RECORD) {
                                itemBean.recNum++;
                            } else {
                                itemBean.picNum++;
                            }
                        }
                        return Observable.just(itemBean);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ItemBean>() {
                    @Override
                    public void call(ItemBean media) {
                        startGallery(mParent);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        mStopGallery = true;
                    }
                });
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            notifyDataSetChanged();
        }
    };

    @Override
    public TimesHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_times, parent, false);
        return new TimesHolder(view);
    }

    @Override
    public void onBindViewHolder(final TimesHolder holder, final int position) {
        final ItemBean itemBean = mItemBeanList.get(position);
        holder.picNum.setText(String.valueOf(itemBean.picNum));
        holder.recNum.setText(String.valueOf(itemBean.recNum));
        holder.date.setText(itemBean.dateDay);
        final View.OnClickListener l = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(holder.getAdapterPosition(), mItemBeanList.get(holder.getAdapterPosition()).dateDay);
                }
            }
        };
        holder.itemLayout.setOnClickListener(l);
        holder.carousel.setOnClickListener(l);
        holder.carousel.setMediaList(mMediaList.get(position));
    }

    @Override
    public int getItemCount() {
        return mItemBeanList == null ? 0 : mItemBeanList.size();
    }

    public void startGallery(RecyclerView recyclerView) {
        if (mStopGallery) {
            mStopGallery = false;
            LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            final int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
            final int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
            for (int position = firstVisibleItemPosition; position <= lastVisibleItemPosition; position++) {
                final View child = recyclerView.getChildAt(position - firstVisibleItemPosition);
                if (child != null) {
                    TimesHolder viewHolder = (TimesHolder) recyclerView.getChildViewHolder(child);
                    viewHolder.carousel.startGallery();
                } else {
                    mStopGallery = true;
                    break;
                }
            }
        }
    }

    public void stopGallery(RecyclerView recyclerView) {
        if (!mStopGallery) {
            mStopGallery = true;
            LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            final int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
            final int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
            for (int position = firstVisibleItemPosition; position <= lastVisibleItemPosition; position++) {
                View child = recyclerView.getChildAt(position - firstVisibleItemPosition);
                TimesHolder viewHolder = (TimesHolder) recyclerView.getChildViewHolder(child);
                viewHolder.carousel.stopGallery();
            }
        }
    }

    class TimesHolder extends RecyclerView.ViewHolder {

        View itemLayout;
        GalleryImageView carousel;
        TextView picNum;
        TextView recNum;
        TextView date;

        TimesHolder(View itemView) {
            super(itemView);

            itemLayout = itemView.findViewById(R.id.item_layout);
            carousel = (GalleryImageView) itemView.findViewById(R.id.carousel);
            picNum = (TextView) itemView.findViewById(R.id.times_picnum);
            recNum = (TextView) itemView.findViewById(R.id.times_recnum);
            date = (TextView) itemView.findViewById(R.id.times_time);
        }
    }

    public static class ItemBean {
        String dateDay;
        int picNum;
        int recNum;
    }

    public interface OnItemClickListener {
        void onItemClick(int position, String date);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
