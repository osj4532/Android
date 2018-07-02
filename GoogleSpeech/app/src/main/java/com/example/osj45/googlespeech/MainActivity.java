package com.example.osj45.googlespeech;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

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
    private final int REQ_CODE_SPEECH_INPUT = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.txtLight = findViewById(R.id.txtLight);
        this.txtAccelerormeter = findViewById(R.id.txtAccelerormeter);
        this.txtProximity =  findViewById(R.id.txtProximity);
        this.txtOrientation =  findViewById(R.id.txtOrientation);

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
        //구글 스피치
        Button voiceButton =  findViewById(R.id.btnVoice);
        voiceButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

                try {
                    startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
                } catch (ActivityNotFoundException a) {
                    Toast.makeText(getApplicationContext(), "Not Supported", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    // Toast.makeText(getApplicationContext(), result.get(0), Toast.LENGTH_LONG).show();

                    if (result.get(0).equals("카메라 켜")) {
                        mCam = Camera.open();
                        Camera.Parameters p = mCam.getParameters();
                        p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                        mCam.setParameters(p);
                        mCam.startPreview();
                    }
                    else if (result.get(0).equals("카메라 꺼")) {
                        mCam.stopPreview();
                        mCam.release();
                        mCam = null;
                    }
                    else if (result.get(0).equals("사진")) {
                        try {
                            SurfaceView surfaceView = new SurfaceView(getApplicationContext());

                            mCam = Camera.open();
                            mCam.setPreviewDisplay(surfaceView.getHolder());
                            Camera.Parameters p = mCam.getParameters();
                            p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                            mCam.setParameters(p);
                            mCam.startPreview();

                            mCam.takePicture(null, null, mCall);
                            Thread.sleep(1000);

                            SurfaceHolder surfaceHolder = surfaceView.getHolder();
                            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        } catch (InterruptedException e2) {
                            e2.printStackTrace();
                        }
                    }
                }
                break;
            }
        }
    }

    Camera.PictureCallback mCall = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            FileOutputStream outStream = null;

            try{
                File sdFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath());  // Environment.getExternalStorageDirectory(), "A"

                if(!sdFile.exists()) sdFile.mkdirs();

                Calendar cal = Calendar.getInstance();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
                String strFileName = (simpleDateFormat.format(cal.getTime()));

                outStream = new FileOutputStream(sdFile + "/Camera/" + strFileName + ".jpg");
                outStream.write(data);
                outStream.close();

                mCam.startPreview();
                mCam.stopPreview();
                mCam.release();
                mCam = null;

                Toast.makeText(getApplicationContext(), sdFile.getPath(), Toast.LENGTH_LONG).show();
            } catch (FileNotFoundException e){
                Log.d("CAM", e.getMessage());
            } catch (IOException e){
                Log.d("CAM", e.getMessage());
            }
        }
    };

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
