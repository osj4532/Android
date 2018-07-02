package com.example.osj45.sensor;

import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private TextView txtLight;
    private TextView txtAccelerormeter;
    private TextView txtProximity;
    private TextView txtOrientation;
    private Camera mCam;
    private boolean isOneTime = true;
    private long mShakeTime = 0;
    private int mShakeCount = 0;
    private float mLeftRight = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.txtLight = (TextView) findViewById(R.id.txtLight);
        this.txtAccelerormeter = (TextView) findViewById(R.id.txtAccelerormeter);
        this.txtProximity = (TextView) findViewById(R.id.txtProximity);
        this.txtOrientation = (TextView) findViewById(R.id.txtOrientation);

        SensorManager mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        Sensor mLightSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        Sensor mAccelerormeterSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor mProximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        Sensor mOrientation = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

        if (mLightSensor == null) {
            this.txtLight.setText("No Light Sensor");
        }
        else {
            float max =  mLightSensor.getMaximumRange();
            // this.txtLight.setText("Max Reading(Lux): " + String.valueOf(max));
            mSensorManager.registerListener(mSensorEventListener, mLightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }

        if (mAccelerormeterSensor == null) {
            this.txtAccelerormeter.setText("No Accelerormeter Sensor");
        }
        else {
            mSensorManager.registerListener(mSensorEventListener, mAccelerormeterSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }

        if (mProximity == null) {
            this.txtProximity.setText("No Proximity Sensor");
        }
        else {
            mSensorManager.registerListener(mSensorEventListener, mProximity, SensorManager.SENSOR_DELAY_NORMAL);
        }

        if (mOrientation == null) {
            this.txtOrientation.setText("No Orientation Sensor");
        }
        else {
            mSensorManager.registerListener(mSensorEventListener, mOrientation, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCam != null) {
            this.mCam.stopPreview();
            this.mCam.release();
        }
    }

    SensorEventListener mSensorEventListener = new SensorEventListener() {
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            // TODO Auto-generated method stub
            if(event.sensor.getType()==Sensor.TYPE_LIGHT){
                float currentReading = event.values[0];
                txtLight.setText("Current Reading(Lux): " + String.valueOf(currentReading));

                if (currentReading <= 5 && isOneTime) {
                    mCam = Camera.open();
                    Camera.Parameters p = mCam.getParameters();
                    p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                    mCam.setParameters(p);
                    mCam.startPreview();

                    try {
                        Thread.sleep(5000);

                        mCam.stopPreview();
                        mCam.release();
                        mCam = null;
                        isOneTime = false;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } else if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                int SHAKE_SKIP_TIME = 500;
                float SHAKE_THRESHOLD_GRAVITY = 2.7F;

                float axisX = event.values[0];
                float axisY = event.values[1];
                float axisZ = event.values[2];

                float gravityX = axisX / SensorManager.GRAVITY_EARTH;
                float gravityY = axisY / SensorManager.GRAVITY_EARTH;
                float gravityZ = axisZ / SensorManager.GRAVITY_EARTH;

                Float f = gravityX * gravityX + gravityY * gravityY + gravityZ * gravityZ;
                double squaredD = Math.sqrt(f.doubleValue());
                float gForce = (float) squaredD;

                if (gForce > SHAKE_THRESHOLD_GRAVITY) {
                    long currentTime = System.currentTimeMillis();
                    if (mShakeTime + SHAKE_SKIP_TIME > currentTime) return;
                    mShakeTime = currentTime;
                    mShakeCount++;
                    txtAccelerormeter.setText("Shaking Count: " + String.valueOf(mShakeCount));
                }
            } else if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
                float distance = event.values[0];
                txtProximity.setText("Distance: " + String.valueOf(distance));
            } else if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
                String strResult = "방위각: " + event.values[0] + ", 피치 : " + event.values[1] + ", 롤 : " + event.values[2];
                float currentLeftRight = event.values[0];

                if (mLeftRight > 0) {
                    if (currentLeftRight > mLeftRight)
                        txtOrientation.setText("Right: " + strResult);
                    else
                        txtOrientation.setText("Left: " + strResult);
                    mLeftRight = currentLeftRight;
                }
                else
                    mLeftRight = currentLeftRight;
            }
        }
    };
}
