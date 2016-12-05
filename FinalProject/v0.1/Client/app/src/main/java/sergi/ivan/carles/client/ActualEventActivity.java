package sergi.ivan.carles.client;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ActualEventActivity extends AppCompatActivity {

    private String newPost;
    private ArrayList<Integer> points;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actual_event_activiy);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference act_ref = database.getReference("act_group");

        act_ref.addValueEventListener(new ValueEventListener()   {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                newPost = dataSnapshot.getValue().toString();
                Log.i("info", newPost);
                //todo: Mostrar cançons i puntuació actual al layout
                try {
                    JSONObject jsonObject = new JSONObject(newPost);
                    JSONArray jArr = jsonObject.getJSONArray("points");
                    points = new ArrayList<>();
                    for (int i=0; i < jArr.length(); i++) {
                        points.add(jArr.getInt(i));
                        Log.i("info", points.get(i).toString());
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.i("info", "error data conversion from firebase");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        //TODO: quan cliques enviar votació

    }
}
