package kr.hs.yii.make.eyecover.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import kr.hs.yii.make.eyecover.services.NotificationService;

/**
 * 부팅시 서비스를 자동으로 시작하도록 합니다.
 */

public class OnBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent onBootIntent = new Intent(context,NotificationService.class);
        context.startService(onBootIntent);
        Log.i("onBootReceiver","NotiService Started");
    }
}
