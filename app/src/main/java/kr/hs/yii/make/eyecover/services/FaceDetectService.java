package kr.hs.yii.make.eyecover.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.SparseArray;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.Landmark;

import java.io.File;
import java.io.FileNotFoundException;

import kr.hs.yii.make.eyecover.R;
import kr.hs.yii.make.eyecover.receiver.EyecoverBroadcastReceiver;
import kr.hs.yii.make.eyecover.utils.Utility;

/**
 * Google의 모바일 비전을 사용하는 얼굴인식 서비스 입니다
 * TakeImageService에 의하여 호출됩니다.
 * 사진 촬영에 성공하면 EyecoverPopypService를 호출합니다.
 */

public class FaceDetectService extends Service {

    // 얼굴 인식 처리를 위한 클래스
    private FaceDetector mFaceDetector;
    private Frame faceImage;
    private File imagePath;
    private Bitmap faceImageBitmap;
    private SparseArray<Face> faces;

    // 크기 비교를 위한 얼굴의 너비
    private float faceWidth;
    private float prefFaceWidth;

    // 설정되어 있는 값을 불러오기 위한 저장소 열기
    private SharedPreferences mPref;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.d("FaceDetectService","onCreate");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 기능이 중지된 경우 즉시 중단합니다.
        //if(!Utility.isEyecoverEnabled)
        //    stopSelf();

        Log.d("FaceDetectService","onStartCommand");
 /*
        // 얼굴 인식을 위한 사진 파일을 엽니다.
        imagePath = new File(getCacheDir(),"facedetect.jpg");
        // Jpg파일을 비트맵으로 변환합니다.
        faceImageBitmap = BitmapFactory.decodeFile(imagePath.getPath());
        Log.d("FaceDetect","imagePath = "+imagePath.getAbsolutePath());
        // 얼굴 인식기를 초기화 합니다
        */
        try {
            faceImageBitmap = BitmapFactory.decodeStream(openFileInput("facedetect.jpg"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        mFaceDetector = new FaceDetector.Builder(getApplicationContext())
                .setTrackingEnabled(false)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .build();
        // 비트맵을 얼굴인식 전용 프레임으로 변환합니다
        faceImage = new Frame.Builder().setBitmap(faceImageBitmap).build();
        // 얼굴 인식을 시작합니다
        faces = mFaceDetector.detect(faceImage);
        Log.d("FaceDetect",faces.size()+"");

        if(!mFaceDetector.isOperational()){
            Log.d("FaceDetect","FaceDetector not available");
        }

        for (int i = 0; i < faces.size(); ++i) {
            Face face = faces.valueAt(i);
            for (Landmark landmark : face.getLandmarks()) {
                int cx = (int) (landmark.getPosition().x);
                int cy = (int) (landmark.getPosition().y);
                Log.d("FaceDetect","x: "+cx+", y: "+cy);
            }
            faceWidth = face.getWidth();
        }
        // 설정해놓은 얼굴의 너비를 구합니다
        prefFaceWidth = getPrefFaceWidth();

        // 로그 표시
        Log.i("FaceDetectService","calculated width : " + faceWidth);
        // 팝업 표시 여부를 구분하기 위해 비교

        Intent PopupBroadcast = new Intent();
        PopupBroadcast.setAction(EyecoverBroadcastReceiver.NAME);
        PopupBroadcast.putExtra(EyecoverBroadcastReceiver.ACTION,EyecoverBroadcastReceiver.ACTION_POPUP);
        sendBroadcast(PopupBroadcast);

        return START_STICKY;
    }

    /**
     * 거리 측정을 위해 설정된 얼굴의 너비를 가져옵니다
     * @return 설정 저장소에 기록된 얼굴의 너비값
     */
    private float getPrefFaceWidth(){
        mPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return mPref.getFloat(getString(R.string.pref_face_width),0);
    }
}
