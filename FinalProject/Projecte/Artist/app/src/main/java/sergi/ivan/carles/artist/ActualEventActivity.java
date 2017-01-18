package sergi.ivan.carles.artist;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import static android.R.color.holo_green_light;
import static android.R.color.holo_orange_dark;
import static java.lang.System.arraycopy;
import static java.lang.System.currentTimeMillis;


public class ActualEventActivity extends AppCompatActivity {

    public static final int GROUP_MAX_SIZE = 4;
    public static final String END_VOTE_TIME = "endVoteTime";
    public static final String POS_ACT_GROUP = "pos_act";
    public static final String POS_GROUP_SELECTED = "position_group_selected";
    public static final String REF_GROUPS = "groups";
    private long OFFSET_MILLIS_VOTE = 3600000; //Default time 15 minutes
    private ArrayList<Group> groups;
    private ArrayList<Song> songs;
    private ListView group_list;
    private TextView view_group;
    private TextView view_song1;
    private TextView view_song2;
    private TextView view_song3;
    private TextView view_song4;
    private TextView points_song1;
    private TextView points_song2;
    private TextView points_song3;
    private TextView points_song4;
    private Button buttonVote;
    private int pos;
    private int pos_act;
    private boolean voting;
    private boolean listening;
    private ValueEventListener ListenerDatabase;
    private FirebaseDatabase database;
    private Date endVoteTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("info", "onCreate");
        super.onCreate(savedInstanceState);
        database = FirebaseDatabase.getInstance();
        final DatabaseReference actRef = database.getReference("act_group");
        setContentView(R.layout.activity_actual_event);

        //Load saved data
        SharedPreferences settings = getPreferences(0);
        if (savedInstanceState != null) {
            Log.i("info", "Loadig savedInstanceState");
            endVoteTime = new Date(savedInstanceState.getLong(END_VOTE_TIME));
            pos_act = savedInstanceState.getInt(POS_ACT_GROUP);
            pos = savedInstanceState.getInt(POS_GROUP_SELECTED);
            Log.i("info", String.format("Pos act: '%d'", pos_act));
            Log.i("info", String.format("Pos: '%d'", pos));
        }
        else{
            Log.i("info", "Null savedInstanceState... Loading settings or default values");
            pos_act = settings.getInt(POS_ACT_GROUP, -1);
            endVoteTime = new Date(settings.getLong(END_VOTE_TIME,currentTimeMillis()-1));
            //endVoteTime = new Date(currentTimeMillis()-1); //finalitza votació
            pos=-1;
        }
        listening = false;

        //Random songs generation
        songs = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Song importedSong = new Song(String.valueOf(i), String.format("Cancion%d", i), String.format("Artista%d", i));
            songs.add(importedSong);
        }

        //Random voting groups generation
        groups = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            String[] songIds = new String[]{String.valueOf(i+1), String.valueOf(i+2), String.valueOf(i+3), String.valueOf(i+4)};
            groups.add(new Group("patata",getResources().getString(R.string.group_to_select)+String.format(" %d", i), songIds));
        }

        //Set layout
        view_group = (TextView) findViewById(R.id.view_group);
        view_song1 = (TextView) findViewById(R.id.view_song1);
        view_song2 = (TextView) findViewById(R.id.view_song2);
        view_song3 = (TextView) findViewById(R.id.view_song3);
        view_song4 = (TextView) findViewById(R.id.view_song4);
        points_song1 = (TextView) findViewById(R.id.points_song_1);
        points_song2 = (TextView) findViewById(R.id.points_song_2);
        points_song3 = (TextView) findViewById(R.id.points_song_3);
        points_song4 = (TextView) findViewById(R.id.points_song_4);
        group_list = (ListView) findViewById(R.id.group_list);
        buttonVote = (Button) findViewById(R.id.btn_vote);
        group_list.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                getGroupsNames()
        ));

        if (endVoteTime.after(new Date(currentTimeMillis()+500))) { //+500 milliseconds (big offset to make sure timmer can be inicialized)
            Log.i("info", "Votació en curs... inicialització del Timmer");
            setVotingState(true);
            initializeTimer(actRef);
        } else {
            Log.i("info", "Votació acabada recollir resultats");
            showGroup(pos_act, actRef, true);
            setVotingState(false);
        }

        if(pos >= 0){showGroup(pos, actRef, false);}

        group_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (voting & listening) {
                    actRef.removeEventListener(ListenerDatabase);
                    listening = false;
                }
                showGroup(position, actRef, false);
                pos = position;
            }
        });

        //Send the selected group to firebase database
        buttonVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!voting & pos >= 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ActualEventActivity.this);
                    builder.setTitle(R.string.confirm);
                    String msg = getResources().getString(R.string.confirm_msg);
                    builder.setMessage(msg + " " + groups.get(pos).getName() + ' ' + getResources().getString(R.string.to_a_vote));
                    builder.setPositiveButton(R.string.send, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.i("info", String.format("CurrentMillis: %d", currentTimeMillis()));
                            endVoteTime = new Date(currentTimeMillis() + OFFSET_MILLIS_VOTE);
                            Log.i("info", String.format("EndVoteTime  : %d", endVoteTime.getTime()));
                            sendGroup(pos, actRef);
                            Log.i("info", "group sent");
                            setVotingState(true);
                            initializeTimer(actRef);
                            pos_act = pos;
                        }
                    });
                    builder.setNegativeButton(android.R.string.cancel, null);
                    builder.create().show();
                } else{
                    if(!listening){showGroup(pos_act, actRef, false);}
                    if(pos < 0){  //None group selected
                        Toast.makeText(
                                ActualEventActivity.this,
                                getResources().getString(R.string.none_group_selected),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences settings = getPreferences(0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong(END_VOTE_TIME, endVoteTime.getTime());
        editor.putInt(POS_ACT_GROUP, pos_act);
        editor.commit();
        Log.i("info", "Settings Saved");
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putLong(END_VOTE_TIME, endVoteTime.getTime());
        savedInstanceState.putInt(POS_ACT_GROUP, pos_act);
        savedInstanceState.putInt(POS_GROUP_SELECTED, pos);
        super.onSaveInstanceState(savedInstanceState);
        Log.i("info", "savedInstanceState Saved");
    }

    private void sendGroup(final int position, DatabaseReference actRef) {
        if(position >= 0) {
            String[] sNames = new String[GROUP_MAX_SIZE];
            String[] sArtists = new String[GROUP_MAX_SIZE];
            String[] songsIds;
            songsIds = groups.get(position).getSongIds();
            for (int i = 0; i < GROUP_MAX_SIZE; i++) {
                for (int j = 0; j < songs.size(); j++) {
                    if (songs.get(j).getId() == songsIds[i]) {
                        sNames[i] = songs.get(j).getName();
                        sArtists[i] = songs.get(j).getArtist();
                    }
                }
            }
            actRef.child("endVoteTime").setValue(endVoteTime.getTime());
            for(int i=0; i<GROUP_MAX_SIZE; i++){
                actRef.child("song"+String.valueOf(i)).child("name").setValue(sNames[i]);
                actRef.child("song"+String.valueOf(i)).child("artist").setValue(sArtists[i]);
                actRef.child("song"+String.valueOf(i)).child("points").setValue(groups.get(pos).getPoints()[i]);
            }

        }
        else {
            actRef.child("endVoteTime").removeValue();
            for(int i=0; i<GROUP_MAX_SIZE; i++){
                actRef.child("song"+String.valueOf(i)).child("name").removeValue();
                actRef.child("song"+String.valueOf(i)).child("artist").removeValue();
                actRef.child("song"+String.valueOf(i)).child("points").removeValue();
            }
        }
    }

    private void showGroup(final int position, final DatabaseReference actRef, final boolean endVoting) {
        if(position < 0){return;}
        view_group.setText(groups.get(position).getName());
        if(position == pos_act){
            listening = true;
            ListenerDatabase = actRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()) {
                        long[] actPoints = new long[GROUP_MAX_SIZE];
                        for (int i = 0; i < GROUP_MAX_SIZE; i++) {
                            actPoints[i] = (long) dataSnapshot.child("song" + String.valueOf(i) + "/points").getValue();
                        }
                        long[] findMax = new long[GROUP_MAX_SIZE];
                        arraycopy(actPoints, 0, findMax, 0, GROUP_MAX_SIZE);
                        int[] orderIndex = new int[GROUP_MAX_SIZE];
                        int max = 0;
                        int n = 0;
                        while (n < GROUP_MAX_SIZE) {
                            for (int i = 0; i < GROUP_MAX_SIZE; i++) {
                                if (findMax[i] > max) {
                                    max = i;
                                }
                            }
                            findMax[max] = -1;
                            orderIndex[n] = max;
                            max = 0;
                            n += 1;
                        }
                        String[] sIds = groups.get(position).getSongIds();
                        view_song1.setText(getSong(sIds[orderIndex[0]]).getName() + "   " + getSong(sIds[orderIndex[0]]).getArtist());
                        view_song2.setText(getSong(sIds[orderIndex[1]]).getName() + "   " + getSong(sIds[orderIndex[1]]).getArtist());
                        view_song3.setText(getSong(sIds[orderIndex[2]]).getName() + "   " + getSong(sIds[orderIndex[2]]).getArtist());
                        view_song4.setText(getSong(sIds[orderIndex[3]]).getName() + "   " + getSong(sIds[orderIndex[3]]).getArtist());
                        points_song1.setText(String.valueOf(actPoints[orderIndex[0]]) + " " + getResources().getString(R.string.points));
                        points_song2.setText(String.valueOf(actPoints[orderIndex[1]]) + " " + getResources().getString(R.string.points));
                        points_song3.setText(String.valueOf(actPoints[orderIndex[2]]) + " " + getResources().getString(R.string.points));
                        points_song4.setText(String.valueOf(actPoints[orderIndex[3]]) + " " + getResources().getString(R.string.points));
                    }

                    if(endVoting) {
                        actRef.removeEventListener(ListenerDatabase);
                        listening = false;
                        if(dataSnapshot.exists()){sendGroup(-1, actRef);}
                        pos_act = -1;
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("info", "Error reading database: "+databaseError.getDetails());
                }
            });
        }
        else{
            String[] sIds = groups.get(position).getSongIds();
            view_song1.setText(getSong(sIds[0]).getName()+"   "+getSong(sIds[0]).getArtist());
            view_song2.setText(getSong(sIds[1]).getName()+"   "+getSong(sIds[1]).getArtist());
            view_song3.setText(getSong(sIds[2]).getName()+"   "+getSong(sIds[2]).getArtist());
            view_song4.setText(getSong(sIds[3]).getName()+"   "+getSong(sIds[3]).getArtist());
            points_song1.setText("");
            points_song2.setText("");
            points_song3.setText("");
            points_song4.setText("");
        }
    }
    private Song getSong(String id){
        for(int i=0; i<songs.size(); i++){
            if(songs.get(i).getId().equals(id)){
                return songs.get(i);
            }
        }
        Log.e("info", "No es troba la cançó!");
        return null;
    }


    private ArrayList<String> getGroupsNames() {
        database = FirebaseDatabase.getInstance();
        final DatabaseReference groupRef = database.getReference(REF_GROUPS);
        Intent intent = getIntent();
        final ArrayList<String> group_names = new ArrayList<>();
        final ArrayList<String> group_ids = intent.getStringArrayListExtra("groupIds");
        groupRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot groupsSnapshot) {
                for (int i = 0; i < group_ids.size(); i++) {
                    group_names.add(groupsSnapshot.child(group_ids.get(i)).child("name").getValue().toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        return group_names;
    }

    private void initializeTimer(final DatabaseReference actRef) {
        //Initialize the timer to end the current vote
        new Timer(true).schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(listening){
                                    actRef.removeEventListener(ListenerDatabase);
                                    listening = false;
                                }
                                showGroup(pos_act, actRef, true);
                                setVotingState(false);
                            }
                        });
                    }
                },
                endVoteTime
        );
    }

    private void setVotingState(boolean isVoting) {
        if(isVoting){
            buttonVote.setText(R.string.voting);
            buttonVote.setBackgroundColor(getResources().getColor(holo_orange_dark));
            voting = true;
        } else{
            buttonVote.setText(R.string.vote);
            buttonVote.setBackgroundColor(getResources().getColor(holo_green_light));
            voting = false;
        }

    }

}
