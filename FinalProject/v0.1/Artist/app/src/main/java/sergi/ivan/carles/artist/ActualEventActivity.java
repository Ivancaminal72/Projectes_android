package sergi.ivan.carles.artist;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;


public class ActualEventActivity extends AppCompatActivity {

    private ArrayList<Group> groups;
    private ArrayList<Song> songList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actual_event);

        //Random songList generation
        songList = new ArrayList<>();
        for(int i=0; i<10; i++){
            Song importedSong = new Song(i,"Nombre Canción","Nombre del artista");
            songList.add(importedSong);
        }

        //Random voting groups generation
        groups = new ArrayList<>();
        for(int i=0; i<20; i++){
            Integer[] songkeys = new Integer[]{1, 2, 3, 4};
            groups.add(new Group("grupX",songkeys));
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("act_group");
        myRef.setValue(groups.get(1).toJson());
        Log.i("info", "group sent");
    }
}
