package sergi.ivan.carles.artist;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {

    public static final int NEW_ARTIST = 0;
    public static ArrayList<String[]> artists;
    private EditText edit_email;
    private EditText edit_password;
    private LoginActivity.UserLoginTask LoginTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        artists = new ArrayList<>();
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

                // Check for a valid password
                if (password.length() < 8) {
                    edit_password.setError(getString(R.string.error_invalid_password));
                    focusView = edit_password;
                    cancel = true;
                }

                if (cancel) {
                    focusView.requestFocus();
                } else {
                    LoginTask = new LoginActivity.UserLoginTask(email, password);
                    LoginTask.execute((Void) null);
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

    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;
        private boolean existsEmail;
        private String artistId;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void...Params) {

            // TODO: V0.8 check credentials firebase

            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            existsEmail = false;
            for (int i = 0; i< artists.size(); i++) {
                String email = artists.get(i)[0];
                String password = artists.get(i) [1];
                if (email.equals(mEmail)) {// Account exists, return true if the password matches.
                    existsEmail=true;
                    artistId = artists.get(i)[2];
                    return password.equals(mPassword);
                }
            }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            LoginTask = null;

            if (success && existsEmail) {
                Intent intent = new Intent(LoginActivity.this, InitActivity.class);
                intent.putExtra("artistId", artistId);
                startActivity(intent);
                Log.i("info", "correct sign in!");
            } else if(existsEmail){
                edit_password.setError(getString(R.string.error_incorrect_password));
                edit_password.requestFocus();
            }else{
                edit_email.setError(getString(R.string.error_email_no_exist));
                edit_email.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            LoginTask = null;
        }
    }
}

