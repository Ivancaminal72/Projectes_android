package sergi.ivan.carles.client;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.Date;

import static java.lang.System.currentTimeMillis;

public class EventActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        Intent intent = getIntent();
        getSupportActionBar().setTitle(intent.getStringExtra("name"));
        long durationEvent = 10800000;

        TextView text_start_date = (TextView) findViewById(R.id.text_enter_start_date);
        TextView text_end_date = (TextView) findViewById(R.id.text_enter_end_date);
        TextView text_place = (TextView) findViewById(R.id.text_enter_place);
        TextView text_room = (TextView) findViewById(R.id.text_enter_room);

        Date start = new Date(intent.getLongExtra("start", currentTimeMillis()));
        Date end = new Date(intent.getLongExtra("end", currentTimeMillis() + durationEvent));
        text_start_date.setText(start.getDate()+"/"+String.valueOf(start.getMonth()+1)+"/"+String.valueOf(start.getYear()+1900)+" - "+String.valueOf(start.getHours()+1)+":"+String.valueOf(start.getMinutes()));
        text_end_date.setText(end.getDate()+"/"+String.valueOf(end.getMonth()+1)+"/"+String.valueOf(end.getYear()+1900)+" - "+String.valueOf(end.getHours()+1)+":"+String.valueOf(end.getMinutes()));
        text_place.setText(intent.getStringExtra("place"));
        if (intent.hasExtra("room")) {
            text_room.setText(intent.getStringExtra("room"));
        }
    }
}
