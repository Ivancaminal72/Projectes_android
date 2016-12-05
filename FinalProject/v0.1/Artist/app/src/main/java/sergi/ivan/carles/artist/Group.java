package sergi.ivan.carles.artist;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import static java.lang.System.arraycopy;

/**
 * Created by Ivan on 02/12/2016.
 */

public class Group {
    private static final int GROUP_MAX_SIZE = 4;
    private String name;
    private String [] songkeys;
    private ArrayList<Integer> points;

    public Group(String name, String[] songkeys) {
        this.name = name;
        this.songkeys = new String[GROUP_MAX_SIZE];
        arraycopy(songkeys,0,this.songkeys,0,GROUP_MAX_SIZE);
        this.points = new ArrayList<>(Arrays.asList(1,2,1,2));
    }

    public String toJson(){
        try{
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", this.name);
            JSONArray jsonSongs = new JSONArray(Arrays.asList(this.songkeys));
            JSONArray jsonPoints = new JSONArray(this.points);
            jsonObject.put("songkeys",jsonSongs);
            jsonObject.put("points", jsonPoints);
            return jsonObject.toString();
        }

        catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
}
