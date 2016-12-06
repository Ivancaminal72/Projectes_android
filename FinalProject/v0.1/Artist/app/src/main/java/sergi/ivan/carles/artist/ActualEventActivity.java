package sergi.ivan.carles.artist;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import static java.lang.System.arraycopy;


public class ActualEventActivity extends AppCompatActivity {

    public static final int GROUP_MAX_SIZE = 4;
    private ArrayList<Group> groups;
    private ArrayList<Song> songList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actual_event);

        //Random songList generation
        songList = new ArrayList<>();
        for(int i=0; i<10; i++){
            Song importedSong = new Song(i,String.format("Cancion%d",i),String.format("Artista%d",i));
            songList.add(importedSong);
        }

        //Random voting groups generation
        groups = new ArrayList<>();
        for(int i=0; i<2; i++){
            Integer[] songkeys = new Integer[]{1, 2, 3, 4};
            groups.add(new Group(String.format("Cancion%d",i),songkeys));
        }

        //Send the selected group to firebase database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("act_group");
        String jsonData = toJson(1);
        if(jsonData != null){
            myRef.setValue(jsonData);
            Log.i("info", "group sent");
        }
        else{
            Log.e("info", "group data is null");
        }


    }

    private String toJson(int index) {
        ArrayList <String> sNames = new ArrayList<>();
        ArrayList <String> sArtists = new ArrayList<>();
        Integer [] songsIds;
        songsIds = groups.get(index).getSongids();
        for(int i=0; i<GROUP_MAX_SIZE; i++){
            for(int j=0; j<songList.size(); j++){
                if(songList.get(j).getSongId()==songsIds[i]){
                    sNames.add(songList.get(j).getName());
                    sArtists.add(songList.get(j).getArtist());
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
