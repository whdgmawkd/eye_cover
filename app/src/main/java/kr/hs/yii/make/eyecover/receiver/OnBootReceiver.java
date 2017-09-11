package kr.hs.yii.make.eyecover.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import kr.hs.yii.make.eyecover.services.NotificationService;

/**
 * Run Application on Boot.
 * Make Notification and restore previous status(Screenfilter and eyecover)
 */

public class OnBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent onBootIntent = new Intent(context,NotificationService.class);
        context.startService(onBootIntent);
        Log.i("onBootReceiver","NotiService Started");
    }
}
