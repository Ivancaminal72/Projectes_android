package sergicarlesivanartist.parsexml;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        String streamxml ="";

        try
        {
            InputStream fraw = getResources().openRawResource(R.raw.exemplellista);

            BufferedReader brin = new BufferedReader(new InputStreamReader(fraw));

            while((brin.readLine())!=null) {
                streamxml += brin.readLine() + "\n";

            }

            brin.close();
            Log.i("sergi",streamxml);
        }

        catch (Exception ex) {
            Log.e("Ficheros", "Error al leer fichero desde recurso raw");
        }
    }
}
