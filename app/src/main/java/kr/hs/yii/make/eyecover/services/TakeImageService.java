package kr.hs.yii.make.eyecover.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.camera2.params.Face;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import kr.hs.yii.make.eyecover.receiver.EyecoverBroadcastReceiver;
import kr.hs.yii.make.eyecover.utils.Utility;

/**
 * Created by parkjongheum on 11/08/2017.
 */

@Deprecated
public class TakeImageService extends Service implements SurfaceHolder.Callback {

    private Camera mCamera;
    private Camera.Parameters parameters;
    private Bitmap bmp;
    FileOutputStream fo;
    private Camera.Size pictureSize;
    SurfaceView sv;
    private SurfaceHolder sHolder;
    private WindowManager windowManager;
    WindowManager.LayoutParams params;
    public Intent cameraIntent;
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    private TakeImage takeImage;

    // handler for make fault toast massage "failed to open camera"
    Handler handler = new Handler();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(!Utility.isEyecoverEnabled)
            stopSelf();

        takeImage = new TakeImage();
        cameraIntent = intent;
        Log.d("TakeImageService","onStartCommand()");
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        editor = pref.edit();

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.width = 1;
        params.height = 1;
        params.x = 0;
        params.y = 0;
        sv = new SurfaceView(getApplicationContext());

        windowManager.addView(sv,params);
        sHolder = sv.getHolder();
        sHolder.addCallback(this);

        return START_STICKY;
    }

    Camera.PictureCallback mCall = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            Log.d("TakeImageService","Done");
            if(bmp != null)
                bmp.recycle();
            //call garbage collector for memory optimization
            System.gc();
            bmp = decodeBitmap(data);
            bmp = Utility.rotateImage(bmp);
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG,70,bytes);
            // write image to application temp folder
            try {
                fo = openFileOutput("facedetect.jpg", Context.MODE_PRIVATE);
                fo.write(bytes.toByteArray());
                Log.d("TakeImageService","PictureCallback: File Written to temp folder");
                fo.close();
            } catch(Exception e){
                Log.e("TakeImageService","PictureCallback: Error",e);
                e.printStackTrace();
            }
            if(mCamera != null){
                mCamera.stopPreview();
                mCamera.release();
                mCamera = null;
            }

            Log.d("TakeImageService","Successfully Taken");
            if(bmp != null){
                bmp.recycle();
                bmp = null;
                System.gc();
            }
            mCamera = null;
            sendFaceDetectBroadcast(data);
        }
    };

    private void sendFaceDetectBroadcast(byte[] data){
        Intent FaceDetectBroadcast = new Intent();
        FaceDetectBroadcast.setAction(EyecoverBroadcastReceiver.NAME);
        FaceDetectBroadcast.putExtra(EyecoverBroadcastReceiver.ACTION,EyecoverBroadcastReceiver.ACTION_FACE_DETECT);
        sendBroadcast(FaceDetectBroadcast);
    }

    private Camera openFrontFacingCamera(){
        if(mCamera != null){
            mCamera.stopPreview();
            mCamera.release();
        }
        Camera cam = null;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        int cameraCount = Camera.getNumberOfCameras();
        for(int camIdx=0; camIdx<cameraCount; camIdx++){
            Camera.getCameraInfo(camIdx,cameraInfo);
            if(cameraInfo.facing ==  Camera.CameraInfo.CAMERA_FACING_FRONT){
                try {
                    cam = Camera.open(camIdx);
                } catch (RuntimeException e){
                    Log.e("TakeImageService","openFrontFacingCamera: Camera failed to open : " + e.getLocalizedMessage());
                }
            }
        }
        return cam;
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

    /**
     * check if this device has camera hardware
     * @param context
     * @return true if camera presented
     */
    private boolean checkCameraHardware(Context context){
        if(context.getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)){
            Log.d("TakeImageService","CheckCamera: Camera found");
            return true;
        } else {
            return false;
        }
    }

    /**
     * check if this device has front camera hardware
     * @param context
     * @return true if front camera presented
     */
    private boolean checkFrontCamera(Context context){
        if(context.getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA_FRONT)){
            return true;
        } else {
            return false;
        }
    }

    private class TakeImage extends AsyncTask<Intent, Void, Void>{
        @Override
        protected Void doInBackground(Intent... intents) {
            takeImage(intents[0]);
            return null;
        }
    }

    private synchronized void takeImage(Intent intent) {
        if (checkCameraHardware(getApplicationContext()) && checkFrontCamera(getApplicationContext())) {
            mCamera = openFrontFacingCamera();
            if (mCamera != null) {
                try {
                    mCamera.setPreviewDisplay(sv.getHolder());
                } catch (IOException e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "API doesn't support front camera",
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                    stopSelf();
                }
                parameters = mCamera.getParameters();
                pictureSize = getBiggestPictureSize(parameters);
                if (pictureSize != null) {
                    parameters.setPictureSize(pictureSize.width, pictureSize.height);
                }

                mCamera.setParameters(parameters);
                mCamera.startPreview();
                mCamera.takePicture(null, null, mCall);
            } else {
                mCamera = null;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplication(),
                                "Your device doesn't have front camera!",
                                Toast.LENGTH_LONG).show();
                    }
                });
                stopSelf();
            }
        } else {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(),
                            "Your device doesn't have a camera!",
                            Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        /* Intent faceDetectBroadcast = new Intent();
        faceDetectBroadcast.setAction(EyecoverBroadcastReceiver.NAME);
        faceDetectBroadcast.putExtra(EyecoverBroadcastReceiver.ACTION,EyecoverBroadcastReceiver.ACTION_FACE_DETECT);
        sendBroadcast(faceDetectBroadcast); */
        Log.d("TakeImageService","onDestroy");
        takeImage.cancel(false);
        if(mCamera != null){
            mCamera.stopPreview();;
            mCamera.release();
            mCamera = null;
        }
        if(sv != null)
            windowManager.removeView(sv);
        super.onDestroy();
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        if(cameraIntent != null)
            takeImage.execute(cameraIntent);
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2){

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        if(mCamera != null){
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    public static Bitmap decodeBitmap(byte[] data){
        Bitmap bitmap = null;
        BitmapFactory.Options bfOptions = new BitmapFactory.Options();
        bfOptions.inDither = true;
        bfOptions.inPurgeable = true;
        bfOptions.inInputShareable = true;
        bfOptions.inTempStorage = new byte[32*1024];

        if(data != null){
            bitmap = BitmapFactory.decodeByteArray(data,0,data.length,bfOptions);
        }
        return bitmap;
    }
}
