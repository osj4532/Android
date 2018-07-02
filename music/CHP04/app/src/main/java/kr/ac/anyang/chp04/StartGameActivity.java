package kr.ac.anyang.chp04;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

/**
 * Created by Administrator on 2018-04-23.
 */

public class StartGameActivity extends AppCompatActivity {
    GameSurfaceView m_SurfaceView;

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

        Intent intent = getIntent();
        int iColor = Integer.parseInt(intent.getStringExtra("BALL_COLOR"));
        int iBall = Integer.parseInt(intent.getStringExtra("BALL_MANY"));
        int iMusic = Integer.parseInt(intent.getStringExtra("SELECTED_MUSIC"));
        //엑티비티 하나에서 음악을 실행할때 아래 2줄만 사용하면 된다.(서피스 뷰전에 사용 전에)
        //Intent / startService
        Intent intentMusic = new Intent(this, GameMusicService.class);
        intentMusic.putExtra("SELECTED_MUSIC", iMusic);
        startService(intentMusic);

        this.m_SurfaceView = new GameSurfaceView(this, m_DisplayMetrics.widthPixels, m_DisplayMetrics.heightPixels, iColor, iBall);
        setContentView(this.m_SurfaceView);
    }

    @Override
    public void onBackPressed() {
        stopService(new Intent(this, GameMusicService.class));
        StartGameActivity.this.finish();
    }
}
