package kr.ac.anyang.chp04;

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
    private int m_rect_with = 200;
    private int m_easy;
    private Vector m_CoodX;
    private Vector m_CoodY;
    private Vector m_Order;
    // private Boolean m_isFirstTime = true;
    private int m_iStartIndex = -1;

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

        this.setOnTouchListener((View.OnTouchListener) this);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Paint pBackColor = new Paint();
        pBackColor.setColor(Color.BLUE);
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

    public void setEasy(int easy) {
        this.m_easy = easy;
        drawGameRect();
    }

    private void drawGameRect() {
        this.m_CoodX = new Vector();
        this.m_CoodY = new Vector();
        this.m_Order = new Vector();

        for (int i = 0; i < this.m_easy; i++) {
            this.m_CoodX.add((int) (Math.random() * this.m_device_width));
            this.m_CoodY.add((int) (Math.random() * this.m_game_height));

            if (i < (this.m_easy / 2)) this.m_Order.add(String.valueOf(i));
            else this.m_Order.add(String.valueOf(i - (this.m_easy / 2)));
        }
    }

    public boolean onTouch(View view, MotionEvent event) {
        float touchX;
        float touchY;
        int index = event.getActionIndex();
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_POINTER_DOWN:
                if (index == 1 && this.m_iStartIndex > -1) {
                    touchX = event.getX(index);
                    touchY = event.getY(index);
                    for (int i = 0; i < this.m_easy; i++) {
                        int iStartX = (int) this.m_CoodX.elementAt(i);
                        int iStartY = (int) this.m_CoodY.elementAt(i);
                        if ((touchX >= iStartX) && (touchX <= iStartX + this.m_rect_with))
                            if ((touchY >= iStartY) && (touchY <= iStartY + this.m_rect_with)) {
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

                if (this.m_iStartIndex > -1) Toast.makeText(this.context, "처음부터 다시 하세요!!!", Toast.LENGTH_SHORT).show();
                break;
            case MotionEvent.ACTION_POINTER_UP:
                this.m_iStartIndex = -1;
                break;
            case MotionEvent.ACTION_DOWN:
                touchX = event.getX();
                touchY = event.getY();
                for (int i = 0; i < this.m_easy; i++) {
                    int iStartX = (int) this.m_CoodX.elementAt(i);
                    int iStartY = (int) this.m_CoodY.elementAt(i);
                    if ((touchX >= iStartX) && (touchX <= iStartX + this.m_rect_with))
                        if ((touchY >= iStartY) && (touchY <= iStartY + this.m_rect_with)) {
                            this.m_iStartIndex = i;
                            break;
                        }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                this.m_iStartIndex = -1;
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        //invalidate();
        return true;
    }
}
