package kr.hs.yii.make.eyecover.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import kr.hs.yii.make.eyecover.services.NotificationService;

/**
 * Created by parkjongheum on 14/08/2017.
 */

public class bootCompleteReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent onBootIntent = new Intent(context,NotificationService.class);
        context.startService(onBootIntent);
        Log.i("onBootReceiver","NotiService Started");
    }
}