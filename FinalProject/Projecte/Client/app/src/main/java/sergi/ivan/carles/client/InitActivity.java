package sergi.ivan.carles.client;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class InitActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);
        getSupportActionBar().setTitle(R.string.next_events);
    }
}
