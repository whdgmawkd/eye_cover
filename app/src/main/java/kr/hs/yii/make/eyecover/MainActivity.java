package kr.hs.yii.make.eyecover;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import kr.hs.yii.make.eyecover.receiver.TileReceiver;
import kr.hs.yii.make.eyecover.screenfilter.ScreenfilterService;
import kr.hs.yii.make.eyecover.services.NotificationService;
import kr.hs.yii.make.eyecover.utils.Utility;

public class MainActivity extends AppCompatActivity {

    private ImageButton eyecoverBtn;
    private Button scBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scBtn = (Button)findViewById(R.id.btn_screenfilter);
        scBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("MainActivity", "callScreenFilterService");
                Intent scfIntent = new Intent(getApplicationContext(), ScreenfilterService.class);
                scfIntent.putExtra(getString(R.string.intent_screenfilter), true);
                getApplicationContext().startService(scfIntent);
                Intent i = new Intent();
                i.setAction(TileReceiver.ACTION_UPDATE_STATUS);
                i.putExtra(Utility.EXTRA_ACTION,Utility.ACTION_START);
                sendBroadcast(i);
            }
        });
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getApplicationContext().startService(new Intent(this,NotificationService.class));
        eyecoverBtn = (ImageButton)findViewById(R.id.eyecoverMainButton);
        eyecoverBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),EyecoverActivity.class));
            }
        });
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
}
