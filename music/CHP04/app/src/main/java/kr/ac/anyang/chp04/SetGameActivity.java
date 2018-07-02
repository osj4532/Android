package kr.ac.anyang.chp04;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;

import kr.ac.anyang.chp04.GameSurfaceView;
import kr.ac.anyang.chp04.R;

/**
 * Created by Administrator on 2018-04-23.
 */

public class SetGameActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener, View.OnClickListener {
    int iR = 0, iG = 0, iB = 0;
    String sBall;
    int iMusic;
    int[] iTitleMusic = new int[4];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        // 화면에서 위에 타이틀바, 상태바 없애기
        // res 에서 styles.xml에 <item name="windowNoTitle">true</item> 추가
        setTheme(android.R.style.Theme_NoTitleBar_Fullscreen);
        // 장치의 너비와 높이 구하기
        DisplayMetrics m_DisplayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(m_DisplayMetrics);

        setContentView(R.layout.activity_gameset);

        Button btnListen = (Button) findViewById(R.id.btnListen);
        btnListen.setOnClickListener((View.OnClickListener) this);
        Button btnStop = (Button) findViewById(R.id.btnStop);
        btnStop.setOnClickListener((View.OnClickListener) this);

        SeekBar sbRed = (SeekBar) findViewById(R.id.sbRed);
        sbRed.setOnSeekBarChangeListener((SeekBar.OnSeekBarChangeListener) this);
        SeekBar sbGreen = (SeekBar) findViewById(R.id.sbGreen);
        sbGreen.setOnSeekBarChangeListener((SeekBar.OnSeekBarChangeListener) this);
        SeekBar sbBlue = (SeekBar) findViewById(R.id.sbBlue);
        sbBlue.setOnSeekBarChangeListener((SeekBar.OnSeekBarChangeListener) this);

        Button btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                //intent.putExtra("COLOR_RED", String.valueOf(iR));
                //intent.putExtra("COLOR_GREEN", String.valueOf(iG));
                //intent.putExtra("COLOR_BLUE", String.valueOf(iB));
                intent.putExtra("BALL_COLOR", String.valueOf(Color.rgb(iR, iG, iB)));
                intent.putExtra("BALL_MANY", sBall);
                intent.putExtra("SELECTED_MUSIC", String.valueOf(iTitleMusic[iMusic]));
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        this.iTitleMusic[0] = R.raw.kalimba;
        this.iTitleMusic[1] = R.raw.maid_with_the_flaxen_hair;
        this.iTitleMusic[2] = R.raw.sleep_away;
        this.iTitleMusic[3] = R.raw.a;

        String[] sTitleMusic = new String[4];
        sTitleMusic[0] = "Kalimba"; sTitleMusic[1] = "Maid with the Flaxen Hair"; sTitleMusic[2] = "Sleep Away";
        sTitleMusic[3] = "a";

        Spinner spinner1 = (Spinner) findViewById(R.id.spMusic);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, sTitleMusic);
        spinner1.setAdapter(adapter1);
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                iMusic = pos; //pos는 위치
            }

            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        Spinner spinner2 = (Spinner) findViewById(R.id.spEasy);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this, R.array.easy_array, 	android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter2);
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                sBall = parent.getItemAtPosition(pos).toString();
            }

            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }

    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.btnListen:
                Intent intent = new Intent(this, GameMusicService.class);
                intent.putExtra("SELECTED_MUSIC", this.iTitleMusic[this.iMusic]);
                startService(intent);
                break;
            case R.id.btnStop:
                stopService(new Intent(this, GameMusicService.class));
                break;
        }
    }

    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId())
        {
            case R.id.sbRed:
                this.iR = progress;
                break;
            case R.id.sbGreen:
                this.iG = progress;
                break;
            case R.id.sbBlue:
                this.iB = progress;
                break;
        }

        Button btnBC = (Button) findViewById(R.id.btnBC);
        btnBC.setBackgroundColor(Color.rgb(this.iR, this.iG, this.iB));
    }

    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    public void onStartTrackingTouch(SeekBar seekBar) {
    }
}
