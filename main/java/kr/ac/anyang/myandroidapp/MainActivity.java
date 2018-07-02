package kr.ac.anyang.myandroidapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    Context c;
    TextView txtBattery;
    Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.c = this;
        this.txtBattery = (TextView) findViewById(R.id.txtBattery);
        this.vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        intentFilter.addAction(Intent.ACTION_BATTERY_LOW);

        registerReceiver(this.mBroadcastServer, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(this.mBroadcastServer);
        stopService(new Intent(c, MusicService.class));
        vibrator.cancel();
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
                    if (strBody.contains("차분")) {
                        intentMusic.putExtra("SELECTED_MUSIC", R.raw.m1);
                        strMusic = "차분한 음악";
                    }
                    else if (strBody.contains("상쾌")) {
                        intentMusic.putExtra("SELECTED_MUSIC", R.raw.m2);
                        strMusic = "상쾌한 음악";
                    }
                    else if (strBody.contains("신남")) {
                        intentMusic.putExtra("SELECTED_MUSIC", R.raw.m3);
                        strMusic = "신나는 음악";
                    }
                    else if (strBody.contains("대출")) {
                        intentMusic.putExtra("SELECTED_MUSIC", R.raw.m0);
                        strMusic = "니 아버지 모하시노?";
                    }
                    startService(intentMusic);

                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(strFrom, null, strMusic, null, null);
                }
            }
            else if (intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED)) {
                int maxvalue = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 0);
                int value = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
                int level = value * 100 / maxvalue;
                txtBattery.setText("현재 배터리 레벨=" + level);

                if (level < 60)
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
}
