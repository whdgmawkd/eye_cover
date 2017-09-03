package kr.hs.yii.make.eyecover.eyecover;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import kr.hs.yii.make.eyecover.R;
import kr.hs.yii.make.eyecover.receiver.EyecoverDetectReceiver;
import kr.hs.yii.make.eyecover.utils.Utility;

public class EyecoverActivity extends AppCompatActivity {

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
                Intent eyecoverBroadcast = new Intent();
                eyecoverBroadcast.setAction(EyecoverDetectReceiver.ACTION_EYECOVER_STATUS);
                eyecoverBroadcast.putExtra(Utility.EXTRA_EYECOVER,Utility.ACTION_EYECOVER_STOP);
                sendBroadcast(eyecoverBroadcast);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent eyecoverBroadcast = new Intent();
        eyecoverBroadcast.setAction(EyecoverDetectReceiver.ACTION_EYECOVER_STATUS);
        eyecoverBroadcast.putExtra(Utility.EXTRA_EYECOVER,Utility.ACTION_EYECOVER_START);
        sendBroadcast(eyecoverBroadcast);

        Toast.makeText(this, "EyecoverFaceDetect Broadcast Sended!", Toast.LENGTH_SHORT).show();
    }

}
