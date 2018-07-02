package kr.ac.anyang.chp04;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import java.util.Vector;

/**
 * Created by Administrator on 2018-04-09.
 */

public class GameSurfaceView extends SurfaceView implements SurfaceHolder.Callback, View.OnTouchListener {
    private GameThread m_thread;
    private Context context;
    private float m_device_width;
    private float m_device_height;
    private int m_rect_with = 200;
    private int m_easy;
    private Vector m_CoodX;
    private Vector m_CoodY;
    private Vector m_Order;
    private int m_iStartIndex = -1;
    private int m_ball_color = -1;

    public GameSurfaceView(Context context, int m_width, int m_height, int iColor, int iBall) {
        super(context);

        setBackgroundColor(Color.YELLOW);

        this.context = context;

        // 장치의 너비와 높이
        this.m_device_width = m_width;
        this.m_device_height = m_height;

        // 게임 난이도
        if (iBall < 1)
            this.m_easy = 2;
        else
            this.m_easy = iBall;

        this.m_ball_color = iColor;

        drawGameRect();

        this.setOnTouchListener((View.OnTouchListener) this);

        getHolder().addCallback(this);
        this.m_thread = new GameThread(getHolder(), this);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Paint pBackColor = new Paint();
        if (this.m_ball_color == -1)
            pBackColor.setColor(Color.BLUE);
        else
            pBackColor.setColor(this.m_ball_color);

        Paint pFontColor = new Paint();
        pFontColor.setTextSize(40);
        pFontColor.setColor(Color.WHITE);

        for (int i = 0; i < this.m_easy; i++) {
            int iStartX = (int) this.m_CoodX.elementAt(i);
            int iStartY = (int) this.m_CoodY.elementAt(i);
            String strText = String.valueOf(this.m_Order.elementAt(i));
            canvas.drawRect(iStartX, iStartY, iStartX + this.m_rect_with, iStartY + this.m_rect_with, pBackColor);
            canvas.drawText(strText, iStartX + 20, iStartY + 50, pFontColor);
        }
    }

    private void drawGameRect() {
        this.m_CoodX = new Vector();
        this.m_CoodY = new Vector();
        this.m_Order = new Vector();

        for (int i = 0; i < this.m_easy; i++) {
            this.m_CoodX.add((int) (Math.random() * this.m_device_width));
            this.m_CoodY.add((int) (Math.random() * this.m_device_height));

            if (i < (this.m_easy / 2)) this.m_Order.add(String.valueOf(i));
            else this.m_Order.add(String.valueOf(i - (this.m_easy / 2)));
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_POINTER_DOWN:
                break;
            case MotionEvent.ACTION_POINTER_UP:
                break;
            case MotionEvent.ACTION_DOWN:
                for (int i = 0; i < this.m_easy; i++) {
                    int iStartX = (int) this.m_CoodX.elementAt(i);
                    int iStartY = (int) this.m_CoodY.elementAt(i);
                    if ((touchX >= iStartX) && (touchX <= iStartX + this.m_rect_with))
                        if ((touchY >= iStartY) && (touchY <= iStartY + this.m_rect_with))
                        {
                            if (this.m_iStartIndex < 0) {
                                this.m_iStartIndex = i;
                                break;
                            }
                            else {
                                if (i != this.m_iStartIndex) {
                                    if (this.m_Order.elementAt(i).equals(this.m_Order.elementAt(m_iStartIndex))) {
                                        if (i < this.m_iStartIndex) {
                                            this.m_CoodX.remove(this.m_iStartIndex); this.m_CoodY.remove(this.m_iStartIndex); this.m_Order.remove(this.m_iStartIndex);
                                            this.m_CoodX.remove(i); this.m_CoodY.remove(i); this.m_Order.remove(i);
                                        } else {
                                            this.m_CoodX.remove(i); this.m_CoodY.remove(i); this.m_Order.remove(i);
                                            this.m_CoodX.remove(this.m_iStartIndex); this.m_CoodY.remove(this.m_iStartIndex); this.m_Order.remove(this.m_iStartIndex);
                                        }

                                        this.m_easy -= 2;
                                        this.m_iStartIndex = -1;
                                        invalidate();
                                    }
                                    else {
                                        this.m_iStartIndex = -1;
                                        Toast.makeText(this.context, "처음부터 다시 하세요!!!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return true;
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    public void surfaceCreated(SurfaceHolder holder) {
        this.m_thread.setRunning(true);
        this.m_thread.start();
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        this.m_thread.setRunning(false);
        while (retry) {
            try {
                this.m_thread.join();
                retry = false;
            } catch (InterruptedException e) {}
        }
    }
}