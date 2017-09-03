package kr.hs.yii.make.eyecover.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;

import java.util.Objects;

import kr.hs.yii.make.eyecover.utils.Utility;

/**
 * Created by parkjongheum on 02/09/2017.
 */

public class EyecoveryPopupService extends Service {



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent.hasExtra(Utility.EXTRA_POPUP_STATE)){
            if(intent.getStringExtra(Utility.EXTRA_POPUP_STATE).equals(Utility.EXTRA_POPUP_ENABLE) && !Utility.isPopupEnable){
                // create popup
            } else if(intent.getStringExtra(Utility.EXTRA_POPUP_STATE).equals(Utility.EXTRA_POPUP_DISABLE) && Utility.isPopupEnable){
                // remove popup
            }
        }
        // todo : callback take image service to check distance
        return START_STICKY;
    }
}
