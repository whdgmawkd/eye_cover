package kr.hs.yii.make.eyecover.services;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.SparseArray;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import kr.hs.yii.make.eyecover.R;
import kr.hs.yii.make.eyecover.utils.Utility;

/**
 * Using google mobile vision's face detector.
 * this service is called after TakeImageService. and send detected face's width to EyecoverPopupService.
 */

public class FaceDetectService extends Service {

    private FaceDetector mFaceDetector;
    private Frame faceImage;
    private File imagePath;
    private Bitmap faceImageBitmap;
    private SparseArray<Face> faces;
    private float faceWidth;
    private float prefFaceWidth;
    private SharedPreferences mPref;
    private Intent popupIntent = new Intent(getApplicationContext(),EyecoveryPopupService.class);

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(!Utility.isEyecoverEnabled)
            stopSelf();

        // open file for face detect
        imagePath = new File(getCacheDir(),"facedetect.jpg");
        // decode jpg file to bitmap.
        faceImageBitmap = BitmapFactory.decodeFile(imagePath.getAbsolutePath());
        // face detector setup.
        mFaceDetector = new FaceDetector.Builder(getApplicationContext())
                .setTrackingEnabled(false)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .build();
        // convert bitmap to face detect frame.
        faceImage = new Frame.Builder().setBitmap(faceImageBitmap).build();
        // detect face.
        faces = mFaceDetector.detect(faceImage);
        Face mFace = faces.valueAt(0);
        // get current face width
        faceWidth = mFace.getWidth();
        prefFaceWidth = getPrefFaceWidth();

        // compare current face width and saved face width.
        if(faceWidth < prefFaceWidth){
            // make warning popup
            popupIntent.putExtra(Utility.EXTRA_POPUP_STATE,Utility.EXTRA_POPUP_ENABLE);
        }else {
            // remove warning popup
            popupIntent.putExtra(Utility.EXTRA_POPUP_STATE,Utility.EXTRA_POPUP_DISABLE);
        }
        startService(popupIntent);

        return START_STICKY;
    }

    // return saved face width.
    private float getPrefFaceWidth(){
        mPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return mPref.getFloat(getString(R.string.pref_face_width),0);
    }
}
