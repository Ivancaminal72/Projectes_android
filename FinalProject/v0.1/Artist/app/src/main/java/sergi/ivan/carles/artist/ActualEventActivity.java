package sergi.ivan.carles.artist;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class ActualEventActivity extends AppCompatActivity {

    public static final int GROUP_MAX_SIZE = 4;
    private ArrayList<Group> groups;
    private ArrayList<Song> songs;
    private ListView group_list;
    private TextView view_group;
    private TextView view_song1;
    private TextView view_song2;
    private TextView view_song3;
    private TextView view_song4;
    private Button buttonVote;
    private int pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actual_event);

        //Random songs generation
        songs = new ArrayList<>();
        for(int i=0; i<100; i++){
            Song importedSong = new Song(i,String.format("Cancion%d",i),String.format("Artista%d",i));
            songs.add(importedSong);
        }

        //Random voting groups generation
        groups = new ArrayList<>();
        for(int i=0; i<20; i++){
            Integer[] songkeys = new Integer[]{1+i, 2+i, 3+i, 4+i};
            groups.add(new Group(String.format("Group to select %d",i),songkeys));
        }

        //Set layout
        view_group = (TextView) findViewById(R.id.view_group);
        view_song1 = (TextView) findViewById(R.id.view_song1);
        view_song2 = (TextView) findViewById(R.id.view_song2);
        view_song3 = (TextView) findViewById(R.id.view_song3);
        view_song4 = (TextView) findViewById(R.id.view_song4);
        group_list = (ListView) findViewById(R.id.group_list);
        group_list.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                getGroupsNames()
        ));
        group_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                view_group.setText(groups.get(position).getName());
                Integer[] selected_songids = groups.get(position).getSongids();
                view_song1.setText(songs.get(selected_songids[0]).getName());
                view_song2.setText(songs.get(selected_songids[1]).getName());
                view_song3.setText(songs.get(selected_songids[2]).getName());
                view_song4.setText(songs.get(selected_songids[3]).getName());
                pos = position;
            }
        });

        //Send the selected group to firebase database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("act_group");
        buttonVote = (Button) findViewById(R.id.btn_vote);
        buttonVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String jsonData = toJson(pos);
                myRef.setValue(jsonData);
                if(jsonData != null){
                    Log.i("info", "group sent");
                }
                else{
                    Log.e("ERROR", "group data is null");
                }
            }
        });
    }

    private ArrayList<String> getGroupsNames() {
        ArrayList<String> group_names = new ArrayList<>();
        for(int i =0; i<groups.size(); i++){
            group_names.add(groups.get(i).getName());
        }
        return group_names;
    }

    private String toJson(int index) {
        ArrayList <String> sNames = new ArrayList<>();
        ArrayList <String> sArtists = new ArrayList<>();
        Integer [] songsIds;
        songsIds = groups.get(index).getSongids();
        for(int i=0; i<GROUP_MAX_SIZE; i++){
            for(int j = 0; j< songs.size(); j++){
                if(songs.get(j).getSongId()==songsIds[i]){
                    sNames.add(songs.get(j).getName());
                    sArtists.add(songs.get(j).getArtist());
                }
            }
        }
        try{
            JSONObject jsonObject = new JSONObject();
            JSONArray jsonSongs = new JSONArray(sNames);
            JSONArray jsonArtists = new JSONArray(sArtists);
            JSONArray jsonPoints = new JSONArray(groups.get(index).getPoints());
            jsonObject.put("songs",jsonSongs);
            jsonObject.put("artists", jsonArtists);
            jsonObject.put("points", jsonPoints);
            return jsonObject.toString();
        }

        catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
