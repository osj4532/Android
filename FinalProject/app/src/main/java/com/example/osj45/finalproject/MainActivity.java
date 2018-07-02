package com.example.osj45.finalproject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.util.List;


public class MainActivity  extends Activity implements SensorEventListener{

    private SensorManager sensorManager;
    private MyView myView;
    private static int deviceWidth, deviceHeight;
    private long shakeTime;
    private static final int SHAKE_SKIP_TIME = 500;
    private static final float SHAKE_THRESHOLD_GRAVITY = 2.7f;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 화면 높이 너비 구하기
        DisplayMetrics disp = getApplicationContext().getResources().getDisplayMetrics();
        deviceWidth = disp.widthPixels;
        deviceHeight = disp.heightPixels;


        Vibrator v = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        //뷰 설정
        myView = new MyView(this, deviceWidth, deviceHeight,v);
        setContentView(myView);

        //풀스크린
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //센서
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
        if(sensors.size() > 0){
            sensorManager.registerListener(this, sensors.get(0),
                    SensorManager.SENSOR_DELAY_GAME);
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(final Sensor sensor, int accuracy){

    }

    @Override
    public void onSensorChanged(final SensorEvent event){
        Sensor sensor = event.sensor;

        switch(sensor.getType()){
            case Sensor.TYPE_ACCELEROMETER :
                myView.move(event.values[0],event.values[1]);
                float axisX = event.values[0];
                float axisY = event.values[1];
                float axisZ = event.values[2];

                float gravityX = axisX / SensorManager.GRAVITY_EARTH;
                float gravityY = axisY / SensorManager.GRAVITY_EARTH;
                float gravityZ = axisZ / SensorManager.GRAVITY_EARTH;

                Float f = gravityX * gravityX + gravityY * gravityY + gravityZ * gravityZ;
                double squaredD = Math.sqrt(f.doubleValue());
                float gForce = (float) squaredD;
                if(gForce > SHAKE_THRESHOLD_GRAVITY){
                    long currentTime = System.currentTimeMillis();
                    if(shakeTime + SHAKE_SKIP_TIME > currentTime){
                        return;
                    }
                    shakeTime = currentTime;
                    myView.shake();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        myView.onPause();
        new AlertDialog.Builder(this)
                .setMessage("게임을 종료하시겠습니까?")
                .setCancelable(false)
                .setPositiveButton("예", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MainActivity.this.finish();
                    }
                })
                .setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        myView.onResume();
                    }
                })
                .show();
    }
}


