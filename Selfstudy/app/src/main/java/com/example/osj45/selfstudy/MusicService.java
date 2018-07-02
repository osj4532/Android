package com.example.osj45.selfstudy;


import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

public class MusicService extends Service {
    private static final String TAG = "MusicService";
    private MediaPlayer mp;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy(){
        Log.d(TAG,"onDestroy()");
        if (this.mp.isPlaying())
            this.mp.stop();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        Log.d(TAG, "onStart()");
        if(intent != null) {
            int position = intent.getIntExtra("BACKGROUND_MUSIC", R.raw.starwarsmaintheme);
            this.mp = MediaPlayer.create(this, position);
            this.mp.setLooping(true); // Set looping
            this.mp.start();
        }

        return super.onStartCommand(intent, flags, startId);
    }


}
