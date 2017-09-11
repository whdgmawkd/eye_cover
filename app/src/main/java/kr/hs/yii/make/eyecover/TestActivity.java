package kr.hs.yii.make.eyecover;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import kr.hs.yii.make.eyecover.services.NotificationService;

/*
 * Deprecated Activity. Will be removed.
 */

@Deprecated
public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

    }
}
