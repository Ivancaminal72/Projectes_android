package sergi.ivan.carles.artist;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
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

import static sergi.ivan.carles.artist.InitActivity.REF_ARTISTS;
import static sergi.ivan.carles.artist.LoginActivity.REF_USERS;

public class RegisterActivity extends AppCompatActivity {

    private DatabaseReference artistRef;
    private DatabaseReference userRef;
    private String email;
    private String artistic_name;
    private String password;
    private String repeat_pass;
    private String country;
    private String city;
    private String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().setTitle(R.string.register);
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        userRef = database.getReference(REF_USERS);
        artistRef = database.getReference(REF_ARTISTS);

        final EditText editEmail = (EditText) findViewById(R.id.email);
        final EditText editUser = (EditText) findViewById(R.id.artistic_name);
        final EditText editPassword = (EditText) findViewById(R.id.edit_password);
        final EditText editRepeatPass = (EditText) findViewById(R.id.edit_password_repeat);
        final EditText editCountry = (EditText) findViewById(R.id.country);
        final EditText editCity = (EditText) findViewById(R.id.city);
        final EditText editPhone = (EditText) findViewById(R.id.phone);
        Button btn_signup = (Button) findViewById(R.id.btn_signup);

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = editEmail.getText().toString();
                artistic_name = editUser.getText().toString();
                password = editPassword.getText().toString();
                repeat_pass = editRepeatPass.getText().toString();
                country = editCountry.getText().toString();
                city = editCity.getText().toString();
                phone = editPhone.getText().toString();
                if (email.matches("") || password.matches("") || artistic_name.matches("") || repeat_pass.matches("") || country.matches("") || city.matches("")) {
                    Toast.makeText(
                            RegisterActivity.this,
                            getResources().getString(R.string.empty_fields),
                            Toast.LENGTH_SHORT).show();
                } else if (!email.contains("@")) {
                    Toast.makeText(
                            RegisterActivity.this,
                            getResources().getString(R.string.error_invalid_email),
                            Toast.LENGTH_SHORT).show();
                } else if (password.length() < 8) {
                    Toast.makeText(
                            RegisterActivity.this,
                            getResources().getString(R.string.error_invalid_password),
                            Toast.LENGTH_SHORT).show();
                } else if (!password.equals(repeat_pass)) {
                    Toast.makeText(
                            RegisterActivity.this,
                            getResources().getString(R.string.error_incorrect_password),
                            Toast.LENGTH_SHORT).show();
                } else {
                    attemptToRegister();
                }
            }
        });

    }
    private void attemptToRegister() {

        //Runnable to checkConnection after 2s
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                LoginActivity.checkConnection(RegisterActivity.this);
            }
        };

        final Handler handler = new Handler();
        handler.postDelayed(runnable, 2000);

        final boolean[] existEmail = {false};

        //Check if is a non duplicated email
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot users) {
                if (users.hasChildren()){
                    for (DataSnapshot user : users.getChildren()) {
                        String existingEmail = user.child("email").getValue().toString();
                        if (email.equals(existingEmail)) {
                            existEmail[0] = true;
                        }
                    }
                }

                if(!existEmail[0]){
                    String artistId = artistRef.push().getKey();
                    artistRef.child(artistId).child("profile").child("artistic_name").setValue(artistic_name);
                    artistRef.child(artistId).child("profile").child("country").setValue(country);
                    artistRef.child(artistId).child("profile").child("city").setValue(city);
                    String key = userRef.push().getKey();
                    userRef.child(key).child("email").setValue(email);
                    userRef.child(key).child("password").setValue(password);
                    userRef.child(key).child("artistId").setValue(artistId);
                    if (!phone.matches("")) {
                        artistRef.child(artistId).child("profile").child("phone").setValue(phone);
                    }
                    Intent intent = new Intent();
                    intent.putExtra("email", email);
                    intent.putExtra("password", password);
                    intent.putExtra("artistId", artistId);
                    setResult(RESULT_OK, intent);
                    finish();
                }else{
                    Toast.makeText(
                            RegisterActivity.this,
                            R.string.error_invalid_email,
                            Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(
                        RegisterActivity.this,
                        R.string.error_database,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Log.i("info", "Back button pressed");
        setResult(RESULT_CANCELED);
        finish();

        super.onBackPressed();
    }
}