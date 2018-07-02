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


/**
 * Created by Administrator on 2018-04-23.
 */

public class SetGameActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {
    int iR = 0, iG = 0, iB = 0;
    String sM;

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

        SeekBar sbRed = (SeekBar) findViewById(R.id.sbRed);
        sbRed.setOnSeekBarChangeListener((SeekBar.OnSeekBarChangeListener) this);
        SeekBar sbGreen = (SeekBar) findViewById(R.id.sbGreen);
        sbGreen.setOnSeekBarChangeListener((SeekBar.OnSeekBarChangeListener) this);
        SeekBar sbBlue = (SeekBar) findViewById(R.id.sbBlue);
        sbBlue.setOnSeekBarChangeListener((SeekBar.OnSeekBarChangeListener) this);

        //저장하기 버튼 이벤트
        Button btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("COLOR_RED", String.valueOf(iR));
                intent.putExtra("COLOR_GREEN", String.valueOf(iG));
                 intent.putExtra("COLOR_BLUE", String.valueOf(iB));

                intent.putExtra("BALL_MANY", sM);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        Spinner spinner = (Spinner) findViewById(R.id.spEasy);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.easy_array, 	android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                sM = parent.getItemAtPosition(pos).toString();
            }

            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }
    //시크바 이벤트
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
