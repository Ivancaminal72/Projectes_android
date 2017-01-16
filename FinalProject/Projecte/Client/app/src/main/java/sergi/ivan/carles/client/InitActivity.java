package sergi.ivan.carles.client;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import static java.lang.System.currentTimeMillis;

public class InitActivity extends AppCompatActivity {

    public static final int MILLIS_DAY = 86400000;
    public static final int MILLIS_HOUR = 3600000;
    public static final int MILLIS_MINUTE = 60000;
    public static final String REF_EVENTS = "events";
    private ArrayList<Event> events;
    private ListView event_list;
    private EventAdapter adapter;
    private FirebaseDatabase database;
    private ChildEventListener ListenerDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);
        getSupportActionBar().setTitle(R.string.next_events);
        database = FirebaseDatabase.getInstance();
        final DatabaseReference eventRef = database.getReference(REF_EVENTS);

        //Load upcoming artist events
        events = new ArrayList<>();
        Query queryEvents = eventRef.orderByChild("end").startAt(currentTimeMillis(), "end");
        showEvents(queryEvents);

        adapter = new EventAdapter();
        event_list = (ListView) findViewById(R.id.event_listView);
        event_list.setAdapter(adapter);

        event_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                //onUpdateEvent(pos);
            }
        });
    }

    private void showEvents(Query queryEvents) {
        ListenerDatabase = queryEvents.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot eventSnapshot, String previousChildName) {
                String key = eventSnapshot.getKey();
                String name = eventSnapshot.child("name").getValue().toString();
                String place = eventSnapshot.child("place").getValue().toString();
                Long init = (long) eventSnapshot.child("start").getValue();
                Long end = (long) eventSnapshot.child("end").getValue();
                if (eventSnapshot.child("room").exists()) {
                    Log.i("info", "event Added with room");
                    String room = eventSnapshot.child("room").getValue().toString();
                    events.add(new Event(key,name, new Date(init), new Date(end), place, room));
                } else {
                    Log.i("info", "event Added");
                    events.add(new Event(key,name, new Date(init), new Date(end), place));
                }
                sortEvents();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot eventSnapshot, String s) {
                String key = eventSnapshot.getKey();
                for(int i = 0; i<events.size(); i++){
                    if(key.equals(events.get(i).getKey())){
                        String name = eventSnapshot.child("name").getValue().toString();
                        String place = eventSnapshot.child("place").getValue().toString();
                        Long init = (long) eventSnapshot.child("start").getValue();
                        Long end = (long) eventSnapshot.child("end").getValue();
                        events.get(i).setName(name);
                        events.get(i).setPlace(place);
                        events.get(i).setStartDate(new Date(init));
                        events.get(i).setEndDate(new Date(end));
                        if (eventSnapshot.child("room").exists()) {
                            String room = eventSnapshot.child("room").getValue().toString();
                            events.get(i).setRoom(room);
                        }
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot eventSnapshot) {
                String key = eventSnapshot.getKey();
                for(int i = 0; i<events.size(); i++){
                    if(key.equals(events.get(i).getKey())){
                        events.remove(i);
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
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
                }else{
                    Log.i("info", "Old group should not be here");
                    time.setText(R.string.old);
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
}
