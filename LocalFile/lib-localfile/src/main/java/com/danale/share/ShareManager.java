package com.danale.share;

/**
 * Description :
 * CreateTime : 2018/1/25 9:36
 *
 * @author ningerlei@danale.com
 * @version <v1.0>
 * @Editor : Administrator
 * @ModifyTime : 2018/1/25 9:36
 * @ModifyDescription :
 */

public class ShareManager {

    private static volatile ShareManager Instance;

    private ShareManager(){}

    public static ShareManager getInstance() {
        if (Instance == null) {
            synchronized (ShareManager.class) {
                if (Instance == null) {
                    Instance = new ShareManager();
                }
            }
        }
        return Instance;
    }
}
