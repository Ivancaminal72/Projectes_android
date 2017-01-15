package sergi.ivan.carles.artist;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class AddGroupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);
        getSupportActionBar().setTitle(getResources().getString(R.string.all_your_songs));

    }
}
