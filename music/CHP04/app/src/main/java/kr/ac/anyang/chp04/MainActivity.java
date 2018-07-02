package kr.ac.anyang.chp04;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    static final int GET_STRING = 1;
    private int iBGColor = -1;
    private int iBall = -1;
    private int iMusic = -1;

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

        setContentView(R.layout.activity_main);

        Button btnSet = (Button) findViewById(R.id.btnSet);
        btnSet.setOnClickListener((View.OnClickListener) this);
        Button btnStart = (Button) findViewById(R.id.btnStart);
        btnStart.setOnClickListener((View.OnClickListener) this);
    }

    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.btnSet:
                Intent intent4set = new Intent(this, SetGameActivity.class);
                startActivityForResult(intent4set, GET_STRING);
                break;
            case R.id.btnStart:
                Intent intent4start = new Intent(MainActivity.this, StartGameActivity.class);
                intent4start.putExtra("BALL_COLOR", String.valueOf(this.iBGColor));
                intent4start.putExtra("BALL_MANY", String.valueOf(this.iBall));
                intent4start.putExtra("SELECTED_MUSIC", String.valueOf(this.iMusic));
                startActivity(intent4start);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GET_STRING) {
            if (resultCode == RESULT_OK) {
                //int iR = 0, iG = 0, iB = 0;
                //iR = Integer.parseInt(data.getStringExtra("COLOR_RED"));
                //iG = Integer.parseInt(data.getStringExtra("COLOR_GREEN"));
                //iB = Integer.parseInt(data.getStringExtra("COLOR_BLUE"));
                this.iBGColor = Integer.parseInt(data.getStringExtra("BALL_COLOR")); //Color.rgb(iR, iG, iB);
                this.iBall = Integer.parseInt(data.getStringExtra("BALL_MANY"));
                this.iMusic = Integer.parseInt(data.getStringExtra("SELECTED_MUSIC"));
            }
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("게임을 종료하시겠습니까?")
                .setCancelable(false)
                .setPositiveButton("예", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MainActivity.this.finish();
                    }
                })
                .setNegativeButton("아니요", null)
                .show();
    }
}
