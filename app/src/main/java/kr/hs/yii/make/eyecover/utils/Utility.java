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

    // for screenfilter
    public static final String ACTION_START = "START";
    public static final String ACTION_STOP = "STOP";

    // common intent key
    public static final String EXTRA_ACTION = "EXTRA_ACTION";

    // intent actions
    public static final String ACTION_BRIGHTNESS_CHANGE = "CHANGE_BRIGHTNESS";
    public static final String EXTRA_BRIGHTNESS = "EXTRA_BRIGHTNESS";
    public static final String EXTRA_EYECOVER = "EXTRA_EYECOVER";
    public static final String ACTION_EYECOVER_START = "START";
    public static final String ACTION_EYECOVER_STOP = "STOP";

    // for popup intent actions
    public static final String EXTRA_POPUP_STATE = "EXTRA_POPUP_STATE";
    public static final String EXTRA_POPUP_ENABLE = "ENABLE_POPUP";
    public static final String EXTRA_POPUP_DISABLE = "DISABLE_POPUP";

    // boolean for check status
    public static boolean isScreenFilterEnabled = false;
    public static boolean isPopupEnabled = false;
    public static boolean isEyecoverEnabled = false;

    public static int getTrueScreenHeight(Context context) {
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        display.getRealMetrics(dm);

        return dm.heightPixels;
    }

    public static int getTrueScreenWidth(Context context) {
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        display.getRealMetrics(dm);

        return dm.widthPixels;
    }
}
