package sergi.ivan.carles.artist;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;


public class ActualEventActivity extends AppCompatActivity {

    private ArrayList<Group> groups;
    private Group act_group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actual_event);

        groups = new ArrayList<>();
        for(int i=0; i<20; i++){
            String[] songkeys = new String[]{"sk1", "sk1", "sk1", "sk1"};
            groups.add(new Group("grupX",songkeys));
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("act_vote");
        myRef.setValue(groups.get(1).toJson());
        Log.i("info", "group sent");
    }
}
