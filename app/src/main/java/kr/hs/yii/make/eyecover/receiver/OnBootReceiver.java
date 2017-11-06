package kr.hs.yii.make.eyecover.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import kr.hs.yii.make.eyecover.R;
import kr.hs.yii.make.eyecover.services.EyecoverService;
import kr.hs.yii.make.eyecover.services.NotificationService;

/**
 * this broadcast receiver restores last eyecover status.
 */

public class OnBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("onBootReceiver","Restore Previous Eyecover Status");
        SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(context);
        Intent eyecoverIntent = new Intent(context, EyecoverService.class);
        if(mPref.getBoolean(context.getString(R.string.pref_last_eyecover_status),false)){
            eyecoverIntent.putExtra(EyecoverService.STATUS, true);
        } else {
            eyecoverIntent.putExtra(EyecoverService.STATUS, false);
        }
        context.startService(eyecoverIntent);
    }
}
