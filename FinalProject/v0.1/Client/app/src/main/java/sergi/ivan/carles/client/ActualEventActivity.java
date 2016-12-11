package sergi.ivan.carles.client;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.List;

public class ActualEventActivity extends AppCompatActivity {

    private String newPost;
    private ArrayList<Integer> points;
    private ArrayList<String> songs;
    private ArrayList<String> artists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actual_event_activiy);
        ListView lv = (ListView) findViewById(R.id.vote_list);
        //lv.setAdapter(new MyListAdaper(this, R.layout.list_item, data));

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
                    JSONArray jArrPoints = jsonObject.getJSONArray("points");
                    JSONArray jArrSongs = jsonObject.getJSONArray("songs");
                    JSONArray jArrArtists = jsonObject.getJSONArray("artists");
                    points = new ArrayList<>();
                    songs = new ArrayList<>();
                    artists = new ArrayList<>();
                    for (int i=0; i < jArrPoints.length(); i++) {
                        points.add(jArrPoints.getInt(i));
                        songs.add(jArrSongs.getString(i));
                        artists.add(jArrArtists.getString(i));
                        Log.i("info", points.get(i).toString() + "  "+songs.get(i)+ "   "+artists.get(i));
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

    private class MyListAdaper extends ArrayAdapter<String> {
        private int layout;
        private List<String> mObjects;
        private MyListAdaper(Context context, int resource, List<String> objects) {
            super(context, resource, objects);
            mObjects = objects;
            layout = resource;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder mainViewholder = null;
            if(convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(layout, parent, false);
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.thumbnail = (ImageView) convertView.findViewById(R.id.list_item_thumbnail);
                viewHolder.title = (TextView) convertView.findViewById(R.id.list_item_text);
                viewHolder.button = (Button) convertView.findViewById(R.id.list_item_btn);
                convertView.setTag(viewHolder);
            }
            mainViewholder = (ViewHolder) convertView.getTag();
            mainViewholder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(), "Button was clicked for list item " + position, Toast.LENGTH_SHORT).show();
                }
            });
            mainViewholder.title.setText(getItem(position));

            return convertView;
        }
    }
    public class ViewHolder {
        ImageView thumbnail;
        TextView title;
        Button button;
    }
}
