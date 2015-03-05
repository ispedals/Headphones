package pedals.is.headphones;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (HeadphoneService.RUNNING) {
            stopService(new Intent(this, HeadphoneService.class));
        } else {
            startService(new Intent(this, HeadphoneService.class));
        }
        finish();
    }

}
