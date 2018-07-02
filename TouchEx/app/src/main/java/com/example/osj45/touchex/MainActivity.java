package com.example.osj45.touchex;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    GameView gView;

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

        // 장치의 ActionBar 또는 TitleBar 구하기
        int m_actionBarHeight = 0;
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
            m_actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
        this.gView = new GameView(this, m_DisplayMetrics.widthPixels, m_DisplayMetrics.heightPixels, m_actionBarHeight);
        setContentView(this.gView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // TODO Auto-generated method stub
        //menu.add(0, 0, 0, "난이도 상");
        //menu.add(0, 1, 1, "난이도 중");
        //menu.add(0, 2, 3, "난이도 하");

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.gamemenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch(item.getItemId()){
            case R.id.d1:
                Toast.makeText(MainActivity.this, "난이도 상", Toast.LENGTH_LONG).show();
                this.gView.setEasy(10);
                this.gView.invalidate();
                break;
            case R.id.d2:
                Toast.makeText(MainActivity.this, "난이도 중", Toast.LENGTH_LONG).show();
                this.gView.setEasy(6);
                this.gView.invalidate();
                break;
            case R.id.d3:
                Toast.makeText(MainActivity.this, "난이도 하", Toast.LENGTH_LONG).show();
                this.gView.setEasy(2);
                this.gView.invalidate();
                break;
        }
        return true;
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