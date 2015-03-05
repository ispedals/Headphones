package pedals.is.headphones;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.IBinder;

public class HeadphoneService extends Service {

    public static boolean RUNNING = false;

    private static final int ONGOING_NOTIFICATION = 987;

    private static int MUTECOUNT = 0;

    private static AudioManager am;

    private final BroadcastReceiver headphoneReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
                int state = intent.getIntExtra("state", -1);
                switch (state) {
                    case 0:
                        muteStreams();
                        break;
                    case 1:
                        unmuteStreams();
                        break;
                    default:
                        muteStreams();
                }
            }
            else if(intent.getAction().equals(AudioManager.ACTION_AUDIO_BECOMING_NOISY)){
                muteStreams();
            }
            else if(intent.getAction().equals("android.media.VOLUME_CHANGED_ACTION")){
                if(!am.isWiredHeadsetOn()){
                    muteStreams();
                }
            }
        }
    };


    public HeadphoneService() {
    }

    private static void muteStreams(){
        MUTECOUNT++;
        am.setStreamMute(AudioManager.STREAM_NOTIFICATION, true);
        am.setStreamMute(AudioManager.STREAM_ALARM, true);
        am.setStreamMute(AudioManager.STREAM_MUSIC, true);
        am.setStreamMute(AudioManager.STREAM_RING, true);
        am.setStreamMute(AudioManager.STREAM_SYSTEM, true);
        am.setStreamMute(AudioManager.STREAM_DTMF, true);
        am.setStreamMute(AudioManager.STREAM_VOICE_CALL, true);
    }

    private static void unmuteStreams(){
        for(;MUTECOUNT>0;MUTECOUNT--) {
            am.setStreamMute(AudioManager.STREAM_NOTIFICATION, false);
            am.setStreamMute(AudioManager.STREAM_ALARM, false);
            am.setStreamMute(AudioManager.STREAM_MUSIC, false);
            am.setStreamMute(AudioManager.STREAM_RING, false);
            am.setStreamMute(AudioManager.STREAM_SYSTEM, false);
            am.setStreamMute(AudioManager.STREAM_DTMF, false);
            am.setStreamMute(AudioManager.STREAM_VOICE_CALL, false);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        am = (AudioManager)getSystemService(AUDIO_SERVICE);

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_HEADSET_PLUG);
        filter.addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        filter.addAction("android.media.VOLUME_CHANGED_ACTION");
        registerReceiver(headphoneReceiver, filter);

        Notification notification = new Notification(R.drawable.ic_launcher,
                "Headphone Started", System.currentTimeMillis());
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);
        notification.setLatestEventInfo(this, "Headphone is running",
                "Tap to stop", pendingIntent);
        startForeground(ONGOING_NOTIFICATION, notification);
        if(!am.isWiredHeadsetOn()){
            muteStreams();
        }
        RUNNING = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(headphoneReceiver);
        RUNNING = false;
        unmuteStreams();
    }
}
