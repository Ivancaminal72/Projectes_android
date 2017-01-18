package sergi.ivan.carles.artist;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static java.lang.System.currentTimeMillis;
import static sergi.ivan.carles.artist.InitActivity.REF_GROUPS;

public class EventActivity extends AppCompatActivity {

    private static final int ADD_GROUPS = 2;
    private EditText edit_name;
    private EditText edit_place;
    private EditText edit_room;
    private Button btn_end_date;
    private Button btn_end_time;
    private Button btn_start_date;
    private Button btn_start_time;
    private Date init;
    private Date end;
    private String key;
    private long durationEvent;
    private ArrayList<String> groupKeys;
    private ArrayList<String> groupNames;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        durationEvent = 10800000;

        Intent intent = getIntent();
        init = new Date(intent.getLongExtra("start", currentTimeMillis()));
        end = new Date(intent.getLongExtra("end", currentTimeMillis() + durationEvent));
        if (intent.hasExtra("key")) {
            getSupportActionBar().setTitle(R.string.modify_event);
            key = intent.getStringExtra("key");
            edit_name.setText(intent.getStringExtra("name"));
            edit_place.setText(intent.getStringExtra("place"));
            if (intent.hasExtra("room")) {
                edit_room.setText((intent.getStringExtra("room")));
            }
            if (intent.hasExtra("groupKeys")) {
                groupKeys = intent.getStringArrayListExtra("groupKeys");
                getGroupsFromFirebase();
            }
        } else {
            getSupportActionBar().setTitle(R.string.new_event);
        }
        edit_name = (EditText) findViewById(R.id.edit_name);
        edit_place = (EditText) findViewById(R.id.edit_place);
        edit_room = (EditText) findViewById(R.id.edit_room);
        btn_end_date = (Button) findViewById(R.id.btn_end_date);
        btn_end_time = (Button) findViewById(R.id.btn_end_time);
        btn_start_date = (Button) findViewById(R.id.btn_start_date);
        btn_start_time = (Button) findViewById(R.id.btn_start_time);
        ListView group_listview = (ListView) findViewById(R.id.group_listView);

        groupKeys = new ArrayList<>();
        groupNames = new ArrayList<>();
        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                groupNames
        );
        group_listview.setAdapter(adapter);

        btn_start_date.setText(init.getDate() + "/" + (init.getMonth() + 1) + "/" + (init.getYear() + 1900));
        btn_end_date.setText(end.getDate() + "/" + (end.getMonth() + 1) + "/" + (end.getYear() + 1900));
        int min = init.getMinutes();
        if (min <= 9) btn_start_time.setText(init.getHours() + ":0" + min);
        else btn_start_time.setText(init.getHours() + ":" + min);
        min = end.getMinutes();
        if (min <= 9) btn_end_time.setText(end.getHours() + ":0" + min);
        else btn_end_time.setText(end.getHours() + ":" + min);

        btn_start_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker(true, init.getYear() + 1900, init.getMonth(), init.getDate());
            }
        });

        btn_end_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker(false, end.getYear() + 1900, end.getMonth(), end.getDate());
            }
        });

        btn_start_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePicker(true, init.getHours(), init.getMinutes());
            }
        });

        btn_end_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePicker(false, end.getHours(), end.getMinutes());
            }
        });

        FloatingActionButton btn_new_group = (FloatingActionButton) findViewById(R.id.btn_new_group);
        btn_new_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newGroup();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflate = getMenuInflater();
        inflate.inflate(R.menu.options_event_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.op_save:
                String name = edit_name.getText().toString();
                String place = edit_place.getText().toString();
                String room = edit_room.getText().toString();
                if (name.length() == 0 || place.length() == 0) {
                    Toast.makeText(
                            EventActivity.this,
                            getResources().getString(R.string.incomplete),
                            Toast.LENGTH_SHORT).show();
                } else {
                    Intent data = new Intent();
                    data.putExtra("name", name);
                    data.putExtra("place", place);
                    data.putExtra("start", init.getTime());
                    data.putExtra("end", end.getTime());
                    if (key != null) {
                        data.putExtra("key", key);
                    }
                    if (room.length() > 0) {
                        data.putExtra("room", room);
                    }
                    if (groupKeys.size() > 0) {
                        data.putExtra("groupKeys", groupKeys);
                    }
                    setResult(RESULT_OK, data);
                    finish();
                    return true;
                }

            case R.id.delete_event:
                Intent data = new Intent();
                if (key != null) {
                    data.putExtra("key", "delete" + key);
                    setResult(RESULT_OK, data);
                    finish();
                    return true;
                } else {
                    edit_name.setText("");
                    edit_place.setText("");
                    edit_room.setText("");
                }

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void timePicker(final boolean startTime, int hours, int minutes) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        if (startTime) {
                            if (minute <= 9) btn_start_time.setText(hourOfDay + ":0" + minute);
                            else btn_start_time.setText(hourOfDay + ":" + minute);
                            init.setHours(hourOfDay);
                            init.setMinutes(minute);
                            end = new Date(init.getTime() + durationEvent);
                            int min = end.getMinutes();
                            if (min <= 9) btn_end_time.setText(end.getHours() + ":0" + min);
                            else btn_end_time.setText(end.getHours() + ":" + min);
                            btn_end_date.setText(end.getDate() + "/" + (end.getMonth() + 1) + "/" + (end.getYear() + 1900));

                        } else {
                            if (minute <= 9) btn_end_time.setText(hourOfDay + ":0" + minute);
                            else btn_end_time.setText(hourOfDay + ":" + minute);
                            end.setHours(hourOfDay);
                            end.setMinutes(minute);
                            durationEvent = end.getTime() - init.getTime();
                        }
                    }
                }, hours, minutes, true);
        timePickerDialog.show();
    }

    private void datePicker(final boolean startDate, int Year, int Month, int Day) {
        final DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        if (startDate) {
                            btn_start_date.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                            init.setYear(year - 1900);
                            init.setMonth(month);
                            init.setDate(dayOfMonth);
                            end = new Date(init.getTime() + durationEvent);
                            btn_end_date.setText(end.getDate() + "/" + (end.getMonth() + 1) + "/" + (end.getYear() + 1900));

                        } else {
                            btn_end_date.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                            end.setYear(year - 1900);
                            end.setMonth(month);
                            end.setDate(dayOfMonth);
                            durationEvent = end.getTime() - init.getTime();
                        }
                    }
                }, Year, Month, Day);
        datePickerDialog.show();
    }

    private ArrayList<String> getEventGroupNames(ArrayList<Group> groups) {
        ArrayList<String> group_names = new ArrayList<>();
        for (int i = 0; i < groups.size(); i++) {
            for (int j = 0; j < groupKeys.size(); j++) {
                if (groups.get(i).getKey().equals(groupKeys.get(j))) {
                    group_names.add(groups.get(i).getName());
                }
            }
        }
        return group_names;
    }

    private void getGroupsFromFirebase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference groupRef = database.getReference(REF_GROUPS);

        groupRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot groupsSnapshot) {
                ArrayList<Group> groups = new ArrayList<>();
                for (DataSnapshot group : groupsSnapshot.getChildren()) {
                    String key = group.getKey();
                    String name = group.child("name").getValue().toString();
                    String[] Ids = new String[(int) group.child("songIds").getChildrenCount()];
                    int i = 0;
                    for (DataSnapshot id : group.child("songIds").getChildren()) {
                        Ids[i] = id.getValue().toString();
                        i++;
                    }
                    groups.add(new Group(key, name, Ids));
                }
                groupNames.clear();
                groupNames.addAll(getEventGroupNames(groups));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void newGroup() {
        Intent intent = new Intent(this, AddGroupActivity.class);
        startActivityForResult(intent, ADD_GROUPS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ADD_GROUPS:
                if (resultCode == RESULT_OK) {
                    ArrayList<String> Keys = data.getStringArrayListExtra("groupKeys");
                    groupKeys.addAll(Keys);
                    getGroupsFromFirebase();
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
