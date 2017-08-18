package kr.hs.yii.make.eyecover.screenfilter;

import android.animation.Animator;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityManager;

import kr.hs.yii.make.eyecover.MainActivity;
import kr.hs.yii.make.eyecover.R;
import kr.hs.yii.make.eyecover.receiver.TileReceiver;
import kr.hs.yii.make.eyecover.utils.Utility;

import static android.view.WindowManager.LayoutParams;

public class ScreenfilterService extends Service {

    private WindowManager mWindowManager;
    private NotificationManager mNotificationManager;
    private AccessibilityManager mAccessibilityManager;

    private Notification mNoti;

    private View mLayout;
    private WindowManager.LayoutParams mLayoutParams;

    private int mode = 0;

    private boolean isShowing = false;

    private static final int ANIMATE_DURATION_MILES = 250;
    private static final int NOTIFICATION_NO = 1024;
    private static int brightness = 80;



    private static final String TAG = ScreenfilterService.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();

        mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mAccessibilityManager = (AccessibilityManager) getSystemService(Context.ACCESSIBILITY_SERVICE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        destroyMaskView();
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(MainActivity.class.getCanonicalName());
        broadcastIntent.putExtra("event_id", 2);
        sendBroadcast(broadcastIntent);
    }

    private void createMaskView() {
        mAccessibilityManager.isEnabled();

        updateLayoutParams(mode, -1);
        mLayoutParams.gravity = Gravity.CENTER;

        if (mLayout == null) {
            mLayout = new View(this);
            mLayout.setLayoutParams(
                    new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                    )
            );
            mLayout.setBackgroundColor(Color.BLACK);
            mLayout.setAlpha(0f);
        }

        try {
            mWindowManager.addView(mLayout, mLayoutParams);
        } catch (Exception e) {
            e.printStackTrace();
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction(MainActivity.class.getCanonicalName());
            broadcastIntent.putExtra("event_id", 1);
            sendBroadcast(broadcastIntent);
        }
    }

    private void updateLayoutParams(int mode, int paramInt) {
        if (mLayoutParams == null) {
            mLayoutParams = new LayoutParams();
        }
        this.mAccessibilityManager.isEnabled();
        mLayoutParams.type = LayoutParams.TYPE_SYSTEM_OVERLAY;

        int max = Math.max(Utility.getTrueScreenWidth(this), Utility.getTrueScreenHeight(this));
        mLayoutParams.height = mLayoutParams.width = max + 200;
        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        mLayoutParams.format = PixelFormat.TRANSPARENT;
        float targetAlpha = (100 - brightness) * 0.01f;
        if (paramInt != -1) {
            if (isShowing) {
                if (Math.abs(targetAlpha - mLayout.getAlpha()) < 0.1f) {
                    mLayout.setAlpha(targetAlpha);
                } else {
                    mLayout.animate().alpha(targetAlpha).setDuration(100).start();
                }
            } else {
                mLayout.animate().alpha(targetAlpha).setDuration(ANIMATE_DURATION_MILES).start();
            }
        }

        if (mLayout != null) {
            mLayout.setBackgroundColor(Color.argb(220, 200, 100, 0));
        }
    }

    private float constrain(float paramFloat1, float paramFloat2, float paramFloat3) {
        if (paramFloat1 < paramFloat2) {
            return paramFloat2;
        }
        if (paramFloat1 > paramFloat3) {
            return paramFloat3;
        }
        return paramFloat1;
    }

    private void destroyMaskView() {
        isShowing = false;
        cancelNotification();
        if (mLayout != null) {
            mLayout.animate()
                    .alpha(0f)
                    .setDuration(ANIMATE_DURATION_MILES)
                    .setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animator) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animator) {
                            try {
                                mWindowManager.removeViewImmediate(mLayout);
                                mLayout = null;
                            } catch (Exception e) {

                            }
                        }

                        @Override
                        public void onAnimationCancel(Animator animator) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animator) {

                        }
                    });
        }
    }

    private void createNotification() {
        Log.i(TAG, "Create running notification");
        Intent openIntent = new Intent(this, MainActivity.class);
        Intent pauseIntent = new Intent();
        pauseIntent.setAction(TileReceiver.ACTION_UPDATE_STATUS);
        Log.i(TAG, "Create "+Utility.ACTION_STOP+" action");
        pauseIntent.putExtra(Utility.EXTRA_ACTION, Utility.ACTION_STOP);

        Notification.Action pauseAction = new Notification.Action(
                R.drawable.ic_info_black_24dp,
                "Stop",
                PendingIntent.getBroadcast(getBaseContext(), 0, pauseIntent, Intent.FILL_IN_DATA)
        );

        mNoti = new Notification.Builder(getApplicationContext())
                .setContentTitle("ScreenFilter")
                .setContentText("ScreenFilter is running")
                .setSmallIcon(R.drawable.ic_notification)
                .addAction(pauseAction)
                .setContentIntent(PendingIntent.getActivity(getApplicationContext(), 0, openIntent, PendingIntent.FLAG_UPDATE_CURRENT))
                .setAutoCancel(false)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setShowWhen(false)
                .build();

    }

    private void cancelNotification() {
        try {
            mNotificationManager.cancel(NOTIFICATION_NO);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int arg) {
        if (intent != null && intent.hasExtra(Utility.EXTRA_ACTION)) {
            String action = intent.getStringExtra(Utility.EXTRA_ACTION);


            switch (action) {
                case Utility.ACTION_START:
                    Log.i(TAG, "Start Mask");
                    if (mLayout == null){
                        createMaskView();
                    }

                    createNotification();
                    startForeground(NOTIFICATION_NO, mNoti);
                    try {
                        updateLayoutParams(mode, brightness);
                        mWindowManager.updateViewLayout(mLayout, mLayoutParams);
                    } catch (Exception e) {
                        // do nothing....
                        e.printStackTrace();
                    }
                    isShowing = true;
                    break;
                case Utility.ACTION_STOP:
                    Log.i(TAG, "Stop Mask");
                    isShowing = false;
                    stopSelf();
                    break;

            }

        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    private MaskBinder mBinder = new MaskBinder();

    public class MaskBinder extends Binder {

        public boolean isMaskShowing() {
            return isShowing;
        }

    }

}
