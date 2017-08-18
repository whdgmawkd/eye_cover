package kr.hs.yii.make.eyecover.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;

import java.util.Calendar;

@SuppressWarnings("unchecked")
public class Utility {

    public static final String ACTION_START = "1";
    public static final String ACTION_STOP = "2";
    public static final String EXTRA_ACTION = "EXTRA_ACTION";

    public static final int CM_TILE_CODE = 1001;

    public static final int REQUEST_ALARM_SUNRISE = 1002, REQUEST_ALARM_SUNSET = 1003;

    public static final String TAG = Utility.class.getSimpleName();

    public static int getTrueScreenHeight(Context context) {
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        display.getRealMetrics(dm);
        int dpi = dm.heightPixels;

        return dpi;
    }

    public static int getTrueScreenWidth(Context context) {
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        display.getRealMetrics(dm);
        int dpi = dm.widthPixels;

        return dpi;
    }

    public static float dpToPx(Context context, float dp) {
        Resources r = context.getResources();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
    }

}
