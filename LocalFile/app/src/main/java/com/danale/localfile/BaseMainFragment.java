package com.danale.localfile;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Description :
 * CreateTime : 2018/1/22 10:45
 *
 * @author ningerlei@danale.com
 * @version <v1.0>
 * @Editor : Administrator
 * @ModifyTime : 2018/1/22 10:45
 * @ModifyDescription :
 */

public class BaseMainFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_media, container, false);
        TextView textView = (TextView) view.findViewById(R.id.des);
        textView.setText(getArguments().getString("des"));
        return view;
    }

    public static BaseMainFragment newInstance(String des) {
        BaseMainFragment mediaFragment = new BaseMainFragment();
        final Bundle args = new Bundle();
        args.putString("des", des);
        mediaFragment.setArguments(args);
        return mediaFragment;
    }

    public void select() {}
}
