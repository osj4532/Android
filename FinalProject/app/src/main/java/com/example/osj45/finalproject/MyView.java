package com.example.osj45.finalproject;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Vibrator;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by osj45 on 2018-06-06.
 */

public class MyView extends SurfaceView implements SurfaceHolder.Callback {

    private MyThread thread;
    private Context context;
    private int deviceWidth, devieceHeight;
    private float x, y;
    private final static int PLYER_WIDTH = 30;
    private int score, stage = 1;
    private Vibrator vibrator;
    private int tmp = 1;
    private boolean fin = false;
    Random rand;
    int BLOCK_NUM = 200;
    int count = 0;
    List<Block> blocks;
    private boolean pause = false;
    private SurfaceHolder hold;
    private TimerTask tt = null;
    private int taskcount = 0;

    public MyView(Context context, int w, int h, Vibrator v) {
        super(context);

        this.context = context;
        getHolder().addCallback(this);
        deviceWidth = w;
        devieceHeight = h;
        thread = new MyThread(getHolder(),this);

        rand = new Random();

        blocks = new ArrayList<Block>();

        level();
        vibrator = v;

        hold = getHolder();
    }

    public void level(){
        fin = false;
        count = taskcount;
        tt = new TimerTask() {
            @Override
            public void run() {

                ArrayList<Integer> arr = new ArrayList<Integer>();
                int c = 0;
                for(int i = 0; i < 100; i++){
                    arr.add(c);
                    c+=100;
                    if(c >= deviceWidth*0.9)
                        break;
                }

                count++;
                taskcount = count;
                blocks.add(new Block(arr.get(rand.nextInt(arr.size())), 40, deviceWidth, devieceHeight));
                if (count == BLOCK_NUM) {
                    cancel();
                    count = 0;
                    taskcount = count;
                    fin = true;
                }
            }
        };
        Timer timer = new Timer();
        timer.schedule(tt, 0, 300);
    }

    //플레이어 이동
    public void move(float mx, float my){
        x -= (mx * 4f);
        y += (my * 4f);
        if((x - PLYER_WIDTH) < 0){
            x = PLYER_WIDTH;
        }else if((x + PLYER_WIDTH) > deviceWidth){
            x = deviceWidth - PLYER_WIDTH;
        }
        if((y - PLYER_WIDTH) < 60){
            y = PLYER_WIDTH + 60;
        }else if((y + PLYER_WIDTH) > devieceHeight){
            y = devieceHeight - PLYER_WIDTH;
        }
    }

    public void shake(){
        if(fin)
            stage++;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh){
        deviceWidth = w;
        devieceHeight = h;
        x = (w - PLYER_WIDTH) / 2f;
        y = (h - PLYER_WIDTH) / 2f;
    }

    //그리기
    protected void doDraw(Canvas canvas){
        canvas.drawColor(Color.WHITE);



        Paint plyer = new Paint(Paint.ANTI_ALIAS_FLAG);
        plyer.setColor(Color.RED);
        canvas.drawCircle(x,y,PLYER_WIDTH,plyer);


        Paint block = new Paint(Paint.ANTI_ALIAS_FLAG);
        block.setColor(Color.BLUE);


        Paint txt = new Paint(Paint.ANTI_ALIAS_FLAG);
        txt.setColor(Color.BLACK);
        txt.setTextSize(50);
        canvas.drawLine(0,60,deviceWidth,60,txt);


        canvas.drawText("Score : " + score,10,50,txt);
        canvas.drawText("Stage : " + stage ,deviceWidth-250,50,txt);

        for(int i = 0; i < blocks.size(); i++) {
            canvas.drawRect(blocks.get(i).x,blocks.get(i).y,blocks.get(i).x+Block.Block_Width,blocks.get(i).y+Block.Block_Width,block);
        }
        //왼쪽 위 충돌
        for(int i = 0; i < blocks.size(); i++){
            if(blocks.get(i).x < x-PLYER_WIDTH && blocks.get(i).x + Block.Block_Width > x-PLYER_WIDTH/2){
                if(blocks.get(i).y < y-PLYER_WIDTH  && blocks.get(i).y + Block.Block_Width > y){
                    score-=1;
                    if(score < 0)
                        score = 0;
                    vibrator.vibrate(500);
                }
            }
        }

        //오른쪽 위 충돌
        for(int i = 0; i < blocks.size(); i++){
            if(blocks.get(i).x < x+PLYER_WIDTH/2 && blocks.get(i).x + Block.Block_Width > x+PLYER_WIDTH){
                if(blocks.get(i).y < y-PLYER_WIDTH && blocks.get(i).y + Block.Block_Width > y){
                    score-=1;
                    if(score < 0)
                        score = 0;
                    vibrator.vibrate(500);
                }
            }
        }

        //왼쪽 아래 충돌
        for(int i = 0; i < blocks.size(); i++){
            if(blocks.get(i).x < x-PLYER_WIDTH && blocks.get(i).x + Block.Block_Width > x-PLYER_WIDTH/2){
                if(blocks.get(i).y < y  && blocks.get(i).y + Block.Block_Width > y + PLYER_WIDTH){
                    score-=1;
                    if(score < 0)
                        score = 0;
                    vibrator.vibrate(500);
                }
            }
        }

        //오른쪽 아래 충돌
        for(int i = 0; i < blocks.size(); i++){
            if(blocks.get(i).x < x+PLYER_WIDTH/2 && blocks.get(i).x + Block.Block_Width > x+PLYER_WIDTH){
                if(blocks.get(i).y < y && blocks.get(i).y + Block.Block_Width > y+PLYER_WIDTH){
                    score-=1;
                    if(score < 0)
                        score = 0;
                    vibrator.vibrate(500);
                }
            }
        }

        for(int i = 0; i < blocks.size(); i++){
            if(blocks.get(i).move()) {
                blocks.remove(i);
                score++;
            }
        }

        if(stage > tmp){
            level();
            tmp = stage;
        }

        txt.setColor(Color.BLACK);
        txt.setTextSize(30);
        if(fin){
            canvas.drawText("핸드폰을 흔들면 "+(stage+1)+"스테이지로 넘어갑니다.",10,100,txt);
        }

    }

    //서피스뷰 시작시
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        thread.setRunning(true);
        thread.start();
    }
    //서피스뷰 변경시
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }
    //서피스뷰 종료시
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        thread.setRunning(false);
        try{
            thread.join();
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
   public void onPause(){
        tt.cancel();
        synchronized (hold){
            pause = true;
        }
   }

   public void onResume(){
       if(!fin)
            level();
       synchronized (hold){
           pause = false;
           hold.notifyAll();
       }
   }

    //서피스뷰 스레드
    public class MyThread extends Thread{
        private boolean isRunning = false;
        private SurfaceHolder myHolder;
        private MyView myView;

        public MyThread(SurfaceHolder holder, MyView view){
            myHolder = holder;
            myView = view;
        }
        public void setRunning(boolean b){
            isRunning = b;
        }
        @Override
        public void run(){
            Canvas canvas;
            while(isRunning){
                canvas = null;
                try{
                    canvas = myHolder.lockCanvas();
                    synchronized (myHolder) {
                        myView.doDraw(canvas);
                        while(pause){
                            try{
                                myHolder.wait();
                            }catch (InterruptedException e){}
                        }
                    }
                }finally {
                    if(canvas != null){
                        myHolder.unlockCanvasAndPost(canvas);
                    }
                }
            }
        }


    }
}
