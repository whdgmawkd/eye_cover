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
 * Created by parkjongheum on 31/08/2017.
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
        imagePath = new File(getCacheDir(),"facedetect.jpg");
        faceImageBitmap = BitmapFactory.decodeFile(imagePath.getAbsolutePath());
        mFaceDetector = new FaceDetector.Builder(getApplicationContext())
                .setTrackingEnabled(false)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .build();
        faceImage = new Frame.Builder().setBitmap(faceImageBitmap).build();
        faces = mFaceDetector.detect(faceImage);
        Face mFace = faces.valueAt(0);
        faceWidth = mFace.getWidth();
        prefFaceWidth = getPrefFaceWidth();
        if(faceWidth < prefFaceWidth){
            popupIntent.putExtra(Utility.EXTRA_POPUP_STATE,Utility.EXTRA_POPUP_ENABLE);
        }else {
            popupIntent.putExtra(Utility.EXTRA_POPUP_STATE,Utility.EXTRA_POPUP_DISABLE);
        }
        startService(popupIntent);

        return START_STICKY;
    }

    private float getPrefFaceWidth(){
        mPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return mPref.getFloat(getString(R.string.pref_face_width),0);
    }
}
