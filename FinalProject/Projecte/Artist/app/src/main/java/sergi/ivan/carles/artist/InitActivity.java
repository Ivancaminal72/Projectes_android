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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import static java.lang.System.currentTimeMillis;
import static sergi.ivan.carles.artist.ActualEventActivity.GROUP_MAX_SIZE;


public class InitActivity extends AppCompatActivity {

    public static final int MILLIS_DAY = 86400000;
    public static final int MILLIS_HOUR = 3600000;
    public static final int MILLIS_MINUTE = 60000;
    public static final int NEW_EVENT = 0;
    public static final int UPDATE_EVENT = 1;
    public static final String REF_ARTIST_EVENT = "artist_events";
    public static final String REF_EVENTS = "events";
    public static final String REF_GROUPS = "groups";
    public static final String REF_SONGS = "songs";
    private ArrayList<Event> events;
    private ListView event_list;
    private EventAdapter adapter;
    private FirebaseDatabase database;
    private ChildEventListener ListenerDatabase;
    private DatabaseReference artistEventRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);
        getSupportActionBar().setTitle(R.string.next_events);
        database = FirebaseDatabase.getInstance();
        final DatabaseReference eventRef = database.getReference(REF_EVENTS);
        final DatabaseReference songRef = database.getReference(REF_SONGS);
        artistEventRef = database.getReference(REF_ARTIST_EVENT);

        /*//Push some demo songs to firabase
        for(int i=0; i<20; i++){
            String key = songRef.push().getKey();
            String name = "song"+i;
            String artist = "artist"+i;
            songRef.child(key).child("name").setValue(name);
            songRef.child(key).child("artist").setValue(artist);
        }*/

        //Load upcoming artist events
        events = new ArrayList<>();
        adapter = new EventAdapter();
        event_list = (ListView) findViewById(R.id.event_listView);
        event_list.setAdapter(adapter);

        event_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                onUpdateEvent(pos);
            }
        });
        Query queryFutureEvents = eventRef.orderByChild("end").startAt(currentTimeMillis(), "end");
        showEvents(queryFutureEvents);

    }

    private void showEvents(final Query queryEvents) {
        artistEventRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot artistEventsSnapshot) {
                final ArrayList<String> eventKeys = new ArrayList<>();
                final ArrayList<ArrayList<String>> eventGroupIds = new ArrayList<>();
                for(DataSnapshot event : artistEventsSnapshot.getChildren()){
                    eventKeys.add(event.getKey());
                    ArrayList<String> groupIds = new ArrayList<>();
                    if(event.hasChildren()){
                        for(DataSnapshot id : event.getChildren()){
                            groupIds.add(id.getValue().toString());
                        }
                    }
                    eventGroupIds.add(groupIds);
                }

                ListenerDatabase = queryEvents.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot eventSnapshot, String previousChildName) {
                        String key = eventSnapshot.getKey();
                        for(int i=0; i<eventKeys.size(); i++){
                            if(key.equals(eventKeys.get(i))){
                                String name = eventSnapshot.child("name").getValue().toString();
                                String place = eventSnapshot.child("place").getValue().toString();
                                Long init = (long) eventSnapshot.child("start").getValue();
                                Long end = (long) eventSnapshot.child("end").getValue();
                                Event event = new Event(key,name, new Date(init), new Date(end), place);
                                if (eventSnapshot.child("room").exists()) {
                                    event.setRoom(eventSnapshot.child("room").getValue().toString());
                                }
                                if(eventGroupIds.get(i).size() > 0){
                                    event.setGroupIds(eventGroupIds.get(i));
                                }
                                events.add(event);
                                sortEvents();
                                adapter.notifyDataSetChanged();
                            }
                        }
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

            @Override
            public void onCancelled(DatabaseError databaseError) {

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
        intent.putExtra("key", event.getKey());
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
        final DatabaseReference eventRef = database.getReference(REF_EVENTS);
        switch (requestCode){
            case NEW_EVENT:
                if(resultCode == RESULT_OK){
                    String key = eventRef.push().getKey();
                    sendEventToFirebase(data, eventRef, key);
                }
                break;
            case UPDATE_EVENT:
                if(resultCode == RESULT_OK){
                    Log.i("info", "UPDATE_EVENT HA ENTRADO");
                    String key = data.getStringExtra("key");
                    String delete = "delete";
                    if(delete.equals(key.substring(0,6))){
                        eventRef.child(key.substring(6)).removeValue();
                    }else{
                     sendEventToFirebase(data, eventRef, key);
                    }
                }
                break;

            default:
                super.onActivityResult(requestCode, resultCode, data);
        }

    }

    private void sendEventToFirebase(Intent data, DatabaseReference eventRef, String key) {
        if(database == null){FirebaseDatabase.getInstance();}
        final DatabaseReference artistEventRef = database.getReference(REF_ARTIST_EVENT);

        String name = data.getStringExtra("name");
        String place = data.getStringExtra("place");
        Long init = data.getLongExtra("start", currentTimeMillis());
        Long end = data.getLongExtra("end", currentTimeMillis());

        eventRef.child(key).child("start").setValue(init);
        eventRef.child(key).child("place").setValue(place);
        eventRef.child(key).child("name").setValue(name);
        eventRef.child(key).child("end").setValue(end);

        if(data.hasExtra("room")){
            String room = data.getStringExtra("room");
            eventRef.child(key).child("room").setValue(room);
        }

        if(data.hasExtra("groupKeys")){
            ArrayList<String> groupKeys = data.getStringArrayListExtra("groupKeys");
            artistEventRef.child(key).removeValue();
            for(int i=0; i<groupKeys.size(); i++){
                artistEventRef.child(key).child("groupId"+String.valueOf(i)).setValue(groupKeys.get(i));
            }
        }else{
            artistEventRef.child(key).removeValue();
            artistEventRef.child(key).setValue(false);
        }
        Query queryFutureEvents = eventRef.orderByChild("end").startAt(currentTimeMillis(), "end");
        showEvents(queryFutureEvents);
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
