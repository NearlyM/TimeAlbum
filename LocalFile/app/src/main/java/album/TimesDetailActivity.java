package album;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.danale.localfile.BaseActivity;
import com.danale.localfile.FileExplore;
import com.danale.localfile.R;
import com.danale.localfile.constant.MediaType;

/**
 * Description :
 * CreateTime : 2018/1/23 19:23
 *
 * @author ningerlei@danale.com
 * @version <v1.0>
 * @Editor : Administrator
 * @ModifyTime : 2018/1/23 19:23
 * @ModifyDescription :
 */

public class TimesDetailActivity extends BaseActivity {

    public static void startActivity(Context context, String dateDay) {
        Intent intent = new Intent();
        intent.setClass(context, TimesDetailActivity.class);
        intent.putExtra("dateDay", dateDay);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_detail);

        String dateDay = getIntent().getStringExtra("dateDay");
        final FragmentManager supportFragmentManager = getSupportFragmentManager();
        final FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragment, FileExplore.newInstance(new FileExplore.Selector.Builder().setMediaType(MediaType.HYBIRD).setDateDay(dateDay).build()));
        fragmentTransaction.commit();
    }
}
