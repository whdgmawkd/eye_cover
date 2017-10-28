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

    // 필터 세기 조절을 위한 SeekBar
    private SeekBar brightnessSeekBar;
    // 필터 기능 On/Off를 위한 스위치
    private Switch screenfilterSwitch;
    // 밝기 저장을 위한 설정 저장소
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

        // 액티비티 시작 시 스크린 필터 활성화 여부를 확인하여 스위치를 설정합니다.
        if(!Utility.isScreenFilterEnabled) {
            brightnessSeekBar.setEnabled(false);
            screenfilterSwitch.setChecked(false);
        }else{
            brightnessSeekBar.setEnabled(true);
            screenfilterSwitch.setChecked(true);
        }
        // 스위치 상태가 변경되면 필터를 시작하거나 종료합니다.
        screenfilterSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Intent changeScreenFilterState = new Intent(getApplicationContext(),ScreenfilterService.class);
                if(b){
                    // Start ScreenFilter Service
                    changeScreenFilterState.putExtra(Utility.EXTRA_ACTION,Utility.ACTION_START);
                    changeScreenFilterState.putExtra(Utility.EXTRA_BRIGHTNESS,100-brightnessSeekBar.getProgress());
                    brightnessSeekBar.setEnabled(true);
                    startService(changeScreenFilterState);
                } else {
                    // Stop ScreenFilter Service
                    changeScreenFilterState.putExtra(Utility.EXTRA_ACTION,Utility.ACTION_STOP);
                    brightnessSeekBar.setEnabled(false);
                    startService(changeScreenFilterState);
                }
            }
        });

        // 필터 강도를 변경합니다.
        brightnessSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                // Change ScreenFilter's brightness
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
