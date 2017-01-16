package sergi.ivan.carles.artist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

import static java.lang.System.currentTimeMillis;

public class AddGroupActivity extends AppCompatActivity {

    private static final String REF_SONGS = "songs";
    private ArrayList<Song> songs;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);
        getSupportActionBar().setTitle(getResources().getString(R.string.make_groups));
        database = FirebaseDatabase.getInstance();
        final DatabaseReference songRef = database.getReference(REF_SONGS);

        //Todo: v0.5 Load songs from firebase
        //Intent intent = getIntent();
        songRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot songsSnapshot) {
                for(DataSnapshot song : songsSnapshot.getChildren()){
                    String name = song.child("name").getValue().toString();
                    String artist = song.child("artist").getValue().toString();
                    songs.add(new Song(song.getKey(),name,artist));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Todo: v0.5 Show songs to listview

        //Todo: v0.5 Make groups of 4 songs and send them to firebase

        //Todo: v0.5 Return intent EventActivity CharSequence [] song IDS
    }
    
}
