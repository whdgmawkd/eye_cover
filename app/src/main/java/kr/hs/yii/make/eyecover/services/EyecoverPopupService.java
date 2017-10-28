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
import kr.hs.yii.make.eyecover.receiver.EyecoverBroadcastReceiver;
import kr.hs.yii.make.eyecover.utils.Utility;

/**
 * 눈가리개 경고 팝업을 표시하는 서비스 입니다.
 * FaceDetectService에 의하여 호출됩니다.
 */

public class EyecoverPopupService extends Service {

    // 화면 가장 위에 창을 띄우기 위한 기본 설정입니다.
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
        // 서비스가 처음 실행될 때 핵심 콤포넌트를 할당합니다.
        mWindowManager = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        mAccessibilityManager = (AccessibilityManager)getSystemService(Context.ACCESSIBILITY_SERVICE);
        inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
    }

    /**
     * 눈가리개 팝업 경고창을 생성하여 표시합니다.
     * @param popupType 팝업 형태를 지정합니다.
     */
    private void createPopupView(int popupType){
        // TODO: popupType에 맞춰 레이아웃 변경하는 기능이 필요함.
        // 얘 뭐하는지 나도 모르겠음 왜 접근성 관리자에서 확인하는지 검색해야함.
        mAccessibilityManager.isEnabled();
        // 팝업이 활성화 된것으로 설정합니다.
        Utility.isPopupEnabled = true;

        // 레이아웃 파라미터를 설정합니다.
        if(mLayoutParams == null)
            mLayoutParams = new WindowManager.LayoutParams();
        // "TYPE_SYSTEM_ALERT"는 다른 앱 위에 시스템 경고를 표시할 때 사용하므로 구현에 필요합니다.
        mLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        // 레이아웃의 크기를 기기의 화면에 맞춥니다.
        mLayoutParams.height = Resources.getSystem().getDisplayMetrics().heightPixels;
        mLayoutParams.width = Resources.getSystem().getDisplayMetrics().widthPixels;
        mLayoutParams.gravity = Gravity.CENTER;
        mLayoutParams.format = PixelFormat.TRANSPARENT;

        // 내부에 들어갈 레이아웃을 설정합니다.
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

        // 윈도우 매니저에 뷰를 추가합니다.
        try{
            mWindowManager.addView(mLayout,mLayoutParams);
        } catch (Exception e){
            e.printStackTrace();
        }

        // 내부 레이아웃의 닫기 버튼 로직
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

    /**
     * 표시하고 있던 팝업을 제거합니다.
     */
    private void destroyPopupView(){
        // TODO: 12/09/2017 레이아웃이 사라지는 애니메이션을 구현해야 함
        if(mLayout != null){
            Log.d("mLayout","called");
            mWindowManager.removeView(mLayout);
        }
        Utility.isPopupEnabled = false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 기능을 사용하지 않음으로 변경하면 서비스를 종료합니다.
        if(!Utility.isEyecoverEnabled) {
            Log.d("Eyecover","feature is not enabled");
            stopSelf();
        }

        Log.i("Eyecover","Service called");

        if(intent.hasExtra(Utility.EXTRA_POPUP_STATE)){
            Log.d("Eyecover","Found State");
            if(intent.getStringExtra(Utility.EXTRA_POPUP_STATE).equals(Utility.EXTRA_POPUP_ENABLE)){
                Log.d("Eyecover","Enable Popup");
                createPopupView(0);
            }
        } else if(Utility.isPopupEnabled) {
            destroyPopupView();
        }
        // 갱신을 위해 TakeImageService를 호출합니다.
        Intent takeImageBroadcast = new Intent();
        takeImageBroadcast.setAction(EyecoverBroadcastReceiver.NAME);
        takeImageBroadcast.putExtra(EyecoverBroadcastReceiver.ACTION,EyecoverBroadcastReceiver.ACTION_TAKE_IMAGE);
        sendBroadcast(takeImageBroadcast);
        // TODO: 12/09/2017 TakeImageService를 재 호출하여 얼굴 인식 결과 갱신
        return START_STICKY;
    }
}
