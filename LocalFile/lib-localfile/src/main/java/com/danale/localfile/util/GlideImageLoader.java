package com.danale.localfile.util;

import android.app.Activity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.danale.local.R;
import com.danale.localfile.util.BitmapUtil;
import com.danale.localfile.bean.ImageLoader;


public class GlideImageLoader implements ImageLoader {

    @Override
    public void displayImage(Activity activity, String path, ImageView imageView, int width, int height) {



        Glide.with(activity)                             //配置上下文
                .load(BitmapUtil.getRotatedUri(activity,path))      //设置图片路径(fix #8,文件名包含%符号 无法识别和显示)
                .apply(new RequestOptions().error(R.drawable.default_picture).placeholder(R.drawable.default_picture).diskCacheStrategy(DiskCacheStrategy.ALL))
                .into(imageView);
    }

    @Override
    public void clearMemoryCache() {
    }
}
