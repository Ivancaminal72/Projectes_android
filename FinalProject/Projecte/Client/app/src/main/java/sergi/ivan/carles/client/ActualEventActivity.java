package sergi.ivan.carles.client;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class ActualEventActivity extends AppCompatActivity {

    private static final int GROUP_MAX_SIZE = 4;
    private MyListAdapter adapter;
    private String newPost;
    private ArrayList<Song> songs;
    private ListView list_songs;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private final DatabaseReference act_ref = database.getReference("act_group");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actual_event_activiy);

        songs = new ArrayList<>();

        act_ref.addValueEventListener(new ValueEventListener()   {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                newPost = dataSnapshot.getValue().toString();
                Log.i("info", newPost);
                    for (int i=0; i < GROUP_MAX_SIZE; i++) {
                        songs.add(i, new Song(
                                (long)dataSnapshot.child("song"+String.valueOf(i)+"/points").getValue(),
                                dataSnapshot.child("song"+String.valueOf(i)+"/name").getValue().toString(),
                                dataSnapshot.child("song"+String.valueOf(i)+"/artist").getValue().toString()));
                        Log.i("info", String.valueOf(songs.get(i).getPoints()) + "  "+songs.get(i).getName()+ "   "+songs.get(i).getArtist());
                    }

                }
                //todo: Mostrar cançons i puntuació actual al layout


            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
        adapter = new MyListAdapter();
        list_songs = (ListView) findViewById(R.id.vote_list);
        list_songs.setAdapter(adapter);
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
            Button button = (Button) result.findViewById(R.id.list_item_btn);
            button.setText("VOTE");
            return result;
        }
    }
}
