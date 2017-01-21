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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static sergi.ivan.carles.artist.InitActivity.GROUP_MAX_SIZE;

public class AddGroupActivity extends AppCompatActivity {

    private static final int AUTO_GROUP_NAME_SIZE = 5;
    public static final String NEW_EVENT_KEY = "new";
    private EditText edit_group_name;
    private SongAdapter adapter;
    private ArrayList<String> groupIds;
    private ArrayList<String> groupNames;
    private ArrayList<Song> songs;
    private ArrayList<String[]> groupSongIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);
        getSupportActionBar().setTitle(getResources().getString(R.string.make_groups));

        groupIds = new ArrayList<>();
        groupNames = new ArrayList<>();
        groupSongIds = new ArrayList<>();
        edit_group_name = (EditText) findViewById(R.id.edit_group_name);
        final Button btn_add_group = (Button) findViewById(R.id.btn_add_group);
        final ListView song_list = (ListView) findViewById(R.id.song_list);

        songs = InitActivity.songs;
        adapter = new SongAdapter();
        song_list.setAdapter(adapter);

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
                if (count == GROUP_MAX_SIZE) {//Correct group

                    //Get or create groupName for the group
                    String groupName = edit_group_name.getText().toString();
                    if(groupName.length() == 0){
                        for (int i=0; i<GROUP_MAX_SIZE; i++){
                            String songName = songs.get(pos[i]).getName();
                            if(songName.length() <= AUTO_GROUP_NAME_SIZE){
                                groupName = groupName + songName+" ";
                            }else{
                                groupName = groupName + songName.substring(0, AUTO_GROUP_NAME_SIZE)+" ";
                            }

                        }
                    }
                    Log.i("info", "Group Name: "+groupName);

                    //Save data
                    String[] songIds =new String[GROUP_MAX_SIZE];
                    for(int i=0; i<GROUP_MAX_SIZE; i++){
                        songIds[i]=songs.get(pos[i]).getId();
                    }
                    groupNames.add(groupName);
                    groupSongIds.add(songIds);
                    groupIds.add(NEW_EVENT_KEY);

                    //Set all songs checked to not checked
                    for(int i=0; i<GROUP_MAX_SIZE; i++){
                        songs.get(pos[i]).toggleChecked();
                    }
                    adapter.notifyDataSetChanged();
                    edit_group_name.setText("");

                    //Toast to inform correct group generation
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
    public void onBackPressed() {
        Log.i("info", "Back button pressed");
        Intent data = new Intent();
        if(groupIds.size() > 0){
            data.putExtra("groupIds", groupIds);
            data.putExtra("groupNames", groupNames);
            for(String[] songIds : groupSongIds){
                Log.i("info", songIds[0]);
                Log.i("info", songIds[1]);
                Log.i("info", songIds[2]);
                Log.i("info", songIds[3]);
            }
            data.putExtra("groupSongIds", groupSongIds);
            setResult(RESULT_OK, data);
            finish();
        }else{
            setResult(RESULT_CANCELED);
            finish();
        }

        super.onBackPressed();
    }

    private class SongAdapter extends ArrayAdapter<Song> {
        SongAdapter() {super(AddGroupActivity.this, R.layout.item_songs_list, songs);}

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
