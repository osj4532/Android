package com.example.osj45.touchex;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.Vector;

/**
 * Created by Administrator on 2018-03-27.
 */

public class GameView extends View implements View.OnTouchListener {
    private Context context;
    private float m_device_width;
    private float m_device_height;
    private float m_device_actionBar_height;
    private float m_game_height;
    private float m_loc_x_Bar;
    private float m_loc_y_Bar;
    private int m_easy;
    private Vector m_CoodX;
    private Vector m_CoodY;

    public GameView(Context context, int m_width, int m_height, int m_actionBarHeight) {
        super(context);
        setBackgroundColor(Color.YELLOW);

        this.context = context;
        // 게임 난이도
        this.m_easy = 2;
        // 장치의 너비와 높이
        this.m_device_width = m_width;
        this.m_device_height = m_height;
        this.m_device_actionBar_height = m_actionBarHeight;
        this.m_game_height = this.m_device_height - this.m_device_actionBar_height; // 실제 게임 뷰 높이 (액션바의 높이를 차감

        drawGameRect();

        this.setOnTouchListener(this);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.BLUE);

        for (int i = 0; i < this.m_easy; i++) {
            int iStartX = (int) this.m_CoodX.elementAt(i);
            int iStartY = (int) this.m_CoodY.elementAt(i);
            canvas.drawRect(iStartX, iStartY, iStartX + 100, iStartY + 100, paint);
        }
    }

    public void setEasy(int easy) {
        this.m_easy = easy;
        drawGameRect();
    }

    private void drawGameRect() {
        this.m_CoodX = new Vector();
        this.m_CoodY = new Vector();

        for (int i = 0; i < this.m_easy; i++) {
            this.m_CoodX.add((int) (Math.random() * this.m_device_width));
            this.m_CoodY.add((int) (Math.random() * this.m_game_height));
        }
    }

    public boolean onTouch(View view, MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                for(int i = 0; i< m_easy;i++){
                    if(touchX >= (int)m_CoodX.get(i) && touchX <= (int)m_CoodX.get(i)+100 ){
                        if(touchY >= (int)m_CoodY.get(i) && touchY <= (int)m_CoodY.get(i)+100){
                            m_CoodX.remove(i);
                            m_CoodY.remove(i);
                            m_easy--;
                            break;
                        }
                    }
                }
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        invalidate();
        return true;
    }
}