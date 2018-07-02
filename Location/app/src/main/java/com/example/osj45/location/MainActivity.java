package com.example.osj45.location;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Vibrator;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    Context c;
    TextView txtBattery;
    TextView txtMLocation;
    TextView txtULocation;
    Vibrator vibrator;
    LocationManager locationManager;
    String strCurrentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        c = this;
        txtBattery = findViewById(R.id.txtBattery);
        txtMLocation =  findViewById(R.id.txtMLocation);
        txtULocation =  findViewById(R.id.txtULocation);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        Button btnSendMessage = findViewById(R.id.btnSend);
        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toast.makeText(getApplicationContext(), "메시지", Toast.LENGTH_SHORT).show();
                EditText etPhoneNumber = findViewById(R.id.txtPhoneNumber);
                if (etPhoneNumber.getText().toString() != null || etPhoneNumber.getText().toString() != "") {
                    String strPhoneNumber = etPhoneNumber.getText().toString();
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(strPhoneNumber, null, "위치정보요청", null, null);
                }
            }
        });

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        intentFilter.addAction(Intent.ACTION_BATTERY_LOW);

        registerReceiver(mBroadcastServer, intentFilter);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListener);
        //0,0 : 시간, 거리
        // 보통 이렇게는 안쓴다 0,0은 베터리 소모가 크다
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastServer);
        stopService(new Intent(c, MusicService.class));
        vibrator.cancel();
        this.locationManager.removeUpdates(this.mLocationListener);
    }

    public String getContactList(String strUPhoneNumber) {
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] projection = new String[] {
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,          // 연락처 ID -> 사진 정보 가져오는데 사용
                ContactsContract.CommonDataKinds.Phone.NUMBER,               // 연락처
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME };      // 연락처 이름.
        String[] selectionArgs = null;
        String sortOrder = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " COLLATE LOCALIZED ASC";
        Cursor contactCursor = this.getContentResolver().query(uri, projection, null,selectionArgs, sortOrder);
        ArrayList contactlist = new ArrayList();
        if (contactCursor.moveToFirst()) {
            do {
                String phonenumber = contactCursor.getString(1).replaceAll("-","");
                if (strUPhoneNumber.equals(phonenumber)) return contactCursor.getString(2);
                if (phonenumber.length() == 10) {
                    phonenumber = phonenumber.substring(0, 3) + ""
                            + phonenumber.substring(3, 6) + ""
                            + phonenumber.substring(6);
                } else if (phonenumber.length() > 8) {
                    phonenumber = phonenumber.substring(0, 3) + ""
                            + phonenumber.substring(3, 7) + ""
                            + phonenumber.substring(7);
                }
                // ID, Phone Number, Name
                contactlist.add(contactCursor.getLong(0) + ":" + phonenumber + ":" + contactCursor.getString(2));
            } while (contactCursor.moveToNext());
        }
        return "NoName";
    }

    BroadcastReceiver mBroadcastServer = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if ("android.provider.Telephony.SMS_RECEIVED".equals(intent.getAction())) {
                if (bundle != null)
                {
                    SmsMessage[] smsMessage = null;
                    String str = "";
                    String strFrom = "";
                    String strBody = "";
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    smsMessage = new SmsMessage[pdus.length];
                    for (int i = 0; i < smsMessage.length; i++){
                        smsMessage[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                        str += "SMS from " + smsMessage[i].getOriginatingAddress();
                        str += " :";
                        str += smsMessage[i].getMessageBody().toString();
                        str += "\n" ;
                        strFrom = smsMessage[i].getOriginatingAddress();
                        strBody = smsMessage[i].getMessageBody().toString();
                    }

                    Intent intentMusic = new Intent(c, MusicService.class);
                    stopService(intentMusic);

                    String strMusic = "";
                    if (strBody.startsWith("신남")) {
                        intentMusic.putExtra("SELECTED_MUSIC", R.raw.m3);
                        strMusic = "신나는 음악";
                        startService(intentMusic);

                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(strFrom, null, strMusic, null, null);
                    }
                    else if (strBody.startsWith("위치정보요청")) {
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(strFrom, null, "위치정보확인," + strCurrentLocation, null, null);
                    }
                    else if (strBody.startsWith("위치정보확인")) {
                        String[] strMSG = strBody.split(",");    // 위치정보확인, 위도, 경도, 고도
                        String strUName = getContactList(strFrom);
                        TextView txtULocation = findViewById(R.id.txtULocation);
                        txtULocation.setText(strUName + "님의\n위도:" + strMSG[1] + "\n경도:" + strMSG[2] + "\n고도:" + strMSG[3]);
                        // Uri uri = Uri.parse("geo:" + location.getLatitude() + "," + location.getLongitude() + "?q=" + Uri.encode("37.391777,126.919785" + "(" + "ME" + ")"));
                        Uri uriYou = Uri.parse("geo:" + strMSG[1] + "," + strMSG[2] + "?z=10");
                        Intent intent4Location = new Intent(Intent.ACTION_VIEW, uriYou);
                        startActivity(intent4Location);
                    }
                }
            }
            else if (intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED)) {
                int maxvalue = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 0);
                int value = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
                int level = value * 100 / maxvalue;
                txtBattery.setText("현재 배터리 레벨=" + level);

                if (level < 30)
                {
                    //long[] pattern = new long[]{ 100,300,100,700,300,2000 };
                    //vibrator.vibrate(pattern, -1);
                    long millisecond = 1000;  // 1초
                    vibrator.vibrate(millisecond);
                }
            } else if (intent.getAction().equals(Intent.ACTION_BATTERY_LOW)) {
                txtBattery.setText("배터리 부족");
            }
        }
    };

    LocationListener mLocationListener = new LocationListener() {   //gps정보를 알수 있는 리스너
        public void onLocationChanged(Location location) {
            strCurrentLocation = location.getLatitude() + "," + location.getLongitude() + "," + location.getAltitude(); // 경도, 위도, 고도
            txtMLocation.setText("위도:" + location.getLatitude() + "\n경도:" + location.getLongitude() + "\n고도:" + location.getAltitude());
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onProviderDisabled(String provider) {
        }
    };
}
