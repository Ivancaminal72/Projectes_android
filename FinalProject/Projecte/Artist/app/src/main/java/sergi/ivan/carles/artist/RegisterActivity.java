package sergi.ivan.carles.artist;

import android.content.Intent;
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


public class RegisterActivity extends AppCompatActivity {

    private DatabaseReference artistRef;
    private DatabaseReference userRef;
    private boolean finded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().setTitle(R.string.register);
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        final DatabaseReference artistRef = database.getReference(REF_ARTISTS);
        final DatabaseReference userRef = database.getReference("users");
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
                String email = editEmail.getText().toString();
                String artistic_name = editUser.getText().toString();
                String password = editPassword.getText().toString();
                String repeat_pass = editRepeatPass.getText().toString();
                String country = editCountry.getText().toString();
                String city = editCity.getText().toString();
                String phone = editPhone.getText().toString();
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
                    String artistId = artistRef.push().getKey();
                    if(findEmail(email)){
                        Toast.makeText(
                                RegisterActivity.this,
                                getResources().getString(R.string.error_registered_email),
                                Toast.LENGTH_SHORT).show();
                    }
                    else {
                        artistRef.child(artistId).child("profile").child("artistic_name").setValue(artistic_name);
                        artistRef.child(artistId).child("profile").child("country").setValue(country);
                        artistRef.child(artistId).child("profile").child("city").setValue(city);
                        userRef.child("email").setValue(email);
                        userRef.child("password").setValue(password);
                        userRef.child("artistId").setValue(artistId);
                        if (!phone.matches("")) {
                            artistRef.child(artistId).child("profile").child("phone").setValue(phone);
                        }
                        Intent intent = new Intent();
                        intent.putExtra("email", email);
                        intent.putExtra("password", password);
                        intent.putExtra("artistId", artistId);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }
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

    private boolean findEmail(final String email) {
        finded = false;
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot users) {
                    if (users.hasChildren()){
                        for (DataSnapshot user : users.getChildren()) {
                            String existing_email = user.child("email").getValue().toString();
                            if (email.equals(existing_email)) {
                                finded = true;
                            }
                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

        return finded;
    }

}