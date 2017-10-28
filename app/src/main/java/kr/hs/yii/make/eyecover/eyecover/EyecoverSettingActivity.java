package kr.hs.yii.make.eyecover.eyecover;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import java.io.IOException;

import kr.hs.yii.make.eyecover.R;
import kr.hs.yii.make.eyecover.utils.Utility;

public class EyecoverSettingActivity extends AppCompatActivity implements View.OnClickListener, SurfaceHolder.Callback {
// TODO: 04/10/2017 https://github.com/googlesamples/android-Camera2Basic/blob/master/Application/src/main/java/com/example/android/camera2basic/Camera2BasicFragment.java

    private Button takePictureBtn;
    private TextView widthText;

    // Camera
    private Camera mCamera;

    private SurfaceView sv;
    private SurfaceHolder sHolder;

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eyecover_setting);

        // set actionbar back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // view assign
        widthText = (TextView)findViewById(R.id.faceWidthTextView);
        takePictureBtn = (Button)findViewById(R.id.takePictureButton);
        takePictureBtn.setOnClickListener(this);
        sv = (SurfaceView) findViewById(R.id.cameraPreview);
        // initialize srufaceview for camera preview
        getWindow().setFormat(PixelFormat.UNKNOWN);
        sHolder = sv.getHolder();
        sHolder.addCallback(this);
        sHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        // preference Manager
        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = pref.edit();
        // open camera
        mCamera = openFrontfacingCamera();
        if(mCamera != null){
            Camera.Parameters parameters = mCamera.getParameters();
            Camera.Size pictureSize = getBiggestPictureSize(parameters);
            if (pictureSize != null) {
                parameters.setPictureSize(pictureSize.width, pictureSize.height);
            }
            mCamera.setParameters(parameters);
        } else {
            Log.e("ECSettings","Failed to open Camera");
            widthText.setText("카메라를 열 수 없습니다. 잠시후 다시 시도하십시오.");
        }
    }

    private Camera.Size getBiggestPictureSize(Camera.Parameters parameters){
        Camera.Size result = null;
        for(Camera.Size size : parameters.getSupportedPictureSizes()){
            if(result == null){
                result = size;
            } else {
                int resultArea = result.width * result.height;
                int newArea = size.width * size.height;

                if(newArea > resultArea){
                    result = size;
                }
            }
        }
        return (result);
    }

    private Camera openFrontfacingCamera(){
        if(mCamera != null){
            mCamera.stopPreview();
            mCamera.release();
        }
        Camera cam = null;
        Camera.CameraInfo camInfo = new Camera.CameraInfo();
        int camCnt = Camera.getNumberOfCameras();
        for(int camIdx = 0; camIdx<camCnt; camIdx++){
            Camera.getCameraInfo(camIdx,camInfo);
            if(camInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT){
                try {
                    cam = Camera.open(camIdx);
                } catch (RuntimeException e){
                    Log.e("ECSettings","Failed to open camera : index " + camIdx);
                }
            }
        }
        return cam;
    }

    Camera.PictureCallback mCall = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] bytes, Camera camera) {
            Log.d("ECSettings","Take Picture Done");
            Bitmap faceImageBitmap = Utility.rotateImage(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
            FaceDetector mFaceDetector = new FaceDetector.Builder(getApplicationContext())
                    .setTrackingEnabled(false)
                    .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                    .build();
            Frame faceImage = new Frame.Builder().setBitmap(faceImageBitmap).build();

            SparseArray<Face> faces = mFaceDetector.detect(faceImage);

            if(!mFaceDetector.isOperational()){
                Log.d("ECSettings","not operational");
            }
            Log.d("ECSettings","Detecting faces");

            float faceWidth = 0;

            for(int i = 0; i< faces.size(); i++){
                Face face = faces.valueAt(i);
                faceWidth = face.getWidth();
                Log.d("ECSettings","Face width : "+ faceWidth);
            }
            editor.putFloat(getString(R.string.pref_face_width), faceWidth);
            Log.d("ECSettings","Store calculated face width : "+ faceWidth);
            editor.commit();
            editor.apply();
            widthText.setText(""+ faceWidth +"\n설정되었습니다.");
        }
    };

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch(id){
            case R.id.takePictureButton:
                mCamera.takePicture(null,null,mCall);
                takePictureBtn.setEnabled(false);
                break;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        mCamera.stopPreview();
        try {
            mCamera.setPreviewDisplay(surfaceHolder);
            int rotateDeg;
            if(Build.MANUFACTURER.equals("Samsung") || Build.MANUFACTURER.equals("LGE"))
                rotateDeg = 90;
            else
                rotateDeg = 270;
            mCamera.setDisplayOrientation(rotateDeg);
            mCamera.startPreview();
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        if(mCamera != null){
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }
}
