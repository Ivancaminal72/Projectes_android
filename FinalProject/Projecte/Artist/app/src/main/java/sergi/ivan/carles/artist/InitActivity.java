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
            String id = songRef.push().getKey();
            String name = "song"+i;
            String artist = "artist"+i;
            songRef.child(id).child("name").setValue(name);
            songRef.child(id).child("artist").setValue(artist);
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
                final ArrayList<String> eventIds = new ArrayList<>();
                final ArrayList<ArrayList<String>> eventGroupIds = new ArrayList<>();
                for(DataSnapshot event : artistEventsSnapshot.getChildren()){
                    eventIds.add(event.getKey());
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
                        String id = eventSnapshot.getKey();
                        for(int i=0; i<eventIds.size(); i++){
                            if(id.equals(eventIds.get(i))){
                                String name = eventSnapshot.child("name").getValue().toString();
                                String place = eventSnapshot.child("place").getValue().toString();
                                Long init = (long) eventSnapshot.child("start").getValue();
                                Long end = (long) eventSnapshot.child("end").getValue();
                                Event event = new Event(id,name, new Date(init), new Date(end), place);
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
                        String id = eventSnapshot.getKey();
                        for(int i = 0; i<events.size(); i++){
                            if(id.equals(events.get(i).getId())){
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
                        String id = eventSnapshot.getKey();
                        for(int i = 0; i<events.size(); i++){
                            if(id.equals(events.get(i).getId())){
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
        intent.putExtra("id", event.getId());
        intent.putExtra("name", event.getName());
        intent.putExtra("start", event.getStartDate().getTime());
        intent.putExtra("end", event.getEndDate().getTime());
        intent.putExtra("place", event.getPlace());
        String room = event.getRoom();
        if(room != null){
            intent.putExtra("room", room);
        }
        ArrayList<String> groupIds = event.getGroupIds();
        if(groupIds != null){
            intent.putExtra("groupIds", groupIds);
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
        final DatabaseReference artistEventRef = database.getReference(REF_ARTIST_EVENT);
        switch (requestCode){
            case NEW_EVENT:
                if(resultCode == RESULT_OK){
                    String id = eventRef.push().getKey();
                    String name = data.getStringExtra("name");
                    String place = data.getStringExtra("place");
                    Long init = data.getLongExtra("start", currentTimeMillis());
                    Long end = data.getLongExtra("end", currentTimeMillis());
                    Event event = new Event(id,name,new Date(init),new Date(end),place);

                    eventRef.child(id).child("start").setValue(init);
                    eventRef.child(id).child("place").setValue(place);
                    eventRef.child(id).child("name").setValue(name);
                    eventRef.child(id).child("end").setValue(end);

                    if(data.hasExtra("room")){
                        String room = data.getStringExtra("room");
                        eventRef.child(id).child("room").setValue(room);
                        event.setRoom(room);
                    }

                    if(data.hasExtra("groupIds")){
                        ArrayList<String> groupIds = data.getStringArrayListExtra("groupIds");
                        for(int i=0; i<groupIds.size(); i++){
                            artistEventRef.child(id).child("groupId"+String.valueOf(i)).setValue(groupIds.get(i));
                        }
                        event.setGroupIds(groupIds);
                    }else{
                        artistEventRef.child(id).setValue(false);
                    }
                    events.add(event);
                    sortEvents();
                    adapter.notifyDataSetChanged();
                }
                break;
            case UPDATE_EVENT:
                if(resultCode == RESULT_OK){
                    String id = data.getStringExtra("id");
                    String delete = "delete";

                    if(delete.equals(id.substring(0,6))){ //Case delete event
                        eventRef.child(id.substring(6)).removeValue();
                        artistEventRef.child(id.substring(6)).removeValue();
                        for(int i=0; i<events.size(); i++){
                            if(id.equals(events.get(i).getId())){
                                events.remove(i);
                                break;
                            }
                        }
                    }

                    String name = data.getStringExtra("name");
                    String place = data.getStringExtra("place");
                    Long init = data.getLongExtra("start", currentTimeMillis());
                    Long end = data.getLongExtra("end", currentTimeMillis());

                    for(int i=0; i<events.size(); i++) {
                        if (id.equals(events.get(i).getId())) {//Case update event
                            Event oldEvent = events.get(i);
                            Event updatedEvent = new Event(id, name, new Date(init), new Date(end), place);

                            if(!oldEvent.getName().equals(updatedEvent.getName())){
                                eventRef.child(id).child("name").setValue(name);
                            }
                            if(!oldEvent.getPlace().equals(updatedEvent.getPlace())){
                                eventRef.child(id).child("place").setValue(place);
                            }
                            if(!oldEvent.getStartDate().equals(updatedEvent.getStartDate())){
                                eventRef.child(id).child("start").setValue(init);
                            }
                            if(!oldEvent.getEndDate().equals(updatedEvent.getEndDate())){
                                eventRef.child(id).child("end").setValue(end);
                            }

                            if (data.hasExtra("room")) {
                                String room = data.getStringExtra("room");
                                updatedEvent.setRoom(room);
                                if(!room.equals(oldEvent.getRoom())){
                                    eventRef.child(id).child("room").setValue(room);
                                }
                            }else{
                                if(oldEvent.getRoom() != null){
                                    eventRef.child(id).child("room").removeValue();
                                }
                            }
                            if (data.hasExtra("groupIds")) {
                                ArrayList<String> groupIds = data.getStringArrayListExtra("groupIds");
                                updatedEvent.setGroupIds(groupIds);
                                if(!groupIds.equals(oldEvent.getGroupIds())){
                                    if(groupIds != updatedEvent.getGroupIds()) {
                                        artistEventRef.child(id).removeValue();
                                        for(int j=0; j<groupIds.size(); j++){
                                            artistEventRef.child(id).child("groupId"+String.valueOf(j)).setValue(groupIds.get(j));
                                        }
                                    }
                                }
                            }else{
                                if(oldEvent.getGroupIds() != null){
                                    artistEventRef.child(id).removeValue();
                                    artistEventRef.child(id).setValue(false);
                                }
                            }
                            events.remove(i);
                            events.add(updatedEvent);
                            sortEvents();
                            adapter.notifyDataSetChanged();
                            break;
                        }
                    }
                }
                break;

            default:
                super.onActivityResult(requestCode, resultCode, data);
        }

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
