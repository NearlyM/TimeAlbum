package com.danale.localfile;

import com.danale.localfile.util.DataCache;

/**
 * Description :
 * CreateTime : 2018/1/23 16:54
 *
 * @author ningerlei@danale.com
 * @version <v1.0>
 * @Editor : Administrator
 * @ModifyTime : 2018/1/23 16:54
 * @ModifyDescription :
 */

public class LocalFile {
    public static void init(String userName) {
        DataCache.getInstance().mUsername = userName;
    }
}
