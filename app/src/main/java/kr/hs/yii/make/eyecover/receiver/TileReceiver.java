package kr.hs.yii.make.eyecover.receiver;

/**
 * Created by waned8673 on 17. 8. 18.
 */

    import android.content.BroadcastReceiver;
    import android.content.Context;
    import android.content.Intent;
    import android.os.Build;
    import android.util.Log;

    import kr.hs.yii.make.eyecover.screenfilter.ScreenfilterService;
    import kr.hs.yii.make.eyecover.utils.Utility;

public class TileReceiver extends BroadcastReceiver {

    public static final String ACTION_UPDATE_STATUS = "kr.hs.yii.make.eyecover.ACTION_UPDATE_STATUS";
    private static String TAG = "TileReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "received \"" + intent.getAction() + "\" action");
        if (ACTION_UPDATE_STATUS.equals(intent.getAction())) {
            String action = intent.getStringExtra(Utility.EXTRA_ACTION);
            Log.i(TAG, "handle \"" + action + "\" action");
            switch (action) {
                case Utility.ACTION_START:
                    Intent intent1 = new Intent(context, ScreenfilterService.class);
                    intent1.putExtra(Utility.EXTRA_ACTION, Utility.ACTION_START);
                    context.startService(intent1);
                    break;
                case Utility.ACTION_STOP:
                    Intent intent3 = new Intent(context, ScreenfilterService.class);
                    intent3.putExtra(Utility.EXTRA_ACTION, Utility.ACTION_STOP);
                    context.startService(intent3);
                    break;
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Intent tileUpdateIntent = new Intent(context, ScreenfilterService.class);
                tileUpdateIntent.putExtra(Utility.EXTRA_ACTION, action);
                context.startService(tileUpdateIntent);
            }
        }
    }

}