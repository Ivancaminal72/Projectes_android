package sergicarlesivanartist.parsexml;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import org.xml.sax.InputSource;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        

        try
        {
            InputStream fraw = getResources().openRawResource(R.raw.exemplellista);
            /*
            BufferedReader brin = new BufferedReader(new InputStreamReader(fraw));

            while((brin.readLine())!=null) {
                streamxml += brin.readLine() + "\n";

            }

            brin.close();
            
            */
            parsexml parsexml = new parsexml();
            List<Track> lista = parsexml.parsear(fraw);
        }

        catch (Exception ex) {
            Log.e("Ficheros", "Error al leer fichero desde recurso raw");
        }
    }
}
