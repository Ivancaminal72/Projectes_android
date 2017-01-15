package sergi.ivan.carles.client;

import android.content.SharedPreferences;
import android.os.CountDownTimer;
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
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static java.lang.System.currentTimeMillis;

public class ActualEventActivity extends AppCompatActivity {

    private static final int GROUP_MAX_SIZE = 4;
    public static final String END_VOTE_TIME = "endVoteTime";
    public static final String OLD_END_VOTE_TIME = "oldEndVoteTime";
    public static final String VOTED = "voted";
    private MyListAdapter adapter;
    private ArrayList<Song> act_group;
    private ListView list_songs;
    private FirebaseDatabase database;
    private Button buttonVote;
    private TextView countdown;
    private int songSelected;
    private Date endVoteTime;
    private Date oldEndVoteTime;
    private boolean voted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actual_event_activiy);
        database = FirebaseDatabase.getInstance();
        final DatabaseReference actRef = database.getReference("act_group");
        act_group = new ArrayList<>();
        songSelected = -1;
        countdown = (TextView) findViewById(R.id.countdown);

        //Load saved data
        if (savedInstanceState != null) {
            Log.i("info", "Loadig savedInstanceState");
            oldEndVoteTime = new Date(savedInstanceState.getLong(OLD_END_VOTE_TIME));
            voted = savedInstanceState.getBoolean(VOTED);
        }
        else{
            SharedPreferences settings = getPreferences(0);
            Log.i("info", "Null savedInstanceState... Loading settings or default values");
            oldEndVoteTime = new Date(settings.getLong(OLD_END_VOTE_TIME,currentTimeMillis()-1));
            voted = settings.getBoolean(VOTED,false);
        }

        actRef.addValueEventListener(new ValueEventListener()   {
            @Override
            public void onDataChange(DataSnapshot actGroup) {
                Log.i("info", "Update actual group");
                DataSnapshot date = actGroup.child("endVoteTime");
                if(date.exists()) {
                    endVoteTime = new Date((long) date.getValue());
                    if(oldEndVoteTime.getTime() != endVoteTime.getTime()) {
                        voted = false;
                        oldEndVoteTime = endVoteTime;
                    }

                    Log.i("info", String.format("currentTime '%s'", currentTimeMillis()));
                    Log.i("info", String.format("endVoteTime '%s'", endVoteTime.getTime()));

                    act_group.clear();
                    boolean correctGroup = true;
                    for (int i = 0; i < GROUP_MAX_SIZE; i++) {
                        DataSnapshot song = actGroup.child(String.format("song%d", i));
                        if (song.exists()) {
                            DataSnapshot points = song.child("points");
                            DataSnapshot name = song.child("name");
                            DataSnapshot artist = song.child("artist");
                            if(points.exists() && name.exists() && artist.exists()){
                                act_group.add(i, new Song(
                                        i,
                                        (long) points.getValue(),
                                        name.getValue().toString(),
                                        artist.getValue().toString()));
                            } else{
                                correctGroup=false;
                            }

                        } else {
                            correctGroup=false;
                        }
                    }

                    if(correctGroup){
                        //Start orientative timmer
                        new CountDownTimer(endVoteTime.getTime() - currentTimeMillis(), 1000) {

                            public void onTick(long millisUntilFinished) {
                                long minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished);
                                long secInMin = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished);
                                long seconds = secInMin-TimeUnit.MINUTES.toSeconds(minutes);
                                countdown.setText(String.format("%d min %d s", minutes,seconds));
                            }

                            public void onFinish() {
                                countdown.setText(R.string.finishedTime);
                            }

                        }.start();

                        //Sort act_group
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

                }

                else{
                    countdown.setText(R.string.noSongs);
                    Log.i("info", "No hi ha endVoteTime");
                }
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
                if(songSelected >= 0 && !voted){
                    Log.i("info", String.format("Voto cancó: %d", songSelected));
                    DatabaseReference transRef = actRef.child("song"+String.valueOf(
                            act_group.get(songSelected).getGroupPosition()))
                            .child("points");
                    transRef.runTransaction(new Transaction.Handler(){
                        @Override
                        public Transaction.Result doTransaction(MutableData currentData) {
                            if(currentData.getValue() == null){
                                Log.e("info", "Can't vote because the song is not available");
                            }else{
                                Long actualPoints = (long) currentData.getValue();
                                currentData.setValue(actualPoints+1);
                            }

                            return Transaction.success(currentData);
                        }

                        @Override
                        public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot currentData) {
                            if (databaseError != null) {
                                Log.i("info", "Firebase error voting song");
                                Toast.makeText(
                                        ActualEventActivity.this,
                                        getResources().getString(R.string.time_finished),
                                        Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Log.i("info", "Firebase vote song correctly");
                                voted = true;
                                Toast.makeText(
                                        ActualEventActivity.this,
                                        getResources().getString(R.string.voted),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else if(songSelected < 0){
                    Toast.makeText(
                            ActualEventActivity.this,
                            getResources().getString(R.string.none_song_selected),
                            Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(
                            ActualEventActivity.this,
                            getResources().getString(R.string.already_voted),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences settings = getPreferences(0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong(OLD_END_VOTE_TIME, oldEndVoteTime.getTime());
        editor.putBoolean(VOTED, voted);
        editor.commit();
        Log.i("info", "Settings Saved");
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putLong(OLD_END_VOTE_TIME, oldEndVoteTime.getTime());
        savedInstanceState.putBoolean(VOTED, voted);
        super.onSaveInstanceState(savedInstanceState);
        Log.i("info", "savedInstanceState Saved");
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
 
