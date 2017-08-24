package kr.hs.yii.make.eyecover;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import kr.hs.yii.make.eyecover.eyecover.EyecoverActivity;
import kr.hs.yii.make.eyecover.screenfilter.ScreenFilterActivity;
import kr.hs.yii.make.eyecover.services.NotificationService;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private CardView eyecoverCardview;
    private CardView usePatternCardview;
    private CardView screenFilterCardview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getApplicationContext().startService(new Intent(this,NotificationService.class));
        eyecoverCardview = (CardView)findViewById(R.id.eyeCoverCardview);
        usePatternCardview = (CardView)findViewById(R.id.usePatternCardview);
        screenFilterCardview = (CardView)findViewById(R.id.screenFilterCardview);
        eyecoverCardview.setOnClickListener(this);
        usePatternCardview.setOnClickListener(this);
        screenFilterCardview.setOnClickListener(this);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();

        if(id==R.id.menu_settings){
            startActivity(new Intent(this,SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main_menu, menu);
        return true;
    }

    @Override
    public void onClick(View view) {
        int viewID = view.getId();

        switch(viewID){
            case R.id.eyeCoverCardview:
                startActivity(new Intent(this,EyecoverActivity.class));
                break;
            case R.id.usePatternCardview:
                break;
            case R.id.screenFilterCardview:
                startActivity(new Intent(this, ScreenFilterActivity.class));
                break;
        }
    }
}
