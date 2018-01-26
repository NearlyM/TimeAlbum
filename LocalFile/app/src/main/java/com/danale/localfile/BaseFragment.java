package com.danale.localfile;

import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * Description : <Content><br>
 * CreateTime : 16-9-8 下午4:17
 *
 * @author DaiWeiJie
 * @version <v1.0>
 * @Editor : DaiWeiJie
 * @ModifyTime : 16-9-8 下午4:17
 * @ModifyDescription : <Content>
 */
public abstract class BaseFragment extends android.support.v4.app.Fragment  {


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        //这样就会使用apk classloader加载
        if (savedInstanceState != null){
            savedInstanceState.setClassLoader(getClass().getClassLoader());
        }
        super.onCreate(savedInstanceState);
    }

}
