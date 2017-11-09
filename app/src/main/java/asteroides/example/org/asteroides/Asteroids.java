package asteroides.example.org.asteroides;

import android.app.Application;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.preference.PreferenceManager;

/**
 * Created by jmtt_ on 11/8/2017.
 *
 */

public class Asteroids extends Application {
    private static final int AUDIO_SESSION = 402;
    private MediaPlayer mediaPlayer;

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(new LifecycleHandler(new BackgroundListener(){

            @Override
            public void onSendBackground() {
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.pause();
                }
            }

            @Override
            public void onSendForeground() {
                if(!mediaPlayer.isPlaying() && PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("hasMusic", true)){
                    mediaPlayer.start();
                }
            }
        }));

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes att = new AudioAttributes.Builder().setLegacyStreamType(AudioManager.STREAM_MUSIC).build();
            mediaPlayer = MediaPlayer.create(this, R.raw.audio, att, AUDIO_SESSION);
        }else{
            mediaPlayer = MediaPlayer.create(this, R.raw.audio);
        }
        mediaPlayer.setLooping(true);
        if(!mediaPlayer.isPlaying() && PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("hasMusic", true)){
            mediaPlayer.start();
        }
    }

    @Override
    public void onTerminate() {

        if(mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onTerminate();
    }

    public void resetMusic(boolean newValue){
        if(newValue){
            if(!mediaPlayer.isPlaying()){
                mediaPlayer.start();
            }
        }else{
            if(mediaPlayer.isPlaying()){
                mediaPlayer.pause();
            }
        }
    }

    public interface BackgroundListener{
        public void onSendBackground();
        public void onSendForeground();
    }
}
