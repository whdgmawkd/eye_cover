package kr.hs.yii.make.eyecover.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import kr.hs.yii.make.eyecover.services.TakeImageService;
import kr.hs.yii.make.eyecover.utils.Utility;

/**
 * Deprecated Receiver.
 * Using this MAKE TakeImageService NEVER taking picture.
 */

@Deprecated
public class EyecoverDetectReceiver extends BroadcastReceiver {

    public final static String ACTION_EYECOVER_STATUS = "kr.hs.yii.make.eyecover.ACTION_EYECOVER_STATUS";

    private AlarmManager am;
    private PendingIntent pi;

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.hasExtra(Utility.EXTRA_EYECOVER)){
            if(intent.getStringExtra(Utility.EXTRA_EYECOVER).equals(Utility.ACTION_EYECOVER_START)){
                Log.d("EyecoverReciever","send intent");
                //mTask.execute(context);
            }else{
                //mTask.cancel(false);
            }
        }

    }

    private static AsyncTask<Context,Void,Void> mTask = new AsyncTask<Context, Void, Void>() {
        @Override
        protected Void doInBackground(Context... contexts) {
            Intent faceDetectIntent = new Intent(contexts[0], TakeImageService.class);
            faceDetectIntent.putExtra("TAKE_SHOT",true);
            PendingIntent pi = PendingIntent.getService(contexts[0],0,faceDetectIntent,0);
            while(true){
                contexts[0].startService(faceDetectIntent);
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(isCancelled())
                    return null;
            }
        }
    };
}
