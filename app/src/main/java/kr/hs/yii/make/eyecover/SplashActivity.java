package kr.hs.yii.make.eyecover;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(!Settings.canDrawOverlays(this)){ // make alert to grant overlay permission
                Log.d(getPackageName(),"compatibility issue found.");
                AlertDialog.Builder mAlertBuilder = new AlertDialog.Builder(this);
                mAlertBuilder.setTitle(R.string.alert_overlay_grant_title)
                        .setMessage(R.string.alert_overlay_grant_msg)
                        .setPositiveButton(R.string.alert_overlay_grant_go_settings, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                                startActivityForResult(intent, 1234);
                            }
                        })
                        .show();
            }
            else{
                startMainActivity();
            }
        }else { // just start activity
            startMainActivity();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("SplashActivity","activity result found");
        if(requestCode==1234){
            Log.d("SplashActivity",""+resultCode);
            startMainActivity();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    void startMainActivity(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();
            }
        },1000L);
    }
}
