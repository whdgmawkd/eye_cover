package kr.hs.yii.make.eyecover.screenfilter;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import kr.hs.yii.make.eyecover.R;

/**
 * Created by waned8673 on 17. 8. 18.
 */

public class ScreenfilterService extends Service{
    @Nullable
    @Override
    public IBinder onBind(Intent intent){
        return null;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        Bundle args = intent.getExtras();
        boolean isFilterEnable = args.getBoolean(getString(R.string.intent_screenfilter));
        Log.d("ScreenfilterService","Started");
        if(isFilterEnable){
            //todo Enable secreen filter
            Toast.makeText(getApplicationContext(), "screenfilter", Toast.LENGTH_LONG).show();
        }
        super.onStart(intent, startId);
    }
}
