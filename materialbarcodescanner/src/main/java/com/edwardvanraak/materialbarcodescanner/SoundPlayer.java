package com.edwardvanraak.materialbarcodescanner;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;

class SoundPlayer {
    // default bleep is R.raw.bleep

    SoundPlayer(Context context, int sound) {
        playSound(context, sound);
    }

    private void playSound(Context context, int sound) {
        MediaPlayer mediaPlayer = MediaPlayer.create(context, sound);

        if (mediaPlayer != null) {
            mediaPlayer.setOnCompletionListener(onCompletionListener);
            mediaPlayer.setOnPreparedListener(onPreparedListener);
            mediaPlayer.setScreenOnWhilePlaying(true);
            mediaPlayer.setVolume(0.5f, 0.5f);
            mediaPlayer.setLooping(false);
        }
    }

    private static final OnCompletionListener onCompletionListener = new OnCompletionListener() {

        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.release();
        }
    };

    private static final OnPreparedListener onPreparedListener = new OnPreparedListener() {

        @Override
        public void onPrepared(MediaPlayer mediaPlayer) {
            mediaPlayer.start();
        }
    };
}