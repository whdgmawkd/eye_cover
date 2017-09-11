package kr.hs.yii.make.eyecover.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import kr.hs.yii.make.eyecover.R;

/**
 * Notification Service called by BootCompleteReceiver.
 * Supported Action : toggle screen filter, turn off eyecover
 */

public class NotificationService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        Log.i("NotiService","Service Started");

        NotificationCompat.Builder mNotiBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Test Notification")
                .setContentText("this is test notification from services");

        Notification mNoti = mNotiBuilder.build();
        mNoti.flags |= Notification.FLAG_AUTO_CANCEL;
        NotificationManager mNotiManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        mNotiManager.notify(getResources().getInteger(R.integer.notification_id),mNoti);
        Bundle extras = intent.getExtras();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO: 11/09/2017 Integrated Notification Service
        return START_STICKY;
    }
}
