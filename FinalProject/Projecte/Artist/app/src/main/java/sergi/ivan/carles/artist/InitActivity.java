package sergi.ivan.carles.artist;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);
        getSupportActionBar().setTitle(R.string.next_events);

        //Load artist events
        events = new ArrayList<>();
        //Todo: (versió 0.5) consultar a firebase els events de l'artista (make not chatch old events)
        events.add(new Event("Carnaval2", new Date(currentTimeMillis()+1000000), new Date(currentTimeMillis()+1200000), "place", "room"));
        events.add(new Event("Carnaval1", new Date(currentTimeMillis()-1000000), new Date(currentTimeMillis()+1200000), "place"));
        events.add(new Event("Carnaval3", new Date(currentTimeMillis()+10000000), new Date(currentTimeMillis()+12000000), "place", "room"));
        events.add(new Event("Carnaval5", new Date(currentTimeMillis()+140000000), new Date(currentTimeMillis()+150000000), "place"));
        events.add(new Event("Carnaval6", new Date(currentTimeMillis()+200000000), new Date(currentTimeMillis()+220000000), "place"));
        events.add(new Event("Carnaval6", new Date(currentTimeMillis()+240000000), new Date(currentTimeMillis()+340000000), "place"));
        events.add(new Event("Carnaval8", new Date(currentTimeMillis()+1300000000), new Date(currentTimeMillis()+1400000000), "place"));


        sortEvents();
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
        switch (requestCode){
            case NEW_EVENT:
                if(resultCode == RESULT_OK){
                    String name = data.getStringExtra("name");
                    String place = data.getStringExtra("place");
                    Long init = data.getLongExtra("start", currentTimeMillis());
                    Long end = data.getLongExtra("end", currentTimeMillis());
                    if(data.hasExtra("room")){
                        String room = data.getStringExtra("room");
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
