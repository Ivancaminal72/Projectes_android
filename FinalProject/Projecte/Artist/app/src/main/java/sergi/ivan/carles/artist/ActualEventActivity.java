package sergi.ivan.carles.artist;

import android.content.DialogInterface;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
    private long OFFSET_MILLIS_VOTE = 200000; //Default time 6 minutes
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
        pos_act = -1;
        if (savedInstanceState != null) {//Saved values
            Log.i("info", "Loadig savedInstanceState");
            endVoteTime = new Date(savedInstanceState.getLong("endVoteTime"));
            pos_act = savedInstanceState.getInt("pos_act");
            Log.i("info", String.format("SavedInstanceState... Pos act group: '%d'", pos_act));
        }
        else{ //Default values
            Log.i("info", "Null savedInstanceState");
            pos_act = -1;
            endVoteTime = new Date(currentTimeMillis()-1);
        }

        if (endVoteTime.after(new Date(currentTimeMillis()))) {voting = true;}
        else {voting = false; pos_act = -1;}
        listening = false;
        pos=-1;
        setContentView(R.layout.activity_actual_event);
        database = FirebaseDatabase.getInstance();
        final DatabaseReference actRef = database.getReference("act_group");

        //Random songs generation
        songs = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Song importedSong = new Song(i, String.format("Cancion%d", i), String.format("Artista%d", i));
            songs.add(importedSong);
        }

        //Random voting groups generation
        groups = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            int[] songkeys = new int[]{1 + i, 2 + i, 3 + i, 4 + i};
            groups.add(new Group(String.format("Group to select %d", i), songkeys));
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
        group_list.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                getGroupsNames()
        ));


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
        buttonVote = (Button) findViewById(R.id.btn_vote);
        buttonVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!voting & pos >= 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ActualEventActivity.this);
                    builder.setTitle(R.string.confirm);
                    String msg = getResources().getString(R.string.confirm_msg);
                    builder.setMessage(msg + " " + groups.get(pos).getName() + " to a vote?");
                    builder.setPositiveButton(R.string.send, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            sendGroup(pos, actRef);
                            Log.i("info", "group sent");
                            buttonVote.setText(R.string.voting);
                            buttonVote.setBackgroundColor(getResources().getColor(holo_orange_dark));
                            voting = true;
                            endVoteTime = new Date(currentTimeMillis() + OFFSET_MILLIS_VOTE);
                            //Initialize the timer to end the current vote
                            new Timer(true).schedule(
                                    new TimerTask() {
                                        @Override
                                        public void run() {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    showGroup(pos_act, actRef, true);
                                                    buttonVote.setText(R.string.vote);
                                                    buttonVote.setBackgroundColor(getResources().getColor(holo_green_light));
                                                }
                                            });
                                        }
                                    },
                                    endVoteTime
                            );
                            pos_act = pos;
                        }
                    });
                    builder.setNegativeButton(android.R.string.cancel, null);
                    builder.create().show();
                } else if (pos >= -1) {
                    showGroup(pos_act, actRef, false);
                } else {
                    Toast.makeText(
                            ActualEventActivity.this,
                            getResources().getString(R.string.none_group_selected),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putLong("endVoteTime", endVoteTime.getTime());
        savedInstanceState.putInt("pos_act", pos_act);
        super.onSaveInstanceState(savedInstanceState);
        Log.i("info", "savedInstanceState Saved");
    }

    private void sendGroup(final int position, DatabaseReference actRef) {
        if(position >= 0) {
            String[] sNames = new String[GROUP_MAX_SIZE];
            String[] sArtists = new String[GROUP_MAX_SIZE];
            int[] songsIds;
            songsIds = groups.get(position).getSongIds();
            for (int i = 0; i < GROUP_MAX_SIZE; i++) {
                for (int j = 0; j < songs.size(); j++) {
                    if (songs.get(j).getSongId() == songsIds[i]) {
                        sNames[i] = songs.get(j).getName();
                        sArtists[i] = songs.get(j).getArtist();
                    }
                }
            }
            for(int i=0; i<GROUP_MAX_SIZE; i++){
                actRef.child("song"+String.valueOf(i)).child("name").setValue(sNames[i]);
                actRef.child("song"+String.valueOf(i)).child("artist").setValue(sArtists[i]);
                actRef.child("song"+String.valueOf(i)).child("points").setValue(groups.get(pos).getPoints()[i]);
            }

        }
        else {
            for(int i=0; i<GROUP_MAX_SIZE; i++){
                actRef.child("song"+String.valueOf(i)).child("name").removeValue();
                actRef.child("song"+String.valueOf(i)).child("artist").removeValue();
                actRef.child("song"+String.valueOf(i)).child("points").removeValue();
            }
        }
    }

    private void showGroup(final int position, final DatabaseReference actRef, final boolean endVoting) {
        if (position < 0) {
            // TODO: Potser millor fer un 'disable' del botó perquè no es pugui votar sense un grup seleccionat?
            return;
        }
        view_group.setText(groups.get(position).getName());
        if(position == pos_act){
            listening = true;
            ListenerDatabase = actRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    long[] actPoints = new long[GROUP_MAX_SIZE];
                    for (int i=0; i < GROUP_MAX_SIZE; i++) {
                        actPoints[i] = (long)dataSnapshot.child("song"+String.valueOf(i)+"/points").getValue();
                    }
                    long[] findMax = new long[GROUP_MAX_SIZE];
                    arraycopy(actPoints,0,findMax,0,GROUP_MAX_SIZE);
                    int[] orderIndex = new int[GROUP_MAX_SIZE];
                    int max = 0;
                    int n = 0;
                    while(n<GROUP_MAX_SIZE){
                        for(int i=0; i<GROUP_MAX_SIZE; i++) {
                            if(findMax[i] > max) {max = i;}
                        }
                        findMax[max] = -1;
                        orderIndex[n] = max;
                        max=0;
                        n+=1;
                    }
                    int[] sIds = groups.get(position).getSongIds();
                    view_song1.setText(songs.get(sIds[orderIndex[0]]).getName()+"   "+songs.get(sIds[orderIndex[0]]).getArtist());
                    view_song2.setText(songs.get(sIds[orderIndex[1]]).getName()+"   "+songs.get(sIds[orderIndex[1]]).getArtist());
                    view_song3.setText(songs.get(sIds[orderIndex[2]]).getName()+"   "+songs.get(sIds[orderIndex[2]]).getArtist());
                    view_song4.setText(songs.get(sIds[orderIndex[3]]).getName()+"   "+songs.get(sIds[orderIndex[3]]).getArtist());
                    points_song1.setText(String.valueOf(actPoints[orderIndex[0]])+" "+getResources().getString(R.string.points));
                    points_song2.setText(String.valueOf(actPoints[orderIndex[1]])+" "+getResources().getString(R.string.points));
                    points_song3.setText(String.valueOf(actPoints[orderIndex[2]])+" "+getResources().getString(R.string.points));
                    points_song4.setText(String.valueOf(actPoints[orderIndex[3]])+" "+getResources().getString(R.string.points));

                    //Case of end of voting
                    if(endVoting) {
                        actRef.removeEventListener(ListenerDatabase);
                        listening = false;
                        sendGroup(-1, actRef);
                        voting = false;
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("info", "Error reading database: "+databaseError.getDetails());
                }
            });
        }
        else{
            int[] sIds = groups.get(position).getSongIds();
            view_song1.setText(songs.get(sIds[0]).getName()+"   "+songs.get(sIds[0]).getArtist());
            view_song2.setText(songs.get(sIds[1]).getName()+"   "+songs.get(sIds[1]).getArtist());
            view_song3.setText(songs.get(sIds[2]).getName()+"   "+songs.get(sIds[2]).getArtist());
            view_song4.setText(songs.get(sIds[3]).getName()+"   "+songs.get(sIds[3]).getArtist());
            points_song1.setText("");
            points_song2.setText("");
            points_song3.setText("");
            points_song4.setText("");
        }
    }

    private ArrayList<String> getGroupsNames() {
        ArrayList<String> group_names = new ArrayList<>();
        for(int i =0; i<groups.size(); i++){
            group_names.add(groups.get(i).getName());
        }
        return group_names;
    }
}
