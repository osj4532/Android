package com.example.osj45.selfstudy;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by osj45 on 2018-05-08.
 */

public class GameView extends SurfaceView implements SurfaceHolder.Callback, View.OnTouchListener{
    private GameThread thread;
    private Context mContext;
    private int mDevice_width;
    private int mDevice_height;
    private Vector m_CoodX;
    private Vector m_CoodY;
    private int m_enemy_many;
    private int m_enemy_with;
    private int plyerX,plyerY;
    private Vector<Missile> mList;

    public GameView(Context context, int width, int height){
        super(context);
        mContext = context;
        mDevice_width = width;
        mDevice_height = height;

        init();

        this.setOnTouchListener(this);
    }

    private void init(){
        getHolder().addCallback(this);
        thread = new GameThread(getHolder(),this);

        m_enemy_many = 5;
        m_enemy_with = 100;

        this.m_CoodX = new Vector();
        this.m_CoodY = new Vector();

        for(int i = 0; i < m_enemy_many; i++){
            this.m_CoodX.add((int)(Math.random() * this.mDevice_width));
            this.m_CoodY.add((int)(this.mDevice_height*(0.1)));

            this.plyerX = this.mDevice_width/2;
            this.plyerY = (int)(this.mDevice_height*(0.9));
        }

        mList = new Vector<Missile>();

        setFocusable(true);
    }

    public void drawMissile(Canvas canvas){
        if(mList.size()==0)
            return;
        for(int i = 0 ;i<mList.size();i++){
            if(mList.get(i).move()){
                mList.remove(i);
            }
        }

        Bitmap missile = BitmapFactory.decodeResource(getResources(),R.drawable.laser);

        for(int i = 0; i < mList.size(); i++){
            canvas.drawBitmap(missile,mList.get(i).x-12,mList.get(i).y-100,new Paint());

        }
    }

    public void collisionInspection(){
        //적 미사일 충돌 검사
        for(int i = 0; i < m_enemy_many; i++){
            for(int j = 0; j < mList.size(); j++){
                int iStartX = (int) this.m_CoodX.elementAt(i);
                int iStartY = (int) this.m_CoodY.elementAt(i);

                if(mList.get(j).x > iStartX && mList.get(j).y > iStartY ){
                    if(mList.get(j).x < iStartX + m_enemy_with && mList.get(j).y < iStartY + m_enemy_with) {
                        m_CoodX.remove(i);
                        m_CoodY.remove(i);
                        m_enemy_many--;
                        mList.remove(j);
                    }
                }
            }
        }
    }

    public void doDraw(Canvas canvas){
        //배경 이미지
        Bitmap background = BitmapFactory.decodeResource(getResources(),R.drawable.background);
        background = Bitmap.createScaledBitmap(background,mDevice_width,mDevice_height, true);
        canvas.drawBitmap(background,0,0, new Paint());

        //플레이어
        Paint pColor = new Paint();
        pColor.setColor(Color.WHITE);

        //적
        Paint enemyColor = new Paint();
        enemyColor.setColor(Color.BLUE);

        //뱡향키 및 발사키
        Paint commandColor = new Paint();
        commandColor.setColor(Color.GRAY);

        for(int i = 0; i < this.m_enemy_many; i++){
            int iStartX = (int) this.m_CoodX.elementAt(i);
            int iStartY = (int) this.m_CoodY.elementAt(i);
            //적
            canvas.drawRect(iStartX, iStartY, iStartX + m_enemy_with,iStartY + m_enemy_with, enemyColor);
        }
        //플레이어
        canvas.drawRect(plyerX -25, plyerY -25,plyerX +25,plyerY +25, pColor);
        //방향키 및 발사키
        //좌
        canvas.drawCircle((float)(this.mDevice_width*0.1),(float)(this.mDevice_height*0.8),30,commandColor);
        //우
        canvas.drawCircle((float)(this.mDevice_width*0.2),(float)(this.mDevice_height*0.8),30,commandColor);
        //발사
        canvas.drawCircle((float)(this.mDevice_width*0.8),(float)(this.mDevice_height*0.8),60,commandColor);

        drawMissile(canvas);

        collisionInspection();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();
        switch(event.getActionMasked()){
            case MotionEvent.ACTION_POINTER_DOWN:
                break;
            case MotionEvent.ACTION_POINTER_UP:
                break;
            case MotionEvent.ACTION_DOWN:

                //좌 이동
                if(touchX >= this.mDevice_width*0.1 - 30 && touchY >=  this.mDevice_height*0.8 - 30){
                    if(touchX <= this.mDevice_width*0.1 + 30 && touchY <=  this.mDevice_height*0.8 + 30){
                        plyerX -= 20;
                    }
                }
                //우 이동
                if(touchX >= this.mDevice_width*0.2 - 30 && touchY >=  this.mDevice_height*0.8 - 30) {
                    if (touchX <= this.mDevice_width * 0.2 + 30 && touchY <= this.mDevice_height * 0.8 + 30) {
                        plyerX += 20;
                    }
                }

                //발사 버튼
                if(touchX >= this.mDevice_width*0.8 - 60 && touchY >=  this.mDevice_height*0.8 - 60) {
                    if (touchX <= this.mDevice_width * 0.8 + 60 && touchY <= this.mDevice_height * 0.8 + 60) {
                        if(mList.size() <11) {
                            mList.add(new Missile(plyerX, plyerY - 45));
                            Intent intent = new Intent(mContext, Shot.class);
                            mContext.startService(intent);
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

    public void surfaceChanged(SurfaceHolder holder, int arg1, int arg2, int arg3){

    }

    public void surfaceCreated(SurfaceHolder holder){
        thread.setRunning(true);
        thread.start();
    }

    public void surfaceDestroyed(SurfaceHolder holder){
        boolean retry = true;
        thread.setRunning(false);
        while(retry){
            try{
                thread.join();
                retry = false;
            }catch (InterruptedException e){
            }
        }
    }



    public class GameThread extends Thread{

        private SurfaceHolder mThreadSurfaceHolder;
        private GameView mThreadSurfaceView;
        private boolean myThreadRun = false;

        public GameThread(SurfaceHolder holder, GameView view){
            mThreadSurfaceHolder = holder;
            mThreadSurfaceView = view;
        }

        public void setRunning(boolean b){
            myThreadRun = b;
        }

        @Override
        public void run(){
            while(myThreadRun){
                Canvas canvas = null;
                try{
                    canvas = mThreadSurfaceHolder.lockCanvas(null);
                    synchronized (mThreadSurfaceHolder){
                        doDraw(canvas);
                    }
                }finally {
                    if(canvas != null){
                        mThreadSurfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
            }
        }
    }
}
