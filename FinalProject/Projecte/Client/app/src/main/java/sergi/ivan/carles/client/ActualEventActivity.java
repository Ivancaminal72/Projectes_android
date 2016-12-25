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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ActualEventActivity extends AppCompatActivity {

    private static final int GROUP_MAX_SIZE = 4;
    private MyListAdapter adapter;
    private ArrayList<Song> songs;
    private ListView list_songs;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actual_event_activiy);

        database = FirebaseDatabase.getInstance();
        final DatabaseReference act_ref = database.getReference("act_group");

        songs = new ArrayList<>();

        act_ref.addValueEventListener(new ValueEventListener()   {
            @Override
            public void onDataChange(DataSnapshot songGroup) {
                String newPost = songGroup.getValue().toString();
                Log.i("info", newPost);
                for (int i=0; i < GROUP_MAX_SIZE; i++) {
                    DataSnapshot song = songGroup.child(String.format("song%d", i));
                    if (song.exists()) {
                        songs.add(i, new Song(
                                (long) song.child("points").getValue(),
                                song.child("name").getValue().toString(),
                                song.child("artist").getValue().toString()));
                        Log.i("info", String.valueOf(songs.get(i).getPoints()) + "  " + songs.get(i).getName() + "   " + songs.get(i).getArtist());
                    } else {
                        Log.e("error", String.format("No puc trobar la cancó '%s'", i));
                    }
                }
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

            }
        });
        //TODO: quan cliques enviar votació
    }

    private class MyListAdapter extends ArrayAdapter<Song> {
        MyListAdapter() {
            super(ActualEventActivity.this, R.layout.list_item, songs);
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
 
