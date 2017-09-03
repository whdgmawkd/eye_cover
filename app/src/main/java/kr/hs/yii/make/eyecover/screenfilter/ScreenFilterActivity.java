package kr.hs.yii.make.eyecover.screenfilter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;

import kr.hs.yii.make.eyecover.R;
import kr.hs.yii.make.eyecover.services.ScreenfilterService;
import kr.hs.yii.make.eyecover.utils.Utility;

public class ScreenFilterActivity extends AppCompatActivity {

    private SeekBar brightnessSeekBar;
    private Switch screenfilterSwitch;
    private SharedPreferences mPref;
    private SharedPreferences.Editor mPrefEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_filter);
        mPref = PreferenceManager.getDefaultSharedPreferences(this);
        mPrefEditor = mPref.edit();
        brightnessSeekBar = (SeekBar)findViewById(R.id.brightnessBar);
        brightnessSeekBar.setProgress(mPref.getInt(getString(R.string.pref_screenfilter_brightness),0));
        screenfilterSwitch = (Switch) findViewById(R.id.screenfilter_switch);
        if(!Utility.isScreenFilterEnabled) {
            brightnessSeekBar.setEnabled(false);
            screenfilterSwitch.setChecked(false);
        }else{
            brightnessSeekBar.setEnabled(true);
            screenfilterSwitch.setChecked(true);
        }

        screenfilterSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Intent changeScreenFilterState = new Intent(getApplicationContext(),ScreenfilterService.class);
                if(b){
                    changeScreenFilterState.putExtra(Utility.EXTRA_ACTION,Utility.ACTION_START);
                    changeScreenFilterState.putExtra(Utility.EXTRA_BRIGHTNESS,100-brightnessSeekBar.getProgress());
                    brightnessSeekBar.setEnabled(true);
                    startService(changeScreenFilterState);
                } else {
                    changeScreenFilterState.putExtra(Utility.EXTRA_ACTION,Utility.ACTION_STOP);
                    brightnessSeekBar.setEnabled(false);
                    startService(changeScreenFilterState);
                }
            }
        });
        brightnessSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                int val = seekBar.getProgress();
                Log.d("ScreenFilterActivity","birghtness changed to "+val);
                Intent brightnessChangeIntent = new Intent(getApplicationContext(), ScreenfilterService.class);
                brightnessChangeIntent.putExtra(Utility.EXTRA_ACTION,Utility.ACTION_BRIGHTNESS_CHANGE);
                brightnessChangeIntent.putExtra(Utility.EXTRA_BRIGHTNESS,100-val);
                startService(brightnessChangeIntent);
                mPrefEditor.putInt(getString(R.string.pref_screenfilter_brightness),val);
                mPrefEditor.apply();
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
