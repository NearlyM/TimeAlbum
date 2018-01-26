package album;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.danale.localfile.BaseMainFragment;
import com.danale.localfile.FileExplore;
import com.danale.localfile.LocalFile;
import com.danale.localfile.R;
import com.danale.localfile.constant.MediaType;
import com.danale.localfile.util.ContextUtils;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import java.util.Arrays;
import java.util.List;

import album.adapter.ViewPagerAdapter;

/**
 * Description :
 * CreateTime : 2018/1/19 14:22
 *
 * @author ningerlei@danale.com
 * @version <v1.0>
 * @Editor : Administrator
 * @ModifyTime : 2018/1/19 14:22
 * @ModifyDescription :
 */

public class AlbumFragment extends BaseMainFragment {

    private static final String[] CHANNELS = new String[]{"时光", "照片", "视频"};
    private List<String> mDataList = Arrays.asList(CHANNELS);
    private ViewPager mViewPager;
    TimesFragment timesFragment;

    public static AlbumFragment newInstance() {
        return  new AlbumFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_album, container, false);
        mViewPager = (ViewPager) view.findViewById(R.id.view_pager);
        LocalFile.init("18740476836");
        initViewPager();
        initMagicIndicator(view);
        return view;
    }

    private void initViewPager() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        timesFragment = TimesFragment.newInstance();
        adapter.addFragment(timesFragment);
        adapter.addFragment(FileExplore.newInstance(new FileExplore.Selector.Builder().setMediaType(MediaType.IMAGE).build()));
        adapter.addFragment(FileExplore.newInstance(new FileExplore.Selector.Builder().setMediaType(MediaType.RECORD).build()));
        mViewPager.setAdapter(adapter);
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position == 0) {
                    timesFragment.startGallery();
                } else {
                    timesFragment.stopGallery();
                }
            }
        });
    }

    @Override
    public void select() {
        super.select();
        timesFragment.startGallery();
    }

    private void initMagicIndicator(View view) {
        MagicIndicator magicIndicator = (MagicIndicator) view.findViewById(R.id.magic_indicator);
        CommonNavigator commonNavigator = new CommonNavigator(getContext());
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return mDataList == null ? 0 : mDataList.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                SimplePagerTitleView simplePagerTitleView = new ColorTransitionPagerTitleView(context);
                simplePagerTitleView.setText(mDataList.get(index));
                simplePagerTitleView.setNormalColor(Color.parseColor("#a1a1a1"));
                simplePagerTitleView.setSelectedColor(Color.BLACK);
                simplePagerTitleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                simplePagerTitleView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mViewPager.setCurrentItem(index);
                    }
                });
                return simplePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                indicator.setColors(Color.parseColor("#40c4ff"));
                indicator.setXOffset(ContextUtils.dp2px(context, 5));
                indicator.setMode(LinePagerIndicator.MODE_WRAP_CONTENT);
                return indicator;
            }
        });
        magicIndicator.setNavigator(commonNavigator);
        LinearLayout titleContainer = commonNavigator.getTitleContainer(); // must after setNavigator
        titleContainer.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
//        titleContainer.setDividerPadding(UIUtil.dip2px(getContext(), 10));
        titleContainer.setDividerDrawable(getResources().getDrawable(R.drawable.simple_splitter));
        ViewPagerHelper.bind(magicIndicator, mViewPager);

    }
}
