package kr.hs.yii.make.eyecover.screenfilter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.SeekBar;

import kr.hs.yii.make.eyecover.R;
import kr.hs.yii.make.eyecover.services.ScreenfilterService;
import kr.hs.yii.make.eyecover.utils.Utility;

public class ScreenFilterActivity extends AppCompatActivity {

    private SeekBar brightnessSeekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_filter);
        brightnessSeekBar = (SeekBar)findViewById(R.id.brightnessBar);
        brightnessSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                int val = seekBar.getProgress();
                Log.d("ScreenFilterActivity","birghtness changed to "+val);
                Intent brightnessChangeIntent = new Intent(getApplicationContext(), ScreenfilterService.class);
                brightnessChangeIntent.putExtra(Utility.EXTRA_ACTION,Utility.ACTION_BRIGHTNESS_CHANGE);
                brightnessChangeIntent.putExtra(Utility.EXTRA_BRIGHTNESS,val);
                startService(brightnessChangeIntent);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}
