package com.danale.localfile.util;

import com.danale.localfile.bean.Media;

import java.util.LinkedList;

/**
 * Description :
 * CreateTime : 2018/1/22 16:38
 *
 * @author ningerlei@danale.com
 * @version <v1.0>
 * @Editor : Administrator
 * @ModifyTime : 2018/1/22 16:38
 * @ModifyDescription :
 */

public class DataCache {
    private static volatile DataCache instance;
    private DataCache() {}

    public static DataCache getInstance () {
        if (instance == null) {
            synchronized (DataCache.class) {
                if (instance == null) {
                    instance = new DataCache();
                }
            }
        }
        return instance;
    }

    public LinkedList<Media> cachedMedias = new LinkedList<>();

    public String mUsername;
}
