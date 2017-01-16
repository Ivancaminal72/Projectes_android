package sergi.ivan.carles.artist;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import static java.lang.System.currentTimeMillis;


public class InitActivity extends AppCompatActivity {

    public static final int MILLIS_DAY = 86400000;
    public static final int MILLIS_HOUR = 3600000;
    public static final int MILLIS_MINUTE = 60000;
    public static final int NEW_EVENT = 0;
    public static final int UPDATE_EVENT = 1;
    private ArrayList<Event> events;
    private ListView event_list;
    private EventAdapter adapter;
    private FirebaseDatabase database;
    private ChildEventListener ListenerDatabase;
    private boolean loaded;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);
        getSupportActionBar().setTitle(R.string.next_events);

        //Load artist events
        events = new ArrayList<>();

        //Todo: (versió 0.5) consultar a firebase els events de l'artista (make not chatch old events)
        loaded = false;
        database = FirebaseDatabase.getInstance();
        final DatabaseReference actRef = database.getReference("events");

        if(!loaded) {
            ListenerDatabase = actRef.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                    String name = dataSnapshot.child("name").getValue().toString();
                    String place = dataSnapshot.child("place").getValue().toString();
                    Long init = (long) dataSnapshot.child("start").getValue();
                    Long end = (long) dataSnapshot.child("end").getValue();
                    if (dataSnapshot.child("room").exists()) {
                        String room = dataSnapshot.child("room").getValue().toString();
                        events.add(new Event(name, new Date(init), new Date(end), place, room));
                    } else {
                        events.add(new Event(name, new Date(init), new Date(end), place));
                    }
                    sortEvents();
                    adapter.notifyDataSetChanged();
                    actRef.removeEventListener(ListenerDatabase);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }

            });
            loaded = true;
        }

        adapter = new EventAdapter();
        event_list = (ListView) findViewById(R.id.event_listView);
        event_list.setAdapter(adapter);

        event_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                onUpdateEvent(pos);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_init_activity, menu);
        return true;
    }

    private void onUpdateEvent(int pos) {
        Event event = events.get(pos);
        Intent intent = new Intent(this, EventActivity.class);
        intent.putExtra("name", event.getName());
        intent.putExtra("start", event.getStartDate().getTime());
        intent.putExtra("end", event.getEndDate().getTime());
        intent.putExtra("place", event.getPlace());
        String room = event.getRoom();
        if(room != null){
            intent.putExtra("room", room);
        }
        startActivityForResult(intent, UPDATE_EVENT);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.op_new:
                Intent intent = new Intent(this, EventActivity.class);
                startActivityForResult(intent, NEW_EVENT);
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        database = FirebaseDatabase.getInstance();
        final DatabaseReference actRef = database.getReference("events");
        switch (requestCode){
            case NEW_EVENT:
                if(resultCode == RESULT_OK){
                    String key = actRef.push().getKey();
                    String name = data.getStringExtra("name");
                    actRef.child(key).child("name").setValue(name);
                    String place = data.getStringExtra("place");
                    actRef.child(key).child("place").setValue(place);
                    Long init = data.getLongExtra("start", currentTimeMillis());
                    actRef.child(key).child("start").setValue(init);
                    Long end = data.getLongExtra("end", currentTimeMillis());
                    actRef.child(key).child("end").setValue(end);
                    if(data.hasExtra("room")){
                        String room = data.getStringExtra("room");
                        actRef.child(key).child("room").setValue(room);
                        Event event = new Event(name, new Date(init),
                                new Date(end), place, room);
                        events.add(event);
                    }else{
                        Event event = new Event(name, new Date(init),
                                new Date(end), place);
                        events.add(event);
                    }
                    sortEvents();
                    adapter.notifyDataSetChanged();
                }
                break;
            case UPDATE_EVENT:
                if(resultCode == RESULT_OK){
                    String name = data.getStringExtra("name");
                    String place = data.getStringExtra("place");
                    Long init = data.getLongExtra("start", currentTimeMillis());
                    Long end = data.getLongExtra("end", currentTimeMillis());
                    int pos = data.getIntExtra("pos",-1);
                    Event event = events.get(pos);
                    event.setName(name);
                    event.setPlace(place);
                    event.setStartDate(new Date(init));
                    event.setEndDate(new Date(end));
                    if(data.hasExtra("room")){
                        String room = data.getStringExtra("room");
                        event.setRoom(room);
                    }
                    sortEvents();
                    adapter.notifyDataSetChanged();
                }
                break;

            default:
                super.onActivityResult(requestCode, resultCode, data);
        }

    }

    private void sortEvents() {
        //Sort events by startDate
        Collections.sort(events, new Comparator<Event>() {
            @Override
            public int compare(Event e1, Event e2) {
                if (e1.getStartDate().after(e2.getStartDate())) return 1;
                else return -1;
            }
        });
    }

    private class EventAdapter extends ArrayAdapter<Event> {
        EventAdapter() {
            super(InitActivity.this, R.layout.item_events_list, events);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View result = convertView;
            if(convertView == null) {
                LayoutInflater inflater = getLayoutInflater();
                result = inflater.inflate(R.layout.item_events_list, parent, false);
            }
            Event event = getItem(position);
            TextView time = (TextView) result.findViewById(R.id.item_time);
            Date initDate = event.getStartDate();
            Long timeRemaining = initDate.getTime()-currentTimeMillis();
            if(timeRemaining<0){
                Date endDate = event.getEndDate();
                Long timeToEnd = endDate.getTime()-currentTimeMillis();
                if(timeToEnd>0){
                    time.setTextColor(getResources().getColor(R.color.colorAccent));
                    time.setText(R.string.now);
                }
            } else if(timeRemaining < MILLIS_HOUR){
                time.setText(String.valueOf(timeRemaining/ MILLIS_MINUTE)+" m");
            } else if(timeRemaining < MILLIS_DAY){
                time.setText(String.valueOf(timeRemaining/ MILLIS_HOUR)+" h");
            } else{
                time.setText(String.valueOf(timeRemaining/ MILLIS_DAY)+" d");
            }

            TextView name = (TextView) result.findViewById(R.id.item_name);
            name.setText(event.getName());

            TextView place = (TextView) result.findViewById(R.id.item_place);
            if(event.getRoom() != null){
                place.setText(event.getPlace() +" - "+event.getRoom());
            }else{
                place.setText(event.getPlace());
            }
            return result;
        }
    }
}
