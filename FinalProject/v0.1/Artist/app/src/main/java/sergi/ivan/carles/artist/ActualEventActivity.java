package sergi.ivan.carles.artist;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;


public class ActualEventActivity extends AppCompatActivity {

    private ArrayList<Pair> act_group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actual_event);

        act_group = new ArrayList<>();
        act_group.add(new Pair<>("SongKey1", 0));
        act_group.add(new Pair<>("SongKey2", 0));
        act_group.add(new Pair<>("SongKey3", 0));
        act_group.add(new Pair<>("SongKey4", 0));
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Artist1");

        myRef.setValue(act_group);

        Log.i("info", "group sent");
    }
}
