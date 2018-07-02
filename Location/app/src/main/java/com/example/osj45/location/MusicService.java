package com.example.osj45.location;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

public class MusicService extends Service {
    MediaPlayer player;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDestroy() {
        if (this.player.isPlaying())
            this.player.stop();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null) {
            int position = intent.getIntExtra("SELECTED_MUSIC", R.raw.m3);
            this.player = MediaPlayer.create(this, position);
            this.player.setLooping(false); // Set looping
            this.player.start();
        }

        return super.onStartCommand(intent, flags, startId);
    }
}