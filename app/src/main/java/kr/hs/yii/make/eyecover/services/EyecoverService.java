package kr.hs.yii.make.eyecover.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.IntDef;


/**
 * integrated eyecover service.
 */
public class EyecoverService extends Service {

    boolean isEnabled = false;
    public static final String STATUS = "kr.hs.yii.make.eyecover.services.EyecoverService.STATUS";

    public EyecoverService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    /**
     *
     * @param intent
     * @param flags
     * @param startId
     * @return START_REDELIVER_INTENT
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent.hasExtra(STATUS)){
            isEnabled = intent.getBooleanExtra(STATUS, false);
        }
        return START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // does not return communication channel
        return null;
    }
}
