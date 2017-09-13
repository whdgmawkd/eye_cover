package kr.hs.yii.make.eyecover.eyecover;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import kr.hs.yii.make.eyecover.R;
import kr.hs.yii.make.eyecover.receiver.EyecoverDetectReceiver;
import kr.hs.yii.make.eyecover.services.TakeImageService;
import kr.hs.yii.make.eyecover.utils.Utility;

public class EyecoverActivity extends AppCompatActivity {

    // 기능 온오프 토글 스위치
    private ToggleButton eyecoverToggleButton;

    // 기능 온오프시 서비스에 보낼 Intent
    private Intent eyecoverServiceIntent = new Intent(this, TakeImageService.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eyecover);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // 토글 버튼 할당 및 상태에 따른 서비스 호출
        eyecoverToggleButton = (ToggleButton) findViewById(R.id.eyecover_toggle_button);
        eyecoverToggleButton.setChecked(Utility.isEyecoverEnabled);
        eyecoverToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    // Start TakeImageService to enable eyecover
                    Utility.isEyecoverEnabled = true;
                    startService(eyecoverServiceIntent);
                    Log.d("EyecoverActivity","Start TakeImageService");
                } else {
                    // Stop TakeImageService to disable eyecover
                    Utility.isEyecoverEnabled = false;
                    stopService(eyecoverServiceIntent);
                    Log.d("EyecoverActivity","Stop TakeImageService");
                }
            }
        });
    }

}
