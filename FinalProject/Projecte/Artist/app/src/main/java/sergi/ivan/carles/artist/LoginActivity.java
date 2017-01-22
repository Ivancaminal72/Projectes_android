package sergi.ivan.carles.artist;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.util.ArrayList;

import static sergi.ivan.carles.artist.InitActivity.REF_ARTISTS;

public class LoginActivity extends AppCompatActivity {

    public static final int NEW_ARTIST = 0;
    public static final String REF_USERS = "users";
    public static ArrayList<String[]> artists;
    private EditText edit_email;
    private EditText edit_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        artists = new ArrayList<>();
        //loadArtists();
        edit_email = (EditText) findViewById(R.id.edit_email);
        edit_password = (EditText) findViewById(R.id.edit_password);
        Button btn_login = (Button) findViewById(R.id.btn_login);
        Button btn_register = (Button)  findViewById(R.id.btn_register);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Store values at the time of the login attempt
                String email = edit_email.getText().toString();
                String password = edit_password.getText().toString();

                boolean cancel = false;
                View focusView = null;

                // Check for a valid email address.
                if (TextUtils.isEmpty(email)) {
                    edit_email.setError(getString(R.string.error_field_required));
                    focusView = edit_email;
                    cancel = true;
                } else if (!email.contains("@")) {
                    edit_email.setError(getString(R.string.error_invalid_email));
                    focusView = edit_email;
                    cancel = true;
                }

                if (cancel) {
                    focusView.requestFocus();
                } else {
                    attemptToLogin(email,password);
                }
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivityForResult(intent, NEW_ARTIST);
            }
        });
    }

    private void attemptToLogin(final String mEmail, final String mPassword){

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference  userRef = database.getReference(REF_USERS);


        //Runnable to checkConnection after 2s
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                checkConnection(LoginActivity.this);
            }
        };

        final Handler handler = new Handler();
        handler.postDelayed(runnable, 2000);

        //Check user credentials
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot usersSnapshot) {
                handler.removeCallbacks(runnable);
                boolean found = false;
                if(usersSnapshot.hasChildren()){
                    for (DataSnapshot user : usersSnapshot.getChildren()) {
                        if (user.hasChild("email")) {
                            String email = user.child("email").getValue().toString();
                            if (email.equals(mEmail)) {
                                found = true;
                                String password = user.child("password").getValue().toString();
                                if (password.equals(mPassword)) {
                                    String artistId = user.child("artistId").getValue().toString();
                                    Intent intent = new Intent(LoginActivity.this, InitActivity.class);
                                    intent.putExtra("artistId", artistId);
                                    startActivity(intent);
                                    //saveArtists();
                                    finish();

                                } else {
                                    edit_password.setError(getString(R.string.error_incorrect_password));
                                    edit_password.requestFocus();
                                }
                            }
                        } else {
                            Log.e("info", "User without email!");
                        }
                    }
                }
                if(!found){
                    edit_email.setError(getString(R.string.error_email_no_exist));
                    edit_email.requestFocus();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(
                        LoginActivity.this,
                        R.string.error_database,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == NEW_ARTIST){
            if(resultCode == RESULT_OK){
                //Get extras
                String email = data.getStringExtra("email");
                String password = data.getStringExtra("password");
                String artistId = data.getStringExtra("artistId");

                //Add artist
                String [] artist = new String[3];
                artist[0]=email;
                artist[1]=password;
                artist[2]=artistId;
                artists.add(artist);

                //Show credentials
                edit_email.setText(email);
                edit_password.setText(password);
            }
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public static void checkConnection(final Context context) {
        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (!connected) {
                    Toast.makeText(
                            context,
                            R.string.error_internet_connection,
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(
                        context,
                        R.string.error_database,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveArtists() {
        try {
            FileOutputStream fos = openFileOutput(REF_ARTISTS, Context.MODE_PRIVATE);
            for (int i = 0; i < artists.size(); i++) {
                String [] artist = artists.get(i);
                String artist_txt = String.format("%s;%s;%s\n", artist[0], artist[1],artist[2]);
                fos.write(artist_txt.getBytes());
            }
            fos.close();
        } catch (FileNotFoundException e) {
            Log.e("info", "saveArtists: FileNotFoundException");
        } catch (IOException e) {
            Log.e("info", "saveArtists: IOException");
        }
    }

    private void loadArtists() {
        ArrayList<String[]> list = new ArrayList<>();
        try {
            FileInputStream fis = openFileInput(REF_ARTISTS);
            try{
                int fileSize = (int)fis.getChannel().size();
                byte[] buffer = new byte[fileSize];
                int nbytes = fis.read(buffer);
                String content = new String(buffer, 0, nbytes);
                String[] lines = content.split("\n");
                for (String line : lines) {
                    String[] artist = line.split(";");
                    artists.add(artist);
                }
                fis.close();
            } catch (ClosedChannelException e){
                Log.e("info", "saveArtists: ClosedChannelException");
            }
        } catch (FileNotFoundException e) {
            Log.e("info", "saveArtists: FileNotFoundException");
        } catch (IOException e) {
            Toast.makeText(this, R.string.read_error, Toast.LENGTH_SHORT).show();
            Log.e("info", "saveArtists: IOException");
        }
    }

}

