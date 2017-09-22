package kr.hs.yii.make.eyecover.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import kr.hs.yii.make.eyecover.services.EyecoverPopupService;
import kr.hs.yii.make.eyecover.services.FaceDetectService;
import kr.hs.yii.make.eyecover.services.TakeImageService;

/**
 * 각 서비스 결과에 따라 다음 서비스를 호출
 */

// TODO: 20/09/2017 모든 서비스의 호출을 startService 에서 sendBroadcast로 변경해야 함.

public class EyecoverBroadcastReceiver extends BroadcastReceiver {

    // 리시버 호출용 태그
    public static final String NAME = "kr.hs.yii.make.eyecover.EYECOVER_ACTION";

    // 리시버로 받아올 액션
    public static final String ACTION = "ACTION"
            ,ACTION_TAKE_IMAGE = "ACTION_TAKE_IMAGE"
            ,ACTION_FACE_DETECT = "ACTION_FACE_DETECT"
            ,ACTION_POPUP = "ACTION_POPUP"
            ,ACTION_HALT = "ACTION_HALT";

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d("EyecoverReceiver","onReceive");
        Log.d("EyecoverReceiver","Action : " + intent.getAction());
        Log.d("EyecoverReceiver","Extra : " + intent.getExtras().getString(ACTION));

        if(intent.getAction().equals(Intent.ACTION_BATTERY_LOW)){
            stopAllServices(context);
            return;
        }

        Intent callService = null;

        String action = intent.getExtras().getString(ACTION);
        switch(action){
            case ACTION_TAKE_IMAGE:
                callService = new Intent(context, TakeImageService.class);
                break;
            case ACTION_FACE_DETECT:
                callService = new Intent(context, FaceDetectService.class);
                break;
            case ACTION_POPUP:
                callService = new Intent(context, EyecoverPopupService.class);
                break;
            case ACTION_HALT:
                stopAllServices(context);
                break;
        }
        if(callService!=null)
            context.startService(callService);
    }

    private void stopAllServices(Context context){
        context.stopService(new Intent(context,TakeImageService.class));
        context.stopService(new Intent(context,FaceDetectService.class));
        context.stopService(new Intent(context,EyecoverPopupService.class));
    }
}
