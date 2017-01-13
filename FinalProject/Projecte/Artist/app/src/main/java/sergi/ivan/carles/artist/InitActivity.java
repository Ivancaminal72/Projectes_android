package sergi.ivan.carles.artist;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import static java.lang.System.currentTimeMillis;


public class InitActivity extends AppCompatActivity {

    private ArrayList<Event> events;
    private ListView event_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);

        //Load artist events
        events = new ArrayList<>();
        //Todo: (versió 0.5) consultar a firebase els events de l'artista
        events.add(new Event("carnaval2", new Date(currentTimeMillis()+7000000), new Date(currentTimeMillis()+9000000), "place"));
        events.add(new Event("carnaval1", new Date(currentTimeMillis()+4000000), new Date(currentTimeMillis()+6000000), "place"));
        events.add(new Event("carnaval3", new Date(currentTimeMillis()+10000000), new Date(currentTimeMillis()+12000000), "place"));

        //Sort events by startDate
        Collections.sort(events, new Comparator<Event>(){
            public int compare(Event e1, Event e2) {
                if (e1.getStartDate().after(e2.getStartDate())) return 1;
                else return -1;
            }
        });

        event_list = (ListView) findViewById(R.id.events_list);
        event_list.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                events
        ));
    }
}
