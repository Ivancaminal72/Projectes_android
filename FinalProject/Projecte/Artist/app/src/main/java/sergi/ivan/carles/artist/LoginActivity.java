package sergi.ivan.carles.artist;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        //Todo: If Artist is not registered: Intent -> Register Activity

        //Todo: Else: Intent ->Init Activity (with Artist Id)
    }
}
