package sergi.ivan.carles.artist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;
import java.util.ArrayList;
import java.util.Date;
import static java.lang.System.currentTimeMillis;

public class AddGroupActivity extends AppCompatActivity {

    private static final String REF_SONGS = "songs";
    private ArrayList<Song> songs;
    private EditText edit_group_name;
    private FirebaseDatabase database;
    private SongAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);
        getSupportActionBar().setTitle(getResources().getString(R.string.make_groups));
        database = FirebaseDatabase.getInstance();
        final DatabaseReference songRef = database.getReference(REF_SONGS);

        //Intent intent = getIntent();
        songs = new ArrayList<>();
        adapter = new SongAdapter();
        ListView song_list = (ListView) findViewById(R.id.song_list);
        song_list.setAdapter(adapter);

        song_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> list, View v, int pos, long id) {
                songs.get(pos).toggleChecked();
                adapter.notifyDataSetChanged();
            }
        });

        //Load songs from firebase and show them to the ListView
        songRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot songsSnapshot) {
                for(DataSnapshot song : songsSnapshot.getChildren()){
                    String name = song.child("name").getValue().toString();
                    String artist = song.child("artist").getValue().toString();
                    songs.add(new Song(song.getKey(),name,artist));
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Todo: v0.5 Make groups of 4 songs and send them to firebase

        //Todo: v0.5 Return intent EventActivity CharSequence [] song IDS
    }

    public void onButtonAddClick(View v) {
        edit_group_name = (EditText) findViewById(R.id.edit_group_name);
        String name = edit_group_name.getText().toString();
        if(name.length() == 0){
            Toast.makeText(
                    this,
                    getResources().getString(R.string.group_name),
                    Toast.LENGTH_SHORT).show();
        }
        else {
            int count = 0;
            for (int i = 0; i < songs.size(); i++) {
                if (songs.get(i).isChecked()) {
                    count++;
                }
            }
            if (count != 4) {
                Toast.makeText(
                        this,
                        getResources().getString(R.string.four_songs),
                        Toast.LENGTH_SHORT).show();
            } else {
                //TODO: send songs to firebase
            }
        }
    }

    private class SongAdapter extends ArrayAdapter<Song> {
        SongAdapter() {
            super(AddGroupActivity.this, R.layout.item_songs_list, songs);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View result = convertView;
            if(convertView == null) {
                LayoutInflater inflater = getLayoutInflater();
                result = inflater.inflate(R.layout.item_songs_list, parent, false);
            }
            Song song = getItem(position);
            TextView name = (TextView) result.findViewById(R.id.list_item_song);
            TextView artist = (TextView) result.findViewById(R.id.list_item_artist);
            name.setText(song.getName());
            artist.setText(song.getArtist());
            CheckBox checkbox = (CheckBox) result.findViewById(R.id.list_item_checkbox);
            checkbox.setChecked(song.isChecked());
            return result;
        }
    }
}
