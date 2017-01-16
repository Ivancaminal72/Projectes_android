package sergi.ivan.carles.artist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

public class AddGroupActivity extends AppCompatActivity {

    private ArrayList<Song> songs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);
        getSupportActionBar().setTitle(getResources().getString(R.string.make_groups));

        Intent intent = getIntent();

        //Todo: v0.5 Load songs from firebase

        //Todo: v0.5 Show songs to listview

        //Todo: v0.5 Make groups of 4 songs and send them to firebase

        //Todo: v0.5 Return intent EventActivity CharSequence [] song IDS
    }
}
