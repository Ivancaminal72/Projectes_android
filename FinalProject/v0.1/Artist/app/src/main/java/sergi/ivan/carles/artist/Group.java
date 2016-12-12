package sergi.ivan.carles.artist;

import java.util.ArrayList;
import java.util.Arrays;
import static java.lang.System.arraycopy;

/**
 * Created by Ivan on 02/12/2016.
 */

public class Group {
    private static final int GROUP_MAX_SIZE = 4;
    private String name;
    private Integer [] songids;
    private ArrayList<Integer> points;

    public Group(String name, Integer[] songkeys) {
        this.name = name;
        this.songids = new Integer[GROUP_MAX_SIZE];
        arraycopy(songkeys,0,this.songids,0,GROUP_MAX_SIZE);
        this.points = new ArrayList<>(Arrays.asList(0,5,10,15));
    }

    public String getName() {
        return name;
    }

    public Integer[] getSongids() {
        return songids;
    }

    public ArrayList<Integer> getPoints() {
        return points;
    }

}
