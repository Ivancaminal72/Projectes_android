package sergi.ivan.carles.client;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import java.util.Date;
import java.util.Locale;

import static java.lang.System.currentTimeMillis;

public class EventActivity extends AppCompatActivity {

    private static String dateToString(Date d) {
        return String.format(Locale.getDefault(), "%02d/%02d/%04d - %02d:%02d",
           d.getDate(), d.getMonth() + 1, d.getYear() + 1900,
           d.getHours()+1, d.getMinutes());
    }

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
        text_start_date.setText(dateToString(start));
        text_end_date.setText(dateToString(end));
        text_place.setText(intent.getStringExtra("place"));
        if (intent.hasExtra("room")) {
            text_room.setText(intent.getStringExtra("room"));
        }
    }
}
