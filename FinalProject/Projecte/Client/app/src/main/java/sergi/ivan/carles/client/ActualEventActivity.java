package sergi.ivan.carles.client;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ActualEventActivity extends AppCompatActivity {

    private static final int GROUP_MAX_SIZE = 4;
    private MyListAdapter adapter;
    private ArrayList<Song> act_group;
    private ListView list_songs;
    private FirebaseDatabase database;
    private Button buttonVote;
    private int songSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actual_event_activiy);
        database = FirebaseDatabase.getInstance();
        final DatabaseReference actRef = database.getReference("act_group");
        act_group = new ArrayList<>();
        songSelected = -1;

        actRef.addValueEventListener(new ValueEventListener()   {
            @Override
            public void onDataChange(DataSnapshot songGroup) {
                String newPost = songGroup.getValue().toString();
                Log.i("info", newPost);
                act_group.clear();
                for (int i=0; i < GROUP_MAX_SIZE; i++) {
                    DataSnapshot song = songGroup.child(String.format("song%d", i));
                    if (song.exists()) {
                        act_group.add(i, new Song(
                                i,
                                (long) song.child("points").getValue(),
                                song.child("name").getValue().toString(),
                                song.child("artist").getValue().toString()));
                        Log.i("info", String.valueOf(act_group.get(i).getPoints()) + "  " + act_group.get(i).getName() + "   " + act_group.get(i).getArtist());
                    } else {
                        Log.e("info", String.format("No puc trobar la cancó '%s'", i));
                    }
                }
                Collections.sort(act_group, new Comparator<Song>(){
                    public int compare(Song s1, Song s2) {
                        if (s1.getPoints() == s2.getPoints())
                            return 0;
                        else if (s1.getPoints() < s2.getPoints())
                            return 1;
                        else
                            return -1;
                        }
                });
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
        adapter = new MyListAdapter();
        list_songs = (ListView) findViewById(R.id.vote_list);
        list_songs.setAdapter(adapter);
        list_songs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                songSelected=position;
            }
        });
        buttonVote = (Button) findViewById(R.id.btn_vote);
        buttonVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(songSelected >= 0){
                    Log.i("info", String.format("Voto cancó: %d", songSelected));
                    actRef.child("song"+String.valueOf(act_group.get(songSelected).getGroupPosition()))
                            .child("points").setValue(act_group.get(songSelected).getPoints()+1);
                }
            }
        });
    }

    private class MyListAdapter extends ArrayAdapter<Song> {
        MyListAdapter() {
            super(ActualEventActivity.this, R.layout.list_item, act_group);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View result = convertView;
            if(convertView == null) {
                LayoutInflater inflater = getLayoutInflater();
                result = inflater.inflate(R.layout.list_item, parent, false);
            }
            Song song = getItem(position);
            TextView title = (TextView) result.findViewById(R.id.list_item_song);
            title.setText(song.getName());
            TextView art = (TextView) result.findViewById(R.id.list_item_artist);
            art.setText(song.getArtist());
            TextView points = (TextView) result.findViewById(R.id.list_item_points);
            points.setText(String.valueOf(song.getPoints()));
            return result;
        }
    }
}
 