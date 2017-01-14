package sergi.ivan.carles.artist;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

public class EventActivity extends AppCompatActivity {

    private EditText edit_name;
    private EditText edit_place;
    private EditText edit_room;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        getSupportActionBar().setTitle("EventActivity");

        edit_name = (EditText) findViewById(R.id.edit_name);
        edit_place = (EditText) findViewById(R.id.edit_place);
        edit_room = (EditText) findViewById(R.id.edit_room);

        Intent intent = getIntent();
        if(intent.hasExtra("name")){
            edit_name.setText(intent.getStringExtra("name"));
            edit_place.setText(intent.getStringExtra("place"));
            if(intent.hasExtra("room")){
                edit_room.setText((intent.getStringExtra("room")));
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflate = getMenuInflater();
        inflate.inflate(R.menu.options_event_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.op_save:
                String name = edit_name.getText().toString();
                String place = edit_place.getText().toString();
                String room = edit_room.getText().toString();
                if(name.length() == 0 || place.length() == 0){
                    Toast.makeText(
                            EventActivity.this,
                            getResources().getString(R.string.incomplete),
                            Toast.LENGTH_SHORT).show();
                }else{
                    Intent data = new Intent();
                    data.putExtra("name", name);
                    data.putExtra("place",place);
                    if(room.length() != 0){
                        data.putExtra("room", room);
                    }
                    setResult(RESULT_OK, data);
                    finish();
                    return true;
                }

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
