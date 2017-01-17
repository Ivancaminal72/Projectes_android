package sergi.ivan.carles.artist;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

import java.util.ArrayList;

public class AddGroupActivity extends AppCompatActivity {

    private static final String REF_SONGS = "songs";
    public static final int MAX_LENGHT = 5;
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
        final DatabaseReference groupRef = database.getReference(getString(R.string.REF_GROUPS));

        //Intent intent = getIntent();
        edit_group_name = (EditText) findViewById(R.id.edit_group_name);
        final Button btn_add_group = (Button) findViewById(R.id.btn_add_group);
        final ListView song_list = (ListView) findViewById(R.id.song_list);

        songs = new ArrayList<>();
        adapter = new SongAdapter();
        song_list.setAdapter(adapter);

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

                song_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> list, View v, int pos, long id) {
                        songs.get(pos).toggleChecked();
                        adapter.notifyDataSetChanged();
                    }
                });

                //Make groups of 4 songs
                btn_add_group.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int count = 0;
                        int[] pos = new int[songs.size()];
                        for (int i = 0; i < songs.size(); i++) {
                            if (songs.get(i).isChecked()) {
                                pos[count] = i;
                                count++;
                            }
                        }
                        if (count == 4) {//Correct group
                            String groupName = edit_group_name.getText().toString();
                            if(groupName.length() == 0){
                                for (int i=0; i<4; i++){
                                    String songName = songs.get(pos[i]).getName();
                                    if(songName.length() <= MAX_LENGHT){
                                        groupName = groupName + songName+" ";
                                    }else{
                                        groupName = groupName + songName.substring(0,MAX_LENGHT)+" ";
                                    }

                                }
                            }

                            //Set all songs checked to not checked
                            for(int i=0; i<4; i++){
                                songs.get(pos[i]).toggleChecked();
                            }
                            adapter.notifyDataSetChanged();

                            //Send group to firebase
                            sendGroupToFirebase(pos, groupName, groupRef);

                            Toast.makeText(
                                    AddGroupActivity.this,
                                    getResources().getString(R.string.Group_done),
                                    Toast.LENGTH_SHORT).show();

                        } else {//Incorrect group
                            Toast.makeText(
                                    AddGroupActivity.this,
                                    getResources().getString(R.string.four_songs),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Todo: v0.5 Return intent EventActivity CharSequence [] song IDS

    }

    private void sendGroupToFirebase(int[] pos, String groupName, DatabaseReference groupRef) {
        String key = groupRef.push().getKey();
        for(int i=0; i<4; i++){
            groupRef.child(key).child("songIds").child("id"+String.valueOf(i+1))
                    .setValue(songs.get(pos[i]).getId());
        }
        groupRef.child(key).child("name").setValue(groupName);
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
            CheckBox checkbox = (CheckBox) result.findViewById(R.id.list_item_checkbox);
            name.setText(song.getName());
            artist.setText(song.getArtist());
            checkbox.setChecked(song.isChecked());
            return result;
        }
    }
}
