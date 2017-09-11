package kr.hs.yii.make.eyecover.services;

import android.animation.Animator;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;

import java.util.Objects;

import kr.hs.yii.make.eyecover.R;
import kr.hs.yii.make.eyecover.utils.Utility;

/**
 * Make Eyecover Warning Popup.
 * this service is called after FaceDetectService.
 */

public class EyecoveryPopupService extends Service {

    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mLayoutParams;
    private AccessibilityManager mAccessibilityManager;
    private LayoutInflater inflater;

    private View mLayout;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Default Assignment
        mWindowManager = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        mAccessibilityManager = (AccessibilityManager)getSystemService(Context.ACCESSIBILITY_SERVICE);
        inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
    }

    private void createPopupView(int popupType){
        // TODO: 07/09/2017 set mLayout's view by popupType
        mAccessibilityManager.isEnabled();
        Utility.isPopupEnabled = true;

        if(mLayoutParams == null)
            mLayoutParams = new WindowManager.LayoutParams();
        // This Popup is "SYSTEM_ALERT". always displayed over other apps.
        mLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        // Fit Device Width and Height.
        mLayoutParams.height = Resources.getSystem().getDisplayMetrics().heightPixels;
        mLayoutParams.width = Resources.getSystem().getDisplayMetrics().widthPixels;
        mLayoutParams.gravity = Gravity.CENTER;
        mLayoutParams.format = PixelFormat.TRANSPARENT;

        // Inner Layout Inflate.
        if(mLayout == null){
            mLayout = new View(getApplicationContext());
            mLayout.setLayoutParams(
                    new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    )
            );
            mLayout = inflater.inflate(R.layout.alert_warning,null);
        }

        // Show Popup on Device.
        try{
            mWindowManager.addView(mLayout,mLayoutParams);
        } catch (Exception e){
            e.printStackTrace();
        }

        // Close Button Logic.
        Button btn = mLayout.findViewById(R.id.button4);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Close Popup and stop service.
                Log.d("POPUP","BTN ONCLICK");
                destroyPopupView();
                stopSelf();
            }
        });
    }

    private void destroyPopupView(){
        if(mLayout != null){
            Log.d("mLayout","called");
            mWindowManager.removeView(mLayout);
        }
        Utility.isPopupEnabled = false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(!Utility.isEyecoverEnabled)
            stopSelf();

        if(intent.hasExtra(Utility.EXTRA_POPUP_STATE)){
            if(intent.getStringExtra(Utility.EXTRA_POPUP_STATE).equals(Utility.EXTRA_POPUP_ENABLE) && !Utility.isPopupEnabled){
                createPopupView(0);
            } else if(intent.getStringExtra(Utility.EXTRA_POPUP_STATE).equals(Utility.EXTRA_POPUP_DISABLE) && Utility.isPopupEnabled){
                destroyPopupView();
            }
        }
        // Call TakeImageService to measure distance.
        Intent callback = new Intent(this, TakeImageService.class);
        // todo : callback take image service to check distance
        return START_STICKY;
    }
}
