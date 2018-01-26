package album;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.danale.localfile.BaseFragment;
import com.danale.localfile.R;
import com.danale.localfile.bean.Media;
import com.danale.localfile.constant.MediaType;
import com.danale.localfile.util.MediaScanner;

import java.util.List;
import java.util.TreeMap;

import album.adapter.TimesAdapter;
import rx.functions.Action1;

/**
 * Description :
 * CreateTime : 2018/1/19 16:41
 *
 * @author ningerlei@danale.com
 * @version <v1.0>
 * @Editor : Administrator
 * @ModifyTime : 2018/1/19 16:41
 * @ModifyDescription :
 */

public class TimesFragment extends BaseFragment implements TimesAdapter.OnItemClickListener
        , View.OnTouchListener {

    RecyclerView mTimesRV;

    private TimesAdapter mTimesAdapter;

    public static TimesFragment newInstance() {
        return new TimesFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_times, container, false);
        mTimesAdapter = new TimesAdapter(getContext(), mTimesRV);
        mTimesRV = view.findViewById(R.id.times_rv);
        scanFile();
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mTimesRV.setLayoutManager(layoutManager);
        mTimesRV.setAdapter(mTimesAdapter);

        mTimesRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    mTimesAdapter.startGallery(mTimesRV);
                } else {
                    mTimesAdapter.stopGallery(mTimesRV);
                }
            }
        });

        mTimesRV.setOnTouchListener(this);
        mTimesAdapter.setOnItemClickListener(this);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        startGallery();
    }

    public void startGallery() {
        if (mTimesAdapter != null) {
            mTimesAdapter.startGallery(mTimesRV);
        }
    }

    public void stopGallery() {
        if (mTimesAdapter != null) {
            mTimesAdapter.stopGallery(mTimesRV);
        }
    }

    private void scanFile() {
        MediaScanner.scanFile(getContext(), "18740476836", MediaType.HYBIRD)
                .subscribe(new Action1<TreeMap<String, List<Media>>>() {
                    @Override
                    public void call(TreeMap<String, List<Media>> stringListTreeMap) {
                        mTimesAdapter.updateData(stringListTreeMap);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {}
                });
    }

    @Override
    public void onItemClick(int position, String date) {
        TimesDetailActivity.startActivity(getContext(), date);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        mTimesAdapter.stopGallery(mTimesRV);
        return false;
    }

    @Override
    public void onPause() {
        super.onPause();
        stopGallery();
    }
}
