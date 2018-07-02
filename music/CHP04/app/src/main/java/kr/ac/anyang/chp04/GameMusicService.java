package kr.ac.anyang.chp04;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Administrator on 2018-04-30.
 */

public class GameMusicService extends Service {
    private static final String TAG = "MusicService";
    MediaPlayer player;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate()");
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Music Service가 중지되었습니다.", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onDestroy()");
        if (this.player.isPlaying())
            this.player.stop();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Music Service가 시작되었습니다.", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onStart()");
        if(intent != null) {
            int position = intent.getIntExtra("SELECTED_MUSIC", R.raw.kalimba);
            this.player = MediaPlayer.create(this, position);
            this.player.setLooping(false); // Set looping
            this.player.start();
        }

        return super.onStartCommand(intent, flags, startId);
    }
}
