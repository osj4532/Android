package com.example.osj45.chp04;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Administrator on 2018-03-27.
 */

public class GameView extends View implements View.OnTouchListener {
    private float m_device_width;
    private float m_device_height;
    private float m_loc_x_Bar;
    private float m_loc_y_Bar;

    public GameView(Context context, int m_width, int m_height) {
        super(context);
        setBackgroundColor(Color.YELLOW);

        // 장치의 너비와 높이
        this.m_device_width = m_width;
        this.m_device_height = m_height;
        // 바의 초기 위치
        this.m_loc_x_Bar = m_width / 2 - 200;
        this.m_loc_y_Bar = m_height - 80;

        this.setOnTouchListener((View.OnTouchListener) this);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.BLUE);
        canvas.drawRect(this.m_loc_x_Bar, this.m_loc_y_Bar, this.m_loc_x_Bar + 400, this.m_loc_y_Bar + 80, paint);
    }

    public boolean onTouch(View view, MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (touchX <= this.m_device_width / 2) this.m_loc_x_Bar = this.m_loc_x_Bar - 40;
                else this.m_loc_x_Bar = this.m_loc_x_Bar + 40;

                if(this.m_loc_x_Bar <= 0) this.m_loc_x_Bar = this.m_loc_x_Bar + 40;
                if(this.m_loc_x_Bar + 400 >= this.m_device_width) this.m_loc_x_Bar = this.m_loc_x_Bar - 40;

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
